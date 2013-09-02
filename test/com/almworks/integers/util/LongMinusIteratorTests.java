package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import junit.framework.TestCase;

public class LongMinusIteratorTests extends TestCase{
  protected final long MIN = Long.MIN_VALUE;
  protected final long MAX = Long.MAX_VALUE;

  private LongArray a(long... values) {
    return new LongArray(values);
  }

  public void testMinusEmptyArrays() {
    testMinus(a(), a(), a());
    testMinus(a(), a(1, 3, 5), a());
    testMinus(a(1, 3, 5), a(), a(1, 3, 5));
  }

  public void testMinusExcludesNotPresent() {
    testMinus(a(1, 3, 5), a(0), a(1, 3, 5));
    testMinus(a(1, 3, 5), a(-2), a(1, 3, 5));
    testMinus(a(1, 3, 5), a(6), a(1, 3, 5));
    testMinus(a(1, 3, 5), a(-2, 0, 2, 6), a(1, 3, 5));
  }

  public void testMinusExcludesPresent() {
    testMinus(a(1, 3, 5), a(1), a(3, 5));
    testMinus(a(1, 3, 5), a(3), a(1, 5));
    testMinus(a(1, 3, 5), a(5), a(1, 3));
    testMinus(a(1, 3, 5), a(1, 3, 5), a());
  }

  public void testMinusExtremeValues() {
    testMinus(a(1, 3, 5), a(MIN), a(1, 3, 5));
    testMinus(a(1, 3, 5), a(MAX), a(1, 3, 5));
    testMinus(a(MIN, 1, 3, 5, MAX), a(1, 3, 5), a(MIN, MAX));
    testMinus(a(MIN, 1, 3, 5, MAX), a(MIN, 3, MAX), a(1, 5));
  }

  public void testMinusNotUnique() {
    testMinus(a(1, 1, 3, 3, 5, 5), a(1, 3, 5), a());
    testMinus(a(1, 1, 3, 3, 5, 5), a(1, 5), a(3, 3));
    testMinus(a(1, 3, 5), a(1, 1, 5, 5), a(3));
  }

  private void testMinus(LongArray include, LongArray exclude, LongArray difference) {
    IntegersFixture.assertContents(new LongMinusIterator(include.iterator(), exclude.iterator()), difference);
  }

  public void testSimple() {
    LongIterator it1 = LongArray.create(0, 1, 2).iterator();
    LongIterator empty = LongIterator.EMPTY;
    LongIterator minus = new LongMinusIterator(it1, empty);
    assertFalse(minus.hasValue());
    assertTrue(minus.hasNext());
    minus.next();
    assertEquals(0, minus.value());
    assertTrue(minus.hasNext());
    assertEquals(0, minus.value());

    assertEquals(1, minus.nextValue());
    assertTrue(minus.hasNext());
    assertEquals(1, minus.value());
  }
}
