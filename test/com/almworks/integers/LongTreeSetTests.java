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

import static com.almworks.integers.LongTreeSet.ColoringType.BALANCED;
import static com.almworks.integers.LongTreeSet.ColoringType.TO_ADD;
import static com.almworks.integers.LongTreeSet.ColoringType.TO_REMOVE;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public class LongTreeSetTests extends WritableLongSetChecker {

  protected WritableLongSortedSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected WritableLongSortedSet createSetWithCapacity(int capacity) {
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

  protected WritableLongSortedSet[] createSetFromSortedList(LongList sortedList) {
    return new WritableLongSortedSet[]{
        LongTreeSet.createFromSortedUnique(sortedList),
        LongTreeSet.createFromSortedUnique(sortedList, sortedList.size(), TO_ADD),
        LongTreeSet.createFromSortedUnique(sortedList, sortedList.size(), TO_REMOVE)
    };
  }

  public void testRandom2() {
    int setSize = 510, listSize = 510;
    int nAttempts = 10;

    LongArray toAdd = new LongArray(listSize);
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      LongTreeSet treeSet = new LongTreeSet(setSize);
      LongArray expected = generateRandomLongArray( setSize, IntegersFixture.SortedStatus.UNORDERED);
      treeSet.addAll(expected);
      expected.sortUnique();

      toAdd.clear();
      toAdd.addAll(generateRandomLongArray( listSize, IntegersFixture.SortedStatus.SORTED_UNIQUE));
      for (LongIterator it: treeSet) {
        toAdd.removeAllSorted(it.value());
      }

      treeSet.addAll(toAdd);
      treeSet.removeAll(toAdd);
      treeSet.compactify();
      CHECK.order(expected, treeSet.toArray());
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

  public void testEdgeCasesWithCompactify() {
    LongTreeSet set = new LongTreeSet();
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

    LongTreeSet set;

    for (int attempt = 0; attempt < nAttempts; attempt++) {
      LongArray sortedUniqueArray = generateRandomLongArray( listSize, IntegersFixture.SortedStatus.SORTED_UNIQUE);
      if (attempt % 4 == 0) {
        set = LongTreeSet.createFromSortedUnique(sortedUniqueArray);
      } else {
        set = LongTreeSet.createFromSortedUnique(
            sortedUniqueArray.iterator(), RAND.nextInt(listSize * 2), types[attempt % 4  - 1]);
      }
      checkSet(set, sortedUniqueArray);
    }
  }

  public void _test() {
    for (int len = 1; len < 66; len++) {
      System.out.println("data: " + LongCollections.toBoundedString(LongIterators.range(len - 1)));
      LongTreeSet set = LongTreeSet.createFromSortedUnique(LongIterators.range(len - 1), len, TO_REMOVE);
      System.out.println("set : " + LongCollections.toBoundedString(set));
      System.out.println(set.toDebugString());
      System.out.println("---------------------------------");
      if (len == 30) len *= 2;
    }
  }
}
