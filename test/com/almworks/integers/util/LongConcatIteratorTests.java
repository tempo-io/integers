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

package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import junit.framework.TestCase;

public class LongConcatIteratorTests extends IntegersFixture {
  public LongIterator[] its;

  public void inicIts() {
    its = new LongIterator[]{LongIterator.EMPTY,
        LongArray.create(0).iterator(),
        LongArray.create(1, 2).iterator(),
        LongArray.create(3, 4, 5).iterator()};
  }

  public void testSimple() {
    inicIts();
    LongIterator res = new LongConcatIterator(its);
    CHECK.order(LongArray.create(0, 1, 2, 3, 4, 5).iterator(), res);
  }

  public void testAll() {
    int[][] sets = {
        {0, 2, 1, 0},
        {0, 1, 2, 3},
        {},
        {0, 0},
        {0, 1, 0, 2, 0, 3}
    };
    LongArray expected = new LongArray();
    LongIterator actual;
    LongIterator[] its2;
    for (int[] set: sets) {
      inicIts();
      expected.clear();
      for (int i: set) {
        expected.addAll(its[i]);
      }

      inicIts();
      its2 = new LongIterator[set.length];
      for (int i = 0; i < set.length; i++) {
        its2[i] = its[set[i]];
      }

      actual = new LongConcatIterator(its2);
      assertFalse(actual.hasValue());
      for (int i = 0; i < expected.size(); i++) {
        if (i % 2 == 0) assertTrue(actual.hasNext());
        assertEquals(expected.get(i), actual.nextValue());
      }
      assertFalse(actual.hasNext());
    }
  }
}
