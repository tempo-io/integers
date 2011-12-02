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
   * Will set the value in the list at a position relative to the current position.
   * <p>
   * set(0, X) will set the value at {@link #index()}, set(-1, X) will set the value at index()-1, etc
   */
  void set(int offset, #e# value) throws NoSuchElementException;

  /**
   * Removes a number of items from the collection. Boundaries fromOffset and toOffset are relative to current
   * position, element at toOffset will not be removed (if there's any). <p>
   * Iterator position will be placed at fromOffset-1. If fromOffset is the starting index,
   * then index() and value() would throw NoSuchElementException until iterator is advanced <p>
   * Example: removeRange(-1,2) will remove the current element, elements at index()-1 and at index()+1,
   * and will position iterator at index()-2.
   */
  void removeRange(int fromOffset, int toOffset) throws NoSuchElementException;

  /**
  * Removes element at current iterator position. If it was the last element, further call to value() or index()
  * will throw NoSuchElementException.
  * @throws NoSuchElementException if iterator has never been advanced.
  */
  void remove() throws NoSuchElementException, ConcurrentModificationException;
}
