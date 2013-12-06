package com.almworks.integers.wrappers;

import com.almworks.integers.*;
import com.almworks.integers.util.FindingIntIterator;
import com.almworks.integers.util.FindingIntLongIterator;
import com.carrotsearch.hppc.IntLongOpenHashMap;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.IntLongCursor;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HPPCIntLongOpenHashMap extends AbstractWritableIntLongMap {
  /**
   * Minimum capacity for the map.
   */
  public final static int MIN_CAPACITY = IntLongOpenHashMap.MIN_CAPACITY;

  /**
   * Default capacity.
   */
  public final static int DEFAULT_CAPACITY = IntLongOpenHashMap.DEFAULT_CAPACITY;

  /**
   * Default load factor.
   */
  public final static float DEFAULT_LOAD_FACTOR = IntLongOpenHashMap.DEFAULT_LOAD_FACTOR;

  /**
   * Default return value
   */
  public final static long DEFAULT_RETURN_VALUE= 0;


  IntLongOpenHashMap myMap;

  public HPPCIntLongOpenHashMap() {
    myMap = new IntLongOpenHashMap();
  }

  public HPPCIntLongOpenHashMap(int initicalCapacity) {
    myMap = new IntLongOpenHashMap(initicalCapacity);
  }

  public HPPCIntLongOpenHashMap(int initialCapacity, float loadFactor) {
    myMap = new IntLongOpenHashMap(initialCapacity, loadFactor);
  }

  public static HPPCIntLongOpenHashMap createFrom(IntIterable keys, LongIterable values) {
//    if (keys.size() != values.size()) {
//      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
//    }
//    int size = keys.size();
    float loadFactor = IntLongOpenHashMap.DEFAULT_LOAD_FACTOR;
//    int initialCapacity = (int)(size / loadFactor) + 1;
    HPPCIntLongOpenHashMap map = new HPPCIntLongOpenHashMap();
//    HPPCIntLongOpenHashMap map = new HPPCIntLongOpenHashMap(initialCapacity, loadFactor);
    map.putAll(keys, values);
    return map;
  }

  public static HPPCIntLongOpenHashMap createFrom(int[] keys, long[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }
    int size = keys.length;
    float loadFactor = IntLongOpenHashMap.DEFAULT_LOAD_FACTOR;
    int initialCapacity = (int)(size / loadFactor) + 1;
    HPPCIntLongOpenHashMap map = new HPPCIntLongOpenHashMap(initialCapacity, loadFactor);
//    map.putAll(keys, values);
    return map;
  }

  @Override
  public boolean containsKey(int key) {
    return myMap.containsKey(key);
  }

  @Override
  public int size() {
    return myMap.size();
  }

  @Override
  protected IntLongIterator iteratorImpl() {
    return new FindingIntLongIterator() {
      Iterator<IntLongCursor> cursor = myMap.iterator();
      @Override
      protected boolean findNext() {
        if (!cursor.hasNext()) return false;
        myCurrentLeft = cursor.next().key;
        myCurrentRight = cursor.next().value;
        return true;
      }
    };
  }

  @Override
  protected IntIterator keysIteratorImpl() {
    return IntLongIterators.leftProjection(iteratorImpl());
  }

  @Override
  protected LongIterator valuesIteratorImpl() {
    return IntLongIterators.rightProjection(iteratorImpl());
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

  @Override
  protected long removeImpl(int key) {
    return myMap.remove(key);
  }
}
