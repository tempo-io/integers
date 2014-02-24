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
import java.util.NoSuchElementException;

public abstract class IntLongFindingIterator extends AbstractIntLongIterator {
  protected int myNextLeft = 0xDEADBEEF;
  private int myCurrentLeft;
  protected long myNextRight = 0xDEADBEEF;
  private long myCurrentRight;

  private static final int NO_CACHED = 0, CACHED = 1, FINISHED = 2;
  private int myIteratorStatus = NO_CACHED;
  private boolean myIterated = false;

  /**
   * In this method {@code myNextLeft} and {@code myNextRight} must be assigned next value, if they exist.
   * @return true if this iterator has next value, otherwise - false
   * */
  protected abstract boolean findNext() throws ConcurrentModificationException;

  public final boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
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
    return myIteratorStatus == CACHED;
  }


  @Override
  public IntLongIterator next() {
    if (myIteratorStatus == CACHED) {
      myCurrentLeft = myNextLeft;
      myCurrentRight = myNextRight;
      myIteratorStatus = NO_CACHED;
    } else {
      if (myIteratorStatus == FINISHED || !findNext()) {
        throw new NoSuchElementException();
      }
      myCurrentLeft = myNextLeft;
      myCurrentRight = myNextRight;
    }
    myIterated = true;
    return this;
  }

  @Override
  public boolean hasValue() {
    return myIterated;
  }

  @Override
  public int left() throws NoSuchElementException {
    if (!hasValue()) {
      throw new NoSuchElementException();
    }
    return myCurrentLeft;
  }

  @Override
  public long right() throws NoSuchElementException {
    if (!hasValue()) {
      throw new NoSuchElementException();
    }
    return myCurrentRight;
  }
}
