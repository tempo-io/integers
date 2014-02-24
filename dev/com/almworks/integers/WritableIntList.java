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

import com.almworks.integers.func.IntToInt;
import org.jetbrains.annotations.NotNull;

public interface WritableIntList extends IntList, IntCollector {
  @NotNull
  WritableIntListIterator iterator();

  @NotNull
  WritableIntListIterator iterator(int from);

  @NotNull
  WritableIntListIterator iterator(int from, int to);

  /**
   * Use this method in FOR-EACH statement if you want to iterate using WritableIntListIterator.
   * Example:<br>
   *   {@code for {WritableIntListIterator ii : myList.write()} ...}
   */
  @NotNull Iterable<WritableIntListIterator> write();

  void removeRange(int from, int to);

  int removeAt(int index);

  void removeAll(int value);

  void insertMultiple(int index, int value, int count);

  void insert(int index, int value);

  void insertAll(int index, IntIterator iterator);

  void insertAll(int index, IntList list);

  void insertAll(int index, IntList values, int sourceIndex, int count);

  boolean addSorted(int value);

  void set(int index, int value);

  void setRange(int from, int to, int value);

  void setAll(int index, IntList values);

  void setAll(int index, IntList values, int sourceIndex, int count);

  void apply(int from, int to, IntToInt function);

  void sort(WritableIntList... sortAlso);

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
   * Increasing list's size and shifts all values to the right of index. The resulting "hole"
   * in the range [index; index + count) contains undefined values.
   *
   * @param index where to insert "hole"
   * @param count how much size increase is needed, must be >= 0
   */
  void expand(int index, int count);

  void clear();
}