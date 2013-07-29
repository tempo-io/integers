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

import static com.almworks.util.TestUtil.assertEquals;
import static junit.framework.Assert.assertTrue;

public class BinarySearchChecker {
  private static void check(BinarySearcher bs, long... values) {
    assertTrue(LongArray.create(values).isSorted());
    bs.init(values);

    for (int index = 0; index < bs.size(); index++) {
      for (long i = bs.get(index) - 1; i <= bs.get(index) + 1; i++) {
        int res = bs.binSearch(i);
        int res2 = -res - 1;
        if (res >= 0) {
          assertEquals(i, bs.get(res));
        } else {
          if (res2 == 0) {
            assertTrue(i < bs.get(res2));
          } else {
            if (res2 == bs.size()) {
              assertTrue(bs.get(res2 - 1) < i);
            } else {
              assertTrue(bs.get(res2 - 1) <= i && i < bs.get(res2));
            }
          }
        }
      }
    }
  }

  public static void test(BinarySearcher bs) {
    check(bs, 0, 2, 5, 10);
    check(bs, 0, 5, 10, 11, 12, 14, 20, 25, 25);

    int arrLength = 100;
    long[] arr = new long[arrLength];
    Random r = new RandomHolder().getRandom();

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < arrLength; j++) {
        arr[j] = r.nextInt();
      }
      Arrays.sort(arr);
      check(bs, arr);
    }
  }

  public static interface BinarySearcher {
    void init(long... values);

    long get(int index);

    int binSearch(long value);

    int size();
  }
}
