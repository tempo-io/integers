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
   * Moves current position of the iterator relative to current position. Positive value means move to greate indexes,
   * negative - to smaller indexes. Zero doesn't change iterator state.
   */
  void move(int offset) throws ConcurrentModificationException, NoSuchElementException;

  /**
   * Returns the value at a position relative to the last returned item.
   * <p>
   * get(0) will return the same value as the last call to next(), get(-1) will return previous value, get(1) will return
   * the next value to be returned by next()
   */
  #e# get(int offset) throws ConcurrentModificationException, NoSuchElementException;

  /**
   * @return current position of the iterator. The index of last returned element by call to {@link #next()} if iterator isn't moved.
   * @throws NoSuchElementException if iterator isn't ever advanced ({@link #next()} isn't ever called)
   */
  int lastIndex() throws NoSuchElementException;
}