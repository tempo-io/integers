package com.almworks.integers.util;

import com.almworks.integers.LongList;
import junit.framework.TestCase;

/**
 * This class is made abstract to avoid running very long tests
 */
public abstract class LongSetBuilderPerformanceComparison extends TestCase {

  public void testComparison() {
    int RUNS = 10;
    int CYCLES = 300;
    int CYCLE_LENGTH = 200;
    int OVERLAY = CYCLE_LENGTH / 2;

    runLongSetBuilder(RUNS, CYCLES, CYCLE_LENGTH, LongSetBuilder.DEFAULT_TEMP_STORAGE_SIZE);
  }

  public void _testSize() {
    int from = 10;
    int to = 1024;
    long[] r = new long[to - from + 1];
    int counter = 0;
    for (int i = from; i <= to; i++) {
      System.out.println("tempSize = " + i);
      r[i - from] = runLongSetBuilder(10, 500, 200, i);
    }
    System.out.println();
    for (int i = 0 ; i < r.length; i++) {
      System.out.println((i + from) + ";" + r[i]);
    }
  }

  public void testSize2() {
    int[] values = {128, 256, 512, 1024};
    long[] r = new long[values.length];
    int counter = 0;
    for (int i = 0; i < values.length; i++) {
      System.out.println("tempSize = " + values[i]);
      r[i] = runLongSetBuilder(500, 500, 200, values[i]);
    }
    System.out.println();
    for (int i = 0 ; i < values.length; i++) {
      System.out.println(values[i] + ";" + r[i]);
    }
  }

  private long runLongSetBuilder(int runs, int cycles, int cycleLength, int tempStorageSize) {
    int overlay = cycleLength / 2;
    LongList collection = null;
    LongSetBuilder set = null;
    long start = System.currentTimeMillis();
    for (int k = 0; k < runs; k++) {
      set = new LongSetBuilder(tempStorageSize);
      for (int i = cycles - 1; i >= 0; i--) {
        int from = i * cycleLength - overlay;
        int to = from + cycleLength + overlay;
        for (int j = from; j <= to; j++) {
          set.add(j);
        }
      }
      collection = set.commitToArray();
    }
    long time = System.currentTimeMillis() - start;
    System.out.println("size = " + collection.size());
    System.out.println("time = " + time + "ms");
    return time;
  }
}
