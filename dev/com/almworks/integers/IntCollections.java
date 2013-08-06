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

// CODE GENERATED FROM com/almworks/integers/PCollections.tpl


package com.almworks.integers;

import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.almworks.integers.IntegersUtils.EMPTY_INTS;

public class IntCollections {
  public static int[] toNativeArray(IntIterable iterable) {
    if (iterable instanceof IntList) return ((IntList) iterable).toNativeArray();
    return toNativeArray(iterable.iterator());
  }

  public static int[] toNativeArray(IntIterator it) {
    if (!it.hasNext()) return EMPTY_INTS;
    IntArray array = new IntArray();
    array.addAll(it);
    return array.toNativeArray();
  }

  public static int[] toSortedNativeArray(IntIterable iterable) {
    int[] array = toNativeArray(iterable);
    Arrays.sort(array);
    return array;
  }

  public static IntList toSortedUnique(IntIterable values) {
    return toSorted(true, values);
  }

  public static IntList toSorted(boolean unique, IntIterable values) {
    if (values instanceof IntList) {
      IntList list = (IntList) values;
      if ((unique && list.isUniqueSorted()) || (!unique && list.isSorted())) return list;
    }
    int[] array = toNativeArray(values.iterator());
    if (array.length == 0) return IntList.EMPTY;
    Arrays.sort(array);
    int length = unique ? removeSubsequentDuplicates(array, 0, array.length) : array.length;
    return new IntArray(array, length);
  }

  public static IntList toSortedUnique(int[] values) {
    int res = isSortedUnique(true, values, 0, values.length);
    if (res == 0) return new IntArray(values);
    if (res < 0) {
      int[] copy = new int[-res];
      int i = 1;
      int prev = values[0];
      for (int j = 1; j < values.length; j++) {
        int value = values[j];
        if (value > prev) {
          copy[i] = value;
          i++;
          prev = value;
        }
      }
      assert i == copy.length;
      return new IntArray(copy);
    }
    IntArray copy = IntArray.copy(values);
    copy.sortUnique();
    return copy;
  }

  public static boolean isSorted(@Nullable int[] ints) {
    return ints == null || isSorted(ints, 0, ints.length);
  }

  /**
   * @return true if arrays is sorted in ascending order
   */
  public static boolean isSorted(int[] array, int offset, int length) {
    return isSortedUnique(true, array, offset, length) <= 0;
  }

  /**
   * Checks if slice of array is sorted and doesn't contain duplicates. Empty slices and slices of length 1 are always sorted and unique.
   * @param checkNotUnique don't stop if head is sorted but duplicate found. If false terminates and returns index where terminated.
   * @param ints array to check. if null is treated as empty array
   * @param offset begin of array slice to be checked
   * @param length of array slice to be checked
   * @return positive no info: element at returned index is less or equal to previous. Check terminated at the index.
   * If checkNotUnique is true the element at returned index is less than previous and head is sorted but may contain duplicates.<br>
   * negative array is sorted but contains duplicates. Return is -count where count is number of duplicates found<br>
   * 0 array is sorted and all elements are unique
   * @throws IllegalArgumentException if offset or length is negative
   * @throws ArrayIndexOutOfBoundsException if offset+length is greater than array length
   */
  @SuppressWarnings({"NonBooleanMethodNameMayNotStartWithQuestion"})
  public static int isSortedUnique(boolean checkNotUnique, @Nullable int[] ints, int offset, int length) {
    if (ints == null) ints = EMPTY_INTS;
    if (length < 0 || offset < 0) throw new IllegalArgumentException(offset + " " + length);
    if (ints.length < offset + length) throw new ArrayIndexOutOfBoundsException(offset + "+" + length + ">" + ints.length);
    if (length < 2) return 0;
    int dupCount = 0;
    int prev = ints[offset];
    for (int i = 1; i < length; i++) {
      int next = ints[i + offset];
      if (next < prev) return i;
      else if (next == prev) {
        if (!checkNotUnique) return i;
        dupCount++;
      }
      prev = next;
    }
    return -dupCount;
  }

  public static void swap(int[] x, int a, int b) {
    int t = x[a];
    x[a] = x[b];
    x[b] = t;
  }

  public static int binarySearch(int val, int[] array) {
    return binarySearch(val, array, 0, array.length);
  }

  /**
   * Copied from Arrays.
   *
   * @param from index to start search, inclusive
   * @param to   ending index, exclusive
   */
  public static int binarySearch(int key, int[] a, int from, int to) {
    int low = from;
    int high = to - 1;

    while (low <= high) {
      int mid = (low + high) >> 1;
      int midVal = a[mid];

      if (midVal < key)
        low = mid + 1;
      else if (midVal > key)
        high = mid - 1;
      else
        return mid; // key found
    }
    return -(low + 1);  // key not found.
  }

  /**
  * Replaces subsequences of equal elements with single element. Common usage is remove duplicates from
  * sorted array and make all elements unique.
  * @return new length of an array without duplicated elements 
  */
  public static int removeSubsequentDuplicates(int[] array, int offset, int length) {
    if (length < 2)
      return length;
    int index = offset;
    length += offset;
    while (index < length - 1) {
      int prev = array[index];
      int seqEnd = index + 1;
      while (seqEnd < length && prev == array[seqEnd])
        seqEnd++;
      if (seqEnd > index + 1)
        System.arraycopy(array, seqEnd, array, index + 1, length - seqEnd);
      length -= seqEnd - index - 1;
      index++;
    }
    return length - offset;
  }

  /** @return index of a duplicate (not necessarily leftmost) or -1 if none */
  public static int findDuplicate(IntList unsorted) {
    IntArray sorted = new IntArray(unsorted);
    sorted.sort();
    return findDuplicateSorted(unsorted);
  }

  /** @return index of the leftmost duplicate or -1 if none*/
  public static int findDuplicateSorted(IntList sorted) {
    int prev = 0;
    for (int i = 0, m = sorted.size(); i < m; ++i) {
      int k = sorted.get(i);
      if (i > 0 && prev == k) return i;
      prev = k;
    }
    return -1;
  }

  public static int[] ensureCapacity(@Nullable int[] array, int capacity) {
    int length = array == null ? -1 : array.length;
    if (length >= capacity)
      return array;
    if (capacity == 0)
      return EMPTY_INTS;
    return reallocArray(array, Math.max(16, Math.max(capacity, length * 2)));
  }

  public static int[] reallocArray(@Nullable int[] array, int length) {
    assert length >= 0 : length;
    int current = array == null ? -1 : array.length;
    if (current == length)
      return array;
    if (length == 0)
      return EMPTY_INTS;
    int[] newArray = new int[length];
    int copy = Math.min(length, current);
    if (copy > 0)
      System.arraycopy(array, 0, newArray, 0, copy);
    return newArray;
  }

  public static int indexOf(int[] ints, int from, int to, int value) {
    for (int i = from; i < to; i++) {
      if (ints[i] == value)
        return i;
    }
    return -1;
  }

  public static int[] arrayCopy(int[] array, int offset, int length) {
    if (length == 0)
      return EMPTY_INTS;
    int[] copy = new int[length];
    System.arraycopy(array, offset, copy, 0, length);
    return copy;
  }

  public static int compare(int a, int b) {
    return (a < b ? -1 : (a == b ? 0 : 1));
  }

  /**
   * Returns IntList adapter of the specified collection. If collection changes, the returned IntList changes also. <br>
   * Beware of unboxing: each call to the returned IntList leads to unboxing of the source collection element. If you will frequently access the returned IntList, it's a better idea to make a copy. See {@link IntArray#create(java.util.Collection)}
   */
  public static IntList asIntList(@Nullable final List<Integer> coll) {
    if (coll == null || coll.isEmpty()) return IntList.EMPTY;
    return new AbstractIntList() {
      @Override
      public int size() {
        return coll.size();
      }

      @Override
      public int get(int index) throws NoSuchElementException {
        return coll.get(index);
      }
    };
  }

  /**
   * This algorithm supposes that the set to intersect with is usually shorter than the merged ones.
   * It also supposes that a and b have a great deal of common elements (this assumption is not very important, though).
   * */
  public static IntList uniteTwoLengthySortedSetsAndIntersectWithThirdShort(IntList a, IntList b, IntList intersectWith) {
    IntArray result = new IntArray(Math.min(intersectWith.size(), 16));
    int ia = 0;
    int sza = a.size();
    int ib = 0;
    int szb = b.size();
    int v;
    boolean add;
    for (IntIterator iiw : intersectWith) {
      v = iiw.value();
      add = false;
      ia = a.binarySearch(v, ia, sza);
      if (ia >= 0) {
        add = true;
      } else {
        ia = -ia - 1;
        ib = b.binarySearch(v, ib, szb);
        if (ib >= 0) {
          add = true;
        } else {
          ib = -ib - 1;
        }
      }
      if (add) result.add(v);
    }
    return result;
  }

  public static IntList diffSortedLists(IntList a, IntList b) {
    int ia = 0;
    int sza = a.size();
    int ib = 0;
    int szb = b.size();
    IntArray diff = new IntArray();
    while (ia < sza || ib < szb) {
      if (ia >= sza) {
        for (; ib < szb; ++ib) diff.add(b.get(ib));
        break;
      }
      if (ib >= szb) {
        for (; ia < sza; ++ia) diff.add(a.get(ia));
        break;
      }
      int ea = a.get(ia);
      int eb = b.get(ib);
      if (ea < eb) {
        diff.add(ea);
        ++ia;
      } else if (ea > eb) {
        diff.add(eb);
        ++ib;
      } else {
        ++ia;
        ++ib;
      }
    }
    return diff;
  }

  public static void removeAllAtSorted(WritableIntList list, IntList indexes) {
    if (!indexes.isSorted()) throw new IllegalArgumentException("Indexes are not sorted: " + indexes);
    int rangeStart = -1;
    int rangeFinish = -2;
    int diff = 0;
    for (IntIterator it : indexes) {
      int ind = it.value();
      if (rangeFinish < 0) {
        rangeStart = ind;
      } else if (ind != rangeFinish) {
        assert rangeStart >= 0 : list + " " + indexes + ' ' + it + ' ' + rangeStart + ' ' + rangeFinish;
        assert ind > rangeFinish : indexes + " " + ind + ' ' + rangeFinish + ' ' + rangeStart;
        list.removeRange(rangeStart - diff, rangeFinish - diff);
        diff += rangeFinish - rangeStart;
        rangeStart = ind;
      }
      rangeFinish = ind + 1;
    }
    if (rangeFinish - rangeStart >= 1) list.removeRange(rangeStart - diff, rangeFinish - diff);
  }
}
