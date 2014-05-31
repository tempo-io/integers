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

import static com.almworks.integers.IntegersUtils.hash;

/**
 * Operations {@link #E#AmortizedSet#include(#e#)} and {@link #E#AmortizedSet#exclude(#e#)}
 * may be slower than {@link #E#AmortizedSet#add(#e#)} and {@link #E#AmortizedSet#remove(#e#)} respectively
 * @author Igor Sereda
 */
public class #E#AmortizedSet extends AbstractWritable#E#Set implements Writable#E#SortedSet {
  // Maximum number of elements to be added to #E#ChainHashSet without rehash with loadFactor = 0.5 and memory for 1024 elements
  public static final int DEFAULT_CHUNKSIZE = 511;

  private #E#Array myBaseList;
  private final int myChunkSize;
  // The intersection of myAdded and myRemoved is always empty
  private final Writable#E#SortedSet myAdded;
  private final Writable#E#Set myRemoved;

  // true if set wasn't modified after last coalesce()
  private boolean myCoalesced;

  private int[][] myTempInsertionPoints = {null};
  private #E#Array myRemovedTemp = null;

  public #E#AmortizedSet(Writable#E#SortedSet addedSet, Writable#E#Set removedSet) {
    this(0, addedSet, removedSet, DEFAULT_CHUNKSIZE);
  }

  public #E#AmortizedSet(int capacity, Writable#E#SortedSet addedSet, Writable#E#Set removedSet, int chunkSize) {
    if (!addedSet.isEmpty() || !removedSet.isEmpty()) {
      throw new IllegalArgumentException("sets must be empty");
    }
    myChunkSize = chunkSize;
    myBaseList = new #E#Array(capacity);
    this.myAdded = addedSet;
    this.myRemoved = removedSet;
  }

  public #E#AmortizedSet() {
    this(0);
  }

  public #E#AmortizedSet(int capacity) {
    this(capacity, new #E#TreeSet(DEFAULT_CHUNKSIZE), #E#ChainHashSet.createForAdd(DEFAULT_CHUNKSIZE, 0.5f), DEFAULT_CHUNKSIZE);
  }

  /**
   * @param iterable sorted unique
   * @param capacity the capacity for new set
   * @return new #E#AmortizedSet contained all elements from the specified iterable
   */
  public static #E#AmortizedSet createFromSortedUnique(#E#Iterable iterable, int capacity) {
    #E#AmortizedSet res = new #E#AmortizedSet();
    res.myBaseList = #E#Collections.collectIterable(capacity, iterable);
    assert res.myBaseList.isSortedUnique();
    return res;
  }

  /**
   * @return new #E#AmortizedSet that contains all elements from the specified iterable
   */
  public static #E#AmortizedSet createFromSortedUnique(#E#Iterable iterable) {
    return createFromSortedUnique(iterable, 0);
  }

  /**
   * Extracts elements from the specified array.
   * @return new #E#AmortizedSet with elements extracted from the specified array.
   * @see com.almworks.integers.#E#Array#extractHostArray()
   */
  public static #E#AmortizedSet createFromSortedUniqueArray(#E#Array array) {
    assert array.isSortedUnique();
    int size = array.size();
    #E#AmortizedSet set = new #E#AmortizedSet();
    set.myBaseList = new #E#Array(array.extractHostArray(), size);
    return set;
  }

  protected void add0(#e# value) {
    myRemoved.remove(value);
    myAdded.add(value);
    maybeCoalesce();
  }

  /**
   * {@inheritDoc}
   * <br>{@link #include} in most cases is slower than {@link #E#AmortizedSet#add(#e#)}
   */
  @Override
  public boolean include(#e# value) {
    return super.include(value);
  }

  protected boolean include0(#e# value) {
    if (contains(value)) return false;
    add(value);
    return true;
  }

  // todo optimize addAll(#E#List)

  @Override
  protected void remove0(#e# value) {
    myAdded.remove(value);
    myRemoved.add(value);
    maybeCoalesce();
  }

  /**
   * {@inheritDoc}
   * {@link #exclude} in most cases is slower than {@link #E#AmortizedSet#remove(#e#)}
   */
  public boolean exclude(#e# value) {
    return super.exclude(value);
  }

  protected boolean exclude0(#e# value) {
    if (!contains(value)) return false;
    remove(value);
    return true;
  }

// todo optimize removeAll

  public void retain(#E#List values) {
    modified();
    coalesce();
    myBaseList.retain(values);
  }

  private #E#Iterator sortedRemovedIterator() {
    if (myRemoved instanceof #E#SortedSet) {
      return myRemoved.iterator();
    } else {
      if (myRemovedTemp == null) {
        myRemovedTemp = new #E#Array(myChunkSize);
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
    for (#E#Iterator it : sortedRemovedIterator()) {
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

  public boolean contains(#e# value) {
    if (myRemoved.contains(value)) return false;
    return myAdded.contains(value) || myBaseList.binarySearch(value) >= 0;
  }

  public #e# getLowerBound() {
    #E#Iterator it = this.iterator();
    if (it.hasNext()) {
      return it.nextValue();
    } else {
      return #EW#.MAX_VALUE;
    }
  }

  public #e# getUpperBound() {
    #e# val = myAdded.getUpperBound();
    for (int index = myBaseList.size() - 1; 0 <= index; index--) {
      #e# baseValue = myBaseList.get(index);
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

  public #E#Iterator tailIterator(#e# fromElement) {
    int baseIndex = myBaseList.binarySearch(fromElement);
    if (baseIndex < 0) {
      baseIndex = -baseIndex - 1;
    }
    #E#Iterator baseIterator = myBaseList.iterator(baseIndex, myBaseList.size());
    return failFast(new CoalescingIterator(baseIterator, myAdded.tailIterator(fromElement)));
  }

  @NotNull
  public #E#Iterator iterator() {
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
      for (#E#Iterator iterator: myAdded) {
        int idx = myBaseList.binarySearch(iterator.value(), from, myBaseList.size());
        if (idx < 0) {
          size++;
          idx = -idx - 1;
        }
        from = idx;
      }
    }
    if (!myRemoved.isEmpty()) {
      for (#E#Iterator it : myRemoved) {
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
  public #E#List asList() {
    coalesce();
    return myBaseList;
  }

  public #E#Array toArray() {
    coalesce();
    return #E#Array.copy(myBaseList);
  }

  @Override
  protected void toNativeArrayImpl(#e#[] dest, int destPos) {
    coalesce();
    myBaseList.toNativeArray(0, dest, destPos, size());
  }

  public String toDebugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("#E#AmortizedSet  ").append(size()).append('\n');
    builder.append("myBaseList:      ").append(#E#Collections.toBoundedString(myBaseList)).append('\n');
    builder.append("myAdded:         ").append(#E#Collections.toBoundedString(myAdded)).append('\n');
    builder.append("myRemoved:       ").append(#E#Collections.toBoundedString(sortedRemovedIterator()));
    return builder.toString();
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (int i = 0; i < myBaseList.size(); i++) {
      #e# value = myBaseList.get(i);
      if (!myRemoved.contains(value) && !myAdded.contains(value)) {
        h += hash(value);
      }
    }
    for (#E#Iterator it : myAdded) {
      h += hash(it.value());
    }
    return h;
  }

  private class CoalescingIterator extends #E#FindingIterator {
    private #E#Iterator myIterator;
    private boolean myShouldReactOnCoalesce;

    public CoalescingIterator(#E#Iterator baseIterator, #E#Iterator addedIterator) {
      myIterator = new #E#UnionIteratorOfTwo(baseIterator, addedIterator);
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
