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
   * Changes the current position of the iterator.
   * @param offset the difference between new and old iterator indexes.
   * Zero won't change iterator state. Positive value will move it to greater indexes, negative - to smaller indexes.
   */
  void move(int offset) throws ConcurrentModificationException, NoSuchElementException;

  /**
   * Returns the value at a position relative to the current position.
   * <p>
   * get(0) will return the same value as value(), get(-1) will return value at index()-1, etc.
   */
  #e# get(int offset) throws ConcurrentModificationException, NoSuchElementException;

  /**
   * @return The current position of the iterator.
   * <br>{@code move(p)} would change index by {@code p}, {@link #next()} and {@link #nextValue()} would change it by 1.
   * @throws NoSuchElementException if iterator has never been advanced
   * ({@link #next()} or {@link #nextValue()} weren't ever called)
   */
  int index() throws NoSuchElementException;
}