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

import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class WritableLongIntMapFromLongObjMap implements WritableLongIntMap {

  private final WritableLongObjMap<Integer> myMap;

  public WritableLongIntMapFromLongObjMap(WritableLongObjMap<Integer> map) {
    this.myMap = map;
  }

  private int checkNull(Integer value) {
    return value == null ? 0 : value;
  }

  @Override
  public int get(long key) {
    return checkNull(myMap.get(key));
  }

  @Override
  public boolean containsKey(long key) {
    return myMap.containsKey(key);
  }

  @Override
  public boolean containsKeys(LongIterable keys) {
    return myMap.containsKeys(keys);
  }

  @Override
  public int size() {
    return myMap.size();
  }

  @Override
  public boolean isEmpty() {
    return myMap.isEmpty();
  }

  @NotNull
  @Override
  public LongIntIterator iterator() {
    final LongObjIterator<Integer> it = myMap.iterator();
    return new AbstractLongIntIterator() {
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return it.hasNext();
      }

      @Override
      public LongIntIterator next() {
        it.next();
        return this;
      }

      @Override
      public boolean hasValue() {
        return it.hasValue();
      }

      @Override
      public long left() throws NoSuchElementException {
        return it.left();
      }

      @Override
      public int right() throws NoSuchElementException {
        return it.right();
      }
    };
  }

  @Override
  public LongIterator keysIterator() {
    return myMap.keysIterator();
  }

  @Override
  public IntIterator valuesIterator() {
    final Iterator<Integer> it = myMap.valuesIterator();
    return new IntFindingIterator() {
      @Override
      protected boolean findNext() throws ConcurrentModificationException {
        if (!it.hasNext()) {
          return false;
        }
        myNext = it.next();
        return true;
      }
    };
  }

  @Override
  public void clear() {
    myMap.clear();
  }

  @Override
  public int put(long key, int value) {
    return checkNull(myMap.put(key, value));
  }

  @Override
  public WritableLongIntMap add(long key, int value) {
    put(key, value);
    return this;
  }

  @Override
  public boolean putIfAbsent(long key, int value) {
    return myMap.putIfAbsent(key, value);
  }

  @Override
  public int remove(long key) {
    return checkNull(myMap.remove(key));
  }

  @Override
  public boolean remove(long key, int value) {
    return myMap.remove(key, value);
  }

  @Override
  public void putAll(LongIntIterable entries) {
    final LongIntIterator it = entries.iterator();
    myMap.putAll(new LongObjFindingIterator<Integer>() {
      @Override
      protected boolean findNext() throws ConcurrentModificationException {
        if (!it.hasNext()) {
          return false;
        }
        myNextLeft = it.left();
        myNextRight = it.right();
        return true;
      }
    });
  }

  @Override
  public void putAll(LongSizedIterable keys, IntSizedIterable values) {
    putAllKeys(keys, values);
  }

  @Override
  public void putAll(long[] keys, int[] values) {
    Integer[] wrappedValues = new Integer[values.length];
    for (int i = 0; i < values.length; i++) {
      wrappedValues[i] = values[i];
    }
    myMap.putAll(keys, wrappedValues);
  }

  @Override
  public void putAllKeys(LongIterable keys, IntIterable values) {
    final IntIterator it = values.iterator();
    myMap.putAllKeys(keys, new Iterable<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public Integer next() {
            return it.nextValue();
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    });
  }


  @Override
  public void removeAll (long...keys) {
    myMap.removeAll(keys);
  }

  @Override
  public void removeAll (LongIterable keys) {
    myMap.removeAll(keys);
  }

  public boolean equals(Object o) {
    return myMap.equals(o);
  }

  public int hashCode() {
    return myMap.hashCode();
  }

  public String toTableString() {
    if (myMap instanceof AbstractWritableLongObjMap) {
      return ((AbstractWritableLongObjMap) myMap).toTableString();
    }
    throw new UnsupportedOperationException();
  }
}