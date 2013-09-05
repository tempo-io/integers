package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import com.almworks.integers.SetOperationsChecker;
import junit.framework.TestCase;

public class LongMinusIteratorTests extends TestCase {
  private LongArray create(long... values) {
    return new LongArray(values);
  }

  public void testMinusNotUnique() {
    testMinus(create(1, 1, 3, 3, 5, 5), create(1, 3, 5), create());
    testMinus(create(1, 1, 3, 3, 5, 5), create(1, 5), create(3, 3));
    testMinus(create(1, 3, 5), create(1, 1, 5, 5), create(3));
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

  public void testAllCases() {
    new SetOperationsChecker().check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return new LongMinusIterator(arrays[0].iterator(), arrays[1].iterator());
      }
    }, new SetOperationsChecker.MinusGetter(), true, true);
  }

}
