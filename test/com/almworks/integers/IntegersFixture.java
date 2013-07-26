package com.almworks.integers;

import com.almworks.integers.util.*;
import com.almworks.util.RandomHolder;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Random;

import static com.almworks.integers.LongArray.create;

public abstract class IntegersFixture extends TestCase {
  protected static final CollectionsCompare CHECK = new CollectionsCompare();

  private static LongArray a(long... values) {
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

  public static LongArray[] generateRandomLongArrays(int intersectionLength, int inputLength, int maxArrayLength, int minValue, int maxValue) {
    assert minValue < maxValue;
    int diff = maxValue - minValue;
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
        arrays[i].add(minValue + r.nextInt(diff));
      }
      arrays[i].sortUnique();
      IntegersDebug.println(arrays[i]);
    }
    return arrays;
  }

  public static interface UnionCreator {
    LongIterator get(LongArray ... arrays);
  }

  private static void checkUnionCreator(UnionCreator uc, LongArray ... arrays) {
    LongArray expected = new LongArray();
    for (LongArray array : arrays) {
      expected.addAll(array);
    }
    expected.sortUnique();
    CHECK.order(expected.iterator(), uc.get(arrays));
    if (arrays.length == 2) {
      CHECK.order(expected.iterator(), uc.get(arrays[1], arrays[0]));
    }
  }

  public static void testUnion(UnionCreator uc, boolean onlyTwo) {
    checkUnionCreator(uc, a(1, 2, 3), a(4, 5, 6));
    checkUnionCreator(uc, a(1, 2, 3, 4, 5), a(4, 5, 6, 7, 8));
    checkUnionCreator(uc, a(1, 3, 5, 7, 9), a(2, 4, 6, 8, 10));
    checkUnionCreator(uc, a(1, 3, 5, 7, 9, 11, 15), a(3, 7, 9));
    checkUnionCreator(uc, a(1, 3, 5, 7, 9), a(1, 9));
    checkUnionCreator(uc, a(1, 3, 5), a());
    checkUnionCreator(uc, a(), a());
    checkUnionCreator(uc, a(Long.MIN_VALUE), a(Long.MIN_VALUE + 1));
    checkUnionCreator(uc, a(Long.MAX_VALUE), a(Long.MAX_VALUE - 1));

    testUnionRandom(uc, 0, 2, 100, 1000000);
    testUnionRandom(uc, 100, 2, 200, 1000000);
    testUnionRandom(uc, 50, 2, 500, 10000000);
    testUnionRandom(uc, 250, 2, 500, 10000000);
    testUnionRandom(uc, 0, 2, 1000, 10000000);
    testUnionRandom(uc, 1000, 2, 1000, 10000000);

    if (!onlyTwo) {
      testUnionRandom(uc, 10, 100, 200, 1000000);
      testUnionRandom(uc, 10, 100, 200, 1000000);
      testUnionRandom(uc, 1, 100, 200, 10000000);
      testUnionRandom(uc, 5, 100, 1000, Integer.MAX_VALUE);
      testUnionRandom(uc, 5, 100, 1000, 1000000);
      testUnionRandom(uc, 0, 1000, 1000, Integer.MAX_VALUE);
    }
  }

  private static void testUnionRandom(UnionCreator uc, int intersectionLength, int arraysNumber, int maxArrayLength, int maxValue) {
    LongArray[] arrays = generateRandomLongArrays(intersectionLength, arraysNumber, maxArrayLength, 0, maxValue);
    checkUnionCreator(uc, arrays);
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
