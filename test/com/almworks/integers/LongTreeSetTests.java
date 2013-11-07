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

import com.almworks.integers.util.LongSetBuilder;

import java.lang.reflect.Field;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public class LongTreeSetTests extends WritableLongSetChecker {

  private static final long MIN = Long.MIN_VALUE;
  private static final long MAX = Long.MAX_VALUE;

  protected boolean isSupportTailIterator() {
    return true;
  }

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
        LongTreeSet.createFromSortedUnique(sortedList, sortedList.size(), LongTreeSet.ColoringType.TO_ADD),
        LongTreeSet.createFromSortedUnique(sortedList, sortedList.size(), LongTreeSet.ColoringType.TO_REMOVE)
    };
  }

  public void testRandom2() {
    int setSize = 510, listSize = 510;
    int nAttempts = 10;

    LongArray toAdd = new LongArray(listSize);
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      LongTreeSet dynamicSet = new LongTreeSet(setSize);
      LongArray expected = generateRandomLongArray(setSize, false);
      dynamicSet.addAll(expected);
      expected.sortUnique();

      toAdd.clear();
      toAdd.addAll(generateRandomLongArray(listSize, true));
      for (LongIterator it: dynamicSet) {
        toAdd.removeAllSorted(it.value());
      }

      dynamicSet.addAll(toAdd);
      dynamicSet.removeAll(toAdd);
      dynamicSet.compactify();
      CHECK.order(expected, dynamicSet.toArray());
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

  public void testGetBounds() {
    LongTreeSet set = new LongTreeSet();
    assertEquals(MAX, set.getLowerBound());
    assertEquals(MIN, set.getUpperBound());
    set.addAll(0, 2);
    assertEquals(0, set.getLowerBound());
    assertEquals(2, set.getUpperBound());
    set.removeAll(0, 2);
    assertEquals(MAX, set.getLowerBound());
    assertEquals(MIN, set.getUpperBound());
    set.add(MIN);
    assertEquals(MIN, set.getLowerBound());
    assertEquals(MIN, set.getUpperBound());
  }
}
