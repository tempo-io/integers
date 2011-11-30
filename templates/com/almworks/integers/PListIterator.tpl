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

public interface #E#ListIterator extends #E#Iterator {
  /**
   * Moves current position of the iterator relative to current position.
   * Positive value means move to greate indexes, negative - to smaller indexes. Zero doesn't change iterator state.
   */
  void move(int offset) throws ConcurrentModificationException, NoSuchElementException;

  /**
   * Returns the value at a position relative to the last returned item.
   * <p>
   * get(0) will return the same value as the last call to nextValue(), get(-1) will return previous value, get(1) will return
   * the next value to be returned by nextValue()
   */
  #e# get(int offset) throws ConcurrentModificationException, NoSuchElementException;

  /**
   * @return The current position of the iterator and the index of an element returned by {@link #value()}.
   * <br>{@code move(p)} would change index by {@code p}, {@link #next()} and {@link #nextValue()} would change it by 1.
   * @throws NoSuchElementException if iterator has never been advanced
   * ({@link #next()} or {@link #nextValue()} weren't ever called)
   */
  int index() throws NoSuchElementException;
}