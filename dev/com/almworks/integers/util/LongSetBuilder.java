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
import static com.almworks.integers.IntegersUtils.*;
import java.util.Arrays;
import java.util.NoSuchElementException;

public final class LongSetBuilder implements Cloneable, LongCollector {
  public static final int DEFAULT_TEMP_STORAGE_SIZE = 1024;

  private final int myTempLength;

  private long[] mySorted;
  private long[] myTemp;

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

  public LongSetBuilder() {
    this(DEFAULT_TEMP_STORAGE_SIZE);
  }

  LongSetBuilder(int tempSize) {
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
    mergeTemp();
    if (other.isEmpty())
      return;
    int otherSize = other.size();
    int totalLength = mySortedSize + otherSize;
    if (mySorted == null || totalLength > mySorted.length) {
      // merge with reallocation
      merge0withReallocation(other, otherSize, totalLength);
    } else {
      merge0inPlace(other, otherSize, totalLength);
    }
  }

  private void merge0inPlace(LongList other, int otherSize, int totalLength) {
    // in place
    // 1. find offset (scan for duplicates)
    // 2. merge starting from the end
    if (mySortedSize > 0 && otherSize > 0 && mySorted[0] > other.get(otherSize - 1)) {
      System.arraycopy(mySorted, 0, mySorted, otherSize, mySortedSize);
      other.toArray(0, mySorted, 0, otherSize);
      mySortedSize = totalLength;
    } else if (mySortedSize > 0 && otherSize > 0 && mySorted[mySortedSize - 1] < other.get(0)) {
      other.toArray(0, mySorted, mySortedSize, otherSize);
      mySortedSize = totalLength;
    } else {
      int insertCount = 0;
      int pi = 0, pj = 0;
      while (pi < mySortedSize && pj < otherSize) {
        long vi = mySorted[pi];
        long vj = other.get(pj);
        if (vi < vj) {
          pi++;
        } else if (vi > vj) {
          pj++;
          insertCount++;
        } else {
          assert vi == vj;
          pi++;
          pj++;
        }
      }
      insertCount += (otherSize - pj);
      pi = mySortedSize - 1;
      pj = otherSize - 1;
      int i = mySortedSize + insertCount;
      while (pi >= 0 && pj >= 0) {
        assert i > pi : i + " " + pi;
        long vi = mySorted[pi];
        long vj = other.get(pj);
        if (vi < vj) {
          mySorted[--i] = vj;
          pj--;
        } else if (vi > vj) {
          mySorted[--i] = vi;
          pi--;
        } else {
          assert vi == vj;
          mySorted[--i] = vi;
          pi--;
          pj--;
        }
      }
      if (pj >= 0) {
        int size = pj + 1;
        other.toArray(0, mySorted, 0, size);
        i -= size;
      } else if (pi >= 0) {
        i -= pi + 1;
      }
      assert i == 0 : i;
      mySortedSize += insertCount;
    }
  }

  private void merge0withReallocation(LongList other, int otherSize, int totalLength) {
    int newSize = Math.max(totalLength, mySorted == null ? 0 : mySorted.length * 2);
    long[] newArray = new long[newSize];
    int pi = 0, pj = 0;
    int i = 0;
    if (mySortedSize > 0 && otherSize > 0) {
      // boundary conditions: quickly merge disjoint sets
      if (mySorted[0] > other.get(otherSize - 1)) {
        other.toArray(0, newArray, 0, otherSize);
        i = pj = otherSize;
      } else if (mySorted[mySortedSize - 1] < other.get(0)) {
        System.arraycopy(mySorted, 0, newArray, 0, mySortedSize);
        i = pi = mySortedSize;
      }
    }
    while (pi < mySortedSize && pj < otherSize) {
      long vi = mySorted[pi];
      long vj = other.get(pj);
      if (vi < vj) {
        newArray[i++] = vi;
        pi++;
      } else if (vi > vj) {
        newArray[i++] = vj;
        pj++;
      } else {
        assert vi == vj;
        newArray[i++] = vi;
        pi++;
        pj++;
      }
    }
    if (pi < mySortedSize) {
      int size = mySortedSize - pi;
      System.arraycopy(mySorted, pi, newArray, i, size);
      i += size;
    } else if (pj < otherSize) {
      int size = otherSize - pj;
      other.toArray(pj, newArray, i, size);
      i += size;
    }
    mySorted = newArray;
    mySortedSize = i;
  }

  private void mergeTemp() {
    if (myTempSize == 0)
      return;
    Arrays.sort(myTemp, 0, myTempSize);
    if (mySorted == null || mySortedSize + myTempSize > mySorted.length) {
      // merge with reallocation
      int oldLength = mySorted == null ? 0 : mySorted.length;
      long[] newSorted = new long[Math.max(mySortedSize + myTempSize, oldLength * 2)];
      long last = 0;
      int destP = 0;
      int sourceP = 0;
      for (int i = 0; i < myTempSize; i++) {
        long v = myTemp[i];
        if (i > 0 && v == last)
          continue;
        last = v;
        if (mySorted != null && sourceP < mySortedSize) {
          int k = LongCollections.binarySearch(v, mySorted, sourceP, mySortedSize);
          if (k >= 0) {
            // found
            continue;
          }
          int insertion = -k - 1;
          if (insertion < sourceP) {
            assert false : insertion + " " + sourceP;
            continue;
          }
          int chunkSize = insertion - sourceP;
          System.arraycopy(mySorted, sourceP, newSorted, destP, chunkSize);
          sourceP = insertion;
          destP += chunkSize;
        }
        newSorted[destP++] = v;
      }
      if (mySorted != null && sourceP < mySortedSize) {
        int chunkSize = mySortedSize - sourceP;
        System.arraycopy(mySorted, sourceP, newSorted, destP, chunkSize);
        destP += chunkSize;
      }
      mySorted = newSorted;
      mySortedSize = destP;
    } else {
      // merge in place
      // a) find insertion points and count how many to be inserted
      if (myTempInsertionPoints == null)
        myTempInsertionPoints = new int[myTempLength];
      Arrays.fill(myTempInsertionPoints, 0, myTempSize, -1);
      int insertCount = 0;
      int sourceP = 0;
      long last = 0;
      for (int i = 0; i < myTempSize; i++) {
        long v = myTemp[i];
        if (i > 0 && v == last) {
          continue;
        }
        last = v;
        if (sourceP < mySortedSize) {
          int k = LongCollections.binarySearch(v, mySorted, sourceP, mySortedSize);
          if (k >= 0) {
            // found
            continue;
          }
          int insertion = -k - 1;
          if (insertion < sourceP) {
            assert false : insertion + " " + sourceP;
            continue;
          }
          sourceP = insertion;
        }
        myTempInsertionPoints[i] = sourceP;
        insertCount++;
      }
      // b) myTempInsertionPoints contains places in the old array for insertion
      // insertCount contains number of insertions
      sourceP = mySortedSize;
      mySortedSize += insertCount;
      int i = myTempSize - 1;
      while (insertCount > 0) {
        // get next to insert
        while (i >= 0 && myTempInsertionPoints[i] == -1)
          i--;
        assert i >= 0 : i;
        int insertion = myTempInsertionPoints[i];
        if (sourceP > insertion) {
          System.arraycopy(mySorted, insertion, mySorted, insertion + insertCount, sourceP - insertion);
          sourceP = insertion;
        }
        mySorted[insertion + insertCount - 1] = myTemp[i];
        insertCount--;
        i--;
      }
    }
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
}
