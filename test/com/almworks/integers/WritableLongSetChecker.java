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
import com.almworks.integers.func.LongFunctions;
import com.almworks.integers.util.LongAmortizedSet;

import java.util.*;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.LongCollections.map;
import static com.almworks.integers.LongCollections.sizeOfIterable;
import static com.almworks.integers.LongCollections.toSorted;
import static com.almworks.integers.LongProgression.range;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public abstract class WritableLongSetChecker<T extends WritableLongSet> extends IntegersFixture {
  protected abstract T createSet();

  protected abstract T createSetWithCapacity(int capacity);

  protected abstract List<T> createSetFromSortedUniqueList(LongList sortedUniqueList);

  protected abstract T create1SetFromSortedUniqueList(LongList sortedUniqueList);

  protected T set;

  private SetOperationsChecker setOperations = new SetOperationsChecker();

  protected static final long MIN = Long.MIN_VALUE, MAX = Long.MAX_VALUE;

  public void setUp() throws Exception {
    super.setUp();
    set = createSet();
  }

  public void testAddRemoveSimple() {
    WritableLongList sourceAdd = LongArray.create(14, 7, 3,
        12, 6, 2,
        13, 8, 5,
        12, 20, 16,
        10, 22, 18);
    WritableLongList sourceRemove = LongArray.create(7, 3, 19,
        6, 14, -9,
        5, 8, 100,
        12, 2, 40,
        -1, 25, Long.MIN_VALUE);
    assert sourceAdd.size() == sourceRemove.size() : sourceAdd.size() + " " + sourceRemove.size();
    int size = sourceAdd.size();
    WritableLongList expected = new LongArray();

    int stateCount = 0;
    LongIterator iiAdd = sourceAdd.iterator();
    LongIterator iiRemove = sourceRemove.iterator();
    for (int i = 0; i < size; i++) {
      if (stateCount >= 0) {
        set.add(iiAdd.nextValue());
        expected.add(iiAdd.value());
      } else {
        long value = iiRemove.nextValue();
        set.remove(value);
        assertFalse(set.contains(value));
        expected.removeAll(iiRemove.value());
      }
      stateCount++;
      if (stateCount == 3) stateCount = -2;
    }
    assertEquals(expected.size(), set.size());
    expected.sortUnique();
    checkSet(set, expected);
  }

  public void testContains() {
    int arSize = 45, maxVal = Integer.MAX_VALUE, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      LongArray arr = generateRandomLongArray(arSize, SORTED_UNIQUE, maxVal);
      LongArray arr2 = LongCollections.collectLists(arr, map(LongFunctions.INC, arr), map(LongFunctions.DEC, arr));
      arr2.sortUnique();
      for (WritableLongSet curSet : createSetFromSortedUniqueList(arr)) {
        for (int i = 0; i < arr2.size(); i++) {
          long value = arr2.get(i);
          assertEquals(arr.binarySearch(value) >= 0, curSet.contains(value));
        }
      }
    }
  }

  public void testContains2() {
    int arSize = 45, maxVal = Integer.MAX_VALUE, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      LongArray arr = generateRandomLongArray(arSize, SORTED_UNIQUE, maxVal);
      LongArray arr2 = LongCollections.collectLists(arr, map(LongFunctions.INC, arr), map(LongFunctions.DEC, arr));
      arr2.sortUnique();
      for (WritableLongSet curSet : createSetFromSortedUniqueList(arr)) {
        for (int i = 0; i < arr2.size(); i++) {
          long value = arr2.get(i);
          boolean contains = arr.binarySearch(value) >= 0;
          assertEquals(contains, curSet.contains(value));
          assertEquals(contains, curSet.exclude(value));
          assertEquals(false, curSet.contains(value));
        }
      }
    }
  }

  public void testExcludeRandom() {
    int maxSize = 30, maxVal = 100, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      for (WritableLongSet set: createSetFromSortedUniqueList(generateRandomLongArray(maxSize, SORTED_UNIQUE, maxVal))) {
        for (int i = 0; i < maxVal; i++) {
          boolean res = set.contains(i);
          assertEquals(res, set.exclude(i));
          assertFalse(set.contains(i));
        }
      }
    }
  }

  public void testAdd() {
    int attempts = 12;
    int addCount = 100, maxVal = Integer.MAX_VALUE;
    LongArray expected = new LongArray(addCount);
    for (int attempt = 0; attempt < attempts; attempt++) {
      if (attempt % 3 == 0) set = createSet();
      set.clear();
      expected.clear();
      for (int i = 0, n = RAND.nextInt(addCount); i < n; i++) {
        int value = RAND.nextInt(maxVal);
        set.add(value);
        expected.add(value);
      }
      expected.sortUnique();
      checkSet(set, expected);
    }
  }

  public void testAddAll() {
    LongArray expected = LongArray.create(ap(1, 2, 10));
    set.addAll(expected);
    checkSet(set, expected);

    expected.addAll(ap(0, 2, 10));

    set.addAll(expected.toNativeArray());
    expected.sortUnique();
    checkSet(set, expected);

    set.removeAll(expected);
    assertTrue(set.isEmpty());

    expected = generateRandomLongArray(10, UNORDERED);
    set.addAll(expected.iterator());
    expected.sortUnique();
    checkSet(set, expected);
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
    assertFalse(set.contains(11));
    assertEquals(7, set.size());
    set.remove(256);
    assertFalse(set.contains(256));
    assertEquals(7, set.size());
    set.removeAll(1,2,5);
    assertEquals(4, set.size());
  }

  public void testSimple2() {
    for (long i: ap(0, 2, 10)) {
      set.add(i);
    }
    for (int i = 0; i < 20; i++) {
      assertEquals(i % 2 == 0, set.contains(i));
    }
    LongArray expected = new LongArray(ap(0, 2, 10));
    checkSet(set, expected);

    for (int i = 0; i < 20; i++) {
      if (i % 2 == 0 && i % 4 != 0) {
        assertTrue(set.exclude(i));
      }
      if (i % 2 == 1) {
        assertFalse(set.exclude(i));
      }
    }
    expected = new LongArray(ap(0, 4, 5));
    checkSet(set, expected);
    for (int i = 0; i < 20; i++) {
      assertTrue(i % 4 == 0 ? set.contains(i) : !set.contains(i));
    }

    set.clear();
    assertTrue(set.isEmpty());
    for (int i = 0; i < 20; i++) {
      assertFalse(set.contains(i));
    }
  }

  public void testSimple3() {
    int size = 20, maxVal = 1000, attemptsCount = 10;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      LongArray expected = generateRandomLongArray(size, SORTED_UNIQUE, maxVal);
      for (LongSet set : createSetFromSortedUniqueList(expected)) {
        checkSet(set, expected);
      }
    }
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
    assertFalse(set.contains(Long.MIN_VALUE));
    assertEquals(set.size(), 4);

    res.sortUnique();
    checkSet(set, res);
  }

  public void testFromSortedList() throws Exception {
    int testNumber = 10;
    int arraySize = 10000;
    for (int i = 0; i < testNumber; i++) {
      LongArray res = IntegersFixture.generateRandomLongArray(arraySize, SORTED_UNIQUE);
      LongArray res2 = new LongArray(res.size() * 3 + 1);
      for (int j = 0, n = res.size(); j < n; j++) {
        long val = res.get(j);
        res2.addAll(val - 1, val, val + 1);
      }
      res2.sortUnique();

      for (WritableLongSet set : createSetFromSortedUniqueList(res)) {
        for (int j = 0; j < res2.size(); j++) {
          long val = res2.get(j);
          assertEquals(res.binarySearch(val) >= 0, set.contains(val));
        }
      }
    }
  }

  public void testRandom() {
    // maxVal, setSize, listSize, nAttempts
    testRandom(100, 5, 20, 10);
    testRandom(100, 20, 20, 10);
    testRandom(700, 100, 100, 10);
    testRandom(Integer.MAX_VALUE, 510, 510, 10);
  }

  public void testRandom(int maxVal, int setSize, int listSize, int nAttempts) {
    LongArray toAdd = new LongArray(listSize);
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      if (attempt % 3 == 0) set = createSet();
      LongArray expected = generateRandomLongArray(setSize, UNORDERED, maxVal);
      set.clear();
      set.addAll(expected);
      expected.sortUnique();
      checkSet(set, expected);

      toAdd.clear();
      toAdd.addAll(generateRandomLongArray(listSize, SORTED_UNIQUE, maxVal));
      for (LongIterator it: set) {
        toAdd.removeAllSorted(it.value());
      }
      set.addAll(toAdd);
      LongArray expected2 = LongArray.copy(expected);
      expected2.merge(toAdd);
      checkSet(set, expected2);

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
          LongArray array = generateRandomLongArray(size, UNORDERED, maxVal);
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
    LongIteratorSpecificationChecker.checkIteratorThrowsCME(it);

    it = set.iterator();
    set.remove(5);
    assertFalse(set.contains(5));
    LongIteratorSpecificationChecker.checkIteratorThrowsCME(it);
  }

  public void testIsEmpty() {
    assertTrue(set.isEmpty());
    set.addAll(0, 7, 10);
    assertFalse(set.isEmpty());
  }

  public void testRetainSimple() {
    set.addAll(range(20));
    set.removeAll(range(0, 20, 2));
    checkSet(set, range(1, 20, 2));

    set = createSet();
    for (int i = 0; i < 2; i++) {
      set.addAll(range(2, 25, 2));
      set.retain(range(3, 25, 3));
      checkSet(set, range(6, 25, 6));
      set.clear();
    }
  }

  public void testRetainComplex() {
    final boolean isSorted = set instanceof LongSortedSet;
    final SortedStatus sortedStatus = isSorted ? SORTED_UNIQUE : UNORDERED;
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        set = create1SetFromSortedUniqueList(arrays[0]);
        set.retain(arrays[1]);
        return isSorted ? set.iterator() : toSorted(false, set).iterator();
      }
    }, new SetOperationsChecker.IntersectionGetter(isSorted), true, sortedStatus);
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
    for (T createdSet : createSetFromSortedUniqueList(expected)) {
      LongArray res = new LongArray();
      int count = expected.size();
      try {
        for (LongIterator i: createdSet) {
          res.add(i.value());
          count--;
          if (count == 0) {
            createdSet.add(99);
          }
        }
        fail();
      } catch (ConcurrentModificationException e) {}
      CHECK.order(expected, (createdSet instanceof LongSortedSet) ? res : toSorted(false, res));

      createdSet.clear();
      CHECK.order(LongIterator.EMPTY, createdSet.iterator());
      createdSet.add(10);
      CHECK.order(LongArray.create(10).iterator(), createdSet.iterator());
    }
  }

  public void testIteratorSpecification() {
    if (!(set instanceof LongSortedSet)) return;
    LongIteratorSpecificationChecker.checkIterator(new LongIteratorSpecificationChecker.IteratorGetter<LongIterator>() {
      @Override
      public List<LongIterator> get(long... values) {
        assert 0 == LongCollections.isSortedUnique(false, values, 0, values.length);

        List<LongIterator> res = new ArrayList<LongIterator>();

        set = createSet();
        set.addAll(values);
        res.add(set.iterator());
        if (values.length == 0) {
          return res;
        }

        for (WritableLongSet createdSet : createSetFromSortedUniqueList(new LongArray(values))) {
          res.add(createdSet.iterator());
        }

        LongArray toRemoveAll = new LongArray();
        if (Integer.MIN_VALUE < values[0]) {
          toRemoveAll.addAll(range(values[0] - 3, values[0]));
        }
        long lastVal = values[values.length - 1];
        if (lastVal < Integer.MAX_VALUE) {
          toRemoveAll.addAll(range(lastVal + 1, lastVal + 4));
        }
        long val = (values[0] + lastVal) / 2;
        while (set.contains(val)) {
          val++;
        }
        toRemoveAll.add(val);
        toRemoveAll.sortUnique();

        int i = 0;
        int maxCount = 10;
        for (int tryCoalesce = 0; tryCoalesce < 2; tryCoalesce++) {
          for (LongArray toAdd : LongCollections.allSubLists(new LongArray(values))) {
            for (LongArray toRemove : LongCollections.allSubLists(new LongArray(toRemoveAll))) {
              T createdSet = create1SetFromSortedUniqueList(new LongArray(values));
              createdSet.addAll(toAdd);
              if (tryCoalesce == 1 && createdSet instanceof LongAmortizedSet) {
                ((LongAmortizedSet) createdSet).coalesce();
              }
              createdSet.removeAll(toRemove);
              res.add(createdSet.iterator());
            }
            if (i++ >= maxCount) {
              break;
            }
          }
        }

        return res;
      }
    }, LongIteratorSpecificationChecker.ValuesType.SORTED_UNIQUE);
  }

  public void testTailIterator() {
    if (!(set instanceof WritableLongSortedSet)) return;
    WritableLongSortedSet sortedSet = (WritableLongSortedSet)set;
    sortedSet.addAll(ap(1, 2, 50));
    for (int i = 0; i < 99; i++) {
      assertEquals(i + 1 - (i % 2), sortedSet.tailIterator(i).nextValue());
    }

    sortedSet.clear();
    CHECK.order(LongIterator.EMPTY, sortedSet.tailIterator(0));
    sortedSet.add(10);
    CHECK.order(new LongIterator.Single(10), sortedSet.tailIterator(9));
    CHECK.order(new LongIterator.Single(10), sortedSet.tailIterator(10));
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

  public void testRemoveRandom() {
    int attempts = 3, maxVal = Integer.MAX_VALUE;
    boolean deepCheck = false;
    int[] sizes = {10, 100, 1000, 10000};
    for (int size : sizes) {
      for (int attempt = 0; attempt < attempts; attempt++) {
        LongArray toAdd = generateRandomLongArray(size, SORTED_UNIQUE, maxVal);
        toAdd.shuffle(RAND);
        set = createSet();
        for (int i = 0; i < toAdd.size(); i++) {
          long value = toAdd.get(i);
          assertTrue("i = " + i + "; toAdd.get(i) = " + value, set.include(value));
          assertTrue(set.contains(value));
        }
        toAdd.sortUnique();
        checkSet(set, toAdd);
        for (int i = 0; i < toAdd.size(); i++) {
          long value = toAdd.get(i);
          assertTrue("i = " + i + "; toAdd.get(i) = " + value, set.exclude(value));
          assertFalse(set.contains(value));
          if (deepCheck) {
            for (int j = i + 1; j < toAdd.size(); j++) {
              assertTrue("set not contains elements " + toAdd.get(j) + "; index = " + j + "; was removed ", set.contains(toAdd.get(j)));
            }
          }
        }
        assertTrue(set.isEmpty());
      }
    }
  }

  protected void checkBounds(LongArray array) {
    long upper = array.size() == 0 ? MIN : array.getLast(0);
    long lower = array.size() == 0 ? MAX : array.get(0);
    for (WritableLongSet set : createSetFromSortedUniqueList(array)) {
      WritableLongSortedSet sortedSet = (WritableLongSortedSet) set;
      assertEquals(sortedSet.toString(), upper, sortedSet.getUpperBound());
      assertEquals(sortedSet.toString(), lower, sortedSet.getLowerBound());
    }
  }

  public void testGetBounds() {
    if (!(set instanceof WritableLongSortedSet)) return;
    LongArray values = LongArray.create(MIN, MIN + 1, 0, 1, MAX - 1, MAX);
    for (LongArray array : LongCollections.allSubLists(values)) {
      checkBounds(array);
    }
  }

  public void testToNativeArray() {
    for (T set : createSetFromSortedUniqueList(LongProgression.range(20, 40))) {
      long[] ar = new long[40];
      set.toNativeArray(ar, 3);
      LongArray actual = new LongArray(new LongArray(ar).subList(3, 23));
      LongList expected = LongProgression.range(20, 40);
      CHECK.unordered(actual, expected);

      set.removeAll(LongIterators.range(20, 40, 2));
      set.toNativeArray(ar, 4);
      expected = LongProgression.range(21, 40, 2);
      actual = new LongArray(new LongArray(ar).subList(4, 4 + expected.size()));
      CHECK.unordered(actual, expected);
    }
  }

  public void testInclude() {
    for (long i: ap(0, 2, 100)) {
      assertTrue(set.include(i));
    }
    for (long i: ap(0, 1, 200)) {
      assertEquals(i % 2 == 1, set.include(i));
    }
  }

  public void testIterators2() {
    if (!(set instanceof LongSortedSet)) return;
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

  public void testTailIteratorRandom() {
    if (!(set instanceof LongSortedSet)) return;
    final int size = 200,
        testCount = 5;
    LongArray expected;
    LongArray testValues;
    for (int i = 0; i < testCount; i++) {
      expected = new LongArray(LongIterators.limit(randomIterator(), size));
      expected.sortUnique();
      testValues = LongCollections.collectLists(
          expected, map(LongFunctions.INC, expected), map(LongFunctions.DEC, expected));
      testValues.sortUnique();
      for (T set0 : createSetFromSortedUniqueList(expected)) {
        LongSortedSet sortedSet = (LongSortedSet) set0;
        for (int j = 0; j < testValues.size(); j++) {
          long key = testValues.get(j);
          int ind = expected. binarySearch(key);
          CHECK.order(expected.iterator(ind >= 0 ? ind : -ind - 1), sortedSet.tailIterator(key));
        }
      }
    }
  }

}
