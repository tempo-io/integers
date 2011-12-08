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

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public interface Writable#E#ListIterator extends #E#ListIterator {

  /**
   * @throws IllegalStateException if remove() or removeRange() was previously called without subsequent advance.
   */
  #e# value() throws NoSuchElementException, IllegalStateException;

  /**
   * @throws IllegalStateException if remove() or removeRange() was previously called without subsequent advance.
   */
  void move(int offset) throws NoSuchElementException, IllegalStateException;

  /**
   * @throws IllegalStateException if remove() or removeRange() was previously called without subsequent advance.
   */
  #e# get(int offset) throws NoSuchElementException, IllegalStateException;

  /**
   * @throws IllegalStateException if remove() or removeRange() was previously called without subsequent advance.
   */
  int index() throws NoSuchElementException, IllegalStateException;

  /**
   * Will set the value in the list at a position relative to the current position.
   * <p>
   * set(0, X) will set the value at {@link #index()}, set(-1, X) will set the value at index()-1, etc
   * @throws IllegalStateException if remove() or removeRange() was previously called without subsequent advance.
   */
  void set(int offset, #e# value) throws NoSuchElementException, IllegalStateException;

  /**
   * Removes a number of items from the collection. Boundaries fromOffset and toOffset are relative to current
   * position, element at toOffset will not be removed (if there's any).
   * <p>After calling this method, subsequent calls to any methods except hasNext(), next(), and nextValue()
   * would throw IllegalStateException until iterator is advanced.
   * Example: removeRange(-1,2) will remove the current element, elements at index()-1 and at index()+1,
   * and the iterator will point at the element at index()+2 after it is advanced.
   * @throws IllegalStateException if remove() or removeRange() was previously called without subsequent advance.
   */
  void removeRange(int fromOffset, int toOffset) throws NoSuchElementException, IllegalStateException;

  /**
  * Removes an element at the current iterator position.
  * <p>After calling this method, subsequent calls to any methods except hasNext(), next(), and nextValue()
  * would throw IllegalStateException until iterator is advanced.
  * After calling this method and subsequent advance, iterator would point at element which is next to removed one.
  * @throws NoSuchElementException if iterator has never been advanced.
  * @throws IllegalStateException if remove() or removeRange() was previously called without subsequent advance.
  */
  void remove() throws NoSuchElementException, IllegalStateException;
}
