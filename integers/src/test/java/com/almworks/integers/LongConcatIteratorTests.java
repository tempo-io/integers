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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.almworks.integers.LongCollections.collectIterables;

public class LongConcatIteratorTests extends IntegersFixture {
  public LongIterator[] its;

  public void testIteratorSpecification() {
    LongIteratorSpecificationChecker.checkIterator(myRand, new LongIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public List<LongIterator> get(final long... values) {
        List<LongIterator> res = new ArrayList<LongIterator>();
        res.add(new LongConcatIterator(new LongNativeArrayIterator(values)));
        res.add(new LongConcatIterator(Arrays.<LongIterable>asList(new LongNativeArrayIterator(values))));

        res.add(new LongConcatIterator(LongIterator.EMPTY, new LongNativeArrayIterator(values)));
        res.add(new LongConcatIterator(new LongNativeArrayIterator(values), LongIterator.EMPTY));
        if (values.length < 2) return res;

        LongArray array = new LongArray(values);
        int size = values.length;
        res.add(new LongConcatIterator(array.subList(0, size / 2), array.subList(size / 2, size)));
        res.add(new LongConcatIterator(array.subList(0, size / 2), LongIterator.EMPTY, array.subList(size / 2, size)));
        if (values.length < 3) return res;

        int s31 = size / 3, s32 = s31 * 2;
        res.add(new LongConcatIterator(array.subList(0, s31), array.subList(s31, s32), array.subList(s32, size)));
        return res;
      }
    });
  }

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

  public void testRandom() {
    LongArray[] arrays = new LongArray[5];
    for (int i = 0; i < 20; i++) {
      for (int j = 0; j < arrays.length; j++) {
        arrays[j] = generateRandomLongArray(30, IntegersFixture.SortedStatus.UNORDERED);
      }
      CHECK.order(collectIterables(300, arrays).iterator(), LongIterators.concat(arrays));
    }
  }
}
