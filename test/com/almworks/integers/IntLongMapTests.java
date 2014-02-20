package com.almworks.integers;

import java.util.Arrays;
import java.util.List;

public class IntLongMapTests extends WritableIntLongMapChecker<IntLongListMap> {

  @Override
  protected IntLongListMap createMap() {
    return new IntLongListMap();
  }

  @Override
  protected IntLongListMap createMapWithCapacity(int capacity) {
    return new IntLongListMap(new IntArray(capacity), new LongArray(capacity));
  }

  @Override
  protected List<IntLongListMap> createMapFromLists(IntList keys, LongList values) {
    return Arrays.asList(new IntLongListMap(new IntArray(keys), new LongArray(values)));
  }

  public void testIntLongMap() {
    map = new IntLongListMap();
    map.insertAt(0, 5, 10);
    IntLongListMap.ConsistencyViolatingMutator m = map.startMutation();
    m.commit();
    assertEquals(5, map.getKeyAt(0));
    assertEquals(10, map.getValueAt(0));
  }
}
