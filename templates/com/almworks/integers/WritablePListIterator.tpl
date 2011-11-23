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
   * Will set the value in the list at a position relative to the last returned item.
   * <p>
   * set(0, X) will set the value at the place of the last call to nextValue(), get(-1) will set previous value, etc
   */
  void set(int offset, #e# value) throws NoSuchElementException;

  /**
   * Removes backCount last items from the collection. Before this method is called, backCount calls to nextValue() must
   * have passed.
   */
  void removeRange(int fromOffset, int toOffset) throws NoSuchElementException;

  /**
  * Removes element at current iterator position.
  * @throws NoSuchElementException if iterator wasn't ever advanced or moved outside of range.
  */
  void remove() throws NoSuchElementException, ConcurrentModificationException;
}
