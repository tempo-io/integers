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

import com.almworks.integers.AbstractIntLongIteratorWithFlag;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class FindingIntLongIterator extends AbstractIntLongIteratorWithFlag {
  protected int myCurrentLeft;
  protected long myCurrentRight;
  private int myNextLeft;
  private long myNextRight;
  private boolean myIsValueStored = false;

  /**
   * In this method in {@code myCurrent} must be assigned next value, if it exist.
   * @return true if this iterator has next value, otherwise - false
   * */
  protected abstract boolean findNext();

  public final boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    if (myIsValueStored) {
      return true;
    }
    boolean hasNext = findNext();
    if (hasNext) {
      myIsValueStored = true;
    }
    return hasNext;
  }

  protected final void nextImpl() throws ConcurrentModificationException, NoSuchElementException {
    if (myIsValueStored) {
      myIsValueStored = false;
    } else {
      boolean hasNext = findNext();
      if (!hasNext) throw new NoSuchElementException();
    }
    myNextLeft = myCurrentLeft;
    myNextRight = myCurrentRight;
  }

  @Override
  protected int leftImpl() {
    return myNextLeft;
  }

  @Override
  protected long rightImpl() {
    return myNextRight;
  }
}
