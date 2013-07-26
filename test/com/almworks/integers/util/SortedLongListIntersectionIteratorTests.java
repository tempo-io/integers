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

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.LongArray.create;

public class SortedLongListIntersectionIteratorTests extends IntegersFixture {
  protected final long MIN = Long.MIN_VALUE;
  protected final long MAX = Long.MAX_VALUE;

  private LongArray a(long... values) {
    return new LongArray(values);
  }

  public void testInterEmptyArrays() {
    testInterSym(a(), a(), a());
    testInterSym(a(), a(1, 3, 5), a());
    testInterSym(a(1, 3, 5), a(), a());
  }

  public void testInterEmptyIntersection() {
    testInterSym(a(1, 3, 5), a(0), a());
    testInterSym(a(1, 3, 5), a(-2), a());
    testInterSym(a(1, 3, 5), a(6), a());
    testInterSym(a(1, 3, 5), a(-2, 0, 2, 6), a());
  }

  public void testInterNotEmptyIntersection() {
    testInterSym(a(1, 3, 5), a(1), a(1));
    testInterSym(a(1, 3, 5), a(3), a(3));
    testInterSym(a(1, 3, 5), a(5), a(5));
    testInterSym(a(1, 3, 5), a(1, 3, 5), a(1, 3, 5));
    testInterSym(a(1, 3, 5), a(0, 1, 2, 3, 4, 5, 6), a(1, 3, 5));
  }

  public void testInterExtremeValues() {
    testInterSym(a(1, 3, 5), a(MIN), a());
    testInterSym(a(1, 3, 5), a(MAX), a());
    testInterSym(a(MIN, 1, 3, 5, MAX), a(1, 3, 5), a(1, 3, 5));
    testInterSym(a(MIN, 1, 3, 5, MAX), a(MIN, 3, MAX), a(MIN, 3, MAX));
  }

  private void testInterSym(LongArray array1, LongArray array2, LongArray intersection) {
    // when intersection is symmetric
    testInter(array1, array2, intersection);
    testInter(array2, array1, intersection);
  }

  private void testInter(LongArray array1, LongArray array2, LongArray intersection) {
    IntegersFixture.assertContents(new SortedLongListIntersectionIterator(array1.iterator(), array2.iterator()), intersection);
  }


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
    LongArray[] arrays = generateRandomLongArrays(intersectionLength, arraysNumber, maxArrayLength, 0, maxValue);
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
