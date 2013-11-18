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
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class WritableLongSetChecker extends IntegersFixture {
  protected abstract WritableLongSet createSet();

  protected abstract WritableLongSet createSetWithCapacity(int capacity);

  protected abstract WritableLongSet[]  createSetFromSortedList(LongList sortedList) ;

  protected WritableLongSet set;

  private SetOperationsChecker setOperations = new SetOperationsChecker();

  protected void checkSet(WritableLongSet set, LongList sortedExpected) {
    assertEquals(sortedExpected.size(), set.size());
    LongArray res = set.toArray();
    if (!(set instanceof LongSortedSet)) res.sortUnique();
    CHECK.order(sortedExpected, res);
    CHECK.order(sortedExpected.iterator(), res.iterator());
  }

  private void checkSortedStatus(LongArray array) {
    if (set instanceof LongSortedSet) {
      assertTrue(array.isSorted(true));
    }
  }

  public void setUp() throws Exception {
    super.setUp();
    set = createSet();
  }

  protected void testRemove(LongList srcAdd, LongList srcRemove) {
    set.addAll(srcAdd);
    set.removeAll(srcRemove);
    assertEquals(0, set.size());
    assertTrue(set.isEmpty());

    set.addAll(srcAdd);
    for (LongIterator ii : srcRemove) set.remove(ii.value());
    assertTrue(set.isEmpty());
  }

  public void testAddRemove() {
    WritableLongList sourceAdd = LongArray.create(14,7,3,12,6,2,13,8,5,15,4,0,-1,10,-2,20,-6,32);
    WritableLongList sourceRemove = LongArray.create(7,3,6,14,5,8,2,-7,0,15,3,1);
    WritableLongList expected = new LongArray();// = LongArray.create(-6,-2,-1,4,10,12,13,20,32);

    int stateCount = 0;
    LongIterator iiAdd = sourceAdd.iterator();
    LongIterator iiRemove = sourceRemove.iterator();
    for (int i = 0; i < 30; i++) {
      if (stateCount >= 0) {
        set.add(iiAdd.nextValue());
        expected.add(iiAdd.value());
      } else {
        set.remove(iiRemove.nextValue());
        expected.removeAll(iiRemove.value());
      }
      stateCount++;
      if (stateCount == 3) stateCount = -2;
    }
    assertEquals(expected.size(), set.size());
    LongArray actual = set.toArray();
    actual.sortUnique();
    checkSet(set, expected.sortUnique());
  }

  public void testAdd() {
    int attempts = 12;
    int addCount = 100, maxVal = Integer.MAX_VALUE;
    LongArray expected = new LongArray(addCount);
    for (int attempt = 0; attempt < attempts; attempt++) {
//      System.out.println(attempt);
      if (attempt % 3 == 0) set = createSet();
      set.clear();
      expected.clear();
      for (int i = 0, n = RAND.nextInt(addCount); i < n; i++) {
//        System.out.println(i);
        int value = RAND.nextInt(maxVal);
        set.add(value);
        expected.add(value);
      }
      checkSet(set, expected.sortUnique());
    }
  }

  public void testAddAll() {
    LongArray expected = LongArray.create(ap(1, 2, 10));
    set.addAll(expected);
    checkSet(set, expected);
    System.out.println(set.toArray().sortUnique());

    expected.addAll(ap(0, 2, 10));

    set.addAll(expected.toNativeArray());
    expected.sortUnique();
    checkSet(set, expected);

    set.removeAll(expected);
    assertTrue(set.isEmpty());
  }

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
        assertEquals(expectedValues.size(), set.size());
        checkSet(set, expectedValues);
      }
    });
  }

  public void testSimple() {
    set.addAll(3, 1, 5, 2, 0, 4);
    LongList expected = LongProgression.arithmetic(0, 6);
    checkSet(set, expected);
    set.removeAll(expected.toNativeArray());
    assertTrue(set.isEmpty());

    set.clear();
    set.addAll(1, 2, 5, 7, 8, 11, 14, 15);
    assertEquals(8, set.size());
    set.remove(11);
    assertEquals(7, set.size());
    set.remove(256);
    assertEquals(7, set.size());
    set.removeAll(1,2,5);
    assertEquals(4, set.size());
  }

  public void testEdgeCases() {
    assertFalse(set.exclude(Long.MIN_VALUE));
    assertFalse(set.exclude(0));
    set.removeAll(12, 23, 12, 51);
    assertEquals(set.size(), 0);
    assertTrue(set.isEmpty());

    set.addAll(1, 3, 2, Long.MIN_VALUE, Long.MAX_VALUE);
    assertEquals(set.size(), 5);
    if (set instanceof LongSortedSet) {
      assertTrue(set.toArray().isSorted(true));
    }
    assertTrue(set.contains(1));
    assertTrue(set.contains(Long.MIN_VALUE));
    assertFalse(set.contains(0));
  }

  public void testEdgeCases2() {
    LongArray res = LongArray.create(12, 23, 15, 51);
    for (int i = 0; i < res.size(); i++) {
      assertTrue(set.include(res.get(i)));
      assertFalse(set.include(res.get(i)));
    }
    assertEquals(set.size(), 4);
    set.remove(Long.MIN_VALUE);
    assertEquals(set.size(), 4);

    res.sortUnique();
    checkSet(set, res);
    if (set instanceof LongSortedSet) {
      CHECK.order(set.iterator(), res.iterator());
    } else {
      CHECK.unordered(new LongArray(set.iterator()), new LongArray(res.iterator()));
    }

  }

  public void testFromSortedList() throws Exception {
    int testNumber = 10;
    int arraySize = 10000;
    for (int i = 0; i < testNumber; i++) {
      LongArray res = IntegersFixture.generateRandomLongArray(arraySize, true);
      LongArray res2 = new LongArray(res.size() * 3 + 1);
      for (int j = 0, n = res.size(); j < n; j++) {
        long val = res.get(j);
        res2.addAll(val - 1, val, val + 1);
      }
      res2.sortUnique();

      for (WritableLongSet set : createSetFromSortedList(res)) {
        for (int j = 0; j < res2.size(); j++) {
          long val = res2.get(j);
          assertEquals(res.binarySearch(val) >= 0, set.contains(val));
        }
      }
    }
  }
  public void testRandom() {
    int maxVal = 100;
    int setSize = 20, listSize = 20;
    int nAttempts = 10;
//    int maxVal = Integer.MAX_VALUE;
//    int setSize = 510, listSize = 510;
//    int nAttempts = 10;

    LongArray toAdd = new LongArray(listSize);
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
//      System.out.println(attempt);
      if (attempt % 3 == 0) set = createSet();
      LongArray expected = generateRandomLongArray(setSize, false, maxVal);
      set.clear();
      set.addAll(expected);
      expected.sortUnique();
      checkSet(set, expected);

      toAdd.clear();
      toAdd.addAll(generateRandomLongArray(listSize, true, maxVal));
      for (LongIterator it: set) {
        toAdd.removeAllSorted(it.value());
      }
      set.addAll(toAdd);
      set.removeAll(toAdd);
      checkSet(set, expected);
    }
  }

  public void testAddAllEdgeCases() {
    int maxVal = Integer.MAX_VALUE;
    int[] listSizes = {95, 1025};
    int nAttempts = 10;

    for (int i = 0; i < nAttempts; i++) {
      set = createSet();
      for (int j = 0; j < 2; j++) {
        LongArray expected = new LongArray();
        for (int size: listSizes) {
          LongArray array = generateRandomLongArray(size, false, maxVal);
          set.addAll(array);
          expected.addAll(array);
          expected.sortUnique();
          checkSet(set, expected);
        }
        set.clear();
      }
    }
  }

  public void testIteratorExceptions() {
    LongArray res = LongArray.create(2, 4, 6, 8);
    set.clear();
    set.addAll(res);
    LongIterator iterator = set.iterator();
    assertFalse(iterator.hasValue());
    try {
      iterator.value();
    } catch (NoSuchElementException e) {}

    for (int i = 0; i < res.size(); i++) {
      iterator.next();
    }

    assertFalse(iterator.hasNext());
    try {
      iterator.next();
      fail();
    } catch (NoSuchElementException e) {}
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
    } catch (ConcurrentModificationException e) {}
    try {
      it.next();
      fail();
    } catch (ConcurrentModificationException e) {}
    try {
      it.hasValue();
      fail();
    } catch (ConcurrentModificationException e) {}
    try {
      it.value();
      fail();
    } catch (ConcurrentModificationException e) {}
  }

  public void testIsEmpty() {
    assertTrue(set.isEmpty());
    set.addAll(0, 7, 10);
    assertFalse(set.isEmpty());
  }

  public void testRetainSimple() {
    set.addAll(ap(1, 1, 20));
//    System.out.println(set.toArray());
    set.retain(new LongArray(ap(1, 2, 20)));
//    System.out.println(set.toArray());
    checkSet(set, LongArray.create(ap(1, 2, 10)));

//    set.clear();
    set = createSet();
    for (int i = 0; i < 2; i++) {
      set.addAll(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24);
      set.retain(LongArray.create(3, 6, 9, 12, 15, 18, 21, 24));
      checkSet(set, LongArray.create(6, 12, 18, 24));
      set.clear();
    }

    set = createSet();
//    for (int attempt = 0; attempt < 10)
  }

  public void testRetainComplex() {
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        WritableLongSet set = createSetFromSortedList(arrays[0])[0];
        set.retain(arrays[1]);
        return (set instanceof LongSortedSet) ?
            set.iterator() :
            LongCollections.toSorted(false, set).iterator();
      }
    }, new SetOperationsChecker.IntersectionGetter(set instanceof LongSortedSet), set instanceof LongSortedSet, true);
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
    int count = expected.size();
    WritableLongList res = new LongArray();
    set.addAll(expected);
    try {
      for (LongIterator i: set) {
        res.add(i.value());
        count--;
        if (count == 0) {
          set.add(99);
        }
      }
      fail();
    } catch (ConcurrentModificationException e) {}
    if (set instanceof LongSortedSet) {
      CHECK.order(expected, res);
    } else {
      CHECK.unordered(expected, res);
    }

    set.clear();
    CHECK.order(LongIterator.EMPTY, set.iterator());
    set.add(10);
    CHECK.order(LongArray.create(10).iterator(), set.iterator());

  }

  public void testTailIterator() {
    if (!(set instanceof WritableLongSortedSet)) return;
    WritableLongSortedSet sortedSet = (WritableLongSortedSet)set;

    sortedSet.addAll(ap(1, 2, 50));
    System.out.println(LongCollections.toBoundedString(sortedSet.tailIterator(-1)));
    for (int i = 0; i < 99; i++) {
      assertEquals(i + 1 - (i % 2), sortedSet.tailIterator(i).nextValue());
    }

    sortedSet.clear();
    CHECK.order(LongIterator.EMPTY, sortedSet.tailIterator(0));
    sortedSet.add(10);
    CHECK.order(new LongIterator.Single(10), sortedSet.tailIterator(9));
    CHECK.order(LongIterator.EMPTY, sortedSet.tailIterator(11));
  }

  public void testTailIteratorHasMethods() {
    if (!(set instanceof WritableLongSortedSet)) return;
    WritableLongSortedSet sortedSet = (WritableLongSortedSet)set;

    set.addAll(LongProgression.arithmetic(1, 10, 2));
    int curSize = 10;
    for (int i = 0; i < 20; i++) {
      LongIterator iterator = sortedSet.tailIterator(i);
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
    if (!(set instanceof WritableLongSortedSet)) return;
    WritableLongSortedSet sortedSet = (WritableLongSortedSet)set;
    final int size = 300,
        testCount = 5;
    LongArray expected = new LongArray(size);
    LongArray testValues = new LongArray(size * 3);
    for (int i = 0; i < testCount; i++) {
      expected.clear();
      testValues.clear();
      sortedSet.clear();
      for (int j = 0; j < size; j++) {
        long val = RAND.nextLong();
        expected.add(val);
        testValues.addAll(val - 1, val, val + 1);
      }
      sortedSet.addAll(expected);
      expected.sortUnique();
      testValues.sortUnique();

      for (int j = 0; j < testValues.size(); j++) {
        final long key = testValues.get(j);
        int ind = expected.binarySearch(key);
        if (ind < 0) ind = -ind - 1;
        CHECK.order(expected.iterator(ind), sortedSet.tailIterator(key));
      }
    }
  }
}
