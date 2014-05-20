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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntCollections.repeat;
import static com.almworks.integers.IntIterators.cycle;
import static com.almworks.integers.IntIterators.limit;
import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.LongCollections.concatLists;
import static com.almworks.integers.LongIteratorSpecificationChecker.checkIteratorThrowsCME;
import static com.almworks.integers.LongIterators.range;
import static com.almworks.integers.WritableLongIntMapProjection.DEFAULT_CONTAINS_VALUE;

public abstract class WritableLongIntMapChecker<T extends WritableLongIntMap> extends WritableLongSetChecker<WritableLongIntMapProjection> {
  protected abstract T createMap();

  protected abstract T createMapWithCapacity(int capacity);

  protected abstract List<T> createMapsFromLists(LongList keys, IntList values);

  @Override
  protected List<WritableLongIntMapProjection> createSets(LongList sortedUniqueList) {
    IntList repeatContains = repeat(DEFAULT_CONTAINS_VALUE, sortedUniqueList.size());
    List<T> maps = createMapsFromLists(sortedUniqueList, repeatContains);
    List<WritableLongIntMapProjection> sets = new ArrayList();
    for (T map0 : maps) {
      sets.add(new WritableLongIntMapProjection(map0));
    }
    return sets;
  }

  @Override
  protected WritableLongIntMapProjection createSet() {
    return new WritableLongIntMapProjection(createMap());
  }

  @Override
  protected WritableLongIntMapProjection createSet(LongList sortedUniqueList) {
    T map0 = createMap();
    map0.putAll(sortedUniqueList, repeat(DEFAULT_CONTAINS_VALUE, sortedUniqueList.size()));
    return new WritableLongIntMapProjection(map0);
  }

  @Override
  protected WritableLongIntMapProjection createSetWithCapacity(int capacity) {
    return createSet();
  }

  protected T map;

  public void setUp() throws Exception {
    super.setUp();
    map = createMap();
  }

  public void testSimpleMap() {
    for (int i = 0; i < 10; i++) {
      map.put(i, i * i);
    }
    LongArray expected = new LongArray(range(0, 10));
    assertEquals(10, map.size());
    assertTrue(map.containsKeys(expected));
    LongArray actual = new LongArray(10);
    for (LongIterator keysIt : map.keysIterator()) {
      actual.add(keysIt.value());
    }
    actual.sort();
    CHECK.order(actual.iterator(), expected.iterator());

    for (LongIterator it : expected) {
      long value = it.value();
      assertEquals(value * value, map.get(value));
    }
  }

  public void testConstructors() {
    // assumed that method createMapsFromLists returns variants with all constructors
    int attemptsCount = 20;
    int maxSize = 50;
//    int maxSize = 1000;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      LongArray keys = generateRandomLongArray(maxSize, SORTED_UNIQUE);
      IntArray values = generateRandomIntArray(keys.size(), UNORDERED);
      assert keys.size() == values.size();

      for (T map : createMapsFromLists(keys, values)) {
        checkMap(map, keys, values);
      }
    }
  }

  public void testIterators() {
    // assumed that method createMapsFromLists returns variants with all constructors
    int attemptsCount = 10;
    int maxSize = 50;
//    int maxSize = 1000;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      LongArray keys = generateRandomLongArray(maxSize, SORTED_UNIQUE);
      IntArray values = generateRandomIntArray(keys.size(), UNORDERED);

      for (T map : createMapsFromLists(keys, values)) {
        LongArray actualKeys = LongCollections.collectIterables(map.size(), map.keysIterator());
        IntArray actualValues = IntCollections.collectIterable(map.size(), map.valuesIterator());
        CHECK.unordered(actualKeys, keys);
        CHECK.unordered(actualValues, values);

        actualKeys.clear();
        actualValues.clear();
        for (LongIntIterator iter : map) {
          actualKeys.add(iter.left());
          actualValues.add(iter.right());
        }
        CHECK.unordered(actualKeys, keys);
        CHECK.unordered(actualValues, values);
      }

    }
  }

  public void testIteratorConcurrentModificationException2() {
    // assumed that method createMapsFromLists returns variants with all constructors
    int attemptsCount = 10;
    int maxSize = 50;
//    int maxSize = 1000;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      LongArray keys = generateRandomLongArray(maxSize, SORTED_UNIQUE);
      IntArray values = generateRandomIntArray(keys.size(), UNORDERED);

      for (T map : createMapsFromLists(keys, values)) {
        int keyForAdd = 0;
        while (map.containsKey(keyForAdd)) keyForAdd = myRand.nextInt();

        LongIterator keysIt = map.keysIterator();
        IntIterator valuesIt = map.valuesIterator();
        LongIntIterator it = map.iterator();

        map.add(keyForAdd, keyForAdd * keyForAdd);
        checkIteratorThrowsCME(keysIt);
        checkIteratorThrowsCME(valuesIt);
        checkIteratorThrowsCME(it);
        map.remove(keyForAdd);
      }
    }
  }

  public void testValuesIterator() {
    IntArray expected = IntArray.create(1, 1, 2, 2, 3, 3);
    map.putAll(LongArray.create(0, 2, 4, 6, 8, 10), expected);
    IntArray actual = IntCollections.collectIterable(expected.size(), map.valuesIterator());
    actual.sort();
    assertEquals(expected, actual);

    int size = 20, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      map.clear();
      LongArray keys = generateRandomLongArray(size, SortedStatus.SORTED_UNIQUE, 100, 200);
      keys.shuffle(myRand);
      expected = new IntArray(size);
      for (int i = 0; i < keys.size(); i++) {
        long key = keys.get(i);
        expected.add((int) (key * key));
      }
      map.putAll(keys, expected);
      actual = new IntArray(map.valuesIterator());
      actual.sort();
      expected.sort();
      CHECK.order(actual.toNativeArray(), expected.toNativeArray());
    }
  }

  public void testPut() {
    int size = 100, attempts = 10, maxVal = 1000;
    for (int attempt = 0; attempt < attempts; attempt++) {
      map.clear();
      LongArray keys = generateRandomLongArray(size, SortedStatus.SORTED_UNIQUE, maxVal);
      LongArray shuffledKeys = LongCollections.collectLists(keys, keys);
      shuffledKeys.shuffle(myRand);

      for (int i = 0; i < shuffledKeys.size(); i++) {
        long key = shuffledKeys.get(i);
        long prevValue = map.get(key);
        assertEquals(prevValue, map.put(key, (int) (-key * key)));
      }
    }
  }

  public void checkMap(T map, LongList keys, IntList values) {
    assertEquals(keys.size(), map.size());
    assertEquals(keys.isEmpty(), map.isEmpty());
    for (int i = 0; i < keys.size(); i++) {
      assertEquals(values.get(i), map.get(keys.get(i)));
    }
  }

  private static IntList getSqrValues(final LongList keys) {
    return new AbstractIntList() {
      @Override
      public int size() {
        return keys.size();
      }

      @Override
      public int get(int index) throws NoSuchElementException {
        return (int) (keys.get(index) * keys.get(index));
      }
    };
  }

  public void testAddAllRemoveAllSimple() {
    LongArray keys = new LongArray(LongProgression.arithmetic(1, 10, 2));
    IntList values = getSqrValues(keys);
    map.putAll(keys, values);
    checkMap(map, keys, values);

    keys.addAll(LongProgression.arithmetic(0, 10, 2));
    values = getSqrValues(keys);
    map.putAll(keys.toNativeArray(), values.toNativeArray());

    checkMap(map, keys, values);

    map.removeAll(keys);
    assertTrue(map.isEmpty());

    keys = generateRandomLongArray(10, UNORDERED);
    values = getSqrValues(keys);
    map.putAll(LongIntIterators.pair(keys.iterator(), values.iterator()));
    checkMap(map, keys, values);
  }

  public void testRemoveKey() {
    int size = 400, attempts = 10, maxVal = 1000;
    for (int attempt = 0; attempt < attempts; attempt++) {
      LongArray keys = generateRandomLongArray(size, SortedStatus.UNORDERED, maxVal);
      IntArray values = new IntArray(keys.size());
      for (int i = 0; i < keys.size(); i++) {
        long key = keys.get(i);
        values.add((int) (-key * key));
      }
      map.putAll(keys, values);

      keys.sortUnique();
      for (int i = 0; i < maxVal; i++) {
        if (keys.binarySearch(i) >= 0) {
          assertEquals(-i * i, map.remove(i));
        } else {
          assertEquals(0, map.remove(i));
        }
      }
    }
  }

  public void testToTableString() {
    long[][] keysArray = {{}, {-1}, {0, 1, 2, 3}, {10, 20, 30}, {1, 3, 5, 7, 11, 13}};
    for (long[] keys : keysArray) {
      IntArray values = new IntArray(keys.length);
      for (long key : keys) {
        values.add((int) (key * key));
      }
      for (T map : createMapsFromLists(new LongArray(keys), values)) {
        String output;
        if (map instanceof AbstractWritableLongIntMap) {
          output = ((AbstractWritableLongIntMap) map).toTableString();
        } else if (map instanceof WritableLongIntMapFromLongObjMap) {
          output = ((WritableLongIntMapFromLongObjMap) map).toTableString();
        } else {
          continue;
        }
        int idx = 0, commasCount = 0;
        for (; idx < output.length() && output.charAt(idx) != '\n'; idx++) {
          if (output.charAt(idx) == ',') commasCount++;
        }
        assertEquals(Math.max(0, keys.length - 1), commasCount);
        assertFalse(idx == output.length());
        idx++;
        for (; idx < output.length() && output.charAt(idx) != '\n'; idx++) {
          if (output.charAt(idx) == ',') commasCount--;
        }
        assertEquals(0, commasCount);
      }
    }

  }

  public void checkPutIfAbsent(LongList keys) {
    map.clear();
    for (int i = 0; i < keys.size(); i++) {
      long key = keys.get(i);
      int idx = keys.subList(0, i).indexOf(key);
      if (idx >= 0) {
        assertEquals(idx, map.get(key));
        assertFalse(map.putIfAbsent(key, i));
        assertEquals(idx, map.get(key));
      } else {
        assertEquals(LongIntMap.DEFAULT_VALUE, map.get(key));
        assertTrue(map.putIfAbsent(key, i));
        assertEquals(i, map.get(key));
      }
    }
  }

  public void testPutIfAbsent() {
    checkPutIfAbsent(LongProgression.range(20));
    checkPutIfAbsent(concatLists(LongProgression.range(20), LongProgression.range(20)));
    checkPutIfAbsent(concatLists(LongProgression.range(20), LongProgression.range(19, 0, -1)));
    int attemptsCount = 10, size = 300, max = size * 3 / 2;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      checkPutIfAbsent(generateRandomLongArray(size, UNORDERED, max));
    }
  }

  public void testRemoveKeyValue() {
    int size = 20, maxKey = size * 2;
    LongList keys = LongProgression.range(0, maxKey, maxKey / size);
    IntList values = new IntArray(limit(cycle(0, 1, 2, 3), 20));
    for (T map0 : createMapsFromLists(keys, values)) {
      for (int i = 0; i < maxKey; i++) {
        assertFalse(map0.remove(i, -1));
        if (keys.contains(i)) {
          int val = (i/2) % 4;
          if (val == 0) {
            assertTrue(map0.remove(i, 0));
          } else {
            assertFalse(map0.remove(i, 0));
            assertTrue(map0.remove(i, val));
          }
        } else {
          assertFalse(map0.remove(i, 0));
          assertFalse(map0.remove(i, 1));
        }
      }
    }
  }

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
          expectedHash += IntegersUtils.hash(keys.get(i)) + IntegersUtils.hash(values.get(i));
        }

        for (T map0 : createMapsFromLists(keys, values)) {
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

  public void testEquals2() {
    int attemptsCount = 20;
    int maxVal = 10;
    int mapSize = 200;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      LongArray keys = generateRandomLongArray(mapSize, SORTED_UNIQUE);
      IntArray values = generateRandomIntArray(keys.size(), UNORDERED, maxVal);

      for (T map0 : createMapsFromLists(keys, values)) {
        assertTrue(map0.equals(map0));
        assertFalse(map0.equals(null));
        assertFalse(map0.equals(LongIntMap.EMPTY));
        assertTrue(map0.equals(map0));
        T expected = createMap();

        expected.putAllKeys(keys.subList(0, keys.size() - 1), values);
        expected.put(keys.getLast(0), values.getLast(0) != 0 ? 0 : 1);
        assertFalse(map0.equals(expected));

        for (T map1 : createMapsFromLists(keys, values)) {
          assertEquals(map1, map0);
        }
      }
    }
  }
}
