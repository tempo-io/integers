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
import com.almworks.integers.util.AmortizedSortedLongSet;
import com.almworks.integers.util.IntegersDebug;
import com.almworks.integers.util.LongSetBuilder;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class DynamicLongSetTests extends WritableLongSetChecker {
//  protected final DynamicLongSet set = new DynamicLongSet();

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
      IntegersDebug.println("===" + attempt + "====");
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

  public void testCompactify() {
    checkTreeFromSortedList(7);
    checkTreeFromSortedList(8);
    checkTreeFromSortedList(13);
    checkTreeFromSortedList(14);
    checkTreeFromSortedList(15);
    checkTreeFromSortedList(16);
    checkTreeFromSortedList(20);
  }

  private void checkTreeFromSortedList(int n) {
    System.out.println("==== " + n + " =====");
    DynamicLongSet set = DynamicLongSet.fromSortedList(LongProgression.arithmetic(1, n), DynamicLongSet.ColoringType.TO_REMOVE);
    CHECK.order(set.iterator(), new LongProgression.ArithmeticIterator(1, 1, n));
    set.debugPrintTreeStructure(System.out);
    System.out.println("");
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

  public void testAddRemove2() {
    WritableLongList sourceAdd = LongArray.create(14,7,3,    12,6,2,     13,8,5,    15,17,19,      12,20,16,        10, 22, 18/*,     -1,10,-2,20,-6,32*/);
    WritableLongList sourceRemove = LongArray.create(    7,3,       6,14,       5,8,         12, 2,        -1, 25);
    LongList expected = LongArray.create(10, 12, 13, 15, 16, 17, 18, 19, 20, 22);
//    LongList expected = LongArray.create(-6,-2,-1,4,10,12,13,20,32);
    DynamicLongSet set = new DynamicLongSet();

    LongIterator iiAdd = sourceAdd.iterator();
    LongIterator iiRemove = sourceRemove.iterator();
    for (int stateCount = 0; iiAdd.hasNext() || iiRemove.hasNext(); stateCount++) {
      if (stateCount == 3) stateCount = -2;
      if (stateCount >= 0) {
        IntegersDebug.println("add", iiAdd.nextValue());
        set.add(iiAdd.value());
      } else {
        IntegersDebug.println("rem", iiRemove.nextValue());
        set.remove(iiRemove.value());
      }

      set.debugPrintTreeStructure(System.out);
      System.out.println("");
    }
    assertEquals(expected, set.toLongArray());
  }

  public void testDlsVsJuc() {
    long overallDls = 0;
    long overallJuc = 0;
    DynamicLongSet dls = new DynamicLongSet();
    Set<Long> juc = new TreeSet<Long>();
    int burnIn = 2;
    for (int epoch = 0; epoch < 5; ++epoch) {
      LongArray toRemove = new LongArray();
      for (int i = 0; i < 1000000; ++i) {
        long e = RAND.nextLong();
        toRemove.add(RAND.nextLong());
        dls.add(e);
        juc.add(e);
      }

      long startDls = System.nanoTime();
      dls.removeAll(toRemove);
      if (epoch > burnIn) {
        overallDls += System.nanoTime() - startDls;
      }


      long startJuc = System.nanoTime();
      for (LongIterator li : toRemove) {
        juc.remove(li.nextValue());
      }
      if (epoch > burnIn) {
        overallJuc += System.nanoTime() - startJuc;
      }

      if (epoch % 5 == 0 && epoch > 0) {
        dls.clear();
        juc.clear();
      }
    }
    double nanoInMilli = 1000000.0;
    System.out.format("Overall DLS: %.3fms\nOverall J.U.C.: %.3fms", overallDls / nanoInMilli, overallJuc / nanoInMilli);
  }

  public void testAslsVsJuc() {
    long overallAsls = 0;
    long overallJuc = 0;
    AmortizedSortedLongSet asls = new AmortizedSortedLongSet();
    Set<Long> juc = new TreeSet<Long>();
    int burnIn = 2;
    for (int epoch = 0; epoch < 5; ++epoch) {
      LongArray toRemove = new LongArray();
      for (int i = 0; i < 100000; ++i) {
        long e = RAND.nextLong();
        toRemove.add(RAND.nextLong());
        asls.add(e);
        juc.add(e);
      }

      long startAsls = System.nanoTime();
      asls.removeAll(toRemove);
      if (epoch > burnIn) {
        overallAsls += System.nanoTime() - startAsls;
      }


      long startJuc = System.nanoTime();
      for (LongIterator li : toRemove) {
        juc.remove(li.nextValue());
      }
      if (epoch > burnIn) {
        overallJuc += System.nanoTime() - startJuc;
      }

      if (epoch % 5 == 0 && epoch > 0) {
        asls.clear();
        juc.clear();
      }
    }
    double nanoInMilli = 1000000.0;
    System.out.format("Overall ASLS: %.3fms\nOverall J.U.C.: %.3fms", overallAsls / nanoInMilli, overallJuc / nanoInMilli);
  }
}
