package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongCollections;

import java.util.Arrays;

public class LongIterableLexicographicComparatorTests extends IntegersFixture {
  public void testSimple() {
    long[][] ars = {{0,1,2}, {2,4,6}, {0,1}, {5,7,9}, {-1, 1, 2}, {0}, {10}, {0,1,2,3}};
    LongArray[] arrays = new LongArray[ars.length];
    for (int i = 0; i < ars.length; i++) {
      arrays[i] = new LongArray(ars[i]);
    }
    Arrays.sort(arrays, new LongIterableLexicographicComparator());
    StringBuilder sb = new StringBuilder();
    for (LongArray array: arrays) {
      LongCollections.append(sb, array);
    }
    assertEquals("(-1, 1, 2)(0)(0, 1)(0, 1, 2)(0, 1, 2, 3)(2, 4, 6)(5, 7, 9)(10)", sb.toString());
  }
}