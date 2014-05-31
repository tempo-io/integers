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

/**
 * Class that allows you to consider the specified sorted unique list as set.
 * Changes in list propagate to this set.
 */
public class #E#ListSet extends Abstract#E#Set implements #E#SortedSet {
  protected final #E#List myList;

  public static #E#ListSet asSet(#E#List sortedUniqueList) {
    assert sortedUniqueList.isSortedUnique();
    return new #E#ListSet(sortedUniqueList);
  }

  private #E#ListSet(#E#List sortedUniqueList) {
    assert sortedUniqueList.isSortedUnique();
    myList = sortedUniqueList;
  }

  @Override
  protected void toNativeArrayImpl(#e#[] dest, int destPos) {
    myList.toNativeArray(0, dest, destPos, myList.size());
  }

  @Override
  public #E#Iterator tailIterator(#e# fromElement) {
    int idx = myList.binarySearch(fromElement);
    if (idx < 0) {
      idx = -idx - 1;
    }
    if (idx == size()) {
      return #E#Iterator.EMPTY;
    }
    return myList.iterator(idx);
  }

  @Override
  public #e# getUpperBound() {
    return myList.isEmpty() ? #EW#.MIN_VALUE : myList.get(myList.size() - 1);
  }

  @Override
  public #e# getLowerBound() {
    return myList.isEmpty() ? #EW#.MAX_VALUE : myList.get(0);
  }

  @Override
  public boolean contains(#e# value) {
    return myList.binarySearch(value) >= 0;
  }

  @Override
  public int size() {
    return myList.size();
  }

  @NotNull
  @Override
  public #E#Iterator iterator() {
    return myList.iterator();
  }
}
