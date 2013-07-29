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
import com.almworks.util.RandomHolder;
import java.util.Random;

import static com.almworks.integers.LongArray.create;

public class SetOperationsChecker extends IntegersFixture{

  public static LongArray[] generateRandomArrays(int intersectionLength, int arraysNumber, int maxArrayLength, int... minMaxValues) {
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
    Random r = new RandomHolder().getRandom();
    LongArray[] arrays = new LongArray[arraysNumber];

    LongArray intersection = create();
    for ( int i = 0; i < intersectionLength; i++) {
      intersection.add(r.nextInt());
    }

    for (int i = 0; i < arraysNumber; i++) {
      int arrayLength = r.nextInt(maxArrayLength);
      arrays[i] = LongArray.copy(intersection);

      for (int j = 0; j < arrayLength; j++) {
        int minValue = mValues[i * 2];
        int maxValue = mValues[i * 2 + 1];
        int diff = maxValue - minValue;
        arrays[i].add(minValue + r.nextInt(diff));
      }
      arrays[i].sortUnique();
      IntegersDebug.println(arrays[i]);
    }
    return arrays;
  }

  private static void checkNewSetCreator(newSetCreator creator, newSetCreator expected, LongArray... arrays) {
    CHECK.order(expected.get(arrays), creator.get(arrays));
    if (arrays.length == 2) {
      CHECK.order(expected.get(arrays[1], arrays[0]), creator.get(arrays[1], arrays[0]));
    }
  }

  public static void testSetOperations(newSetCreator uc, newSetCreator expected, boolean onlyTwo) {
    checkNewSetCreator(uc, expected, a(1, 3, 5), a());
    checkNewSetCreator(uc, expected, a(), a());

    if (!onlyTwo) {
    checkNewSetCreator(uc, expected, a(), a(), a());
    checkNewSetCreator(uc, expected, a(), a(1, 3, 5), a());
    }

    checkNewSetCreator(uc, expected, a(1, 3, 5), a(0));
    checkNewSetCreator(uc, expected, a(1, 3, 5), a(3));
    checkNewSetCreator(uc, expected, a(1, 3, 5), a(1, 3, 5));
    checkNewSetCreator(uc, expected, a(1, 3, 5), a(0, 1, 2, 3, 4, 5, 6));


    checkNewSetCreator(uc, expected, a(1, 2, 3), a(4, 5, 6));
    checkNewSetCreator(uc, expected, a(1, 2, 3, 4, 5, 6), a(4, 5, 6, 7, 8));
    checkNewSetCreator(uc, expected, a(1, 3, 5, 7, 9), a(2, 4, 6, 8, 10));
    checkNewSetCreator(uc, expected, a(1, 3, 5, 7, 9, 11, 15), a(3, 7, 9));
    checkNewSetCreator(uc, expected, a(1, 3, 5, 7, 9), a(1, 9));
    checkNewSetCreator(uc, expected, a(1, 3, 5), a(1, 2, 3, 4, 5, 6));
    checkNewSetCreator(uc, expected, create(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24),
        create(3, 6, 9, 12, 15, 18, 21, 24));

    checkNewSetCreator(uc, expected, a(MIN), a(MIN + 1));
    checkNewSetCreator(uc, expected, a(MAX), a(MAX - 1));
    checkNewSetCreator(uc, expected, a(1, 3, 5), a(MIN));
    checkNewSetCreator(uc, expected, a(1, 3, 5), a(MAX));
    checkNewSetCreator(uc, expected, a(MIN, 1, 3, 5, MAX), a(1, 3, 5));
    checkNewSetCreator(uc, expected, a(MIN, 1, 3, 5, MAX), a(MIN, 3, MAX));


    testUnionRandom(uc, expected, 0, 100, 2, 1000000);
    testUnionRandom(uc, expected, 100, 200, 2, 1000000);
    testUnionRandom(uc, expected, 50, 500, 2, 10000000);
    testUnionRandom(uc, expected, 250, 500, 2, 10000000);
    testUnionRandom(uc, expected, 0, 1000, 2, 10000000);
    testUnionRandom(uc, expected, 1000, 1000, 2, 10000000);

    // empty intersection
    testUnionRandom(uc, expected, 0, 100, 2, 0, 1000, 1100, 2000);
    testUnionRandom(uc, expected, 0, 1000, 2, 0, MAX / 2, MAX / 2 + 1, MAX);
    if (!onlyTwo) {
      testUnionRandom(uc, expected, 0, 10, 4, 0, 100, 105, 200, 205, 300, 305, 400);
      testUnionRandom(uc, expected, 0, 10, 3, 0, 1000, 1005, 2000, 2005, 3000);
    }

    if (!onlyTwo) {
      testUnionRandom(uc, expected, 10, 200, 100, 1000000);
      testUnionRandom(uc, expected, 10, 200, 100, 1000000);
      testUnionRandom(uc, expected, 1, 200, 100, 10000000);
      testUnionRandom(uc, expected, 5, 1000, 100, MAX);
      testUnionRandom(uc, expected, 5, 1000, 100, 1000000);
      testUnionRandom(uc, expected, 0, 1000, 1000, MAX);
    }
  }

  private static void testUnionRandom(newSetCreator uc, newSetCreator expected, int intersectionLength, int maxArrayLength, int arraysNumber, int ... minMaxValues) {
    LongArray[] arrays = generateRandomArrays(intersectionLength, arraysNumber, maxArrayLength, minMaxValues);
    checkNewSetCreator(uc, expected, arrays);
  }

  public static interface newSetCreator {
    LongIterator get(LongArray... arrays);
  }

  public static class UnionGetter implements newSetCreator{
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
    public LongIterator get(LongArray ... arrays) {
      LongArray expected = new LongArray();
      for (int i = 0; i < arrays[0].size(); i++) {
        boolean exist = true;
        long value = arrays[0].get(i);
        for (int j = 1; j < arrays.length && exist; j++) {
          if (arrays[j].binarySearch(value) < 0) exist = false;
        }
        if (exist) expected.add(value);
      }
      return expected.iterator();
    }
  }
}
