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

// CODE GENERATED FROM com/almworks/integers/EmptyPIterator.tpl


package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class EmptyLongIterator extends AbstractLongIterator implements WritableLongListIterator {
  public void set(int offset, long value) throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  public void removeRange(int fromOffset, int toOffset) throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  @Override
  public void remove() throws NoSuchElementException, ConcurrentModificationException {
    throw new NoSuchElementException();
  }

  public boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    return false;
  }

  public WritableLongListIterator next() throws ConcurrentModificationException, NoSuchElementException {
    throw new NoSuchElementException();
  }

  public boolean hasValue() {
    return false;
  }

  public long value() throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
    if (offset != 0) throw new NoSuchElementException();
//    throw new NoSuchElementException();
  }

  public long get(int offset) throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  public int index() throws NoSuchElementException {
    throw new NoSuchElementException();
  }
}
