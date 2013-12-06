package com.almworks.integers;

public class LongIteratorsTests extends IntegersFixture {

  public void testRepeat1() throws Exception {
    LongIterator repeat = LongIterators.repeat(10);
    for (int i = 0; i < 10; i++) {
      assertEquals(10, repeat.nextValue());
      assertTrue(repeat.hasNext());
    }
    assertTrue(repeat.hasNext());
  }

  public void testRepeat() throws Exception {
    LongIterator repeat = LongIterators.cycle(2, 4, 6);
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(2 + j * 2, repeat.nextValue());
      }
    }
  }

  public void testArithmeticProgression() throws Exception {
    long start = -5, step = 10;
    long cur = start;
    LongIterator arithmetic = LongIterators.arithmeticProgression(start, step);
    for (int i = 0; i < 100; i++) {
      assertEquals(cur, arithmetic.nextValue());
      cur += 10;
    }
  }

  public void testLimit() throws Exception {
    for (int i = 0; i < 10; i++) {
      CHECK.order(LongCollections.repeat(5, i * 10).iterator(), LongIterators.limit(LongCollections.repeat(5, i * 20 + 20), i * 10));
      CHECK.order(LongProgression.arithmetic(0, i * 10, 1).iterator(),
          LongIterators.limit(LongProgression.arithmetic(0, i * 20 + 20, 1).iterator(), i * 10));
    }
  }

  public void checkRange(long start, long step, int count) {
    if (step == 0) step++;
    LongList expected = LongProgression.arithmetic(start, count, step);
    long stop = expected.get(expected.size() - 1) + 1;
    CHECK.order(expected.iterator(), LongIterators.range(start, stop, step));
    CHECK.order(expected.iterator(), LongIterators.arithmetic(start, count, step));
  }


  public void testRangeArithmetic() {
    int[][] tests = {{0, 1, 10}, {0, 3, 10}, {0, 5, 10}, {0, 10, 1}, {20, -7, 5}, {-100, 10, 20}};
    for (int[] test: tests) {
      checkRange(test[0], test[1], test[2]);
    }
    for (int attempt = 0; attempt < 20; attempt++) {
      checkRange(RAND.nextInt(), RAND.nextInt(2000) - 1000, RAND.nextInt(100));
    }
  }

  public void testArithmetic() {
    LongIterator arithmetic = LongIterators.arithmetic(0, 10, 5);
    System.out.println(LongCollections.toBoundedString(arithmetic));
  }
}
