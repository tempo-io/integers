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

public interface #E#Set extends #E#SizedIterable {
  #E#SortedSet EMPTY = new #E#EmptySet();

  /**
   * @return true if this set contains element {@code value}. Otherwise false
   */
  boolean contains(#e# value);

  /**
   * @return true if this set contains all of the elements produced by {@code iterable}.
   * Otherwise false
   */
  boolean containsAll(#E#Iterable iterable);

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
   * @see #toNativeArray(#e#[])
   * */
  #E#Array toArray();

   /**
   * Writes values from this set (without duplicates) to dest.
   * @param dest destination array. Its length should be greater than
   * {@link #size()}{@code + destPos}
   * @param destPos starting position in the destination data.
   * @return dest
   * @exception  IndexOutOfBoundsException  if copying would cause
   *               access of data outside array bounds.
   */
  #e#[] toNativeArray(#e#[] dest, int destPos);

  /**
   * Writes values from this set to {@code dest}.
   * @see {@link #toNativeArray(#e#[], int)}
   */
  #e#[] toNativeArray(#e#[] dest);

  /**
   * Compares the specified object with this set for equality.
   * Returns {@code true} if the given object also {@code #E#Set},
   * the two sets have the same size, and every member of the specified set is
   * contained in this set (or equivalently, every member of this set is
   * contained in the specified set). This definition ensures that the
   * equals method works properly across different implementations of the
   * {@link #E#Set} interface.
   *
   * @param o object to be compared for equality with this set
   * @return {@code true} if the specified set is equal to this set
   */
  boolean equals(Object o);

  /**
   * @return A hash code of elements stored in the set. The hash code is defined identically to
   * {@link java.util.Set#hashCode()} (sum of hash codes of elements within the set).
   * Because sum is commutative, this ensures that different order of elements in a set does not affect the hash code.
   * @see #E#Set#equals(Object)
   * @see IntegersUtils#hash(#e#)
   */
  int hashCode();
}