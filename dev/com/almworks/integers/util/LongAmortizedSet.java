package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Igor Sereda
 */
public class LongAmortizedSet implements WritableLongSortedSet {
  private static final int DEFAULT_CHUNKSIZE = 512;

  private LongArray myBaseList;
  private final WritableLongSortedSet myAdded;// = new LongTreeSet();
  private final WritableLongSet myRemoved;// = new LongChainHashSet(509, 512);

  private int myModCount = 0;
  // true if set wasn't modified after last coalesce()
  private boolean myCoalesced;

  private int[][] myTempInsertionPoints = {null};

  public LongAmortizedSet(WritableLongSortedSet myAddedSet, WritableLongSortedSet myRemovedSet) {
    this(0, myAddedSet, myRemovedSet);
  }

  public LongAmortizedSet(int capacity, WritableLongSortedSet myAddedSet, WritableLongSet myRemovedSet) {
    myBaseList = new LongArray(capacity);
    myAdded = myAddedSet;
    myRemoved = myRemovedSet;
  }

  public LongAmortizedSet() {
    this(0);
  }

  public LongAmortizedSet(int capacity) {
    this(capacity, new LongTreeSet(), new LongChainHashSet());
  }

  private void add0(long value) {
    myRemoved.remove(value);
    myAdded.add(value);
    maybeCoalesce();
  }

  public void add(long value) {
    modified();
    add0(value);
  }

  public boolean include(long value) {
    modified();
    if (contains(value)) return false;
    add(value);
    return true;
  }

  // todo optimize
  public void addAll(LongList values) {
    modified();
    addAll(values.iterator());
  }

  public void addAll(LongIterator iterator) {
    modified();
    while (iterator.hasNext())
      add0(iterator.nextValue());
  }

  public void addAll(long... values) {
    modified();
    if (values != null && values.length != 0) {
      if (values.length == 1) {
        add(values[0]);
      } else {
        addAll(new LongArray(values));
      }
    }
  }

  public void remove(long value) {
    modified();
    myAdded.remove(value);
    myRemoved.add(value);
    maybeCoalesce();
  }

  public boolean exclude(long value) {
    modified();
    if (!contains(value)) return false;
    remove(value);
    return true;
  }

  public void removeAll(long ... values) {
    for (long value : values) {
      remove(value);
    }
  }

  public void removeAll(LongList values) {
    removeAll(values.iterator());
  }

  public void removeAll(LongIterator iterator) {
    for (LongIterator it: iterator) {
      remove(it.value());
    }
  }

  @Override
  public LongAmortizedSet retain(LongList values) {
    modified();
    coalesce();
    myBaseList.retain(values);
    return this;
  }

  private LongIterator sortedRemovedIterator() {
    LongArray removeArray = new LongArray(myRemoved.iterator());
//    LongArray removedArray = myRemoved.toLongArray();
    removeArray.sort();
    return removeArray.iterator();
  }

  public boolean contains(long value) {
    if (myRemoved.contains(value)) return false;
    if (myAdded.contains(value)) return true;
    return myBaseList.binarySearch(value) >= 0;
  }

  public boolean containsAll(LongIterable iterable) {
    if (iterable == this) return true;
    for (LongIterator iterator : iterable.iterator()) {
      if (!contains(iterator.value())) return false;
    }
    return true;
  }

  private void modified() {
    myModCount++;
    myCoalesced = false;
  }

  private void maybeCoalesce() {
    if (myAdded.size() >= DEFAULT_CHUNKSIZE || myRemoved.size() >= DEFAULT_CHUNKSIZE) {
      coalesce();
    }
  }

  void coalesce() {
    myCoalesced = true;
    // todo add method WLL.removeSortedFromSorted(LIterable)
    // todo optimize
    for (LongIterator removeIt : sortedRemovedIterator()) {
      myBaseList.removeAllSorted(removeIt.value());
    }
    myBaseList.mergeWithSmall(myAdded.toArray(), myTempInsertionPoints);
    myAdded.clear();
    myRemoved.clear();
  }

  public LongIterator tailIterator(long fromElement) {
    int baseIndex = myBaseList.binarySearch(fromElement);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    LongIterator baseIterator = myBaseList.iterator(baseIndex, myBaseList.size());
    return new FailFastLongIterator(new CoalescingIterator(baseIterator, myAdded.tailIterator(fromElement))) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  @NotNull
  public LongIterator iterator() {
    LongIterator baseIterator = myBaseList.iterator();
    return new FailFastLongIterator(new CoalescingIterator(baseIterator, myAdded.iterator())) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  /**
   * @return new LongAmortizedSet contained all elements from the specified iterator
   */
  public static LongAmortizedSet fromSortedIterable(LongIterator src) {
    return fromSortedIterator(src, 0);
  }

  /**
   * @param capacity the capacity for new set
   * @return new LongAmortizedSet contained all elements from the specified iterator
   */
  public static LongAmortizedSet fromSortedIterator(LongIterator src, int capacity) {
    return fromSortedIterable0(src, capacity);
  }

  /**
   * @return new LongAmortizedSet contained all elements from the specified src
   */
  public static LongAmortizedSet fromSortedList(LongList src) {
    return fromSortedIterable0(src, src.size());
  }

  /**
   * @param capacity the capacity for new set
   * @return new LongAmortizedSet contained all elements from the specified src
   */
  public static LongAmortizedSet fromSortedList(LongList src, int capacity) {
    return fromSortedIterable0(src, capacity);
  }

  private static LongAmortizedSet fromSortedIterable0(LongIterable iterable, int capacity) {
    LongAmortizedSet res = new LongAmortizedSet();
    res.myBaseList = LongCollections.collectIterables(capacity, iterable);
    return res;
  }

  public boolean isEmpty() {
    if (!myAdded.isEmpty()) return false;
    if (myBaseList.isEmpty()) return true;
    if (myRemoved.isEmpty()) return false;
    return !iterator().hasNext();
  }

  public void clear() {
    modified();
    myAdded.clear();
    myRemoved.clear();
    myBaseList = new LongArray();
  }

  public int size() {
    int size = myBaseList.size();
    // intersection of myRemoved and myAdded is empty
    for (LongIterator iterator: myAdded.iterator()) {
      if (myBaseList.binarySearch(iterator.value()) < 0) size++;
    }
//    LongIterator removedIt = myRemoved.iterator();
//    while (removedIt.hasNext()) {//removeIterator()) {
//      if (myBaseList.binarySearch(removedIt.nextValue()) >= 0) size--;
//    }
    for (LongIterator it : myRemoved) {
      if (myBaseList.binarySearch(it.value()) >= 0) size--;
    }
    return size;
  }

  /**
   * @return a sorted list of numbers, contained in this set,
   * which should be used before any further mutation of the builder.
   * Changes in set may affect the returned list
   */
  public LongList asList() {
    coalesce();
    return myBaseList;
  }

  public LongArray toArray() {
    coalesce();
    return LongArray.copy(myBaseList);
  }

  public String toDebugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LongAmortizedSet\n");
    builder.append("myBaseList: ").append(LongCollections.toBoundedString(myBaseList)).append('\n');
    builder.append("myAdded: ").append(LongCollections.toBoundedString(myAdded)).append('\n');
    builder.append("myRemoved: ").append(LongCollections.toBoundedString(sortedRemovedIterator()));
    return builder.toString();
  }

  public StringBuilder toString(StringBuilder builder) {
    builder.append("LAS ").append(size()).append(" [");
    String sep = "";
    for  (LongIterator i : this) {
      builder.append(sep).append(i.value());
      sep = ", ";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }

  private class CoalescingIterator extends FindingLongIterator {
    private LongIterator myIterator;
    private boolean myShouldReactOnCoalesce;

    public CoalescingIterator(LongIterator baseIterator, LongIterator addedIterator) {
      myIterator = new LongUnionIteratorTwo(baseIterator, addedIterator);
      myShouldReactOnCoalesce = !myCoalesced;
    }

    @Override
    protected boolean findNext() {
      if (myShouldReactOnCoalesce && myCoalesced) {
        myShouldReactOnCoalesce = false;
        // baseIndex always >= -1
        if (!myIterated) {
          myIterator = myBaseList.iterator();
        } else {
          // myIterated = true -> myNext always exist in myBaseList
          int baseIndex = myBaseList.binarySearch(myCurrent);
          myIterator = myBaseList.iterator(baseIndex + 1, myBaseList.size());
        }
      }
      while (myIterator.hasNext()) {
        myCurrent = myIterator.nextValue();
        if (!myRemoved.contains(myCurrent)) return true;
      }
      return false;
    }

  }
}