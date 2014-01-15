package com.almworks.integers;

import com.almworks.integers.util.IntSizedIterable;
import com.almworks.integers.util.LongOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntIterators.range;

public abstract class WritableIntLongMapChecker extends IntegersFixture {
  protected abstract WritableIntLongMap createMap();

  protected abstract WritableIntLongMap createMapWithCapacity(int capacity);

  protected abstract WritableIntLongMap[] createMapFromSortedList(IntList keys, LongList values);

  protected WritableIntLongMap map;

  public void setUp() throws Exception {
    super.setUp();
    map = createMap();
  }

  public void testSimple() {
    for (int i = 0; i < 10; i++) {
      map.put(i, i * i);
    }
    IntArray expected = new IntArray(range(0, 10));
    assertEquals(10, map.size());
    assertTrue(map.containsKeys(expected));
    IntArray actual = new IntArray(10);
    for (IntIterator keysIt : map.keysIterator()) {
      actual.add(keysIt.value());
    }
    actual.sort();
    CHECK.order(actual.iterator(), expected.iterator());

    for (IntIterator it : expected) {
      int value = it.value();
      assertEquals(value * value, map.get(value));
    }
  }

  public void testSize() {
    // sets
  }

  public void testIsEmpty() {
    // sets
  }

  public void testGet() {
    int size = 20, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      IntArray keys = generateRandomIntArray(size, SortedStatus.SORTED_UNIQUE);
      LongArray values = generateRandomLongArray( keys.size(), IntegersFixture.SortedStatus.UNORDERED);

//      map.putAll(keys, values);
    }
  }

  public void testClear() {
    // sets
  }

  public void testIterator() {
  }

  public void testKeysIterator() {
    // sets
  }

  public void testValuesIterator() {
    LongArray expected = LongArray.create(1, 1, 2, 2, 3, 3);
    map.putAll(IntArray.create(0, 2, 4, 6, 8, 10), expected);
    LongArray actual = LongCollections.collectIterables(expected.size(), map.valuesIterator());
    actual.sort();
    assertEquals(expected, actual);

    int size = 20, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      map.clear();
      IntArray keys = generateRandomIntArray(size, SortedStatus.SORTED_UNIQUE, 100, 200);
      keys.shuffle(RAND);
      expected = new LongArray(size);
      for (int i = 0; i < keys.size(); i++) {
        int key = keys.get(i);
        expected.add(key * key);
      }
      map.putAll(keys, expected);
      actual = new LongArray(map.valuesIterator());
      actual.sort();
      expected.sort();
      CHECK.order(expected, actual);
    }
  }

  public void testContainsKeys() {
    // sets
  }

  public void testPut() {
    int size = 100, attempts = 10, maxVal = 1000000;
    for (int attempt = 0; attempt < attempts; attempt++) {
      map.clear();
      IntArray keys = generateRandomIntArray(size, SortedStatus.SORTED_UNIQUE, maxVal);
      IntArray shuffledKeys = IntCollections.collectLists(keys, keys);
      shuffledKeys.shuffle(RAND);

      for (int i = 0; i < shuffledKeys.size(); i++) {
        int key = shuffledKeys.get(i);
        long prevValue = map.get(key);
        assertEquals(prevValue, map.put(key, -key * key));
      }
    }
  }

  public void testPutIfAbsent() {
    // sets - include
  }

  public void testAdd() {
    // sets - add
  }

  public void testRemoveKey() {
    int size = 400, attempts = 10, maxVal = 1000;
    for (int attempt = 0; attempt < attempts; attempt++) {
      IntArray keys = generateRandomIntArray(size, SortedStatus.UNORDERED, maxVal);
      LongArray values = new LongArray(keys.size());
      for (int i = 0; i < keys.size(); i++) {
        int key = keys.get(i);
        values.add(-key * key);
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

  public void testRemoveKeyValue() {
    // sets -
  }

  public void testPutAll() {
    // sets
  }

  public void testRemoveAll() {
    // sets
  }
}
