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

import static com.almworks.integers.LongCollections.toSortedUnique;
import static com.almworks.integers.LongLongestCommonSubsequence.getLCS;

public class LongLongestCommonSubsequenceTests extends IntegersFixture {

  public void testSimple() {
    long[][][] cases = {{{1, 2, 3}, {2, 3, 4}, {2, 3}},
      {{1, 2, 3}, {3, 4, 5}, {3}},
      {{1, 2, 3}, {4, 5, 6}, {}},
      {{0, 2, 4, 6, 8, 10, 12}, {0, 3, 6, 9, 12}, {0, 6, 12}},
      {{1, 2, 3, 4, 5}, {-1, 1, -2, 3, -3, 4, 5, -4, -5, -5}, {1, 3, 4, 5}},
      {{1}, {1}, {1}},
      {{1, 2, 3, 4, 5}, {1, 2, 3, 4, 5}, {1, 2, 3, 4, 5}},
      {{1, 2, 3, 4, 5}, {}, {}},
      {{}, {1, 2, 3, 4, 5}, {},},
      {{0, 1, 2, 3}, {3, 1, 2, 0}, {1, 2}},
      {{0, 1, 2, 3, 4, 5, 6, 7}, {7, 1, 2, 3, 4, 5, 6, 0}, {1, 2, 3, 4, 5, 6}},
      {null, null, {}},
      {{}, {}, {}},
      {{1, 2, 3}, {}, {}},
      {{1, 10, 100, 200}, {1, 10, 100, 200}, {1, 10, 100, 200}},
      {{1, 10, 100, 200}, {1, 10, 100, 200, 0}, {1, 10, 100, 200}},
      {{0, 1, 10, 100, 200}, {1, 10, 100, 200}, {1, 10, 100, 200}},
      {{1, 10, 100, 200}, {3, 4, 5, 6}, {}},
      {{1, 10, 100, 200}, {10, 11, 100}, {10, 100}},
      {{10, 11, 12, 13, 14, 15}, {10, 11, 13, -1, 15, 16, 17}, {10, 11, 13, 15}},
      {{10, 11, 12, 13, 14, 15}, {16, 17, 13, -1, 14, 15}, {13, 14, 15}},
      {{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, {10, 9, 8, 6, 7, 5, 4, 3, 2, 1}, {6, 7}}};
    for (long[][] curCase : cases) {
      LongArray a = new LongArray(curCase[0]);
      LongArray b = new LongArray(curCase[1]);
      LongArray result = new LongArray(curCase[2]);
      CHECK.order(result, getLCS(a, b));
    }
  }

  public void testLcsOptimization() {
    long[][][] cases = {{
      {1, 5, 10, 20, 3, 4, 8, 99},
        {20, 8, 4, 1, 5, 10, 3, 99}},
      {{1, 2, 3, 4, 5, 6},
        {3, 2, 4, 1, 6, 5}},
      {in(1, 100),
        join(in(1, 20), in(25, 50), 21, in(51, 80), 81, 85, 84, 83, 82, in(86, 100), 22, 23, 24)},
      {in(1,200),
        join(in(1, 150), in(160, 200), in(151, 159))},
    };
    for (long[][] curCase : cases) {
      LongArray a = new LongArray(curCase[0]);
      LongArray b = new LongArray(curCase[1]);
      checkLcs(a, b);
    }
  }

  public static long[] in(int from, int to) {
    return interval(from, to);
  }

  private long[] join(Object... objects) {
    LongArray array = new LongArray();
    for (Object obj : objects) {
      if (obj instanceof long[]) {
        array.addAll((long[])obj);
      } else if (obj instanceof Long) {
        array.add((Long) obj);
      } else if (obj instanceof Integer) {
        array.add((Integer) obj);
      } else {
        assert false : obj.getClass();
      }
    }
    return array.toNativeArray();
  }

  private void checkLcs(LongArray a, LongArray b) {
    assert (a == null) == (b == null);
    if (a == null) {
      a = generateRandomLongArray(500, SortedStatus.UNORDERED, 100000);
      b = new LongArray(a);
      b.shuffle(myRand);
    }
    assert a.size() == b.size() && toSortedUnique(a).equals(toSortedUnique(b));

    LongList r = LongLongestCommonSubsequence.getLCS(a, b, false);
    LongList rOptimized = LongLongestCommonSubsequence.getLCS(a, b, true);

    CHECK.order(r, rOptimized);
  }

  public void testRandom() {
    for (int i = 0; i < 20; i++) {
      checkLcs(null, null);
    }
  }


}
