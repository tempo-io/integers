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

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Iterator for iterating through two specified iterables.
 * Invocation of {@link #next()} invokes {@code next()} in both inner iterators.
 * {@link #left()} and {@link #right()} return {@code value()} of the corresponding inner iterators.
 * {@link #hasNext()} returns false if any of the inner iterators returns false.
 * I.e. the remaining values in the other iterator are ignored.
 */
public class IntLongPairIterator extends AbstractIntLongIteratorWithFlag {
  private final IntIterator myIt1;
  private final LongIterator myIt2;

  public IntLongPairIterator(IntIterable first, LongIterable second) {
    myIt1 = first.iterator();
    myIt2 = second.iterator();
  }

  @Override
  protected int leftImpl() {
    return myIt1.value();
  }

  @Override
  protected long rightImpl() {
    return myIt2.value();
  }

  @Override
  protected void nextImpl() throws NoSuchElementException {
    if (!hasNext()) throw new NoSuchElementException();
    myIt1.next();
    myIt2.next();
  }

  @Override
  public boolean hasNext() throws ConcurrentModificationException {
    return myIt1.hasNext() && myIt2.hasNext();
  }
}