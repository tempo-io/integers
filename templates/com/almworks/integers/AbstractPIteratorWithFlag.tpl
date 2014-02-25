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

import java.util.NoSuchElementException;


/**
 * This is a convenient base class for iterators that store
 * {@link com.almworks.integers.#E#Iterator#hasValue()}
 * state in a separate boolean flag.
 * These are the iterators that cannot calculate {@link #E#Iterator#hasValue()}
 * based only on their state. Example: {@link #E#Iterators#repeat(#e#)}, {@link #E#Iterator.Single}
 *
 * <br>Instead of implementing {@code next()} and {@code value()}, you'll need to implement {@code nextImpl()} and {@code valueImpl()}, where:
 * <ul>
 * <li>{@link Abstract#E#IteratorWithFlag#nextImpl()} is called before any call to
 * {@link Abstract#E#IteratorWithFlag#valueImpl()} and should be used to either initialize the
 * state or advance the iterator.
 * <li>{@link Abstract#E#IteratorWithFlag#valueImpl()} can safely assume that the iterator is initialized.
 * </ul>
 * */
public abstract class Abstract#E#IteratorWithFlag extends Abstract#E#Iterator {
  protected boolean myIterated = false;

  public boolean hasValue() {
    return myIterated;
  }

  public #e# value() throws NoSuchElementException {
    if (!myIterated)
      throw new NoSuchElementException();
    return valueImpl();
  }

  public #E#Iterator next() {
    nextImpl();
    myIterated = true;
    return this;
  }

  /**
   * Called before any call to {@link Abstract#E#IteratorWithFlag#valueImpl()}, should be used to either initialize the state or advance the iterator.
   * */
  protected abstract void nextImpl() throws NoSuchElementException;

  /**
   * Called after any call to {@link Abstract#E#IteratorWithFlag#nextImpl()}, should return value of iterator.
   * Can safely assume that the iterator is initialized.
   * */
  protected abstract #e# valueImpl();
}
