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

// CODE GENERATED FROM com/almworks/integers/PSetBuilder.tpl




package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public final class IntSetBuilder extends AbstractIntSet implements Cloneable, IntCollector, IntSortedSet {
  public static final int DEFAULT_TEMP_STORAGE_SIZE = 1024;

  private final int myTempLength;

  @NotNull
  private IntArray mySorted = new IntArray();

  private IntArray myTemp = new IntArray();

  /**
   * Used when merge() is called
   */
  private int[][] myTempInsertionPoints = {null};

  /**
   * When finished, no change possible: the array has passed outside.
   */
  private boolean myFinished;

  private int myModCount = 0;

  public IntSetBuilder() {
    this(DEFAULT_TEMP_STORAGE_SIZE);
  }

  public IntSetBuilder(int tempSize) {
    myTempLength = tempSize;
  }

  public void add(int value) {
    if (myFinished) throw new IllegalStateException();
    if (myTemp.getCapacity() == 0) {
      myTemp = new IntArray(myTempLength);
    }
    if (myTemp.size() == myTempLength)
      coalesce();
    assert myTemp.size() < myTempLength;
    myTemp.add(value);
  }

  public void addAll(int... values) {
    addAll(new IntArray(values));
  }

  public void addAll(IntIterable iterable) {
    for (IntIterator it : iterable) {
      add(it.value());
    }
  }

  public void addAll(IntList values) {
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

  public void mergeFrom(IntSetBuilder other) {
    other.coalesce();
    if (other.mySorted.isEmpty())
      return;
    mergeFromSortedCollection(other.mySorted);
  }

  public void mergeFromSortedCollection(IntList other) {
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

  public IntArray commitToArray() {
    myFinished = true;
    coalesce();
    return mySorted;
  }

  /**
   * This method does not finalize the builder.
   * @return sorted list of set elements, which should be used before any further mutation of the builder.
   */
  public IntList toList() {
    coalesce();
    if (mySorted.isEmpty()) {
      return IntList.EMPTY;
    }
    return mySorted;
  }

  @Override
  public void toNativeArrayImpl(int[] dest, int destPos) {
    coalesce();
    mySorted.toNativeArray(0, dest, destPos, size());
  }

  public IntSetBuilder clone() {
    coalesce();
    IntSetBuilder r;
    try {
      r = (IntSetBuilder) super.clone();
    } catch (Exception e) {
      throw new Error(e);
    }
    r.myFinished = false;
    r.myTemp = new IntArray();
    r.myTempInsertionPoints = new int[][]{null};
    r.mySorted = IntArray.copy(mySorted);
    return r;
  }

  public boolean isEmpty() {
    return mySorted.isEmpty() && myTemp.isEmpty();
  }

  public void clear(boolean reuseArrays) {
    mySorted.clear();
    myTemp.clear();
    if (myFinished && !reuseArrays) {
      mySorted = new IntArray();
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

  public boolean contains(int value) {
    return mySorted.binarySearch(value) >= 0 || myTemp.contains(value);
  }

  public String toDebugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("IntSetBuilder\n");
    builder.append("mySorted: ").append(IntCollections.toBoundedString(mySorted)).append('\n');
    builder.append("myTemp: ").append(IntCollections.toBoundedString(myTemp)).append('\n');
    return builder.toString();
  }

  @NotNull
  public IntIterator iterator() {
    coalesce();
    return new IntFailFastIterator(mySorted.iterator()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public IntIterator tailIterator(int value) {
    coalesce();
    int baseIndex = mySorted.binarySearch(value);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    IntIterator baseIterator = mySorted.iterator(baseIndex, mySorted.size());
    return new IntFailFastIterator(baseIterator) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  @Override
  public int getUpperBound() {
    int val = Integer.MIN_VALUE;
    if (!mySorted.isEmpty()) {
      val = mySorted.get(mySorted.size() - 1);
    }
    for (int i = 0; i < myTemp.size(); i++) {
      val = Math.max(i, myTemp.get(i));
    }
    return val;
  }

  @Override
  public int getLowerBound() {
    int val = Integer.MAX_VALUE;
    if (!mySorted.isEmpty()) {
      val = mySorted.get(0);
    }
    for (int i = 0; i < myTemp.size(); i++) {
      val = Math.min(i, myTemp.get(i));
    }
    return val;

  }
}
