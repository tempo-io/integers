package com.almworks.integers;

import com.almworks.integers.util.*;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

public abstract class IntegersFixture extends TestCase {
  protected static final CollectionsCompare CHECK = new CollectionsCompare();

  public static final String SEED = "com.almworks.integers.seed";
  public static final Random RAND = createRandom();

  public void setUp() {
    System.setProperty("integers.test", "true");
  }

  /**
   * Add {@code -Dcom.almworks.integers.seed=12312423455}
   * to system properties to reproduce the test
   * */
  private static Random createRandom() {
    String seedStr = System.getProperty(SEED, "");
    long seed;
    try {
      seed = Long.parseLong(seedStr);
      System.out.println("Using seed from settings: " + seed);
    } catch (NumberFormatException _) {
      seed = System.currentTimeMillis();
      System.out.println("-Dcom.almworks.integers.seed=" + seed);
    }
    return new Random(seed);
  }

  protected static LongArray a(long... values) {
    return new LongArray(values);
  }

  protected LongSetBuilder prog(long start, int step, int count) {
    LongSetBuilder r = new LongSetBuilder();
    for (int i = 0; i < count; i++)
      r.add(start + i * step);
    return r;
  }

  protected void checkSet(LongSetBuilder builder, long[]... v) {
    LongList collection = builder.clone().toSortedList();
    checkSet(collection, v);
  }

  protected void checkSet(LongList collection, long[]... v) {
    LongArray r = new LongArray();
    for (long[] ints : v) {
      r.addAll(ints);
    }
    r.sortUnique();
    long[] expected = r.toNativeArray();
    CHECK.order(collection.iterator(), expected);
  }

  protected void checkList(LongList collection, long[]... v) {
    LongArray r = new LongArray();
    for (long[] ints : v) {
      r.addAll(ints);
    }
    long[] expected = r.toNativeArray();
    CHECK.order(collection.iterator(), expected);
  }

  protected long[] range(int from, int to) {
    return from < to ? ap(from, 1, to - from + 1) : ap(from, -1, from - to + 1);
  }

  protected long[] ap(long start, int step, int count) {
    long[] r = new long[count];
    long cur = start;
    for (int i = 0; i < r.length; i++) {
      r[i] = cur;
      cur += step;
    }
    return r;
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
    LongIterator it;
    for (int i = 0; i < ints.length; i++) {
      it = collection.iterator();
      if (i > 0) {
          ((LongListIterator) it).move(i);
      }
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
      collection.toArray(i, array, 1, collection.size() - i);
      CHECK.order(IntegersUtils.arrayCopy(array, 1, array.length - 1), IntegersUtils.arrayCopy(ints, i, ints.length - i));
    }
    for (int i = ints.length; i >= 0; i--) {
      CheckLongCollection checker = new CheckLongCollection(collection, 0);
      AbstractLongListDecorator.iterate(collection, 0, i, checker);
      assertEquals(i, checker.index);
    }
  }

  protected void checkInsertIndexes(final LongListInsertingDecorator ins, int... expected) {
    if (expected == null)
      expected = IntegersUtils.EMPTY_INTS;
    CHECK.order(ins.insertIndexIterator(), expected);
    LongList base = ins.getBase();
    for (int i = 0; i < base.size(); i++) {
      int newIndex = ins.getNewIndex(i);
      assertTrue(IntegersUtils.indexOf(expected, newIndex) < 0);
      assertEquals(base.get(i), ins.get(newIndex));
    }
    final int[] expectedCopy = expected;
    boolean res = ins.iterate(0, ins.size(), new AbstractLongListDecorator.LongVisitor() {
      int index = -1;

      public boolean accept(long value, LongList source) {
        index++;
        assertEquals(value, ins.get(index));
        if (IntegersUtils.indexOf(expectedCopy, index) >= 0)
          assertSame(ins, source);
        else
          assertSame(ins.getBase(), source);
        return true;
      }
    });
    assertTrue(res);
  }

  protected void checkRemovedIndexes(final LongListRemovingDecorator rem, int... expected) {
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

  /**
   * @param minMaxValues the min and max values for arrays. There is 3 possible values for minMaxValues.length
   *                 <ul><li>0 - {@code min = 0, max = Integer.MAX_VALUE}
   *                     <li>1 - {@code min = 0, max = minMaxValues[0]}
   *                     <li>2 - {@code min = minMaxValues[0], max = minMaxValues[1]}
   * @return {@link LongArray} with values uniformly distributed on the interval [minValue..maxValue)
   * */
  public static LongArray generateRandomArray(int maxArrayLength, boolean isSortUnique, int ... minMaxValues) {
    int mLen = minMaxValues.length;
    int minValue = 0, maxValue = Integer.MAX_VALUE;
    switch (mLen) {
      case 0: break;
      case 1: {
        maxValue = minMaxValues[0];
        if (maxValue < 0) throw new IllegalArgumentException();
      }
      case 2: {
        minValue = minMaxValues[0];
        maxValue = minMaxValues[1];
      }
    }
    int diff = maxValue - minValue;
    if (diff <= 0) throw new IllegalArgumentException();

    LongArray res = new LongArray(maxArrayLength);
    for (int i = 0; i < maxArrayLength; i++) {
      res.add(minValue + RAND.nextInt(diff));
    }
    if (isSortUnique) {
      res.sortUnique();
    }
    IntegersDebug.println(res);
    return res;
  }

  static void setFinalStatic(Object obj, String name, int newValue) throws Exception {
    Field field = obj.getClass().getDeclaredField(name);
    field.setAccessible(true);

    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

    field.set(null, newValue);
  }
}
