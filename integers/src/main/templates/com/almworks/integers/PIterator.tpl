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

/**
* @see #E#Iterable
*/
public interface #E#Iterator extends #E#Iterable, Iterator<#E#Iterator> {
  /**
  * Constant value for empty Iterators
  */
  Writable#E#ListIterator EMPTY = new #E#EmptyIterator();

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
  #e# value() throws NoSuchElementException;

  /**
  * @return next element and advances the iterator
  * @throws NoSuchElementException if there is no next element and iterator has reached an end
  * @throws ConcurrentModificationException if underlying collection is concurrently modified
  */
  #e# nextValue() throws ConcurrentModificationException, NoSuchElementException;

  class Single extends Abstract#E#IteratorWithFlag {
    private #e# myValue;

    public Single(#e# value) {
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
    protected #e# valueImpl() throws NoSuchElementException {
      return myValue;
    }
  }
}