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
  private long myNext;

  private static final int FINISHED = 0, NO_VALUE = 1, VALUE_STORED = 2;
  private int myIteratorStatus = NO_VALUE;
  private boolean myIterated = false;

  /**
   * In this method in {@code myCurrent} should be assigned the next value.
   * @return true if the next value exists and was assigned to {@code myCurrent}, otherwise false
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
    assert myIteratorStatus == VALUE_STORED || myIteratorStatus == FINISHED;
    return myIteratorStatus == VALUE_STORED;
  }

  public LongIterator next() throws ConcurrentModificationException, NoSuchElementException {
    if (myIteratorStatus == VALUE_STORED) {
      myNext = myCurrent;
      myIteratorStatus = NO_VALUE;
    } else {
      if (myIteratorStatus == FINISHED || !findNext()) {
        throw new NoSuchElementException();
      }
      assert myIteratorStatus == NO_VALUE;
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
