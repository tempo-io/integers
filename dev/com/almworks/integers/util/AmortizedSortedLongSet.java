package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Igor Sereda
 */
public class AmortizedSortedLongSet implements WritableSortedLongSet {
  private static final int DEFAULT_CHUNKSIZE = 512;

  private LongArray myBaseList;
  private final DynamicLongSet myAdded = new DynamicLongSet();
  //  private final ChainHashLongSet myRemoved = new ChainHashLongSet(509, 512);//HashSet<Long>(DEFAULT_CHUNKSIZE);
  private final Set<Long> myRemoved = new HashSet<Long>(DEFAULT_CHUNKSIZE);
//  private final DynamicLongSet myRemoved = new DynamicLongSet();

  private int myModCount = 0;
  private boolean myCoalescingStatus;

  private int[][] myTempInsertionPoints = {null};

  public AmortizedSortedLongSet() {
    myBaseList = new LongArray();
  }

  public void add(long value) {
    modified();
    myRemoved.remove(value);
    myAdded.add(value);
    maybeCoalesce();
  }

  public boolean include(long value) {
    modified();
    boolean contain = contains(value);
    if (!contain) add(value);
    return !contain;
  }

  public void addAll(LongList values) {
    modified();
    addAll(values.iterator());
  }

  public void addAll(LongIterator iterator) {
    modified();
    while (iterator.hasNext())
      add(iterator.nextValue());
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
    boolean contain = contains(value);
    if (contain) remove(value);
    return contain;
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
  public AmortizedSortedLongSet retain(LongList values) {
    modified();
    coalesce();
    myBaseList.retain(values);
    return this;
  }

  private LongIterator sortedRemoveIterator() {
    LongArray removeArray = LongArray.create(myRemoved);
//    LongArray removedArray = myRemoved.toLongArray();
    removeArray.sortUnique();
    return removeArray.iterator();
  }

  public boolean contains(long value) {
    if (myRemoved.contains(value)) return false;
    if (myAdded.contains(value)) return true;
    return myBaseList.binarySearch(value) >= 0;
  }

  public boolean containsAll(LongIterable iterable) {
    for (LongIterator iterator : iterable.iterator()) {
      if (!contains(iterator.value())) return false;
    }
    return true;
  }

  private void modified() {
    myModCount++;
  }

  private void maybeCoalesce() {
    if (myAdded.size() >= DEFAULT_CHUNKSIZE || myRemoved.size() >= DEFAULT_CHUNKSIZE) {
      coalesce();
    }
  }

  void coalesce() {
    myCoalescingStatus = true;
    // todo add method WLL.removeSortedFromSorted(LIterable)
    int ind = 0;
    LongIterator removeIt = sortedRemoveIterator();
    while (removeIt.hasNext() && !myBaseList.isEmpty()) {
      ind = myBaseList.binarySearch(removeIt.nextValue(), ind, myBaseList.size());
      if (ind >= 0) {
        myBaseList.removeAt(ind);
      } else {
        ind = -ind - 1;
      }
    }
    myBaseList.mergeWithSmall(myAdded.toList(), myTempInsertionPoints);
    myAdded.clear();
    myRemoved.clear();
  }

  public LongIterator tailIterator(long fromElement) {
    myCoalescingStatus = false;
    int baseIndex = myBaseList.binarySearch(fromElement);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    LongIterator baseIterator = myBaseList.iterator(baseIndex, myBaseList.size());
    return new FailFastLongIterator(new CoalescingIterator(baseIterator, myAdded.tailIterator(fromElement), myRemoved)) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  @NotNull
  public LongIterator iterator() {
    myCoalescingStatus = false;
    LongIterator baseIterator = myBaseList.iterator();
    return new FailFastLongIterator(new CoalescingIterator(baseIterator, myAdded.iterator(), myRemoved)) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public void replaceFrom(LongSetBuilder builder) {
    myAdded.clear();
    myRemoved.clear();
    myBaseList = new LongArray(builder.toTemporaryReadOnlySortedCollection());
  }

  public static AmortizedSortedLongSet fromSortedIterable(LongIterable src) {
    return fromSortedIterable(src, 0);
  }

  public static AmortizedSortedLongSet fromSortedIterable(LongIterable src, int capacity) {
    AmortizedSortedLongSet res = new AmortizedSortedLongSet();
    res.myBaseList = LongCollections.collectIterables(capacity, src);
    return res;
  }

  public static AmortizedSortedLongSet fromSortedList(LongList src) {
    AmortizedSortedLongSet res = new AmortizedSortedLongSet();
    res.myBaseList = new LongArray(src);
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
    for(Long val: myRemoved) {
      if (myBaseList.binarySearch(val) >= 0) size--;
    }
    return size;
  }

  public LongList toList() {
    coalesce();
    return myBaseList;
  }

  public LongArray toArray() {
    coalesce();
    return LongArray.copy(myBaseList);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AmortizedSortedLongSet\n");
    builder.append("myBaseList: ").append(LongCollections.toBoundedString(myBaseList)).append('\n');
    builder.append("myAdded: ").append(LongCollections.toBoundedString(myAdded)).append('\n');
    builder.append("myRemoved: ").append(LongCollections.toBoundedString(sortedRemoveIterator()));
    return builder.toString();
  }

  private class CoalescingIterator extends FindingLongIterator {
    private LongIterator myIterator;
    private Set<Long> removedSet;
//    private ChainHashLongSet removedSet;

    private boolean myCoalescingStatusAtCreation;

    public CoalescingIterator(LongIterator baseIterator, LongIterator addedIterator, Set<Long> removedSet) {
//    public CoalescingIterator(LongIterator baseIterator, LongIterator addedIterator, ChainHashLongSet removedSet) {
      myIterator = new LongUnionIteratorTwo(baseIterator, addedIterator);
      myCoalescingStatusAtCreation = myCoalescingStatus;
      this.removedSet = removedSet;
    }

    @Override
    protected boolean findNext() {
      if (myCoalescingStatusAtCreation != myCoalescingStatus) {
        myCoalescingStatusAtCreation = myCoalescingStatus;
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
        if (!removedSet.contains(myCurrent)) return true;
      }
      return false;
    }

  }
}