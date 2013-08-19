package com.almworks.integers.optimized;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongProgression;

public class SlidingLongArrayTests extends IntegersFixture {
  private final CyclicLongQueue myArray = new CyclicLongQueue(5);

  public void setUp() {
    myArray.clear();
  }

  public void testGrow() {
    assertEquals(0, myArray.size());
    myArray.add(10);
    assertEquals(1, myArray.size());
    assertEquals(10, myArray.get(0));
    for (int i = 1; i < 5; ++i) {
      myArray.add(10 + i);
    }
    assertEquals(5, myArray.size());
    for (int i = 0; i < 5; ++i) {
      assertEquals(10 + i, myArray.get(i));
    }
    myArray.add(15);
    assertEquals(6, myArray.size());
    for (int i = 0; i < 6; ++i) {
      assertEquals(10 + i, myArray.get(i));
    }
    for (int i = 6; i < 1000; ++i) {
      myArray.add(10 + i);
    }
    assertEquals(1000, myArray.size());
    for (int i = 0; i < 1000; ++i) {
      assertEquals(10 + i, myArray.get(i));
    }
  }

  public void testShrink() {
    myArray.add(10);
    myArray.add(11);
    myArray.add(12);
    assertEquals(10, myArray.removeFirst());
    assertEquals(11, myArray.removeFirst());
    assertEquals(12, myArray.get(0));
    assertEquals(1, myArray.size());
    for (int i = 0; i < 4; ++i) {
      myArray.add(20 + i);
    }
    CHECK.order(myArray, 12, 20, 21, 22, 23);
    myArray.add(30);
    CHECK.order(myArray, 12, 20, 21, 22, 23, 30);
    assertEquals(12, myArray.removeFirst());
    CHECK.order(myArray, 20, 21, 22, 23, 30);
    for (int i = 0; i < 4; ++i) {
      assertEquals(20 + i, myArray.removeFirst());
    }
    assertEquals(1, myArray.size());
    assertEquals(30, myArray.removeFirst());
    assertEquals(0, myArray.size());
  }

  public void testRandom() {
    for (int rtest = 0; rtest < 100; ++rtest) {
      String msg = "rtest #" + rtest;
      myArray.clear();
      int next = 0;
      int min = 0;
      for (int i = 0; i < 100; ++i) {
        boolean add = IntegersFixture.RAND.nextBoolean();
        if (add) {
          myArray.add(next);
          next += 1;
        } else if (min < next) {
          assertEquals(msg, min, myArray.removeFirst());
          min += 1;
        }
        assertEquals(msg + " i=" + i, next - min, myArray.size());
      }
      CHECK.order(LongProgression.arithmetic(min, next - min), myArray); // fix msg
    }
  }

  public void testBinSearch() {
    for (int i = 0; i < 5; ++i) {
      myArray.add(10 + i*2);
    }
    for (int i = 0; i < 3; ++i) {
      myArray.removeFirst();
    }
    for (int i = 5; i < 8; ++i) {
      myArray.add(10 + i*2);
    }
    CHECK.order(myArray, 16, 18, 20, 22, 24);
    assertEquals(0, myArray.binarySearch(16));
    assertEquals(1, myArray.binarySearch(18));
    assertEquals(2, myArray.binarySearch(20));
    assertEquals(3, myArray.binarySearch(22));
    assertEquals(4, myArray.binarySearch(24));
    assertEquals(-1, myArray.binarySearch(10));
    assertEquals(-1, myArray.binarySearch(14));
    assertEquals(-1, myArray.binarySearch(15));
    assertEquals(-2, myArray.binarySearch(17));
    assertEquals(-3, myArray.binarySearch(19));
    assertEquals(-4, myArray.binarySearch(21));
    assertEquals(-5, myArray.binarySearch(23));
    assertEquals(-6, myArray.binarySearch(25));
    assertEquals(-6, myArray.binarySearch(27));
  }

  public void testPinnedIterator() {
    myArray.addAll(10, 11, 12, 13);
    CyclicLongQueue.PinnedIterator it = myArray.pinnedIterator();

    assertTrue(it.hasNext());
    assertEquals(10, it.nextValue());
    assertEquals(11, it.nextValue());

    myArray.removeFirst();
    assertEquals(11, it.value());

    try {
      myArray.removeFirst();
      fail("should have thrown ISE, state: " + myArray);
    } catch (IllegalStateException expected) {}
    assertEquals(11, it.value());
    assertEquals(11, myArray.get(0));

    assertEquals(12, it.nextValue());

    myArray.removeFirst();
    myArray.addAll(14, 15);
    assertEquals(12, it.value());
    assertTrue(it.hasNext());
    assertEquals(13, it.nextValue());
    assertTrue(it.hasNext());
    assertEquals(14, it.nextValue());
    assertTrue(it.hasNext());
    assertEquals(15, it.nextValue());

    assertFalse(it.hasNext());
    assertEquals(15, it.value());

    assertEquals(2, myArray.removeFirst(2));
    assertFalse(it.hasNext());
    assertEquals(15, it.value());

    try {
      assertEquals(2, myArray.removeFirst(2));
      fail("should have thrown ISE, state: " + myArray);
    } catch (IllegalStateException expected) {}

    assertFalse(it.hasNext());
    assertEquals(15, it.value());
    assertFalse(it.hasNext());
    myArray.addAll(LongProgression.arithmetic(16, 100));
    assertEquals(15, it.value());
    assertTrue(it.hasNext());
    assertEquals(16, it.nextValue());
  }

  public void testAttachDetach() {
    CyclicLongQueue.PinnedIterator it = myArray.pinnedIterator();
    assertFalse(it.hasNext());
    myArray.add(10);
    try {
      myArray.removeFirst();
      fail("should have thrown ISE, state: " + myArray);
    } catch (IllegalStateException expected) {}
    assertEquals(10, it.nextValue());

    it.detach();

    myArray.removeFirst();
    assertEquals(10, it.value());
    myArray.addAll(11, 12, 13, 14, 15);
    CHECK.order(myArray, 11, 12, 13, 14, 15);
    myArray.removeFirst();
    myArray.add(16);
    CHECK.order(myArray, 12, 13, 14, 15, 16);
    // Note: the value has changed!
    assertEquals(16, it.value());

    it.attach();
    assertEquals(16, it.value());
    assertFalse(it.hasNext());

    try {
      myArray.clear();
      fail("should have thrown ISE, state: " + myArray);
    } catch (IllegalStateException expected) {}
    try {
      myArray.removeFirst(5);
      fail("should have thrown ISE, state: " + myArray);
    } catch (IllegalStateException expected) {}
    myArray.removeFirst(4);
    myArray.add(17);
    assertTrue(it.hasNext());
    assertEquals(17, it.nextValue());
    assertFalse(it.hasNext());
  }

  public void testCheckToStringWithPiterators() {
    CyclicLongQueue sia = new CyclicLongQueue(5);

    sia.addAll(10, 11, 12, 13, 14);
    assertEquals("(10, 11, 12, 13, 14)", sia.toStringWithPiterators().toString());

    CyclicLongQueue.PinnedIterator i1 = sia.pinnedIterator().next();
    CyclicLongQueue.PinnedIterator i2 = sia.pinnedIterator().next().next().next();
    CyclicLongQueue.PinnedIterator i3 = sia.pinnedIterator().next().next().next().next().next();
    assertEquals("(10*, 11, 12*, 13, 14*)", sia.toStringWithPiterators().toString());

    sia.detach(i1);
    sia.removeFirst(2);
    sia.addAll(15, 16);
    assertEquals("(12*, 13, 14*, 15, 16)", sia.toStringWithPiterators().toString());

    sia.addAll(LongProgression.arithmetic(17, 10, 1));
    i3.next().next().next().next().next();
    assertEquals("[15] (12*, 13, 14, 15, 16, ..., 19*, ..., 22, 23, 24, 25, 26)", sia.toStringWithPiterators().toString());

    CyclicLongQueue.PinnedIterator i4 = sia.pinnedIterator();
    for (int i = 0; i < 7; ++i) i4.next();
    assertEquals("[15] (12*, 13, 14, 15, 16, ..., 18*, 19*, ..., 22, 23, 24, 25, 26)", sia.toStringWithPiterators().toString());

    CyclicLongQueue.PinnedIterator i5 = sia.pinnedIterator();
    CyclicLongQueue.PinnedIterator i6 = sia.pinnedIterator();
    for (int i = 0; i < 6; ++i) i5.next();
    for (int i = 0; i < 10; ++i) i6.next();
    assertEquals("[15] (12*, 13, 14, 15, 16, 17*, 18*, 19*, ..., 21*, 22, 23, 24, 25, 26)", sia.toStringWithPiterators().toString());
  }
}