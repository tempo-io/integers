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

// CODE GENERATED FROM com/almworks/integers/util/ModifyingPListRemovingDecorator.tpl


package com.almworks.integers.util;

import com.almworks.integers.*;

/**
 * Removing decorator for a list of native ints that modifies the list of removed indices with which it works.
 * @author igor baltiyskiy
 */
public class WritableLongListRemovingDecorator extends AbstractLongListRemovingDecorator {
  private final WritableIntList myRemovedSorted;

  public WritableLongListRemovingDecorator(LongList base) {
    this(base, new IntArray());
  }

  protected IntList getRemovedPrepared() {
    return myRemovedSorted;
  }

  private WritableLongListRemovingDecorator(LongList base, WritableIntList removed) {
    super(base);
    myRemovedSorted = removed;
  }

  /**
   * Creates an object of this class that decorates removal of some indices from the specified list. The list of indices to remove must be sorted. This list will be used internally, so do not reuse it.
   * @param base the decorated list
   * @param sortedRemoveIndexes collection passed for further use in decorator, don't reuse, don't prepare
   */
  public static WritableLongListRemovingDecorator createFromSorted(LongList base, WritableIntList sortedRemoveIndexes) {
    prepareSortedIndicesInternal(sortedRemoveIndexes);
    return new WritableLongListRemovingDecorator(base, sortedRemoveIndexes);
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
  public static WritableLongListRemovingDecorator createFromUnsorted(LongList base, int... removeIndexes) {
    if (removeIndexes == null || removeIndexes.length == 0)
      return new WritableLongListRemovingDecorator(base);
    return new WritableLongListRemovingDecorator(base, prepareUnsortedIndicesInternal(removeIndexes));
  }

}
