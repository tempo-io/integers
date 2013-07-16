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
    LongArray res[] = {
        create(),
        create()};
    templateCase(res, create());

    //Iterators list is empty.
//    res = null;
//
//    templateCase(res, create());

    // Extreme values in the iterators
    long value = Long.MIN_VALUE;
    res[0] = LongArray.create(value);
    res[1] = LongArray.create(value + 1);
    templateCase(res, create(value, value + 1));
  }

  public void testSimpleCase() {
    LongArray res[] = {
        create(1, 3, 5, 7),
        create(2, 3, 4, 6, 100)};
    templateCase(res, create(1, 2, 3, 4, 5, 6, 7, 100));

    res[0] = create(1, 2, 3, 4);
    res[1] = create(5, 6, 7, 8);
    templateCase(res, create(1, 2, 3, 4, 5, 6, 7, 8));

    res[0] = create();
    res[1] = create(2, 8);
    templateCase(res, create(2, 8));

    res[0] = create(2, 8);
    res[1] = create();
    templateCase(res, create(2, 8));

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
    LongArray expected = create();

    for (int i = 0; i < resLength; i++) {
      int arrayLength = r.nextInt(maxArrayLength);
      res[i] = create();
      for (int j = 0; j < arrayLength; j++) {
        res[i].add(r.nextInt(maxValue));
      }
      res[i].sortUnique();
      IntegersDebug.println(res[i]);

      for (LongIterator iter = res[i].iterator(); iter.hasNext(); ) {
        long val = iter.nextValue();
        expected.add(val);
      }
    }
    expected.sortUnique();

    IntegersDebug.println(expected);
    templateCase(res, expected);

  }

  public void testCreate() {
    List<LongArray> list = new ArrayList<LongArray>();
    list.add(LongArray.create(1, 2, 5, 10));
    list.add(LongArray.create(-3, 2, 3, 4, 11));
    LongArray intersection = new LongArray(SortedLongListUnionIterator.create(list));
    CHECK.order(intersection, -3, 1, 2, 3, 4, 5, 10, 11);
  }

}
