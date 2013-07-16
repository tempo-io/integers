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
import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;
import com.almworks.util.RandomHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.almworks.integers.LongArray.create;

public class SortedLongListIntersectionIteratorTests extends IntegersFixture {
  public void templateCase(LongArray arrays[], LongIterable expected) {
    List<LongIterator> iterators = new ArrayList<LongIterator>(arrays.length);
    IntegersDebug.println(arrays.length);

    for (int i = 0; i < arrays.length; i++) {
      iterators.add(arrays[i].iterator());
      IntegersDebug.println("iterator ", i, " : ", arrays[i]);
    }
    SortedLongListIntersectionIterator res = new SortedLongListIntersectionIterator(iterators);
    CHECK.order(res, expected.iterator());
  }

  public void testSimpleCase() {
    LongArray res[] = {
        create(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24),
        create(3, 6, 9, 12, 15, 18, 21, 24)};
    templateCase(res, create(6, 12, 18, 24));

    res[0] = create(1, 2, 3, 4);
    res[1] = create(5, 6, 7, 8);
    templateCase(res, create());

    res[0] = create(0, 2, 3, 4, 6, 7, 8, 9, 10, 16, 17, 18, 19, 20);
    res[1] = create(0, 5, 10, 15, 20);
    templateCase(res, create(0, 10, 20));

    IntegersDebug.print("output: ", 234, " : ");
  }

  public void testExtremeCase() {
    //All iterators are empty.
    LongArray res[] = {
        create(),
        create()};
    templateCase(res, create());

    long value = Long.MIN_VALUE;
    res[0] = LongArray.create(value);
    res[1] = LongArray.create(value, value + 1);
    templateCase(res, create(value));

    value = Long.MAX_VALUE;
    res[0] = LongArray.create(value);
    res[1] = LongArray.create(value - 1, value);
    templateCase(res, create(value));

    res[0] = create();
    templateCase(res, create());

    res[0] = create(0, 2, 4, 6, 8);
    res[1] = create(1, 3, 5, 7, 9);
    templateCase(res, create());


  }

  public void testRandomIntersectionExistsCase() {
    _testRandom(10, 100, 200, 1000000);
    _testRandom(1, 100, 200, 10000000);
    _testRandom(5, 100, 1000, Integer.MAX_VALUE);
    _testRandom(5, 100, 1000, 1000000);
    _testRandom(0, 1000, 1000, Integer.MAX_VALUE);
  }

  public void _testRandom(int intersectionLength, int resLength, int maxArrayLength, int maxValue) {
    Random r = new RandomHolder().getRandom();

    LongArray[] res = new LongArray[resLength];
    LongArray expected;

    LongArray intersection = create();
    for ( int i = 0; i < intersectionLength; i++) {
      intersection.add(r.nextInt(maxArrayLength));
    }

    for (int i = 0; i < resLength; i++) {
      int arrayLength = r.nextInt(maxArrayLength);
      res[i] = LongArray.copy(intersection);

      for (int j = 0; j < arrayLength; j++) {
        res[i].add(r.nextInt(maxValue));
      }
      res[i].sortUnique();
      IntegersDebug.println(res[i]);
    }

    expected = LongArray.copy(res[0]);
    for (int i = 1; i < resLength; i++) {
      expected.retain(res[i]);
    }
    assert expected.isUniqueSorted();
    IntegersDebug.println("expected", expected);
    int len = 0;
    for (LongIterator iter = expected.iterator(); iter.hasNext(); iter.next()) len++;
    templateCase(res, expected);
  }

  public void testCreate() {
    List<LongArray> list = new ArrayList<LongArray>();
    list.add(LongArray.create(1, 2, 5, 10, 20));
    list.add(LongArray.create(1, 5, 21, 30, 40));
    LongArray intersection = new LongArray(SortedLongListIntersectionIterator.create(list));
    CHECK.order(intersection, 1, 5);
  }
}
