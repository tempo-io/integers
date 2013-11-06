package com.almworks.integers;

import junit.framework.TestCase;

import java.util.List;

public class LongIteratorSpecificationChecker {
  public static interface IteratorGetter {
    List<LongIterator> get(long ... values);
  }

  // todo extends from IntegersFixture or make IF#ap public static
  protected static long[] ap(long start, int step, int count) {
    long[] r = new long[count];
    long cur = start;
    for (int i = 0; i < r.length; i++) {
      r[i] = cur;
      cur += step;
    }
    return r;
  }

  public static void check(IteratorGetter getter) {
    testEmpty(getter);
    testSimple(getter);
    testValues(getter, 0, 1, 2);
    testValues(getter, 0, 2, 4, 6, 8);
    testValues(getter);
    testValues(getter, 5, -10, 5, 0);
    testValues(getter, 0, 0, 1, 1, 2, 2);
    testValues(getter, Long.MAX_VALUE, Long.MIN_VALUE, 0);

    testValues(getter, ap(0, 1, 100));
    testValues(getter, IntegersFixture.generateRandomLongArray(100, false).extractHostArray());
  }

  private static void testSimple(IteratorGetter getter) {
    for(LongIterator it: getter.get(0, 1, 2)) {
      TestCase.assertFalse(it.hasValue());
      TestCase.assertTrue(it.hasNext());

      it.next();
      TestCase.assertEquals(0, it.value());
      TestCase.assertTrue(it.hasNext());
      TestCase.assertEquals(0, it.value());

      TestCase.assertEquals(1, it.nextValue());
      TestCase.assertTrue(it.hasNext());
      TestCase.assertEquals(1, it.value());
    }
  }

  private static void testEmpty(IteratorGetter getter) {
    for (LongIterator empty: getter.get()) {
      TestCase.assertFalse(empty.hasValue());
      TestCase.assertFalse(empty.hasNext());
    }
  }

  private static void testValues(IteratorGetter getter, long ... values) {
    for(LongIterator it: getter.get(values)) {
      TestCase.assertFalse(it.hasValue());
      for (int i = 0, n = values.length; i < n; i++) {
        TestCase.assertTrue(it.hasNext());
        it.next();
        TestCase.assertTrue(it.hasNext() || (i == n - 1 && !it.hasNext()));
        TestCase.assertEquals(values[i], it.value());
        TestCase.assertTrue(it.hasValue());
      }
      TestCase.assertFalse(it.hasNext());
    }
    for(LongIterator it: getter.get(values)) {
      int i = 0;
      for (LongIterator it2: it) {
        TestCase.assertEquals(values[i++], it2.value());
      }
    }
    for(LongIterator it: getter.get(values)) {
      int i = 0;
      while(it.hasNext()) {
        TestCase.assertEquals(values[i++], it.nextValue());
      }
    }
  }
}