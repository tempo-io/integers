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
  protected WritableLongSet[] createSetFromSortedUniqueList(LongList sortedList) {
    WritableLongSet set = createSet();
    set.addAll(sortedList);
    return new WritableLongSet[]{set};
  }

  public void test() {
    int size = 30;
    set = LongOpenHashSet.createForAdd(size);
    for (int i = 0; i < 48; i++) {
      long value = RAND.nextLong();
      set.add(value);
    }
    System.out.println("OK!");
    set.add(RAND.nextLong());
    System.out.println("OK!");
  }
}

