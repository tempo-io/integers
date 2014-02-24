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

import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class DiffIndexedIntListDecorator extends AbstractIntList {
  private final IntList mySource;
  private final IntList myIndices;

  public DiffIndexedIntListDecorator(IntList source, IntList indices) {
    mySource = source;
    myIndices = indices;
  }

  public int size() {
    return myIndices.size();
  }

  public int get(int index) {
    return mySource.get(myIndices.get(index) + index);
  }

  public boolean isEmpty() {
    return myIndices.isEmpty();
  }

  @NotNull
  public IntListIterator iterator(int from, int to) {
    IntListIterator indexIterator = myIndices.iterator(from, to);
    return new DiffIndexedIterator(from, indexIterator);
  }

  public IntList getSource() {
    return mySource;
  }

  public IntList getIndexes() {
    return myIndices;
  }

  private class DiffIndexedIterator extends AbstractIntIterator implements IntListIterator {
    private int myNext;
    private boolean myIterated;
    private final IntListIterator myIndexIterator;

    public DiffIndexedIterator(int from, IntListIterator indexIterator) {
      myIndexIterator = indexIterator;
      myNext = from;
    }

    public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
      myIndexIterator.move(offset);
      myNext += offset;
    }

    public int get(int offset) throws NoSuchElementException {
      int index = myIndexIterator.get(offset);
      return mySource.get(index + myNext + offset - 1);
    }

    public int index() throws NoSuchElementException {
      if (!myIterated) throw new NoSuchElementException();
      return myIndexIterator.index();
    }

    public boolean hasNext() throws ConcurrentModificationException, NoSuchElementException {
      return myIndexIterator.hasNext();
    }

    public IntListIterator next() throws ConcurrentModificationException, NoSuchElementException {
      myIndexIterator.next();
      myNext++;
      myIterated = true;
      return this;
    }

    public int value() throws IllegalStateException {
      if (!myIterated) throw new NoSuchElementException();
      int index = myIndexIterator.value() + myNext - 1;
      return mySource.get(index);
    }
  }
}