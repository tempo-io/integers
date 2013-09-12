package com.almworks.integers.util;

import com.almworks.integers.*;

public class AmortizedSortedLongSetTempTests extends WritableLongSetChecker {
  protected WritableLongSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new AmortizedSortedLongSetTemp();
  }

  protected  WritableLongSet[] createSetFromSortedList(LongList sortedList) {
    return new WritableLongSet[]{AmortizedSortedLongSetTemp.fromSortedList(sortedList)};
  }

  public void testIteratorCoalesce() {
    AmortizedSortedLongSetTemp set = new AmortizedSortedLongSetTemp();
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
    set.toList();
    assertEquals(2, it.nextValue());
    assertFalse(it.hasNext());
  }

  public void _testToString() {
    AmortizedSortedLongSetTemp set = new AmortizedSortedLongSetTemp();
    set.addAll(0, 2, 4, 6, 8);
    set.coalesce();
    set.addAll(1, 3, 5, 7, 9);
    set.removeAll(0, 3, 6, 9);
    System.out.println(set.toString());
    System.out.println(LongCollections.toBoundedString(set));
  }

}
