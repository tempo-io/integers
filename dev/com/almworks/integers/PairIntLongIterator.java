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

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/**
 * Iterator for iterating through two specified iterators created from iterables.
 * Invocation of {@link #next()} invokes {@code next()} in both inner iterators.
 * {@link #left()} and {@link #right()} return {@code value()} of the corresponding inner iterators.
 * {@link #hasNext()} returns false if any of the inner iterators returns false.
 * I.e. the remaining values in the other iterator are ignored.
 */
public class PairIntLongIterator implements IntLongIterator {
  private final IntIterator myIt1;
  private final LongIterator myIt2;

  public PairIntLongIterator(IntIterable first, LongIterable second) {
    myIt1 = first.iterator();
    myIt2 = second.iterator();
  }

  @NotNull
  public IntLongIterator iterator() {
    return this;
  }

  public boolean hasNext() {
    return (myIt1.hasNext() && myIt2.hasNext());
  }

  public PairIntLongIterator next() throws NoSuchElementException, IllegalStateException {
    if (!hasNext()) throw new NoSuchElementException();
    myIt1.next();
    myIt2.next();
    return this;
  }

  public int left() {
    if (!hasValue()) throw new NoSuchElementException();
    return myIt1.value();
  }

  public long right() {
    if (!hasValue()) throw new NoSuchElementException();
    return myIt2.value();
  }

  public boolean hasValue() {
    return myIt2.hasValue();
  }

  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
}