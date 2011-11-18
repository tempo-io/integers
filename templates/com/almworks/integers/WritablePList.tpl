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

package com.almworks.integers;

import com.almworks.integers.func.#E#Function;
import org.jetbrains.annotations.NotNull;

public interface Writable#E#List extends #E#List, #E#Collector {
  @NotNull
  Writable#E#ListIterator iterator();

  @NotNull
  Writable#E#ListIterator iterator(int from);

  @NotNull
  Writable#E#ListIterator iterator(int from, int to);

  void removeRange(int from, int to);

  #e# removeAt(int index);

  void removeAll(#e# value);

  void insertMultiple(int index, #e# value, int count);

  void insert(int index, #e# value);

  void insertAll(int index, #E#Iterator iterator);

  void insertAll(int index, #E#List list);

  void insertAll(int index, #E#List values, int sourceIndex, int count);

  boolean addSorted(#e# value);

  void set(int index, #e# value);

  void setRange(int from, int to, #e# value);

  void setAll(int index, #E#List values);

  void setAll(int index, #E#List values, int sourceIndex, int count);

  void apply(int from, int to, #E#Function function);

  void sort(Writable#E#List... sortAlso);

  void swap(int index1, int index2);

  /**
   * This method reverses the order in which elements appear in the list, striving to use
   * a mimimum of additional memory while reversing.
   * Note that, depending on implementation of a list, an amount of memory used by a list might
   * differ from an amount used by its reversion.
   */
  void reverseInPlace();

  /**
   * Assumes that list is sorted.
   */
  void removeDuplicates();

  /**
   * Increasing list's size and shifts all values to the right of index. The resulting "hole"
   * in the range [index; index + count) contains undefined values.
   *
   * @param index where to insert "hole"
   * @param count how much size increase is needed, must be >= 0
   */
  void expand(int index, int count);

  void clear();
}