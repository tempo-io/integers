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

import com.almworks.integers.util.IntegersDebug;
import com.almworks.integers.util.LongMeasurableIterable;
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

  /**
   * Constructs an empty array with the specified initial capacity.
   * */
  public LongArray(int capacity) {
    if (capacity < 0) throw new IllegalArgumentException();
    myArray = capacity == 0 ? EMPTY_LONGS : new long[capacity];
  }

  /**
   * Constructs a LongArray and adds all elements from {@code copyFrom}
   */
  public LongArray(LongList copyFrom) {
    this(copyFrom == null ? 0 : copyFrom.size());
    if (copyFrom != null) {
      addAll(copyFrom);
    }
  }

  /**
   * Constructs a LongArray and add all elements from {@code iterator}
   */
  public LongArray(LongIterator iterator) {
    myArray = EMPTY_LONGS;
    if (iterator != null)
      addAll(iterator);
  }

  /**
   * Constructs a LongArray that is backed by {@code hostArray}
   * See {@link LongArray#copy(long[])} }, {@link LongArray#create(long...)}.
   * */
  public LongArray(long[] hostArray) {
    this(hostArray, hostArray == null ? 0 : hostArray.length);
  }

  /**
   * Constructs a LongArray with the specified size {@code length} that is backed by {@code hostArray}
   * See {@link LongArray#copy(long[])}, {@link LongArray#create(long...)}.
   * @param length size of the new array. If {@code length} >= {@code hostArray.length}
   *               new size is equal to {@code hostArray.length} otherwise equal to {@code length}.
   * @throws IllegalArgumentException if {@code length < 0}
   * */
  public LongArray(long[] hostArray, int length) {
    if (length < 0) throw new IllegalArgumentException();
    myArray = hostArray == null ? EMPTY_LONGS : hostArray;
    updateSize(length < 0 ? 0 : length >= myArray.length ? myArray.length : length);
  }

  /**
   * @return LongArray containing elements from the specified {@code long[]} array
   */
  public static LongArray copy(long[] array) {
    return copy(array, array == null ? 0 : array.length);
  }

  /**
   * @return LongArray with the specified size
   * containing elements from the specified {@code long[]} array
   */
  public static LongArray copy(long[] array, int length) {
    return new LongArray(LongCollections.arrayCopy(array, 0, length));
  }

  /**
   * @return LongArray containing elements from the {@code iterable}
   */
  public static LongArray copy(@Nullable LongIterable iterable) {
    if (iterable == null) return new LongArray();
    return LongCollections.collectIterables(iterable);
  }

  /**
   * @return LongArray containing the specified elements.
   * See {@link LongArray#LongArray(long[])}, {@link LongArray#LongArray(long[], int)}.
   * */
  public static LongArray create(long ... values) {
    if (values == null)
      values = EMPTY_LONGS;
    return new LongArray(values);
  }

  /**
   * May be used to convert the collection of {@code Long} to native long[] array:
   * {@code LongArray.create(values).extractHostArray()}.
   * @return LongArray containing elements from collection {@code values}
   */
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

  public int indexOf(long value) {
    return LongCollections.indexOf(myArray, 0, size(), value);
  }

  public final long get(int index) {
    return myArray[index];
  }

  public long[] toNativeArray(int sourceOffset, long[] dest, int destOffset, int length) {
    System.arraycopy(myArray, sourceOffset, dest, destOffset, length);
    return dest;
  }

  /**
   * Increases the capacity of this <tt>LongArray</tt> instance, if
   * necessary, to ensure that it can hold at least the number of elements
   * specified by the {@code expectedCapacity} argument.
   * @param expectedCapacity the desired minimum capacity
   */
  public void ensureCapacity(int expectedCapacity) {
    myArray = LongCollections.ensureCapacity(myArray, expectedCapacity);
  }

  /**
   * @return the capacity of this array
   */
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

  public void setAll(int index, LongList values, int sourceIndex, int count) {
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

  public LongArray sort(WritableLongList... sortAlso) {
    if (sortAlso == null || sortAlso.length == 0)
      Arrays.sort(myArray, 0, size());
    else
      super.sort(sortAlso);
    return this;
  }

  public void addAll(LongList list) {
    int added = list.size();
    int sz = size();
    int newSize = sz + added;
    ensureCapacity(newSize);
    list.toNativeArray(0, myArray, sz, added);
    updateSize(newSize);
  }

  @Override
  protected boolean isSorted(boolean isUnique) {
    int r = LongCollections.isSortedUnique(!isUnique, myArray, 0, size());
    return r == 0 || (r < 0 && !isUnique);
  }

  /**
   * @return true if size of this array equals to {@code array.length} and all elements
   * in the specified array equal to corresponding elements
   */
  public boolean equalOrder(long[] array) {
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
  public LongArray sortUnique() {
    Arrays.sort(myArray, 0, size());
    updateSize(LongCollections.removeSubsequentDuplicates(myArray, 0, size()));
    return this;
  }

  /**
   * removes from this array all of its elements that are not contained in the {@code values}
   * <p>Complexity: {@code O((M + N) * log(M))}, where N - {@code size()}, M - {@code values.size()}
   */
  public void retain(LongList values) {
    retain(values, false);
  }

  /**
   * removes from this array all of its elements that are not contained in the {@code values}
   * <p>Complexity: {@code O(N * log(M)}, where N - {@code size()}, M - {@code values.size()}
   * @param values sorted {@code LongList}
   * */
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

  /**
   * Removes {@code value} from this sorted list, if this list contains the {@code value}.
   * @return true if this array was modified otherwise false
   */
  public boolean removeSorted(long value) {
    assert isSorted();
    int index = binarySearch(value);
    if (index < 0) return false;
    if (index >= 0) removeAt(index);
    return true;
  }

  // todo javadoc
  // remove from this sorted array indexes from the specified sorted list
  // @return true if this array was modified otherwise false
  public void removeSortedIndexes(IntList list) {
    IntIterator it = list.iterator();
    if (!it.hasNext()) return;
    int index = it.nextValue(), to, len;
    int from = index + 1;
    while (it.hasNext()) {
      to = it.nextValue();
      len = to - from;
      System.arraycopy(myArray, from, myArray, index, len);
      index += len;
      from = to + 1;
    }
    System.arraycopy(myArray, from, myArray, index, size() - from);
    updateSize(size() - list.size());
  }

  /**
   * Extracts from this array inner buffer with {@code long[]} values and clears this array.
   * @return long[] array with values from this array.
   */
  public long[] extractHostArray() {
    long[] array = myArray;
    myArray = EMPTY_LONGS;
    updateSize(0);
    return array;
  }

  // todo add javadoc
  // this: [0,2,4,7,9,10], src: [0,4,8,9] --> [0,2, -1]
  public int getInsertionPoints(LongMeasurableIterable src, int[][] insertionPoints) {
    if (insertionPoints[0] == null || insertionPoints[0].length < src.size()) {
      insertionPoints[0] = new int[src.size()];
    }
    int[] insertionPoints0 = insertionPoints[0];
    Arrays.fill(insertionPoints0, 0, src.size(), -1);
    int insertCount = 0;
    int destIndex = 0;
    long last = 0;
    LongIterator it = src.iterator();
    for (int i = 0, n = src.size(); i < n; i++) {
      long v = it.nextValue();
      if (i > 0 && v == last)
        continue;
      last = v;
      if (destIndex < size()) {
        int k = LongCollections.binarySearch(v, myArray, destIndex, size());
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
//    if (output) System.out.println(Arrays.toString(insertionPoints[0]));
//    bh.consume(insertionPoints);
    return insertCount;
  }

  /**
   * Merge the specified sorted list and this sorted unique array. Depending on {@code size()} and {@code src.size()} uses {@link LongArray#mergeWithSmall(LongList)} or
   * {@link LongArray#mergeWithSameLength(LongList)}.
   * */
  public LongArray merge(LongList src) {
    if (src.size() == 0) return this;
    int k = size() / src.size();
    if (k < 10) {
      mergeWithSameLength(src);
    } else {
      mergeWithSmall(src);
    }
    return this;
  }

  /**
   * <p>Merge the specified sorted list and this sorted array. If src or this array are not sorted, result is undefined.
   * If there {@code getCapacity() < src.size() + size()}, will do reallocation.
   * <p>Complexity: {@code O(eps(N, T, N/T) * N + T * log(N))}, where {@code N = size()} and {@code T = src.size()}.
   * {@code eps} depend on ratio of {@code N/T} and mixing of {@code src} and this array.
   * In the general case, if N/T > 4, then eps < 0.5.
   * <p>Prefer to use this method over {@link com.almworks.integers.LongArray#mergeWithSameLength(LongList)}
   * if {@code src.size()} is much smaller than {@code size()}. If they are close
   * use {@link com.almworks.integers.LongArray#mergeWithSameLength(LongList)}
   *
   * @param  src sorted list.
   * @return this.
   * @see LongArray#merge(LongList)
   * */
  public LongArray mergeWithSmall(LongList src) {
    mergeWithSmall(src, new int[][]{null});
    return this;
  }

  /**
   * @param insertionPoints contains temporary array in the first element. If it isn't there,
   *                        it will be written there
   * @see #mergeWithSmall(LongList)
   */
  public LongArray mergeWithSmall(LongList src, int[][] insertionPoints) {
    if (src.size() == 0)
      return this;
    if (size() + src.size() > myArray.length) {
      // merge with reallocation
      int oldLength = myArray.length;
      long[] newDest = new long[Math.max(size() + src.size(), oldLength * 2)];
      long last = 0;
      int newDestSize = 0;
      int destIndex = 0;
      for (int i = 0; i < src.size(); i++) {
        long v = src.get(i);
        if (i > 0 && v == last)
          continue;
        last = v;
        if (destIndex < size()) {
          int k = LongCollections.binarySearch(v, myArray, destIndex, size());
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
      if (destIndex < size()) {
        int chunkSize = size() - destIndex;
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
      int destIndex = size();
      updateSize(size() + insertCount);
      int i = src.size() - 1;
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
    return this;
  }

  /**
   * Merge the specified sorted list and sorted this array. If there {@code getCapacity() < src.size() + size()}, will go reallocation.<br>
   * Complexity: {@code O(N + T)}, where N - size() and T - {@code src.size()}.<br>
   * Prefer to use if the {@code size()} and {@code src.size()} are comparable.
   * If the {@code src.size()} is much smaller than {@code size()},
   * it's better to use {@link com.almworks.integers.LongArray#mergeWithSmall(LongList)}
   *
   * @param  src sorted list of numbers (set)
   *
   * @return this
   * */
  public LongArray mergeWithSameLength(LongList src) {
    int srcSize = src.size();
    int totalLength = size() + srcSize;
    if (totalLength > myArray.length) {
      // merge with reallocation
      merge0withReallocation(src, srcSize, totalLength);
    } else {
      merge0inPlace(src, srcSize, totalLength);
    }
    return this;
  }

  private void merge0inPlace(LongList src, int srcSize, int totalLength) {
    // in place
    // 1. find offset (scan for duplicates)
    // 2. merge starting from the end
    if (size() > 0 && srcSize > 0 && myArray[0] > src.get(srcSize - 1)) {
      System.arraycopy(myArray, 0, myArray, srcSize, size());
      src.toNativeArray(0, myArray, 0, srcSize);
      updateSize(totalLength);
    } else if (size() > 0 && srcSize > 0 && myArray[size() - 1] < src.get(0)) {
      src.toNativeArray(0, myArray, size(), srcSize);
      updateSize(totalLength);
    } else {
      int insertCount = 0;
      int pi = 0, pj = 0;
      while (pi < size() && pj < srcSize) {
        long vi = myArray[pi];
        long vj = src.get(pj);
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
      pi = size() - 1;
      pj = srcSize - 1;
      boolean curThis = false;
      int lastInserted = -1;
      int i = size() + insertCount;
      while (pi >= 0 && pj >= 0) {
        assert i > pi : i + " " + pi;
        long vi = myArray[pi];
        long vj = src.get(pj);
        if (vi < vj) {
          myArray[--i] = vj;
          pj--;
        } else {
          if (vi > vj) {
            myArray[--i] = vi;
            pi--;
          } else {
            assert vi == vj;
            myArray[--i] = vi;
            pi--;
            pj--;
          }
        }
      }
      if (pj >= 0) {
        int size = pj + 1;
        src.toNativeArray(0, myArray, 0, size);
        i -= size;
      } else if (pi >= 0) {
        i -= pi + 1;
      }
      assert i == 0 : i;
      updateSize(size() + insertCount);
    }
  }

  private void merge0withReallocation(LongList src, int srcSize, int totalLength) {
    int newSize = Math.max(totalLength, myArray.length * 2);
    long[] newArray = new long[newSize];
    int pi = 0, pj = 0;
    int i = 0;
    if (size() > 0 && srcSize > 0) {
      // boundary conditions: quickly merge disjoint sets
      if (myArray[0] > src.get(srcSize - 1)) {
        src.toNativeArray(0, newArray, 0, srcSize);
        i = pj = srcSize;
      } else if (myArray[size() - 1] < src.get(0)) {
        System.arraycopy(myArray, 0, newArray, 0, size());
        i = pi = size();
      }
    }
    while (pi < size() && pj < srcSize) {
      long vi = myArray[pi];
      long vj = src.get(pj);
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
    if (pi < size()) {
      int size = size() - pi;
      System.arraycopy(myArray, pi, newArray, i, size);
      i += size;
    } else if (pj < srcSize) {
      int size = srcSize - pj;
      src.toNativeArray(pj, newArray, i, size);
      i += size;
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
      LongCollections.swap(myArray, ind, curSize);
    }
  }
}
