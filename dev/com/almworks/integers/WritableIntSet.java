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

// CODE GENERATED FROM com/almworks/integers/WritablePSet.tpl


package com.almworks.integers;

public interface WritableIntSet extends IntCollector, IntSet {

  /**
   * Removes all of the elements from this set.
   * The set will be empty after this call returns.
   */
  void clear();

  /**
   * Adds the specified element to this set if it is not already present.
   * Returns {@code true} if the element was added, otherwise {@code false}.
   *
   * @return {@code false} if this set already contains the element.
   * Otherwise {@code true}.
   * If return value is unused, better use {@link WritableIntSet#add(int)}
   * */
  boolean include(int value);

  /**
   * Removes the specified element from this set.
   * Returns {@code true} if the element was removed, otherwise {@code false}.
   *
   * @return {@code true} if this set contained {@code value}. Otherwise {@code false}.
   * If return value is unused, better use {@link WritableIntSet#remove(int)}
   * */
  boolean exclude(int value);

  /**
  * Removes the specified element from this set.
  * */
  void remove(int value);

  /**
   * Removes from this set all elements that are contained in {@code values}
   */
  void removeAll(int ... values);

  /**
   * Removes from this set all elements that are contained in the {@code iterable}
   */
  void removeAll(IntIterable iterable);

  /**
   * Removes from this set all of its elements that are not contained in the specified list.
   * @see #removeAll(IntIterable)
   * @see #containsAll(IntIterable)
   * */
  void retain(IntList values);
}
