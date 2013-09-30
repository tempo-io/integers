package com.almworks.integers.util;

import com.almworks.integers.LongList;
import com.almworks.integers.WritableLongSet;

/**
 * it's normal ASLST fail iterator tests.
 */
public class AmortizedSortedLongSetTests extends AmortizedSortedLongSetTempTests {
  @Override
  protected WritableLongSet createSet() {
    return createSetWithCapacity(-1);
  }

  @Override
  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new AmortizedSortedLongSet();
  }

  @Override
  protected  WritableLongSet[] createSetFromSortedList(LongList sortedList) {
    AmortizedSortedLongSet newSet = new AmortizedSortedLongSet();
    newSet.addAll(sortedList);
    return new WritableLongSet[]{newSet};
  }
}
