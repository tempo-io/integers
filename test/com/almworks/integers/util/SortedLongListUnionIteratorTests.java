package com.almworks.integers.util;

import com.almworks.integers.*;
import com.almworks.util.RandomHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.almworks.integers.LongArray.create;

public class SortedLongListUnionIteratorTests extends IntegersFixture {

  public void templateCase(LongArray arrays[], LongIterable expected) {
    List<LongIterator> iterators = new ArrayList<LongIterator>(arrays.length);
    IntegersDebug.println(arrays.length);

    for (int i = 0; i < arrays.length; i++) {
      iterators.add(arrays[i].iterator());
      IntegersDebug.println("iterator ", i, " : ", arrays[i]);
    }
    SortedLongListUnionIterator res = new SortedLongListUnionIterator(iterators);
    CHECK.order(res, expected.iterator());
  }

  public void testExtremeCase() {
    //All iterators are empty.
    LongArray[] input = {
        create(),
        create()};
    templateCase(input, create());

    LongArray[] empty = {};
    templateCase(empty, create());

    // Extreme values in the iterators
    long value = Long.MIN_VALUE;
    input[0] = LongArray.create(value);
    input[1] = LongArray.create(value + 1);
    templateCase(input, create(value, value + 1));
  }

  public void testSimpleCase() {
    LongArray input[] = {
        create(1, 3, 5, 7),
        create(2, 3, 4, 6, 100)};
    templateCase(input, create(1, 2, 3, 4, 5, 6, 7, 100));

    input[0] = create(1, 2, 3, 4);
    input[1] = create(5, 6, 7, 8);
    templateCase(input, create(1, 2, 3, 4, 5, 6, 7, 8));

    input[0] = create();
    input[1] = create(2, 8);
    templateCase(input, create(2, 8));

    input[0] = create(2, 8);
    input[1] = create();
    templateCase(input, create(2, 8));

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
    LongArray expected = create();

    for (LongList array : arrays) {
      expected.addAll(array);
    }
    expected.sortUnique();

    IntegersDebug.println(expected);
    templateCase(arrays, expected);
  }

  public void testCreate() {
    List<LongArray> list = new ArrayList<LongArray>();
    list.add(LongArray.create(1, 2, 5, 10));
    list.add(LongArray.create(-3, 2, 3, 4, 11));
    LongArray intersection = new LongArray(SortedLongListUnionIterator.create(list));
    CHECK.order(intersection, -3, 1, 2, 3, 4, 5, 10, 11);
  }

}
