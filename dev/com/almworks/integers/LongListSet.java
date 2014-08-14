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
public class LongListSet extends AbstractLongSet implements LongSortedSet {
  protected final LongList myList;
  protected int mySize;

  public static LongListSet setFromSortedUniqueList(LongList sortedUniqueList) {
    assert sortedUniqueList.isSortedUnique();
    return new LongListSet(sortedUniqueList, true);
  }

  public static LongListSet setFromSortedList(LongList sortedList) {
    assert sortedList.isSorted();
    return new LongListSet(sortedList, false);
  }

  private LongListSet(LongList sortedUniqueList, boolean isUnique) {
    assert sortedUniqueList.isSortedUnique();
    myList = sortedUniqueList;
    mySize = isUnique ? sortedUniqueList.size() : -1;
  }

  @Override
  protected void toNativeArrayImpl(long[] dest, int destPos) {
    myList.toNativeArray(0, dest, destPos, myList.size());
  }

  @Override
  public LongIterator tailIterator(long fromElement) {
    int idx = myList.binarySearch(fromElement);
    if (idx < 0) {
      idx = -idx - 1;
    }
    if (idx == size()) {
      return LongIterator.EMPTY;
    }
    return myList.iterator(idx);
  }

  @Override
  public long getUpperBound() {
    return myList.isEmpty() ? Long.MIN_VALUE : myList.get(myList.size() - 1);
  }

  @Override
  public long getLowerBound() {
    return myList.isEmpty() ? Long.MAX_VALUE : myList.get(0);
  }

  @Override
  public boolean contains(long value) {
    return myList.binarySearch(value) >= 0;
  }

  @Override
  public int size() {
    if (mySize == -1) {
      mySize = myList.size();
      for (int i = 1; i < myList.size(); i++) {
        if (myList.get(i) == myList.get(i - 1)) {
          mySize--;
        }
      }
    }
    return mySize;
  }

  @NotNull
  @Override
  public LongIterator iterator() {
    return myList.iterator();
  }
}
