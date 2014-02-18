package com.almworks.integers;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.*;

import static com.almworks.integers.IntegersFixture.*;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class LongIteratorSpecificationChecker<I extends LongIterator> {
  public static int MAX = Integer.MAX_VALUE;
  public static int MIN = Integer.MIN_VALUE;

  protected final IteratorGetter<I> getter;
  protected final ValuesType type;


  public interface IteratorGetter<I extends LongIterator> {
    List<I> get(long ... values);
  }

  public static long[][] valuesArray = {{},
      {5},
      {0, 1, 2},
      {239, 1000},
      {0, 2, 4, 6, 8},
      {0, 5, 6, 10, 11, 15},
      {MIN / 10, 0, MAX / 10},
      {MIN / 10},
      {MAX / 10},
      {0, 10, 20, 30, 40},
      {0, -7, -14, -21},
      {1000, 990, 980, 970},
      {0, 0, 1, 1, 2, 2},
      {-10, 10, 20, 30, -23, 40, 21, 33, 15, 0},
      {1, 2, 3, 4, 5, 6, 29, -7, 144, 15},
      ap(0, 1, 19),
      ap(0, -1, 10),
      ap(1, 2, 10)
  };

  public enum ValuesType {
    ALL {
      @Override
      public boolean check(long... values) {
        return true;
      }

      @Override
      public long[] generateValues(int size) {
        return generateRandomLongArray(size, SortedStatus.UNORDERED).extractHostArray();
      }

      @Override
      public ValuesType[] supportedTypes() {
        return new ValuesType[]{ALL, SORTED, SORTED_UNIQUE, ARITHMETHIC};
      }
    },
    SORTED {
      @Override
      public boolean check(long... values) {
        return new LongArray(values).isSorted();
      }

      @Override
      public long[] generateValues(int size) {
        LongArray array = generateRandomLongArray(size, SortedStatus.SORTED);
        array.addAll(array.get(IntProgression.range(0, size, 3)));
        array.sort();
        return array.toNativeArray();
      }

      @Override
      public ValuesType[] supportedTypes() {
        return new ValuesType[]{SORTED, SORTED_UNIQUE};
      }
    },
    SORTED_UNIQUE {
      @Override
      public boolean check(long... values) {
        return new LongArray(values).isUniqueSorted();
      }

      @Override
      public long[] generateValues(int size) {
        return generateRandomLongArray(size, SortedStatus.SORTED_UNIQUE).extractHostArray();
      }

      @Override
      public ValuesType[] supportedTypes() {
        return new ValuesType[]{SORTED_UNIQUE};
      }
    },
    ARITHMETHIC {
      @Override
      public boolean check(long... values) {
        if (values.length < 3) {
          return true;
        }
        long step = values[1] - values[0];
        for (int i = 2; i < values.length; i++) {
          if (values[i] - values[i - 1] != step) {
            return false;
          }
        }
        return true;
      }

      @Override
      public long[] generateValues(int size) {
        long start = RAND.nextInt(), step = RAND.nextInt() - MAX / 2;
        int count = RAND.nextInt(size);
        return LongProgression.Arithmetic.fillArray(start, step, count);
      }

      @Override
      public ValuesType[] supportedTypes() {
        return new ValuesType[]{ARITHMETHIC};
      }
    };
    public abstract boolean check(long ... values);
    public abstract long[] generateValues(int size);
    public abstract ValuesType[] supportedTypes();
  }

  protected LongIteratorSpecificationChecker(IteratorGetter<I> getter, ValuesType type) {
    this.getter = getter;
    this.type = type;
  }

  public static void checkIterator(IteratorGetter<LongIterator> getter) {
    checkIterator(getter, ValuesType.ALL);
  }

  public static void checkIterator(IteratorGetter<LongIterator> getter, ValuesType type) {
    LongIteratorSpecificationChecker checker = new LongIteratorSpecificationChecker(getter, type);
    checker.run();
  }

  protected void run() {
    testEmpty();
    testSimple();

    for (long[] values : valuesArray) {
      if (type.check(values)) {
        testValues(values);
      }
    }
    int attempts = 8, size = 10;
    for (ValuesType curType : type.supportedTypes()) {
      for (int attempt = 0; attempt < attempts; attempt++) {
        testValues(curType.generateValues(size));
      }
    }

    testRemoveException();
  }

  private void testRemoveException() {
    for(LongIterator it: getter.get(0, 1, 2)) {
      if (!(it instanceof WritableLongListIterator))
        try {
          it.next();
          it.remove();
          fail();
        } catch (UnsupportedOperationException ex) {
          // ok
        }
    }
  }

  private void testSimple() {
    for(LongIterator it: getter.get(0, 1, 2)) {
      assertFalse(it.hasValue());
      checkValueAndCatchNSEE(it);
      assertTrue(it.hasNext());

      it.next();
      assertEquals(0, it.value());
      assertTrue(it.hasNext());
      assertEquals(0, it.value());

      assertEquals(1, it.nextValue());
      assertTrue(it.hasNext());
      assertEquals(1, it.value());
    }
  }

  private void testEmpty() {
    for (LongIterator empty: getter.get()) {
      checkValueAndCatchNSEE(empty);
      checkNextAndCatchNSEE(empty);
    }
  }

  protected void testValues(long ... values) {
    checkOrder(values);
  }

  private void checkOrder(long ... values) {
    for(LongIterator it: getter.get(values)) {
      assertFalse(Arrays.toString(values), it.hasValue());
      checkValueAndCatchNSEE(it);
      for (int i = 0; i < values.length; i++) {
        assertTrue(Arrays.toString(values), it.hasNext());
        it.next();
        assertEquals(Arrays.toString(values), values[i], it.value());
        assertTrue(Arrays.toString(values), it.hasValue());
      }
      assertFalse(Arrays.toString(values), it.hasNext());
      checkNextAndCatchNSEE(it);
    }
    for(LongIterator it: getter.get(values)) {
      assertFalse(Arrays.toString(values), it.hasValue());
      checkValueAndCatchNSEE(it);
      int i = 0;
      for (LongIterator it2 : it) {
        assertEquals(values[i++], it2.value());
      }
      assertEquals(values.length, i);
    }
    for(LongIterator it: getter.get(values)) {
      Assert.assertFalse(it.hasValue());
      checkValueAndCatchNSEE(it);

      int i = 0;
      while (it.hasNext()) {
        assertEquals(values[i++], it.nextValue());
      }
      assertEquals(values.length, i);
      checkNextAndCatchNSEE(it);
    }

    for(LongIterator it : getter.get(values)) {
      Assert.assertFalse(it.hasValue());
      checkValueAndCatchNSEE(it);
      CHECK.order(it, values);
    }
  }

  protected void checkValueAndCatchNSEE(LongIterator it) {
    assertFalse(it.hasValue());
    try {
      it.value();
      fail();
    } catch (NoSuchElementException _) {
      // ok
    }

  }

  protected void checkNextAndCatchNSEE(LongIterator it) {
    Assert.assertFalse(it.hasNext());
    try {
      it.next();
      fail();
    } catch (NoSuchElementException _) {
      // ok
    }
  }

  public static void checkIteratorThrowsCME(Iterator iterator) {
    try {
      iterator.hasNext();
      TestCase.fail();
    } catch (ConcurrentModificationException e) {}
    try {
      iterator.next();
      TestCase.fail();
    } catch (ConcurrentModificationException e) {}

    if (iterator instanceof IntIterator) {
      IntIterator it = (IntIterator) iterator;
      try {
        it.value();
        TestCase.fail();
      } catch (ConcurrentModificationException e) {}
    } else if (iterator instanceof LongIterator) {
      LongIterator it = (LongIterator) iterator;
      try {
        it.hasValue();
        TestCase.fail();
      } catch (ConcurrentModificationException e) {}
      try {
        it.value();
        TestCase.fail();
      } catch (ConcurrentModificationException e) {}
    } else if (iterator instanceof IntLongIterator) {
      IntLongIterator it = (IntLongIterator) iterator;
      try {
        it.hasValue();
        TestCase.fail();
      } catch (ConcurrentModificationException e) {}
      try {
        it.left();
        TestCase.fail();
      } catch (ConcurrentModificationException e) {}
      try {
        it.right();
        TestCase.fail();
      } catch (ConcurrentModificationException e) {}
    }
  }
}