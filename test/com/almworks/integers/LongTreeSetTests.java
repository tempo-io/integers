/*
 * Copyright 2011 ALM Works Ltd
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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.almworks.integers.LongProgression.range;
import static com.almworks.integers.LongTreeSet.ColoringType.BALANCED;
import static com.almworks.integers.LongTreeSet.ColoringType.TO_ADD;
import static com.almworks.integers.LongTreeSet.ColoringType.TO_REMOVE;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public class LongTreeSetTests extends WritableLongSetChecker<LongTreeSet> {

  protected LongTreeSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected LongTreeSet createSetWithCapacity(int capacity) {
    LongTreeSet newSet;
    newSet = capacity == -1 ? new LongTreeSet() : new LongTreeSet(capacity);
    try {
      setFinalStatic(newSet, "SHRINK_FACTOR", 6);
      setFinalStatic(newSet, "SHRINK_MIN_LENGTH", 4);

      Field field = newSet.getClass().getDeclaredField("SHRINK_FACTOR");
      field.setAccessible(true);
      return newSet;
    } catch (Exception ex) {
      return new LongTreeSet();
    }
  }

  protected List<LongTreeSet> createSetFromSortedUniqueList(LongList sortedUniqueList) {
    return Arrays.asList(
        LongTreeSet.createFromSortedUnique(sortedUniqueList),
        LongTreeSet.createFromSortedUnique(sortedUniqueList, sortedUniqueList.size(), TO_ADD),
        LongTreeSet.createFromSortedUnique(sortedUniqueList, sortedUniqueList.size(), TO_REMOVE));
  }

  @Override
  protected LongTreeSet create1SetFromSortedUniqueList(LongList sortedUniqueList) {
    return LongTreeSet.createFromSortedUnique(sortedUniqueList);
  }

  public void testRandom2() {
    int setSize = 510, listSize = 510;
    int nAttempts = 10;

    LongArray toAdd = new LongArray(listSize);
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      LongTreeSet treeSet = new LongTreeSet(setSize);
      LongArray expected = generateRandomLongArray(setSize, IntegersFixture.SortedStatus.UNORDERED);
      treeSet.addAll(expected);
      expected.sortUnique();

      toAdd.clear();
      toAdd.addAll(generateRandomLongArray(listSize, IntegersFixture.SortedStatus.SORTED_UNIQUE));
      for (LongIterator it: treeSet) {
        toAdd.removeAllSorted(it.value());
      }

      treeSet.addAll(toAdd);
      treeSet.removeAll(toAdd);
      treeSet.compactify();
      CHECK.order(expected, treeSet.toArray());
    }
  }

  public void testEdgeCasesWithCompactify() {
    set = new LongTreeSet();
    assertFalse(set.exclude(MIN));
    assertFalse(set.exclude(0));
    set.removeAll(12, 23, 12, 51);
    assertTrue(set.isEmpty());
    set.compactify();
    assertTrue(set.isEmpty());
    LongList ll = new LongArray();
    LongTreeSet set2 = LongTreeSet.createFromSortedUnique(ll);
    assertTrue(set2.isEmpty());
    set.addAll(1, 3, 2, MIN, Long.MAX_VALUE);
    assertTrue(new LongArray(set.toArray()).isSorted(true));
    assertTrue(set.contains(1));
    assertTrue(set.contains(MIN));
    assertFalse(set.contains(0));

    set.clear();
    set.add(MIN);
    assertTrue(set.contains(MIN));
    assertFalse(set.contains(MIN + 1));
    assertFalse(set.contains(0));
  }

  public void testStaticConstructorsSimple() {
    for (LongArray array : LongCollections.allSubLists(LongArray.create(Long.MIN_VALUE, Long.MIN_VALUE + 1, -1, 0, 1, Long.MAX_VALUE))) {
      checkSet(LongTreeSet.createFromSortedUnique(array), array);
    }
  }

  public void testStaticConstructorsRandom() {
    int listSize = 510, nAttempts = 12;
    LongTreeSet.ColoringType[] types = {TO_ADD, TO_REMOVE, BALANCED};

    for (int attempt = 0; attempt < nAttempts; attempt++) {
      LongArray sortedUniqueArray = generateRandomLongArray(listSize, IntegersFixture.SortedStatus.SORTED_UNIQUE);
      if (attempt % 4 == 0) {
        set = LongTreeSet.createFromSortedUnique(sortedUniqueArray);
      } else {
        set = LongTreeSet.createFromSortedUnique(
            sortedUniqueArray.iterator(), RAND.nextInt(listSize * 2), types[attempt % 4  - 1]);
      }
      checkSet(set, sortedUniqueArray);
    }
  }

  public void testRetainSimple2() {
    set = new LongTreeSet();
    set.addAll(range(20));
    set.removeAll(range(0, 20, 2));
    checkSet(set, range(1, 20, 2));

    set = new LongTreeSet();
    for (int i = 0; i < 2; i++) {
      set.addAll(range(2, 25, 2));
      set.retain(LongTreeSet.createFromSortedUnique(range(3, 25, 3)));
      checkSet(set, range(6, 25, 6));
      set.clear();
    }
  }
}
