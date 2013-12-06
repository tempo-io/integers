/*
 * Copyright 2013 ALM Works Ltd
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

import com.almworks.integers.AbstractIntIterator;
import com.almworks.integers.IntIterator;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class FailFastIntIterator extends AbstractIntIterator {
  private final IntIterator myIterator;
  private final int myModCountAtCreation = getCurrentModCount();

  protected abstract int getCurrentModCount();

  public FailFastIntIterator(IntIterator it) {
    myIterator = it;
  }

  public boolean hasNext() throws ConcurrentModificationException {
    checkMod();
    return myIterator.hasNext();
  }

  public IntIterator next() throws ConcurrentModificationException, NoSuchElementException {
    checkMod();
    myIterator.next();
    return this;
  }

  public int value() throws IllegalStateException {
    checkMod();
    return myIterator.value();
  }

  private void checkMod() {
    if (myModCountAtCreation != getCurrentModCount())
      throw new ConcurrentModificationException(myModCountAtCreation + " " + getCurrentModCount());
  }
}