package com.almworks.integers.util;

import com.almworks.integers.*;

public class LongAmortizedSortedSetTests extends WritableLongSetChecker {

  protected boolean isSupportTailIterator() {
    return true;
  }

  protected WritableLongSortedSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected WritableLongSortedSet createSetWithCapacity(int capacity) {
    return new LongAmortizedSet();
  }

  protected  WritableLongSortedSet[] createSetFromSortedList(LongList sortedList) {
    return new WritableLongSortedSet[] {LongAmortizedSet.fromSortedList(sortedList)};
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
    LongAmortizedSet set = new LongAmortizedSet(20);
    assertTrue(set.isEmpty());
    set.addAll(0,5,10);
    assertFalse(set.isEmpty());
    set.coalesce();
    assertFalse(set.isEmpty());
    set.removeAll(0, 5, 10);
    assertTrue(set.isEmpty());
    set.coalesce();
    assertTrue(set.isEmpty());
  }

}
