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

// GENERATED CODE!!!
package com.almworks.integers;

import static com.almworks.integers.IntegersUtils.*;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class #E#Collections {
  public static #e#[] toNativeArray(#E#Iterable iterable) {
    if (iterable instanceof #E#List) return ((#E#List) iterable).toNativeArray();
    return toNativeArray(iterable.iterator());
  }

  public static #e#[] toNativeArray(#E#Iterator it) {
    if (!it.hasNext()) return EMPTY_#EC#S;
    #E#Array array = new #E#Array();
    array.addAll(it);
    return array.toNativeArray();
  }

  public static #e#[] toSortedNativeArray(#E#Iterable iterable) {
    #e#[] array = toNativeArray(iterable);
    Arrays.sort(array);
    return array;
  }

  public static #E#List toSortedUnique(#E#Iterable values) {
    return toSorted(true, values);
  }

  public static #E#List toSorted(boolean unique, #E#Iterable values) {
    if (values instanceof #E#List) {
      #E#List list = (#E#List) values;
      if ((unique && list.isUniqueSorted()) || (!unique && list.isSorted())) return list;
    }
    #e#[] array = toNativeArray(values.iterator());
    if (array.length == 0) return #E#List.EMPTY;
    Arrays.sort(array);
    int length = unique ? removeSubsequentDuplicates(array, 0, array.length) : array.length;
    return new #E#Array(array, length);
  }

  public static #E#List toSortedUnique(#e#[] values) {
    int res = isSortedUnique(true, values, 0, values.length);
    if (res == 0) return new #E#Array(values);
    if (res < 0) {
      #e#[] copy = new #e#[-res];
      int i = 1;
      #e# prev = values[0];
      for (int j = 1; j < values.length; j++) {
        #e# value = values[j];
        if (value > prev) {
          copy[i] = value;
          i++;
          prev = value;
        }
      }
      assert i == copy.length;
      return new #E#Array(copy);
    }
    #E#Array copy = #E#Array.copy(values);
    copy.sortUnique();
    return copy;
  }

  public static boolean isSorted(@Nullable #e#[] ints) {
    return ints == null || isSorted(ints, 0, ints.length);
  }

  /**
   * @return true if arrays is sorted in ascending order
   */
  public static boolean isSorted(#e#[] array, int offset, int length) {
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
  public static int isSortedUnique(boolean checkNotUnique, @Nullable #e#[] ints, int offset, int length) {
    if (ints == null) ints = EMPTY_#EC#S;
    if (length < 0 || offset < 0) throw new IllegalArgumentException(offset + " " + length);
    if (ints.length < offset + length) throw new ArrayIndexOutOfBoundsException(offset + "+" + length + ">" + ints.length);
    if (length < 2) return 0;
    int dupCount = 0;
    #e# prev = ints[offset];
    for (int i = 1; i < length; i++) {
      #e# next = ints[i + offset];
      if (next < prev) return i;
      else if (next == prev) {
        if (!checkNotUnique) return i;
        dupCount++;
      }
      prev = next;
    }
    return -dupCount;
  }

  public static void swap(#e#[] x, int a, int b) {
    #e# t = x[a];
    x[a] = x[b];
    x[b] = t;
  }

  public static int binarySearch(#e# val, #e#[] array) {
    return binarySearch(val, array, 0, array.length);
  }

  /**
   * Copied from Arrays.
   *
   * @param from index to start search, inclusive
   * @param to   ending index, exclusive
   */
  public static int binarySearch(#e# key, #e#[] a, int from, int to) {
    int low = from;
    int high = to - 1;

    while (low <= high) {
      int mid = (low + high) >> 1;
      #e# midVal = a[mid];

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
  public static int removeSubsequentDuplicates(#e#[] array, int offset, int length) {
    if (length < 2)
      return length;
    int index = offset;
    length += offset;
    while (index < length - 1) {
      #e# prev = array[index];
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

  public static #e#[] ensureCapacity(@Nullable #e#[] array, int capacity) {
    int length = array == null ? -1 : array.length;
    if (length >= capacity)
      return array;
    if (capacity == 0)
      return EMPTY_#EC#S;
    return reallocArray(array, Math.max(16, Math.max(capacity, length * 2)));
  }

  public static #e#[] reallocArray(@Nullable #e#[] array, int length) {
    assert length >= 0 : length;
    int current = array == null ? -1 : array.length;
    if (current == length)
      return array;
    if (length == 0)
      return EMPTY_#EC#S;
    #e#[] newArray = new #e#[length];
    int copy = Math.min(length, current);
    if (copy > 0)
      System.arraycopy(array, 0, newArray, 0, copy);
    return newArray;
  }

  public static int indexOf(#e#[] ints, int from, int to, #e# value) {
    for (int i = from; i < to; i++) {
      if (ints[i] == value)
        return i;
    }
    return -1;
  }

  public static #e#[] arrayCopy(#e#[] array, int offset, int length) {
    if (length == 0)
      return EMPTY_#EC#S;
    #e#[] copy = new #e#[length];
    System.arraycopy(array, offset, copy, 0, length);
    return copy;
  }

  public static int compare(#e# a, #e# b) {
    return (a < b ? -1 : (a == b ? 0 : 1));
  }
}
