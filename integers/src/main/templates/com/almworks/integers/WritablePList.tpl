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

import com.almworks.integers.func.#E#To#E#;
import org.jetbrains.annotations.NotNull;

public interface Writable#E#List extends #E#List, #E#Collector {
  @NotNull
  Writable#E#ListIterator iterator();

  @NotNull
  Writable#E#ListIterator iterator(int from);

  @NotNull
  Writable#E#ListIterator iterator(int from, int to);

  /**
   * Use this method in FOR-EACH statement if you want to iterate using Writable#E#ListIterator.
   * Example:<br>
   *   {@code for {Writable#E#ListIterator ii : myList.write()} ...}
   */
  @NotNull Iterable<Writable#E#ListIterator> write();

  void removeRange(int from, int to);

  #e# removeAt(int index);

  /**
   * Removes the first occurrence of the specified element from this list, if it is present.
   * @return true if this list was modified, otherwise false
   */
  boolean remove(#e# value);

  /**
   * This method removes from this list all values that are equal to {@code value}
   * <br> {@link #removeAllSorted(#e#)} may be more effective than this method
   * @return true if this list was modified, otherwise false
   * @see #removeAllSorted(#e#)
   */
  boolean removeAll(#e# value);

  /**
   * Removes all appearances of {@code value} from this sorted list.
   * <br>May be more effective than {@link #removeAll(#e#)}.
   * <br>Invoking this method on an unsorted array produces unspecified results.
   * @return true if this array was modified, otherwise false
   */
  boolean removeAllSorted(#e# value);

  /**
   * Removes from this list all values contained in {@code iterable}.
   * @return true if this list was modified, otherwise false
   */
  boolean removeAll(#E#Iterable iterable);

  /**
   * Removes from this list all values contained in the specified array
   * @return true if this list was modified, otherwise false
   */
  boolean removeAll(#e#... values);

  /**
   * Insert {@code value} {@code count} times between indices {@code index, index + 1}
   * @throws IllegalArgumentException if count < 0
   * */
  void insertMultiple(int index, #e# value, int count);

  void insert(int index, #e# value);

  /**
   * Insert values from {@code iterator} between indices {@code index, index + 1}
   */
  void insertAll(int index, #E#Iterator iterator);

  /**
   * Insert values from {@code list} between indices {@code index, index + 1}
   */
  void insertAll(int index, #E#List list);

  void insertAll(int index, #E#List values, int sourceIndex, int count);

  /**
   * Inserts {@code value} in this sorted list if this list doesn't contain {@code value}, keeping sorted order.
   * @return true if this list was modified, otherwise false
   */
  boolean addSorted(#e# value);

  void set(int index, #e# value);

  void setRange(int from, int to, #e# value);

  /**
   * Replaces the elements in this list from {@code index} to {@code index + values.size()}
   * with the elements from {@code values}
   * @throws ArrayIndexOutOfBoundsException if {@code index + values.size > size()};
   */
  void setAll(int index, #E#List values);

  /**
   * Replaces the elements in this list from {@code index} to {@code index + count}
   * with the elements from {@code values} beginning from index {@code sourceIndex}
   * and ending with {@code sourceIndex + count}
   * @throws ArrayIndexOutOfBoundsException if
   * {@code index + count > size()} or {@code sourceIndex + count > values.size()};
   */
  void setAll(int index, #E#List values, int sourceIndex, int count);

  /**
   * Updates the values in this list at the specified interval with result of applying
   * {@code function} to elements at this interval.
   * @param from starting index, inclusive
   * @param to ending index, exclusive
   */
  void apply(int from, int to, #E#To#E# function);

  /**
   * Sorts this list. Permutation of indices in this array
   * will be reflected in the same permutation of indices in each array of {@code sortAlso}.
   * Stability is not guaranteed.
   * @param sortAlso lists in which the order is changed as well as this list
   */
  void sort(Writable#E#List... sortAlso);

  /**
   * Sorts this list, removes duplicates.
   */
  void sortUnique();

  void swap(int index1, int index2);

  /**
   * This method reverses the order in which elements appear in the list, striving to use
   * mimimum of additional memory while reversing.
   * <p>Most implementations will occupy the same memory after calling this method.
   * However, certain optimized implementations may end up occupying more memory after calling this method.
   */
  void reverse();

  /**
   * Assumes that list is sorted.
   */
  void removeDuplicates();

  /**
   * Increases the list size and shifts all values to the right of {@code index}. The resulting "hole"
   * in the range {@code [index; index + count)} contains undefined values.
   *
   * @param index where to insert the "hole"
   * @param count how much size increase is needed, must be >= 0
   *
   * @throws IndexOutOfBoundsException when index < 0 or index > size
   * @throws IllegalArgumentException when count < 0
   */
  void expand(int index, int count);

  void clear();
}