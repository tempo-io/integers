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

// CODE GENERATED FROM com/almworks/integers/PIterator.tpl


package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
* @see IntIterable
*/
public interface IntIterator extends IntIterable, Iterator<IntIterator> {
  /**
  * Constant value for empty Iterators
  */
  WritableIntListIterator EMPTY = new EmptyIntIterator();

  /**
   * @return true next call to {@link #next()} or {@link #nextValue()} won't throw NoSuchElementException
   */
  boolean hasNext() throws ConcurrentModificationException;

  /**
   * @throws NoSuchElementException if iterator has never been advanced
   * ({@link #next()} or {@link #nextValue()} have never been called)
   */
  int value() throws NoSuchElementException;

  /**
  * @return next element and advances the iterator
  * @throws NoSuchElementException if there is no next element and iterator has reached an end
  * @throws ConcurrentModificationException if underlying collection is concurrently modified
  */
  int nextValue() throws ConcurrentModificationException, NoSuchElementException;

  class Single extends AbstractIntIterator {
    private int myValue;
    private boolean myIterated;

    public Single(int value) {
      myValue = value;
    }

    public boolean hasNext() {
      return !myIterated;
    }

    public IntIterator next() throws NoSuchElementException {
      if (myIterated)
        throw new NoSuchElementException();
      myIterated = true;
      return this;
    }

    public int value() throws NoSuchElementException {
      if (!myIterated)
        throw new NoSuchElementException();
      return myValue;
    }
  }
}