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

import com.almworks.integers.AbstractLongIteratorWithFlag;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class FindingLongIterator extends AbstractLongIteratorWithFlag {
  protected long myCurrent = Long.MAX_VALUE;
  private long myNext = Long.MAX_VALUE;
  // 0 - value not stored,
  // 1 - value stored,
  // 2 - iterator ended,
  private int myIteratorStatus = 0;

  /**
   * In this method in {@code myCurrent} should be assigned the next value.
   * @return true if the next value exists and was assigned to {@code myCurrent}, otherwise false
   * */
  protected abstract boolean findNext();

  public final boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    if (myIteratorStatus == 2) {
      return false;
    }
    if (myIteratorStatus == 1) {
      return true;
    }
    // myIteratorStatus = 0
    boolean hasNext = findNext();
    myIteratorStatus = hasNext ? 1 : 2;
    return hasNext;
  }

  protected final void nextImpl() throws ConcurrentModificationException, NoSuchElementException {
    if (myIteratorStatus == 2) {
      throw new NoSuchElementException();
    }
    if (myIteratorStatus == 1) {
      myIteratorStatus = 0;
    } else {
      boolean hasNext = findNext();
      if (!hasNext) throw new NoSuchElementException();
    }
    myNext = myCurrent;
  }

  public long valueImpl() throws NoSuchElementException {
    return myNext;
  }
}
