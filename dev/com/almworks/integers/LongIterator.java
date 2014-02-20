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
* @see LongIterable
*/
public interface LongIterator extends LongIterable, Iterator<LongIterator> {
  /**
  * Constant value for empty Iterators
  */
  WritableLongListIterator EMPTY = new LongEmptyIterator();

  /**
   * @return {@code true} if next call to {@link #next()} or {@link #nextValue()} won't throw NoSuchElementException
   */
  boolean hasNext() throws ConcurrentModificationException;


  /**
   * @return {@code false} if this iterator has never been advanced.
   * In other words, returns {@code false} if the subsequent call to {@link #value()} will throw NoSuchElementException, otherwise {@code true}.
   */
  boolean hasValue();

  /**
   * @throws NoSuchElementException if iterator has never been advanced
   * ({@link #next()} or {@link #nextValue()} have never been called)
   */
  long value() throws NoSuchElementException;

  /**
  * @return next element and advances the iterator
  * @throws NoSuchElementException if there is no next element and iterator has reached an end
  * @throws ConcurrentModificationException if underlying collection is concurrently modified
  */
  long nextValue() throws ConcurrentModificationException, NoSuchElementException;

  class Single extends AbstractLongIteratorWithFlag {
    private long myValue;

    public Single(long value) {
      myValue = value;
    }

    public boolean hasNext() {
      return !myIterated;
    }

    @Override
    protected void nextImpl() throws NoSuchElementException {
      if (!hasNext()) throw new NoSuchElementException();
    }

    @Override
    protected long valueImpl() throws NoSuchElementException {
      return myValue;
    }
  }
}