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

package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class Indexed#E#Iterator extends Abstract#E#Iterator {
  private final #E#List myList;
  private final IntIterator myIndexIterator;
  private boolean myIterated;

  public Indexed#E#Iterator(#E#List list, IntIterator indexIterator) {
    myList = list;
    myIndexIterator = indexIterator;
  }

  public boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    return myIndexIterator.hasNext();
  }

  public #E#Iterator next() throws ConcurrentModificationException, NoSuchElementException {
    myIndexIterator.next();
    myIterated = true;
    return this;
  }

  public #e# value() {
    if (!myIterated) throw new IllegalStateException();
    return myList.get(myIndexIterator.value());
  }
}
