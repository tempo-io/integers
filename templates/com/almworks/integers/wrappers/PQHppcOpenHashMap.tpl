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

package com.almworks.integers.wrappers;

import com.almworks.integers.*;
import com.carrotsearch.hppc.#E##F#OpenHashMap;

import static com.almworks.integers.#F#Collections.sizeOfIterable;
import static com.almworks.integers.wrappers.#E##F#HppcWrappers.cursorTo#E##F#Iterator;
import static com.almworks.integers.wrappers.#E#HppcWrappers.cursorTo#E#Iterator;
import static com.almworks.integers.wrappers.#F#HppcWrappers.cursorTo#F#Iterator;

public class #E##F#HppcOpenHashMap extends AbstractWritable#E##F#Map {
  protected final #E##F#OpenHashMap myMap;

  public #E##F#HppcOpenHashMap() {
    myMap = new #E##F#OpenHashMap();
  }

  public #E##F#HppcOpenHashMap(int initicalCapacity) {
    myMap = new #E##F#OpenHashMap(initicalCapacity);
  }

  public #E##F#HppcOpenHashMap(int initialCapacity, float loadFactor) {
    myMap = new #E##F#OpenHashMap(initialCapacity, loadFactor);
  }

  public static #E##F#HppcOpenHashMap createFrom(#E#Iterable keys, #F#Iterable values) {
    int keysSize = (keys instanceof #E#SizedIterable) ? ((#E#SizedIterable) keys).size() : 0;
    int valuesSize = sizeOfIterable(values, 0);
    if (keysSize * valuesSize != 0) {
      if (keysSize != valuesSize) {
        throw new IllegalArgumentException("keys.size() != values.size()");
      }
    } else {
      keysSize = Math.max(keysSize, valuesSize);
    }

    float loadFactor = #E##F#OpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(keysSize / loadFactor) + 1;
    #E##F#HppcOpenHashMap map = new #E##F#HppcOpenHashMap(initialCapacity);

    #E#Iterator keysIt = keys.iterator();
    #F#Iterator valuesIt = values.iterator();
    map.putAll(#E##F#Iterators.pair(keysIt, valuesIt));

    if (keysIt.hasNext() || valuesIt.hasNext()) {
      throw new IllegalArgumentException("keys.size() != values.size()");
    }
    return map;
  }

  public static #E##F#HppcOpenHashMap createFrom(#e#[] keys, #f#[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }
    int size = keys.length;
    float loadFactor = #E##F#OpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(size / loadFactor) + 1;
    #E##F#HppcOpenHashMap map = new #E##F#HppcOpenHashMap(initialCapacity, loadFactor);
    map.putAll(keys, values);
    return map;
  }

  /**
   * Creates new hashmap with the specified load factor
   * that is garanteed to not invoke {@code resize} after adding {@code count} elements
   * @return new hashmap with the specified capacity dependent on {@code count} and {@code loadFactor}
   */
  public static #E##F#HppcOpenHashMap createForAdd(int count, float loadFactor) {
    int initialCapacity = (int)(count / loadFactor) + 1;
    return new #E##F#HppcOpenHashMap(initialCapacity, loadFactor);
  }

  /**
   * Creates new hashmap with default load factor
   * @see #createForAdd(int, float)
   */
  public static #E##F#HppcOpenHashMap createForAdd(int count) {
    return createForAdd(count, #E##F#OpenHashMap.DEFAULT_LOAD_FACTOR);
  }

  @Override
  public boolean containsKey(#e# key) {
    return myMap.containsKey(key);
  }

  @Override
  public int size() {
    return myMap.size();
  }

  public #E##F#Iterator iterator() {
    return failFast(cursorTo#E##F#Iterator(myMap.iterator()));
  }

  public #E#Iterator keysIterator() {
    return new #E#FailFastIterator(cursorTo#E#Iterator(myMap.keys().iterator())) {
      @Override
      protected int getCurrentModCount() {
        return 0;
      }
    };
  }

  public #F#Iterator valuesIterator() {
    return new #F#FailFastIterator(cursorTo#F#Iterator(myMap.values().iterator())) {
      @Override
      protected int getCurrentModCount() {
        return 0;
      }
    };
  }

  @Override
  public #f# get(#e# key) {
    return myMap.get(key);
  }

  @Override
  public void clear() {
    myMap.clear();
  }

  @Override
  protected #f# putImpl(#e# key, #f# value) {
    return myMap.put(key, value);
  }

  /**
   * <a href="http://trove4j.sourceforge.net">Trove</a>-inspired API method. An equivalent
   * of the following code:
   * <pre>
   *  if (containsKey(key))
   *  {
   *      #f# v = (#f#) (lget() + additionValue);
   *      lset(v);
   *      return v;
   *  }
   *  else
   *  {
   *     put(key, putValue);
   *     return putValue;
   *  }
   * </pre>
   *
   * @param key The key of the value to adjust.
   * @param putValue The value to put if <code>key</code> does not exist.
   * @param additionValue The value to add to the existing value if <code>key</code> exists.
   * @return Returns the current value associated with <code>key</code> (after changes).
   */
  public #f# putOrAdd(#e# key, #f# putValue, #f# additionValue) {
    modified();
    return myMap.putOrAdd(key, putValue, additionValue);
  }

  public #f# addTo(#e# key, #f# additionValue) {
    modified();
    return putOrAdd(key, additionValue, additionValue);
  }

  /**
   * Returns the last value saved in a call to {@link #containsKey}.
   * @see #containsKey
   */
  public #f# lget() {
    return myMap.lget();
  }

  /**
   * Sets the value corresponding to the key saved in the last
   * call to {@link #containsKey}, if and only if the key exists
   * in the map already.
   *
   * @see #containsKey
   * @return Returns the previous value stored under the given key.
   */
  public #f# lset(#f# key) {
    return myMap.lset(key);
  }

  /**
   * @return Returns the slot of the last key looked up in a call to {@link #containsKey} if
   * it returned <code>true</code>.
   *
   * @see #containsKey
   */
  public int lslot() {
    return myMap.lslot();
  }

  @Override
  protected #f# removeImpl(#e# key) {
    return myMap.remove(key);
  }
}
