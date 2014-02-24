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

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PairIntIntIterator implements Iterable<PairIntIntIterator>, Iterator<PairIntIntIterator> {
  private final IntIterator myIt1;
  private final IntIterator myIt2;
  private boolean myIterated;

  public PairIntIntIterator(IntIterator first, IntIterator second) {
    myIt1 = first;
    myIt2 = second;
  }

  public PairIntIntIterator iterator() {
    return this;
  }

  public boolean hasNext() {
    return (myIt1.hasNext() && myIt2.hasNext());
  }

  public PairIntIntIterator next() {
    myIterated = false;
    myIt2.next();
    myIt1.next();
    myIterated = true;
    return this;
  }

  public int value2() {
    if (!myIterated)
      throw new NoSuchElementException();
    return myIt2.value();
  }

  public int value1() {
    if (!myIterated)
      throw new NoSuchElementException();
    return myIt1.value();
  }

  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
}
