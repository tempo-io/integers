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

public final class IntArray extends AbstractWritableIntList {
  /**
   * Holds the host array
   */
  @NotNull
  private int[] myArray;

  public IntArray() {
    myArray = EMPTY_INTS;
  }

  public IntArray(int size) {
    myArray = size <= 0 ? EMPTY_INTS : new int[size];
  }

  public IntArray(IntList copyFrom) {
    this(copyFrom == null ? 0 : copyFrom.size());
    if (copyFrom != null) {
      addAll(copyFrom);
    }
  }

  public IntArray(IntIterator iterator) {
    myArray = EMPTY_INTS;
    if (iterator != null)
      addAll(iterator);
  }

  public IntArray(int[] hostArray) {
    this(hostArray, hostArray == null ? 0 : hostArray.length);
  }

  public IntArray(int[] hostArray, int length) {
    myArray = hostArray == null ? EMPTY_INTS : hostArray;
    updateSize(length < 0 ? 0 : length >= myArray.length ? myArray.length : length);
  }

  public static IntArray copy(int[] array) {
    return copy(array, array == null ? 0 : array.length);
  }

  public static IntArray copy(int[] array, int length) {
    return new IntArray(IntCollections.arrayCopy(array, 0, length));
  }

  public static IntArray copy(@Nullable IntIterable iterable) {
    if (iterable == null) return new IntArray();
    if (iterable instanceof IntArray) {
      IntArray other = (IntArray) iterable;
      return copy(other.myArray, other.size());
    } else if (iterable instanceof IntList)
      return new IntArray((IntList) iterable);
    else return new IntArray(iterable.iterator());
  }

  public static IntArray create(int ... values) {
    if (values == null)
      values = EMPTY_INTS;
    return new IntArray(values);
  }

  public static IntArray create(@Nullable Collection<Integer> values) {
    int[] host = EMPTY_INTS;
    if (values != null && !values.isEmpty()) {
      int sz = values.size();
      host = new int[sz];
      Iterator<Integer> it = values.iterator();
      for (int i = 0; i < sz; ++i) {
        if (it.hasNext()) {
          host[i] = it.next();
        } else assert false : "concurrent modification?";
      }
    }
    return new IntArray(host);
  }

  public static IntArray singleton(Integer value) {
    return new IntArray(new int[]{value});
  }

  public int indexOf(int value) {
    return IntCollections.indexOf(myArray, 0, size(), value);
  }

  public final int get(int index) {
    return myArray[index];
  }

  public int[] toArray(int sourceOffset, int[] dest, int destOffset, int length) {
    System.arraycopy(myArray, sourceOffset, dest, destOffset, length);
    return dest;
  }

  private void ensureCapacity(int expectedSize) {
    myArray = IntCollections.ensureCapacity(myArray, expectedSize);
  }

  public void set(int index, int value) {
    myArray[index] = value;
  }

  public void setRange(int from, int to, int value) {
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

  public void insertMultiple(int index, int value, int count) {
    if (count > 0) {
      makeSpaceForInsertion(index, index + count);
      Arrays.fill(myArray, index, index + count, value);
    }
  }

  public void expand(int index, int count) {
    if (count > 0) {
      makeSpaceForInsertion(index, index + count);
    }
  }

  public void insert(int index, int value) {
    makeSpaceForInsertion(index, index + 1);
    myArray[index] = value;
  }

  public void add(int value) {
    int sz = size();
    int nsz = sz + 1;
    ensureCapacity(nsz);
    myArray[sz] = value;
    updateSize(nsz);
  }

  public void insertAll(int index, IntList collection, int sourceIndex, int count) {
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

  public void setAll(int index, IntList values, int sourceIndex, int count) {
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
    IntCollections.swap(myArray, index1, index2);
  }

  public void addAll(int... ints) {
    if (ints.length == 0)
      return;
    int sz = size();
    int newsz = sz + ints.length;
    ensureCapacity(newsz);
    System.arraycopy(ints, 0, myArray, sz, ints.length);
    updateSize(newsz);
  }

  public void sort(WritableIntList... sortAlso) {
    if (sortAlso == null || sortAlso.length == 0)
      Arrays.sort(myArray, 0, size());
    else
      super.sort(sortAlso);
  }

  public void addAll(IntList collection) {
    if (collection instanceof IntList) {
      IntList list = (IntList) collection;
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
    int r = IntCollections.isSortedUnique(!checkUnique, myArray, 0, size());
    return r == 0 || (r < 0 && !checkUnique);
  }

  public boolean equalOrder(int[] array) {
    if (size() != array.length)
      return false;
    for (int i = 0; i < size(); i++)
      if (array[i] != get(i))
        return false;
    return true;
  }

  public void sortUnique() {
    Arrays.sort(myArray, 0, size());
    updateSize(IntCollections.removeSubsequentDuplicates(myArray, 0, size()));
  }

  public void retain(IntList values) {
    if (values.isEmpty()) {
      clear();
      return;
    }
    IntList sortedValues = IntCollections.toSorted(false, values);
    for (int i = size() - 1; i >= 0; i--) {
      int v = myArray[i];
      if (sortedValues.binarySearch(v) >= 0) continue;
      removeAt(i);
    }
  }

  public boolean equalSortedValues(IntList collection) {
    assert isUniqueSorted();
    if (size() != collection.size())
      return false;
    IntIterator ownIt = iterator();
    int prevOther = Integer.MIN_VALUE;
    for (IntIterator it : collection) {
      int own = ownIt.nextValue();
      int other = it.value();
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
  public int addAllNotMore(IntArray collection, int maxCount) {
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
  public int addAllNotMore(IntIterator iterator, int maxCount) {
    int counter = 0;
    while (iterator.hasNext() && counter < maxCount) {
      add(iterator.nextValue());
      counter++;
    }
    return counter;
  }

  public void removeSorted(int value) {
    assert isSorted();
    int index = binarySearch(value);
    if (index >= 0) removeAt(index);
  }

  public int[] extractHostArray() {
    int[] array = myArray;
    myArray = EMPTY_INTS;
    updateSize(0);
    return array;
  }
}
