package com.almworks.integers.optimized;

import com.almworks.integers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntegersFixture.SortedStatus.*;

public class CyclicLongQueueTests extends LongListChecker {
  private CyclicLongQueue myArray = new CyclicLongQueue(5);

  public void setUp() throws Exception {
    super.setUp();
    myArray = new CyclicLongQueue(5);
  }

  @Override
  protected List<? extends LongList> createLongListVariants(long... values) {
    List<CyclicLongQueue> res = new ArrayList<CyclicLongQueue>();

    CyclicLongQueue queue = new CyclicLongQueue(values.length);
    queue.addAll(values);
    res.add(queue);

    int count = values.length / 2;
    LongArray randomArray = generateRandomLongArray(count, UNORDERED);

    queue = new CyclicLongQueue(values.length);
    queue.addAll(randomArray);
    queue.removeFirst(count);
    queue.addAll(values);
    res.add(queue);

    queue = new CyclicLongQueue(values.length * 2);
    queue.addAll(randomArray);
    queue.addAll(values);
    queue.removeFirst(count);
    res.add(queue);

    return res;
  }

  /**<ul>
   * <li>if {@code count == -2} tries to invoke {@code queue.clear()}.
   * <li>if {@code count == -1} tries to invoke {@code queue.removeFirst()}.
   * <li>if {@code count >= 0} tries to invoke {@code queue.removeFirst(count)}.
   * </ul>
   */
  private void checkRemoveAndCatchISE(CyclicLongQueue queue, int count) {
    try {
      if (count == -2) {
        queue.clear();
      } else if (count == -1) {
        queue.removeFirst();
      } else {
        queue.removeFirst(count);
      }
      fail("should have thrown ISE, state: " + queue);
    } catch (IllegalStateException _) {
      // ok
    }
  }

  public void testGrow() {
    assertEquals(0, myArray.size());
    myArray.add(10);
    assertEquals(1, myArray.size());
    assertEquals(10, myArray.get(0));
    assertEquals(10, myArray.peek());
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
    assertEquals(12, myArray.peek());
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

  public void testRandomAddRemove() {
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
          assertEquals(msg + " i=" + i, min, myArray.removeFirst());
          min += 1;
        }
        assertEquals(msg + " i=" + i, next - min, myArray.size());
      }
      CHECK.order(LongProgression.arithmetic(min, next - min), myArray); // fix msg
    }
  }

  public void testPinnedIterator() {
    myArray.addAll(10, 11, 12, 13);
    CyclicLongQueue.PinnedIterator it = myArray.pinnedIterator();

    assertTrue(it.hasNext());
    assertEquals(10, it.nextValue());
    assertEquals(11, it.nextValue());

    myArray.removeFirst();
    assertEquals(11, it.value());

    checkRemoveAndCatchISE(myArray, -1);

    assertEquals(11, it.value());
    assertEquals(11, myArray.get(0));
    assertEquals(11, myArray.peek());

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

    checkRemoveAndCatchISE(myArray, 2);

    assertFalse(it.hasNext());
    assertEquals(15, it.value());
    assertFalse(it.hasNext());
    myArray.addAll(LongProgression.arithmetic(16, 100));
    assertEquals(15, it.value());
    assertTrue(it.hasNext());
    assertEquals(16, it.nextValue());
  }

  public void testPinnedIteratorExceptions() {
    try {
      myArray.pinnedIterator();
      fail();
    } catch (NoSuchElementException _) {
      // ok
    }
  }

  public void testAttachDetach() {
    myArray.add(10);
    CyclicLongQueue.PinnedIterator it = myArray.pinnedIterator();

    checkRemoveAndCatchISE(myArray, -1);

    assertEquals(10, it.nextValue());

    it.detach();

    myArray.removeFirst();
    assertEquals(10, it.value());
    myArray.addAll(LongArray.create(11, 12, 13, 14, 15).iterator());
    CHECK.order(myArray, 11, 12, 13, 14, 15);
    myArray.removeFirst();
    myArray.add(16);
    CHECK.order(myArray, 12, 13, 14, 15, 16);
    // Note: the value has changed!
    assertEquals(16, it.value());

    it.attach();
    assertEquals(16, it.value());
    assertFalse(it.hasNext());

    checkRemoveAndCatchISE(myArray, -2);
    checkRemoveAndCatchISE(myArray, 5);

    myArray.removeFirst(4);
    myArray.add(17);
    assertTrue(it.hasNext());
    assertEquals(17, it.nextValue());
    assertFalse(it.hasNext());
  }

  public void testCheckToStringWithPiterators() {
    CyclicLongQueue clq = new CyclicLongQueue();
    assertEquals("()", clq.toStringWithPiterators());

    clq.addAll(10, 11, 12, 13, 14);
    assertEquals("(10, 11, 12, 13, 14)", clq.toStringWithPiterators());

    CyclicLongQueue.PinnedIterator i1 = clq.pinnedIterator().next();
    CyclicLongQueue.PinnedIterator i2 = clq.pinnedIterator().next().next().next();
    CyclicLongQueue.PinnedIterator i3 = clq.pinnedIterator().next().next().next().next().next();
    assertEquals("(10*, 11, 12*, 13, 14*)", clq.toStringWithPiterators());

    clq.detach(i1);
    clq.removeFirst(2);
    clq.addAll(15, 16);
    assertEquals("(12*, 13, 14*, 15, 16)", clq.toStringWithPiterators());

    clq.addAll(LongProgression.arithmetic(17, 10, 1));
    for (int i = 0; i < 5; i++) i3.next();
    assertEquals("[15] (12*, 13, 14, 15, 16, ..., 19*, ..., 22, 23, 24, 25, 26)", clq.toStringWithPiterators());

    CyclicLongQueue.PinnedIterator i4 = clq.pinnedIterator();
    for (int i = 0; i < 7; ++i) i4.next();
    assertEquals("[15] (12*, 13, 14, 15, 16, ..., 18*, 19*, ..., 22, 23, 24, 25, 26)", clq.toStringWithPiterators());

    CyclicLongQueue.PinnedIterator i5 = clq.pinnedIterator();
    CyclicLongQueue.PinnedIterator i6 = clq.pinnedIterator();
    for (int i = 0; i < 6; ++i) i5.next();
    for (int i = 0; i < 10; ++i) i6.next();
    assertEquals("[15] (12*, 13, 14, 15, 16, 17*, 18*, 19*, ..., 21*, 22, 23, 24, 25, 26)", clq.toStringWithPiterators());

    clq = new CyclicLongQueue(11);
    clq.addAll(ap(0, 1, 11));
    assertEquals("[11] (0, 1, 2, 3, 4, ..., 6, 7, 8, 9, 10)", clq.toStringWithPiterators());
    clq.pinnedIterator(5);
    assertEquals("[11] (0, 1, 2, 3, 4, 5*, 6, 7, 8, 9, 10)", clq.toStringWithPiterators());

    clq.removeFirst(5);
    clq.addAll(11, 12, 13);
    clq.pinnedIterator(8);
    assertEquals(5, clq.peek());
    assertEquals("(5*, 6, 7, 8, 9, 10, 11, 12, 13*)", clq.toStringWithPiterators());
  }

  public void testPiteratorIndex() {
    myArray.addAll(LongIterators.range(15));
    CHECK.order(LongProgression.range(15), myArray);

    CyclicLongQueue.PinnedIterator it = myArray.pinnedIterator();
    try {
      it.index();
      fail();
    } catch (NoSuchElementException _) {
      // ok
    }
    for (int i = 0; i < 10; i++) {
      it.next();
      assertEquals(i, it.index());
    }
    myArray.addAll(15, 16, 17);
    assertEquals(9, it.index());

    for (int i = 1; i < 10; i++) {
      myArray.removeFirst();
      assertEquals(9 - i, it.index());
    }
  }

  public void testPiteratorIndex2() {
    int mSize = 16;
    for (int startIdx = 0; startIdx < mSize; startIdx++) {
      for (int pinnedIdx = 0; pinnedIdx < mSize; pinnedIdx++) {
        CyclicLongQueue queue = new CyclicLongQueue();
        queue.addAll(LongCollections.repeat(-1, startIdx));
        queue.removeFirst(startIdx);
        queue.addAll(LongProgression.range(mSize));

        CyclicLongQueue.PinnedIterator it = queue.pinnedIterator(pinnedIdx);
        try {
          queue.removeFirst(pinnedIdx + 1);
          fail();
        } catch (IllegalStateException _) {
          // ok
        }
        it.next();

        assertEquals(pinnedIdx + 1, it.index());
        queue.addAll(1, 2, 3);
        assertEquals(pinnedIdx + 1, it.index());
        if (pinnedIdx != 0) {
          queue.removeFirst();
          assertEquals(pinnedIdx, it.index());
        }
      }
    }
  }

}