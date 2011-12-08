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
import com.almworks.integers.optimized.SegmentedLongArray;
import com.almworks.integers.util.LongSetBuilder;
import com.almworks.util.RandomHolder;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Random;

public class DynamicLongSetTests extends TestCase {
  protected final DynamicLongSet set = new DynamicLongSet();
  protected final IntCollectionsCompare compare = new IntCollectionsCompare();

  @Override
  public void setUp() throws Exception {
    super.setUp();
    set.clear();
  }

  public void testSimple() {
    set.addAll(3, 1, 5, 2, 0, 4);
    set.debugPrintTreeStructure(System.out);
    System.err.println(set.dumpArrays(0));
    compare.order(LongProgression.arithmetic(0, 6), set.toSortedLongArray());

    set.clear();
    set.addAll(1, 2, 5, 7, 8, 11, 14, 15);
    set.debugPrintTreeStructure(System.out);
    set.add(4);
    set.debugPrintTreeStructure(System.out);
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
        LongArray sorted = set.toSortedLongArray();
        String debugInfo = sorted.toString() + "\ntoAdd:\n" + toAdd + "\ntree:" + treeStructureToString();
        assertTrue(debugInfo, sorted.checkSorted(true));
        assertEquals(debugInfo, expectedValues.size(), set.size());
        compare.unordered(sorted, expectedValues);
      }
    });
  }

  private String treeStructureToString() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    set.debugPrintTreeStructure(new PrintStream(baos));
    return baos.toString();
  }

  public void testNil() {
    set.addAll(1, 3, 2, Long.MIN_VALUE, Long.MAX_VALUE);
    assertTrue(set.toSortedLongArray().checkSorted(true));
    assertTrue(set.contains(1));
    assertTrue(set.contains(Long.MIN_VALUE));
    assertFalse(set.contains(0));
    set.debugPrintTreeStructure(System.out);
  }

  public void testRandom() {
    Random r = new RandomHolder().getRandom();
    int n = 1000;
    int nAttempts = 5;
    LongSetBuilder anotherSet = new LongSetBuilder();
    DynamicLongSet dynamicSet = new DynamicLongSet(n*nAttempts);
    long[] toAdd = new long[n];
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      for (int i = 0; i < n; ++i) toAdd[i] = r.nextLong();
      dynamicSet.addAll(toAdd);
      dynamicSet.compactify();
      anotherSet.addAll(toAdd);
      LongList anotherSetList = anotherSet.toTemporaryReadOnlySortedCollection();
      LongList setList = dynamicSet.toSortedLongArray();
      System.err.println("attempt #" + attempt);
      compare.order(anotherSetList, setList);
    }
  }

  public void printPerm() {
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

  public void testIterator() {
    SameValuesLongList src = new SameValuesLongList();
    src.addAll(14,12,15,11,13,16);
    SameValuesLongList expected = new SameValuesLongList();
    expected.addAll(11,12,13,14,15,16);
    SameValuesLongList res = new SameValuesLongList();
    DynamicLongSet dls = new DynamicLongSet();
    dls.addAll(src);
    for (LongIterator i : dls) {
      res.add(i.value());
    }
    assertEquals(expected, res);
  }

  public void testRemoveCompactify() {
    WritableLongList source = new LongArray();
    WritableLongList result;

    DynamicLongSet dls, dls2;
    source.addAll(14, 7, 3, 12, 6, 2, 13, 8, 5, 15, 4, 0, -1, 10, -2, 20, -6, 32);
    result = new LongArray(LongCollections.toSortedNativeArray(source));
    dls = DynamicLongSet.fromSortedList(result);
    for (LongIterator i: source) {
      dls.remove(i.value());
    }

    result.clear();
    for (LongIterator ii : source) {
      dls.clear();
      dls = DynamicLongSet.fromSortedListToAdd(result);
      dls2 = DynamicLongSet.fromSortedListToRemove(result);
      dls2.compactify();
      assertEquals(dls.toSortedLongArray(), dls2.toSortedLongArray());
      result.add(ii.value());
      result = new LongArray(LongCollections.toSortedNativeArray(result));
    }

    dls.clear();
    result.clear();
    result.addAll(7,3,6,14,5,8,2,-7,0,15,3,1);
    int statecount = 0;
    LongIterator i1 = source.iterator();
    LongIterator i2 = result.iterator();
    for (int i = 0; i<30;i++) {
      if (statecount >= 0)
        dls.add(i1.nextValue());
      else
        dls.remove(i2.nextValue());
      statecount++;
      if (statecount == 3) statecount = -2;
    }

    dls.clear();
    dls.addAll(1, 2, 3);
    dls2 = new DynamicLongSet();
    dls2.addAll(3,4,5);
    dls2.removeAll(dls);
    source = new SegmentedLongArray();
    source.addAll(4,5);
    assertEquals(source, dls2.toSortedLongArray());
    dls2 = new DynamicLongSet();
    dls2.addAll(3,4,5);
    dls2.addAll(dls);
    source = new SegmentedLongArray();
    source.addAll(1,2,3,4,5);
    assertEquals(source, dls2.toSortedLongArray());
  }
}
