/*
 * Copyright 2010 ALM Works Ltd
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

package com.almworks.integers.util;

import com.almworks.integers.#E#Array;
import com.almworks.integers.IntegersUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Serves as a map with integer keys.
 *
 * @param <E> element type
 */
public class #E#ObjMap<E> implements Iterable<#E#ObjMap.Entry<E>> {
  public static class Entry<V> {
    private final #e# myKey;
    private final V myValue;

    public Entry(#e# aKey, V aValue) {
      myKey = aKey;
      myValue = aValue;
    }

    public #e# getKey() {
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

  private final #E#Array myKeys = new #E#Array();
  private final List<E> myValues = IntegersUtils.arrayList();
  private int myModCount = 0;

  public static <E> #E#ObjMap<E> create() {
    return new #E#ObjMap();
  }

  /**
   * Puts the element for the specified key.
   *
   * @return if the key was already present in the map, returns the previously stored value. Otherwise, returns null.
   */
  @Nullable
  public E put(#e# key, @NonNls @Nullable E value) {
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
  public E get(#e# key) {
    int pos = myKeys.binarySearch(key);
    if (pos >= 0) return myValues.get(pos);
    return null;
  }

  public boolean containsKey(#e# key) {
    return myKeys.binarySearch(key) >= 0;
  }

  /**
   * Returns the leftmost #e# map iterator such that key for the entry returned by the call to {@link #E#MapIterator#next} is greater than the specified key.
   * If there are no positions with keys greater than the specified one, a past-the-end iterator is returned.
   */
  @Nullable
  public #E#MapIterator find(#e# key) {
    int pos = myKeys.binarySearch(key);
    if (pos < 0) {
      pos = -pos - 1;
    }
    return new #E#MapIterator(pos);
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
   * Returns values sorted by their keys in ascending order.
   * @return
   */
  public Iterable<E> getValues() {
    //noinspection ReturnOfCollectionOrArrayField
    return Collections.unmodifiableList(myValues);
  }

  private void mod() {
    ++myModCount;
  }

  @Override
  public #E#MapIterator iterator() {
    return new #E#MapIterator();
  }

  @Override
  public String toString() {
    return "#E#ObjMap " + toList().toString();
  }

  public class #E#MapIterator implements Iterator<Entry<E>> {
    private int myKeyPos;
    private int myExpectedModCount;
    private int myLastRetPos = -1;

    public #E#MapIterator() {
      this(0);
    }

    public #E#MapIterator(int keyPos) {
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
        #e# key = myKeys.get(myKeyPos);
        E value = myValues.get(myKeyPos);
        myLastRetPos = myKeyPos;
        ++myKeyPos;
        return new Entry<E>(key, value);
      } catch(IndexOutOfBoundsException ex) {
        checkMod();
        throw new NoSuchElementException();
      }
    }

    /**
     * @see ListIterator#remove
     */
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
        #e# key = myKeys.get(myKeyPos - 1);
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
