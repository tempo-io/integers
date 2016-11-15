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

public class #E#ListIndexedIterator extends Abstract#E#IteratorWithFlag implements #E#ListIterator {
  private final IntListIterator myIndexes;
  private final #E#List mySource;

  public #E#ListIndexedIterator(#E#List source, IntListIterator indexIterator) {
    mySource = source;
    myIndexes = indexIterator;
  }

  public boolean hasNext() {
    return myIndexes.hasNext();
  }


  public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
    myIndexes.move(offset);
  }

  public #e# get(int relativeOffset) throws NoSuchElementException {
    return mySource.get(myIndexes.get(relativeOffset));
  }

  public int index() throws NoSuchElementException {
    if (!myIterated) throw new NoSuchElementException();
    return myIndexes.index();
  }

  @Override
  protected void nextImpl() throws ConcurrentModificationException, NoSuchElementException {
    myIndexes.next();
  }

  @Override
  protected #e# valueImpl() throws NoSuchElementException {
    return mySource.get(myIndexes.value());
  }
}
