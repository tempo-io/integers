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

// CODE GENERATED FROM com/almworks/integers/wrappers/PQHppcOpenHashMap.tpl


package com.almworks.integers.wrappers;

import com.almworks.integers.*;
import com.carrotsearch.hppc.IntIntOpenHashMap;

import static com.almworks.integers.IntCollections.sizeOfIterable;
import static com.almworks.integers.wrappers.IntHppcWrappers.cursorToIntIterator;
import static com.almworks.integers.wrappers.IntIntHppcWrappers.cursorToIntIntIterator;

public class IntIntHppcOpenHashMap extends AbstractWritableIntIntMap {
  protected final IntIntOpenHashMap myMap;

  public IntIntHppcOpenHashMap() {
    myMap = new IntIntOpenHashMap();
  }

  public IntIntHppcOpenHashMap(int initicalCapacity) {
    myMap = new IntIntOpenHashMap(initicalCapacity);
  }

  public IntIntHppcOpenHashMap(int initialCapacity, float loadFactor) {
    myMap = new IntIntOpenHashMap(initialCapacity, loadFactor);
  }

  public static IntIntHppcOpenHashMap createFrom(IntIterable keys, IntIterable values) {
    int keysSize = (keys instanceof IntSizedIterable) ? ((IntSizedIterable) keys).size() : 0;
    int valuesSize = sizeOfIterable(values, 0);
    if (keysSize * valuesSize != 0) {
      if (keysSize != valuesSize) {
        throw new IllegalArgumentException("keys.size() != values.size()");
      }
    } else {
      keysSize = Math.max(keysSize, valuesSize);
    }

    float loadFactor = IntIntOpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(keysSize / loadFactor) + 1;
    IntIntHppcOpenHashMap map = new IntIntHppcOpenHashMap(initialCapacity);

    IntIterator keysIt = keys.iterator();
    IntIterator valuesIt = values.iterator();
    map.putAll(IntIntIterators.pair(keysIt, valuesIt));

    if (keysIt.hasNext() || valuesIt.hasNext()) {
      throw new IllegalArgumentException("keys.size() != values.size()");
    }
    return map;
  }

  public static IntIntHppcOpenHashMap createFrom(int[] keys, int[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }
    int size = keys.length;
    float loadFactor = IntIntOpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(size / loadFactor) + 1;
    IntIntHppcOpenHashMap map = new IntIntHppcOpenHashMap(initialCapacity, loadFactor);
    map.putAll(keys, values);
    return map;
  }

  /**
   * Creates new hashmap with the specified load factor
   * that is garanteed to not invoke {@code resize} after adding {@code count} elements
   * @return new hashmap with the specified capacity dependent on {@code count} and {@code loadFactor}
   */
  public static IntIntHppcOpenHashMap createForAdd(int count, float loadFactor) {
    int initialCapacity = (int)(count / loadFactor) + 1;
    return new IntIntHppcOpenHashMap(initialCapacity, loadFactor);
  }

  /**
   * Creates new hashmap with default load factor
   * @see #createForAdd(int, float)
   */
  public static IntIntHppcOpenHashMap createForAdd(int count) {
    return createForAdd(count, IntIntOpenHashMap.DEFAULT_LOAD_FACTOR);
  }

  @Override
  public boolean containsKey(int key) {
    return myMap.containsKey(key);
  }

  @Override
  public int size() {
    return myMap.size();
  }

  public IntIntIterator iterator() {
	return new IntIntFailFastIterator(cursorToIntIntIterator(myMap.iterator())) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public IntIterator keysIterator() {
    return new IntFailFastIterator(cursorToIntIterator(myMap.keys().iterator())) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public IntIterator valuesIterator() {
    return new IntFailFastIterator(cursorToIntIterator(myMap.values().iterator())) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  @Override
  public int get(int key) {
    return myMap.get(key);
  }

  @Override
  public void clear() {
    myMap.clear();
  }

  @Override
  protected int putImpl(int key, int value) {
    return myMap.put(key, value);
  }

  /**
   * <a href="http://trove4j.sourceforge.net">Trove</a>-inspired API method. An equivalent
   * of the following code:
   * <pre>
   *  if (containsKey(key))
   *  {
   *      int v = (int) (lget() + additionValue);
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
  public int putOrAdd(int key, int putValue, int additionValue) {
    modified();
    return myMap.putOrAdd(key, putValue, additionValue);
  }

  public int addTo(int key, int additionValue) {
    modified();
    return putOrAdd(key, additionValue, additionValue);
  }

  /**
   * Returns the last value saved in a call to {@link #containsKey}.
   * @see #containsKey
   */
  public int lget() {
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
  public int lset(int key) {
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
  protected int removeImpl(int key) {
    return myMap.remove(key);
  }

  @Override
  public boolean remove(int key, int value) {
    modified();
    if (!containsKey(key)) return false;
    if (!(myMap.lget() == value)) return false;
    myMap.remove(key);
    return true;
  }
}
