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
import com.carrotsearch.hppc.LongIntOpenHashMap;

import static com.almworks.integers.IntCollections.sizeOfIterable;
import static com.almworks.integers.wrappers.LongIntHppcWrappers.cursorToLongIntIterator;
import static com.almworks.integers.wrappers.LongHppcWrappers.cursorToLongIterator;
import static com.almworks.integers.wrappers.IntHppcWrappers.cursorToIntIterator;

public class LongIntHppcOpenHashMap extends AbstractWritableLongIntMap {
  protected final LongIntOpenHashMap myMap;

  public LongIntHppcOpenHashMap() {
    myMap = new LongIntOpenHashMap();
  }

  public LongIntHppcOpenHashMap(int initicalCapacity) {
    myMap = new LongIntOpenHashMap(initicalCapacity);
  }

  public LongIntHppcOpenHashMap(int initialCapacity, float loadFactor) {
    myMap = new LongIntOpenHashMap(initialCapacity, loadFactor);
  }

  public static LongIntHppcOpenHashMap createFrom(LongIterable keys, IntIterable values) {
    int keysSize = (keys instanceof LongSizedIterable) ? ((LongSizedIterable) keys).size() : 0;
    int valuesSize = sizeOfIterable(values, 0);
    if (keysSize * valuesSize != 0) {
      if (keysSize != valuesSize) {
        throw new IllegalArgumentException("keys.size() != values.size()");
      }
    } else {
      keysSize = Math.max(keysSize, valuesSize);
    }

    float loadFactor = LongIntOpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(keysSize / loadFactor) + 1;
    LongIntHppcOpenHashMap map = new LongIntHppcOpenHashMap(initialCapacity);

    LongIterator keysIt = keys.iterator();
    IntIterator valuesIt = values.iterator();
    map.putAll(LongIntIterators.pair(keysIt, valuesIt));

    if (keysIt.hasNext() || valuesIt.hasNext()) {
      throw new IllegalArgumentException("keys.size() != values.size()");
    }
    return map;
  }

  public static LongIntHppcOpenHashMap createFrom(long[] keys, int[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }
    int size = keys.length;
    float loadFactor = LongIntOpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(size / loadFactor) + 1;
    LongIntHppcOpenHashMap map = new LongIntHppcOpenHashMap(initialCapacity, loadFactor);
    map.putAll(keys, values);
    return map;
  }

  /**
   * Creates new hashmap with the specified load factor
   * that is garanteed to not invoke {@code resize} after adding {@code count} elements
   * @return new hashmap with the specified capacity dependent on {@code count} and {@code loadFactor}
   */
  public static LongIntHppcOpenHashMap createForAdd(int count, float loadFactor) {
    int initialCapacity = (int)(count / loadFactor) + 1;
    return new LongIntHppcOpenHashMap(initialCapacity, loadFactor);
  }

  /**
   * Creates new hashmap with default load factor
   * @see #createForAdd(int, float)
   */
  public static LongIntHppcOpenHashMap createForAdd(int count) {
    return createForAdd(count, LongIntOpenHashMap.DEFAULT_LOAD_FACTOR);
  }

  @Override
  public boolean containsKey(long key) {
    return myMap.containsKey(key);
  }

  @Override
  public int size() {
    return myMap.size();
  }

  public LongIntIterator iterator() {
	return new LongIntFailFastIterator(cursorToLongIntIterator(myMap.iterator())) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public LongIterator keysIterator() {
    return new LongFailFastIterator(cursorToLongIterator(myMap.keys().iterator())) {
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
  public int get(long key) {
    return myMap.get(key);
  }

  @Override
  public void clear() {
    myMap.clear();
  }

  @Override
  protected int putImpl(long key, int value) {
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
  public int putOrAdd(long key, int putValue, int additionValue) {
    modified();
    return myMap.putOrAdd(key, putValue, additionValue);
  }

  public int addTo(long key, int additionValue) {
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
  protected int removeImpl(long key) {
    return myMap.remove(key);
  }

  @Override
  public boolean remove(long key, int value) {
    modified();
    if (!containsKey(key)) return false;
    if (!(myMap.lget() == value)) return false;
    myMap.remove(key);
    return true;
  }

  @Override
  public int hashCode() {
    return myMap.hashCode();
  }
}
