package com.almworks.integers.util;

import com.almworks.integers.*;

public class AmortizedSortedLongSetTempTests extends WritableLongSetChecker {
  protected WritableLongSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new AmortizedSortedLongSetTemp();
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

}
