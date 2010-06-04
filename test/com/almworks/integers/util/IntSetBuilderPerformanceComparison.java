package com.almworks.integers.util;

import junit.framework.TestCase;
import com.almworks.integers.IntList;

/**
 * This class is made abstract to avoid running very long tests 
 */
public abstract class IntSetBuilderPerformanceComparison extends TestCase {

  public void testComparison() {
    int RUNS = 10;
    int CYCLES = 300;
    int CYCLE_LENGTH = 200;
    int OVERLAY = CYCLE_LENGTH / 2;
    
    runIntSetBuilder(RUNS, CYCLES, CYCLE_LENGTH, IntSetBuilder.DEFAULT_TEMP_STORAGE_SIZE);
  }

  public void testSize() {
    int from = 10;
    int to = 1024;
    long[] r = new long[to - from + 1];
    for (int i = from; i <= to; i++) {
      System.out.println("tempSize = " + i);
      r[i - from] = runIntSetBuilder(10, 500, 200, i);
    }
    System.out.println();
    for (int i = 0 ; i < r.length; i++) {
      System.out.println((i + from) + ";" + r[i]);
    }
  }

  private long runIntSetBuilder(int runs, int cycles, int cycleLength, int tempStorageSize) {
    int overlay = cycleLength / 2;
    IntList collection = null;
    IntSetBuilder set = null;
    long start = System.currentTimeMillis();
    for (int k = 0; k < runs; k++) {
      set = new IntSetBuilder(tempStorageSize);
      for (int i = cycles - 1; i >= 0; i--) {
        int from = i * cycleLength - overlay;
        int to = from + cycleLength + overlay;
        for (int j = from; j <= to; j++) {
          set.add(j);
        }
      }
      collection = set.toSortedCollection();
    }
    long time = System.currentTimeMillis() - start;
    System.out.println("size = " + collection.size());
    System.out.println("time = " + time + "ms");
    return time;
  }
}
