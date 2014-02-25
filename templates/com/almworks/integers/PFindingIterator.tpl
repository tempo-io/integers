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

package com.almworks.integers.util;

import com.almworks.integers.Abstract#E#Iterator;
import com.almworks.integers.#E#Iterator;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class Finding#E#Iterator extends Abstract#E#Iterator {
  private boolean myFound;
  private boolean mySought;
  private boolean myIterated;

  public final boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    if (myFound)
      return true;
    if (mySought)
      return false;
    myFound = findNext();
    mySought = true;
    return myFound;
  }

  public final #E#Iterator next() throws ConcurrentModificationException, NoSuchElementException {
    if (!mySought) {
      myFound = findNext();
      mySought = true;
    }
    if (!myFound)
      throw new NoSuchElementException();
    mySought = false;
    myFound = false;
    myIterated = true;
    return this;
  }

  public #e# value() throws NoSuchElementException {
    if (!myIterated)
      throw new NoSuchElementException();
    return getNext();
  }

  protected abstract #e# getNext();

  protected abstract boolean findNext();
}
