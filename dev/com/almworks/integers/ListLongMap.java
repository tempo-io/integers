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

// CODE GENERATED FROM com/almworks/integers/ListPMap.tpl


package com.almworks.integers;

import java.util.NoSuchElementException;

public class ListLongMap {
  private final LongList myKeys;
  private final LongList myValues;

  private final ParallelLongList myMap;

  public ListLongMap() {
    this(new LongArray());
  }

  public ListLongMap(WritableLongList storage) {
    if (storage == null)
      throw new NullPointerException();
    if (!storage.isEmpty())
      throw new IllegalArgumentException();
    myMap = new ParallelLongList(storage, 2);
    myKeys = myMap.createListAccessor(0);
    myValues = myMap.createListAccessor(1);
  }

  /**
   * @return <b>index</b> of the pair
   */
  public int put(long key, long value) {
    int idx = findKey(key);
    if (idx < 0) {
      doInsert(-idx - 1, key, value);
    } else {
      setValue(idx, value);
    }
    return idx;
  }

  public void insertAt(int index, long key, long value) {
    assert index >= 0 && index <= size() : index + " " + this;
    assert
      index == 0 || myKeys.get(index - 1) < key : index + " " + key + " " + myKeys.get(index - 1);
    assert index == size() || myKeys.get(index) > key : index + " " + key + " " + myKeys.get(index);
    doInsert(index, key, value);
  }


  private void doInsert(int idx, long key, long value) {
    myMap.insert(idx, key, value);
  }

  public boolean containsKey(long key) {
    return findKey(key) >= 0;
  }

  public void remove(long key) {
    int idx = findKey(key);
    if (idx >= 0) {
      removeAt(idx);
    }
  }

  public void removeAt(int index) {
    myMap.removeAt(index);
  }

  public void removeRange(int from, int to) {
    myMap.removeRange(from, to);
  }

  public int findKey(long key) {
    return myKeys.binarySearch(key);
  }

  public int findKey(long key, int indexFrom) {
    int size = myKeys.size();
    assert indexFrom == size || indexFrom == 0 || myKeys.get(indexFrom - 1) < key : key + " " + indexFrom + " " + this;
    return myKeys.binarySearch(key, indexFrom, size);
  }

  public void setValue(int index, long value) {
    checkIndex(index);
    myMap.set(index, 1, value);
  }

  private void checkIndex(int index) {
    if (index < 0 || index >= size())
      throw new IndexOutOfBoundsException(index + " " + this);
  }

  public void setKey(int index, long key) {
    checkIndex(index);
    checkSetKeyAt(index, key);
    myMap.set(index, 0, key);
  }

  private void checkSetKeyAt(int index, long key) {
    if (index > 0 && myKeys.get(index - 1) >= key)
      throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index - 1) + " " + this);
    if (index + 1 < myMap.size() && myKeys.get(index + 1) <= key)
      throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index + 1) + " " + this);
  }

  public void setAt(int index, long key, long value) {
    checkIndex(index);
    checkSetKeyAt(index, key);
    myMap.set(index, 0, key);
    myMap.set(index, 1, value);
  }

  public long getValue(int index) {
    return myMap.get(index, 1);
  }

  public long getKey(int index) {
    return myMap.get(index, 0);
  }

  public int size() {
    return myMap.size();
  }

  public boolean isEmpty() {
    return myMap.isEmpty();
  }

  public Iterator iterator(int from, int to) {
    return new Iterator(from, to);
  }

  public Iterator iterator(int from) {
    return new Iterator(from, size());
  }

  public Iterator iterator() {
    return new Iterator(0, size());
  }

  /**
   * Modifies all keys within range by adding increment to each key.
   */
  public void adjustKeys(int from, int to, long increment) {
    if (from >= to) return;
    if (from < 0) throw new IndexOutOfBoundsException(from + " " + this);
    int sz = size();
    if (to > sz) throw new IndexOutOfBoundsException(to + " " + this);
    if (from > 0) {
      long b = (long)(getKey(from) + increment);
      if (getKey(from- 1) >= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + myKeys.get(from - 1) + " " + b);
    }
    if (to < sz) {
      long b = (long)(getKey(to - 1) + increment);
      if (getKey(to) <= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + getKey(to) + " " + b);
    }
    for (ParallelLongList.Iterator ii = myMap.iterator(from, to); ii.hasNext();) {
      ii.next(null);
      ii.set(0, 0, (long)(ii.get(0, 0) + increment));
    }
  }

  public LongListIterator keysIterator(int from, int to) {
    return myKeys.iterator(from, to);
  }

  public LongListIterator valuesIterator(int from, int to) {
    return myValues.iterator(from, to);
  }

  public void clear() {
    myMap.clear();
  }

  public class Iterator {
    private final ParallelLongList.Iterator ii;
    private final long[] myKeyValue = new long[2];
    private boolean myEntry;

    public Iterator(int from, int to) {
      ii = myMap.iterator(from, to);
    }

    public boolean hasNext() {
      return ii.hasNext();
    }

    public void next() {
      myEntry = false;
      ii.next(myKeyValue);
      myEntry = true;
    }

    public long key() {
      if (!myEntry) throw new NoSuchElementException();
      return myKeyValue[0];
    }

    public long value() {
      if (!myEntry) throw new NoSuchElementException();
      return myKeyValue[1];
    }

    public long getKey(int relativeOffset) {
      return ii.get(relativeOffset, 0);
    }

    public long getValue(int relativeOffset) {
      return ii.get(relativeOffset, 1);
    }

    public void setValue(int relativeOffset, long value) {
      ii.set(relativeOffset, 1, value);
    }

    public void remove() {
      ii.removeRange(0, 1);
    }

    public void removeRange(int fromOffset, int toOffset) {
      ii.removeRange(fromOffset, toOffset);
    }

    public void move(int offset) {
      ii.move(offset);
    }
  }
}
