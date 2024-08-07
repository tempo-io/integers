/*
 * Copyright 2014 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almworks.integers;

import java.util.*;

import static com.almworks.integers.IntegersFixture.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LongIteratorSpecificationChecker<I extends LongIterator> {
  public static int MAX = Integer.MAX_VALUE;
  public static int MIN = Integer.MIN_VALUE;

  protected final IteratorGetter<I> getter;
  protected final ValuesType type;
  protected final Random myRand;


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
      ap(0, 19, 1),
      ap(0, 10, -1),
      ap(1, 10, 2)
  };

  public enum ValuesType {
    ALL {
      @Override
      public boolean check(long... values) {
        return true;
      }

      @Override
      public long[] generateValues(Random random, int size) {
        return generateRandomLongArray(random, size, SortedStatus.UNORDERED).extractHostArray();
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
      public long[] generateValues(Random random, int size) {
        LongArray array = generateRandomLongArray(random, size, SortedStatus.SORTED);
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
        return new LongArray(values).isSortedUnique();
      }

      @Override
      public long[] generateValues(Random random, int size) {
        return generateRandomLongArray(random, size, SortedStatus.SORTED_UNIQUE).extractHostArray();
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
      public long[] generateValues(Random random, int size) {
        long start = random.nextInt(), step = random.nextInt() - MAX / 2;
        int count = random.nextInt(size);
        return LongProgression.Arithmetic.nativeArray(start, count, step);
      }

      @Override
      public ValuesType[] supportedTypes() {
        return new ValuesType[]{ARITHMETHIC};
      }
    },
    INTEGER {
      @Override
      public boolean check(long... values) {
        for (long value : values) {
          if ((long) ((int) value) != value) {
            return false;
          }
        }
        return true;
      }

      @Override
      public long[] generateValues(Random random, int size) {
        return LongCollections.asLongList(generateRandomIntArray(random, size, SortedStatus.UNORDERED)).toNativeArray();
      }

      @Override
      public ValuesType[] supportedTypes() {
        return new ValuesType[]{INTEGER};
      }
    };
    public abstract boolean check(long ... values);
    public abstract long[] generateValues(Random random, int size);
    public abstract ValuesType[] supportedTypes();
  }

  protected LongIteratorSpecificationChecker(Random random, IteratorGetter<I> getter, ValuesType type) {
    this.myRand = random;
    this.getter = getter;
    this.type = type;
  }

  public static void checkIterator(Random random, IteratorGetter<LongIterator> getter) {
    checkIterator(random, getter, ValuesType.ALL);
  }

  public static void checkIterator(Random random, IteratorGetter<LongIterator> getter, ValuesType type) {
    LongIteratorSpecificationChecker checker = new LongIteratorSpecificationChecker(random, getter, type);
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
        testValues(curType.generateValues(myRand, size));
      }
    }

    testRemoveException();
  }

  private void testRemoveException() {
    for(LongIterator it: getter.get(0, 1, 2)) {
      if (!(it instanceof WritableLongListIterator)) {
        it.next();
        try {
          it.remove();
          fail();
        } catch (UnsupportedOperationException ex) {
          // ok
        }
      }
    }
  }

  private void testSimple() {
    for (LongIterator it : getter.get(0, 1, 2)) {
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

      assertEquals(it, it.next());
      assertEquals(2, it.value());

      assertFalse(it.hasNext());
      try {
        it.next();
        fail();
      } catch (NoSuchElementException _) {
        // ok
      }

      assertEquals(it, it.iterator());
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
      assertFalse(it.hasValue());
      checkValueAndCatchNSEE(it);

      int i = 0;
      while (it.hasNext()) {
        assertEquals(values[i++], it.nextValue());
      }
      assertEquals(values.length, i);
      checkNextAndCatchNSEE(it);
    }

    for(LongIterator it : getter.get(values)) {
      assertFalse(it.hasValue());
      checkValueAndCatchNSEE(it);
      CHECK.order(it, values);
    }
  }

  protected void checkValueAndCatchNSEE(LongIterator it) {
    boolean hasNextBefore = it.hasNext();
    assertFalse(it.hasValue());
    try {
      it.value();
      fail();
    } catch (NoSuchElementException _) {
      // ok
    }
    assertEquals(hasNextBefore, it.hasNext());
  }

  protected static void checkNextAndCatchNSEE(LongIterator it) {
    boolean hasValue = it.hasValue();
    long value = hasValue ? it.value() : -1;
    assertFalse(it.hasNext());
    try {
      it.next();
      fail();
    } catch (NoSuchElementException _) {
      // ok
    }
    assertEquals(hasValue, it.hasValue());
    assertEquals(value, hasValue ? it.value() : -1);
  }

  public static void checkIteratorThrowsCME(Iterator iterator) {
    try {
      iterator.hasNext();
      fail();
    } catch (ConcurrentModificationException e) {}

    try {
      iterator.next();
      fail();
    } catch (ConcurrentModificationException e) {}

    if (iterator instanceof IntIterator) {
      IntIterator it = (IntIterator) iterator;
      try {
        it.hasValue();
        fail();
      } catch (ConcurrentModificationException _) {
        // ok
      }
      try {
        it.value();
        fail();
      } catch (ConcurrentModificationException e) {}
    } else if (iterator instanceof LongIterator) {
      LongIterator it = (LongIterator) iterator;
      try {
        it.hasValue();
        fail();
      } catch (ConcurrentModificationException e) {}
      try {
        it.value();
        fail();
      } catch (ConcurrentModificationException e) {}
    } else if (iterator instanceof LongIntIterator) {
      LongIntIterator it = (LongIntIterator) iterator;
      try {
        it.hasValue();
        fail();
      } catch (ConcurrentModificationException e) {}
      try {
        it.left();
        fail();
      } catch (ConcurrentModificationException e) {}
      try {
        it.right();
        fail();
      } catch (ConcurrentModificationException e) {}
    }
  }
}