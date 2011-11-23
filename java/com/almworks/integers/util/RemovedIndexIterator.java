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

import com.almworks.integers.AbstractIntIterator;
import com.almworks.integers.IntListIterator;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

class RemovedIndexIterator extends AbstractIntIterator implements IntListIterator  {
  private final IntListIterator myRemovedLocations;

  RemovedIndexIterator(IntListIterator removedLocations) {
    myRemovedLocations = removedLocations;
  }

  public boolean hasNext() {
    return myRemovedLocations.hasNext();
  }

  public IntListIterator next() throws ConcurrentModificationException, NoSuchElementException {
    int value = myRemovedLocations.nextValue();
    myValue = value + myRemovedLocations.lastIndex();
    myIterated= true;
    return this;
  }

  public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
    myRemovedLocations.move(offset);
  }

  public int get(int relativeOffset) throws NoSuchElementException {
    int value = myRemovedLocations.get(relativeOffset);
    return value + myRemovedLocations.lastIndex() + relativeOffset;
  }

  public int lastIndex() {
    return myRemovedLocations.lastIndex();
  }

}
