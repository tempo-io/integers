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
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.LongCollections.*;

public class LongAmortizedSetTests extends WritableLongSetChecker<LongAmortizedSet> {

  protected LongAmortizedSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected LongAmortizedSet createSetWithCapacity(int capacity) {
    return new LongAmortizedSet();
  }

  protected List<LongAmortizedSet> createSets(LongList sortedUniqueList) {
    ArrayList<LongAmortizedSet> sets = new ArrayList();
    sets.add(LongAmortizedSet.createFromSortedUnique(sortedUniqueList));

    int size = sortedUniqueList.size();
    if (size != 0 && size < 300) {
      int numberOfSets = 100;
      LongArray removedVariants = LongArray.create(
          sortedUniqueList.get(0) - 1, sortedUniqueList.get(0) + 1, MIN, 0, MAX, sortedUniqueList.get(size - 1));
      removedVariants.removeAll(sortedUniqueList);
      removedVariants.sortUnique();

      for (int attempt = 0; attempt < numberOfSets; attempt++) {
        set = new LongAmortizedSet();
        LongArray addMask = generateRandomLongArray(size, UNORDERED, 2);
        LongArray removeMask = generateRandomLongArray(removedVariants.size(), UNORDERED, 2);
        for (int i = 0; i < addMask.size(); i++) {
          if (addMask.get(i) == 0) set.add(sortedUniqueList.get(i));
        }
        for (int i = 0; i < removeMask.size(); i++) {
          if (removeMask.get(i) == 1) set.add(removedVariants.get(i));
        }
        set.coalesce();

        for (int i = 0; i < addMask.size(); i++) {
          if (addMask.get(i) == 1) set.add(sortedUniqueList.get(i));
        }
        for (int i = 0; i < removeMask.size(); i++) {
          if (removeMask.get(i) == 1) set.remove(removedVariants.get(i));
        }
        sets.add(set);
      }
    }

    set = new LongAmortizedSet(new LongTreeSet(), new LongTreeSet());
    set.addAll(sortedUniqueList);
    sets.add(set);

    return sets;
  }

  @Override
  protected LongAmortizedSet createSet(LongList sortedUniqueList) {
    return LongAmortizedSet.createFromSortedUniqueArray(new LongArray(sortedUniqueList));
  }

  @Override
  protected boolean isSortedSet() {
    return true;
  }

  public void testIteratorCoalesce() {
    set = new LongAmortizedSet();
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

    set = new LongAmortizedSet();
    set.addAll(1, 2, 3);
    set.coalesce();
    set.addAll(4, 5);
    set.removeAll(2, 4, 5);
    CHECK.order(set.tailIterator(0), 1L, 3);
  }

  public void testIsEmpty2() {
    // baseList, myAdded, myRemoved
    set = new LongAmortizedSet(20);

    // 0, 0, 0
    assertTrue(set.isEmpty());

    set.addAll(0,5,10);
    // 0, AB, 0
    assertFalse(set.isEmpty());

    set.coalesce();
    // AB, 0, 0
    assertFalse(set.isEmpty());

    set.addAll(5, 10, 15);
    // AB, AC, 0
    assertFalse(set.isEmpty());

    set.removeAll(20, 25, 30, 35, 40, 45, 50, 55);
    // AB, AC, D
    assertFalse(set.isEmpty());

    set.removeAll(0, 5, 10);
    // AB, C, ABD
    assertFalse(set.isEmpty());

    set.remove(15);
    // AB, 0, ABD
    assertTrue(set.isEmpty());

    set.coalesce();
    // 0, 0, D
    assertTrue(set.isEmpty());

    set = new LongAmortizedSet(20);
    set.addAll(1, 2, 3);
    set.coalesce();
    set.addAll(1, 2, 3);
    // X, X, 0
    assertFalse(set.isEmpty());

    set = new LongAmortizedSet(20);
    set.addAll(1, 2, 3);
    set.removeAll(4, 5, 6);
    // 0, X, Y
    assertFalse(set.isEmpty());

    set.coalesce();
    set.removeAll(4, 5, 6);
    // X, 0, Y
    assertFalse(set.isEmpty());
  }

  public void testAddRemove2() {
    LongArray array = new LongArray(LongProgression.range(0, 7));
    for (LongArray a : allSubLists(array)) {
      for (LongArray b : allSubLists(array)) {
        for (int i = 0; i < 2; i++) {
          set = new LongAmortizedSet();

          LongArray expected = LongArray.copy(a);
          expected.removeAll(b);

          set.addAll(a);
          if (i % 2 == 0) {
            set.coalesce();
          }
          set.removeAll(b);

          assertEquals(expected.isEmpty(), set.isEmpty());
          assertEquals(expected.size(), set.size());
        }
      }
    }
  }

  public void testConstructors() {
    int attemptsCount = 10;
    // more than 512
    int size = 600;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      // LongAmortizedSet.createFromSortedUnique
      LongArray res = generateRandomLongArray(size, SORTED_UNIQUE);
      set = LongAmortizedSet.createFromSortedUnique(res);
      checkSet(set, res);
      set.coalesce();
      checkSet(set, res);

      // LongAmortizedSet.createFromSortedUniqueArray
      res = generateRandomLongArray(size, SORTED_UNIQUE);
      LongArray expected = LongArray.copy(res);
      set = LongAmortizedSet.createFromSortedUniqueArray(res);
      checkSet(set, expected);
      set.coalesce();
      checkSet(set, expected);

      // LongAmortizedSet(WritableLongSortedSet, WritableLongSet, int)
      set = new LongAmortizedSet(0, new LongTreeSet(), new LongTreeSet(), 128);
      set.addAll(expected);
      checkSet(set, expected);
      set.coalesce();
      checkSet(set, expected);
    }
  }

  public void testContains4() {
    int arSize = 45, maxVal = Integer.MAX_VALUE, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      LongArray arr = generateRandomLongArray(arSize, SORTED_UNIQUE, maxVal);
      LongArray arr2 = collectLists(arr, map(LongFunctions.INC, arr), map(LongFunctions.DEC, arr));
      arr2.sortUnique();
      for (LongAmortizedSet curSet : createSets(arr)) {
        for (int i = 0; i < arr2.size(); i++) {
          long value = arr2.get(i);
          assertEquals(arr.binarySearch(value) >= 0, curSet.contains(value));
        }

        for (int i = 0; i < arr.size(); i += 2) {
          long value = arr.get(i);
          curSet.remove(value);
          assertFalse(curSet.contains(value));
        }
        curSet.coalesce();
        for (int i = 0; i < arr.size(); i++) {
          assertEquals(i % 2 == 1, curSet.contains(arr.get(i)));
        }
      }
    }
  }

  public void testAddAll2() {
    int attempts = 20;
    int addCount = 100, maxVal = Integer.MAX_VALUE;
    set = new LongAmortizedSet();
    for (int attempt = 0; attempt < attempts; attempt++) {
      if (attempt % 3 == 0) set = new LongAmortizedSet();
      set.clear();
      LongArray expected = generateRandomLongArray(addCount, UNORDERED, maxVal);
      int size2 = expected.size() / 2;
      set.addAll(expected.subList(0, size2));
      if (attempt % 2 == 0) {
        set.coalesce();
      }
      set.addAll(expected.subList(size2, expected.size()));

      expected.sortUnique();
      checkSet(set, expected);
    }
  }

  public void testRandom(int maxVal, int setSize, int listSize, int nAttempts) {
    set = new LongAmortizedSet();
    LongArray toAdd = new LongArray(listSize);
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      if (attempt % 3 == 0) set = new LongAmortizedSet();
      LongArray expected = generateRandomLongArray(setSize, UNORDERED, maxVal);
      set.clear();
      set.addAll(expected);
      expected.sortUnique();
      checkSet(set, expected);

      toAdd.clear();
      toAdd.addAll(generateRandomLongArray(listSize, SORTED_UNIQUE, maxVal));
      for (LongIterator it : set) {
        toAdd.removeAllSorted(it.value());
      }
      set.addAll(toAdd);
      LongArray expected2 = LongArray.copy(expected);
      expected2.merge(toAdd);
      checkSet(set, expected2);

      set.removeAll(toAdd);
      set.coalesce();
      LongArray notExpected = collectLists(map(LongFunctions.INC, expected), map(LongFunctions.DEC, expected));
      notExpected.removeAll(expected);
      set.removeAll(notExpected);
      checkSet(set, expected);
    }
  }
}
