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

// CODE GENERATED FROM com/almworks/integers/PObjMap.tpl




package com.almworks.integers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Serves as a map with int keys.
 *
 * @param <E> element type
 */
public class IntObjMap<E> implements Iterable<IntObjMap.Entry<E>> {
  public static class Entry<V> {
    private final int myKey;
    private final V myValue;

    public Entry(int aKey, V aValue) {
      myKey = aKey;
      myValue = aValue;
    }

    public int getKey() {
      return myKey;
    }

    public V getValue() {
      return myValue;
    }

    @Override
    public String toString() {
      return myKey + "->" + myValue;
    }
  }

  private final IntArray myKeys = new IntArray();
  private final List<E> myValues = IntegersUtils.arrayList();
  private int myModCount = 0;

  public static <E> IntObjMap<E> create() {
    return new IntObjMap();
  }

  /**
   * Puts the element for the specified key.
   *
   * @return the previously stored value if the key was already present in the map. Otherwise, null.
   */
  @Nullable
  public E put(int key, @NonNls @Nullable E value) {
    mod();
    int pos = myKeys.binarySearch(key);
    if (pos >= 0) {
      return myValues.set(pos, value);
    } else {
      int insPos = -pos - 1;
      myKeys.insert(insPos, key);
      myValues.add(insPos, value);
      return null;
    }
  }

  /**
   * Returns null if the element is not in the map.
   */
  @Nullable
  public E get(int key) {
    int pos = myKeys.binarySearch(key);
    if (pos >= 0) return myValues.get(pos);
    return null;
  }

  public boolean containsKey(int key) {
    return myKeys.binarySearch(key) >= 0;
  }

  /**
   * Returns the leftmost int map iterator such that key for the entry returned by the call to {@link IntMapIterator#next} is greater than the specified key.
   * If there are no positions with keys greater than the specified one, a past-the-end iterator is returned.
   */
  @Nullable
  public IntMapIterator find(int key) {
    int pos = myKeys.binarySearch(key);
    if (pos < 0) {
      pos = -pos - 1;
    }
    return new IntMapIterator(pos);
  }

  /**
   * Pairs are sorted by their integer value (the key).
   */
  public List<Entry<E>> toList() {
    List<Entry<E>> ret = IntegersUtils.arrayList();
    for (int i = 0, iend = myKeys.size(); i < iend; ++i) {
      ret.add(new Entry<E>(myKeys.get(i), myValues.get(i)));
    }
    return ret;
  }

  /**
   * @return an unmodifiable list of values sorted by their keys in ascending order.
   */
  public List<E> getValues() {
    //noinspection ReturnOfCollectionOrArrayField
    return Collections.unmodifiableList(myValues);
  }

  /**
   * Removes mapping for the specified key if it is present.
   * @return previously mapped object
   */
  public E remove(int key) {
    mod();
    int pos = myKeys.binarySearch(key);
    if (pos >= 0) {
      myKeys.removeAt(pos);
      return myValues.remove(pos);
    } else {
      return null;
    }
  }

  private void mod() {
    ++myModCount;
  }

  @Override
  public IntMapIterator iterator() {
    return new IntMapIterator();
  }

  /**
   * Returns a unique sorted {@link IntList} view of the keys contained in this map.
   * The collection is backed by the map, so changes to the map are
   * reflected in the keySet. Unlike {@link java.util.Map#keySet()},
   * {@link IntObjMap#keySet()} returns read-only list.
   *
   * @return IntList of the values contained in this map
   * */
  public IntList keySet() {
    return myKeys;
  }

  public int size() {
    assert myKeys.size() == myValues.size();
    return myKeys.size();
  }

  @Override
  public String toString() {
    return "IntObjMap " + toList().toString();
  }

  public class IntMapIterator implements Iterator<Entry<E>> {
    private int myKeyPos;
    private int myExpectedModCount;
    private int myLastRetPos = -1;

    public IntMapIterator() {
      this(0);
    }

    public IntMapIterator(int keyPos) {
      myKeyPos = keyPos;
      myExpectedModCount = myModCount;
    }

    @Override
    public boolean hasNext() {
      return myKeyPos < myKeys.size();
    }

    @Override
    public Entry<E> next() {
      checkMod();
      try {
        int key = myKeys.get(myKeyPos);
        E value = myValues.get(myKeyPos);
        myLastRetPos = myKeyPos;
        ++myKeyPos;
        return new Entry<E>(key, value);
      } catch(IndexOutOfBoundsException ex) {
        checkMod();
        throw new NoSuchElementException();
      }
    }

    @Override
    public void remove() {
      if(myLastRetPos == -1) {
        throw new IllegalStateException();
      }
      checkMod();

      try {
        myKeys.removeAt(myLastRetPos);
        myValues.remove(myLastRetPos);
        if(myLastRetPos < myKeyPos) {
          --myKeyPos;
        }
        myLastRetPos = -1;
        myExpectedModCount = myModCount;
      } catch(IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }

    }

    public boolean hasPrevious() {
      return myKeyPos > 0;
    }

    public Entry<E> previous() {
      checkMod();
      try {
        int key = myKeys.get(myKeyPos - 1);
        E value = myValues.get(myKeyPos - 1);
        myLastRetPos = myKeyPos - 1;
        --myKeyPos;
        return new Entry<E>(key, value);
      } catch(IndexOutOfBoundsException ex) {
        checkMod();
        throw new NoSuchElementException();
      }
    }

    private void checkMod() {
      if(myExpectedModCount != myModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
}
