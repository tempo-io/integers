/*
 * Copyright 2010 ALM Works Ltd
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

// CODE GENERATED FROM com/almworks/integers/PArray.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import static com.almworks.integers.IntegersUtils.*;

import java.util.*;

public final class LongArray extends AbstractWritableLongList {
  /**
   * Holds the host array
   */
  @NotNull
  private long[] myArray;

  public LongArray() {
    myArray = EMPTY_LONGS;
  }

  public LongArray(int capacity) {
    if (capacity < 0)
      throw new IllegalArgumentException();
    myArray = capacity == 0 ? EMPTY_LONGS : new long[capacity];
  }

  public LongArray(LongList copyFrom) {
    this(copyFrom == null ? 0 : copyFrom.size());
    if (copyFrom != null) {
      addAll(copyFrom);
    }
  }

  public LongArray(LongIterator iterator) {
    myArray = EMPTY_LONGS;
    if (iterator != null)
      addAll(iterator);
  }

  public LongArray(long[] hostArray) {
    this(hostArray, hostArray == null ? 0 : hostArray.length);
  }

  public LongArray(long[] hostArray, int length) {
    myArray = hostArray == null ? EMPTY_LONGS : hostArray;
    updateSize(length < 0 ? 0 : length >= myArray.length ? myArray.length : length);
  }

  public static LongArray copy(long[] array) {
    return copy(array, array == null ? 0 : array.length);
  }

  public static LongArray copy(long[] array, int length) {
    return new LongArray(LongCollections.arrayCopy(array, 0, length));
  }

  public static LongArray copy(@Nullable LongIterable iterable) {
    if (iterable == null) return new LongArray();
    if (iterable instanceof LongArray) {
      LongArray other = (LongArray) iterable;
      return copy(other.myArray, other.size());
    } else if (iterable instanceof LongList)
      return new LongArray((LongList) iterable);
    else return new LongArray(iterable.iterator());
  }

  public static LongArray create(long ... values) {
    if (values == null)
      values = EMPTY_LONGS;
    return new LongArray(values);
  }

  public static LongArray create(@Nullable Collection<Long> values) {
    long[] host = EMPTY_LONGS;
    if (values != null && !values.isEmpty()) {
      int sz = values.size();
      host = new long[sz];
      Iterator<Long> it = values.iterator();
      for (int i = 0; i < sz; ++i) {
        if (it.hasNext()) {
          host[i] = it.next();
        } else assert false : "concurrent modification?";
      }
    }
    return new LongArray(host);
  }

  public static LongArray singleton(Long value) {
    return new LongArray(new long[]{value});
  }

  public int indexOf(long value) {
    return LongCollections.indexOf(myArray, 0, size(), value);
  }

  public final long get(int index) {
    return myArray[index];
  }

  public long[] toArray(int sourceOffset, long[] dest, int destOffset, int length) {
    System.arraycopy(myArray, sourceOffset, dest, destOffset, length);
    return dest;
  }

  public void ensureCapacity(int expectedSize) {
    myArray = LongCollections.ensureCapacity(myArray, expectedSize);
  }

  public int getCapacity() {
    return myArray.length;
  }

  public void set(int index, long value) {
    myArray[index] = value;
  }

  public void setRange(int from, int to, long value) {
    Arrays.fill(myArray, from, to, value);
  }

  public void removeRange(int from, int to) {
    if (from >= to)
      return;
    int sz = size();
    if (to < sz)
      System.arraycopy(myArray, to, myArray, from, sz - to);
    updateSize(sz - (to - from));
  }

  public void clear() {
    updateSize(0);
  }

  public void insertMultiple(int index, long value, int count) {
    if (count > 0) {
      makeSpaceForInsertion(index, index + count);
      Arrays.fill(myArray, index, index + count, value);
    }
  }

  public void expand(int index, int count) {
    makeSpaceForInsertion(index, index + count);
  }

  public void insert(int index, long value) {
    makeSpaceForInsertion(index, index + 1);
    myArray[index] = value;
  }

  public void add(long value) {
    int sz = size();
    int nsz = sz + 1;
    ensureCapacity(nsz);
    myArray[sz] = value;
    updateSize(nsz);
  }

  public void insertAll(int index, LongList collection, int sourceIndex, int count) {
    if (count <= 0)
      return;
    makeSpaceForInsertion(index, index + count);
    collection.toArray(sourceIndex, myArray, index, count);
  }

  private void makeSpaceForInsertion(int from, int to) {
    int sz = size();
    if (from < 0 || from > sz)
      throw new IndexOutOfBoundsException(from + " " + to + " " + sz);
    if (from < to) {
      int added = to - from;
      int newsz = sz + added;
      ensureCapacity(newsz);
      int move = sz - from;
      if (move > 0)
        System.arraycopy(myArray, from, myArray, to, move);
      updateSize(newsz);
    }
  }

  public void setAll(int index, LongList values, int sourceIndex, int count) {
    if (count <= 0)
      return;
    int sz = size();
    if (index < 0 || index >= sz)
      throw new IndexOutOfBoundsException(index + " " + sz);
    if (index + count > sz)
      throw new IndexOutOfBoundsException(index + " " + count + " " + sz);
    values.toArray(sourceIndex, myArray, index, count);
  }

  public void swap(int index1, int index2) {
    LongCollections.swap(myArray, index1, index2);
  }

  public void addAll(long... ints) {
    if (ints.length == 0)
      return;
    int sz = size();
    int newsz = sz + ints.length;
    ensureCapacity(newsz);
    System.arraycopy(ints, 0, myArray, sz, ints.length);
    updateSize(newsz);
  }

  public void sort(WritableLongList... sortAlso) {
    if (sortAlso == null || sortAlso.length == 0)
      Arrays.sort(myArray, 0, size());
    else
      super.sort(sortAlso);
  }

  public void addAll(LongList collection) {
    if (collection instanceof LongList) {
      LongList list = (LongList) collection;
      int added = list.size();
      int sz = size();
      int newSize = sz + added;
      ensureCapacity(newSize);
      list.toArray(0, myArray, sz, added);
      updateSize(newSize);
    } else {
      addAll(collection.iterator());
    }
  }

  @Override
  protected boolean checkSorted(boolean checkUnique) {
    int r = LongCollections.isSortedUnique(!checkUnique, myArray, 0, size());
    return r == 0 || (r < 0 && !checkUnique);
  }

  public boolean equalOrder(long[] array) {
    if (size() != array.length)
      return false;
    for (int i = 0; i < size(); i++)
      if (array[i] != get(i))
        return false;
    return true;
  }

  public void sortUnique() {
    Arrays.sort(myArray, 0, size());
    updateSize(LongCollections.removeSubsequentDuplicates(myArray, 0, size()));
  }

  public void retain(LongList values) {
    retain(values, false);
  }

  public void retainSorted(LongList values) {
    assert values.isSorted();
    retain(values, true);
  }

  private void retain(LongList values, boolean valuesSortedStatus) {
    if (values.isEmpty()) {
      clear();
      return;
    }
    LongList sortedValues = valuesSortedStatus ? values : LongCollections.toSorted(false, values);
    for (int i = size() - 1; i >= 0; i--) {
      long v = myArray[i];
      if (sortedValues.binarySearch(v) >= 0) continue;
      removeAt(i);
    }
  }

  public boolean equalSortedValues(LongList collection) {
    assert isUniqueSorted();
    if (size() != collection.size())
      return false;
    LongIterator ownIt = iterator();
    long prevOther = Long.MIN_VALUE;
    for (LongIterator it : collection) {
      long own = ownIt.nextValue();
      long other = it.value();
      if (other <= prevOther) {
        assert false : collection; // Not sorted
        return false;
      }
      if (own != other)
        return false;
      prevOther = other;
    }
    return true;
  }

  /**
   * Adds first maxCount elements from collection or the whole collection if it size is less than maxCount.
   * @param collection
   * @param maxCount
   * @return number of added elements
   */
  public int addAllNotMore(LongArray collection, int maxCount) {
    int toAdd = Math.min(maxCount, collection.size());
    ensureCapacity(size() + toAdd);
    System.arraycopy(collection.myArray, 0, myArray, size(), toAdd);
    updateSize(size() + toAdd);
    return toAdd;
  }

  /**
   * Adds elements from iterator until maxCount elements are added or iterator reaches its end.
   * @param iterator
   * @param maxCount
   * @return number of added elements
   */
  public int addAllNotMore(LongIterator iterator, int maxCount) {
    int counter = 0;
    while (iterator.hasNext() && counter < maxCount) {
      add(iterator.nextValue());
      counter++;
    }
    return counter;
  }

  public void removeSorted(long value) {
    assert isSorted();
    int index = binarySearch(value);
    if (index >= 0) removeAt(index);
  }

  public long[] extractHostArray() {
    long[] array = myArray;
    myArray = EMPTY_LONGS;
    updateSize(0);
    return array;
  }
}
