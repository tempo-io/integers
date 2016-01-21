/*
 * Copyright 2016 ALM Works Ltd
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

import java.util.Arrays;

import static com.almworks.integers.#E#Collections.toBoundedString;

public class #E#LongestIncreasingSubsequence {
  /**
   * @param a shouldn't contain duplicates
   * @return longest increasing subsequence for a
   */
  public static #E#List getLIS(#E#List a) {
    assert #E#Collections.toSortedUnique(a).size() == a.size() : "duplicates aren't allowed in a:" + toBoundedString(a);
    int sz = a.size() + 1;
    // increasing subseq of a with length i ends with a.get(ids[i])
    int[] ids = new int[sz];
    // increasing subseq of a with length i ends with d[i], we can get rid of d
    #e#[] d = new #e#[sz];
    int[] prev = new int[sz];
    Arrays.fill(d, #EW#.MAX_VALUE);
    d[0] = #EW#.MIN_VALUE;

    int len = 0;
    for (int i = 0; i < a.size(); ++i) {
      #e# ai = a.get(i);
      int j = -#E#Collections.binarySearch(ai, d) - 1;
      assert j > 0 : "a shouldn't contain duplicates -> a.get(i) not presented in d yet";
      if (d[j - 1] < ai && ai < d[j]) {
        ids[j] = i;
        d[j] = ai;
        prev[i] = ids[j - 1];
        if (j > len) len = j;
      }
    }

    #e#[] bestSubseq = new #e#[len];
    for (int i = len - 1, pos = ids[len]; i >= 0; ) {
      bestSubseq[i--] = a.get(pos);
      pos = prev[pos];
    }
    return new #E#Array(bestSubseq);
  }
}