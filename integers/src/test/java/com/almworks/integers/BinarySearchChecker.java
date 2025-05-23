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

import java.util.Random;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED;
import static com.almworks.util.TestUtil.assertEquals;
import static org.junit.Assert.assertTrue;

public class BinarySearchChecker {
  private static void check(BinarySearcher bs, LongArray values) {
    assertTrue(values.isSorted());
    bs.init(values);

    for (int i = 0; i < bs.size(); i++) {
      for (long val = bs.get(i) - 1; val <= bs.get(i) + 1; val++) {
        int res = bs.binSearch(val);
        int res2 = -res - 1;
        if (res >= 0) {
          assertEquals(val, bs.get(res));
        } else {
          if (res2 == 0) {
            assertTrue(val < bs.get(res2));
          } else {
            if (res2 == bs.size()) {
              assertTrue(bs.get(res2 - 1) < val);
            } else {
              assertTrue(bs.get(res2 - 1) <= val && val < bs.get(res2));
            }
          }
        }
      }
    }
  }

  public static void test(Random random, BinarySearcher bs) {
    check(bs, LongArray.create(0, 2, 5, 10));
    check(bs, LongArray.create(0, 5, 10, 11, 12, 14, 20, 25, 25));
    check(bs, LongArray.create(0, 5, 10, 11, 12, 12, 12, 14, 20, 25));
    check(bs, LongArray.create(0, 0, 10, 20, 30));
    check(bs, LongArray.create(10));
    check(bs, LongArray.create(10, 20));
    check(bs, LongArray.create(10, 10));
    check(bs, LongArray.create());

    int arrLength = 100;
    for (IntIterator it : IntIterators.range(50, 400, 50)) {
      LongArray array = IntegersFixture.generateRandomLongArray(random, arrLength, SORTED, it.value());
      check(bs, array);
    }
  }

  public static interface BinarySearcher {
    void init(LongArray values);

    long get(int index);

    int binSearch(long value);

    int size();
  }
}
