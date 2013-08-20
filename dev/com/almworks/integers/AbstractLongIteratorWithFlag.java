/*
 * Copyright 2011 ALM Works Ltd
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

// CODE GENERATED FROM com/almworks/integers/AbstractPIterator.tpl


package com.almworks.integers;

import java.util.NoSuchElementException;


/**
 * This is a convenient base class for iterators that store
 * {@link com.almworks.integers.AbstractLongIterator#hasValue()}
 * state in a separate boolean flag.
 * These are the iterators that cannot calculate {@link AbstractLongIterator#hasValue()}
 * based only on the state necessary to implement {@link AbstractLongIterator#value()} and
 * {@link AbstractLongIterator#hasNext()}. Example: IndexedLongIterator.
 * To implement this class, one needs to implement 3 methods,
 * {@link AbstractLongIteratorWithFlag#nextImpl()}, {@link AbstractLongIteratorWithFlag#valueImpl()} and
 * {@link AbstractLongIteratorWithFlag#hasNext()}
 * <ul>
 * <li>{@link AbstractLongIteratorWithFlag#nextImpl()} is called before any call to
 * {@link AbstractLongIteratorWithFlag#valueImpl()} and should be used to either initialize the
 * state or advance the iterator.
 * <li>{@link AbstractLongIteratorWithFlag#valueImpl()} can safely assume that the iterator is initialized.
 * </ul>
 * */
public abstract class AbstractLongIteratorWithFlag extends AbstractLongIterator {
  protected boolean myIterated = false;

  public LongIterator iterator() {
    return this;
  }

  public boolean hasValue() {
    return myIterated;
  }

  public long value() throws NoSuchElementException {
    if (!myIterated)
      throw new NoSuchElementException();
    return valueImpl();
  }

  public LongIterator next() {
    nextImpl();
    myIterated = true;
    return this;
  }

  public long nextValue() {
    next();
    return value();
  }

  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  protected abstract long valueImpl();

  protected abstract void nextImpl() throws NoSuchElementException;
}
