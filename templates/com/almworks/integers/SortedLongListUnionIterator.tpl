package com.almworks.integers.util;

import com.almworks.integers.IntArray;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import com.almworks.integers.NativeIntFixture;
import com.almworks.util.RandomHolder;

import java.util.Random;

import static com.almworks.integers.LongArray.create;

public class SortedLongListUnionIteratorTests extends NativeIntFixture {
  long max = Long.MAX_VALUE, min = Long.MIN_VALUE;


  public void templateCase(LongArray arrays[], LongIterator expected) {
    LongIterator iterators[] = new LongIterator[arrays.length];
    IntegersDebug.println(arrays.length);
    for (int i = 0; i < arrays.length; i++) {
      iterators[i] = arrays[i].iterator();
      IntegersDebug.println("iterator ", i, " : ", arrays[i]);
    }
    SortedLongListUnionIterator res = new SortedLongListUnionIterator(iterators);
    CHECK.order(res, expected);
  }

  public void testSimpleCase() {
    LongArray res[] = {
        create(1, 3, 5, 7),
        create(2, 3, 4, 6, 100)};
    templateCase(res, create(1, 2, 3, 4, 5, 6, 7, 100).iterator());

    res[0] = create(1, 2, 3, 4);
    res[1] = create(5, 6, 7, 8);
    templateCase(res, create(1, 2, 3, 4, 5, 6, 7, 8).iterator());

    res[0] = create();
    res[1] = create(2, 8);
    templateCase(res, create(2, 8).iterator());
  }


  public void testRandomCase() {
    Random r = new RandomHolder().getRandom();
    int resLength = 200;
    int maxArrayLength = 5000;
    int maxValue = 2000000000;

    LongArray res[] = new LongArray[resLength];
    LongArray expected = create();

    for (int i = 0; i < resLength; i++) {
      int arrayLength = r.nextInt(maxArrayLength);
      res[i] = create();
      for (int j = 0; j < arrayLength; j++) {
        res[i].add((long)r.nextInt(maxValue));
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
    templateCase(res, expected.iterator());

  }

}
