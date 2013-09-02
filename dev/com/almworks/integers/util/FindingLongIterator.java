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
  protected long myCurrent = Long.MIN_VALUE;
  protected long myNext = Long.MIN_VALUE;
  private boolean storeValueState = false;

  /**
   * In this method in the {@code myCurrent} must be assigned next value, if it exist.
   * @return true if this iterator has next value, otherwise - false
   * */
  protected abstract boolean findNext();

  public final boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    if (storeValueState) {
      return true;
    }
    boolean myHasNext = findNext();
    if (myHasNext) {
      storeValueState = true;
    }
    return myHasNext;
  }

  protected final void nextImpl() throws ConcurrentModificationException, NoSuchElementException {
    if (storeValueState) {
      myNext = myCurrent;
      storeValueState = false;
    } else {
      boolean myHasNext = findNext();
      if (!myHasNext) throw new NoSuchElementException();
      myNext = myCurrent;
    }
  }

  public long valueImpl() throws NoSuchElementException {
    return myNext;
  }
}
