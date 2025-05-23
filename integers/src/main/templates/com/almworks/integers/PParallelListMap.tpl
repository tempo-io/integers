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
import java.util.NoSuchElementException;

public class #E#ParallelListMap implements #E##E#Iterable {
  private final #E#List myKeys;
  private final #E#List myValues;

  private final #E#ParallelList myMap;

  public #E#ParallelListMap() {
    this(new #E#Array());
  }

  public #E#ParallelListMap(Writable#E#List storage) {
    if (storage == null)
      throw new NullPointerException();
    if (!storage.isEmpty())
      throw new IllegalArgumentException();
    myMap = new #E#ParallelList(storage, 2);
    myKeys = myMap.createListAccessor(0);
    myValues = myMap.createListAccessor(1);
  }

  /**
   * @return <b>index</b> of the pair
   */
  public int put(#e# key, #e# value) {
    int idx = findKey(key);
    if (idx < 0) {
      doInsert(-idx - 1, key, value);
    } else {
      setValue(idx, value);
    }
    return idx;
  }

  public void insertAt(int index, #e# key, #e# value) {
    assert index >= 0 && index <= size() : index + " " + this;
    assert
      index == 0 || myKeys.get(index - 1) < key : index + " " + key + " " + myKeys.get(index - 1);
    assert index == size() || myKeys.get(index) > key : index + " " + key + " " + myKeys.get(index);
    doInsert(index, key, value);
  }


  private void doInsert(int idx, #e# key, #e# value) {
    myMap.insert(idx, key, value);
  }

  public boolean containsKey(#e# key) {
    return findKey(key) >= 0;
  }

  public void remove(#e# key) {
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

  public int findKey(#e# key) {
    return myKeys.binarySearch(key);
  }

  public int findKey(#e# key, int indexFrom) {
    int size = myKeys.size();
    assert indexFrom == size || indexFrom == 0 || myKeys.get(indexFrom - 1) < key : key + " " + indexFrom + " " + this;
    return myKeys.binarySearch(key, indexFrom, size);
  }

  public void setValue(int index, #e# value) {
    checkIndex(index);
    myMap.set(index, 1, value);
  }

  private void checkIndex(int index) {
    if (index < 0 || index >= size())
      throw new IndexOutOfBoundsException(index + " " + this);
  }

  public void setKey(int index, #e# key) {
    checkIndex(index);
    checkSetKeyAt(index, key);
    myMap.set(index, 0, key);
  }

  private void checkSetKeyAt(int index, #e# key) {
    if (index > 0 && myKeys.get(index - 1) >= key)
      throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index - 1) + " " + this);
    if (index + 1 < myMap.size() && myKeys.get(index + 1) <= key)
      throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index + 1) + " " + this);
  }

  public void setAt(int index, #e# key, #e# value) {
    checkIndex(index);
    checkSetKeyAt(index, key);
    myMap.set(index, 0, key);
    myMap.set(index, 1, value);
  }

  public #e# getValue(int index) {
    return myMap.get(index, 1);
  }

  public #e# getKey(int index) {
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

  @NotNull
  public Iterator iterator() {
    return new Iterator(0, size());
  }

  /**
   * Modifies all keys within range by adding increment to each key.
   */
  public void adjustKeys(int from, int to, #e# increment) {
    if (from >= to) return;
    if (from < 0) throw new IndexOutOfBoundsException(from + " " + this);
    int sz = size();
    if (to > sz) throw new IndexOutOfBoundsException(to + " " + this);
    if (from > 0) {
      #e# b = (#e#)(getKey(from) + increment);
      if (getKey(from- 1) >= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + myKeys.get(from - 1) + " " + b);
    }
    if (to < sz) {
      #e# b = (#e#)(getKey(to - 1) + increment);
      if (getKey(to) <= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + getKey(to) + " " + b);
    }

    for (#E#ParallelList.Iterator ii = myMap.iterator(from, to); ii.hasNext();) {
      ii.next(null);
      ii.set(0, 0, (#e#)(ii.get(0, 0) + increment));
    }
  }

  public #E#ListIterator keysIterator(int from, int to) {
    return myKeys.iterator(from, to);
  }

  public #E#ListIterator valuesIterator(int from, int to) {
    return myValues.iterator(from, to);
  }

  public void clear() {
    myMap.clear();
  }

  public class Iterator implements #E##E#Iterator {
    private final #E#ParallelList.Iterator ii;
    private final #e#[] myKeyValue = new #e#[2];
    private boolean myEntry;

    public Iterator(int from, int to) {
      ii = myMap.iterator(from, to);
    }

    public boolean hasNext() {
      return ii.hasNext();
    }

    @Override
    public boolean hasValue() {
      return ii.hasValue();
    }

    public Iterator next() {
      myEntry = false;
      ii.next(myKeyValue);
      myEntry = true;
      return this;
    }

    public #e# left() {
      if (!myEntry) throw new NoSuchElementException();
      return myKeyValue[0];
    }

    public #e# right() {
      if (!myEntry) throw new NoSuchElementException();
      return myKeyValue[1];
    }

    public #e# getLeft(int relativeOffset) {
      return ii.get(relativeOffset, 0);
    }

    public #e# getRight(int relativeOffset) {
      return ii.get(relativeOffset, 1);
    }

    public void setRight(int relativeOffset, #e# value) {
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

    @NotNull
    @Override
    public Iterator iterator() {
      return this;
    }
  }
}
