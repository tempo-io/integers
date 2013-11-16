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

// CODE GENERATED FROM com/almworks/integers/PList.tpl


package com.almworks.integers;

import static com.almworks.integers.IntegersUtils.*;

import com.almworks.integers.util.LongSizedIterable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.NoSuchElementException;

public interface LongList extends LongSizedIterable {
  LongList EMPTY = new LongArray(EMPTY_LONGS);

  /**
   * Size of the collection. May not be efficient (up to O(N)).
   * @return the number of values in the collection
   */
  int size();

  /**
   * Checks whether collection is empty. Must be efficient.
   * @return true if there are no items in the collection.
   */
  boolean isEmpty();

  /**
   * Checks whether collection contains specified value
   * @param value value to check
   * @return true if collection contains value
   */
  boolean contains(long value);

  /**
   * Creates new native array and stores values there.
   * <p>
   * Note: effectively written code should avoid use this method.
   */
  long[] toNativeArray();

  /**
  * @return the item at index. Valid value of an index is in range [0, size()).
  * Note: May not be efficient (up to O(N)). Efficient algorithm should use iterator to iterate collection.
  */
  long get(int index) throws NoSuchElementException;

  /**
  * @return index of first occurence of value. If not found returns negative value
  */
  int indexOf(long value);

  /**
   * Writes values to dest.
   * @return dest
   */
  long[] toNativeArray(int startIndex, long[] dest, int destOffset, int length);

  /**
   * Returns a sub-list, backed by this list. If the list changes, changes
   * will be reflected in the subList.
   * <p>
   * NB: If values are removed from the parent list,
   * this may result in OOBE when accessing sublist.
   *
   * @param from starting index, inclusive
   * @param to ending index, exclusive
   */
  LongList subList(int from, int to);

  /**
  * Performs binary search. This method assumes that list is sorted. If not sorted the result is unpredictable.
  * @return index of occurence or insertion point -index-1 if not found
  */
  int binarySearch(long value);

  /**
  * Performs binary search on list slice. This method assumes that list slice is sorted. If not sorted the result is unpredictable.
  * @return index of occurence or insertion point -index-1 if not found
  */
  int binarySearch(long value, int from, int to);

  /**
  * @return true if list is sorted in smallest first order
  */
  boolean isSorted();

  /**
  * @return true if list is sorted in smallest first order and all elements are unique
  */
  boolean isUniqueSorted();

  /**
  * @return iterator initially located before first element. Iterator will walk the whole list
  */
  @NotNull
  LongListIterator iterator();

  /**
  * @return iterator initially located before element at index from. Iterator will walk till last element
  */
  @NotNull
  LongListIterator iterator(int from);

  /**
  * @return iterator initially located before element at index from and iterator will walk up to index to (exclusive)
  */
  @NotNull
  LongListIterator iterator(int from, int to);

  /**
   * For a given index i, returns minimum index j, for which exactly one of the following holds:
   * <ul>
   * <li>{@code j = -1 and for all k > i, a[k] = a[i]};</li>
   * <li>{@code j > i and a[i] != a[j]},</li>
   * </ul>
   * where {@code a} represents the list.
   * @return Index of the next different value or {@link #size()} if all values starting from the specified index are the same.
   */
  int getNextDifferentValueIndex(int curIndex);

  /**
  * @return List filled with object wrappers
  */
  List<Long> toList();

  class Single extends AbstractLongList {
    private long myValue;

    public Single(long value) {
      myValue = value;
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public long get(int index) throws NoSuchElementException {
      return myValue;
    }
  }
}