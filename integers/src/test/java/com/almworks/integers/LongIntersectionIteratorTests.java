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
import java.util.List;

import static com.almworks.integers.LongIterators.range;

public class LongIntersectionIteratorTests extends IntegersFixture {
  public void testCreate() {
    List<LongArray> list = new ArrayList<LongArray>();
    list.add(LongArray.create(1, 2, 5, 10, 20));
    list.add(LongArray.create(1, 5, 21, 30, 40));
    LongArray intersection = new LongArray(new LongIntersectionIterator(list));
    CHECK.order(intersection, 1, 5);
  }

  public void testIteratorSpecification() {
    LongIteratorSpecificationChecker.checkIterator(myRand, new LongIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public List<? extends LongIterator> get(final long... values) {
        if (!new LongArray(values).isSortedUnique()) {
          throw new IllegalArgumentException();
        }
        List<LongIterator> res = new ArrayList<LongIterator>();
        AbstractLongList list = new LongArray(values);
        res.add(new LongIntersectionIterator(new LongNativeArrayIterator(values)));

        int length = values.length;
        if (length == 0) {
          res.add(new LongIntersectionIterator());
          res.add(new LongIntersectionIterator(LongIterator.EMPTY));
          res.add(new LongIntersectionIterator(range(0, 10, 2), range(1, 10, 2)));
          res.add(new LongIntersectionIterator(range(0, 30, 3), range(1, 30, 3), range(2, 30, 3)));
          res.add(new LongIntersectionIterator(LongIterator.EMPTY, range(0, 10)));
          return res;
        }
        int min = (int) values[0], max = (int) values[length - 1];
        LongArray array = generateRandomLongArray(100, IntegersFixture.SortedStatus.UNORDERED, min, max + 1);
        array.addAll(values);
        array.sortUnique();
        res.add(new LongIntersectionIterator(new LongNativeArrayIterator(values),
            array));

        array = LongArray.copy(values);
        array.addAll(range(min, min + 10));
        array.addAll(range(max - 10, max + 1));
        array.sortUnique();
        res.add(new LongIntersectionIterator(new LongNativeArrayIterator(values), array));
        return res;
      }
    }, LongIteratorSpecificationChecker.ValuesType.SORTED_UNIQUE);
  }

  public void testAllCases() {
    new SetOperationsChecker().check(myRand, new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return new LongIntersectionIterator(arrays);
      }
    }, new SetOperationsChecker.IntersectionGetter(false), false, SortedStatus.SORTED_UNIQUE);
  }

  public void testSimple() {
    long[][][] variants = {{{0, 1, 2}, {1, 2, 3}},
        {{0, 2, 4, 6, 8, 10}, {0, 3, 6, 9, 12}},
        {{0, 3, 5, 6, 10, 14}, {-1, 0, 4, 10, 100}},
        {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, {0, 2, 4, 6, 8, 10, 12}, {0, 3, 6, 9, 12}}};
    long[][] expected = {{1, 2}, {0, 6}, {0, 10}, {0, 6, 12}};
    for (int j = 0; j < variants.length; j++) {
      long[][] variant = variants[j];
      LongArray[] arrays = new LongArray[variant.length];
      for (int i = 0; i < variant.length; i++) {
        arrays[i] = new LongArray(variant[i]);
      }
      CHECK.order(new LongIntersectionIterator(arrays), expected[j]);
    }
  }
}
