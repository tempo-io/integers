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
import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.LongIterators.range;
import static com.almworks.integers.WritableLongIntMapProjection.DEFAULT_CONTAINS_VALUE;

public abstract class WritableLongIntMapChecker<T extends WritableLongIntMap> extends WritableLongSetChecker<WritableLongIntMapProjection<T>> {
  protected abstract T createMap();

  protected abstract T createMapWithCapacity(int capacity);

  protected abstract List<T> createMapsFromLists(LongList keys, IntList values);

  @Override
  protected List<WritableLongIntMapProjection<T>> createSets(LongList sortedUniqueList) {
    IntList repeatContains = repeat(DEFAULT_CONTAINS_VALUE, sortedUniqueList.size());
    List<T> maps = createMapsFromLists(sortedUniqueList, repeatContains);
    List<WritableLongIntMapProjection<T>> sets = new ArrayList();
    for (T map : maps) {
      sets.add(new WritableLongIntMapProjection<T>(map));
    }
    return sets;
  }

  @Override
  protected WritableLongIntMapProjection<T> createSet() {
    return new WritableLongIntMapProjection<T>(createMap());
  }

  @Override
  protected WritableLongIntMapProjection<T> createSet(LongList sortedUniqueList) {
    T map = createMap();
    map.putAll(sortedUniqueList, repeat(DEFAULT_CONTAINS_VALUE, sortedUniqueList.size()));
    return new WritableLongIntMapProjection<T>(map);
  }

  @Override
  protected WritableLongIntMapProjection<T> createSetWithCapacity(int capacity) {
    return createSet();
  }

  protected T map;

  public void setUp() throws Exception {
    super.setUp();
    map = createMap();
  }

  public void testSimple() {
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

  public void testSize() {
    // sets
  }

  public void testIsEmpty() {
    // sets
  }

  public void testClear() {
    // sets
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
        int keyForAdd = 0;
        while (map.containsKey(keyForAdd)) keyForAdd = RAND.nextInt();

        LongIterator keysIt = map.keysIterator();
        IntIterator valuesIt = map.valuesIterator();
        LongIntIterator it = map.iterator();

        map.add(keyForAdd, keyForAdd * keyForAdd);
        LongIteratorSpecificationChecker.checkIteratorThrowsCME(keysIt);
        LongIteratorSpecificationChecker.checkIteratorThrowsCME(valuesIt);
        LongIteratorSpecificationChecker.checkIteratorThrowsCME(it);
        map.remove(keyForAdd);

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

  public void testKeysIterator() {
    // sets
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
      keys.shuffle(RAND);
      expected = new IntArray(size);
      for (int i = 0; i < keys.size(); i++) {
        long key = keys.get(i);
        expected.add((int) (key * key));
      }
      map.putAll(keys, expected);
      actual = new IntArray(map.valuesIterator());
      actual.sort();
      expected.sort();
      CHECK.order(asLongs(expected), asLongs(actual));
    }
  }

  public void testPut() {
    int size = 100, attempts = 10, maxVal = 1000;
    for (int attempt = 0; attempt < attempts; attempt++) {
      map.clear();
      LongArray keys = generateRandomLongArray(size, SortedStatus.SORTED_UNIQUE, maxVal);
      LongArray shuffledKeys = LongCollections.collectLists(keys, keys);
      shuffledKeys.shuffle(RAND);

      for (int i = 0; i < shuffledKeys.size(); i++) {
        long key = shuffledKeys.get(i);
        long prevValue = map.get(key);
        assertEquals(prevValue, map.put(key, (int) (-key * key)));
      }
    }
  }

  public void checkMap(T map, LongList keys, IntList values) {
    assertEquals(keys.size(), map.size());
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
        AbstractWritableLongIntMap map0 = (AbstractWritableLongIntMap) map;
        String output = map0.toTableString();
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

  public void testContainsKeys() {
    // sets
  }

  public void testPutIfAbsent() {
    // sets - include
  }

  public void testAdd() {
    // sets - add
  }

  public void testRemoveKeyValue() {
    // sets -
  }

  public void testRemoveAll() {
    // sets
  }
}
