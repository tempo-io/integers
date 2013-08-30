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
import com.almworks.integers.util.AmortizedSortedLongSetTemp;
import com.almworks.integers.util.IntegersDebug;
import com.almworks.integers.util.LongSetBuilder;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class WritableLongSetChecker extends IntegersFixture {
  protected abstract WritableLongSet createSet() throws Exception;

  protected abstract WritableLongSet createSetWithCapacity(int capacity) throws Exception;

//  protected abstract WritableLongSet createSetFromSortedList(LongList sortedList) throws Exception;

  protected WritableLongSet set;

  public void setUp() throws Exception {
    super.setUp();
    set = createSet();
    System.setProperty("integers.test", "true");
  }

  public void testClear() throws Exception {

  }

  protected void testRemove(LongList srcAdd, LongList srcRemove) {
    set.addAll(srcAdd);
    set.removeAll(srcRemove.toNativeArray());
    assertTrue(set.isEmpty());

    set.addAll(srcAdd);
    for (LongIterator ii : srcRemove) set.remove(ii.value());
    assertTrue(set.isEmpty());
  }

  public void testRemoveAll() throws Exception {

  }

  public void testRetain() throws Exception {

  }

  public void testInclude() throws Exception {

  }

  public void testExclude() throws Exception {

  }

//  public void testAddRemove() {
//    WritableLongList sourceAdd = LongArray.create(14,7,3,12,6,2,13,8,5,15,4,0,-1,10,-2,20,-6,32);
//    WritableLongList sourceRemove = LongArray.create(7,3,6,14,5,8,2,-7,0,15,3,1);
//    LongList expected = LongArray.create(-6,-2,-1,4,10,12,13,20,32);
//
//    int stateCount = 0;
//    LongIterator iiAdd = sourceAdd.iterator();
//    LongIterator iiRemove = sourceRemove.iterator();
//    for (int i = 0; i < 30; i++) {
//      if (stateCount >= 0) {
//        set.add(iiAdd.nextValue());
//      } else {
//        set.remove(iiRemove.nextValue());
//      }
//      stateCount++;
//      if (stateCount == 3) stateCount = -2;
//    }
//    assertEquals(expected, set.toLongArray());
//  }
//
//  public void testTailIteratorSimple() {
//    LongArray expected = LongArray.create(ap(1, 2, 10));
//    set.addAll(expected);
//    assertEquals(expected, set.toLongArray());
//    int ind = 0;
//
//    LongIterator iterator = set.tailIterator(5);
//    System.out.println(iterator.hasValue());
//    for (int i = 0; i < 21; i++) {
//      CHECK.order(expected.iterator(ind), set.tailIterator(i));
//      if (i % 2 == 1) ind++;
//    }
//  }
//
//  public void testTailIteratorHasMethods() {
//    set.addAll(LongProgression.arithmetic(1, 10, 2));
//    int curSize = 10;
//    for (int i = 0; i < 20; i++) {
//      LongIterator iterator = set.tailIterator(i);
//      assertFalse(iterator.hasValue());
//      assertTrue(iterator.hasNext());
//      iterator.next();
//      assertTrue(iterator.hasValue());
//      for (int j = 0; j < curSize - 1; j++) {
//        iterator.next();
//      }
//      assertTrue(iterator.hasValue());
//      assertFalse(iterator.hasNext());
//      if (i % 2 == 1) curSize--;
//    }
//  }
//
//  public void testTailIteratorRandom() {
//    final int size = 1000,
//        testCount = 20;
//    LongArray expected = new LongArray(size);
//    LongArray testValues = new LongArray(size * 3);
//    for (int i = 0; i < testCount; i++) {
//      expected.clear();
//      testValues.clear();
//      set.clear();
//      for (int j = 0; j < size; j++) {
//        long val = RAND.nextLong();
//        expected.add(val);
//        testValues.addAll(val - 1, val, val + 1);
//      }
//      set.addAll(expected);
//      expected.sortUnique();
//      testValues.sortUnique();
//
//      for (int j = 0; j < testValues.size(); j++) {
//        final long key = testValues.get(j);
//        int ind = expected.binarySearch(key);
//        if (ind < 0) ind = -ind - 1;
//        CHECK.order(expected.iterator(ind), set.tailIterator(key));
//      }
//    }
//  }
//


//ouo

  /** Prefixed with _ because it runs fairly long. */
  public void _testAllPermutations() {
    final LongArray toAdd = LongArray.create(1, 2, 5, 7, 8, 11, 14, 15);
    IntegersUtils.allPermutations(toAdd.size(), new IntProcedure() {
      private int count;
      private final LongArray toAddWith4 = LongArray.copy(toAdd);

      {
        toAddWith4.add(4);
        runTest();
      }

      @Override
      public void invoke(int swapIdx) {
        toAdd.swap(swapIdx, swapIdx + 1);
        runTest();
      }

      private void runTest() {
        System.err.println(count++ + " ");
        set.clear();
        set.addAll(toAdd);
        check(toAdd);
        set.add(4);
        check(toAddWith4);
      }

      private void check(LongList expectedValues) {
        LongArray sorted = new LongArray(set.toList());
        assertTrue( sorted.checkSorted(true));
        assertEquals( expectedValues.size(), set.size());
        CHECK.unordered(sorted, expectedValues);
      }
    });
  }

  public void testSimple() {
    set.addAll(3, 1, 5, 2, 0, 4);
    LongList expected = LongProgression.arithmetic(0, 6);
    CHECK.order(expected, set.toList());
    set.removeAll(expected.toNativeArray());
    assertTrue(set.isEmpty());

    set.clear();
    set.addAll(1, 2, 5, 7, 8, 11, 14, 15);
    set.add(4);
    set.remove(11);
    set.remove(256);
  }

  public void testEdgeCases() {
    assertFalse(set.exclude(Long.MIN_VALUE));
    assertFalse(set.exclude(0));
    set.removeAll(12, 23, 12, 51);
    assertTrue(set.isEmpty());

    set.addAll(1, 3, 2, Long.MIN_VALUE, Long.MAX_VALUE);
    assertTrue(new LongArray(set.toList()).checkSorted(true));
    assertTrue(set.contains(1));
    assertTrue(set.contains(Long.MIN_VALUE));
    assertFalse(set.contains(0));
  }

  public void testFromSortedList() throws Exception {
    int testNumber = 10;
    int arraySize = 10000;
    for (int i = 0; i < testNumber; i++) {
      LongArray res = IntegersFixture.generateRandomArray(arraySize, true);
      LongArray res2 = LongArray.copy(res);
      for (int j = 0, n = res.size(); j < n; j++) {
        res2.addAll(res.get(j) - 1, res.get(j) + 1);
      }
      res2.sortUnique();

//      set = createSetFromSortedList(res);
      for (int j = 0; j < res2.size(); j++) {
        
      }

    }
  }

  public void testEdgeCases2() {
    LongArray res = LongArray.create(12, 23, 12, 51);
    set.addAll(12, 23, 12, 51);
    set.remove(Long.MIN_VALUE);

    res.sortUnique();
    CHECK.order(res, set.toList());
    CHECK.order(res.iterator(), set.iterator());
  }

  public void testRandom() throws Exception {
    int[] setSizes = new int[]{510, 513, 1025, 2049, 4097}; // testing sizes near 2^n
    int nAttempts = 5;
    LongSetBuilder anotherSet = new LongSetBuilder();
//    set = createSet();
    set = createSetWithCapacity(510);
    WritableLongList toAdd = new LongArray();
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      for (int i = 0; i < setSizes[attempt]; ++i) {
        toAdd.add(RAND.nextLong());
      }
      set.addAll(toAdd);
      anotherSet.addAll(toAdd);
      LongList anotherSetList = anotherSet.toTemporaryReadOnlySortedCollection();
      LongList setList = set.toList();
      System.err.println("attempt #" + attempt);
      CHECK.order(anotherSetList, setList);

      // testRemove runs long, so it's ran only twice
      if (attempt > 1) continue;
      testRemove(toAdd, toAdd);
      testRemove(toAdd, anotherSetList);
      testRemove(anotherSetList, toAdd);
      testRemove(anotherSetList, anotherSetList);
    }
  }

  public void testIterator() {
    LongList expected = LongArray.create(11,12,13,14,15,16);
    WritableLongList res = new LongArray();
    set.addAll(14,12,15,11,13,16);
    try {
      for (LongIterator i : set) {
        res.add(i.value());
        if (i.value() == 16) set.add(99);
      }
      fail();
    } catch (ConcurrentModificationException e) {}
    assertEquals(expected, res);
  }

  public void testIteratorExceptions() {
    LongArray res = LongArray.create(2, 4, 6, 8);
    set.clear();
    set.addAll(res);
    LongIterator iterator = set.iterator();
    assertFalse(iterator.hasValue());
    try {
      iterator.value();
    } catch (NoSuchElementException e) {};

    for (int i = 0; i < res.size(); i++) {
      iterator.next();
    }

    assertFalse(iterator.hasNext());
    try {
      iterator.next();
      fail();
    } catch (NoSuchElementException e) {};
  }

  public void testIteratorConcurrentModificationException() {
    set.addAll(2, 4, 6, 8);
    LongIterator it = set.iterator();
    set.add(10);
    catchAllIteratorOperations(it);

    it = set.iterator();
    set.remove(5);
    catchAllIteratorOperations(it);
  }

  public void catchAllIteratorOperations(LongIterator it) {
    try {
      it.hasNext();
      fail();
    } catch (ConcurrentModificationException e) {};
    try {
      it.next();
      fail();
    } catch (ConcurrentModificationException e) {};
    try {
      it.hasValue();
      fail();
    } catch (ConcurrentModificationException e) {};
    try {
      it.value();
      fail();
    } catch (ConcurrentModificationException e) {};
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

  public void testTailIterator() {
    set.addAll(ap(1, 2, 50));
    for (int i = 0; i < 99; i++) {
      assertEquals(i + 1 - (i % 2), set.tailIterator(i).nextValue());
    }
  }

  public void testIsEmpty() {
    set.addAll(0, 7, 10);
    assertFalse(set.isEmpty());
  }

}