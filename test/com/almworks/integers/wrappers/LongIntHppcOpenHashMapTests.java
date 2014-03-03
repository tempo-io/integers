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

import com.almworks.integers.*;

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.wrappers.LongIntHppcOpenHashMap.createForAdd;
import static com.almworks.integers.wrappers.LongIntHppcOpenHashMap.createFrom;

public class LongIntHppcOpenHashMapTests extends WritableLongIntMapChecker<LongIntHppcOpenHashMap> {
  @Override
  protected LongIntHppcOpenHashMap createMap() {
    return new LongIntHppcOpenHashMap();
  }

  @Override
  protected LongIntHppcOpenHashMap createMapWithCapacity(int capacity) {
    return new LongIntHppcOpenHashMap(capacity);
  }

  @Override
  protected List<LongIntHppcOpenHashMap> createMapsFromLists(LongList keys, IntList values) {
    List<LongIntHppcOpenHashMap> res = new ArrayList();
    long[] keysNative = keys.toNativeArray();
    int[] valuesNative = values.toNativeArray();
    res.add(createFrom(keys, values));
    res.add(createFrom(keysNative, valuesNative));
    res.add(createFrom(keys.iterator(), values.iterator()));

    map = new LongIntHppcOpenHashMap();
    map.putAll(keysNative, valuesNative);
    res.add(map);

    map = new LongIntHppcOpenHashMap();
    map.putAll(keys, values);
    res.add(map);

    int countToAdd = keys.size() / 2;
    map = new LongIntHppcOpenHashMap(countToAdd);
    map.putAll(keys.subList(0, countToAdd), values.subList(0, countToAdd));
    map.putAll(keys.subList(countToAdd, keys.size()), values.subList(countToAdd, values.size()));
    res.add(map);

    map = new LongIntHppcOpenHashMap();
    map.putAllKeys(keys.iterator(), values.iterator());
    int size = 10;
    map.putAllKeys(keys, IntCollections.concatLists(values, generateRandomIntArray(size, UNORDERED)));

    return res;
  }

  public void testCreateForAdd() {
    int minSize = 16, maxSize = 515;
    float[] loadFactors = {0.1f, 0.3f, 0.5f, 0.75f, 1.0f};
    for (float loadFactor : loadFactors) {
      for (int size = minSize; size <= maxSize; size++) {
        map = createForAdd(size, loadFactor);
        int innerKeysSize = map.myMap.keys.length;

        LongList keys = LongProgression.range(size);
        IntProgression values = IntProgression.range(size);

        map.putAllKeys(keys.iterator(), values.iterator());
        checkMap(map, keys, values);
        assertEquals(innerKeysSize, map.myMap.keys.length);
      }
    }
  }
}
