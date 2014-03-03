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

/**
 * Decorator on the specified list that allows to remove indices on the fly rather than decorating the already happened removals.
 * @see #E#ListRemovingDecorator
 * @author igor baltiyskiy
 */
public class Writable#E#ListRemovingDecorator extends Abstract#E#ListRemovingDecorator {
  private final WritableIntList myRemovedSorted;

  public Writable#E#ListRemovingDecorator(#E#List base) {
    this(base, new IntArray());
  }

  protected IntList getRemovedPrepared() {
    return myRemovedSorted;
  }

  private Writable#E#ListRemovingDecorator(#E#List base, WritableIntList removed) {
    super(base);
    myRemovedSorted = removed;
  }

  /**
   * Creates an object of this class that decorates removal of some indices from the specified list. The list of indices to remove must be sorted. This list will be used internally, so do not reuse it.
   * @param base the decorated list
   * @param sortedRemoveIndexes collection passed for further use in decorator, don't reuse, don't prepare
   */
  public static Writable#E#ListRemovingDecorator createFromSorted(#E#List base, WritableIntList sortedRemoveIndexes) {
    prepareSortedIndicesInternal(sortedRemoveIndexes);
    return new Writable#E#ListRemovingDecorator(base, sortedRemoveIndexes);
  }

  public void removeAt(int index) {
    int idx = removedBefore(index);
    myRemovedSorted.insert(idx, index);
    for (WritableIntListIterator ii = myRemovedSorted.iterator(idx + 1); ii.hasNext();) {
      int p = ii.nextValue();
      ii.set(0, p - 1);
    }
  }

  /**
   * Creates an object of this class that decorates removal of some indices from the specified list. The list of indices to remove may be unsorted.
   * @param base the decorated list
   * @param removeIndexes list of indices to remove. It is copied internally, so after the method returns, the contents of the list are neither read nor written by the created object.
   * @return
   */
  public static Writable#E#ListRemovingDecorator createFromUnsorted(#E#List base, int... removeIndexes) {
    if (removeIndexes == null || removeIndexes.length == 0)
      return new Writable#E#ListRemovingDecorator(base);
    return new Writable#E#ListRemovingDecorator(base, prepareUnsortedIndicesInternal(removeIndexes));
  }

}
