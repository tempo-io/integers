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

/**
 * A read-only decorator that contains elements from the base list at indices I = (i0, i1, i2, ...).
 * The feature of this decorator is that the list of indices is stored as (i0 - 0, i1 - 1, i2 - 2, ... )
 * so that a run of n successive indices is stored as n equal values.
 * This can be efficiently stored in {@link IntSameValuesList}.
 * <br>
 * This class serves the same purpose as {@link #E#ListRemovingDecorator}.
 * The constructor takes the list of processed indices, you can use
 * {@link #E#ListRemovingDecorator#prepareSortedIndices(WritableIntList)} to generate it.
 * <br>
 * For example: {@code new DiffIndexedDecorator([0, 1, 2, 3, 4, 5], [0, 0, 2, 2]) -> [0, 1, 4, 5]}
 * @see #E#ListRemovingDecorator
 */
public class #E#ListDiffIndexedDecorator extends Abstract#E#List {
  private final #E#List mySource;
  private final IntList myDiffIndices;

  public #E#ListDiffIndexedDecorator(#E#List source, IntList diffIndices) {
    mySource = source;
    myDiffIndices = diffIndices;
  }

  public int size() {
    return myDiffIndices.size();
  }

  public #e# get(int index) {
    return mySource.get(myDiffIndices.get(index) + index);
  }

  public boolean isEmpty() {
    return myDiffIndices.isEmpty();
  }

  @NotNull
  public #E#ListIterator iterator(int from, int to) {
    IntListIterator indexIterator = myDiffIndices.iterator(from, to);
    return new DiffIndexedIterator(from, indexIterator);
  }

  public #E#List getSource() {
    return mySource;
  }

  public IntList getIndices() {
    return myDiffIndices;
  }

  private class DiffIndexedIterator extends Abstract#E#IteratorWithFlag implements #E#ListIterator {
    private int myNext;
    private final IntListIterator myIndexIterator;

    public DiffIndexedIterator(int from, IntListIterator indexIterator) {
      myIndexIterator = indexIterator;
      myNext = from;
    }

    public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
      myIndexIterator.move(offset);
      myNext += offset;
    }

    public #e# get(int offset) throws NoSuchElementException {
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

    @Override
    protected void nextImpl() throws ConcurrentModificationException, NoSuchElementException {
      myIndexIterator.next();
      myNext++;
    }

    @Override
    protected #e# valueImpl() throws NoSuchElementException {
      int index = myIndexIterator.value() + myNext - 1;
      return mySource.get(index);
    }
  }
}
