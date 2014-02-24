/*
 * Copyright 2014 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

/**
 * Operations {@link LongAmortizedSet#include(long)} and {@link LongAmortizedSet#exclude(long)}
 * may be slower than {@link LongAmortizedSet#add(long)} and {@link LongAmortizedSet#remove(long)} respectively
 * @author Igor Sereda
 */
public class LongAmortizedSet extends AbstractWritableLongSet implements WritableLongSortedSet {
  // Maximum number of elements to be added to LongChainHashSet without rehash with loadFactor = 0.5 and memory for 1024 elements
  public static final int DEFAULT_CHUNKSIZE = 511;

  private LongArray myBaseList;
  private final int myChunkSize;
  // The intersection of myAdded and myRemoved is always empty
  private final WritableLongSortedSet myAdded;
  private final WritableLongSet myRemoved;

  // true if set wasn't modified after last coalesce()
  private boolean myCoalesced;

  private int[][] myTempInsertionPoints = {null};
  private LongArray myRemovedTemp = null;

  public LongAmortizedSet(WritableLongSortedSet addedSet, WritableLongSet removedSet) {
    this(0, addedSet, removedSet, DEFAULT_CHUNKSIZE);
  }

  public LongAmortizedSet(int capacity, WritableLongSortedSet addedSet, WritableLongSet removedSet, int chunkSize) {
    if (!addedSet.isEmpty() || !removedSet.isEmpty()) {
      throw new IllegalArgumentException("sets must be empty");
    }
    myChunkSize = chunkSize;
    myBaseList = new LongArray(capacity);
    this.myAdded = addedSet;
    this.myRemoved = removedSet;
  }

  public LongAmortizedSet() {
    this(0);
  }

  public LongAmortizedSet(int capacity) {
    this(capacity, new LongTreeSet(DEFAULT_CHUNKSIZE), LongChainHashSet.createForAdd(DEFAULT_CHUNKSIZE, 0.5f), DEFAULT_CHUNKSIZE);
  }

  /**
   * @param iterable sorted unique
   * @param capacity the capacity for new set
   * @return new LongAmortizedSet contained all elements from the specified iterable
   */
  public static LongAmortizedSet createFromSortedUnique(LongIterable iterable, int capacity) {
    LongAmortizedSet res = new LongAmortizedSet();
    res.myBaseList = LongCollections.collectIterable(capacity, iterable);
    assert res.myBaseList.isSortedUnique();
    return res;
  }

  /**
   * @return new LongAmortizedSet that contains all elements from the specified iterable
   */
  public static LongAmortizedSet createFromSortedUnique(LongIterable iterable) {
    return createFromSortedUnique(iterable, 0);
  }

  /**
   * Extracts elements from the specified array.
   * @return new LongAmortizedSet with elements extracted from the specified array.
   * @see com.almworks.integers.LongArray#extractHostArray()
   */
  public static LongAmortizedSet createFromSortedUniqueArray(LongArray array) {
    assert array.isSortedUnique();
    int size = array.size();
    LongAmortizedSet set = new LongAmortizedSet();
    set.myBaseList = new LongArray(array.extractHostArray(), size);
    return set;
  }

  protected void add0(long value) {
    myRemoved.remove(value);
    myAdded.add(value);
    maybeCoalesce();
  }

  /**
   * {@inheritDoc}
   * <br>{@link #include} in most cases is slower than {@link LongAmortizedSet#add(long)}
   */
  @Override
  public boolean include(long value) {
    return super.include(value);
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
   * {@link #exclude} in most cases is slower than {@link LongAmortizedSet#remove(long)}
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
    if (myRemoved instanceof LongSortedSet) {
      return myRemoved.iterator();
    } else {
      if (myRemovedTemp == null) {
        myRemovedTemp = new LongArray(myChunkSize);
      } else {
        myRemovedTemp.clear();
      }
      myRemovedTemp.addAll(myRemoved);
      myRemovedTemp.sort();
      return myRemovedTemp.iterator();
    }
  }

  // todo refactor after rebuild - we can return FindingIntIterator without buffering points to remove
  private IntIterator sortedIndicesToRemove() {
    if (myRemoved.isEmpty()) {
      return IntIterator.EMPTY;
    }
    if (myTempInsertionPoints[0] == null) {
      myTempInsertionPoints[0] = new int[myChunkSize];
    }
    IntArray points = new IntArray(myTempInsertionPoints[0], 0);
    int baseIndex = 0;
    for (LongIterator it : sortedRemovedIterator()) {
      baseIndex = myBaseList.binarySearch(it.value(), baseIndex, myBaseList.size());
      if (baseIndex >= 0) {
        points.add(baseIndex);
      } else {
        baseIndex = -baseIndex - 1;
        if (baseIndex >= myBaseList.size()) {
          break;
        }
      }
    }
    return points.iterator();
  }

  public boolean contains(long value) {
    if (myRemoved.contains(value)) return false;
    return myAdded.contains(value) || myBaseList.binarySearch(value) >= 0;
  }

  public long getLowerBound() {
    LongIterator it = this.iterator();
    if (it.hasNext()) {
      return it.nextValue();
    } else {
      return Long.MAX_VALUE;
    }
  }

  public long getUpperBound() {
    long val = myAdded.getUpperBound();
    for (int index = myBaseList.size() - 1; 0 <= index; index--) {
      long baseValue = myBaseList.get(index);
      if (baseValue < val) {
        return val;
      }
      if (!myRemoved.contains(baseValue)) {
        return baseValue;
      }
    }
    return val;
  }


  private void maybeCoalesce() {
    if (myAdded.size() >= myChunkSize || myRemoved.size() >= myChunkSize) {
      coalesce();
    }
  }

  void coalesce() {
    myCoalesced = true;
    // todo add method WLL.removeSortedFromSorted(LIterable)
    // todo optimize
    myBaseList.removeAllAtSorted(sortedIndicesToRemove());

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
    if (baseIndex < 0) {
      baseIndex = -baseIndex - 1;
    }
    LongIterator baseIterator = myBaseList.iterator(baseIndex, myBaseList.size());
    return failFast(new CoalescingIterator(baseIterator, myAdded.tailIterator(fromElement)));
  }

  @NotNull
  public LongIterator iterator() {
    return failFast(new CoalescingIterator(myBaseList.iterator(), myAdded.iterator()));
  }

  @Override
  public boolean isEmpty() {
    if (!myAdded.isEmpty()) return false;
    if (myBaseList.isEmpty()) return true;
    if (myRemoved.isEmpty()) return false;

    if (myRemoved.size() < myBaseList.size()) return false;
    return myRemoved.containsAll(myBaseList);
  }

  public void clear() {
    modified();
    myAdded.clear();
    myBaseList.clear();
  }

  public int size() {
    int size = myBaseList.size();
    // myAdded and myRemoved are disjoint
    if (!myAdded.isEmpty()) {
      int from = 0;
      for (LongIterator iterator: myAdded.iterator()) {
        int idx = myBaseList.binarySearch(iterator.value(), from, myBaseList.size());
        if (idx < 0) {
          size++;
          idx = -idx - 1;
        }
        from = idx;
      }
    }
    if (!myRemoved.isEmpty()) {
      for (LongIterator it : myRemoved) {
        if (myBaseList.binarySearch(it.value()) >= 0) size--;
      }
    }
    return size;
  }

  /**
   * @return a sorted list of numbers contained in this set,
   * which should be used before any further mutation of this set.
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

  @Override
  protected void toNativeArrayImpl(long[] dest, int destPos) {
    coalesce();
    myBaseList.toNativeArray(0, dest, destPos, size());
  }

  public String toDebugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LongAmortizedSet  ").append(size()).append('\n');
    builder.append("myBaseList:      ").append(LongCollections.toBoundedString(myBaseList)).append('\n');
    builder.append("myAdded:         ").append(LongCollections.toBoundedString(myAdded)).append('\n');
    builder.append("myRemoved:       ").append(LongCollections.toBoundedString(sortedRemovedIterator()));
    return builder.toString();
  }

  private class CoalescingIterator extends LongFindingIterator {
    private LongIterator myIterator;
    private boolean myShouldReactOnCoalesce;

    public CoalescingIterator(LongIterator baseIterator, LongIterator addedIterator) {
      myIterator = new LongUnionIteratorOfTwo(baseIterator, addedIterator);
      myShouldReactOnCoalesce = !myCoalesced;
    }

    @Override
    protected boolean findNext() {
      if (myShouldReactOnCoalesce && myCoalesced) {
        myShouldReactOnCoalesce = false;
        // baseIndex always >= -1
        if (!hasValue()) {
          myIterator = myBaseList.iterator();
        } else {
          // myIterated = true -> myNext always exist in myBaseList
          int baseIndex = myBaseList.binarySearch(myNext);
          myIterator = myBaseList.iterator(baseIndex + 1, myBaseList.size());
        }
      }
      while (myIterator.hasNext()) {
        myNext = myIterator.nextValue();
        if (!myRemoved.contains(myNext)) return true;
      }
      return false;
    }

  }
}