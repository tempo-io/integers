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
import com.almworks.integers.AbstractLongIteratorWithFlag;
import com.almworks.integers.LongIterator;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class FindingLongIterator extends AbstractLongIteratorWithFlag {
  private boolean myFound;
  private boolean mySought;
  protected long myNext = Long.MIN_VALUE;

  protected abstract boolean findNext();

  public final boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    if (myFound)
      return true;
    if (mySought)
      return false;
    myFound = findNext();
    mySought = true;
    return myFound;
  }

  protected final void nextImpl() throws ConcurrentModificationException, NoSuchElementException {
    if (!mySought) {
      myFound = findNext();
      mySought = true;
    }
    if (!myFound)
      throw new NoSuchElementException();
    mySought = false;
    myFound = false;
  }

  public long valueImpl() throws NoSuchElementException {
    return getNext();
  }

  protected long getNext() {
    return myNext;
  }
}
