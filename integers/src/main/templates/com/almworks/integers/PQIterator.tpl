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

package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public interface #E##F#Iterator extends Iterator<#E##F#Iterator>, #E##F#Iterable {
  /**
   * Constant value for empty Iterators
   */
  #E##F#Iterator EMPTY = new #E##F#EmptyIterator();

  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other
   * words, returns <tt>true</tt> if <tt>next</tt> would return an element
   * rather than throwing an exception.)
   *
   * @return <tt>true</tt> if the iterator has more elements.
   */
  boolean hasNext() throws ConcurrentModificationException;

  /**
   * @return {@code false} if this iterator has never been advanced.
   * In other words, returns {@code false} if the subsequent call to {@link #left()} or {@link #right()} will throw NoSuchElementException, otherwise {@code true}.
   */
  boolean hasValue();

  /**
   * @throws NoSuchElementException if iterator has never been advanced
   * ({@link #next()} has never been called)
   */
  #e# left() throws NoSuchElementException;

  /**
   * @throws NoSuchElementException if iterator has never been advanced
   * ({@link #next()} has never been called)
   */
  #f# right() throws NoSuchElementException;

  class Single extends Abstract#E##F#IteratorWithFlag {
    private #e# myLeft;
    private #f# myRight;

    public Single(#e# left, #f# right) {
      myLeft = left;
      myRight = right;
    }

    public boolean hasNext() {
      return !myIterated;
    }

    @Override
    protected #e# leftImpl() {
      return myLeft;
    }

    @Override
    protected #f# rightImpl() {
      return myRight;
    }

    @Override
    protected void nextImpl() throws NoSuchElementException {
      if (!hasNext()) throw new NoSuchElementException();
    }
  }
}
