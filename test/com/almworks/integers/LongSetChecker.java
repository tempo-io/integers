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

import com.almworks.integers.func.LongFunctions;

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.LongCollections.concatLists;
import static com.almworks.integers.LongCollections.map;
import static com.almworks.integers.LongIteratorSpecificationChecker.ValuesType;
import static com.almworks.integers.LongListSet.setFromSortedList;
import static com.almworks.integers.LongProgression.arithmetic;
import static com.almworks.integers.LongProgression.range;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public abstract class LongSetChecker<T extends LongSet> extends IntegersFixture {
  protected abstract List<T> createSets(LongList sortedUniqueList);

  protected abstract T createSet(LongList sortedUniqueList);

  protected abstract boolean isSortedSet();

  protected T set;

  private SetOperationsChecker setOperations = new SetOperationsChecker();

  protected static final long MIN = Long.MIN_VALUE, MAX = Long.MAX_VALUE;

  public void setUp() throws Exception {
    super.setUp();
  }

  public void testContains() {
    int arSize = 45, maxVal = Integer.MAX_VALUE, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      LongArray arr = generateRandomLongArray(arSize, SORTED_UNIQUE, maxVal);
      LongArray arr2 = LongCollections.collectLists(arr, map(LongFunctions.INC, arr), map(LongFunctions.DEC, arr));
      arr2.sortUnique();
      for (LongSet curSet : createSets(arr)) {
        for (int i = 0; i < arr2.size(); i++) {
          long value = arr2.get(i);
          assertEquals(arr.binarySearch(value) >= 0, curSet.contains(value));
        }
        assertTrue(curSet.containsAll(arr));
        assertTrue(curSet.containsAll(curSet));
        assertTrue(curSet.containsAll(curSet.iterator()));
        assertTrue(curSet.containsAll(LongList.EMPTY));
        assertFalse(curSet.containsAll(concatLists(arr, new LongList.Single(arr.getLast(0) + 1))));
      }
    }
  }

  public void testSimple0() {
    int size = 20, maxVal = 1000, attemptsCount = 10;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      LongArray expected = generateRandomLongArray(size, SORTED_UNIQUE, maxVal);
      for (LongSet set : createSets(expected)) {
        checkSet(set, expected);
      }
    }
  }

  public void testFromSortedList() throws Exception {
    int testNumber = 10;
    int arraySize = 10000;
    for (int i = 0; i < testNumber; i++) {
      LongArray res = generateRandomLongArray(arraySize, SORTED_UNIQUE);
      LongArray res2 = new LongArray(res.size() * 3 + 1);
      for (int j = 0, n = res.size(); j < n; j++) {
        long val = res.get(j);
        res2.addAll(val - 1, val, val + 1);
      }
      res2.sortUnique();

      for (LongSet  set : createSets(res)) {
        for (int j = 0; j < res2.size(); j++) {
          long val = res2.get(j);
          assertEquals(res.binarySearch(val) >= 0, set.contains(val));
        }
      }
    }
  }

  public void testIteratorHasMethods() {
    for (T set : createSets(LongProgression.range(1, 8))) {
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
  }

  protected void checkBounds(LongArray array) {
    long upper = array.size() == 0 ? MIN : array.getLast(0);
    long lower = array.size() == 0 ? MAX : array.get(0);
    for (LongSet  set : createSets(array)) {
      LongSortedSet sortedSet = (LongSortedSet) set;
      assertEquals(sortedSet.toString(), upper, sortedSet.getUpperBound());
      assertEquals(sortedSet.toString(), lower, sortedSet.getLowerBound());
    }
  }

  public void testGetBounds() {
    if (!isSortedSet()) return;
    LongArray values = LongArray.create(MIN, MIN + 1, 0, 1, MAX - 1, MAX);
    for (LongArray array : LongCollections.allSubLists(values)) {
      checkBounds(array);
    }
  }

  public void testToNativeArray() {
    for (T curSet : createSets(LongProgression.range(20, 40))) {
      long[] ar = new long[40];
      curSet.toNativeArray(ar, 3);
      LongArray actual = new LongArray(new LongArray(ar).subList(3, 23));
      LongList expected = LongProgression.range(20, 40);
      CHECK.unordered(actual, expected);

      try {
        curSet.toNativeArray(new long[20], 1);
        fail();
      } catch (IndexOutOfBoundsException _) {
        // ok
      }

      try {
        curSet.toNativeArray(new long[20], -1);
        fail();
      } catch (IndexOutOfBoundsException _) {
        // ok
      }

    }

    for (T curSet : createSets(LongProgression.range(21, 40, 2))) {
      int setSize = curSet.size();
      long[] ar = new long[40];
      curSet.toNativeArray(ar, 4);
      LongList expected = LongProgression.range(21, 40, 2);
      LongArray actual = new LongArray(new LongArray(ar).subList(4, 4 + expected.size()));
      CHECK.unordered(actual, expected);

      try {
        curSet.toNativeArray(new long[setSize], 1);
        fail();
      } catch (IndexOutOfBoundsException _) {
        // ok
      }

      try {
        curSet.toNativeArray(new long[20], -1);
        fail();
      } catch (IndexOutOfBoundsException _) {
        // ok
      }
    }
  }

  public void testIterators2() {
    if (!isSortedSet()) return;
    for (T set : createSets(range(10))) {
      LongIterator it1 = set.iterator();
      for (int i = 0; i < 5; i++) {
        assertEquals(i, it1.nextValue());
      }
      // call coalesce
      CHECK.order(new LongArray(ap(0, 10, 1)), set.toArray());
      CHECK.order(new LongArray(ap(0, 10, 1)).iterator(), set.iterator());
      CHECK.order(new LongArray(ap(5, 5, 1)).iterator(), it1);
    }
  }

  public void testTailIteratorSimple() {
    if (!isSortedSet()) return;
    for (LongSet set : createSets(arithmetic(1, 50, 2))) {
      LongSortedSet sortedSet = (LongSortedSet) set;
      for (int i = 0; i < 99; i++) {
        assertEquals(i + 1 - (i % 2), sortedSet.tailIterator(i).nextValue());
      }
    }
  }

  public void testTailIteratorRandom() {
    if (!isSortedSet()) return;
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
      for (T set0 : createSets(expected)) {
        LongSortedSet sortedSet = (LongSortedSet) set0;
        for (int j = 0; j < testValues.size(); j++) {
          long key = testValues.get(j);
          int ind = expected. binarySearch(key);
          CHECK.order(expected.iterator(ind >= 0 ? ind : -ind - 1), sortedSet.tailIterator(key));
        }
      }
    }
  }

  public void testHashCode() {
    int attemptsCount = 10, shuffleCount = 10;
    int sizeMax = 600, step = 50;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      for (int size = step; size < sizeMax; size += step) {
        LongArray array = generateRandomLongArray(size, SORTED_UNIQUE);
        int expectedHash = 0;
        for (LongIterator it : array) {
          expectedHash += IntegersUtils.hash(it.value());
        }

        for (T set0 : createSets(array)) {
          assertEquals(expectedHash, set0.hashCode());
        }

        if (isSortedSet()) return;

        IntArray indices = new IntArray(IntProgression.range(size));
        for (int i = 0; i < shuffleCount; i++) {
          for (T set : createSets(array.get(indices))) {
            assertEquals(expectedHash, set.hashCode());
            indices.shuffle(myRand);
          }
        }
      }
    }
  }

  public void testEquals() {
    int size = 100;
    int attemptsCount = 10;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      LongArray array = generateRandomLongArray(size, SORTED_UNIQUE);

      // suppose array == [0, 1, 2, ... , size-1]
      if (array.equalSortedUniqueValues(range(size))) continue;

      long lastElem = array.getLast(0);
      LongList list = LongCollections.concatLists(array.subList(0, array.size() - 1), new LongList.Single(lastElem + 1));
      for (T set0 : createSets(array)) {
        assertTrue(set0.equals(set0));
        assertFalse(set0.equals(null));
        assertFalse(set0.equals(LongSet.EMPTY));
        assertFalse(set0.equals(setFromSortedList(list)));
        for (T set1 : createSets(array)) {
          assertTrue(set0.equals(set1));
          assertTrue(set1.equals(set0));
        }
      }
    }
  }

  public void testIteratorSpecification() {
    if (!isSortedSet()) return;
    LongIteratorSpecificationChecker.checkIterator(myRand, new LongIteratorSpecificationChecker.IteratorGetter<LongIterator>() {
      @Override
      public List<LongIterator> get(long... values) {
        ArrayList<LongIterator> iterators = new ArrayList<LongIterator>();
        LongArray valuesArray = new LongArray(values);
        for (T curSet : createSets(valuesArray)) {
          iterators.add(curSet.iterator());
        }
        if (values.length != 0) {
          if (values[0] != Long.MIN_VALUE) {
            for (T curSet : createSets(concatLists(new LongList.Single(Long.MIN_VALUE), valuesArray))) {
              LongSortedSet sortedSet = (LongSortedSet) curSet;
              iterators.add(sortedSet.tailIterator(Long.MIN_VALUE + 1));
              iterators.add(sortedSet.tailIterator(values[0]));
              iterators.add(sortedSet.tailIterator(values[0] - 1));
            }
          }
        }
        return iterators;
      }
    }, ValuesType.SORTED_UNIQUE);
  }
}
