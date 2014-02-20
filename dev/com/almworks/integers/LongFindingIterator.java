/*
  * Copyright 2010 ALM Works Ltd
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
import java.util.NoSuchElementException;

public abstract class LongFindingIterator extends AbstractLongIterator {
  protected long myNext = 0xDEADBEEF;
  private long myCurrent;

  private static final int NO_CACHED = 0, CACHED = 1, FINISHED = 2;
  private int myIteratorStatus = NO_CACHED;
  private boolean myIterated = false;

  /**
   * In this method {@code myNext} should be assigned the next value.
   * @return true if the next value exists and was assigned to {@code myNext}, otherwise false
   * */
  protected abstract boolean findNext() throws ConcurrentModificationException;

  public final boolean hasNext() throws ConcurrentModificationException {
    if (myIteratorStatus == NO_CACHED) {
      boolean hasNext = findNext();
      if (hasNext) {
        myIteratorStatus = CACHED;
        return true;
      } else {
        myIteratorStatus = FINISHED;
        return false;
      }
    }
    assert myIteratorStatus == CACHED || myIteratorStatus == FINISHED;
    return myIteratorStatus == CACHED;
  }

  public LongIterator next() throws ConcurrentModificationException, NoSuchElementException {
    if (myIteratorStatus == CACHED) {
      myCurrent = myNext;
      myIteratorStatus = NO_CACHED;
    } else {
      if (myIteratorStatus == FINISHED || !findNext()) {
        throw new NoSuchElementException();
      }
      assert myIteratorStatus == NO_CACHED;
      myCurrent = myNext;
    }
    myIterated = true;
    return this;
  }

  public long value() throws NoSuchElementException {
    if (!myIterated) {
      throw new NoSuchElementException();
    }
    return myCurrent;
  }

  @Override
  public boolean hasValue() {
    return myIterated;
  }
}
