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
import com.almworks.util.RandomHolder;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

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
    LongList expected = LongProgression.arithmetic(0, 6);
    compare.order(expected, set.toSortedLongArray());
    set.removeAll(expected);
    assertTrue(set.isEmpty());

    set.clear();
    set.addAll(1, 2, 5, 7, 8, 11, 14, 15);
    set.debugPrintTreeStructure(System.out);
    set.add(4);
    set.debugPrintTreeStructure(System.out);
    set.remove(11);
    set.remove(256);
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

  public void testEdgeCases() {
    assertFalse(set.remove(Long.MIN_VALUE));
    assertFalse(set.remove(0));
    set.removeAll(12, 23, 12, 51);
    assertTrue(set.isEmpty());
    set.compactify();
    assertTrue(set.isEmpty());
    LongList ll = new LongArray();
    DynamicLongSet set2 = DynamicLongSet.fromSortedList(ll);
    assertTrue(set2.isEmpty());
    set.addAll(1, 3, 2, Long.MIN_VALUE, Long.MAX_VALUE);
    assertTrue(set.toSortedLongArray().checkSorted(true));
    assertTrue(set.contains(1));
    assertTrue(set.contains(Long.MIN_VALUE));
    assertFalse(set.contains(0));
    set.debugPrintTreeStructure(System.out);
  }

  public void testRandom() {
    Random r = new RandomHolder().getRandom();
    int[] ns = new int[]{510,513,1025,2049,4097}; // testing sizes near 2^n
    int nAttempts = 5;
    LongSetBuilder anotherSet = new LongSetBuilder();
    DynamicLongSet dynamicSet = new DynamicLongSet(510);
    WritableLongList toAdd = new LongArray();
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      for (int i = 0; i < ns[attempt]; ++i) toAdd.add(r.nextLong());
      dynamicSet.addAll(toAdd);
      dynamicSet.compactify();
      anotherSet.addAll(toAdd);
      LongList anotherSetList = anotherSet.toTemporaryReadOnlySortedCollection();
      LongList setList = dynamicSet.toSortedLongArray();
      System.err.println("attempt #" + attempt);
      compare.order(anotherSetList, setList);

      // testRemove runs long, so it's ran only twice
      if (attempt > 1) continue;
      testRemove(toAdd, toAdd);
      testRemove(toAdd, anotherSetList);
      testRemove(anotherSetList, toAdd);
      testRemove(anotherSetList, anotherSetList);
    }
  }
  
  private enum DlsOperation {
    REMOVE, ADD
  }
  
  public void testColoringTypes() {
    int attempts = 7; //2000;
    int sz = 8; //8200;
    int waitTime = 000;
    Random r = new RandomHolder().getRandom();

    testColoringTypes(r, waitTime, attempts, sz, DynamicLongSet.ColoringType.BALANCED, DlsOperation.ADD);
    testColoringTypes(r, waitTime, attempts, sz, DynamicLongSet.ColoringType.TO_ADD, DlsOperation.ADD);
//    testColoringTypes(r, waitTime, attempts, sz, DynamicLongSet.ColoringType.TO_REMOVE, DlsOperation.ADD);
//    testColoringTypes(r, waitTime, attempts, sz, DynamicLongSet.ColoringType.BALANCED, DlsOperation.REMOVE);
//    testColoringTypes(r, waitTime, attempts, sz, DynamicLongSet.ColoringType.TO_ADD, DlsOperation.REMOVE);
//    testColoringTypes(r, waitTime, attempts, sz, DynamicLongSet.ColoringType.TO_REMOVE, DlsOperation.REMOVE);
  }

  private void testColoringTypes(Random r, int waitTime, int attempts, int sz, DynamicLongSet.ColoringType cT, DlsOperation oper) {
    String opName;
    if (oper == DlsOperation.REMOVE) {
      switch (cT) {
        case TO_ADD: opName = "type=toAdd, op=remove, time="; break;
        case BALANCED: opName = "type=balanced, op=remove, time="; break;
        default: opName = "type=toRemove, op=remove, time=";
      }
    } else {
      switch (cT) {
        case TO_ADD: opName = "type=toAdd, op=add, time="; break;
        case BALANCED: opName = "type=balanced, op=add, time="; break;
        default: opName = "type=toRemove, op=add, time=";
      }
    }
    List<DynamicLongSet> list = new ArrayList<DynamicLongSet>(attempts);
    List<WritableLongList> list2 = new ArrayList<WritableLongList>(attempts);
    for (int att = 0; att < attempts; att++) {
      WritableLongList srcList = new LongArray();
      WritableLongList addList = new LongArray();
      for (int i = 0; i < sz; i++) {
        srcList.add(r.nextLong());
        addList.add(r.nextLong());
      }
      srcList.sort();
      DynamicLongSet dls;
      if (oper == DlsOperation.REMOVE) {
        list2.add(srcList);
        dls = DynamicLongSet.fromSortedList(srcList, cT);
      } else {
        list2.add(addList);
        dls = DynamicLongSet.fromSortedList(srcList, cT);
      }
      list.add(dls);
    }

    int i = 0;
    long res = 0, t;
    Iterator<DynamicLongSet> it = list.iterator();
    Iterator<WritableLongList> it2 = list2.iterator();
    for (int att = 0; att < attempts; att++) {
      DynamicLongSet dls = it.next();
      WritableLongList nx = it2.next();
      t = System.currentTimeMillis();
      if (oper == DlsOperation.REMOVE)
        dls.removeAll(nx);
      else
        dls.addAll(nx);
      t = System.currentTimeMillis() - t;
      res += t;
      if (i == 5) {
        res = 0;
        System.out.println("waiting before start");
        t = System.currentTimeMillis();
        while (System.currentTimeMillis() - t < waitTime);
        System.out.println("started");
      }
      i++;
    }
    System.out.println(opName + res);
    System.out.println("waiting after finish");
    t = System.currentTimeMillis();
    while (System.currentTimeMillis() - t < waitTime);
    System.out.println("resuming");
  }

  public void testAdd() {
    int n = 1023;
    Random r = new RandomHolder().getRandom();
    for (int i = 0; i < n; ++i) set.add(r.nextLong());
    long[] toAdd = new long[n];
    for (int i = 0; i < n; ++i) toAdd[i] = r.nextLong();
    long start = System.currentTimeMillis();
    set.addAll(toAdd);
    float tm = (System.currentTimeMillis() - start)/1000F;
    System.out.println("Size = " + set.size() + ", time = " + tm + " seconds.");
  }

  private void testRemove(LongList srcAdd, LongList srcRemove) {
    DynamicLongSet dynamicSet = new DynamicLongSet();

    dynamicSet.addAll(srcAdd);
    dynamicSet.removeAll(srcRemove);
    assertTrue(dynamicSet.isEmpty());

    dynamicSet.addAll(srcAdd);
    for (LongIterator ii : srcRemove) dynamicSet.remove(ii.value());
    assertTrue(dynamicSet.isEmpty());
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
    LongList expected = LongArray.create(11,12,13,14,15,16);
    WritableLongList res = new SameValuesLongList();
    set.addAll(14,12,15,11,13,16);
    try {
      for (LongIterator i : set) {
        res.add(i.value());
        if (i.value() == 16) set.add(99);
      }
      fail();
    } catch (ConcurrentModificationException e) {}
    assertEquals(expected, res);
  }

  public void testAddRemove() {
    WritableLongList sourceAdd = LongArray.create(14,7,3,12,6,2,13,8,5,15,4,0,-1,10,-2,20,-6,32);
    WritableLongList sourceRemove = LongArray.create(7,3,6,14,5,8,2,-7,0,15,3,1);
    LongList expected = LongArray.create(-6,-2,-1,4,10,12,13,20,32);

    int stateCount = 0;
    LongIterator iiAdd = sourceAdd.iterator();
    LongIterator iiRemove = sourceRemove.iterator();
    for (int i = 0; i<30;i++) {
      if (stateCount >= 0)
        set.add(iiAdd.nextValue());
      else
        set.remove(iiRemove.nextValue());
      stateCount++;
      if (stateCount == 3) stateCount = -2;
    }
    assertEquals(expected, set.toSortedLongArray());
  }
}
