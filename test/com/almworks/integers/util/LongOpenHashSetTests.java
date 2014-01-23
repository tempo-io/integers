package com.almworks.integers.util;

import com.almworks.integers.*;

public class LongOpenHashSetTests extends WritableLongSetChecker {

  @Override
  protected WritableLongSet createSet() {
    return new LongOpenHashSet();
  }

  @Override
  protected WritableLongSet createSetWithCapacity(int capacity) {
    return LongOpenHashSet.createForAdd(capacity);
  }

  @Override
  protected WritableLongSet[] createSetFromSortedUniqueList(LongList sortedList) {
    WritableLongSet set = LongOpenHashSet.createForAdd(sortedList.size());
    set.addAll(sortedList);
    return new WritableLongSet[]{set};
  }

  public void testCreateForAdd() {
    int maxSize = 128;
    for (int size = 0; size <= maxSize; size++) {
      assertTrue(LongOpenHashSet.createForAdd(size).getThreshold() >= size);
    }
  }
}

