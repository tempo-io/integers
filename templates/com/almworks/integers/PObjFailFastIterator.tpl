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

public abstract class #E#ObjFailFastIterator<T> extends Abstract#E#ObjIterator<T> {
  private final #E#ObjIterator<T> myIterator;
  private final int myModCountAtCreation = getCurrentModCount();

  protected abstract int getCurrentModCount();

  public #E#ObjFailFastIterator(#E#ObjIterator<T> it) {
    myIterator = it;
  }

  public boolean hasNext() throws ConcurrentModificationException {
    checkMod();
    return myIterator.hasNext();
  }

  public #E#ObjIterator<T> next() throws ConcurrentModificationException, NoSuchElementException {
    checkMod();
    myIterator.next();
    return this;
  }

  public boolean hasValue() throws ConcurrentModificationException {
    checkMod();
    return myIterator.hasValue();
  }

  public #e# left() throws IllegalStateException {
    checkMod();
    return myIterator.left();
  }

  public T right() throws IllegalStateException {
    checkMod();
    return myIterator.right();
  }

  private void checkMod() {
    if (myModCountAtCreation != getCurrentModCount())
      throw new ConcurrentModificationException(myModCountAtCreation + " " + getCurrentModCount());
  }
}
