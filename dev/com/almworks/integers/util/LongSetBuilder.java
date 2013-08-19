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

// CODE GENERATED FROM com/almworks/integers/util/PSetBuilder.tpl


package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.almworks.integers.IntegersUtils.*;

public final class LongSetBuilder implements Cloneable, LongCollector, LongIterable {
  public static final int DEFAULT_TEMP_STORAGE_SIZE = 1024;

  private final int myTempLength;

  private LongArray mySorted = new LongArray();
  private long[] myTemp;

  /**
   * Used when merge() is called
   */
  private int[][] myTempInsertionPoints = {null};

  /**
   * myTemp contains valid ids on [0, myTempSize)
   */
  private int myTempSize;

  /**
   * When finished, no change possible: the array has passed outside.
   */
  private boolean myFinished;

  public LongSetBuilder() {
    this(DEFAULT_TEMP_STORAGE_SIZE);
  }

  public LongSetBuilder(int tempSize) {
    myTempLength = tempSize;
  }

  public void add(long value) {
    if (myFinished)
      throw new IllegalStateException();
    if (myTemp == null) {
      myTemp = new long[myTempLength];
    }
    if (myTempSize == myTempLength)
      mergeTemp();
    assert myTempSize < myTempLength;
    myTemp[myTempSize++] = value;
  }

  public void addAll(long... values) {
    for (long value : values) {
      add(value);
    }
  }

  public void addAll(LongIterator iterator) {
    while (iterator.hasNext())
      add(iterator.nextValue());
  }

  public void addAll(LongList values) {
    addAll(values.iterator());
  }

  public void mergeFrom(LongSetBuilder other) {
    other.mergeTemp();
    if (other.mySorted.size() == 0)
      return;
    mergeFromSortedCollection(other.mySorted);
  }

  public void mergeFromSortedCollection(LongList other) {
    if (myFinished)
      throw new IllegalStateException();
    if (other.isEmpty())
      return;
    mergeTemp();
    mySorted.unionWithSameLengthList(other);
  }

  private void mergeTemp() {
    if (myTempSize == 0)
      return;
    Arrays.sort(myTemp, 0, myTempSize);
    mySorted.unionWithSmallArray(new LongArray(myTemp, myTempSize), myTempInsertionPoints);
    myTempSize = 0;
  }

  public LongList toSortedCollection() {
    myFinished = true;
    mergeTemp();
    if (mySorted == null || mySorted.size() == 0)
      return LongList.EMPTY;
    return mySorted;
  }

  public LongArray toLongArray() {
    myFinished=true;
    mergeTemp();
    return mySorted;
  }

  /**
   * @return a list of numbers, which should be used before any further mutation of the builder.
   *
   * This method does not finalize the builder.
   */
  public LongList toTemporaryReadOnlySortedCollection() {
    mergeTemp();
    if (mySorted.size() == 0)
      return LongList.EMPTY;
    return mySorted;
  }

  public long[] toNativeArray() {
    myFinished = true;
    mergeTemp();
    if (mySorted.size() == 0)
      return EMPTY_LONGS;
    return mySorted.toNativeArray();//LongCollections.arrayCopy(mySorted, 0, mySorted.size());
  }

  public LongSetBuilder clone() {
    mergeTemp();
    LongSetBuilder r;
    try {
      r = (LongSetBuilder) super.clone();
    } catch (Exception e) {
      throw new Error(e);
    }
    r.myFinished = false;
    r.myTemp = null;
    r.myTempInsertionPoints = new int[][]{null};
    if (mySorted != null) { // fix it
      r.mySorted = LongArray.copy(mySorted);
    }
    return r;
  }

  public boolean isEmpty() {
    return mySorted.size() + myTempSize == 0;
  }

  public void clear(boolean reuseArrays) {
    mySorted.clear();
    myTempSize = 0;
    if (myFinished && !reuseArrays) {
      mySorted = null;
    }
    myFinished = false;
  }

  public int size() {
    mergeTemp();
    return mySorted.size();
  }

  public boolean contains(long value) {
    return mySorted.binarySearch(value) >= 0 ||
       LongCollections.indexOf(myTemp, 0, myTempSize, value) != -1;
  }

  @NotNull
  public LongIterator iterator() {
    mergeTemp();
    return mySorted.iterator();
  }
}