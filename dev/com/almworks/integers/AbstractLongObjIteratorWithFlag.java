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

// CODE GENERATED FROM com/almworks/integers/AbstractPQIteratorWithFlag.tpl


package com.almworks.integers;

import java.util.NoSuchElementException;

/**
 * @see LongObjIterator
 * @see AbstractLongIteratorWithFlag
 */
public abstract class AbstractLongObjIteratorWithFlag<T> extends AbstractLongObjIterator<T> {

  protected boolean myIterated = false;

  public boolean hasValue() {
    return myIterated;
  }

  public long left() throws NoSuchElementException {
    if (!myIterated) {
      throw new NoSuchElementException();
    }
    return leftImpl();
  }

  public T right() throws NoSuchElementException {
    if (!myIterated) {
      throw new NoSuchElementException();
    }
    return rightImpl();
  }

  public LongObjIterator next() {
    nextImpl();
    myIterated = true;
    return this;
  }

  /**
   * Called after any call to {@link com.almworks.integers.AbstractLongIteratorWithFlag#nextImpl()}, should return value of iterator.
   * Can safely assume that the iterator is initialized.
   * */
  protected abstract long leftImpl();

  /**
   * Called after any call to {@link com.almworks.integers.AbstractLongIteratorWithFlag#nextImpl()}, should return value of iterator.
   * Can safely assume that the iterator is initialized.
   * */
  protected abstract T rightImpl();

  /**
   * Called before any call to {@link com.almworks.integers.AbstractLongIteratorWithFlag#valueImpl()}, should be used to either initialize the state or advance the iterator.
   * */
  protected abstract void nextImpl() throws NoSuchElementException;
}