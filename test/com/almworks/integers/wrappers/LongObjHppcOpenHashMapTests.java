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

package com.almworks.integers.wrappers;

import com.almworks.integers.IntList;
import com.almworks.integers.LongList;
import com.almworks.integers.WritableLongIntMapChecker;
import com.almworks.integers.WritableLongIntMapFromLongObjMap;

import java.util.Arrays;
import java.util.List;

public class LongObjHppcOpenHashMapTests extends WritableLongIntMapChecker<WritableLongIntMapFromLongObjMap> {
  @Override
  protected WritableLongIntMapFromLongObjMap createMap() {
    return new WritableLongIntMapFromLongObjMap(new LongObjHppcOpenHashMap<Integer>());
  }

  @Override
  protected WritableLongIntMapFromLongObjMap createMapWithCapacity(int capacity) {
    return new WritableLongIntMapFromLongObjMap(new LongObjHppcOpenHashMap<Integer>(capacity));
  }

  @Override
  protected List<WritableLongIntMapFromLongObjMap> createMapsFromLists(LongList keys, IntList values) {
    WritableLongIntMapFromLongObjMap map0 = createMap();
    map0.putAll(keys, values);

    int capacity = Math.max(keys.size(), values.size());
    WritableLongIntMapFromLongObjMap map1 = createMapWithCapacity(capacity);
    map1.putAll(keys, values);

    WritableLongIntMapFromLongObjMap map2 = createMapWithCapacity(capacity * 2);
    map2.putAll(keys, values);

    return Arrays.asList(map0, map1, map2);
  }
}