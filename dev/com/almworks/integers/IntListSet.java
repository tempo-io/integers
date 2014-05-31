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

// CODE GENERATED FROM com/almworks/integers/PListSet.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

/**
 * Class that allows you to consider the specified sorted unique list as set.
 * Changes in list propagate to this set.
 */
public class IntListSet extends AbstractIntSet implements IntSortedSet {
  protected final IntList myList;

  public static IntListSet asSet(IntList sortedUniqueList) {
    assert sortedUniqueList.isSortedUnique();
    return new IntListSet(sortedUniqueList);
  }

  private IntListSet(IntList sortedUniqueList) {
    assert sortedUniqueList.isSortedUnique();
    myList = sortedUniqueList;
  }

  @Override
  protected void toNativeArrayImpl(int[] dest, int destPos) {
    myList.toNativeArray(0, dest, destPos, myList.size());
  }

  @Override
  public IntIterator tailIterator(int fromElement) {
    int idx = myList.binarySearch(fromElement);
    if (idx < 0) {
      idx = -idx - 1;
    }
    if (idx == size()) {
      return IntIterator.EMPTY;
    }
    return myList.iterator(idx);
  }

  @Override
  public int getUpperBound() {
    return myList.isEmpty() ? Integer.MIN_VALUE : myList.get(myList.size() - 1);
  }

  @Override
  public int getLowerBound() {
    return myList.isEmpty() ? Integer.MAX_VALUE : myList.get(0);
  }

  @Override
  public boolean contains(int value) {
    return myList.binarySearch(value) >= 0;
  }

  @Override
  public int size() {
    return myList.size();
  }

  @NotNull
  @Override
  public IntIterator iterator() {
    return myList.iterator();
  }
}
