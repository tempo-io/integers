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

import static com.almworks.integers.IntegersFixture.*;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.LongArray.create;
import static com.almworks.integers.LongCollections.collectIterables;

/**
 * add {@code -Dcom.almworks.integers.print=true} in VM options to print more information
 * */
public class SetOperationsChecker {

  private static final int MIN = Integer.MIN_VALUE;
  private static final int MAX = Integer.MAX_VALUE;
  protected static final CollectionsCompare CHECK = new CollectionsCompare();
  protected static LongArray a(long... values) {
    return new LongArray(values);
  }

  private SetCreator creator;
  private SetCreator expected;
  private SortedStatus[] sortedStatuses;
  // indicate that set creator can take only 2 arrays
  private boolean myTwoArrays = true;

  /**
   * @param intersectionLength the number of common values for all arrays
   * @param arraysNumber number of arrays
   * @param maxArrayLength max random length for every array
   * @param minMaxValues the min and max values for arrays. There is 4 possible values for minMaxValues.length
   *                 <ul><li>0 - for all arrays values {@code min = 0, max = MAX}
   *                     <li>1 - for all arrays values {@code min = 0, max = minMaxValues[0]}
   *                     <li>2 - for all arrays values {@code min = minMaxValues[0], max = minMaxValues[1]}
   *                     <li>arraysNumber * 2 - min and max are contained in minMaxValues and different for all arrays
   *                     @return LongArray[arraysNumber]   */
  public LongArray[] generateRandomArrays(int intersectionLength, int arraysNumber, int maxArrayLength, int... minMaxValues) {
    final int mLen = minMaxValues.length;
    assert mLen == 0 || mLen == 1 || mLen == 2 || mLen == arraysNumber * 2 : mLen;
    int[] mValues = new int[arraysNumber * 2];
    int min, max;
    if (mLen > 2) {
      mValues = minMaxValues;
    } else {
      for (int i = 0; i < arraysNumber * 2; i += 2) {
        switch (mLen) {
          case 0: min = 0; max = MAX; break;
          case 1: min = 0; max = minMaxValues[0]; break;
          case 2: min = minMaxValues[0]; max = minMaxValues[1]; break;
          default: min = 0; max = -1;
        }
        mValues[i] = min;
        mValues[i + 1] = max;
      }
    }
    LongArray intersection = generateRandomLongArray(intersectionLength, UNORDERED);
    LongArray[] arrays = new LongArray[arraysNumber];
    for (int i = 0; i < arraysNumber; i++) {
      arrays[i] = generateRandomLongArray(RAND.nextInt(maxArrayLength), UNORDERED, mValues[i * 2], mValues[i * 2 + 1]);
      arrays[i].addAll(intersection);
      if (getCurrentStatus(i) == UNORDERED) {
        arrays[i].shuffle(RAND);
      } else {
        getCurrentStatus(i).action(arrays[i]);
      }
    }
    return arrays;
  }

  private void checkNewSetCreator(LongArray... arrays) {
    CHECK.order(creator.get(arrays).iterator(), expected.get(arrays).iterator());
    if (arrays.length == 2 && getCurrentStatus(0) == getCurrentStatus(1)) {
      CHECK.order(creator.get(arrays[1], arrays[0]).iterator(), expected.get(arrays[1], arrays[0]).iterator());
    }
  }

  public void check(SetCreator creator, SetCreator expected, boolean onlyTwo, SortedStatus... statuses) {
    this.creator = creator;
    this.expected = expected;
    assert statuses.length == 1 || statuses.length == 2;
    sortedStatuses = statuses;
    this.myTwoArrays = onlyTwo;
    testSetOperations();
  }

  public SortedStatus getCurrentStatus(int num) {
    if (sortedStatuses.length == 1) {
      return sortedStatuses[0];
    } else {
      if (num == 0 || num == 1) {
        return sortedStatuses[num];
      }
    }
    throw new IllegalArgumentException();
  }



  private void testSetOperations() {
    LongArray someValues = LongArray.create(MIN, MIN + 1, 0, 1, 3, 5, MAX - 1, MAX);

    for (LongArray first : LongCollections.allSubLists(someValues)) {
      for (LongArray second : LongCollections.allSubLists(someValues)) {
        IntegersDebug.println(LongCollections.toBoundedString(first) +
          " " + LongCollections.toBoundedString(second));
        checkNewSetCreator(first, second);
      }
    }
    checkNewSetCreator(create(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24),
      create(3, 6, 9, 12, 15, 18, 21, 24));
    checkNewSetCreator(a(MIN), a(MIN + 1));
    checkNewSetCreator(a(MAX), a(MAX - 1));
    checkNewSetCreator(a(0, 2, 4, 10), a(1, 3, 5, 10));

    if (!myTwoArrays) {
      checkNewSetCreator(a(), a(), a());
      checkNewSetCreator(a(), a(1, 3, 5), a());
      checkNewSetCreator(a(0, 10, 20), a(1, 11, 20), a(2, 12, 20));
    }

    if (getCurrentStatus(0) == UNORDERED && getCurrentStatus(1) == UNORDERED) {
      checkNewSetCreator(a(10, 5, 1), a(2, 4, 6, 4));
      checkNewSetCreator(a(5, 10, 15), a(10, 9, 8));
      checkNewSetCreator(a(1, 2, 3), a(3, 2, 1));
      checkNewSetCreator(a(5, 10, -10, 4, 15), a(4, 1, 7, 5, 9, -1));
      checkNewSetCreator(a(3, -3, 6, -6, 9, -9), a(2, -2, 4, -4, 6, -6));
    }

    if (myTwoArrays) {
      int[][] sizes = {{1, 100}, {10, 1000}, {100, 100}, {50, 100}, {90, 100},
        {100, 110}, {100, 10000}, {1000, 10000}};
      for (int attempt = 0; attempt < 10; attempt++) {
        for (int[] size: sizes) {
          checkNewSetCreator(
            generateRandomLongArray(size[0], getCurrentStatus(0), size[0] * 5),
            generateRandomLongArray(size[1], getCurrentStatus(1), size[1] * 5));
        }
      }
    }

    for (int attempt = 0; attempt < 10; attempt++) {
      testRandom(0, 2, 100, 150);
      testRandom(0, 2, 100, 200);
      testRandom(0, 2, 100, 300);
      testRandom(0, 2, 100, 400);
      testRandom(100, 2, 200, 1000000);
      testRandom(50, 2, 500, 10000000);
      testRandom(250, 2, 500, 10000000);
      testRandom(0, 2, 1000, 10000000);
      testRandom(1000, 2, 1000, 10000000);

      // empty intersection
      testRandom(0, 2, 100, 0, 1000, 1100, 2000);
      testRandom(0, 2, 1000, 0, MAX / 2, MAX / 2 + 1, MAX);

      LongArray first = generateRandomLongArray(1000, SortedStatus.SORTED_UNIQUE);
      LongArray complement = collectIterables(new LongMinusIterator(generateRandomLongArray(1000, SortedStatus.SORTED_UNIQUE), first));
      if (getCurrentStatus(0) == UNORDERED) {
        first.shuffle(RAND);
      }
      if (getCurrentStatus(1) == UNORDERED) {
        complement.shuffle(RAND);
      }
      checkNewSetCreator(first, complement);
    }
    if (!myTwoArrays) {
      testRandom(0, 4, 10, 0, 100, 105, 200, 205, 300, 305, 400);
      testRandom(0, 3, 10, 0, 1000, 1005, 2000, 2005, 3000);

      testRandom(10, 100, 200, 1000000);
      testRandom(10, 100, 200, 1000000);
      testRandom(1, 100, 200, 10000000);
      testRandom(5, 100, 1000, MAX);
      testRandom(5, 100, 1000, 1000000);
      testRandom(0, 1000, 1000, MAX);
    }
  }

  private void testRandom(int intersectionLength, int arraysNumber, int maxArrayLength, int... minMaxValues) {
    LongArray[] arrays = generateRandomArrays(intersectionLength, arraysNumber, maxArrayLength, minMaxValues);
    checkNewSetCreator(arrays);
  }

  public static interface SetCreator {
    LongIterable get(LongArray... arrays);
  }

  public static class UnionGetter implements SetCreator {
    public LongIterable get(LongArray ... arrays) {
      LongArray expected = new LongArray();
      for (LongArray array : arrays) {
        expected.addAll(array);
      }
      expected.sortUnique();
      return expected;
    }
  }

  public static class IntersectionGetter implements SetCreator {
    private boolean isSorted;
    public IntersectionGetter(boolean isSorted) {  this.isSorted = isSorted;   }
    public LongIterable get(LongArray ... arrays) {
      LongArray expected = new LongArray();
      for (int i = 0; i < arrays[0].size(); i++) {
        boolean exist = true;
        long value = arrays[0].get(i);
        for (int j = 1; j < arrays.length && exist; j++) {
          if (isSorted) {
            if (arrays[j].binarySearch(value) < 0) exist = false;
          } else {
            if (!arrays[j].contains(value)) exist = false;
          }
        }
        if (exist) expected.add(value);
      }
      expected.sortUnique();
      return expected;
    }
  }

  public static class MinusGetter implements SetCreator {
    public LongIterable get(LongArray ... arrays) {
      LongArray expected = LongArray.copy(arrays[0]);
      expected.removeAll(arrays[1]);
      return expected;
    }
  }

  public static class DiffGetter implements SetCreator {
    @Override
    public LongIterable get(LongArray... arrays) {
      assert arrays[0].isSortedUnique() && arrays[1].isSortedUnique();
      LongArray expected = new LongArray(arrays[0].size());
      expected.addAll(new LongMinusIterator(arrays[0], arrays[1]));
      expected.addAll(new LongMinusIterator(arrays[1], arrays[0]));
      expected.sortUnique();
      return expected;
    }
  }
}
