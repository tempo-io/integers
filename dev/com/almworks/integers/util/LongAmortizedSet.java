package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Operations {@link LongAmortizedSet#include(long)} and {@link LongAmortizedSet#exclude(long)}
 * may be slower than {@link LongAmortizedSet#add(long)} and {@link LongAmortizedSet#remove(long)} respectively
 * @author Igor Sereda
 */
public class LongAmortizedSet extends AbstractWritableLongSet implements WritableLongSortedSet {
  private static final int DEFAULT_CHUNKSIZE = 512;

  private LongArray myBaseList;
  private final WritableLongSortedSet myAdded;
  private final WritableLongSet myRemoved;

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

  protected void add0(long value) {
    myRemoved.remove(value);
    myAdded.add(value);
    maybeCoalesce();
  }

  /**
   * {@inheritDoc}
   * May be slower than {@link LongAmortizedSet#add(long)}
   */
  @Override
  public boolean include(long value) {
    return super.include(value);    //To change body of overridden methods use File | Settings | File Templates.
  }

  protected boolean include0(long value) {
    if (contains(value)) return false;
    add(value);
    return true;
  }

  // todo optimize addAll(LongList)

  @Override
  protected void remove0(long value) {
    myAdded.remove(value);
    myRemoved.add(value);
    maybeCoalesce();
  }

  /**
   * {@inheritDoc}
   * May be slower than {@link LongAmortizedSet#remove(long)}
   */
  public boolean exclude(long value) {
    return super.exclude(value);
  }

  protected boolean exclude0(long value) {
    if (!contains(value)) return false;
    remove(value);
    return true;
  }

// todo optimize removeAll

  public void retain(LongList values) {
    modified();
    coalesce();
    myBaseList.retain(values);
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

  @Override
  protected void modified() {
    super.modified();
    myCoalesced = false;
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
  protected LongIterator iterator1() {
    return new CoalescingIterator(myBaseList.iterator(), myAdded.iterator());
  }

  /**
   * @param iterable sorted unique
   * @param capacity the capacity for new set
   * @return new LongAmortizedSet contained all elements from the specified iterable
   */
  public static LongAmortizedSet createFromSortedUnique(LongIterable iterable, int capacity) {
    LongAmortizedSet res = new LongAmortizedSet();
    res.myBaseList = LongCollections.collectIterables(capacity, iterable);
    assert res.myBaseList.isSorted();
    return res;
  }

  /**
   * @return new LongAmortizedSet contained all elements from the specified iterable
   */
  public static LongAmortizedSet createFromSortedUnique(LongIterable iterable) {
    return createFromSortedUnique(iterable, 0);
  }

  @Override
  public boolean isEmpty() {
    if (!myAdded.isEmpty()) return false;
    if (myBaseList.isEmpty()) return true;
    if (myRemoved.isEmpty()) return false;

    for (int i = 0; i < myBaseList.size(); i++) {
      if (!myRemoved.contains(myBaseList.get(i))) return false;
    }
    for (LongIterator it: myAdded.iterator()) {
      if (!myRemoved.contains(it.value())) return false;
    }
    return true;
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
    int from = 0, to = myBaseList.size();
    for (LongIterator iterator: myAdded.iterator()) {
      int idx = myBaseList.binarySearch(iterator.value(), from, to);
      if (idx < 0) {
        size++;
        idx = -idx - 1;
      }
      from = idx;
    }
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