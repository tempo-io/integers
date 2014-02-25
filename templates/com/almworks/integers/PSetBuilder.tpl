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

public final class #E#SetBuilder extends Abstract#E#Set implements Cloneable, #E#Collector, #E#SortedSet {
  public static final int DEFAULT_TEMP_STORAGE_SIZE = 1024;

  private final int myTempLength;

  @NotNull
  private #E#Array mySorted = new #E#Array();

  private #E#Array myTemp = new #E#Array();

  /**
   * Used when merge() is called
   */
  private int[][] myTempInsertionPoints = {null};

  /**
   * When finished, no change possible: the array has passed outside.
   */
  private boolean myFinished;

  private int myModCount = 0;

  public #E#SetBuilder() {
    this(DEFAULT_TEMP_STORAGE_SIZE);
  }

  public #E#SetBuilder(int tempSize) {
    myTempLength = tempSize;
  }

  public void add(#e# value) {
    if (myFinished) throw new IllegalStateException();
    if (myTemp.getCapacity() == 0) {
      myTemp = new #E#Array(myTempLength);
    }
    if (myTemp.size() == myTempLength)
      coalesce();
    assert myTemp.size() < myTempLength;
    myTemp.add(value);
  }

  public void addAll(#e#... values) {
    addAll(new #E#Array(values));
  }

  public void addAll(#E#Iterable iterable) {
    for (#E#Iterator it : iterable) {
      add(it.value());
    }
  }

  public void addAll(#E#List values) {
    int vSize = values.size();
    int startPos = 0;
    int endPos = myTempLength - myTemp.size();
    while (endPos < vSize) {
      myTemp.addAll(values.subList(startPos, endPos));
      coalesce();
      startPos = endPos;
      endPos += myTempLength;
    }
    myTemp.addAll(values.subList(startPos, vSize));
  }

  public void mergeFrom(#E#SetBuilder other) {
    other.coalesce();
    if (other.mySorted.isEmpty())
      return;
    mergeFromSortedCollection(other.mySorted);
  }

  public void mergeFromSortedCollection(#E#List other) {
    if (myFinished)
      throw new IllegalStateException();
    if (other.isEmpty())
      return;
    coalesce();
    mySorted.merge(other);
  }

  void coalesce() {
    if (myTemp.isEmpty())
      return;
    modified();
    myTemp.sort();
    mySorted.mergeWithSmall(myTemp, myTempInsertionPoints);
    myTemp.clear();
  }

  public #E#Array commitToArray() {
    myFinished = true;
    coalesce();
    return mySorted;
  }

  /**
   * This method does not finalize the builder.
   * @return sorted list of set elements, which should be used before any further mutation of the builder.
   */
  public #E#List toList() {
    coalesce();
    if (mySorted.isEmpty()) {
      return #E#List.EMPTY;
    }
    return mySorted;
  }

  @Override
  public void toNativeArrayImpl(#e#[] dest, int destPos) {
    coalesce();
    mySorted.toNativeArray(0, dest, destPos, size());
  }

  public #E#SetBuilder clone() {
    coalesce();
    #E#SetBuilder r;
    try {
      r = (#E#SetBuilder) super.clone();
    } catch (Exception e) {
      throw new Error(e);
    }
    r.myFinished = false;
    r.myTemp = new #E#Array();
    r.myTempInsertionPoints = new int[][]{null};
    r.mySorted = #E#Array.copy(mySorted);
    return r;
  }

  public boolean isEmpty() {
    return mySorted.isEmpty() && myTemp.isEmpty();
  }

  public void clear(boolean reuseArrays) {
    mySorted.clear();
    myTemp.clear();
    if (myFinished && !reuseArrays) {
      mySorted = new #E#Array();
    }
    myFinished = false;
  }

  private void modified() {
    myModCount++;
  }

  public int size() {
    coalesce();
    return mySorted.size();
  }

  public boolean contains(#e# value) {
    return mySorted.binarySearch(value) >= 0 || myTemp.contains(value);
  }

  public String toDebugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("#E#SetBuilder\n");
    builder.append("mySorted: ").append(#E#Collections.toBoundedString(mySorted)).append('\n');
    builder.append("myTemp: ").append(#E#Collections.toBoundedString(myTemp)).append('\n');
    return builder.toString();
  }

  @NotNull
  public #E#Iterator iterator() {
    coalesce();
    return new #E#FailFastIterator(mySorted.iterator()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public #E#Iterator tailIterator(#e# value) {
    coalesce();
    int baseIndex = mySorted.binarySearch(value);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    #E#Iterator baseIterator = mySorted.iterator(baseIndex, mySorted.size());
    return new #E#FailFastIterator(baseIterator) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  @Override
  public #e# getUpperBound() {
    #e# val = #EW#.MIN_VALUE;
    if (!mySorted.isEmpty()) {
      val = mySorted.get(mySorted.size() - 1);
    }
    for (int i = 0; i < myTemp.size(); i++) {
      val = Math.max(i, myTemp.get(i));
    }
    return val;
  }

  @Override
  public #e# getLowerBound() {
    #e# val = #EW#.MAX_VALUE;
    if (!mySorted.isEmpty()) {
      val = mySorted.get(0);
    }
    for (int i = 0; i < myTemp.size(); i++) {
      val = Math.min(i, myTemp.get(i));
    }
    return val;

  }
}
