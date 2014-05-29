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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;

public abstract class WritableLongObjMapChecker<T extends WritableLongObjMap> extends WritableLongIntMapChecker<WritableLongIntMapFromLongObjMap> {
  @Override
  protected WritableLongIntMapFromLongObjMap createMap() {
    return new WritableLongIntMapFromLongObjMap(this.<Integer>createObjMap());
  }

  @Override
  protected WritableLongIntMapFromLongObjMap createMapWithCapacity(int capacity) {
    return new WritableLongIntMapFromLongObjMap(this.<Integer>createObjMapWithCapacity(capacity));
  }

  @Override
  protected List<WritableLongIntMapFromLongObjMap> createMapsFromLists(LongList keys, IntList values) {

    List<WritableLongObjMap<Integer>> objMapsFromLists = createObjMapsFromLists(keys, values.toList());
    int size = objMapsFromLists.size();

    ArrayList<WritableLongIntMapFromLongObjMap> maps = new ArrayList<WritableLongIntMapFromLongObjMap>(size);
    for (WritableLongObjMap<Integer> map0 : objMapsFromLists) {
      maps.add(new WritableLongIntMapFromLongObjMap(map0));
    }
    return maps;
  }

  protected abstract <E> WritableLongObjMap<E> createObjMap();

  protected abstract <E> WritableLongObjMap<E> createObjMapWithCapacity(int capacity);

  protected abstract <E> List<WritableLongObjMap<E>> createObjMapsFromLists(LongList keys, List<E> values);

  @Override
  public void testHashCode() {
    int attemptsCount = 10, shuffleCount = 10;
    int sizeMax = 600, step = 50;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      for (int size = step; size < sizeMax; size += step) {
        LongArray keys = generateRandomLongArray(size, SORTED_UNIQUE);
        IntArray values = generateRandomIntArray(keys.size(), UNORDERED);
        int expectedHash = 0;
        for (int i = 0; i < size; i++) {
          expectedHash += IntegersUtils.hash(keys.get(i)) + (new Integer(values.get(i))).hashCode();
        }

        for (WritableLongObjMap<Integer> map0 : createObjMapsFromLists(keys, values.toList())) {
          assertEquals(expectedHash, map0.hashCode());
        }

        IntArray indices = new IntArray(IntProgression.range(size));
        for (int i = 0; i < shuffleCount; i++) {
          map = createMap();
          map.putAll(keys.get(indices), values.get(indices));
          assertEquals(expectedHash, map.hashCode());
          indices.shuffle(myRand);
        }
      }
    }
  }
}
