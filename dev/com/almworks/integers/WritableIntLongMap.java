package com.almworks.integers;

import com.almworks.integers.util.IntSizedIterable;
import com.almworks.integers.util.LongSizedIterable;

public interface WritableIntLongMap extends IntLongMapI {

  /**
   * Removes all entries from this map.
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
   * @return {@code true} if the size of this map has changed. Otherwise {@code false}.
   */
  boolean putIfAbsent(int key, long value);

  /**
   * Removes value for the given key.
   * The default value is returned if the key does not exist in this map.
   * @return old value for the specified key if this map contained the key. Otherwise returns default value.
   */
  long remove(int key);

  /**
   * Removes the entry for the key only if the key is currently mapped to the given value.
   * @return {@code true} if the size of this map has changed. Otherwise {@code false}.
   */
  boolean remove(int key, long value);

  /**
   * Puts all keys from the specified iterable to this map, replacing the values
   * of existing keys, if such keys are present.
   * @see #putAll(com.almworks.integers.util.IntSizedIterable, com.almworks.integers.util.LongSizedIterable)
   */
  void putAll(IntLongIterable entries);

  /**
   * @throws IllegalArgumentException if {@code keys.size() != values.size()}
   * @see #putAll(int[], long[])
   */
  void putAll(IntSizedIterable keys, LongSizedIterable values);

  /**
   * Puts all keys from {@code keys} and {@code values} to this map,
   * replacing the values of existing keys, if such keys are present.
   * @throws IllegalArgumentException if {@code keys.length != values.length}
   */
  void putAll(int[] keys, long[] values);

  /**
   * Puts all {@code keys} in the map, taking as many {@code values} as needed;
   * when no values are available, uses default value.
   *
   * (1, 2, 3, 4), null -> ([1, 0], [2, 0], [3, 0], [4, 0])
   * (1, 2, 3, 4), () -> ([1, 0], [2, 0], [3, 0], [4, 0])
   * (1, 2, 3, 4), (1, 2) -> ([1, 1], [2, 2], [3, 0], [4, 0])
   * (1, 2, 3, 4), (1, 2, 3, 4) -> ([1, 1], [2, 2], [3, 3], [4, 4])
   * (1, 2, 3, 4), (1, 2, 3, 4, 6, 7, 8) -> ([1, 1], [2, 2], [3, 3], [4, 4])
   */
  void putAllKeys(IntIterable keys, LongIterable values);

  void removeAll(int... keys);

  void removeAll(IntIterable keys);
}