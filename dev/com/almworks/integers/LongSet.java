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

public interface LongSet extends LongSizedIterable {
  LongSortedSet EMPTY = new LongEmptySet();

  /**
   * @return true if this set contains element {@code value}. Otherwise false
   */
  boolean contains(long value);

  /**
   * @return true if this set contains all of the elements produced by {@code iterable}.
   * Otherwise false
   */
  boolean containsAll(LongIterable iterable);

  /**
   * @return the number of elements in this set (its cardinality)
   */
  int size();

  /**
   * @return true if this set contains no elements
   */
  boolean isEmpty();

  /**
   * @return an array containing all the elements in this set.
   * The array contains no duplicates.
   * @see #toNativeArray(long[])
   * */
  LongArray toArray();

   /**
   * Writes values from this set (without duplicates) to dest.
   * @param dest destination array. Its length should be greater than
   * {@link #size()}{@code + destPos}
   * @param destPos starting position in the destination data.
   * @return dest
   * @exception  IndexOutOfBoundsException  if copying would cause
   *               access of data outside array bounds.
   */
  long[] toNativeArray(long[] dest, int destPos);

  /**
   * Writes values from this set to {@code dest}.
   * @see {@link #toNativeArray(long[], int)}
   */
  long[] toNativeArray(long[] dest);
}
