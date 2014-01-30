package com.almworks.integers.util;

import com.almworks.integers.*;

public class LongChainHashSetTests extends WritableLongSetChecker {

  @Override
  protected WritableLongSet createSet() {
    return new LongChainHashSet();
  }

  @Override
  protected WritableLongSet createSetWithCapacity(int capacity) {
    return LongChainHashSet.createForAdd(capacity);
  }

  @Override
  protected WritableLongSet[] createSetFromSortedUniqueList(LongList sortedList) {
    WritableLongSet set = LongChainHashSet.createForAdd(sortedList.size());
    set.addAll(sortedList);
    return new WritableLongSet[]{set};
  }

  public void testCreateForAdd() {
    int maxSize = 128;
    for (int size = 0; size <= maxSize; size++) {
      assertTrue(LongChainHashSet.createForAdd(size).getThreshold() >= size);
    }
  }

}
