package com.almworks.integers.util;

import com.almworks.integers.*;
import com.almworks.integers.func.LongFunctions;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.LongCollections.arrayCopy;
import static com.almworks.integers.LongCollections.map;
import static com.almworks.integers.LongCollections.toSorted;

public class LongSetBuilderTests extends IntegersFixture {
  protected static final long MIN = Long.MIN_VALUE, MAX = Long.MAX_VALUE;

  protected List<LongSetBuilder> createBuildersFromSortedUniqueList(LongList sortedUniqueList) {
    ArrayList<LongSetBuilder> sets = new ArrayList();
    LongSetBuilder builder = new LongSetBuilder();
    builder.addAll(sortedUniqueList);
    sets.add(builder);

    int size = sortedUniqueList.size();
    if (size != 0 && size < 300) {
      int numberOfSets = 100;
      for (int attempt = 0; attempt < numberOfSets; attempt++) {
        builder = new LongSetBuilder();
        LongArray addMask = generateRandomLongArray(size, UNORDERED, 2);
        for (int i = 0; i < addMask.size(); i++) {
          if (addMask.get(i) == 0) builder.add(sortedUniqueList.get(i));
        }
        builder.mergeTemp();

        for (int i = 0; i < addMask.size(); i++) {
          if (addMask.get(i) == 1) builder.add(sortedUniqueList.get(i));
        }
        sets.add(builder);
      }
    }
    return sets;
  }

  public void testSimple() {
    check(IntegersUtils.EMPTY_LONGS);
    check(interval(0, 0));
    check(interval(0, 0), interval(0, 0), interval(0, 0));
    check(interval(0, 10));
    check(interval(0, 10), interval(10, 0));
    check(interval(0, 100), interval(100, 0));
    check(interval(0, 100), interval(100, 0), interval(50, 150), interval(10, -200), interval(0, 0));
  }

  protected LongSetBuilder prog(long start, int step, int count) {
    LongSetBuilder r = new LongSetBuilder();
    for (int i = 0; i < count; i++)
      r.add(start + i * step);
    return r;
  }

  protected void checkSet(LongSetBuilder builder, long[]... v) {
    LongList collection = builder.clone().commitToArray();
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

  protected void check(long[] ... v) {
    LongSetBuilder builder = new LongSetBuilder();
    for (long[] ints : v) {
      for (long value : ints) {
        builder.add(value);
      }
    }
    checkSet(builder, v);
  }

  public void testAddRandom() {
    for (int i = 0; i < 20; i++) { // replace 100 with 20 to make test run faster on build agent
      int size = RAND.nextInt(16000) + 10;
      int factor = RAND.nextInt(10) + 1;
      int count = size * factor / 2;
      LongArray set = new LongArray();
      LongSetBuilder builder = new LongSetBuilder(5);
      for (int j = 0; j < count; j++) {
        int v = RAND.nextInt(size);
        set.add(v);
        builder.add(v);
      }
      assertEquals(count, set.size());
      set.sortUnique();
      CHECK.order(builder.commitToArray().iterator(), set.toNativeArray());
    }
  }

  public void testAddAllRandom() {
    int elementsCount = 300;
    for (int i = 0; i < 3; i++) {
      int size = RAND.nextInt(1600) + 10;
      int factor = RAND.nextInt(10) + 1;
      int count = size * factor / 2;
      LongArray set = new LongArray();
      LongSetBuilder builder = new LongSetBuilder(100);
      for (int j = 0; j < count; j++) {
        LongArray v = IntegersFixture.generateRandomLongArray(elementsCount, IntegersFixture.SortedStatus.UNORDERED, size);
        set.addAll(v);
        builder.addAll(v);
      }
      assertEquals(elementsCount * count, set.size());
      set.sortUnique();
      CHECK.order(builder.commitToArray().iterator(), set.toNativeArray());
    }
  }

  public void testMerge() {
    LongSetBuilder b;

    b = new LongSetBuilder(5);
    b.mergeFrom(new LongSetBuilder());
    checkSet(b, IntegersUtils.EMPTY_LONGS);

    b = new LongSetBuilder(5);
    b.mergeFrom(prog(0, 1, 10));
    checkSet(b, interval(0, 9));

    b = new LongSetBuilder(5);
    b.mergeFrom(prog(0, 1, 10));
    b.mergeFrom(prog(0, 1, 10));
    checkSet(b, interval(0, 9));

    b = new LongSetBuilder(5);
    b.mergeFrom(prog(0, 2, 10));
    b.mergeFrom(prog(1, 1, 5));
    b.mergeFrom(prog(4, 1, 25));
    checkSet(b, interval(0, 28));

    b = new LongSetBuilder(10);
    b.mergeFrom(prog(0, 1, 10));
    b.mergeFrom(prog(10, 1, 1));
    b.mergeFrom(prog(11, 1, 1));
    checkSet(b, interval(0, 11));

    b = new LongSetBuilder();
    b.mergeFrom(prog(100, 1, 100));
    b.mergeFrom(prog(0, 1, 100));
    checkSet(b, interval(0, 199));

    b = new LongSetBuilder();
    b.mergeFrom(prog(100, 1, 100));
    checkSet(b, interval(100, 199));
    b.mergeFrom(prog(200, 1, 1));
    checkSet(b, interval(100, 200));
    b.mergeFrom(prog(80, 3, 20));
    checkSet(b, interval(100, 200), new long[] {80, 83, 86, 89, 92, 95, 98});
    b.mergeFrom(prog(70, 1, 5));
    checkSet(b, interval(100, 200), new long[] {80, 83, 86, 89, 92, 95, 98}, interval(70, 74));
    b.mergeFrom(prog(201, 1, 10));
    checkSet(b, interval(100, 210), new long[] {80, 83, 86, 89, 92, 95, 98}, interval(70, 74));
    b.mergeFrom(prog(70, 1, 30));
    checkSet(b, interval(70, 210));

    b = new LongSetBuilder();
    b.mergeFrom(prog(100, 1, 100));
    // make sure mySorted is reallocated with lots of free space
    b.mergeFrom(prog(200, 1, 1));
    b.mergeFrom(prog(190, 1, 5));
    b.mergeFrom(prog(190, 1, 20));
    checkSet(b, interval(100, 209));
  }

  public void testAdd() {
    LongSetBuilder b = new LongSetBuilder();
    assertTrue(b.isEmpty());
    for (int i = 0; i < 8; i++) {
      b.add(i * 2);
    }
    assertFalse(b.isEmpty());
    b.addAll(10, 100, 200);
    b.addAll(LongArray.create(-5, 9, 3));
    b.addAll(LongArray.create(7, 15, 4).iterator());

    LongArray expected = LongArray.create();
    for (int i = 0; i < 8; i++) {
      expected.add(i * 2);
    }
    expected.addAll(10, 100, 200, -5, 9, 3, 7, 15, 4);
    expected.sortUnique();

    LongList collection = b.toList();

    CHECK.order(collection.iterator(), expected.iterator());
  }

  public void testTailIterator() {
    LongSetBuilder b = new LongSetBuilder(100);
    b.addAll(ap(1, 2, 50));
    for (int i = 0; i < 99; i++) {
      assertEquals(i + 1 - (i % 2), b.tailIterator(i).nextValue());
    }
  }

  public void testSize() {
    int attemptsCount = 10, addCount = 20, tempSize = 20;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      int stop = 0;
      LongSetBuilder b = new LongSetBuilder(tempSize);
      for (int i = 0; i < addCount; i++) {
        int start = stop;
        stop = start + RAND.nextInt(tempSize * 2);
        b.addAll(LongProgression.range(start, stop));
        assertEquals(stop, b.size());
      }
    }
  }

  public void testContains() {
    int arSize = 45, maxVal = Integer.MAX_VALUE, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      LongArray arr = generateRandomLongArray(arSize, SORTED_UNIQUE, maxVal);
      LongArray arr2 = LongCollections.collectLists(arr, map(LongFunctions.INC, arr), map(LongFunctions.DEC, arr));
      arr2.sortUnique();
      LongSetBuilder b = new LongSetBuilder();
      b.addAll(arr);
      for (int i = 0; i < arr2.size(); i++) {
        long value = arr2.get(i);
        assertEquals(arr.binarySearch(value) >= 0, b.contains(value));
      }
      b.mergeTemp();
      for (int i = 0; i < arr2.size(); i++) {
        long value = arr2.get(i);
        assertEquals(arr.binarySearch(value) >= 0, b.contains(value));
      }
    }
  }

  public void testIterator() {
    LongList expected = LongArray.create(11,12,13,14,15,16);
    LongSetBuilder b = new LongSetBuilder();
    b.addAll(expected);
    LongArray res = new LongArray();
    for (LongIterator i: b) {
      res.add(i.value());
    }
    CHECK.order(expected, res);

    b.clear(false);
    CHECK.order(LongIterator.EMPTY, b.iterator());
    b.add(10);
    CHECK.order(LongArray.create(10).iterator(), b.iterator());
  }

  protected void checkBounds(LongArray array) {
    long upper = array.size() == 0 ? MIN : array.getLast(0);
    long lower = array.size() == 0 ? MAX : array.get(0);

    for (LongSetBuilder builder : createBuildersFromSortedUniqueList(array)) {
      assertEquals(builder.toString(), upper, builder.getUpperBound());
      assertEquals(builder.toString(), lower, builder.getLowerBound());
    }
  }

  public void testGetBounds() {
    LongArray values = LongArray.create(MIN, MIN + 1, 0, 1, 10, MAX - 1, MAX);
    for (LongArray array : LongCollections.allSubLists(values)) {
      checkBounds(array);
    }
  }

}