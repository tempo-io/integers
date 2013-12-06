package com.almworks.integers;

public interface WritableIntLongMap extends IntLongMapI {
  /**
   * Removes all this map's entries.
   */
  void clear();

  /**
   * Associates the specified value with the specified key in this map.
   *
   * @return the previous value associated with {@code key}, or
   * default value if there was no mapping for {@code key}.
   */
  long put(int key, long value);

  /**
   * Associates the specified value with the specified key in this map.
   * @return this
   */
  WritableIntLongMap add(int key, long value);

  /**
   * Associates the specified value with the specified key only if the specified key has no current mapping.
   * @return {@code true} if size of map changed. Otherwise {@code false}.
   */
  boolean putIfAbsent(int key, long value);

  /**
   * Removes the entry for the specified key.
   * @return {@code true} if size of map changed. Otherwise {@code false}.
   */
  long remove(int key);

  /**
   * Removes the entry for a key only if currently mapped to a given value.
   * @return {@code true} if size of map changed. Otherwise {@code false}.
   */
  boolean remove(int key, long value);

  void putAll(IntLongIterable entries);

  void putAll(IntIterable keys, LongIterable values);

  void putAll(int[] keys, long[] values);

  void removeAll(int... keys);

  void removeAll(IntIterable keys);
}