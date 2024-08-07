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

public class #E#IndexedIterator extends Abstract#E#IteratorWithFlag {
  private final #E#List myList;
  private final IntIterator myIndexIterator;

  public #E#IndexedIterator(#E#List list, IntIterator indexIterator) {
    myList = list;
    myIndexIterator = indexIterator;
  }

  public boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
    return myIndexIterator.hasNext();
  }

  @Override
  protected void nextImpl() throws ConcurrentModificationException, NoSuchElementException {
    myIndexIterator.next();
  }

  @Override
  protected #e# valueImpl() {
    return myList.get(myIndexIterator.value());
  }
}
