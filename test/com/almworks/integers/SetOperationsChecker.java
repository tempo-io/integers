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

import com.almworks.integers.util.IntegersDebug;

import static com.almworks.integers.LongArray.create;

public class SetOperationsChecker {

  private static final int MIN = Integer.MIN_VALUE;
  private static final int MAX = Integer.MAX_VALUE;
  protected static final CollectionsCompare CHECK = new CollectionsCompare();
  protected static LongArray a(long... values) {
    return new LongArray(values);
  }

  private newSetCreator creator;
  private newSetCreator expected;
  private boolean sortUniqueStatus = true;
  private boolean onlyTwo = true;

  /**
   *
   * @param intersectionLength the number of common values for all arrays
   * @param arraysNumber number of arrays
   * @param maxArrayLength max random length for every array
   * @param isSortUnique
   * @param minMaxValues the min and max values for arrays. There is 4 possible values for minMaxValues.length
*                 <ul><li>0 - for all arrays values {@code min = 0, max = MAX}
*                     <li>1 - for all arrays values {@code min = 0, max = minMaxValues[0]}
*                     <li>2 - for all arrays values {@code min = minMaxValues[0], max = minMaxValues[1]}
*                     <li>arraysNumber * 2 - min and max are contains in minMaxValues and different for all arrays   @return LongArray[arraysNumber]   */
  public static LongArray[] generateRandomArrays(int intersectionLength, int arraysNumber, int maxArrayLength, boolean isSortUnique, int... minMaxValues) {
    final int mLen = minMaxValues.length;
    assert mLen == 0 || mLen == 1 || mLen == 2 || mLen == arraysNumber * 2;
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
    LongArray[] arrays = new LongArray[arraysNumber];

    LongArray intersection = create();
    for ( int i = 0; i < intersectionLength; i++) {
      intersection.add(IntegersFixture.RAND.nextInt());
    }

    for (int i = 0; i < arraysNumber; i++) {
      int arrayLength = IntegersFixture.RAND.nextInt(maxArrayLength);
      arrays[i] = LongArray.copy(intersection);

      for (int j = 0; j < arrayLength; j++) {
        int minValue = mValues[i * 2];
        int maxValue = mValues[i * 2 + 1];
        int diff = maxValue - minValue;
        arrays[i].add(minValue + IntegersFixture.RAND.nextInt(diff));
      }
      if (isSortUnique) {
        arrays[i].sortUnique();
      }
      IntegersDebug.println(arrays[i]);
    }
    return arrays;
  }

  private void checkNewSetCreator(LongArray... arrays) {
    CHECK.order(expected.get(arrays), creator.get(arrays));
    if (arrays.length == 2) {
      CHECK.order(expected.get(arrays[1], arrays[0]), creator.get(arrays[1], arrays[0]));
    }
  }

  public void check(newSetCreator creator, newSetCreator expected, boolean sortUniqueStatus, boolean onlyTwo) {
    this.creator = creator;
    this.expected = expected;
    this.sortUniqueStatus = sortUniqueStatus;
    this.onlyTwo = onlyTwo;
    testSetOperations();
  }

  private void testSetOperations() {
    checkNewSetCreator(a(1, 3, 5), a());
    checkNewSetCreator(a(), a());

    if (!onlyTwo) {
      checkNewSetCreator(a(), a(), a());
      checkNewSetCreator(a(), a(1, 3, 5), a());
    }

    checkNewSetCreator(a(1, 3, 5), a(0));
    checkNewSetCreator(a(1, 3, 5), a(3));
    checkNewSetCreator(a(1, 3, 5), a(1, 3, 5));
    checkNewSetCreator(a(1, 3, 5), a(0, 1, 2, 3, 4, 5, 6));


    checkNewSetCreator(a(1, 2, 3), a(4, 5, 6));
    checkNewSetCreator(a(1, 2, 3, 4, 5, 6), a(4, 5, 6, 7, 8));
    checkNewSetCreator(a(1, 3, 5, 7, 9), a(2, 4, 6, 8, 10));
    checkNewSetCreator(a(1, 3, 5, 7, 9, 11, 15), a(3, 7, 9));
    checkNewSetCreator(a(1, 3, 5, 7, 9), a(1, 9));
    checkNewSetCreator(a(1, 3, 5), a(1, 2, 3, 4, 5, 6));
    checkNewSetCreator(create(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24),
        create(3, 6, 9, 12, 15, 18, 21, 24));

    checkNewSetCreator(a(MIN), a(MIN + 1));
    checkNewSetCreator(a(MAX), a(MAX - 1));
    checkNewSetCreator(a(1, 3, 5), a(MIN));
    checkNewSetCreator(a(1, 3, 5), a(MAX));
    checkNewSetCreator(a(MIN, 1, 3, 5, MAX), a(1, 3, 5));
    checkNewSetCreator(a(MIN, 1, 3, 5, MAX), a(MIN, 3, MAX));

    if (!sortUniqueStatus) {
      checkNewSetCreator(a(10, 5, 1), a(2, 4, 6, 4));
      checkNewSetCreator(a(5, 10, 15), a(10, 9, 8));
      checkNewSetCreator(a(1, 2, 3), a(3, 2, 1));
    }

    testUnionRandom(0, 2, 100, 1000000);
    testUnionRandom(100, 2, 200, 1000000);
    testUnionRandom(50, 2, 500, 10000000);
    testUnionRandom(250, 2, 500, 10000000);
    testUnionRandom(0, 2, 1000, 10000000);
    testUnionRandom(1000, 2, 1000, 10000000);

    // empty intersection
    testUnionRandom(0, 2, 100, 0, 1000, 1100, 2000);
    testUnionRandom(0, 2, 1000, 0, MAX / 2, MAX / 2 + 1, MAX);
    if (!onlyTwo) {
      testUnionRandom(0, 4, 10, 0, 100, 105, 200, 205, 300, 305, 400);
      testUnionRandom(0, 3, 10, 0, 1000, 1005, 2000, 2005, 3000);
    }

    if (!onlyTwo) {
      testUnionRandom(10, 100, 200, 1000000);
      testUnionRandom(10, 100, 200, 1000000);
      testUnionRandom(1, 100, 200, 10000000);
      testUnionRandom(5, 100, 1000, MAX);
      testUnionRandom(5, 100, 1000, 1000000);
      testUnionRandom(0, 1000, 1000, MAX);
    }
  }

  private void testUnionRandom(int intersectionLength, int arraysNumber, int maxArrayLength, int... minMaxValues) {
    LongArray[] arrays = generateRandomArrays(intersectionLength, arraysNumber, maxArrayLength, sortUniqueStatus, minMaxValues);
    checkNewSetCreator(arrays);
  }

  public static interface newSetCreator {
    LongIterator get(LongArray... arrays);
  }

  public static class UnionGetter implements newSetCreator {
    public LongIterator get(LongArray ... arrays) {
      LongArray expected = new LongArray();
      for (LongArray array : arrays) {
        expected.addAll(array);
      }
      expected.sortUnique();
      return expected.iterator();
    }
  }

  public static class IntersectionGetter implements newSetCreator {
    private boolean isSorted;
    public IntersectionGetter(boolean isSorted) {  this.isSorted = isSorted;   }
    public LongIterator get(LongArray ... arrays) {
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
      return expected.iterator();
    }
  }
}
