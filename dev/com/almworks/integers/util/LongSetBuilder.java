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

public final class LongSetBuilder implements Cloneable, LongCollector, LongSortedSet {
  public static final int DEFAULT_TEMP_STORAGE_SIZE = 1024;

  private final int myTempLength;

  @NotNull
  private LongArray mySorted = new LongArray();

  private LongArray myTemp = new LongArray();

  /**
   * Used when merge() is called
   */
  private int[][] myTempInsertionPoints = {null};

  /**
   * When finished, no change possible: the array has passed outside.
   */
  private boolean myFinished;

  private int myModCount = 0;

  public LongSetBuilder() {
    this(DEFAULT_TEMP_STORAGE_SIZE);
  }

  public LongSetBuilder(int tempSize) {
    myTempLength = tempSize;
  }

  public void add(long value) {
    if (myFinished) throw new IllegalStateException();
    if (myTemp.getCapacity() == 0) {
      myTemp = new LongArray(myTempLength);
    }
    if (myTemp.size() == myTempLength)
      mergeTemp();
    assert myTemp.size() < myTempLength;
    myTemp.add(value);
  }

  public void addAll(long... values) {
    addAll(new LongArray(values));
  }

  public void addAll(LongIterator iterator) {
    while (iterator.hasNext())
      add(iterator.nextValue());
  }

  public void addAll(LongList values) {
    int vSize = values.size();
    int startPos = 0;
    int endPos = myTempLength - myTemp.size();
    while (endPos < vSize) {
      myTemp.addAll(values.subList(startPos, endPos));
      mergeTemp();
      startPos = endPos;
      endPos += myTempLength;
    }
    myTemp.addAll(values.subList(startPos, vSize));
  }

  public void mergeFrom(LongSetBuilder other) {
    other.mergeTemp();
    if (other.mySorted.isEmpty())
      return;
    mergeFromSortedCollection(other.mySorted);
  }

  public void mergeFromSortedCollection(LongList other) {
    if (myFinished)
      throw new IllegalStateException();
    if (other.isEmpty())
      return;
    mergeTemp();
    mySorted.merge(other);
  }

  private void mergeTemp() {
    if (myTemp.isEmpty())
      return;
    modified();
    myTemp.sort();
    mySorted.mergeWithSmall(myTemp, myTempInsertionPoints);
    myTemp.clear();
  }

  public LongArray commitToArray() {
    myFinished = true;
    mergeTemp();
    return mySorted;
  }

  /**
   * @return a list of numbers, which should be used before any further mutation of the builder.
   *
   * This method does not finalize the builder.
   */
  public LongList toList() {
    mergeTemp();
    if (mySorted.isEmpty())
      return LongList.EMPTY;
    return mySorted;
  }

  @Override
  public LongArray toArray() {
    return LongArray.copy(toList());
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
    r.myTemp = new LongArray();
    r.myTempInsertionPoints = new int[][]{null};
    r.mySorted = LongArray.copy(mySorted);
    return r;
  }

  public boolean isEmpty() {
    return mySorted.isEmpty() && myTemp.isEmpty();
  }


  public void clear(boolean reuseArrays) {
    mySorted.clear();
    myTemp.clear();
    if (myFinished && !reuseArrays) {
      mySorted = new LongArray();
    }
    myFinished = false;
  }

  private void modified() {
    myModCount++;
  }

  public int size() {
    mergeTemp();
    return mySorted.size();
  }

  public boolean contains(long value) {
    return mySorted.binarySearch(value) >= 0 ||
        myTemp.indexOf(value) != -1;
  }

  @Override
  public boolean containsAll(LongIterable iterable) {
    if (iterable == this) return true;
    for (LongIterator iterator : iterable.iterator()) {
      if (!contains(iterator.value())) return false;
    }
    return true;
  }

  public String toDebugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LongSetBuilder\n");
    builder.append("mySorted: ").append(LongCollections.toBoundedString(mySorted)).append('\n');
    builder.append("myTemp: ").append(LongCollections.toBoundedString(myTemp)).append('\n');
    return builder.toString();
  }

  public StringBuilder toString(StringBuilder builder) {
    builder.append("LSB ").append(size()).append(" [");
    String sep = "";
    LongArray res = LongCollections.collectIterables(mySorted.size() + myTemp.size(), mySorted);
    res.merge(myTemp);
    for  (LongIterator i : res.iterator()) {
      builder.append(sep).append(i.value());
      sep = ", ";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }

  @NotNull
  public LongIterator iterator() {
    mergeTemp();
    return new FailFastLongIterator(mySorted.iterator()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public LongIterator tailIterator(long value) {
    mergeTemp();
    int baseIndex = mySorted.binarySearch(value);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    LongIterator baseIterator = mySorted.iterator(baseIndex, mySorted.size());
    return new FailFastLongIterator(baseIterator) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }
}
