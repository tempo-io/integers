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

import com.almworks.integers.AbstractLongIterator;
import com.almworks.integers.LongIterator;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class FindingLongIterator extends AbstractLongIterator {
  protected long myCurrent = Long.MAX_VALUE;
  private long myNext = Long.MAX_VALUE;

  // (2, 1) bits: (myNotEnded, myValueStored)
  private int myIteratorStatus = 2;
  private boolean myIterated = false;

  /**
   * In this method in {@code myCurrent} should be assigned the next value.
   * @return true if the next value exists and was assigned to {@code myCurrent}, otherwise false
   * */
  protected abstract boolean findNext();

  public final boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    if (myIteratorStatus == 2) {
      boolean hasNext = findNext();
      if (hasNext) {
        myIteratorStatus = 3;
        return true;
      } else {
        myIteratorStatus = 0;
        return false;
      }
    }
    assert myIteratorStatus == 3 || myIteratorStatus == 0;
    return myIteratorStatus == 3;
  }

  public LongIterator next() throws ConcurrentModificationException, NoSuchElementException {
    if (myIteratorStatus == 3) {
      myNext = myCurrent;
      myIteratorStatus = 2;
    } else {
      if (myIteratorStatus == 0) {
        throw new NoSuchElementException();
      }
      boolean hasNext = findNext();
      if (!hasNext) {
        throw new NoSuchElementException();
      }
      assert myIteratorStatus == 2;
      myNext = myCurrent;
    }
    myIterated = true;
    return this;
  }

  public long value() throws NoSuchElementException {
    if (!myIterated) {
      throw new NoSuchElementException();
    }
    return myNext;
  }

  @Override
  public boolean hasValue() {
    return myIterated;
  }
}
