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
 * There is iterators, that status hasValue() can't be calculate from
 * some class fields, and it's necessary also store separately flag
 * boolean.<br>
 * For such purposes approach class
 * AbstractLongIteratorWithFlag, which store and work with flag take over.
 *
 * For iterator implementation it's necessary to define 2 methods: nextImpl(), valueImpl()
 * nextImpl()  switch iterator position to next state
 * valueImpl() return the current value, if it's possible
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

  abstract protected long valueImpl();

  protected abstract void nextImpl();
}
