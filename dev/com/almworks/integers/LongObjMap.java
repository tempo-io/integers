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

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Serves as a map with long keys.
 *
 * @param <E> element type
 */
public class LongObjMap<E> extends AbstractWritableLongObjMap<E> {
  private final LongArray myKeys = new LongArray();
  private final List<E> myValues = IntegersUtils.arrayList();

  public static <E> LongObjMap<E> create() {
    return new LongObjMap();
  }

  @Override
  protected E putImpl(long key, E value) {
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
  public E get(long key) {
    int pos = myKeys.binarySearch(key);
    if (pos >= 0) return myValues.get(pos);
    return null;
  }

  public boolean containsKey(long key) {
    return myKeys.binarySearch(key) >= 0;
  }

  /**
   * Returns the leftmost long map iterator such that key for the entry returned by the call to {@link LongMapIterator#next} is greater than the specified key.
   * If there are no positions with keys greater than the specified one, a past-the-end iterator is returned.
   */
  @Nullable
  public LongMapIterator find(long key) {
    int pos = myKeys.binarySearch(key);
    if (pos < 0) {
      pos = -pos - 1;
    }
    return new LongMapIterator(pos);
  }

  /**
   * @return an unmodifiable list of values sorted by their keys in ascending order.
   */
  public List<E> getValues() {
    //noinspection ReturnOfCollectionOrArrayField
    return Collections.unmodifiableList(myValues);
  }

  @Override
  protected E removeImpl(long key) {
    int pos = myKeys.binarySearch(key);
    if (pos >= 0) {
      myKeys.removeAt(pos);
      return myValues.remove(pos);
    } else {
      return null;
    }
  }

  @Override
  public LongMapIterator iterator() {
    return new LongMapIterator();
  }

  @Override
  public LongListIterator keysIterator() {
    return myKeys.iterator();
  }

  @Override
  public Iterator valuesIterator() {
    return myValues.iterator();
  }

  /**
   * Returns a unique sorted {@link LongList} view of the keys contained in this map.
   * The collection is backed by the map, so changes to the map are
   * reflected in the keySet. Unlike {@link java.util.Map#keySet()},
   * {@link LongObjMap#keySet()} returns read-only list.
   *
   * @return LongList of the values contained in this map
   * */
  public LongList keySet() {
    return myKeys;
  }

  public int size() {
    assert myKeys.size() == myValues.size();
    return myKeys.size();
  }

  @Override
  public void clear() {
    modified();
    myKeys.clear();
    myValues.clear();
  }

  public class LongMapIterator extends AbstractLongObjIterator<E> {
    private int myFrom;
    private int myNext;
    private int myExpectedModCount;

    public LongMapIterator() {
      this(0);
    }

    public LongMapIterator(int keyPos) {
      if (keyPos < 0) throw new IllegalArgumentException("from < 0");
      myExpectedModCount = myModCount;
      myNext = myFrom = keyPos;
    }

    @Override
    public boolean hasNext() {
      return myNext < size();
    }

    @Override
    public boolean hasValue() {
      return myFrom < myNext;
    }

    public long left() {
      return myKeys.get(myNext - 1);
    }

    public E right() {
      return myValues.get(myNext - 1);
    }

    public LongObjIterator next() {
      checkMod();
      if (myNext >= size()) throw new NoSuchElementException();
      myNext++;
      return this;
    }

    @Override
    public void remove() {
      if (myNext == 0) throw new IllegalStateException();
      checkMod();

      try {
        myKeys.removeAt(myNext - 1);
        myValues.remove(myNext - 1);
        myExpectedModCount = myModCount;
        myNext--;
      } catch(IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }

    public boolean hasPrevious() {
      return myNext > myFrom + 1;
    }

    public LongObjIterator previous() {
      checkMod();
      if (!hasPrevious()) throw new NoSuchElementException();
      myNext--;
      return this;
    }

    private void checkMod() {
      if(myExpectedModCount != myModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
}
