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

// CODE GENERATED FROM com/almworks/integers/WritablePListIterator.tpl




package com.almworks.integers;

import java.util.NoSuchElementException;

public interface WritableIntListIterator extends IntListIterator {

  /**
   * {@inheritDoc}
   * @throws IllegalStateException if {@link #remove} or {@link #removeRange} was previously called without subsequent advance.
   */
  int value() throws NoSuchElementException, IllegalStateException;

  /**
   * @return {@code false} if this iterator has never been advanced or the current value has been removed via 
   * {@link #remove()} or {@link #removeRange(int, int)}.
   * In other words, returns {@code false} if the subsequent call to {@link #value()} will throw
   * NoSuchElementException or IllegalStateException, otherwise {@code true}.
   * @see #value()
   */
  @Override
  boolean hasValue();

  /**
   * @throws IllegalStateException if {@link #remove} or {@link #removeRange} was previously called without subsequent advance.
   */
  void move(int offset) throws NoSuchElementException, IllegalStateException;

  /**
   * @throws IllegalStateException if {@link #remove} or {@link #removeRange} was previously called without subsequent advance.
   */
  int get(int offset) throws NoSuchElementException, IllegalStateException;

  /**
   * @throws IllegalStateException if {@link #remove} or {@link #removeRange} was previously called without subsequent advance.
   */
  int index() throws NoSuchElementException, IllegalStateException;

  /**
   * Will set the value in the list at a position relative to the current position.
   * <p>
   * set(0, X) will set the value at {@link #index()}, set(-1, X) will set the value at index()-1, etc
   * @throws IllegalStateException if {@link #remove} or {@link #removeRange} was previously called without subsequent advance.
   */
  void set(int offset, int value) throws NoSuchElementException, IllegalStateException;

  /**
   * Removes a number of items from the collection. Boundaries fromOffset and toOffset are relative to current
   * position, element at toOffset will not be removed (if there's any).
   * <p>After calling this method, subsequent calls to any methods except hasValue(), hasNext(), next(), and nextValue()
   * would throw IllegalStateException until iterator is advanced.
   * Example: removeRange(-1,2) will remove the current element, elements at index()-1 and at index()+1,
   * and the iterator will point at the element at index()+2 after it is advanced.
   * @throws IllegalStateException if remove() or removeRange() was previously called without subsequent advance.
   */
  void removeRange(int fromOffset, int toOffset) throws NoSuchElementException, IllegalStateException;

  /**
  * Removes an element at the current iterator position.
  * <p>After calling this method, subsequent calls to any methods except hasValue(), hasNext(), next(), and nextValue()
  * would throw IllegalStateException until iterator is advanced.
  * After calling this method and subsequent advance, iterator would point at element which is next to removed one.
  * @throws NoSuchElementException if iterator has never been advanced.
  * @throws IllegalStateException if remove() or removeRange() was previously called without subsequent advance.
  */
  void remove() throws NoSuchElementException, IllegalStateException;
}
