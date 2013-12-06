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
import com.almworks.integers.optimized.CyclicLongQueue;
import com.almworks.integers.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntegersUtils.EMPTY_LONGS;

public class LongCollections {
  public static long[] toNativeArray(LongIterable iterable) {
    if (iterable instanceof LongList) return ((LongList) iterable).toNativeArray();
    if (iterable instanceof LongSizedIterable) {
      LongSizedIterable sized = (LongSizedIterable)iterable;
      int size = sized.size();
      long[] res = LongCollections.collectIterables(size, sized).extractHostArray();
      assert res.length == sized.size();
      return res;
    }
    return collectIterables(iterable).toNativeArray();
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
    LongArray buf = collectIterables(values);
    int bufSize = buf.size();
    if (bufSize == 0) return LongList.EMPTY;
    long[] array = buf.extractHostArray();
    Arrays.sort(array, 0, bufSize);
    int length = unique ? removeSubsequentDuplicates(array, 0, bufSize) : bufSize;
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

  // todo optimize - check borders
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
      int mid = (low + high) >>> 1;
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

  /**
   * @param array
   * @param capacity
   * @return {@code array} if {@code capacity <= array.length} otherwise
   * new array that contains all values of array and has length equal to the
   * maximum of {@code 16}, {@code capacity} and {@code (array.length * 2)}
   */
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

  /**
   * @return {@code array} if {@code array.length} equals {@code length},
   * otherwise its copy(or subarray) whose length equals {@code length}.
   */
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

  /**
   * Returns the index of the first occurrence of the {@code value}
   * in the specified array on the specified interval, or -1 if this array does not contain the element.
   * @param from starting index, inclusive
   * @param to ending index, exclusive
   * */
  public static int indexOf(long[] array, int from, int to, long value) {
    for (int i = from; i < to; i++) {
      if (array[i] == value)
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
   * Beware of unboxing: each call to the returned LongList leads to unboxing of the source collection element.
   * If you will frequently access the returned LongList, it's a better idea to make a copy. See {@link LongArray#create(java.util.Collection)}
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

  /**
   * @param a sorted {@code LongList}
   * @param b sorted {@code LongList}
   * @return union of {@code a} and {@code b} without elements that are contained both in {@code a} and {@code b}
   */
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

  /**
   * Removes from the specified list all elements whose index is contained in the specified {@code IntList indexes}
   * @param indexes sorted {@code IntList}
   */
  public static void removeAllAtSorted(WritableLongList list, IntList indexes) {
    assert indexes.isSorted() : "Indexes are not sorted: " + indexes;
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
   * @return {@code LongList} with element {@code value} repeated {@code count} times.
   * */
  public static LongList repeat(final long value, final int count) {
    if (count < 0)
      throw new IllegalArgumentException();
    if (count == 0) return LongList.EMPTY;

    return new AbstractLongList() {
      @Override
      public int size() {
        return count;
      }

      @Override
      public long get(int index) throws NoSuchElementException {
        return value;
      }
    };
  }

  /**
   * @return union of the two sets
   */
  public static LongTreeSet union(LongSet first, LongSet second) {
    LongArray[] arrays = {first.toArray(), second.toArray()};
    if (!(first instanceof LongSortedSet)) arrays[0].sortUnique();
    if (!(second instanceof LongSortedSet)) arrays[1].sortUnique();
    int dest = arrays[0].size() <= arrays[1].size() ? 1 : 0;
    arrays[dest].merge(arrays[1 - dest]);
    return LongTreeSet.createFromSortedUnique(arrays[dest]);
  }

  /**
   * @return intersection of the two sets
   */
  public static LongTreeSet intersection(LongSet first, LongSet second) {
    LongArray[] arrays = {first.toArray(), second.toArray()};
    if (!(first instanceof LongSortedSet)) arrays[0].sort();
    if (!(second instanceof LongSortedSet)) arrays[1].sort();

    int dest = arrays[0].size() <= arrays[1].size() ? 1 : 0;
    arrays[dest].retainSorted(arrays[1 - dest]);
    return LongTreeSet.createFromSortedUnique(arrays[dest]);
  }

  /**
   * @return union of the specified lists
   */
  public static LongList union(LongList... lists) {
    if (lists == null) return null;
    LongList union = null;
    LongSetBuilder builder = null;
    for (LongList list : lists) {
      if (list != null && !list.isEmpty()) {
        if (union == null) {
          union = list;
        } else {
          if (builder == null) {
            builder = new LongSetBuilder();
            builder.addAll(union);
          }
          builder.addAll(list);
        }
      }
    }
    if (builder != null) union = builder.commitToArray();
    if (union == null) union = LongList.EMPTY;
    return union;
  }

  /**
   * @param includeSorted sorted {@code LongList}
   * @param excludeSorted sorted {@code LongList}
   * @return LongList that contains elements from {@code includeSorted}
   * with the exception of those elements that are contained in {@code excludeSorted}.
   */
  @NotNull
  public static LongList complementSorted(@Nullable LongList includeSorted, @Nullable LongList excludeSorted) {
    if (includeSorted == null || includeSorted.isEmpty()) return LongList.EMPTY;
    if (excludeSorted == null || excludeSorted.isEmpty()) return includeSorted;
    LongMinusIterator complement = new LongMinusIterator(includeSorted.iterator(), excludeSorted.iterator());
    return complement.hasNext() ? collectIterables(includeSorted.size(), complement) : LongList.EMPTY;
  }


  /**
   * @return union of the specified lists
   * @param aSorted sorted {@code LongList}
   * @param bSorted sorted {@code LongList}
   */
  @NotNull
  public static LongList unionSorted(@Nullable LongList aSorted, @Nullable LongList bSorted) {
    if (aSorted == null || aSorted.isEmpty()) return bSorted == null ? LongList.EMPTY : bSorted;
    if (bSorted == null || bSorted.isEmpty()) return aSorted;
    LongArray array;
    if (aSorted.size() < bSorted.size()) {
      array = new LongArray(bSorted);
      array.merge(aSorted);
    } else {
      array = new LongArray(aSorted);
      array.merge(bSorted);
    }
    return array;
  }

  /**
   * @return intersection of the specified lists
   * @param aSorted sorted {@code LongList}
   * @param bSorted sorted {@code LongList}
   */
  @NotNull
  public static LongList intersectionSorted(@Nullable LongList aSorted, @Nullable LongList bSorted) {
    if (aSorted == null || aSorted.isEmpty() || bSorted == null || bSorted.isEmpty()) return LongList.EMPTY;
    LongIterator intersection = new LongIntersectionIterator(aSorted.iterator(), bSorted.iterator());
    return intersection.hasNext() ? collectIterables(Math.max(aSorted.size(), bSorted.size()), intersection) : LongList.EMPTY;
  }

  /**
   * @return true if intersection of the specified lists is not empty otherwise false
   * @param aSorted sorted {@code LongList}
   * @param bSorted sorted {@code LongList}
   */
  public static boolean hasIntersection(@Nullable LongList aSorted, @Nullable LongList bSorted) {
    if (aSorted == null || aSorted.isEmpty() || bSorted == null || bSorted.isEmpty()) return false;
    LongIterator intersection = new LongIntersectionIterator(aSorted.iterator(), bSorted.iterator());
    return intersection.hasNext();
  }

  /**
   * @return true if union of the specified lists is not empty otherwise false
   * @param aSorted sorted {@code LongList}
   * @param bSorted sorted {@code LongList}
   */
  public static boolean hasUnion(@Nullable LongList aSorted, @Nullable LongList bSorted) {
    return aSorted != null && !aSorted.isEmpty() || bSorted != null && !bSorted.isEmpty();
  }

  /**
   * @return true if complement of the specified lists is not empty otherwise false
   * @param includeSorted sorted {@code LongList}
   * @param excludeSorted sorted {@code LongList}
   */
  public static boolean hasComplement(@Nullable LongList includeSorted, @Nullable LongList excludeSorted) {
    if (includeSorted == null || includeSorted.isEmpty()) return false;
    if (excludeSorted == null || excludeSorted.isEmpty()) return !includeSorted.isEmpty();
    LongMinusIterator complement = new LongMinusIterator(includeSorted.iterator(), excludeSorted.iterator());
    return complement.hasNext();
  }

  /**
   * @return list with elements from the specified list in the sorted order without duplicates
   */
  @NotNull
  public static LongList toSortedUnique(@Nullable LongList list) {
    if (list == null || list.isEmpty()) return LongList.EMPTY;
    if (list.size() < 33 && list.isUniqueSorted()) return list; // check only small lists
    LongArray r = new LongArray(list);
    r.sortUnique();
    return r;
  }

  /**
   * Sorts lists comparing corresponding pairs of the specified arrays.
   * @param secondary ties in this array are broken via elements of this array. Must not be shorter than {@code this list}
   * @throws IllegalArgumentException in case the second array is shorter than the first
   * */
  public static void sortPairs(final WritableLongList primary, final WritableLongList secondary) throws IllegalArgumentException {
    if (primary.size() > secondary.size()) throw new IllegalArgumentException("This array is longer than sortAlso: " +
        primary.size() + " > " + secondary.size());
    IntegersUtils.quicksort(primary.size(), new IntFunction2() {
          @Override
          public int invoke(int i, int j) {
            int comp = LongCollections.compare(primary.get(i), primary.get(j));
            if (comp == 0) comp = LongCollections.compare(secondary.get(i), secondary.get(j));
            return comp;
          }
        },
        new IntProcedure2() {
          @Override
          public void invoke(int i, int j) {
            primary.swap(i, j);
            secondary.swap(i, j);
          }
        });
  }

  /**
   * @return array with elements from the {@code iterables}.
   * @see #collectIterables(int, LongIterable...)
   */
  public static LongArray collectIterables(LongIterable ... iterables) {
    return collectIterables(0, iterables);
  }

  /**
   * @param capacity initial capacity for array
   * @return array with elements from the {@code iterables}.
   */
  public static LongArray collectIterables(int capacity, LongIterable ... iterables) {
    LongArray res = new LongArray(capacity);
    for (LongIterable iterable: iterables) {
      if (iterable instanceof LongList) {
        res.addAll((LongList) iterable);
      } else {
        if (iterable instanceof LongSizedIterable) {
          res.ensureCapacity(res.size() + ((LongSizedIterable) iterable).size());
        }
        res.addAll(iterable.iterator());
      }
    }
    return res;
  }

  /**
   * Appends to {@code sb} the string representation of {@code iterable}.
   * String representation - elements from {@code iterable} separated by comma and wrapped in parentheses: "(a, b, c)".
   * If {@code sb == null} creats new StringBuilder.
   * @return {@code sb} with appended string representation of iterable
   */
  @NotNull
  public static StringBuilder append(@Nullable StringBuilder sb, @Nullable LongIterable iterable) {
    if (sb == null) sb = new StringBuilder();
    if (iterable == null) {
      sb.append("null");
    } else {
      LongIterator it = iterable.iterator();
      if (!it.hasNext()) {
        sb.append("()");
      } else {
        sb.append("(").append(it.nextValue());
        while (it.hasNext()) {
          sb.append(", ").append(it.nextValue());
        }
        sb.append(")");
      }
    }
    return sb;
  }

  public static String toBoundedString(LongIterable iterable) {
    return toBoundedString(iterable, 10);
  }

  /**
   * Returns the string representation of {@code iterable}, bounded by {@code 2 * lim} elements.
   * If size of {@code iterable} is less than {@code 2 * lim}, the result string
   * contains all elements from {@code iterable} separated by comma and wrapped in parentheses: "(a, b, c, d)".<br>
   * Otherwise result contains size of {@code iterable}, the first {@code lim} elements of {@code iterable} and
   * the last {@code lim} elements of {@code iterable}.
   * Example: "[size] (a, b, c, ..., d, e, f)"
   * if {@code lim == 3} and size of {@code iterable} is more than 6.
   *
   * @return the string representation of {@code iterable}, bounded by {@code 2 * lim} elements.
   */
  public static String toBoundedString(LongIterable iterable, int lim) {
    if (iterable instanceof LongSizedIterable) {
      LongSizedIterable sizedIterable = (LongSizedIterable)iterable;
      int size = sizedIterable.size();
      if (size > lim * 2) {
        LongIterator lastElemsIt = sizedIterable.iterator();
        if (lastElemsIt instanceof LongListIterator) {
          ((LongListIterator) lastElemsIt).move(size - lim);
        } else {
          for (int i = 0; i < size - lim; i++) {
            lastElemsIt.next();
          }
        }
        return toShortString(size, lim, iterable.iterator(), lastElemsIt);
      } else {
        return append(null, sizedIterable).toString();
      }
    }
    return outputIterator(iterable.iterator(), lim);
  }

  private static String outputIterator(LongIterator it, int lim) {
    LongArray headValues = new LongArray(lim);
    headValues.addAllNotMore(it, lim);
    int itSize = headValues.size();

    CyclicLongQueue tailValues = new CyclicLongQueue(lim);
    for ( int lim2 = lim * 2; it.hasNext() && itSize < lim2; itSize++) {
      tailValues.add(it.nextValue());
    }

    if (!it.hasNext()) {
      return append(null, LongIterators.concat(headValues, tailValues)).toString();
    } else {
      // there are more than 2 * lim elements in the iterator
      for ( ; it.hasNext(); itSize++) {
        tailValues.removeFirst();
        tailValues.add(it.nextValue());
      }
      return toShortString(itSize, lim, headValues.iterator(), tailValues.iterator());
    }
  }

  private static String toShortString(int size, int lim, LongIterator headIt, LongIterator tailIt) {
    assert size > 2 * lim : size + " " + lim;
    StringBuilder sb = new StringBuilder("[").append(size).append("] (");
    sb.append(headIt.nextValue());
    for (int i = 1; i < lim; i++) {
      sb.append(", ").append(headIt.nextValue());
    }
    sb.append(", ...");
    for (int i = 0; i < lim; i++) {
      sb.append(", ").append(tailIt.nextValue());
    }
    return sb.append(")").toString();
  }
}