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

package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

class RemovedIndexIterator extends AbstractIntIteratorWithFlag implements IntListIterator  {
  private final IntListIterator myRemovedLocations;

  RemovedIndexIterator(IntListIterator removedLocations) {
    myRemovedLocations = removedLocations;
  }

  public boolean hasNext() {
    return myRemovedLocations.hasNext();
  }

  protected void nextImpl() throws ConcurrentModificationException, NoSuchElementException {
    myRemovedLocations.next();
  }

  protected int valueImpl() throws NoSuchElementException {
    return myRemovedLocations.value() + myRemovedLocations.index();
  }

  public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
    myRemovedLocations.move(offset);
  }

  public int get(int relativeOffset) throws NoSuchElementException {
    int value = myRemovedLocations.get(relativeOffset);
    return value + myRemovedLocations.index() + relativeOffset;
  }

  public int index() {
    return myRemovedLocations.index();
  }

}
