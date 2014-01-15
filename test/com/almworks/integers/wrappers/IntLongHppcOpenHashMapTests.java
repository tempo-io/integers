package com.almworks.integers.wrappers;

import com.almworks.integers.IntList;
import com.almworks.integers.LongList;
import com.almworks.integers.WritableIntLongMap;
import com.almworks.integers.WritableIntLongMapChecker;

public class IntLongHppcOpenHashMapTests extends WritableIntLongMapChecker {
  @Override
  protected WritableIntLongMap createMap() {
    return new IntLongHppcOpenHashMap();
  }

  @Override
  protected WritableIntLongMap createMapWithCapacity(int capacity) {
    return new IntLongHppcOpenHashMap(capacity);
  }

  @Override
  protected WritableIntLongMap[] createMapFromSortedList(IntList keys, LongList values) {
    return new WritableIntLongMap[]{IntLongHppcOpenHashMap.createFrom(keys, values)};
  }
}
