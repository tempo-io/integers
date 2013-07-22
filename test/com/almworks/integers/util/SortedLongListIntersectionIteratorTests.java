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
    LongArray input[] = {
        create(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24),
        create(3, 6, 9, 12, 15, 18, 21, 24)};
    templateCase(input, create(6, 12, 18, 24));

    input[0] = create(1, 2, 3, 4);
    input[1] = create(5, 6, 7, 8);
    templateCase(input, create());

    input[0] = create(0, 2, 3, 4, 6, 7, 8, 9, 10, 16, 17, 18, 19, 20);
    input[1] = create(0, 5, 10, 15, 20);
    templateCase(input, create(0, 10, 20));
  }

  public void testExtremeCase() {
    //All iterators are empty.
    LongArray input[] = {
        create(),
        create()};
    templateCase(input, create());

    long value = Long.MIN_VALUE;
    input[0] = LongArray.create(value);
    input[1] = LongArray.create(value, value + 1);
    templateCase(input, create(value));

    value = Long.MAX_VALUE;
    input[0] = LongArray.create(value);
    input[1] = LongArray.create(value - 1, value);
    templateCase(input, create(value));

    input[0] = create();
    templateCase(input, create());

    input[0] = create(0, 2, 4, 6, 8);
    input[1] = create(1, 3, 5, 7, 9);
    templateCase(input, create());


  }

  public void testRandomIntersectionExistsCase() {
    testRandom(10, 100, 200, 1000000);
    testRandom(1, 100, 200, 10000000);
    testRandom(5, 100, 1000, Integer.MAX_VALUE);
    testRandom(5, 100, 1000, 1000000);
    testRandom(0, 1000, 1000, Integer.MAX_VALUE);
  }

  public void testRandom(int intersectionLength, int arraysNumber, int maxArrayLength, int maxValue) {
    LongArray[] arrays = generateRandomArrays(intersectionLength, arraysNumber, maxArrayLength, maxValue);
    LongArray expected = LongArray.copy(arrays[0]);

    for (int i = 1; i < arraysNumber; i++) {
      expected.retain(arrays[i]);
    }
    assert expected.isUniqueSorted();
    IntegersDebug.println("expected", expected);
    templateCase(arrays, expected);
  }

  public void testCreate() {
    List<LongArray> list = new ArrayList<LongArray>();
    list.add(LongArray.create(1, 2, 5, 10, 20));
    list.add(LongArray.create(1, 5, 21, 30, 40));
    LongArray intersection = new LongArray(SortedLongListIntersectionIterator.create(list));
    CHECK.order(intersection, 1, 5);
  }
}
