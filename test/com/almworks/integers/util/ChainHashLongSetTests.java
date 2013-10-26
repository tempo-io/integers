package com.almworks.integers.util;

import com.almworks.integers.*;

public class ChainHashLongSetTests extends WritableLongSetChecker {

  @Override
  protected WritableLongSet createSet() {
    return new ChainHashLongSet(512, 1000);
  }

  @Override
  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new ChainHashLongSet(512, capacity);
  }

  @Override
  protected WritableLongSet[] createSetFromSortedList(LongList sortedList) {
    WritableLongSet set = createSet();
    set.addAll(sortedList);
    return new WritableLongSet[]{set};
  }

  public void testSimple() {
    ChainHashLongSet set = new ChainHashLongSet(256, 512);
    for (long i: ap(0, 2, 10)) {
      set.add(i);
    }
    for (int i = 0; i < 20; i++) {
      assertTrue(i % 2 == 0 ? set.contains(i) : !set.contains(i));
    }
    LongArray expected = new LongArray(ap(0, 2, 10));
    LongArray actual = set.toArray();
    actual.sortUnique();
    CHECK.order(expected, actual);

    for (int i = 0; i < 20; i++) {
      if (i % 2 == 0) {
        if (i % 4 != 0) {
          assertTrue(set.exclude(i));
        }
      } else {
        assertFalse(set.exclude(i));
      }
    }
    expected = new LongArray(ap(0, 4, 5));
    actual = set.toArray();
    actual.sortUnique();
    CHECK.order(expected, actual);
    for (int i = 0; i < 20; i++) {
      assertTrue(i % 4 == 0 ? set.contains(i) : !set.contains(i));
    }

    set.clear();
    assertTrue(set.isEmpty());
    for (int i = 0; i < 20; i++) {
      assertFalse(set.contains(i));
    }
  }
}
