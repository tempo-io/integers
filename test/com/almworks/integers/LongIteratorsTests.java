package com.almworks.integers;

import java.util.NoSuchElementException;

public class LongIteratorsTests extends IntegersFixture {

  public void checkNoValue(LongIterator it) {
    assertFalse(it.hasValue());
    try {
      it.value();
      fail("must throw NSEE");
    } catch (NoSuchElementException _) {
      // ok
    }
  }

  public void testRepeat1() throws Exception {
    LongIterator repeat = LongIterators.repeat(10);
    checkNoValue(repeat);
    for (int i = 0; i < 10; i++) {
      assertEquals(10, repeat.nextValue());
      assertTrue(repeat.hasNext());
    }
    assertTrue(repeat.hasNext());
  }

  public void testCycle() throws Exception {
    LongIterator cycle = LongIterators.cycle(2, 4, 6);
    checkNoValue(cycle);
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(2 + j * 2, cycle.nextValue());
      }
    }
  }

  public void testArithmeticProgression() throws Exception {
    long start = -5, step = 10;
    long cur = start;
    LongIterator arithmetic = LongIterators.arithmeticProgression(start, step);
    checkNoValue(arithmetic);
    for (int i = 0; i < 100; i++) {
      assertEquals(cur, arithmetic.nextValue());
      cur += 10;
    }
  }

  public void testLimit() throws Exception {
    for (int i = 0; i < 10; i++) {
      LongIterator it = LongIterators.limit(LongCollections.repeat(5, i * 20), i * 10);
      checkNoValue(it);
      CHECK.order(LongCollections.repeat(5, i * 10).iterator(), it);

      it = LongIterators.limit(LongProgression.arithmetic(0, i * 20, 1).iterator(), i * 10);
      checkNoValue(it);
      CHECK.order(LongProgression.arithmetic(0, i * 10, 1).iterator(), it);
    }
  }

  public void checkRange(long ... params) {
    assert params.length == 3;
    long start = params[0], stop = params[1], step = params[2];

    // get range
    LongArray expected = new LongArray();
    long delta = stop - start;
    assert step != 0 && (delta == 0 || ((delta > 0) == (step > 0)));
    if (step > 0) {
      for (long i = start; i < stop; i += step) {
        expected.add(i);
      }
    } else {
      for (long i = start; stop < i; i += step) {
        expected.add(i);
      }
    }
    LongIterator actual = LongIterators.range(start, stop, step);
    checkNoValue(actual);
    CHECK.order(expected.iterator(), actual);
  }

  public void testRange() {
    long[][] tests = {{0, 0, 1}, {0, 0, -1}, {0, 0, -4},
        {0, 1, 10}, {0, 10, 1}, {0, 10, 2},
        {20, -7, -5}, {-100, 10, 20}, {15, 0, -4}};
    for (long[] test: tests) {
      checkRange(test);
    }
    for (int attempt = 0; attempt < 20; attempt++) {
      long start = (long)RAND.nextInt(2000) - 1000;
      long step = RAND.nextInt(2000) - 1000;
      if (step == 0) step++;
      long stop = start + (step > 0 ? 1 : -1) * RAND.nextInt(1000);
      checkRange(start, stop, step);
    }

    try {
      LongProgression.getCount(0, 10, 0);
      fail();
    } catch (IllegalArgumentException _) {
      // ok
    }
  }

  public void testArithmetic() {
    int[][] tests = {{0, 1, 10}, {0, 3, 10}, {0, 5, 10}, {0, 10, 1}, {20, 7, 5},
        {-100, 10, 20}, {20, 5, -8}};
    for (int[] t: tests) {
      CHECK.order(LongProgression.arithmetic(t[0], t[1], t[2]).iterator(),
        LongIterators.arithmetic(t[0], t[1], t[2]));
    }
    for (int attempt = 0; attempt < 20; attempt++) {
      long start = RAND.nextInt();
      int count = RAND.nextInt(100);
      long step = RAND.nextInt(2000) - 1000;
      LongIterator actual = LongIterators.arithmetic(start, count, step);
      checkNoValue(actual);
      CHECK.order(LongProgression.arithmetic(start, count, step).iterator(), actual);
    }
  }
}
