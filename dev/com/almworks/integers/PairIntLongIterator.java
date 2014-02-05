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

// CODE GENERATED FROM com/almworks/integers/PairIntPIterator.tpl


package com.almworks.integers;

import java.util.NoSuchElementException;

/**
 * Iterator for iterating through two specified iterators created from iterables.
 * Invocation of the {@link #next()} invokes {@code next()} in the both inner iterators.
 * {@link #left()} and {@link #right()} returns {@code value()} of the corresponding inner iterators.
 * {@link #hasNext()} returns false if any of the inner iterators returns false.
 * I.e. the remaining values in the other iterator is ignored.
 */
public class PairIntLongIterator implements IntLongIterator {
  private final IntIterator myIt1;
  private final LongIterator myIt2;
  // 0 - no value, 1 - has value, 2 - broken
  private int myIteratorStatus;

  public PairIntLongIterator(IntIterable first, LongIterable second) {
    myIt1 = first.iterator();
    myIt2 = second.iterator();
  }

  public IntLongIterator iterator() {
    return this;
  }

  public boolean hasNext() {
    return (myIt1.hasNext() && myIt2.hasNext());
  }

  public PairIntLongIterator next() {
    if (myIteratorStatus == 2) {
      throw new NoSuchElementException();
    }
    myIteratorStatus = 2;
    myIt1.next();
    myIt2.next();
    myIteratorStatus = 1;
    return this;
  }

  public int left() {
    if (!hasValue())
      throw new NoSuchElementException();
    return myIt1.value();
  }

  public long right() {
    if (!hasValue())
      throw new NoSuchElementException();
    return myIt2.value();
  }

  public boolean hasValue() {
    return myIteratorStatus == 1;
  }

  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
}