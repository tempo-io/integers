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
import java.util.ConcurrentModificationException;

/**
 * Class that allows you to consider the specified sorted unique list as set.
 * Changes in list propagate to this set.
 */
public class IntListSet extends AbstractIntSet implements IntSortedSet {
  protected final IntList myList;
  protected int mySize;
  protected final boolean myIsUnique;

  public static IntListSet setFromSortedUniqueList(IntList sortedUniqueList) {
    assert sortedUniqueList.isSortedUnique();
    return new IntListSet(sortedUniqueList, true);
  }

  public static IntListSet setFromSortedList(IntList sortedList) {
    assert sortedList.isSorted();
    return new IntListSet(sortedList, false);
  }

  private IntListSet(IntList sortedList, boolean isUnique) {
    assert isUnique ? sortedList.isSortedUnique() : sortedList.isSorted();
    myList = sortedList;
    mySize = isUnique ? sortedList.size() : -1;
    myIsUnique = isUnique;
  }


  @Override
  protected void toNativeArrayImpl(int[] dest, int destPos) {
    if (myIsUnique) {
      myList.toNativeArray(0, dest, destPos, myList.size());
    } else {
      super.toNativeArrayImpl(dest, destPos);
    }
  }

  @Override
  public IntIterator tailIterator(int fromElement) {
    int idx = myList.binarySearch(fromElement);
    return iteratorFromIndex(idx >= 0 ? idx : -idx - 1);
  }

  @NotNull
  @Override
  public IntIterator iterator() {
    return iteratorFromIndex(0);
  }

  @NotNull
  protected IntIterator iteratorFromIndex(final int idx) {
    if (idx == myList.size()) {
      return IntIterator.EMPTY;
    }
    final IntIterator iterator = myList.iterator(idx);
    if (myIsUnique) {
      return iterator;
    } else {
      return new IntFindingIterator() {
        private int curIdx;

        @Override
        protected boolean findNext() throws ConcurrentModificationException {
          curIdx = hasValue() ? myList.getNextDifferentValueIndex(curIdx) : idx;

          if (curIdx == myList.size()) {
            return false;
          }
          myNext = myList.get(curIdx);
          return true;
        }
      };
    }
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
    if (mySize == -1) {
      mySize = 0;
      int i = 0;
      while (i != myList.size()) {
        i = myList.getNextDifferentValueIndex(i);
        mySize++;
      }
    }
    return mySize;
  }

  public IntList getList() {
    return myList;
  }
}
