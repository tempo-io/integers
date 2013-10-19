/*
 * Copyright 2013 ALM Works Ltd
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

import com.almworks.integers.func.IntProcedure;
import com.almworks.integers.util.LongSetBuilder;

import java.lang.reflect.Method;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class WritableSortedLongSetChecker extends WritableLongSetChecker {
  protected abstract boolean isSupportTailIterator();

  @Override
  protected abstract WritableSortedLongSet createSet();

  @Override
  protected abstract WritableSortedLongSet createSetWithCapacity(int capacity);

  @Override
  protected abstract WritableSortedLongSet[] createSetFromSortedList(LongList sortedList) ;

  protected WritableSortedLongSet set;

  public void setUp() throws Exception {
    super.setUp();
    set = createSet();
  }

  public void testIteratorHasMethods() {
    set.addAll(1, 2, 3, 4, 5, 6, 7);
    LongIterator iterator = set.iterator();
    assertFalse(iterator.hasValue());
    assertTrue(iterator.hasNext());
    iterator.next();
    assertTrue(iterator.hasValue());
    for (int i = 0; i < 6; i++) {
      iterator.next();
    }
    assertTrue(iterator.hasValue());
    assertFalse(iterator.hasNext());
  }

  public void testIterator() {
    LongList expected = LongArray.create(11,12,13,14,15,16);
    WritableLongList res = new LongArray();
    set.addAll(14,12,15,11,13,16);
    try {
      for (LongIterator i: set) {
        res.add(i.value());
        if (i.value() == 16) {
          set.add(99);
        }
      }
      fail();
    } catch (ConcurrentModificationException e) {}
    assertEquals(expected, res);

    set.clear();
    CHECK.order(LongIterator.EMPTY, set.iterator());
    set.add(10);
    CHECK.order(LongArray.create(10).iterator(), set.iterator());

  }

  public void testTailIterator() {
    if (!isSupportTailIterator()) return;
    set.addAll(ap(1, 2, 50));
    System.out.println(LongCollections.toBoundedString(set.tailIterator(-1)));
    for (int i = 0; i < 99; i++) {
      assertEquals(i + 1 - (i % 2), set.tailIterator(i).nextValue());
    }

    set.clear();
    CHECK.order(LongIterator.EMPTY, set.tailIterator(0));
    set.add(10);
    CHECK.order(new LongIterator.Single(10), set.tailIterator(9));
    CHECK.order(LongIterator.EMPTY, set.tailIterator(11));
  }

  public void testTailIteratorHasMethods() {
    if (!isSupportTailIterator()) return;
    set.addAll(LongProgression.arithmetic(1, 10, 2));
    int curSize = 10;
    for (int i = 0; i < 20; i++) {
      LongIterator iterator = set.tailIterator(i);
      assertFalse(iterator.hasValue());
      assertTrue(iterator.hasNext());
      iterator.next();
      assertTrue(iterator.hasValue());
      for (int j = 0; j < curSize - 1; j++) {
        iterator.next();
      }
      assertTrue(iterator.hasValue());
      assertFalse(iterator.hasNext());
      if (i % 2 == 1) curSize--;
    }
  }

  public void testTailIteratorRandom() {
    final int size = 300,
        testCount = 5;
    LongArray expected = new LongArray(size);
    LongArray testValues = new LongArray(size * 3);
    for (int i = 0; i < testCount; i++) {
      expected.clear();
      testValues.clear();
      set.clear();
      for (int j = 0; j < size; j++) {
        long val = RAND.nextLong();
        expected.add(val);
        testValues.addAll(val - 1, val, val + 1);
      }
      set.addAll(expected);
      expected.sortUnique();
      testValues.sortUnique();

      for (int j = 0; j < testValues.size(); j++) {
        final long key = testValues.get(j);
        int ind = expected.binarySearch(key);
        if (ind < 0) ind = -ind - 1;
        CHECK.order(expected.iterator(ind), set.tailIterator(key));
      }
    }
  }
}
