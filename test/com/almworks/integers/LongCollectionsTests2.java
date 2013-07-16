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

import com.almworks.util.RandomHolder;

import java.util.Arrays;
import java.util.Random;

// todo merge with LongCollectionsTests
public class LongCollectionsTests2 extends IntegersFixture {
  Random r = new RandomHolder().getRandom();

  public void testArrayCopy() {
    long[] arr = {0, 1, 2, 3, 4, 5};
    long[] res = LongCollections.arrayCopy(arr, 2, 3);
    long[] expected = {2, 3, 4};
    CHECK.order(res, expected);

    res = LongCollections.arrayCopy(arr, 2, 0);
    assertEquals(res, IntegersUtils.EMPTY_LONGS);
  }

  public void checkFindDuplicate(long ... values) {
    int result = LongCollections.findDuplicate(LongArray.create(values));
    if (result == -1) {
      for (int i = 0; i < values.length; i++) {
        for (int j = i + 1; j < values.length; j++) {
          if (values[i] == values[j]) {
            fail(Arrays.toString(values));
          }
        }
      }
    } else {
      for (int i = 0; i < values.length; i++) {
        if (result != i && values[i] == values[result])
          return;
      }
      fail(Arrays.toString(values));
    }
  }

  public void testFindDuplicate() {
    checkFindDuplicate(5, 10, 20, 5);
    checkFindDuplicate(2, 4, 6, 8);
    checkFindDuplicate(2, 4, 6, 8, 2);

    int arrLength = 200;
    int maxInt = 400;
    long[] arr = new long[arrLength];
    for (int test = 0; test < 20; test++) {
      for (int i = 0; i < arrLength; i++) {
        arr[i] = r.nextInt(maxInt);
      }
      checkFindDuplicate(arr);
    }

  }

  public void testIsSorted() {
    assertTrue(LongCollections.isSorted(new long[]{Integer.MIN_VALUE, 1, 4, 5, 10, 20, 21, Integer.MAX_VALUE}));
    assertFalse(LongCollections.isSorted(new long[]{1, 4, 5, 20, 19}));

    assertTrue(LongCollections.isSorted(new long[]{1, 4, 5, 20, 19, 15}, 1, 3));
    assertFalse(LongCollections.isSorted(new long[]{1, 4, 3, 20, 19, 15}, 1, 3));

    assertEquals(0, LongCollections.isSortedUnique(true, new long[]{1, 5, 10, 11, 20}, 0, 5));
    assertEquals(-3, LongCollections.isSortedUnique(true, new long[]{1, 5, 5, 10, 15, 19, 19, 100, 121, 121}, 0, 10));
  }

  public void testToSorted() {
    int arrLength = 100;
    int maxVal = 10000;
    LongArray expected;
    LongArray arr = new LongArray(LongProgression.arithmetic(0, arrLength, 0));
    Random r = new RandomHolder().getRandom();

    for (int test = 0; test < 10; test++) {
      for (int i = 0; i < arrLength; i++) {
        arr.set(i, r.nextInt(maxVal));
      }
      expected = LongArray.copy(arr);
      expected.sort();
      CHECK.order(LongCollections.toSorted(false, arr), expected);
      CHECK.order(LongCollections.toSortedNativeArray(arr), expected.toNativeArray());
      expected.sortUnique();
      CHECK.order(LongCollections.toSorted(true, arr), expected);
      CHECK.order(LongCollections.toSortedUnique(arr), expected);
      CHECK.order(LongCollections.toSortedUnique(arr.toNativeArray()), expected);
    }
  }

  public void testBinarySearch() {
    IntegersFixture.testBinarySearch(new BinarySearcher() {
      private long[] arr;
      private int length;

      public void init(long... values) {
        arr = values;
        length = values.length;
      }

      public int size() {
        return length;
      }

      public long get(int index) {
        return arr[index];
      }

      public int binSearch(long value) {
        return LongCollections.binarySearch(value, arr);
      }
    });
  }

  public void testRemoveAllAtSorted() {
    LongArray a = new LongArray(LongProgression.arithmetic(0, 20));
    LongCollections.removeAllAtSorted(a, IntArray.create(0, 3, 4, 7, 10, 11, 12, 13, 19));
    CHECK.order(a.iterator(), 1, 2, 5, 6, 8, 9, 14, 15, 16, 17, 18);
    LongCollections.removeAllAtSorted(a, IntArray.create(1, 2, 3, 4, 9));
    CHECK.order(a.iterator(), 1, 9, 14, 15, 16, 18);
  }


}
