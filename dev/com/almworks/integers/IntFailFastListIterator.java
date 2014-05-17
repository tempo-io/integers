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

// CODE GENERATED FROM com/almworks/integers/PFailFastIterator.tpl


package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class IntFailFastListIterator extends AbstractIntIterator implements IntListIterator {
  private final IntListIterator myIterator;
  private final int myModCountAtCreation = getCurrentModCount();

  protected abstract int getCurrentModCount();

  public IntFailFastListIterator(IntListIterator it) {
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

  public boolean hasValue() throws ConcurrentModificationException {
    checkMod();
    return myIterator.hasValue();
  }

  public int value() throws IllegalStateException {
    checkMod();
    return myIterator.value();
  }

  @Override
  public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
    checkMod();
    myIterator.move(offset);
  }

  @Override
  public int get(int offset) throws ConcurrentModificationException, NoSuchElementException {
    checkMod();
    return myIterator.get(offset);
  }

  @Override
  public int index() throws NoSuchElementException {
    checkMod();
    return myIterator.index();
  }

  private void checkMod() {
    if (myModCountAtCreation != getCurrentModCount())
      throw new ConcurrentModificationException(myModCountAtCreation + " " + getCurrentModCount());
  }
}