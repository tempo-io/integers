package com.almworks.integers;

public class LongIteratorSpecificationChecker extends IntegersFixture {
  public static interface IteratorGetter {
    LongIterator get(long ... values);
  }

  public static void check(IteratorGetter getter, LongArray ... arrays) {
    testSimple(getter);
    testValues(getter, 0, 1, 2);
    testValues(getter, 0, 2, 4, 6, 8);
    testValues(getter);
    testValues(getter, 5, -10, 5, 0);
    testValues(getter, 0, 0, 1, 1, 2, 2);
    testValues(getter, Long.MAX_VALUE, Long.MIN_VALUE, 0);
    for (LongArray values: arrays) {
      testValues(getter, values.toNativeArray());
    }
  }

  private static void testSimple(IteratorGetter getter) {
    LongIterator it = getter.get(0, 1, 2);
    LongIterator empty = getter.get();

    assertFalse(empty.hasValue());
    assertFalse(empty.hasNext());
    assertFalse(it.hasValue());
    assertTrue(it.hasNext());

    it.next();
    assertEquals(0, it.value());
    assertTrue(it.hasNext());
    assertEquals(0, it.value());

    assertEquals(1, it.nextValue());
    assertTrue(it.hasNext());
    assertEquals(1, it.value());
  }

  private static void testValues(IteratorGetter getter, long ... values) {
    LongIterator it = getter.get(values);
    assertFalse(it.hasValue());
    for (int i = 0, n = values.length; i < n; i++) {
      assertTrue(it.hasNext());
      it.next();
      assertTrue(it.hasNext() || (i == n - 1 && !it.hasNext()));
      assertEquals(values[i], it.value());
      assertTrue(it.hasValue());
    }
    assertFalse(it.hasNext());

    it = getter.get(values);
    int i = 0;
    for (LongIterator it2: it) {
      assertEquals(values[i++], it2.value());
    }
    it = getter.get(values);
    i = 0;
    while(it.hasNext()) {
      assertEquals(values[i++], it.nextValue());
    }
  }
}