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
        {{0, 1, 2, 3, 4, 5, 6, 7}, {7, 1, 2, 3, 4, 5, 6, 0}, {1, 2, 3, 4, 5, 6}}};
    for (long[][] curCase : cases) {
      LongArray a = new LongArray(curCase[0]);
      LongArray b = new LongArray(curCase[1]);
      LongArray result = new LongArray(curCase[2]);
      CHECK.order(result, getLCS(a, b));
    }
  }

}
