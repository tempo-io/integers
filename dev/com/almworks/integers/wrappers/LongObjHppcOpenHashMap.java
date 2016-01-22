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

// CODE GENERATED FROM com/almworks/integers/wrappers/PObjHppcOpenHashMap.tpl


package com.almworks.integers.wrappers;

import com.almworks.integers.*;
import com.carrotsearch.hppc.*;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.almworks.integers.wrappers.LongHppcWrappers.cursorToLongIterator;
import static com.almworks.integers.wrappers.LongObjHppcWrappers.cursorToLongObjIterator;

public class LongObjHppcOpenHashMap<T> extends AbstractWritableLongObjMap<T> {
  protected final LongObjectOpenHashMap<T> myMap;

  public LongObjHppcOpenHashMap() {
    myMap = new LongObjectOpenHashMap<T>();
  }

  public LongObjHppcOpenHashMap(int initialCapacity) {
    myMap = new LongObjectOpenHashMap<T>(initialCapacity);
  }

  public LongObjHppcOpenHashMap(int initialCapacity, float loadFactor) {
    myMap = new LongObjectOpenHashMap<T>(initialCapacity, loadFactor);
  }

  public static <T> LongObjHppcOpenHashMap<T> createFrom(LongIterable keys, Iterable<T> values) {
    int keysSize = (keys instanceof LongSizedIterable) ? ((LongSizedIterable) keys).size() : 0;
    int valuesSize = (values instanceof Collection) ? ((Collection) keys).size() : 0;

    float loadFactor = LongIntOpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(Math.max(keysSize, valuesSize) / loadFactor) + 1;
    LongObjHppcOpenHashMap map = new LongObjHppcOpenHashMap(initialCapacity);

    LongIterator keysIt = keys.iterator();
    Iterator valuesIt = values.iterator();
    map.putAll(LongObjIterators.pair(keysIt, valuesIt));

    if (keysIt.hasNext() || valuesIt.hasNext()) {
      throw new IllegalArgumentException("keys.size() != values.size()");
    }
    return map;
  }

  public static <T> LongObjHppcOpenHashMap<T> createFrom(long[] keys, T[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }
    int size = keys.length;
    float loadFactor = LongObjectOpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(size / loadFactor) + 1;
    LongObjHppcOpenHashMap map = new LongObjHppcOpenHashMap<T>(initialCapacity, loadFactor);
    map.putAll(keys, values);
    return map;
  }

  /**
   * Creates new hashmap with the specified load factor
   * that is garanteed to not invoke {@code resize} after adding {@code count} elements
   * @return new hashmap with the specified capacity dependent on {@code count} and {@code loadFactor}
   */
  public static <T> LongObjHppcOpenHashMap<T> createForAdd(int count, float loadFactor) {
    int initialCapacity = (int)(count / loadFactor) + 1;
    return new LongObjHppcOpenHashMap<T>(initialCapacity, loadFactor);
  }

  /**
   * Creates new hashmap with default load factor
   * @see #createForAdd(int, float)
   */
  public static <T> LongObjHppcOpenHashMap<T> createForAdd(int count) {
    return createForAdd(count, LongObjectOpenHashMap.DEFAULT_LOAD_FACTOR);
  }

  @Override
  public boolean containsKey(long key) {
    return myMap.containsKey(key);
  }

  @Override
  public int size() {
    return myMap.size();
  }

  @NotNull
  public LongObjIterator<T> iterator() {
	return new LongObjFailFastIterator<T>(cursorToLongObjIterator(myMap.iterator())) {
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

  public Iterator<T> valuesIterator() {
    final Iterator<ObjectCursor<T>> it = myMap.values().iterator();
    return new Iterator<T>() {
      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public T next() {
        return it.next().value;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public Collection<T> values() {
    final ObjectContainer<T> valuesContainer = myMap.values();
    return new AbstractCollection<T>() {
      @NotNull
      @Override
      public Iterator<T> iterator() {
        return valuesIterator();
      }

      @Override
      public int size() {
        return valuesContainer.size();
      }

      @Override
      public boolean isEmpty() {
        return valuesContainer.isEmpty();
      }

      @NotNull
      @Override
      public Object[] toArray() {
        return valuesContainer.toArray();
      }

      @Override
      public boolean contains(Object o) {
        return valuesContainer.contains((T) o);
      }
    };
  }

  @Override
  public T get(long key) {
    return myMap.get(key);
  }

  @Override
  public void clear() {
    myMap.clear();
  }

  @Override
  protected T putImpl(long key, T value) {
    return myMap.put(key, value);
  }

  /**
   * Returns the last value saved in a call to {@link #containsKey}.
   * @see #containsKey
   */
  public T lget() {
    return myMap.lget();
  }

  /**
   * Sets the value corresponding to the key saved in the last
   * call to {@link #containsKey}, if and only if the key exists
   * in the map already.
   *
   * @see #containsKey
   * @return Returns the previous value stored under the given key
   * or {@code null} if there was no mapping for the last used key
   */
  public T lset(T value) {
    return myMap.lset(value);
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
  protected T removeImpl(long key) {
    return myMap.remove(key);
  }

  @Override
  public boolean remove(long key, T value) {
    modified();
    if (!containsKey(key)) return false;
    if (!(myMap.lget() == value)) return false;
    myMap.remove(key);
    return true;
  }

}
