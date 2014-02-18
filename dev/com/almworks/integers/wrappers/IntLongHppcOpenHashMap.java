package com.almworks.integers.wrappers;

import com.almworks.integers.*;
import com.almworks.integers.util.IntSizedIterable;
import com.almworks.integers.util.LongSizedIterable;
import com.carrotsearch.hppc.IntLongOpenHashMap;

import static com.almworks.integers.LongCollections.sizeOfIterable;
import static com.almworks.integers.wrappers.IntLongHppcWrappers.cursorToIntLongIterator;
import static com.almworks.integers.wrappers.IntHppcWrappers.intCursorToIterator;
import static com.almworks.integers.wrappers.LongHppcWrappers.cursorToLongIterator;

public class IntLongHppcOpenHashMap extends AbstractWritableIntLongMap {
  private final IntLongOpenHashMap myMap;

  public IntLongHppcOpenHashMap() {
    myMap = new IntLongOpenHashMap();
  }

  public IntLongHppcOpenHashMap(int initicalCapacity) {
    myMap = new IntLongOpenHashMap(initicalCapacity);
  }

  public IntLongHppcOpenHashMap(int initialCapacity, float loadFactor) {
    myMap = new IntLongOpenHashMap(initialCapacity, loadFactor);
  }

  public static IntLongHppcOpenHashMap createFrom(IntIterable keys, LongIterable values) {
    int keysSize = (keys instanceof IntSizedIterable) ? ((IntSizedIterable) keys).size() : 0;
    int valuesSize = sizeOfIterable(values, 0);
    if (keysSize * valuesSize != 0) {
      if (keysSize != valuesSize) {
        throw new IllegalArgumentException("keys.size() != values.size()");
      }
    } else {
      keysSize = Math.max(keysSize, valuesSize);
    }

    float loadFactor = IntLongOpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(keysSize / loadFactor) + 1;
    IntLongHppcOpenHashMap map = new IntLongHppcOpenHashMap(initialCapacity);

    IntIterator keysIt = keys.iterator();
    LongIterator valuesIt = values.iterator();
    map.putAll(IntLongIterators.pair(keysIt, valuesIt));

    if (keysIt.hasNext() || valuesIt.hasNext()) {
      throw new IllegalArgumentException("keys.size() != values.size()");
    }
    return map;
  }

  public static IntLongHppcOpenHashMap createFrom(int[] keys, long[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }
    int size = keys.length;
    float loadFactor = IntLongOpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(size / loadFactor) + 1;
    IntLongHppcOpenHashMap map = new IntLongHppcOpenHashMap(initialCapacity, loadFactor);
    map.putAll(keys, values);
    return map;
  }

  /**
   * Creates new hashmap with the specified load factor
   * that is garanteed to not invoke {@code resize} after adding {@code count} elements
   * @return new hashmap with the specified capacity dependent on {@code count} and {@code loadFactor}
   */
  public static IntLongHppcOpenHashMap createForAdd(int count, float loadFactor) {
    int initialCapacity = (int)(count / loadFactor) + 1;
    return new IntLongHppcOpenHashMap(initialCapacity, loadFactor);
  }

  /**
   * Creates new hashmap with default load factor
   * @see #createForAdd(int, float)
   */
  public static IntLongHppcOpenHashMap createForAdd(int count) {
    return createForAdd(count, IntLongOpenHashMap.DEFAULT_LOAD_FACTOR);
  }



  @Override
  public boolean containsKey(int key) {
    return myMap.containsKey(key);
  }

  @Override
  public int size() {
    return myMap.size();
  }

  public IntLongIterator iterator() {
    return failFast(cursorToIntLongIterator(myMap.iterator()));
  }

  public IntIterator keysIterator() {
    return failFast(intCursorToIterator(myMap.keys().iterator()));
  }

  public LongIterator valuesIterator() {
    return failFast(cursorToLongIterator(myMap.values().iterator()));
//    longIterator(myMap.values().iterator());
  }

  @Override
  public long get(int key) {
    return myMap.get(key);
  }

  @Override
  public void clear() {
    myMap.clear();
  }

  @Override
  protected long putImpl(int key, long value) {
    return myMap.put(key, value);
  }

  /**
   * <a href="http://trove4j.sourceforge.net">Trove</a>-inspired API method. An equivalent
   * of the following code:
   * <pre>
   *  if (containsKey(key))
   *  {
   *      long v = (long) (lget() + additionValue);
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
  public long putOrAdd(int key, long putValue, long additionValue) {
    modified();
    return myMap.putOrAdd(key, putValue, additionValue);
  }

  public long addTo(int key, long additionValue) {
    modified();
    return putOrAdd(key, additionValue, additionValue);
  }

  /**
   * Returns the last value saved in a call to {@link #containsKey}.
   * @see #containsKey
   */
  public long lget() {
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
  public long lset(long key) {
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
  protected long removeImpl(int key) {
    return myMap.remove(key);
  }
}
