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

import static com.almworks.integers.IntegersUtils.*;
import java.util.NoSuchElementException;

public final class LongSetBuilder implements Cloneable, LongCollector, LongIterable {
  public static final int DEFAULT_TEMP_STORAGE_SIZE = 1024;

  private final int myTempLength;

  private long[] mySorted;
  private long[] myTemp;

  /**
   * Used when merge() is called
   */
  private int[][] myTempInsertionPoints = {null};

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
    if (other.mySortedSize == 0)
      return;
    mergeFromSortedCollection(new LongArray(other.mySorted, other.mySortedSize));
  }

  public void mergeFromSortedCollection(LongList other) {
    if (myFinished)
      throw new IllegalStateException();
    if (other.isEmpty())
      return;
    mergeTemp();
    LongArray res = LongCollections.unionWithSameLengthList(mySorted, mySortedSize, other);
    mySortedSize = res.size();
    mySorted = res.extractHostArray();
  }

  private void mergeTemp() {
    if (myTempSize == 0)
      return;
    LongArray res = LongCollections.unionWithSmallArray(mySorted, mySortedSize, myTemp, myTempSize, myTempInsertionPoints);
    mySortedSize = res.size();
    mySorted = res.extractHostArray();
    myTempSize = 0;
  }

  public LongList toSortedCollection() {
    myFinished = true;
    mergeTemp();
    if (mySortedSize == 0)
      return LongList.EMPTY;
    return new LongArray(mySorted, mySortedSize);
  }

  public LongArray toLongArray() {
    myFinished=true;
    mergeTemp();
    return new LongArray(mySorted, mySortedSize);
  }

  /**
   * @return a list of numbers, which should be used before any further mutation of the builder.
   *
   * This method does not finalize the builder.
   */
  public LongList toTemporaryReadOnlySortedCollection() {
    mergeTemp();
    if (mySortedSize == 0)
      return LongList.EMPTY;
    return new AbstractLongList() {
      @Override
      public int size() {
        return mySortedSize;
      }

      @Override
      public long get(int index) throws NoSuchElementException {
        if (index < 0 || index >= mySortedSize) throw new NoSuchElementException("" + index);
        return mySorted[index];
      }
    };
  }

  public long[] toNativeArray() {
    myFinished = true;
    mergeTemp();
    if (mySortedSize == 0)
      return EMPTY_LONGS;
    return LongCollections.arrayCopy(mySorted, 0, mySortedSize);
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

  public boolean contains(long value) {
    return LongCollections.binarySearch(value, mySorted, 0, mySortedSize) >= 0 ||
       LongCollections.indexOf(myTemp, 0, myTempSize, value) != -1;
  }

  @NotNull
  public LongIterator iterator() {
    mergeTemp();
    return new LongArrayIterator(mySorted, 0, mySortedSize);
  }
}
