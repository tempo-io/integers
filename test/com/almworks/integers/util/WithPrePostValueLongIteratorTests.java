package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongList;
public class WithPrePostValueLongIteratorTests extends IntegersFixture {

  public void testSimple() {
    CHECK.order(new WithPrePostValueLongIterator(LongArray.create(0, 1, 2, 3, 10), 239), 0, 1, 2, 3, 10);
  }

  public void testDeviationsFromIntIterator() {
    WithPrePostValueLongIterator iiwppv = new WithPrePostValueLongIterator(LongArray.create(0, 1, 2), 100);
    assertTrue(iiwppv.hasNext());
    // Deviation 1: IntIterator would throw NSEE
    assertEquals(0, iiwppv.value());
    assertTrue(iiwppv.hasNext());
    assertEquals(1, iiwppv.nextValue());
    assertTrue(iiwppv.hasNext());
    assertEquals(2, iiwppv.nextValue());
    assertFalse(iiwppv.hasNext());
    // Deviation 2: IntIterator would throw NSEE, WithPrePostValueLongIterator return postValue
    assertEquals(100, iiwppv.nextValue());
    assertFalse(iiwppv.hasNext());
    assertEquals(100, iiwppv.nextValue());
    assertFalse(iiwppv.hasNext());
  }

  public void testEmpty() {
    WithPrePostValueLongIterator empty = new WithPrePostValueLongIterator(LongList.EMPTY, 10);
    assertFalse(empty.hasNext());
    assertEquals(10, empty.value());
    assertFalse(empty.hasNext());
    assertEquals(10, empty.nextValue());
  }

  public void testAnyPostValue() {
    checkForPostValue(Integer.MIN_VALUE);
    checkForPostValue(0);
  }

  private static void checkForPostValue(int postValue) {
    WithPrePostValueLongIterator iiwppv = new WithPrePostValueLongIterator(LongArray.create(0, 1, 2), postValue);
    CHECK.order(iiwppv, 0, 1, 2);
    assertEquals(postValue, iiwppv.nextValue());
    assertEquals(postValue, iiwppv.nextValue());
  }
}
