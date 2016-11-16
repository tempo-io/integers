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
 * Iterator for iterating through two specified iterables.
 * Invocation of {@link #next()} invokes {@code next()} in both inner iterators.
 * {@link #left()} and {@link #right()} return {@code value()} of the corresponding inner iterators.
 * {@link #hasNext()} returns false if any of the inner iterators returns false.
 * I.e. the remaining values in the other iterator are ignored.
 */
public class #E#ObjPairIterator<T> extends Abstract#E#ObjIteratorWithFlag<T> {
  private final #E#Iterator myItLeft;
  private final Iterator<T> myItRight;
  private T myCurrentRight;

  public #E#ObjPairIterator(#E#Iterable first, Iterator<T> second) {
    myItLeft = first.iterator();
    myItRight = second;
  }

  @Override
  protected #e# leftImpl() {
    return myItLeft.value();
  }

  @Override
  protected T rightImpl() {
    return myCurrentRight;
  }

  @Override
  protected void nextImpl() throws NoSuchElementException {
    if (!hasNext()) throw new NoSuchElementException();
    myItLeft.next();
    myCurrentRight = myItRight.next();
  }

  @Override
  public boolean hasNext() throws ConcurrentModificationException {
    return myItLeft.hasNext() && myItRight.hasNext();
  }
}
