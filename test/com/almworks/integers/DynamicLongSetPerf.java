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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** This class is abstract to prevent it from running in the build. */
public abstract class DynamicLongSetPerf extends DynamicLongSetTests {
  private final int n;
  private final int nAttempts;

  public DynamicLongSetPerf(int n, int nAttempts) {
    this.n = n;
    this.nAttempts = nAttempts;
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 3) {
      System.err.println("Arguments: [test#] [n] [nCycles]\n test# - 1..3\nn - number of elements added during each cycle \n nCycles - amount of cycles");
      System.exit(1);
    }
    int testN = Integer.parseInt(args[0]);
    int n = Integer.parseInt(args[1]);
    int nAttempts = Integer.parseInt(args[2]);
    System.err.println("Starting perf test #" + testN + " with n = " + n + ", nCycles = " + nAttempts);
    DynamicLongSetPerf perf = new DynamicLongSetPerf(n, nAttempts) {};
    switch (testN) {
    case 1: perf.testPerf(); break;
    case 2: perf.testPerf2(); break;
    case 3: perf.testPerfAddIfAbsent(); break;
//    case 4: perf.compareTreeWithTroveLongHashSet(); break;
//    case 5: perf.compareSortedArrayWithTroveLongHashSet(); break;
    default: System.err.println("No test #" + testN); System.exit(2);
    }
  }

  public void testPerf() {
    System.out.println("===");
    LongSetBuilder anotherSet = new LongSetBuilder();
    DynamicLongSet dynamicSet = new DynamicLongSet(n*nAttempts);
    long[] toAdd = new long[n];
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      for (int i = 0; i < n; ++i) toAdd[i] = RAND.nextLong();
      dynamicSet.addAll(toAdd);
      anotherSet.addAll(toAdd);
      LongList anotherSetList = anotherSet.toTemporaryReadOnlySortedCollection();
      LongList setList = dynamicSet.toArray();
      System.err.println("attempt #" + attempt);
      CHECK.order(anotherSetList, setList);
    }
  }

  public void testPerf2() {
    LongSetBuilder lsb = new LongSetBuilder();
    DynamicLongSet dls = new DynamicLongSet();
//    DynamicLongSet dls = new DynamicLongSet(n*nAttempts);
    long[] toAdd = new long[n];
    for (int attempt = 0; attempt < nAttempts; ++attempt) {
      for (int i = 0; i < n; ++i) toAdd[i] = RAND.nextLong();
      lsb.addAll(toAdd);
      dls.addAll(toAdd);
      LongList lsbList = lsb.toTemporaryReadOnlySortedCollection();
      for (int i = 0; i < n; ++i) {
        long l = RAND.nextLong();
        boolean expected = lsbList.binarySearch(l) >= 0;
        boolean actual = dls.contains(l);
        assertEquals(attempt + " " + i, expected, actual);
      }
      System.err.println("attempt #" + attempt);
    }
  }

  public void testPerfAddIfAbsent() {
    LongSetBuilder lsb = new LongSetBuilder();
    DynamicLongSet dls = new DynamicLongSet();
    for (int i = 0; i < n; ++i) {
      if (i % nAttempts == 0) {
        assertEquals(lsb.size(), dls.size());
        System.out.println(i + " set size: " + lsb.size() + " ratio: " + lsb.size()*1./i);
      }
      long key = RAND.nextInt(n);
      boolean lsbContains = lsb.toTemporaryReadOnlySortedCollection().binarySearch(key) >= 0;
      boolean dlsContains = dls.contains(key);
      assertEquals(lsbContains, dlsContains);
      if (!lsbContains) {
        lsb.add(key);
        dls.add(key);
      }
    }
  }

  private enum DlsOperation {
    REMOVE, ADD
  }


  public void testColoringTypes() {
    int attempts = 7; //2000;
    int sz = 8; //8200;
    int waitTime = 0;

    testColoringTypes(waitTime, attempts, sz, DynamicLongSet.ColoringType.BALANCED, DlsOperation.ADD);
    testColoringTypes(waitTime, attempts, sz, DynamicLongSet.ColoringType.TO_ADD, DlsOperation.ADD);
//    testColoringTypes(waitTime, attempts, sz, DynamicLongSet.ColoringType.TO_REMOVE, DlsOperation.ADD);
//    testColoringTypes(waitTime, attempts, sz, DynamicLongSet.ColoringType.BALANCED, DlsOperation.REMOVE);
//    testColoringTypes(waitTime, attempts, sz, DynamicLongSet.ColoringType.TO_ADD, DlsOperation.REMOVE);
//    testColoringTypes(waitTime, attempts, sz, DynamicLongSet.ColoringType.TO_REMOVE, DlsOperation.REMOVE);
  }

  private void testColoringTypes(int waitTime, int attempts, int sz, DynamicLongSet.ColoringType cT, DlsOperation oper) {
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
        srcList.add(RAND.nextLong());
        addList.add(RAND.nextLong());
      }
      srcList.sort();
      if (oper == DlsOperation.REMOVE) {
        list2.add(srcList);
      } else {
        list2.add(addList);
      }
      DynamicLongSet dls = DynamicLongSet.fromSortedList(srcList, cT);
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



// To use these tests, add trove to the test folder
/*
  public void compareTreeWithTroveLongHashSet() throws IOException {
    System.out.println("press any key");
    System.in.read();
    TLongLongHashMap hash = new TLongLongHashMap();
    DynamicLongSet tree = new DynamicLongSet();
    for (int i = 0; i < n; ++i) {
      if ( i % nAttempts == 0) {
        assertEquals(hash.size(), tree.size());
        System.out.println(i + " set size: " + hash.size());
      }
      long key = RAND.nextInt(n);
      boolean hashContains = hash.containsKey(key);
      boolean treeContains = tree.contains(key);
      assertEquals(hashContains, treeContains);
      if (!hashContains) {
        hash.put(key, key);
        tree.add(key);
      }
    }
    int a = 10;
  }

  public void compareSortedArrayWithTroveLongHashSet() throws IOException {
    TLongLongHashMap hash = new TLongLongHashMap();
    LongSetBuilder setBuilder = new LongSetBuilder();
    int domain = (int)(1.2*n);
    for (int i = 0; i < n; ++i) {
      long key = RAND.nextInt(domain);
      hash.put(key, key);
      setBuilder.add(key);
    }
    System.out.println("Press any key");
    System.in.read();
    LongList sorted = setBuilder.toTemporaryReadOnlySortedCollection();
    for (int i = 0; i < n; ++i) {
      long key = RAND.nextInt(domain);
      boolean hashContains = hash.containsKey(key);
      boolean sortedContains = sorted.binarySearch(key) >= 0;
      assertEquals(hashContains, sortedContains);
    }
  }

 */
}
