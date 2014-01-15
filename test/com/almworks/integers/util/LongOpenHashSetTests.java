package com.almworks.integers.util;

import com.almworks.integers.*;

public class LongOpenHashSetTests extends WritableLongSetChecker {

  @Override
  protected WritableLongSet createSet() {
    return new LongOpenHashSet();
  }

  @Override
  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new LongOpenHashSet(capacity);
  }

  @Override
  protected WritableLongSet[] createSetFromSortedList(LongList sortedList) {
    WritableLongSet set = createSet();
    set.addAll(sortedList);
    return new WritableLongSet[]{set};
  }
}

