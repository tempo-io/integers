package com.almworks.integers.wrappers;

import com.almworks.integers.IntList;
import com.almworks.integers.LongList;
import com.almworks.integers.WritableIntLongMap;
import com.almworks.integers.WritableIntLongMapChecker;

import java.util.Arrays;
import java.util.List;

public class IntLongHppcOpenHashMapTests extends WritableIntLongMapChecker<IntLongHppcOpenHashMap> {
  @Override
  protected IntLongHppcOpenHashMap createMap() {
    return new IntLongHppcOpenHashMap();
  }

  @Override
  protected IntLongHppcOpenHashMap createMapWithCapacity(int capacity) {
    return new IntLongHppcOpenHashMap(capacity);
  }

  @Override
  protected List<IntLongHppcOpenHashMap> createMapFromSortedList(IntList keys, LongList values) {
    return Arrays.asList(IntLongHppcOpenHashMap.createFrom(keys, values));
  }
}
