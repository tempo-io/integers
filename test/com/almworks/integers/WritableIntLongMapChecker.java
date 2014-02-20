package com.almworks.integers;

import java.util.List;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntIterators.range;
import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;

public abstract class WritableIntLongMapChecker<T extends WritableIntLongMap> extends IntegersFixture {
  protected abstract T createMap();

  protected abstract T createMapWithCapacity(int capacity);

  protected abstract List<T> createMapFromLists(IntList keys, LongList values);

  protected T map;

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

  public void testConstructors() {
    // assumed that method createMapFromLists returns variants with all constructors
    int attemptsCount = 20;
    int maxSize = 50;
//    int maxSize = 1000;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      IntArray keys = generateRandomIntArray(maxSize, SORTED_UNIQUE);
      LongArray values = generateRandomLongArray(keys.size(), UNORDERED);
      assert keys.size() == values.size();

      for (T map : createMapFromLists(keys, values)) {
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
    // assumed that method createMapFromLists returns variants with all constructors
    int attemptsCount = 10;
    int maxSize = 50;
//    int maxSize = 1000;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      IntArray keys = generateRandomIntArray(maxSize, SORTED_UNIQUE);
      LongArray values = generateRandomLongArray(keys.size(), UNORDERED);

      for (T map : createMapFromLists(keys, values)) {
        int keyForAdd = 0;
        while (map.containsKey(keyForAdd)) keyForAdd = RAND.nextInt();

        IntIterator keysIt = map.keysIterator();
        LongIterator valuesIt = map.valuesIterator();
        IntLongIterator it = map.iterator();

        map.add(keyForAdd, keyForAdd * keyForAdd);
        LongIteratorSpecificationChecker.checkIteratorThrowsCME(keysIt);
        LongIteratorSpecificationChecker.checkIteratorThrowsCME(valuesIt);
        LongIteratorSpecificationChecker.checkIteratorThrowsCME(it);
        map.remove(keyForAdd);

        IntArray actualKeys = IntCollections.collectIterables(map.size(), map.keysIterator());
        LongArray actualValues = LongCollections.collectIterable(map.size(), map.valuesIterator());
        CHECK.unordered(actualKeys, keys);
        CHECK.unordered(actualValues, values);

        actualKeys.clear();
        actualValues.clear();
        for (IntLongIterator iter : map) {
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
    LongArray expected = LongArray.create(1, 1, 2, 2, 3, 3);
    map.putAll(IntArray.create(0, 2, 4, 6, 8, 10), expected);
    LongArray actual = LongCollections.collectIterable(expected.size(), map.valuesIterator());
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

  public void testPut() {
    int size = 100, attempts = 10, maxVal = 1000;
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

  public void checkMap(IntLongMap map, IntList keys, LongList values) {
    assertEquals(keys.size(), map.size());
    for (int i = 0; i < keys.size(); i++) {
      assertEquals(values.get(i), map.get(keys.get(i)));
    }
  }

  private static LongList getSqrValues(final IntList keys) {
    return new AbstractLongList() {
      @Override
      public int size() {
        return keys.size();
      }

      @Override
      public long get(int index) throws NoSuchElementException {
        return keys.get(index) * keys.get(index);
      }
    };
  }

  public void testAddAllRemoveAllSimple() {
    IntArray keys = new IntArray(IntProgression.arithmetic(1, 10, 2));
    LongList values = getSqrValues(keys);
    map.putAll(keys, values);
    checkMap(map, keys, values);

    keys.addAll(IntProgression.arithmetic(0, 10, 2));
    values = getSqrValues(keys);
    map.putAll(keys.toNativeArray(), values.toNativeArray());
    checkMap(map, keys, values);

    map.removeAll(keys);
    assertTrue(map.isEmpty());

    keys = generateRandomIntArray(10, UNORDERED);
    values = getSqrValues(keys);
    map.putAll(IntLongIterators.pair(keys.iterator(), values.iterator()));
    checkMap(map, keys, values);
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

  public void testToTableString() {
    int[][] keysArray = {{}, {-1}, {0, 1, 2, 3}, {10, 20, 30}, {1, 3, 5, 7, 11, 13}};
    for (int[] keys : keysArray) {
      LongArray values = new LongArray(keys.length);
      for (int key : keys) {
        values.add(key * key);
      }
      for (T map : createMapFromLists(new IntArray(keys), values)) {
        AbstractWritableIntLongMap map0 = (AbstractWritableIntLongMap) map;
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
