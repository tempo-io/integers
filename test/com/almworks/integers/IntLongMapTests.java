package com.almworks.integers;

import junit.framework.TestCase;

public class IntLongMapTests extends TestCase {
  public void testIntLongMap() {
    IntLongMap map = new IntLongMap();
    map.insertAt(0, 5, 10);
    IntLongMap.ConsistencyViolatingMutator m = map.startMutation();
    m.commit();
    assertEquals(5, map.getKeyAt(0));
    assertEquals(10, map.getValueAt(0));
  }
}
