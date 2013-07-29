package com.almworks.integers;

import com.almworks.integers.util.*;
import com.almworks.util.RandomHolder;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Random;

import static com.almworks.integers.LongArray.create;

public abstract class IntegersFixture extends TestCase {
  protected static final CollectionsCompare CHECK = new CollectionsCompare();

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
    LongList collection = builder.clone().toSortedCollection();
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
    for (int i = 0; i < r.length; i++) {
      r[i] = start + step * i;
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
    LongList list = collection instanceof LongList ? (LongList) collection : new LongArray(collection);
    if (ints == null)
      ints = IntegersUtils.EMPTY_LONGS;
    CHECK.order(collection.toNativeArray(), ints);
    assertEquals(collection.size(), ints.length);
    CHECK.order(collection.iterator(), ints);
    LongIterator it;
    for (int i = 0; i < ints.length; i++) {
      it = list.iterator();
      if (i > 0) {
        if (it instanceof LongListIterator)
          ((LongListIterator) it).move(i);
        else
          for (int j = 0; j < i; j++)
            it.nextValue();
      }
      CHECK.order(it, IntegersUtils.arrayCopy(ints, i, ints.length - i));
    }
    it = collection.iterator();
    for (int i = 0; i < ints.length; i++) {
      long anInt = ints[i];
      assertTrue(it.hasNext());
      assertEquals(anInt, it.nextValue());
      assertEquals(anInt, list.get(i));
    }
    assertFalse(it.hasNext());
    for (int i = 0; i < ints.length; i++) {
      CheckLongCollection checker = new CheckLongCollection(list, i);
      AbstractLongListDecorator.iterate(list, i, ints.length, checker);
      assertEquals(ints.length, checker.index);
      long[] array = new long[ints.length - i + 1];
      list.toArray(i, array, 1, list.size() - i);
      CHECK.order(IntegersUtils.arrayCopy(array, 1, array.length - 1), IntegersUtils.arrayCopy(ints, i, ints.length - i));
    }
    for (int i = ints.length; i >= 0; i--) {
      CheckLongCollection checker = new CheckLongCollection(list, 0);
      AbstractLongListDecorator.iterate(list, 0, i, checker);
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
}
