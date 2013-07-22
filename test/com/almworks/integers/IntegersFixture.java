package com.almworks.integers;

import com.almworks.integers.util.*;
import com.almworks.util.RandomHolder;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Random;

import static com.almworks.integers.LongArray.create;

public abstract class IntegersFixture extends TestCase {
  protected static final IntCollectionsCompare CHECK = new IntCollectionsCompare();

  protected IntSetBuilder prog(int start, int step, int count) {
    IntSetBuilder r = new IntSetBuilder();
    for (int i = 0; i < count; i++)
      r.add(start + i * step);
    return r;
  }

  protected void checkSet(IntSetBuilder builder, int[]... v) {
    IntList collection = builder.clone().toSortedCollection();
    checkSet(collection, v);
  }

  protected void checkSet(IntList collection, int[]... v) {
    IntArray r = new IntArray();
    for (int[] ints : v) {
      r.addAll(ints);
    }
    r.sortUnique();
    int[] expected = r.toNativeArray();
    CHECK.order(collection.iterator(), expected);
  }

  protected void checkList(IntList collection, int[]... v) {
    IntArray r = new IntArray();
    for (int[] ints : v) {
      r.addAll(ints);
    }
    int[] expected = r.toNativeArray();
    CHECK.order(collection.iterator(), expected);
  }

  protected int[] range(int from, int to) {
    return from < to ? ap(from, 1, to - from + 1) : ap(from, -1, from - to + 1);
  }

  protected int[] ap(int start, int step, int count) {
    int[] r = new int[count];
    for (int i = 0; i < r.length; i++) {
      r[i] = start + step * i;
    }
    return r;
  }

  protected void checkCollectionM(IntList collection, int[]... ints) {
    IntArray t = new IntArray();
    for (int[] ar : ints) {
      t.addAll(ar);
    }
    checkCollection(collection, t.toNativeArray());
  }

  protected void checkCollection(IntList collection, int... ints) {
    IntList list = collection instanceof IntList ? (IntList) collection : new IntArray(collection);
    if (ints == null)
      ints = IntegersUtils.EMPTY_INTS;
    CHECK.order(collection.toNativeArray(), ints);
    assertEquals(collection.size(), ints.length);
    CHECK.order(collection.iterator(), ints);
    IntIterator it;
    for (int i = 0; i < ints.length; i++) {
      it = list.iterator();
      if (i > 0) {
        if (it instanceof IntListIterator)
          ((IntListIterator) it).move(i);
        else
          for (int j = 0; j < i; j++)
            it.nextValue();
      }
      CHECK.order(it, IntegersUtils.arrayCopy(ints, i, ints.length - i));
    }
    it = collection.iterator();
    for (int i = 0; i < ints.length; i++) {
      int anInt = ints[i];
      assertTrue(it.hasNext());
      assertEquals(anInt, it.nextValue());
      assertEquals(anInt, list.get(i));
    }
    assertFalse(it.hasNext());
    for (int i = 0; i < ints.length; i++) {
      CheckCollection checker = new CheckCollection(list, i);
      AbstractIntListDecorator.iterate(list, i, ints.length, checker);
      assertEquals(ints.length, checker.index);
      int[] array = new int[ints.length - i + 1];
      list.toArray(i, array, 1, list.size() - i);
      CHECK.order(IntegersUtils.arrayCopy(array, 1, array.length - 1), IntegersUtils.arrayCopy(ints, i, ints.length - i));
    }
    for (int i = ints.length; i >= 0; i--) {
      CheckCollection checker = new CheckCollection(list, 0);
      AbstractIntListDecorator.iterate(list, 0, i, checker);
      assertEquals(i, checker.index);
    }
  }

  protected void checkInsertIndexes(final IntListInsertingDecorator ins, int... expected) {
    if (expected == null)
      expected = IntegersUtils.EMPTY_INTS;
    CHECK.order(ins.insertIndexIterator(), expected);
    IntList base = ins.getBase();
    for (int i = 0; i < base.size(); i++) {
      int newIndex = ins.getNewIndex(i);
      assertTrue(IntegersUtils.indexOf(expected, newIndex) < 0);
      assertEquals(base.get(i), ins.get(newIndex));
    }
    final int[] expectedCopy = expected;
    boolean res = ins.iterate(0, ins.size(), new AbstractIntListDecorator.IntVisitor() {
      int index = -1;

      public boolean accept(int value, IntList source) {
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

  protected void checkRemovedIndexes(final IntListRemovingDecorator rem, int... expected) {
    if (expected == null)
      expected = IntegersUtils.EMPTY_INTS;
    CHECK.order(rem.removedIndexIterator(), expected);
    IntList base = rem.getBase();
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
    boolean res = rem.iterate(0, rem.size(), new AbstractIntListDecorator.IntVisitor() {
      int index = -1;

      public boolean accept(int value, IntList source) {
        index++;
        assertEquals(rem.get(index), value);
        assertSame(rem.getBase(), source);
        return true;
      }
    });
    assertTrue(res);
  }

  private static class CheckCollection implements AbstractIntListDecorator.IntVisitor {
    int index;
    private final IntList myCollection;

    public CheckCollection(IntList collection, int firstIndex) {
      myCollection = collection;
      index = firstIndex;
    }

    public boolean accept(int value, IntList source) {
      assertEquals(value, myCollection.get(index));
      index++;
      return true;
    }
  }

  public static interface BinarySearcher {
    void init(long... values);

    long get(int index);

    int binSearch(long value);

    int size(); // fix it?
  }

  private static void checkBinarySearch(BinarySearcher bs, long ... values) {
    assertTrue(LongArray.create(values).isSorted());
    bs.init(values);

    for (int index = 0; index < bs.size(); index++) {
      for (long i = bs.get(index) - 1; i <= bs.get(index) + 1; i++) {
        int res = bs.binSearch(i);
        int res2 = -res - 1;
        if (res >= 0) {
          assertEquals(i, bs.get(res));
        } else {
          if (res2 == 0) {
            assertTrue(res2 != 0 || i < bs.get(res2));
          } else {
            if (res2 == bs.size()) {
              assertTrue(bs.get(res2 - 1) < i);
            } else {
              assertTrue(bs.get(res2 - 1) <= i && i < bs.get(res2));
            }
          }
        }
      }
    }
  }

  public static void testBinarySearch(BinarySearcher bs) {
    checkBinarySearch(bs, 0, 2, 5, 10);
    checkBinarySearch(bs, 0, 5, 10, 11, 12, 14, 20, 25, 25);

    int arrLength = 100;
    long[] arr = new long[arrLength];
    int maxValue = Integer.MAX_VALUE - 1;
    Random r = new RandomHolder().getRandom();

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < arrLength; j++) {
        arr[j] = r.nextInt();
      }
      Arrays.sort(arr);
      checkBinarySearch(bs, arr);
    }
  }

  public static LongArray[] generateRandomArrays(int intersectionLength, int inputLength, int maxArrayLength, int maxValue) {
    Random r = new RandomHolder().getRandom();

    LongArray[] arrays = new LongArray[inputLength];

    LongArray intersection = create();
    for ( int i = 0; i < intersectionLength; i++) {
      intersection.add(r.nextInt(maxArrayLength));
    }

    for (int i = 0; i < inputLength; i++) {
      int arrayLength = r.nextInt(maxArrayLength);
      arrays[i] = LongArray.copy(intersection);

      for (int j = 0; j < arrayLength; j++) {
        arrays[i].add(r.nextInt(maxValue));
      }
      arrays[i].sortUnique();
      IntegersDebug.println(arrays[i]);
    }
    return arrays;
  }
}
