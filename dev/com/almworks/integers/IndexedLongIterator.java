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

// CODE GENERATED FROM com/almworks/integers/IndexedPIterator.tpl


package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class IndexedLongIterator extends AbstractLongIteratorWithFlag {
  private final LongList myList;
  private final IntIterator myIndexIterator;

  public IndexedLongIterator(LongList list, IntIterator indexIterator) {
    myList = list;
    myIndexIterator = indexIterator;
  }

  public boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    return myIndexIterator.hasNext();
  }

  public void nextImpl() throws ConcurrentModificationException, NoSuchElementException {
    myIndexIterator.next();
  }

  public long valueImpl() {
    return myList.get(myIndexIterator.value());
  }
}
