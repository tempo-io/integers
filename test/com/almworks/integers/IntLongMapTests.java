package com.almworks.integers;

import junit.framework.TestCase;

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
  protected List<IntLongMap> createMapFromSortedList(IntList keys, LongList values) {
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

  public void test() {
    map = new IntLongMap();
    for (int i = 0; i < 12; i++) {
      map.put(i, i * i);
    }
    System.out.println(map.toTableString());
    System.out.println(map.toString());

  }
}
