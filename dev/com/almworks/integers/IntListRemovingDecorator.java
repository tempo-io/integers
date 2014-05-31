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

// CODE GENERATED FROM com/almworks/integers/PListRemovingDecorator.tpl




package com.almworks.integers;

/**
 * Removing {@code IntList} decorator that does not alter the given list of removed indices.
 * @see WritableIntListRemovingDecorator
 * @see IntListDiffIndexedDecorator
 * @author igor baltiyskiy
 */
public class IntListRemovingDecorator extends AbstractIntListRemovingDecorator {
  private IntList myRemovedSorted;

  protected IntListRemovingDecorator(IntList base) {
    super(base);
    myRemovedSorted = IntList.EMPTY;
  }

  private IntListRemovingDecorator(IntList base, IntList preparedIndices) {
    super(base);
    myRemovedSorted = preparedIndices;
  }

  /**
   * Indices must be prepared for use by the removing decorator.
   * @see #prepareSortedIndices
   * @see #prepareUnsortedIndices
   */
  public static IntListRemovingDecorator createFromPrepared(IntList base, IntList preparedIndices) {
    return new IntListRemovingDecorator(base, preparedIndices);
  }

  /**
   * Prepares the list of remove indices for use by objects this class. The list must be sorted and contain no duplicates.
   * @param indices a sorted unique list of indices that will be prepared for use by objects of this class
   */
  public static void prepareSortedIndices(WritableIntList indices) {
    prepareSortedIndicesInternal(indices);
  }

  /**
   * Prepares the list of remove indices for use by objects of this class. The list may be empty, unsorted, or contain duplicates.
   * @param removeIndexes a possibly empty, unsorted, or non-unique list of indices. The list is copied internally. After this method returns, the contents of original list are not used by this class.
   * @return prepared list of indices ready to be given to {@link #createFromPrepared(IntList, IntList)}
   */
  public static IntArray prepareUnsortedIndices(int... removeIndexes) {
    return prepareUnsortedIndicesInternal(removeIndexes);
  }


  protected IntList getRemovedPrepared() {
    return myRemovedSorted;
  }


}