package com.almworks.integers.wrappers;


import com.almworks.integers.LongList;
import com.almworks.integers.WritableLongSet;
import com.almworks.integers.WritableLongSetChecker;

public class LongHppcOpenHashSetTests extends WritableLongSetChecker {

  @Override
  protected WritableLongSet createSet() {
    return new HPPCLongOpenHashSet();
  }

  @Override
  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new HPPCLongOpenHashSet(capacity);
  }

  @Override
  protected WritableLongSet[] createSetFromSortedUniqueList(LongList sortedList) {
    WritableLongSet tempSet = createSet();
    tempSet.addAll(sortedList);
    return new WritableLongSet[]{tempSet};
  }

}