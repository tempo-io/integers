package com.almworks.integers;

import java.util.Arrays;
import java.util.List;

public class IntLongMapTests extends WritableIntLongMapChecker<IntLongMap> {

  @Override
  protected IntLongMap createMap() {
    return new IntLongMap();
  }

  @Override
  protected IntLongMap createMapWithCapacity(int capacity) {
    return new IntLongMap(new IntArray(capacity), new LongArray(capacity));
  }

  @Override
  protected List<IntLongMap> createMapFromLists(IntList keys, LongList values) {
    return Arrays.asList(new IntLongMap(new IntArray(keys), new LongArray(values)));
  }

  public void testIntLongMap() {
    map = new IntLongMap();
    map.insertAt(0, 5, 10);
    IntLongMap.ConsistencyViolatingMutator m = map.startMutation();
    m.commit();
    assertEquals(5, map.getKeyAt(0));
    assertEquals(10, map.getValueAt(0));
  }
}
