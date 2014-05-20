/*
 * Copyright 2014 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almworks.integers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;

public class LongCyclicQueueTests extends LongListChecker<LongCyclicQueue> {
  private LongCyclicQueue myArray = new LongCyclicQueue(5);

  public void setUp() throws Exception {
    super.setUp();
    myArray = new LongCyclicQueue(5);
  }

  @Override
  protected List<LongCyclicQueue> createLongListVariants(long... values) {
    List<LongCyclicQueue> res = new ArrayList<LongCyclicQueue>();

    LongCyclicQueue queue = new LongCyclicQueue(values.length);
    queue.addAll(values);
    res.add(queue);

    int count = values.length / 2;
    LongArray randomArray = generateRandomLongArray(count, UNORDERED);

    queue = new LongCyclicQueue(values.length);
    queue.addAll(randomArray);
    queue.removeFirst(count);
    queue.addAll(values);
    res.add(queue);

    queue = new LongCyclicQueue(values.length * 2);
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
  private void checkRemoveAndCatchISE(LongCyclicQueue queue, int count) {
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
        boolean add = myRand.nextBoolean();
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
    LongCyclicQueue.PinnedIterator it = myArray.pinnedIterator();

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
    LongCyclicQueue.PinnedIterator it = myArray.pinnedIterator();

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
    LongCyclicQueue clq = new LongCyclicQueue();
    assertEquals("()", clq.toStringWithPiterators());

    clq.addAll(10, 11, 12, 13, 14);
    assertEquals("(10, 11, 12, 13, 14)", clq.toStringWithPiterators());

    LongCyclicQueue.PinnedIterator i1 = clq.pinnedIterator().next();
    LongCyclicQueue.PinnedIterator i2 = clq.pinnedIterator().next().next().next();
    LongCyclicQueue.PinnedIterator i3 = clq.pinnedIterator().next().next().next().next().next();
    assertEquals("(10*, 11, 12*, 13, 14*)", clq.toStringWithPiterators());

    clq.detach(i1);
    clq.removeFirst(2);
    clq.addAll(15, 16);
    assertEquals("(12*, 13, 14*, 15, 16)", clq.toStringWithPiterators());

    clq.addAll(LongProgression.arithmetic(17, 10, 1));
    i3.next().next().next().next().next();
    assertEquals("[15] (12*, 13, 14, 15, 16, ..., 19*, ..., 22, 23, 24, 25, 26)", clq.toStringWithPiterators());

    LongCyclicQueue.PinnedIterator i4 = clq.pinnedIterator();
    for (int i = 0; i < 7; ++i) i4.next();
    assertEquals("[15] (12*, 13, 14, 15, 16, ..., 18*, 19*, ..., 22, 23, 24, 25, 26)", clq.toStringWithPiterators());

    clq.pinnedIterator(5);
    LongCyclicQueue.PinnedIterator it = clq.pinnedIterator(9);
    assertEquals("[15] (12*, 13, 14, 15, 16, 17*, 18*, 19*, ..., 21*, 22, 23, 24, 25, 26)", clq.toStringWithPiterators());
    it.next();
    assertEquals("[15] (12*, 13, 14, 15, 16, 17*, 18*, 19*, ..., 21*, 22, 23, 24, 25, 26)", clq.toStringWithPiterators());
    it.next();
    assertEquals("[15] (12*, 13, 14, 15, 16, 17*, 18*, 19*, ..., 22*, 23, 24, 25, 26)", clq.toStringWithPiterators());

    clq = new LongCyclicQueue(11);
    clq.addAll(ap(0, 11, 1));
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

    LongCyclicQueue.PinnedIterator it = myArray.pinnedIterator();
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
    int mSize = 15;
    for (int startIdx = 0; startIdx < mSize; startIdx++) {
      for (int pinnedIdx = 0; pinnedIdx < mSize; pinnedIdx++) {
        LongCyclicQueue queue = new LongCyclicQueue();
        queue.addAll(LongCollections.repeat(-1, startIdx));
        queue.removeFirst(startIdx);
        queue.addAll(LongProgression.range(mSize));

        LongCyclicQueue.PinnedIterator it = queue.pinnedIterator();
        checkRemoveAndCatchISE(queue, -1);
        it.next();
        assertEquals(0, it.index());
        it.detach();

        it = queue.pinnedIterator(pinnedIdx);
        checkRemoveAndCatchISE(queue, pinnedIdx + 1);

        it.next();
        assertEquals(pinnedIdx != mSize - 1, it.hasNext());
        assertEquals("startIdx = " + startIdx, pinnedIdx, it.index());
        queue.addAll(1, 2, 3);
        assertEquals(pinnedIdx, it.index());
        if (pinnedIdx != 0) {
          queue.removeFirst();
          assertEquals(pinnedIdx - 1, it.index());
        }
      }
    }
  }

  public void testPiteratorSimple() {
    LongCyclicQueue clq = new LongCyclicQueue();
    clq.addAll(LongIterators.range(15));
    LongCyclicQueue.PinnedIterator ii = clq.pinnedIterator(3);
    clq.removeFirst(3);

    ii.next();
    assertEquals(0, ii.index());

    ii = clq.pinnedIterator().next();
    assertEquals(0, ii.index());

  }

  public void testAddAll() {
    int attemptsCount = 10;
    int maxSize = 2048;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      myArray = new LongCyclicQueue();
      int firstCount = myRand.nextInt(maxSize / 2);
      int secondCount = firstCount + myRand.nextInt(maxSize / 2);
      myArray.addAll(LongProgression.range(firstCount));
      CHECK.order(myArray, LongProgression.range(firstCount));
      // ensureCapacity
      myArray.addAll(LongProgression.range(firstCount, secondCount));
      CHECK.order(myArray, LongProgression.range(secondCount));
    }
  }

  public void testRemoveFirst() {
    myArray.addAll(LongCollections.repeat(1, 15));
    assertEquals(10, myArray.removeFirst(10));
    assertEquals(5, myArray.removeFirst(10));
    myArray.addAll(LongCollections.repeat(2, 20));
    for (int i = 0; i < 20; i++) {
      assertEquals(1, myArray.removeFirst(1));
    }
    assertEquals(0, myArray.removeFirst(1));
    assertEquals(0, myArray.removeFirst(5));
  }
}