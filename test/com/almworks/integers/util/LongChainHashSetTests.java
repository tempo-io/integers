package com.almworks.integers.util;

import com.almworks.integers.*;

public class LongChainHashSetTests extends WritableLongSetChecker {

  @Override
  protected WritableLongSet createSet() {
    return new LongChainHashSet();
  }

  @Override
  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new LongChainHashSet(capacity);
  }

  @Override
  protected WritableLongSet[] createSetFromSortedList(LongList sortedList) {
    WritableLongSet set = createSet();
    set.addAll(sortedList);
    return new WritableLongSet[]{set};
  }
}
