/*
 * Copyright 2014 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
