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

public final class LongSetBuilder extends AbstractLongSet implements Cloneable, LongCollector, LongSortedSet {
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
      coalesce();
    assert myTemp.size() < myTempLength;
    myTemp.add(value);
  }

  public void addAll(long... values) {
    addAll(new LongArray(values));
  }

  public void addAll(LongIterable iterable) {
    for (LongIterator it : iterable) {
      add(it.value());
    }
  }

  public void addAll(LongList values) {
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

  public void mergeFrom(LongSetBuilder other) {
    other.coalesce();
    if (other.mySorted.isEmpty())
      return;
    mergeFromSortedCollection(other.mySorted);
  }

  public void mergeFromSortedCollection(LongList other) {
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

  public LongArray commitToArray() {
    myFinished = true;
    coalesce();
    return mySorted;
  }

  /**
   * This method does not finalize the builder.
   * @return sorted list of set elements, which should be used before any further mutation of the builder.
   */
  public LongList toList() {
    coalesce();
    if (mySorted.isEmpty()) {
      return LongList.EMPTY;
    }
    return mySorted;
  }

  @Override
  public void toNativeArrayImpl(long[] dest, int destPos) {
    coalesce();
    mySorted.toNativeArray(0, dest, destPos, size());
  }

  public LongSetBuilder clone() {
    coalesce();
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
    coalesce();
    return mySorted.size();
  }

  public boolean contains(long value) {
    return mySorted.binarySearch(value) >= 0 || myTemp.contains(value);
  }

  public String toDebugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LongSetBuilder\n");
    builder.append("mySorted: ").append(LongCollections.toBoundedString(mySorted)).append('\n');
    builder.append("myTemp: ").append(LongCollections.toBoundedString(myTemp)).append('\n');
    return builder.toString();
  }

  @NotNull
  public LongIterator iterator() {
    coalesce();
    return new LongFailFastIterator(mySorted.iterator()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public LongIterator tailIterator(long value) {
    coalesce();
    int baseIndex = mySorted.binarySearch(value);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    LongIterator baseIterator = mySorted.iterator(baseIndex, mySorted.size());
    return new LongFailFastIterator(baseIterator) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  @Override
  public long getUpperBound() {
    long val = Long.MIN_VALUE;
    if (!mySorted.isEmpty()) {
      val = mySorted.get(mySorted.size() - 1);
    }
    for (int i = 0; i < myTemp.size(); i++) {
      val = Math.max(i, myTemp.get(i));
    }
    return val;
  }

  @Override
  public long getLowerBound() {
    long val = Long.MAX_VALUE;
    if (!mySorted.isEmpty()) {
      val = mySorted.get(0);
    }
    for (int i = 0; i < myTemp.size(); i++) {
      val = Math.min(i, myTemp.get(i));
    }
    return val;

  }

  @Override
  public int hashCode() {
    coalesce();
    int h = 0;
    for (int i = 0, size = mySorted.size(); i < size; i++) {
      h += IntegersUtils.hash(mySorted.get(i));
    }
    return h;
  }
}
