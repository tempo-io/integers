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

// CODE GENERATED FROM com/almworks/integers/WritablePList.tpl


package com.almworks.integers;

import com.almworks.integers.func.LongFunction;
import org.jetbrains.annotations.NotNull;

public interface WritableLongList extends LongList, LongCollector {
  @NotNull
  WritableLongListIterator iterator();

  @NotNull
  WritableLongListIterator iterator(int from);

  @NotNull
  WritableLongListIterator iterator(int from, int to);

  /**
   * Use this method in FOR-EACH statement if you want to iterate using WritableLongListIterator.
   * Example:<br>
   *   {@code for {WritableLongListIterator ii : myList.write()} ...}
   */
  @NotNull Iterable<WritableLongListIterator> write();

  void removeRange(int from, int to);

  long removeAt(int index);

  void removeAll(long value);

  void insertMultiple(int index, long value, int count);

  void insert(int index, long value);

  void insertAll(int index, LongIterator iterator);

  void insertAll(int index, LongList list);

  void insertAll(int index, LongList values, int sourceIndex, int count);

  boolean addSorted(long value);

  void set(int index, long value);

  void setRange(int from, int to, long value);

  void setAll(int index, LongList values);

  void setAll(int index, LongList values, int sourceIndex, int count);

  void apply(int from, int to, LongFunction function);

  void sort(WritableLongList... sortAlso);

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