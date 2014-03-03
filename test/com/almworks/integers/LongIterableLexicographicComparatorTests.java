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

import java.util.Arrays;

public class LongIterableLexicographicComparatorTests extends IntegersFixture {
  public void testSimple() {
    long[][] ars = {{}, {0,1,2}, {2,4,6}, {0,1}, {5,7,9}, {-1, 1, 2}, {0}, {10}, {0,1,2,3}};
    LongArray[] arrays = new LongArray[ars.length + 1];
    for (int i = 0; i < ars.length; i++) {
      arrays[i] = new LongArray(ars[i]);
    }
    arrays[ars.length] = null;
    Arrays.sort(arrays, new LongIterableLexicographicComparator());
    StringBuilder sb = new StringBuilder();
    for (LongArray array: arrays) {
      LongCollections.append(sb, array);
    }
    assertEquals("" +
      "null" +
      "()" +
      "(-1, 1, 2)" +
      "(0)(0, 1)" +
      "(0, 1, 2)" +
      "(0, 1, 2, 3)" +
      "(2, 4, 6)" +
      "(5, 7, 9)" +
      "(10)", sb.toString());
  }
}
