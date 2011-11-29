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
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
* @see #E#Iterable
*/
public interface #E#Iterator extends Iterator<#E#Iterator> {
  /**
  * Constant value for empty Iterators
  */
  Writable#E#ListIterator EMPTY = new Empty#E#Iterator();

  /**
   * @return true next call to {@link #next()} or {@link #nextValue()} won't throw NoSuchElementException
   */
  boolean hasNext() throws ConcurrentModificationException;

  /**
   * @throws NoSuchElementException if there is no next element and iterator has reached its end or if iterator
   * wasn't ever advanced ({@link #next()} or {@link #nextValue()} weren't ever called)
   */
  #e# value() throws NoSuchElementException;

  /**
  * @return next element and advances iterator.
  * @throws NoSuchElementException if there is no next element and iterator has reached an end
  * @throws ConcurrentModificationException if underlaying collection is concurrently modified
  */
  #e# nextValue() throws ConcurrentModificationException, NoSuchElementException;
}