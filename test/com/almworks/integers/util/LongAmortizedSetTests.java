package com.almworks.integers.util;

import com.almworks.integers.*;

public class LongAmortizedSetTests extends WritableLongSetChecker {

  protected WritableLongSortedSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected WritableLongSortedSet createSetWithCapacity(int capacity) {
    return new LongAmortizedSet();
  }

  protected  WritableLongSortedSet[] createSetFromSortedUniqueList(LongList sortedList) {
    return new WritableLongSortedSet[] {LongAmortizedSet.createFromSortedUnique(sortedList)};
  }

  public void testIteratorCoalesce() {
    LongAmortizedSet set = new LongAmortizedSet();
    set.addAll(2, 4, 6, 8);
    LongIterator it = set.iterator();
    // this is way to run coalesce()
    set.coalesce();
    assertFalse(it.hasValue());
    assertEquals(2, it.nextValue());

    set.addAll(1, 20, 30);
    it = set.iterator();
    it.next();
    it.next();
    set.coalesce();
    assertEquals(2, it.value());

    set.addAll(-10, 1, 5, 11);
    it = set.iterator();
    it.next().next().next();
    set.coalesce();
    assertTrue(it.hasValue());
    it.next();
    assertEquals(4, it.value());

    set.clear();
    set.addAll(0, 1, 2);
    it = set.iterator();
    it.next().next();
    set.toArray();
    assertEquals(2, it.nextValue());
    assertFalse(it.hasNext());
  }

  public void _testToString() {
    LongAmortizedSet set = new LongAmortizedSet();
    set.addAll(0, 2, 4, 6, 8);
    set.coalesce();
    set.addAll(1, 3, 5, 7, 9);
    set.removeAll(0, 3, 6, 9);
    System.out.println(set.toString());
    System.out.println(LongCollections.toBoundedString(set));
  }

  public void testIterators2() {
    set.addAll(ap(0, 1, 10));
    LongIterator it1 = set.iterator();
    for (int i = 0; i < 5; i++) {
      assertEquals(i, it1.nextValue());
    }
    // call coalesce
    CHECK.order(new LongArray(ap(0, 1, 10)), set.toArray());
    CHECK.order(new LongArray(ap(0, 1, 10)).iterator(), set.iterator());
    CHECK.order(new LongArray(ap(5, 1, 5)).iterator(), it1);
  }

  public void testIsEmpty2() {
    // baseList, myAdded, myRemoved
    LongAmortizedSet set = new LongAmortizedSet(20);

    // 0, 0, 0
    assertTrue(set.isEmpty());
    set.addAll(0,5,10);

    // 0, A, 0
    assertFalse(set.isEmpty());
    set.coalesce();

    // A, 0, 0
    assertFalse(set.isEmpty());

    set.addAll(5, 10, 15);

    // AB, AC, 0
    assertFalse(set.isEmpty());

    set.removeAll(20, 25, 30, 35, 40, 45, 50, 55);

    // AB, AC, D
    assertFalse(set.isEmpty());

    set.removeAll(0, 5, 10);

    // A, C, AD
    assertFalse(set.isEmpty());

    set.remove(15);

    // A, 0, AD
    assertTrue(set.isEmpty());

    set.coalesce();

    // 0, 0, D
    assertTrue(set.isEmpty());
  }

  public void testAddRemove2() {
    LongArray array = new LongArray(LongProgression.range(0, 7));
    for (LongArray a : LongCollections.allSubLists(array)) {
      for (LongArray b : LongCollections.allSubLists(array)) {
        for (int i = 0; i < 2; i++) {
          LongAmortizedSet set = new LongAmortizedSet();

          LongArray expected = LongArray.copy(a);
          expected.removeAll(b);
          b.sortUnique();

          set.addAll(a);
          if (i % 2 == 0) {
            set.coalesce();
          }
          set.removeAll(b);

          assertEquals(expected.isEmpty(), set.isEmpty());
          assertEquals(expected.size(), set.size());
        }
      }
    }
  }

  public void testConstructors() {
    int attemptsCount = 10;
    // more than 512
    int size = 600;
    LongAmortizedSet set;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      // LongAmortizedSet.createFromSortedUnique
      LongArray res = generateRandomLongArray( size, IntegersFixture.SortedStatus.SORTED_UNIQUE);
      set = LongAmortizedSet.createFromSortedUnique(res);
      checkSet(set, res);
      set.coalesce();
      checkSet(set, res);

      // LongAmortizedSet.createFromSortedUniqueArray
      res = generateRandomLongArray( size, IntegersFixture.SortedStatus.SORTED_UNIQUE);
      LongArray expected = LongArray.copy(res);
      set = LongAmortizedSet.createFromSortedUniqueArray(res);
      checkSet(set, expected);
      set.coalesce();
      checkSet(set, expected);

      // LongAmortizedSet(WritableLongSortedSet, WritableLongSet, int)
      set = new LongAmortizedSet(0, new LongTreeSet(), new LongTreeSet(), 128);
      set.addAll(expected);
      checkSet(set, expected);
      set.coalesce();
      checkSet(set, expected);
    }
  }

  public void testTailIteratorRandom2() {
    WritableLongSortedSet sortedSet = (WritableLongSortedSet)set;
    final int size = 600,
        testCount = 5;
    LongArray expected = new LongArray(size);
    LongArray testValues = new LongArray(size * 3);
    for (int i = 0; i < testCount; i++) {
      expected.clear();
      testValues.clear();
      sortedSet.clear();
      for (int j = 0; j < size; j++) {
        long val = RAND.nextLong();
        expected.add(val);
        testValues.addAll(val - 1, val, val + 1);
      }
      sortedSet.addAll(expected);
      expected.sortUnique();
      testValues.sortUnique();

      for (LongIterator it : testValues) {
        long key0 = it.value();
        for (long key = key0 - 1; key <= key0 + 1; key++) {
          int ind = expected.binarySearch(key);
          CHECK.order(expected.iterator(ind >= 0 ? ind : -ind - 1), sortedSet.tailIterator(key));
        }
      }
    }
  }

  public void testContains2() {
    LongAmortizedSet set = new LongAmortizedSet();
    int arSize = 200, maxVal = Integer.MAX_VALUE, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      LongArray arr = generateRandomLongArray( arSize, IntegersFixture.SortedStatus.SORTED_UNIQUE, maxVal);
      set.addAll(arr.subList(0, arSize / 2));
      set.coalesce();
      set.addAll(arr.subList(arSize / 2, arSize));
      for (int i = 0; i < arr.size(); i++) {
        for (int j = -1; j < 2; j++) {
          long value = arr.get(i) + j;
          assertEquals(arr.binarySearch(value) >= 0, set.contains(value));
        }
      }
      for (int i = 0; i < arr.size(); i++) {
        for (int j = -1; j < 2; j++) {
          long value = arr.get(i) + j;
          set.remove(value);
          assertFalse(set.contains(value));
        }
      }
    }
  }

  public void testAddAll2() {
    int attempts = 20;
    int addCount = 100, maxVal = Integer.MAX_VALUE;
    LongAmortizedSet set = new LongAmortizedSet();
    for (int attempt = 0; attempt < attempts; attempt++) {
      if (attempt % 3 == 0) set = new LongAmortizedSet();
      set.clear();
      LongArray expected = generateRandomLongArray( addCount, IntegersFixture.SortedStatus.UNORDERED, maxVal);
      int size2 = expected.size() / 2;
      set.addAll(expected.subList(0, size2));
      if (attempt % 2 == 0) {
        set.coalesce();
      }
      set.addAll(expected.subList(size2, expected.size()));

      expected.sortUnique();
      checkSet(set, expected);
    }
  }

  public void testRandom(int maxVal, int setSize, int listSize, int nAttempts) {
    LongAmortizedSet set = new LongAmortizedSet();
    LongArray toAdd = new LongArray(listSize);
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      if (attempt % 3 == 0) set = new LongAmortizedSet();
      LongArray expected = generateRandomLongArray( setSize, IntegersFixture.SortedStatus.UNORDERED, maxVal);
      set.clear();
      set.addAll(expected);
      expected.sortUnique();
      checkSet(set, expected);

      toAdd.clear();
      toAdd.addAll(generateRandomLongArray( listSize, IntegersFixture.SortedStatus.SORTED_UNIQUE, maxVal));
      for (LongIterator it : set) {
        toAdd.removeAllSorted(it.value());
      }
      set.addAll(toAdd);
      LongArray expected2 = LongArray.copy(expected);
      expected2.merge(toAdd);
      checkSet(set, expected2);

      set.removeAll(toAdd);
      set.coalesce();
      checkSet(set, expected);
    }
  }
}
