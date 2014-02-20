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



package com.almworks.integers;

import java.util.NoSuchElementException;

import static com.almworks.integers.IntegersUtils.EMPTY_INTS;

public final class IntSetBuilder implements Cloneable, IntCollector {
  public static final int DEFAULT_TEMP_STORAGE_SIZE = 1024;

  private final int myTempLength;

  private int[] mySorted;
  private int[] myTemp;

  /**
   * Used when merge() is called
   */
  private int[] myTempInsertionPoints;

  /**
   * mySorted contains valid ids on [0, mySortedSize)
   */
  private int mySortedSize;

  /**
   * myTemp contains valid ids on [0, myTempSize)
   */
  private int myTempSize;

  /**
   * When finished, no change possible: the array has passed outside.
   */
  private boolean myFinished;

  public IntSetBuilder() {
    this(DEFAULT_TEMP_STORAGE_SIZE);
  }

  public IntSetBuilder(int tempSize) {
    myTempLength = tempSize;
  }

  public void add(int value) {
    if (myFinished)
      throw new IllegalStateException();
    if (myTemp == null) {
      myTemp = new int[myTempLength];
    }
    if (myTempSize == myTempLength)
      mergeTemp();
    assert myTempSize < myTempLength;
    myTemp[myTempSize++] = value;
  }

  public void addAll(int... values) {
    for (int value : values) {
      add(value);
    }
  }

  public void addAll(IntIterator iterator) {
    while (iterator.hasNext())
      add(iterator.nextValue());
  }

  public void addAll(IntList values) {
    addAll(values.iterator());
  }

  public void mergeFrom(IntSetBuilder other) {
    if (other.mySortedSize == 0)
      return;
    other.mergeTemp();
    mergeFromSortedCollection(new IntArray(other.mySorted, other.mySortedSize));
  }

  public void mergeFromSortedCollection(IntList other) {
    if (myFinished)
      throw new IllegalStateException();
    if (other.isEmpty())
      return;
    mergeTemp();
    IntArray res = IntCollections.unionWithSameLengthList(mySorted, mySortedSize, other);
    mySortedSize = res.size();
    mySorted = res.extractHostArray();
  }

  private void mergeTemp() {
    if (myTempInsertionPoints == null)
      myTempInsertionPoints = new int[myTempLength];
    IntArray res = IntCollections.unionWithSmallArray(mySorted, mySortedSize, myTemp, myTempSize, myTempInsertionPoints);
    mySortedSize = res.size();
    mySorted = res.extractHostArray();
    myTempSize = 0;
  }

  public IntList toSortedCollection() {
    myFinished = true;
    mergeTemp();
    if (mySortedSize == 0)
      return IntList.EMPTY;
    return new IntArray(mySorted, mySortedSize);
  }

  public IntArray toIntArray() {
    myFinished = true;
    mergeTemp();
    return new IntArray(mySorted, mySortedSize);
  }

  /**
   * @return a list of numbers, which should be used before any further mutation of the builder.
   *
   * This method does not finalize the builder.
   */
  public IntList toTemporaryReadOnlySortedCollection() {
    mergeTemp();
    if (mySortedSize == 0)
      return IntList.EMPTY;
    return new AbstractIntList() {
      @Override
      public int size() {
        return mySortedSize;
      }

      @Override
      public int get(int index) throws NoSuchElementException {
        if (index < 0 || index >= mySortedSize) throw new NoSuchElementException("" + index);
        return mySorted[index];
      }
    };
  }

  public int[] toNativeArray() {
    myFinished = true;
    mergeTemp();
    if (mySortedSize == 0)
      return EMPTY_INTS;
    return IntCollections.arrayCopy(mySorted, 0, mySortedSize);
  }

  public IntSetBuilder clone() {
    mergeTemp();
    IntSetBuilder r;
    try {
      r = (IntSetBuilder) super.clone();
    } catch (Exception e) {
      throw new Error(e);
    }
    r.myFinished = false;
    r.myTemp = null;
    r.myTempInsertionPoints = null;
    if (r.mySorted != null) {
      r.mySorted = r.mySorted.clone();
    }
    return r;
  }

  public boolean isEmpty() {
    return mySortedSize + myTempSize == 0;
  }

  public void clear(boolean reuseArrays) {
    mySortedSize = 0;
    myTempSize = 0;
    if (myFinished && !reuseArrays) {
      mySorted = null;
    }
    myFinished = false;
  }

  public int size() {
    mergeTemp();
    return mySortedSize;
  }
}
