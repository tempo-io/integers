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

import com.almworks.integers.func.IntProcedure;
import com.almworks.integers.optimized.SameValuesLongList;
import com.almworks.integers.util.LongSetBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class DynamicLongSetTests extends WritableLongSetChecker {
  private static final long MIN = Long.MIN_VALUE;
  private static final long MAX = Long.MAX_VALUE;

  protected WritableLongSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected WritableLongSet createSetWithCapacity(int capacity) {
    DynamicLongSet newSet;
    newSet = capacity == -1 ? new DynamicLongSet() : new DynamicLongSet(capacity);
    try {
      setFinalStatic(newSet, "SHRINK_FACTOR", 6);
      setFinalStatic(newSet, "SHRINK_MIN_LENGTH", 4);

      Field field = newSet.getClass().getDeclaredField("SHRINK_FACTOR");
      field.setAccessible(true);
      return newSet;
    } catch (Exception ex) {
      return new DynamicLongSet();
    }
  }

  protected WritableLongSet[] createSetFromSortedList(LongList sortedList) {
    return new WritableLongSet[]{
        DynamicLongSet.fromSortedList(sortedList),
        DynamicLongSet.fromSortedList(sortedList, DynamicLongSet.ColoringType.TO_ADD),
        DynamicLongSet.fromSortedList(sortedList, DynamicLongSet.ColoringType.TO_REMOVE)
    };
  }

  public void testRandom() {
    int[] ns = new int[]{510, 513, 1025, 2049, 4097}; // testing sizes near 2^n
    int nAttempts = 5;
    LongSetBuilder anotherSet = new LongSetBuilder();
    DynamicLongSet dynamicSet = new DynamicLongSet(510);
    WritableLongList toAdd = new LongArray();
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      for (int i = 0; i < ns[attempt]; ++i) toAdd.add(RAND.nextLong());
      dynamicSet.addAll(toAdd);
      dynamicSet.compactify();
      anotherSet.addAll(toAdd);
      LongList anotherSetList = anotherSet.toTemporaryReadOnlySortedCollection();
      LongList setList = dynamicSet.toLongArray();
//      System.err.println("attempt #" + attempt);
      CHECK.order(anotherSetList, setList);

      // testRemove runs long, so it's ran only twice
      if (attempt > 1) continue;
      testRemove(toAdd, toAdd);
      testRemove(toAdd, anotherSetList);
      testRemove(anotherSetList, toAdd);
      testRemove(anotherSetList, anotherSetList);
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

  public void PrintPerm() {
    final char[] s = "1234567".toCharArray();
    final HashSet<String> ss = new HashSet<String>();
    IntegersUtils.allPermutations(s.length, new IntProcedure() {
      int count;

      {
        doPrint();
      }

      private void doPrint() {
        String str = new String(s);
        assertTrue(ss.add(str));
        System.out.println(count + "\t" + str);
      }

      @Override
      public void invoke(int i) {
        count += 1;
        char t = s[i];
        s[i] = s[i + 1];
        s[i + 1] = t;
        doPrint();
      }
    });
    System.out.println("Distinct: " + ss.size());
  }

  public void testEdgeCasesWithCompactify() {
    DynamicLongSet set = new DynamicLongSet();
    assertFalse(set.exclude(MIN));
    assertFalse(set.exclude(0));
    set.removeAll(12, 23, 12, 51);
    assertTrue(set.isEmpty());
    set.compactify();
    assertTrue(set.isEmpty());
    LongList ll = new LongArray();
    DynamicLongSet set2 = DynamicLongSet.fromSortedList(ll);
    assertTrue(set2.isEmpty());
    set.addAll(1, 3, 2, MIN, Long.MAX_VALUE);
    assertTrue(new LongArray(set.toList()).checkSorted(true));
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
    DynamicLongSet set = new DynamicLongSet();
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
