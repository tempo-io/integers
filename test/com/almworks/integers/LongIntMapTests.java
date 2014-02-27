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

import com.almworks.integers.func.IntIntProcedure;
import com.almworks.integers.func.IntIntToInt;

import java.util.Arrays;
import java.util.List;

public class LongIntMapTests extends WritableLongIntMapChecker<LongIntListMap> {

  @Override
  protected LongIntListMap createMap() {
    return new LongIntListMap();
  }

  @Override
  protected LongIntListMap createMapWithCapacity(int capacity) {
    return new LongIntListMap(new LongArray(capacity), new IntArray(capacity));
  }

  @Override
  protected List<LongIntListMap> createMapsFromLists(LongList keys, IntList values) {
    return Arrays.asList(new LongIntListMap(new LongArray(keys), new IntArray(values)));
  }

  @Override
  public void checkMap(LongIntListMap actualMap, LongList expectedKeys, IntList expectedValues) {
    super.checkMap(actualMap, expectedKeys, expectedValues);

    final LongArray keysArray = new LongArray(expectedKeys);
    final IntArray valuesArray = new IntArray(expectedValues);
    IntegersUtils.quicksort(keysArray.size(), new IntIntToInt() {
          @Override
          public int invoke(int a, int b) {
            return LongCollections.compare(keysArray.get(a), keysArray.get(b));
          }
        }, new IntIntProcedure() {
          @Override
          public void invoke(int a, int b) {
            keysArray.swap(a, b);
            valuesArray.swap(a, b);
          }
        });
    CHECK.order(keysArray, actualMap.keysAsList());
    CHECK.order(asLongs(valuesArray), asLongs(actualMap.valuesAsList()));

    CHECK.order(actualMap.keysIterator(0, actualMap.size()), keysArray.iterator());
    CHECK.order(actualMap.valuesIterator(0, actualMap.size()), valuesArray.iterator());
  }

  LongList keys = LongProgression.range(0, 10, 2);
  IntList values = IntProgression.range(1, 51, 10);

  public void testLongIntMap() {
    map = createMap();
    map.insertAt(0, 5, 10);
    LongIntListMap.ConsistencyViolatingMutator m = map.startMutation();
    m.commit();
    assertEquals(5, map.getKeyAt(0));
    assertEquals(10, map.getValueAt(0));
  }

  public void testAdjustKeys() {
    for (LongIntListMap map : createMapsFromLists(keys, values)) {
      map.adjustKeys(1, 3, 1);
      LongList expectedKeys = LongArray.create(0, 3, 5, 6, 8);
      CHECK.order(map.keysIterator(0, map.size()), expectedKeys.iterator());
      checkMap(map, expectedKeys, values);

      map.adjustKeys(1, 3, -2);
      expectedKeys = LongArray.create(0, 1, 3, 6, 8);
      CHECK.order(map.keysIterator(0, map.size()), expectedKeys.iterator());
      checkMap(map, expectedKeys, values);

      try {
        map.adjustKeys(-1, 2, 5);
        fail();
      } catch (IndexOutOfBoundsException ex) { }

      try {
        map.adjustKeys(1, 6, 5);
        fail();
      } catch (IndexOutOfBoundsException ex) { }

      try {
        map.adjustKeys(1, 2, -1);
        fail();
      } catch (IllegalArgumentException ex) { }

      try {
        map.adjustKeys(4, 5, -2);
        fail();
      } catch (IllegalArgumentException ex) { }
    }
  }

  public void testSet() {
    for (LongIntListMap map : createMapsFromLists(keys, values)) {
      map.setAt(0, -1, -10);
      LongList expectedKeys = LongArray.create(-1, 2, 4, 6, 8);
      IntList expectedValues = IntArray.create(-10, 11, 21, 31, 41);
      checkMap(map, expectedKeys, expectedValues);

      map.setKey(1, 3);
      try {
        map.setKey(1, 4);
        fail();
      } catch (IllegalArgumentException ex) { }

      map.setAt(1, 3, 100);
      expectedKeys = LongArray.create(-1, 3, 4, 6, 8);
      expectedValues = IntArray.create(-10, 100, 21, 31, 41);
      checkMap(map, expectedKeys, expectedValues);
    }
  }

  public void testMutatorSimple() {
    for (LongIntListMap map : createMapsFromLists(keys, values)) {
      LongIntListMap.ConsistencyViolatingMutator m = map.startMutation();
      m.keys().set(0, -1);
      m.values().set(0, -10);

      try {
        map.startMutation();
        fail();
      } catch (IllegalStateException _) {
        // ok
      }

      try {
        map.get(10);
        fail();
      } catch (IllegalStateException _) {
        // ok
      }

      m.commit();
      checkMap(map, LongArray.create(-1, 2, 4, 6, 8), IntArray.create(-10, 11, 21, 31, 41));

      m = map.startMutation();
      m.keys().set(1, 10);

      try {
        m.commit();
        fail();
      } catch (IllegalStateException _) {
        // ok
      }
    }
  }
}
