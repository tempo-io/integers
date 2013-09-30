/*
 * Copyright 2013 ALM Works Ltd
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

import static com.almworks.integers.LongArray.create;

public class SetOperationsChecker {

  private static final int MIN = Integer.MIN_VALUE;
  private static final int MAX = Integer.MAX_VALUE;
  protected static final CollectionsCompare CHECK = new CollectionsCompare();
  protected static LongArray a(long... values) {
    return new LongArray(values);
  }

  private SetCreator creator;
  private SetCreator expected;
  private boolean sortUniqueStatus = true;
  // indicate that set creator can take only 2 arrays
  private boolean myTwoArrays = true;

  private static LongArray getSubList(LongList values, int mask) {
    assert (mask + 1) <= (1 << values.size()) : mask + " " + (1 << values.size());
    LongArray res = new LongArray();
    for (int i = 0, n = values.size(); i < n; i++) {
      if ((mask & (1 << i)) != 0) {
        res.add(values.get(i));
      }
    }
    return res;
  }

  /**
   * @param intersectionLength the number of common values for all arrays
   * @param arraysNumber number of arrays
   * @param maxArrayLength max random length for every array
   * @param isSortUnique
   * @param minMaxValues the min and max values for arrays. There is 4 possible values for minMaxValues.length
   *                 <ul><li>0 - for all arrays values {@code min = 0, max = MAX}
   *                     <li>1 - for all arrays values {@code min = 0, max = minMaxValues[0]}
   *                     <li>2 - for all arrays values {@code min = minMaxValues[0], max = minMaxValues[1]}
   *                     <li>arraysNumber * 2 - min and max are contained in minMaxValues and different for all arrays
   *                     @return LongArray[arraysNumber]   */
  public static LongArray[] generateRandomArrays(int intersectionLength, int arraysNumber, int maxArrayLength, boolean isSortUnique, int... minMaxValues) {
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
          default: min = 0; max = 10;
        }
        mValues[i] = min;
        mValues[i + 1] = max;
      }
    }
    LongArray intersection = IntegersFixture.generateRandomArray(intersectionLength, isSortUnique);
    LongArray[] arrays = new LongArray[arraysNumber];
    for (int i = 0; i < arraysNumber; i++) {
      arrays[i] = IntegersFixture.generateRandomArray(IntegersFixture.RAND.nextInt(maxArrayLength), false, mValues[i * 2], mValues[i * 2 + 1]);
      arrays[i].addAll(intersection);
      if (isSortUnique) {
        arrays[i].sortUnique();
      }
    }
    return arrays;
  }

  private void checkNewSetCreator(LongArray... arrays) {
    CHECK.order(expected.get(arrays).iterator(), creator.get(arrays).iterator());
    if (arrays.length == 2) {
      CHECK.order(expected.get(arrays[1], arrays[0]).iterator(), creator.get(arrays[1], arrays[0]).iterator());
    }
  }

  public void check(SetCreator creator, SetCreator expected, boolean sortUniqueStatus, boolean onlyTwo) {
    this.creator = creator;
    this.expected = expected;
    this.sortUniqueStatus = sortUniqueStatus;
    this.myTwoArrays = onlyTwo;
    testSetOperations();
  }

  private void testSetOperations() {

    LongArray someValues = LongArray.create(MIN, -10, 1, 3, 5, 6, MAX);
    LongArray first, second;
    for (int i = 0; i < (1 << someValues.size()); i++) {
      for (int j = 0; j < (1 << someValues.size()); j++) {
        first = getSubList(someValues, i);
        second = getSubList(someValues, j);
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
    if (!sortUniqueStatus) {
      checkNewSetCreator(a(10, 5, 1), a(2, 4, 6, 4));
      checkNewSetCreator(a(5, 10, 15), a(10, 9, 8));
      checkNewSetCreator(a(1, 2, 3), a(3, 2, 1));
    }

    testRandom(0, 2, 100, 1000000);
    testRandom(100, 2, 200, 1000000);
    testRandom(50, 2, 500, 10000000);
    testRandom(250, 2, 500, 10000000);
    testRandom(0, 2, 1000, 10000000);
    testRandom(1000, 2, 1000, 10000000);

    // empty intersection
    testRandom(0, 2, 100, 0, 1000, 1100, 2000);
    testRandom(0, 2, 1000, 0, MAX / 2, MAX / 2 + 1, MAX);
    if (!myTwoArrays) {
      testRandom(0, 4, 10, 0, 100, 105, 200, 205, 300, 305, 400);
      testRandom(0, 3, 10, 0, 1000, 1005, 2000, 2005, 3000);
    }

    if (!myTwoArrays) {
      testRandom(10, 100, 200, 1000000);
      testRandom(10, 100, 200, 1000000);
      testRandom(1, 100, 200, 10000000);
      testRandom(5, 100, 1000, MAX);
      testRandom(5, 100, 1000, 1000000);
      testRandom(0, 1000, 1000, MAX);
    }
  }

  private void testRandom(int intersectionLength, int arraysNumber, int maxArrayLength, int... minMaxValues) {
    LongArray[] arrays = generateRandomArrays(intersectionLength, arraysNumber, maxArrayLength, sortUniqueStatus, minMaxValues);
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
      return expected;
    }
  }

  public static class MinusGetter implements SetCreator {
    public LongIterable get(LongArray ... arrays) {
      LongArray expected = new LongArray(arrays[0]);
      expected.removeAll(arrays[1]);
      return expected;
    }
  }
}
