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

public interface Writable#E##F#Map extends #E##F#Map {

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
  #f# put(#e# key, #f# value);

  /**
   * Associates the specified value with the specified key in this map.
   * @return this
   */
  Writable#E##F#Map add(#e# key, #f# value);

  /**
   * Associates the specified value with the specified key only if the specified key has no current mapping.
   * @return {@code true} if the size of this map has changed. Otherwise {@code false}.
   */
  boolean putIfAbsent(#e# key, #f# value);

  /**
   * Removes value for the given key.
   * The default value is returned if the key does not exist in this map.
   * @return old value for the specified key if this map contained the key. Otherwise returns default value.
   */
  #f# remove(#e# key);

  /**
   * Removes the entry for the key only if the key is currently mapped to the given value.
   * @return {@code true} if the size of this map has changed. Otherwise {@code false}.
   */
  boolean remove(#e# key, #f# value);

  /**
   * Puts all keys from the specified iterable to this map, replacing the values
   * of existing keys, if such keys are present.
   * @see #putAll(#E#SizedIterable, #F#SizedIterable)
   */
  void putAll(#E##F#Iterable entries);

  /**
   * @throws IllegalArgumentException if {@code keys.size() != values.size()}
   * @see #putAll(#e#[], #f#[])
   */
  void putAll(#E#SizedIterable keys, #F#SizedIterable values);

  /**
   * Puts all keys from {@code keys} and {@code values} to this map,
   * replacing the values of existing keys, if such keys are present.
   * @throws IllegalArgumentException if {@code keys.length != values.length}
   */
  void putAll(#e#[] keys, #f#[] values);

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
  void putAllKeys(#E#Iterable keys, #F#Iterable values);

  void removeAll(#e#... keys);

  void removeAll(#E#Iterable keys);
}
