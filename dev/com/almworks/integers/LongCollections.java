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

import com.almworks.integers.func.IntFunction2;
import com.almworks.integers.func.IntProcedure2;
import com.almworks.integers.optimized.SameValuesLongList;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntegersUtils.EMPTY_LONGS;

public class LongCollections {
  public static long[] toNativeArray(LongIterable iterable) {
    if (iterable instanceof LongList) return ((LongList) iterable).toNativeArray();
    return toNativeArray(iterable.iterator());
  }

  public static long[] toNativeArray(LongIterator it) {
    if (!it.hasNext()) return EMPTY_LONGS;
    LongArray array = new LongArray();
    array.addAll(it);
    return array.toNativeArray();
  }

  public static long[] toSortedNativeArray(LongIterable iterable) {
    long[] array = toNativeArray(iterable);
    Arrays.sort(array);
    return array;
  }

  public static LongList toSortedUnique(LongIterable values) {
    return toSorted(true, values);
  }

  public static LongList toSorted(boolean unique, LongIterable values) {
    if (values instanceof LongList) {
      LongList list = (LongList) values;
      if ((unique && list.isUniqueSorted()) || (!unique && list.isSorted())) return list;
    }
    long[] array = toNativeArray(values.iterator());
    if (array.length == 0) return LongList.EMPTY;
    Arrays.sort(array);
    int length = unique ? removeSubsequentDuplicates(array, 0, array.length) : array.length;
    return new LongArray(array, length);
  }

  public static LongList toSortedUnique(long[] values) {
    if (values.length == 0) {
      return LongList.EMPTY;
    } else {
      return toWritableSortedUnique(values);
    }
  }

  public static LongArray toWritableSortedUnique(long[] values) {
    int res = isSortedUnique(true, values, 0, values.length);
    if (res == 0) return new LongArray(values);
    if (res < 0) {
      long[] copy = new long[-res];
      int i = 1;
      long prev = values[0];
      for (int j = 1; j < values.length; j++) {
        long value = values[j];
        if (value > prev) {
          copy[i] = value;
          i++;
          prev = value;
        }
      }
      assert i == copy.length;
      return new LongArray(copy);
    }
    LongArray copy = LongArray.copy(values);
    copy.sortUnique();
    return copy;
  }

  public static boolean isSorted(@Nullable long[] ints) {
    return ints == null || isSorted(ints, 0, ints.length);
  }

  /**
   * @return true if arrays is sorted in ascending order
   */
  public static boolean isSorted(long[] array, int offset, int length) {
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
  public static int isSortedUnique(boolean checkNotUnique, @Nullable long[] ints, int offset, int length) {
    if (ints == null) ints = EMPTY_LONGS;
    if (length < 0 || offset < 0) throw new IllegalArgumentException(offset + " " + length);
    if (ints.length < offset + length) throw new ArrayIndexOutOfBoundsException(offset + "+" + length + ">" + ints.length);
    if (length < 2) return 0;
    int dupCount = 0;
    long prev = ints[offset];
    for (int i = 1; i < length; i++) {
      long next = ints[i + offset];
      if (next < prev) return i;
      else if (next == prev) {
        if (!checkNotUnique) return i;
        dupCount++;
      }
      prev = next;
    }
    return -dupCount;
  }

  public static void swap(long[] x, int a, int b) {
    long t = x[a];
    x[a] = x[b];
    x[b] = t;
  }

  public static int binarySearch(long val, long[] array) {
    return binarySearch(val, array, 0, array.length);
  }

  /**
   * Copied from Arrays.
   *
   * @param from index to start search, inclusive
   * @param to   ending index, exclusive
   */
  public static int binarySearch(long val, long[] a, int from, int to) {
    int low = from;
    int high = to - 1;

    while (low <= high) {
      int mid = (low + high) >> 1;
      long midVal = a[mid];

      if (midVal < val)
        low = mid + 1;
      else if (midVal > val)
        high = mid - 1;
      else
        return mid; // val found
    }
    return -(low + 1);  // val not found.
  }

  /**
   * Replaces subsequences of equal elements with single element. Common usage is remove duplicates from
   * sorted array and make all elements unique.
   * @return new length of an array without duplicated elements
   */
  public static int removeSubsequentDuplicates(long[] array, int offset, int length) {
    if (length < 2)
      return length;
    int index = offset;
    length += offset;
    while (index < length - 1) {
      long prev = array[index];
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
  public static int findDuplicate(LongList unsorted) {
    final LongArray sorted = new LongArray(unsorted);
    final IntArray perms = new IntArray(IntProgression.arithmetic(0, sorted.size()));
    IntegersUtils.quicksort(sorted.size(),
      new IntFunction2() {
        @Override
        public int invoke(int a, int b) {
          return LongCollections.compare(sorted.get(a), sorted.get(b));
        }
      },
      new IntProcedure2() {
        @Override
        public void invoke(int a, int b) {
          sorted.swap(a, b);
          perms.swap(a, b);
        }
      });
    int result = findDuplicateSorted(sorted);
    return result == -1 ? -1 : perms.get(result);
  }

  /** @return index of the leftmost duplicate or -1 if none*/
  public static int findDuplicateSorted(LongList sorted) {
    long prev = 0;
    for (int i = 0, m = sorted.size(); i < m; ++i) {
      long k = sorted.get(i);
      if (i > 0 && prev == k) return i;
      prev = k;
    }
    return -1;
  }

  public static long[] ensureCapacity(@Nullable long[] array, int capacity) {
    int length = array == null ? -1 : array.length;
    if (capacity < 0)
      throw new IllegalArgumentException();
    if (length >= capacity)
      return array;
    if (capacity == 0)
      return EMPTY_LONGS;
    return reallocArray(array, Math.max(16, Math.max(capacity, length * 2)));
  }

  public static long[] reallocArray(@Nullable long[] array, int length) {
    assert length >= 0 : length;
    int current = array == null ? -1 : array.length;
    if (current == length)
      return array;
    if (length == 0)
      return EMPTY_LONGS;
    long[] newArray = new long[length];
    int copy = Math.min(length, current);
    if (copy > 0)
      System.arraycopy(array, 0, newArray, 0, copy);
    return newArray;
  }

  public static int indexOf(long[] ints, int from, int to, long value) {
    for (int i = from; i < to; i++) {
      if (ints[i] == value)
        return i;
    }
    return -1;
  }

  public static long[] arrayCopy(long[] array, int offset, int length) {
    if (length == 0)
      return EMPTY_LONGS;
    long[] copy = new long[length];
    System.arraycopy(array, offset, copy, 0, length);
    return copy;
  }

  public static int compare(long a, long b) {
    return (a < b ? -1 : (a == b ? 0 : 1));
  }

  /**
   * Returns LongList adapter of the specified collection. If collection changes, the returned LongList changes also. <br>
   * Beware of unboxing: each call to the returned LongList leads to unboxing of the source collection element. If you will frequently access the returned LongList, it's a better idea to make a copy. See {@link LongArray#create(java.util.Collection)}
   */
  public static LongList asLongList(@Nullable final List<Long> coll) {
    if (coll == null || coll.isEmpty()) return LongList.EMPTY;
    return new AbstractLongList() {
      @Override
      public int size() {
        return coll.size();
      }

      @Override
      public long get(int index) throws NoSuchElementException {
        return coll.get(index);
      }
    };
  }

  /**
   * This algorithm supposes that the set to intersect with is usually shorter than the merged ones.
   * It also supposes that a and b have a great deal of common elements (this assumption is not very important, though).
   * */
  public static LongList uniteTwoLengthySortedSetsAndIntersectWithThirdShort(LongList a, LongList b, LongList intersectWith) {
    LongArray result = new LongArray(Math.min(intersectWith.size(), 16));
    int ia = 0;
    int sza = a.size();
    int ib = 0;
    int szb = b.size();
    long v;
    boolean add;
    for (LongIterator iiw : intersectWith) {
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

  public static LongList diffSortedLists(LongList a, LongList b) {
    int ia = 0;
    int sza = a.size();
    int ib = 0;
    int szb = b.size();
    LongArray diff = new LongArray();
    while (ia < sza || ib < szb) {
      if (ia >= sza) {
        for (; ib < szb; ++ib) diff.add(b.get(ib));
        break;
      }
      if (ib >= szb) {
        for (; ia < sza; ++ia) diff.add(a.get(ia));
        break;
      }
      long ea = a.get(ia);
      long eb = b.get(ib);
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

  public static void removeAllAtSorted(WritableLongList list, IntList indexes) {
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

  /**
   * @return SameValuesLongList with element {@code value}, repeated {@code count} times.
   * */
  public static SameValuesLongList repeat(long value, int count) {
    if (count < 0)
      throw new IllegalArgumentException();
    SameValuesLongList array = new SameValuesLongList(new IntLongMap(new IntArray(1), new LongArray(1)));
    array.insertMultiple(0, value, count);
    return array;
  }

  public static DynamicLongSet union(DynamicLongSet first, DynamicLongSet second) {
    LongArray a = first.toSortedLongArray();
    LongArray b = second.toSortedLongArray();

    a.unionWithSameLengthList(b);
    return DynamicLongSet.fromSortedList(a);
  }

  public static DynamicLongSet intersection(DynamicLongSet dest, DynamicLongSet src) {
    dest.retain(src);
    return dest;
  }

}