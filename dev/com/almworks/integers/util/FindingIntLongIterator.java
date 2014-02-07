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

// CODE GENERATED FROM com/almworks/integers/util/FindingPIterator.tpl


package com.almworks.integers.util;

import com.almworks.integers.AbstractIntLongIterator;
import com.almworks.integers.IntLongIterator;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class FindingIntLongIterator extends AbstractIntLongIterator {
  protected int myCurrentLeft, myNextLeft;
  protected long myCurrentRight, myNextRight;
  private static final int FINISHED = 0, NO_VALUE = 1, VALUE_STORED = 2;
  private int myIteratorStatus = NO_VALUE;
  private boolean myIterated = false;

  /**
   * In this method in {@code myNextLeft} and {@code myNextRight} must be assigned next value, if it exist.
   * @return true if this iterator has next value, otherwise - false
   * */
  protected abstract boolean findNext();

  public final boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    if (myIteratorStatus == NO_VALUE) {
      boolean hasNext = findNext();
      if (hasNext) {
        myIteratorStatus = VALUE_STORED;
        return true;
      } else {
        myIteratorStatus = FINISHED;
        return false;
      }
    }
    return myIteratorStatus == VALUE_STORED;
  }


  @Override
  public IntLongIterator next() {
    if (myIteratorStatus == VALUE_STORED) {
      myCurrentLeft = myNextLeft;
      myCurrentRight = myNextRight;
      myIteratorStatus = NO_VALUE;
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
