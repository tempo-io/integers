package com.almworks.integers;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static com.almworks.integers.LongProgression.Arithmetic.fillArray;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.*;
import static org.junit.Assert.assertFalse;

public class LongListIteratorSpecificationChecker extends LongIteratorSpecificationChecker<LongListIterator> {
  private LongListIteratorSpecificationChecker(IteratorGetter<LongListIterator> getter, ValuesType type) {
    super(getter, type);
  }

  public static void checkListIterator(IteratorGetter<LongListIterator> getter) {
    checkListIterator(getter, ValuesType.ALL);
  }

  public static void checkListIterator(IteratorGetter<LongListIterator> getter, ValuesType type) {
    LongListIteratorSpecificationChecker checker = new LongListIteratorSpecificationChecker(getter, type);
    checker.run();
  }

  @Override
  protected void testValues(long... values) {
    super.testValues(values);
    testIteratorIndex(values);
    testIteratorMove();
    testIteratorGet();
  }

  private void testIteratorIndex(long... values) {
    for (LongListIterator it : getter.get(values)) {
      try {
        it.index();
        fail();
      } catch (NoSuchElementException ex) {
        // ok
      }

      int index = 0;
      while (it.hasNext()) {
        it.next();
        assertEquals(index++, it.index());
      }
      index--;
      while (index > 0) {
        assertEquals(index--, it.index());
        it.move(-1);
      }
    }
  }

  private void testIteratorMove() {
    long[] values = fillArray(1, 2, 10);
    for (LongListIterator it : getter.get(values)) {
      assertFalse(it.hasValue());
      try {
        it.value();
        fail();
      } catch (NoSuchElementException ex) {}
      it.move(1);
      assertEquals(0, it.index());
      assertEquals(values[0], it.value());
      assertEquals(values[1], it.nextValue());
      assertEquals(1, it.index());
      assertEquals(values[0], it.get(-1));
      assertEquals(values[9], it.get(8));
      try {
        it.move(-2);
        fail();
      } catch (NoSuchElementException ex) {}

      it.move(3);

      assertEquals(4, it.index());
      assertEquals(values[4], it.value());
      assertEquals(values[4], it.get(0));
      it.move(-1);
      assertEquals(3, it.index());
      assertEquals(values[4], it.get(1));
      assertEquals(values[4], it.nextValue());

      it.move(5);
      assertEquals(19, it.value());
      assertFalse(it.hasNext());

      it.move(-1);
      assertEquals(19, it.nextValue());
      assertFalse(it.hasNext());
    }
  }

  private void testIteratorGet() {
    long[] values = IntegersFixture.interval(0, 10);
    for (LongListIterator it : getter.get(values)) {
      for (int idx = 0; idx < values.length; idx++) {
        assertTrue(it.hasNext());
        assertEquals(values[idx], it.nextValue());

        try {
          it.get(-idx - 1);
          fail();
        } catch (NoSuchElementException _) {
          // ok
        }

        for (int i = 0; i < values.length; i++) {
          assertEquals(values[i], it.get(-idx + i));
        }

        try {
          it.get(-idx - values.length);
          fail();
        } catch (NoSuchElementException _) {
          // ok
        }
      }
    }
  }

}