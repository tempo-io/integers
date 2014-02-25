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
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import static com.almworks.integers.IntegersUtils.EMPTY_#EC#S;

public final class #E#Array extends AbstractWritable#E#List {
  /**
   * Holds the host array
   */
  @NotNull
  private #e#[] myArray;

  public #E#Array() {
    myArray = EMPTY_#EC#S;
  }

  /**
   * Constructs an empty array with the specified initial capacity.
   * */
  public #E#Array(int capacity) {
    if (capacity < 0) throw new IllegalArgumentException();
    myArray = capacity == 0 ? EMPTY_#EC#S : new #e#[capacity];
  }

  /**
   * Constructs a #E#Array and adds all elements from {@code copyFrom}
   */
  public #E#Array(#E#List copyFrom) {
    this(copyFrom == null ? 0 : copyFrom.size());
    if (copyFrom != null) {
      addAll(copyFrom);
    }
  }

  /**
   * Constructs a #E#Array and add all elements from {@code iterator}
   */
  public #E#Array(#E#Iterator iterator) {
    myArray = EMPTY_#EC#S;
    if (iterator != null)
      addAll(iterator);
  }

  /**
   * Constructs a #E#Array that is backed by {@code hostArray}
   * See {@link #E#Array#copy(#e#[])} }, {@link #E#Array#create(#e#...)}.
   * */
  public #E#Array(#e#[] hostArray) {
    this(hostArray, hostArray == null ? 0 : hostArray.length);
  }

  /**
   * Constructs a #E#Array with the specified size {@code length} that is backed by {@code hostArray}
   * See {@link #E#Array#copy(#e#[])}, {@link #E#Array#create(#e#...)}.
   * @param length size of the new array. If {@code length} >= {@code hostArray.length}
   *               new size is equal to {@code hostArray.length} otherwise equal to {@code length}.
   * @throws IllegalArgumentException if {@code length < 0}
   * */
  public #E#Array(#e#[] hostArray, int length) {
    if (length < 0) throw new IllegalArgumentException();
    myArray = hostArray == null ? EMPTY_#EC#S : hostArray;
    updateSize(length < 0 ? 0 : length >= myArray.length ? myArray.length : length);
  }

  /**
   * @return #E#Array containing elements from the specified {@code #e#[]} array
   */
  public static #E#Array copy(#e#[] array) {
    return copy(array, array == null ? 0 : array.length);
  }

  /**
   * @return #E#Array with the specified size
   * containing elements from the specified {@code #e#[]} array
   */
  public static #E#Array copy(#e#[] array, int length) {
    return new #E#Array(#E#Collections.arrayCopy(array, 0, length));
  }

  /**
   * @return #E#Array containing elements from {@code iterable}
   */
  public static #E#Array copy(@Nullable #E#Iterable iterable) {
    if (iterable == null) return new #E#Array();
    return #E#Collections.collectIterable(0, iterable);
  }

  /**
   * @return #E#Array containing the specified elements.
   * See {@link #E#Array##E#Array(#e#[])}, {@link #E#Array##E#Array(#e#[], int)}.
   * */
  public static #E#Array create(#e# ... values) {
    if (values == null)
      values = EMPTY_#EC#S;
    return new #E#Array(values);
  }

  /**
   * May be used to convert the collection of {@code #E#} to native #e#[] array:
   * {@code #E#Array.create(values).extractHostArray()}.
   * @return #E#Array containing elements from collection {@code values}
   */
  public static #E#Array create(@Nullable Collection<#E#> values) {
    #e#[] host = EMPTY_#EC#S;
    if (values != null && !values.isEmpty()) {
      int sz = values.size();
      host = new #e#[sz];
      Iterator<#E#> it = values.iterator();
      for (int i = 0; i < sz; ++i) {
        if (it.hasNext()) {
          host[i] = it.next();
        } else assert false : "concurrent modification?";
      }
    }
    return new #E#Array(host);
  }

  public int indexOf(#e# value) {
    return #E#Collections.indexOf(value, myArray, 0, size());
  }

  public final #e# get(int index) {
    return myArray[index];
  }

  public #e#[] toNativeArray(int sourceOffset, #e#[] dest, int destOffset, int length) {
    System.arraycopy(myArray, sourceOffset, dest, destOffset, length);
    return dest;
  }

  /**
   * Increases the capacity of this <tt>#E#Array</tt> instance, if
   * necessary, to ensure that it can hold at least the number of elements
   * specified by {@code expectedCapacity} argument.
   * @param expectedCapacity the desired minimum capacity
   */
  public void ensureCapacity(int expectedCapacity) {
    myArray = #E#Collections.ensureCapacity(myArray, expectedCapacity);
  }

  /**
   * @return the capacity of this array
   */
  public int getCapacity() {
    return myArray.length;
  }

  public void set(int index, #e# value) {
    myArray[index] = value;
  }

  public void setRange(int from, int to, #e# value) {
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

  public void insertMultiple(int index, #e# value, int count) {
    if (count < 0) throw new IllegalArgumentException();
    if (count == 0) return;
    makeSpaceForInsertion(index, index + count);
    Arrays.fill(myArray, index, index + count, value);
  }

  public void expand(int index, int count) {
    if (count < 0)
      throw new IllegalArgumentException();
    makeSpaceForInsertion(index, index + count);
  }

  public void insert(int index, #e# value) {
    makeSpaceForInsertion(index, index + 1);
    myArray[index] = value;
  }

  public void add(#e# value) {
    int sz = size();
    int nsz = sz + 1;
    ensureCapacity(nsz);
    myArray[sz] = value;
    updateSize(nsz);
  }

  public void insertAll(int index, #E#List collection, int sourceIndex, int count) {
    if (count <= 0)
      return;
    makeSpaceForInsertion(index, index + count);
    collection.toNativeArray(sourceIndex, myArray, index, count);
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

  public void setAll(int index, #E#List values, int sourceIndex, int count) {
    if (count <= 0)
      return;
    int sz = size();
    if (index < 0 || index >= sz)
      throw new IndexOutOfBoundsException(index + " " + sz);
    if (index + count > sz)
      throw new IndexOutOfBoundsException(index + " " + count + " " + sz);
    values.toNativeArray(sourceIndex, myArray, index, count);
  }

  public void swap(int index1, int index2) {
    #E#Collections.swap(myArray, index1, index2);
  }

  public void addAll(#e#... ints) {
    if (ints.length == 0)
      return;
    int sz = size();
    int newsz = sz + ints.length;
    ensureCapacity(newsz);
    System.arraycopy(ints, 0, myArray, sz, ints.length);
    updateSize(newsz);
  }

  public void sort(Writable#E#List... sortAlso) {
    if (sortAlso == null || sortAlso.length == 0)
      Arrays.sort(myArray, 0, size());
    else
      super.sort(sortAlso);
  }

  public void addAll(#E#List list) {
    int added = list.size();
    int sz = size();
    int newSize = sz + added;
    ensureCapacity(newSize);
    list.toNativeArray(0, myArray, sz, added);
    updateSize(newSize);
  }

  public void addAll(#E#Set set) {
    int added = set.size();
    int sz = size();
    int newSize = sz + added;
    ensureCapacity(newSize);
    set.toNativeArray(myArray, sz);
    updateSize(newSize);
  }

  @Override
  protected boolean isSorted(boolean checkUnique) {
    int r = #E#Collections.isSortedUnique(!checkUnique, myArray, 0, size());
    return r == 0 || (r < 0 && !checkUnique);
  }

  /**
   * @return true if size of this array equals to {@code array.length} and all elements
   * in the specified array equal to corresponding elements
   */
  public boolean equalOrder(#e#[] array) {
    if (size() != array.length)
      return false;
    for (int i = 0; i < size(); i++)
      if (array[i] != get(i))
        return false;
    return true;
  }

  /**
   * Sorts this array using {@link Arrays#sort(int[])}, removes duplicates and updates size.
   */
  @Override
  public void sortUnique() {
    Arrays.sort(myArray, 0, size());
    updateSize(#E#Collections.removeSubsequentDuplicates(myArray, 0, size()));
  }

  /**
   * removes from this array all of its elements that are not contained in {@code values}
   * <p>Complexity: {@code O((M + N) * log(M))}, where N - {@code size()}, M - {@code values.size()}
   */
  public void retain(#E#List values) {
    retain(values, false);
  }

  /**
   * removes from this array all of its elements that are not contained in {@code values}
   * <p>Complexity: {@code O(N * log(M)}, where N - {@code size()}, M - {@code values.size()}
   * @param values sorted {@code #E#List}
   * */
  public void retainSorted(#E#List values) {
    assert values.isSorted();
    retain(values, true);
  }

  private void retain(#E#List values, boolean valuesSortedStatus) {
    if (values.isEmpty()) {
      clear();
      return;
    }
    #E#List sortedValues = valuesSortedStatus ? values : #E#Collections.toSorted(false, values);
    int i = 0, i2;
    int insertionPoint = 0;
    int mSize = size();
    while (i < mSize) {
      while (i < mSize && sortedValues.binarySearch(myArray[i]) < 0) i++;
      if (i == mSize) break;
      i2 = i;
      while (i2 < mSize && sortedValues.binarySearch(myArray[i2]) >= 0) i2++;

      int diff = i2 - i;
      System.arraycopy(myArray, i, myArray, insertionPoint, diff);
      insertionPoint += diff;
      i = i2;
    }
    updateSize(insertionPoint);
  }

  /**
   * Adds first maxCount elements from collection or the whole collection if it size is less than maxCount.
   * @return number of added elements
   */
  public int addAllNotMore(#E#List collection, int maxCount) {
    int toAdd = Math.min(maxCount, collection.size());
    addAll(collection.subList(0, toAdd));
    return toAdd;
  }

  /**
   * Adds elements from iterable until maxCount elements are added or iterable reaches its end.
   * @return number of added elements
   */
  public int addAllNotMore(#E#Iterable iterable, int maxCount) {
    int counter = 0;
    #E#Iterator it = iterable.iterator();
    while (it.hasNext() && counter < maxCount) {
      add(it.nextValue());
      counter++;
    }
    return counter;
  }

  /**
   * Removes 1 times {@code value} from this sorted list, if this list contains {@code value}.
   * <br>Example: ar = [0, 1, 1, 2]; ar.removeSorted(1) -> [0, 1, 2]
   * @return {@code true} if this set contained {@code value}. Otherwise {@code false}.
   * @see #removeAllSorted(#e#)
   */
  public boolean removeSorted(#e# value) {
    assert isSorted();
    int index = binarySearch(value);
    if (index < 0) return false;
    if (index >= 0) removeAt(index);
    return true;
  }

  /**
   * Removes from this list all elements whose index is contained in the specified {@code IntList indices}
   * <p>Unlike {@link #E#Collections#removeAllAtSorted(Writable#E#List, IntList)}, which in case of #E#Array takes
   * {@code O(N*M)} time, this method requires just {@code O(N + M)}, where N - {@code size()}, M - {@code indices.size()}.
   *
   * @param indices sorted {@code IntIterable}
   * @see com.almworks.integers.#E#Collections#removeAllAtSorted(Writable#E#List, IntList)
   */
  public void removeAllAtSorted(IntIterable indices) {
    IntIterator it = indices.iterator();
    if (!it.hasNext()) return;
    int index = it.nextValue(), to, len;
    int indicesSize = 1;
    int from = index + 1;
    while (it.hasNext()) {
      to = it.nextValue();
      indicesSize++;
      len = to - from;
      System.arraycopy(myArray, from, myArray, index, len);
      index += len;
      from = to + 1;
    }
    System.arraycopy(myArray, from, myArray, index, size() - from);
    updateSize(size() - indicesSize);
  }

  /**
   * Extracts from this array inner buffer with {@code #e#[]} values and clears this array.
   * @return #e#[] array with values from this array.
   */
  public #e#[] extractHostArray() {
    #e#[] array = myArray;
    myArray = EMPTY_#EC#S;
    updateSize(0);
    return array;
  }

  /**
   * Finds positions for inserting elements of sorted {@code src} into
   * this sorted unique array and writes them to {@code points[0]}.
   * {@code points[0][idx]} will be equal to {@code -1} if {@code src.get(idx)} is contained in this array,
   * otherwise equal to index in this array where inserting {@code src.get(idx)} keeps this array sorted unique.
   * <br>Examples:
   * <table>
   *   <thead><tr><th>this</th><th>src</th><th>return</th><th>points[0]</th></tr></thead>
   *   <tr><td>[0, 2, 4, 7, 9, 10]</td><td>[0, 4, 8, 8, 9]</td><td class="right">1</td><td>[-1, -1, 4, -1, -1]</td></tr>
   *   <tr><td>[0, 2, 4, 7, 9, 10]</td><td>[1, 4, 6, 6, 7, 11, 12]</td><td class="right">4</td><td>[1, -1, 3, -1, -1, 6, 6]</td></tr>
   * </table>
   *
   * @param src sorted iterable
   * @param points container for insertion points array, not null.
   * If {@code points[0] == null} or {@code points[0].length < src.size()}, a new array will be allocated in {@code points[0]}.
   * If {@code points[0].length > src.size()}, elements in {@code points[0]} beyond {@code src.size()} will be ignored.
   * @return number of unique elements from {@code src} that are not contained in this array.
   */
  public int getInsertionPoints(#E#SizedIterable src, int[][] points) {
    final int srcSize = src.size(), size = size();
    if (points[0] == null || points[0].length < srcSize) {
      points[0] = new int[srcSize];
    }
    int[] insertionPoints0 = points[0];
    Arrays.fill(insertionPoints0, 0, srcSize, -1);
    int insertCount = 0;
    int destIndex = 0;
    #e# last = 0;
    #E#Iterator it = src.iterator();
    for (int i = 0; i < srcSize; i++) {
      #e# v = it.nextValue();
      if (i > 0 && v == last)
        continue;
      last = v;
      if (destIndex < size) {
        int k = #E#Collections.binarySearch(v, myArray, destIndex, size);
        if (k >= 0) {
          // found
          destIndex = k;
          continue;
        }
        int insertion = -k - 1;
        if (insertion < destIndex) {
          assert false : insertion + " " + destIndex;
          continue;
        }
        destIndex = insertion;
      }
      insertionPoints0[i] = destIndex;
      insertCount++;
    }
    return insertCount;
  }

  /**
   * Merges the specified sorted unique list and this sorted unique array.
   * Depending on {@code size()} and {@code src.size()} uses
   * {@link #E#Array#mergeWithSmall(#E#List)} or {@link #E#Array#mergeWithSameLength(#E#List)}.
   * <p>If this array is smaller than the specified list,
   * it will copy the specified list and merge it with existing contents using {@link #E#Array#mergeWithSmall(#E#List)}.
   */
  public void merge(#E#List src) {
    if (src.isEmpty()) return;
    final int srcSize = src.size(), size = size();
    float k = 1f * size / (srcSize + 1);
    if (k < 10) {
      k = 1f * srcSize / (size + 1);
      if (k < 10) {
        mergeWithSameLength(src);
      } else {
        #e#[] small = myArray;
        myArray = #E#Collections.ensureCapacity(EMPTY_#EC#S, size + srcSize);
        src.toNativeArray(0, myArray, 0, srcSize);
        updateSize(srcSize);
        mergeWithSmall(new #E#Array(small, size));
      }
    } else {
      mergeWithSmall(src);
    }
  }

  /**
   * <p>Merges the specified sorted list and this sorted array into this sorted unique array.
   * If {@code src} is not sorted or this array is not sorted unique, result is undefined.
   * {@code src} can contain duplicates.
   * <p>If {@code getCapacity() < src.size() + size()}, will do reallocation.
   * <p>Complexity: {@code O(eps(N/T, this, src) * N + T * log(N))}, where {@code N = size()} and {@code T = src.size()}.
   * {@code eps} grows as {@code N/T} grows.
   * {@code eps} also depends on how evenly {@code src} values are distributed in this array: it is higher when
   * they are uniformly distributed.
   * In the general case, if {@code N/T > 4}, then on average {@code eps < 0.5}.
   * <p>Prefer to use this method over {@link com.almworks.integers.#E#Array#mergeWithSameLength(#E#List)}
   * If {@code src.size()} is much smaller than {@code size()}. If they are close
   * use {@link com.almworks.integers.#E#Array#mergeWithSameLength(#E#List)}
   *
   * @param  src sorted list.
   * @see #merge(#E#List)
   * */
  public void mergeWithSmall(#E#List src) {
    mergeWithSmall(src, new int[][]{null});
  }

  /**
   *
   * @param insertionPoints contains temporary array in the first element. If it isn't there,
   *                        it will be written there
   * @see #mergeWithSmall(#E#List)
   */
  public void mergeWithSmall(#E#List src, int[][] insertionPoints) {
    if (src.isEmpty()) return;
    final int oldSize = size(), srcSize = src.size();
    if (oldSize + srcSize > myArray.length) {
      // merge with reallocation
      int oldLength = myArray.length;
      #e#[] newDest = new #e#[Math.max(oldSize + srcSize, oldLength * 2)];
      #e# last = 0;
      int newDestSize = 0;
      int destIndex = 0;
      for (int i = 0; i < srcSize; i++) {
        #e# v = src.get(i);
        if (i > 0 && v == last)
          continue;
        last = v;
        if (destIndex < oldSize) {
          int k = #E#Collections.binarySearch(v, myArray, destIndex, oldSize);
          if (k >= 0) {
            // found
            continue;
          }
          int insertion = -k - 1;
          if (insertion < destIndex) {
            assert false : insertion + " " + destIndex;
            continue;
          }
          int chunkSize = insertion - destIndex;
          System.arraycopy(myArray, destIndex, newDest, newDestSize, chunkSize);
          destIndex = insertion;
          newDestSize += chunkSize;
        }
        newDest[newDestSize++] = v;
      }
      if (destIndex < oldSize) {
        int chunkSize = oldSize - destIndex;
        System.arraycopy(myArray, destIndex, newDest, newDestSize, chunkSize);
        newDestSize += chunkSize;
      }
      myArray = newDest;
      updateSize(newDestSize);
    } else {
      // merge in place
      // a) find insertion points and count how many to be inserted
      if (insertionPoints == null) insertionPoints = new int[][]{null};
      int insertCount = getInsertionPoints(src, insertionPoints);
      int[] insertionPoints0 = insertionPoints[0];
      int destIndex = oldSize;
      updateSize(oldSize + insertCount);
      int i = srcSize - 1;
      while (insertCount > 0) {
        // get next to insert
        while (i >= 0 && insertionPoints0[i] == -1)
          i--;
        assert i >= 0 : i;
        int insertion = insertionPoints0[i];
        if (destIndex > insertion) {
          System.arraycopy(myArray, insertion, myArray, insertion + insertCount, destIndex - insertion);
          destIndex = insertion;
        }
        myArray[insertion + insertCount - 1] = src.get(i);
        insertCount--;
        i--;
      }
    }
  }

  /**
   * Merges the specified sorted unique list into this sorted unique array.
   * If {@code getCapacity() < src.size() + size()}, will do reallocation.<br>
   * If src or this array are not sorted unique, result is undefined.
   * Complexity: {@code O(N + T)}, where {@code N = size()} and {@code T = src.size()}.<br>
   * Prefer to use if {@code size()} and {@code src.size()} are comparable.
   * If {@code src.size()} is much smaller than {@code size()},
   * it's better to use {@link com.almworks.integers.#E#Array#mergeWithSmall(#E#List)}
   *
   * @param  src sorted list of numbers (set)
   * */
  public void mergeWithSameLength(#E#List src) {
    int srcSize = src.size();
    int totalLength = size() + srcSize;
    if (totalLength > myArray.length) {
      // merge with reallocation
      merge0withReallocation(src, srcSize, totalLength);
    } else {
      merge0inPlace(src, srcSize, totalLength);
    }
  }

  private void merge0inPlace(#E#List src, int srcSize, int totalLength) {
    // in place
    // 1. find offset (scan for duplicates)
    // 2. merge starting from the end
    int size = size();
    if (size > 0 && srcSize > 0 && myArray[0] > src.get(srcSize - 1)) {
      System.arraycopy(myArray, 0, myArray, srcSize, size);
      src.toNativeArray(0, myArray, 0, srcSize);
      updateSize(totalLength);
    } else if (size > 0 && srcSize > 0 && myArray[size - 1] < src.get(0)) {
      src.toNativeArray(0, myArray, size, srcSize);
      updateSize(totalLength);
    } else {
      int insertCount = 0;
      int pi = 0, pj = 0;
      while (pi < size && pj < srcSize) {
        #e# vi = myArray[pi];
        #e# vj = src.get(pj);
        if (vi < vj) {
          pi++;
        } else if (vi > vj) {
          pj++;
          insertCount++;
        } else {
          assert vi == vj;
          pi++;
          pj++;
        }
      }
      insertCount += (srcSize - pj);
      pi = size - 1;
      pj = srcSize - 1;
      int i = size + insertCount;
      while (pi >= 0 && pj >= 0) {
        assert i > pi : i + " " + pi;
        #e# vi = myArray[pi];
        #e# vj = src.get(pj);
        if (vi < vj) {
          myArray[--i] = vj;
          pj--;
        } else if (vi > vj) {
          myArray[--i] = vi;
          pi--;
        } else {
          assert vi == vj;
          myArray[--i] = vi;
          pi--;
          pj--;
        }
      }
      if (pj >= 0) {
        int length = pj + 1;
        src.toNativeArray(0, myArray, 0, length);
        i -= length;
      } else if (pi >= 0) {
        i -= pi + 1;
      }
      assert i == 0 : i;
      updateSize(size + insertCount);
    }
  }

  private void merge0withReallocation(#E#List src, int srcSize, int totalLength) {
    int newSize = Math.max(totalLength, myArray.length * 2);
    #e#[] newArray = new #e#[newSize];
    int pi = 0, pj = 0;
    int i = 0;
    int size = size();
    if (size > 0 && srcSize > 0) {
      // boundary conditions: quickly merge disjoint sets
      if (myArray[0] > src.get(srcSize - 1)) {
        src.toNativeArray(0, newArray, 0, srcSize);
        i = pj = srcSize;
      } else if (myArray[size - 1] < src.get(0)) {
        System.arraycopy(myArray, 0, newArray, 0, size);
        i = pi = size;
      }
    }
    while (pi < size && pj < srcSize) {
      #e# vi = myArray[pi];
      #e# vj = src.get(pj);
      if (vi < vj) {
        newArray[i++] = vi;
        pi++;
      } else if (vi > vj) {
        newArray[i++] = vj;
        pj++;
      } else {
        assert vi == vj;
        newArray[i++] = vi;
        pi++;
        pj++;
      }
    }
    if (pi < size) {
      int length = size - pi;
      System.arraycopy(myArray, pi, newArray, i, length);
      i += length;
    } else if (pj < srcSize) {
      int length = srcSize - pj;
      src.toNativeArray(pj, newArray, i, length);
      i += length;
    }
    myArray = newArray;
    updateSize(i);
  }

  /**
   * Shuffles this array using {@code random} as number generator.
   */
  public void shuffle(Random random) {
    for (int curSize = size() - 1; 0 < curSize; curSize--) {
      int ind = random.nextInt(curSize);
      #E#Collections.swap(myArray, ind, curSize);
    }
  }
}
