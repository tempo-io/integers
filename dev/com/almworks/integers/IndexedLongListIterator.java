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

// CODE GENERATED FROM com/almworks/integers/IndexedPListIterator.tpl


package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class IndexedLongListIterator extends AbstractLongIterator implements LongListIterator {
  private final IntListIterator myIndexes;
  private final LongList mySource;
  private boolean myIterated;

  public IndexedLongListIterator(LongList source, IntListIterator indexIterator) {
    mySource = source;
    myIndexes = indexIterator;
  }

  public boolean hasNext() {
    return myIndexes.hasNext();
  }

  public LongListIterator next() throws ConcurrentModificationException, NoSuchElementException {
    myIndexes.next();
    myIterated = true;
    return this;
  }

  public long value() throws NoSuchElementException {
    if (!myIterated) throw new NoSuchElementException();
    return mySource.get(myIndexes.value());
  }

  public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
    myIndexes.move(offset);
  }

  public long get(int relativeOffset) throws NoSuchElementException {
    return mySource.get(myIndexes.get(relativeOffset));
  }

  public int index() throws NoSuchElementException {
    if (!myIterated) throw new NoSuchElementException();
    return myIndexes.index();
  }
}
