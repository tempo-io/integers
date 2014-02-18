package com.almworks.integers.wrappers;

import com.almworks.integers.*;

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
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

    return res;
  }

  public void testToString() {
    int size = 10, maxVal = 1000;
    for (int i = 0; i < 10; i++) {
      IntArray keys = generateRandomIntArray(size, UNORDERED, maxVal);
      LongArray values = generateRandomLongArray(size, UNORDERED, maxVal);
      for (IntLongHppcOpenHashMap map : createMapFromLists(keys, values)) {
        System.out.println(map.toString());
      }
    }
  }

}
