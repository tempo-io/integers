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

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;

public abstract class IntegersFixture extends TestCase {
  protected static final CollectionsCompare CHECK = new CollectionsCompare();

  public static final String SEED = "com.almworks.integers.seed";
  public static final Random RAND = createRandom();

  public enum SortedStatus {
    UNORDERED {
      @Override
      public void action(LongArray array) {
      }

      @Override
      public void action(IntArray array) {
      }
    },
    SORTED {
      @Override
      public void action(LongArray array) {
        array.sort();
      }

      @Override
      public void action(IntArray array) {
        array.sort();
      }
    },
    SORTED_UNIQUE {
      @Override
      public void action(LongArray array) {
        array.sortUnique();
      }

      @Override
      public void action(IntArray array) {
        array.sortUnique();
      }
    };
    public abstract void action(LongArray array);
    public abstract void action(IntArray array);
  }

  /**
   * Add {@code -Dcom.almworks.integers.seed=12312423455}
   * to system properties to reproduce the test
   * */
  private static Random createRandom() {
    long seed;
    try {
      seed = Long.getLong(SEED);
      System.out.println("Using seed from settings: " + seed);
    } catch (NullPointerException _) {
      seed = System.currentTimeMillis();
      System.out.println("-Dcom.almworks.integers.seed=" + seed);
    }
    return new Random(seed);
  }

  public static void checkSet(LongSet set, LongList sortedUniqueExpected) {
    assert sortedUniqueExpected.isSortedUnique();
    assertEquals(sortedUniqueExpected.size(), set.size());
    if (set instanceof LongSortedSet) {
      LongArray buffer = LongCollections.collectIterable(set.size(), set.iterator());
      CHECK.order(sortedUniqueExpected, buffer);

      buffer = set.toArray();
      CHECK.order(sortedUniqueExpected, buffer);
    } else {
      LongArray setToIterator = LongCollections.collectIterable(set.size(), set.iterator());
      LongArray setToArray = set.toArray();
      CHECK.order(setToArray, setToIterator);

      setToArray.sort();
      CHECK.order(sortedUniqueExpected, setToArray);
    }
  }

  protected void checkList(LongList collection, long[]... v) {
    LongArray r = new LongArray();
    for (long[] ints : v) {
      r.addAll(ints);
    }
    long[] expected = r.toNativeArray();
    CHECK.order(collection.iterator(), expected);
  }

  public static long[] interval(int from, int to) {
    return from < to ? ap(from, to - from + 1, 1) : ap(from, from - to + 1, -1);
  }

  public static long[] ap(long start, int count, int step) {
    return LongProgression.Arithmetic.nativeArray(start, count, step);
  }

  protected void checkCollectionM(LongList collection, long[]... ints) {
    LongArray t = new LongArray();
    for (long[] ar : ints) {
      t.addAll(ar);
    }
    checkCollection(collection, t.toNativeArray());
  }

  protected void checkCollection(LongList collection, long... ints) {
    if (ints == null)
      ints = IntegersUtils.EMPTY_LONGS;
    CHECK.order(collection.toNativeArray(), ints);
    assertEquals(collection.size(), ints.length);
    CHECK.order(collection.iterator(), ints);
    LongListIterator it;
    for (int i = 0; i < ints.length; i++) {
      it = collection.iterator();
      it.move(i);
      CHECK.order(it, IntegersUtils.arrayCopy(ints, i, ints.length - i));
    }
    it = collection.iterator();
    for (int i = 0; i < ints.length; i++) {
      long anInt = ints[i];
      assertTrue(it.hasNext());
      assertEquals(anInt, it.nextValue());
      assertEquals(anInt, collection.get(i));
    }
    assertFalse(it.hasNext());
    for (int i = 0; i < ints.length; i++) {
      CheckLongCollection checker = new CheckLongCollection(collection, i);
      AbstractLongListDecorator.iterate(collection, i, ints.length, checker);
      assertEquals(ints.length, checker.index);
      long[] array = new long[ints.length - i + 1];
      collection.toNativeArray(i, array, 1, collection.size() - i);
      CHECK.order(IntegersUtils.arrayCopy(array, 1, array.length - 1), IntegersUtils.arrayCopy(ints, i, ints.length - i));
    }
    for (int i = ints.length; i >= 0; i--) {
      CheckLongCollection checker = new CheckLongCollection(collection, 0);
      AbstractLongListDecorator.iterate(collection, 0, i, checker);
      assertEquals(i, checker.index);
    }
  }

  protected void checkRemovedIndexes(final AbstractLongListRemovingDecorator rem, int... expected) {
    if (expected == null)
      expected = IntegersUtils.EMPTY_INTS;
    CHECK.order(rem.removedIndexIterator(), expected);
    LongList base = rem.getBase();
    for (int i = 0; i < base.size(); i++) {
      boolean removed = rem.isRemovedAt(i);
      boolean kept = IntegersUtils.indexOf(expected, i) < 0;
      assertTrue(String.valueOf(i), removed != kept);
      int newIndex = rem.getNewIndex(i);
      if (removed)
        assertEquals(-1, newIndex);
      else {
        assertTrue(newIndex >= 0);
        assertEquals(base.get(i), rem.get(newIndex));
      }
    }
    boolean res = rem.iterate(0, rem.size(), new AbstractLongListDecorator.LongVisitor() {
      int index = -1;

      public boolean accept(long value, LongList source) {
        index++;
        assertEquals(rem.get(index), value);
        assertSame(rem.getBase(), source);
        return true;
      }
    });
    assertTrue(res);
  }

  private static class CheckLongCollection implements AbstractLongListDecorator.LongVisitor {
    int index;
    private final LongList myCollection;

    public CheckLongCollection(LongList collection, int firstIndex) {
      myCollection = collection;
      index = firstIndex;
    }

    public boolean accept(long value, LongList source) {
      assertEquals(value, myCollection.get(index));
      index++;
      return true;
    }
  }

  public static void assertContents(LongIterator it, LongArray values) {
    int index = 0;
    while(it.hasNext()) {
      if(index >= values.size()) {
        fail("Iterator is too long: " + s(it) + " past expected " + s(values.iterator()));
      }
      assertEquals("Wrong value at index " + index, values.get(index), it.nextValue());
      index++;
    }
    assertEquals("Iterator is too short", values.size(), index);
  }

  private static String s(LongIterator it) {
    final StringBuilder b = new StringBuilder();
    while(it.hasNext()) {
      b.append(it.nextValue()).append(", ");
    }
    if(b.length() > 0) {
      b.setLength(b.length() - 2);
    }
    b.insert(0, '[');
    b.append(']');
    return b.toString();
  }

  private static void fillRandomArray(IntCollector collector, int arrayLength, int... minMaxValues) {
    int mLen = minMaxValues.length;
    int minValue = 0, maxValue = Integer.MAX_VALUE;
    switch (mLen) {
      case 0: break;
      case 1: {
        maxValue = minMaxValues[0];
        if (maxValue < 0) throw new IllegalArgumentException();
        break;
      }
      case 2: {
        minValue = minMaxValues[0];
        maxValue = minMaxValues[1];
        break;
      }
    }
    int diff = maxValue - minValue;
    if (diff <= 0) throw new IllegalArgumentException(minValue + " " + maxValue);

    for (int i = 0; i < arrayLength; i++) {
      collector.add(minValue + RAND.nextInt(diff));
    }
  }

  /**
   * @param minMaxValues are the min and max values for arrays. There is 3 possible values for minMaxValues.length
   *                 <ul><li>0 - {@code min = 0, max = Integer.MAX_VALUE}
   *                     <li>1 - {@code min = 0, max = minMaxValues[0]}
   *                     <li>2 - {@code min = minMaxValues[0], max = minMaxValues[1]}
   * @param maxLength the maximum length of returned array. If {@code sortUnique = true}
   *                  the actual length may be less than maxLength.
   * @return {@link LongArray} with values uniformly distributed on the interval [minValue..maxValue)
   * */
  public static LongArray generateRandomLongArray(int maxLength, SortedStatus status, int... minMaxValues) {
    final LongArray res = new LongArray(maxLength);
    fillRandomArray(new IntCollectorAdapter() {
      public void add(int value) {
        res.add(value);
      }
    }, maxLength, minMaxValues);
    status.action(res);
    return res;
  }

  /**
   * @param minMaxValues are the min and max values for arrays. There is 3 possible values for minMaxValues.length
   *                 <ul><li>0 - {@code min = 0, max = Integer.MAX_VALUE}
   *                     <li>1 - {@code min = 0, max = minMaxValues[0]}
   *                     <li>2 - {@code min = minMaxValues[0], max = minMaxValues[1]}
   * @param maxLength the maximum length of returned array. If {@code sortUnique = true}
   *                  the actual length may be less than maxLength.
   * @return {@link IntArray} with values uniformly distributed on the interval [minValue..maxValue)
   * */
  public static IntArray generateRandomIntArray(int maxLength, SortedStatus status, int... minMaxValues) {
    final IntArray res = new IntArray(maxLength);
    fillRandomArray(new IntCollectorAdapter() {
      public void add(int value) {
        res.add(value);
      }
    }, maxLength, minMaxValues);
    status.action(res);
    return res;
  }

  public static LongIterator randomIterator() {
    return new AbstractLongIteratorWithFlag() {
      long myValue;
      @Override
      protected long valueImpl() {
        return myValue;
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {
        myValue = RAND.nextLong();
      }

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }
    };
  }

  public static void setFinalStatic(Object obj, String name, int newValue) throws Exception {
    Field field = obj.getClass().getDeclaredField(name);
    field.setAccessible(true);

    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

    field.set(null, newValue);
  }

  public static LongList asLongs(final IntList list) {
    return new AbstractLongList() {
      @Override
      public int size() {
        return list.size();
      }

      @Override
      public long get(int index) throws NoSuchElementException {
        return list.get(index);
      }
    };
  }
}