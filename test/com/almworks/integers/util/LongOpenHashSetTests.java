package com.almworks.integers.util;

import com.almworks.integers.*;

public class LongOpenHashSetTests extends WritableLongSetChecker {

  @Override
  protected WritableLongSet createSet() {
    return new LongOpenHashSet();
  }

  @Override
  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new LongOpenHashSet(capacity);
  }

  @Override
  protected WritableLongSet[] createSetFromSortedList(LongList sortedList) {
    WritableLongSet set = createSet();
    set.addAll(sortedList);
    return new WritableLongSet[]{set};
  }

  public void testSimple() {
    WritableLongSet set = new LongOpenHashSet();
    for (long i: ap(0, 2, 10)) {
      set.add(i);
    }
    for (int i = 0; i < 20; i++) {
      assertTrue(i % 2 == 0 ? set.contains(i) : !set.contains(i));
    }
    LongArray expected = new LongArray(ap(0, 2, 10));
    checkSet(set, expected);
//    System.out.println(set.size());
    for (int i = 0; i < 20; i++) {
//      System.out.println(set.size());
      if (i % 2 == 0) {
        if (i % 4 != 0) {
          assertTrue(set.exclude(i));
        }
      } else {
        assertFalse(set.exclude(i));
      }
    }
    expected = new LongArray(ap(0, 4, 5));
    checkSet(set, expected);
    for (int i = 0; i < 20; i++) {
      assertTrue(i % 4 == 0 ? set.contains(i) : !set.contains(i));
    }

    set.clear();
    assertTrue(set.isEmpty());
    for (int i = 0; i < 20; i++) {
      assertFalse(set.contains(i));
    }
  }

  public void testSimple2() {
    int size = 20, maxVal = 1000;
    for (int attempt = 0; attempt < 10; attempt++) {
      if (attempt % 2 == 0) {
        set = createSet();
      } else {
        set.clear();
      }
      LongArray expected = generateRandomLongArray(size, false, maxVal);
      set.addAll(expected);
      expected.sortUnique();
      LongArray actual = new LongArray(set.iterator());
      actual.sortUnique();
      CHECK.order(expected, actual);
    }
  }
}

