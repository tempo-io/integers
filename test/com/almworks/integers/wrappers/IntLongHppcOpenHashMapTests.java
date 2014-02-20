package com.almworks.integers.wrappers;

import com.almworks.integers.*;
import com.almworks.integers.util.LongChainHashSet;
import com.carrotsearch.hppc.IntLongOpenHashMap;
import com.carrotsearch.hppc.cursors.IntLongCursor;

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.LongProgression.range;
import static com.almworks.integers.wrappers.IntLongHppcOpenHashMap.createForAdd;
import static com.almworks.integers.wrappers.IntLongHppcOpenHashMap.createFrom;

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
  protected List<IntLongHppcOpenHashMap> createMapFromLists(IntList keys, LongList values) {
    List<IntLongHppcOpenHashMap> res = new ArrayList<IntLongHppcOpenHashMap>();
    int[] keysNative = keys.toNativeArray();
    long[] valuesNative = values.toNativeArray();
    res.add(createFrom(keys, values));
    res.add(createFrom(keysNative, valuesNative));
    res.add(createFrom(keys.iterator(), values.iterator()));

    map = new IntLongHppcOpenHashMap();
    map.putAll(keysNative, valuesNative);
    res.add(map);

    map = new IntLongHppcOpenHashMap();
    map.putAll(keys, values);
    res.add(map);

    int countToAdd = keys.size() / 2;
    map = new IntLongHppcOpenHashMap(countToAdd);
    map.putAll(keys.subList(0, countToAdd), values.subList(0, countToAdd));
    map.putAll(keys.subList(countToAdd, keys.size()), values.subList(countToAdd, values.size()));
    res.add(map);

    map = new IntLongHppcOpenHashMap();
    map.putAllKeys(keys.iterator(), values.iterator());
    int size = 10;
    map.putAllKeys(keys, LongCollections.concatLists(values, generateRandomLongArray(size, UNORDERED)));

    return res;
  }

  public void testCreateForAdd() {
    int minSize = 16, maxSize = 515;
    float[] loadFactors = {0.1f, 0.3f, 0.5f, 0.75f, 1.0f};
    for (float loadFactor : loadFactors) {
      for (int size = minSize; size <= maxSize; size++) {
        map = createForAdd(size, loadFactor);
        int innerKeysSize = map.myMap.keys.length;

        IntList keys = IntProgression.range(size);
        LongProgression values = LongProgression.range(size);

        map.putAllKeys(keys.iterator(), values.iterator());
        checkMap(map, keys, values);
        assertEquals(innerKeysSize, map.myMap.keys.length);
      }
    }
  }
}
