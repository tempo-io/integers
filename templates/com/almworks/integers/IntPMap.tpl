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

// GENERATED CODE!!!
package com.almworks.integers;

import java.util.NoSuchElementException;

public class Int#E#Map {
  private final WritableIntList myKeys;
  private final Writable#E#List myValues;

  public Int#E#Map(WritableIntList keys, Writable#E#List values) {
    myKeys = keys;
    myValues = values;
  }

  public Int#E#Map() {
    this(new IntArray(), new #E#Array());
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public int size() {
    assert myKeys.size() == myValues.size();
    return myKeys.size();
  }

  public void clear() {
    myKeys.clear();
    myValues.clear();
  }

  public int findKey(int key) {
    return findKey(key, 0);
  }

  public #e# getValueAt(int index) {
    return myValues.get(index);
  }

  public void insertAt(int index, int key, #e# value) {
    assert index >= 0 && index <= size() : index + " " + this;
    assert
      index == 0 || myKeys.get(index - 1) < key : index + " " + key + " " + myKeys.get(index - 1);
    assert index == size() || myKeys.get(index) > key : index + " " + key + " " + myKeys.get(index);
    doInsert(index, key, value);
  }

  private void doInsert(int idx, int key, #e# value) {
    myKeys.insert(idx, key);
    myValues.insert(idx, value);
  }

  public void adjustKeys(int from, int to, int increment) {
    if (from >= to) return;
    if (from < 0) throw new IndexOutOfBoundsException(from + " " + this);
    int sz = size();
    if (to > sz) throw new IndexOutOfBoundsException(to + " " + this);
    if (from > 0) {
      int b = getKey(from) + increment;
      if (getKey(from- 1) >= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + myKeys.get(from - 1) + " " + b);
    }
    if (to < sz) {
      int b = getKey(to - 1) + increment;
      if (getKey(to) <= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + getKey(to) + " " + b);
    }
    for (WritableIntListIterator it = myKeys.iterator(from, to); it.hasNext();) it.set(0, it.next() + increment);
  }

  public void setKey(int index, int key) {
    checkIndex(index);
    checkSetKeyAt(index, key);
    myKeys.set(index, key);
  }

  public void removeRange(int from, int to) {
    myKeys.removeRange(from, to);
    myValues.removeRange(from, to);
  }

  public int getKey(int index) {
    return myKeys.get(index);
  }

  public int findKey(int key, int from) {
    int size = myKeys.size();
    assert from == size || from == 0 || myKeys.get(from - 1) < key : key + " " + from + " " + this;
    return myKeys.binarySearch(key, from, size);
  }

  public void setAt(int index, int key, #e# value) {
    checkIndex(index);
    checkSetKeyAt(index, key);
    myKeys.set(index, key);
    myValues.set(index, value);
  }

  public Iterator iterator() {
    return iterator(0, size());
  }

  public Iterator iterator(int from) {
    return iterator(from, size());
  }

  public Iterator iterator(int from, int to) {
    return new Iterator(myKeys.iterator(from, to), myValues.iterator(from, to));
  }

  public boolean containsKey(int key) {
    return findKey(key) >= 0;
  }

  public IntListIterator keysIterator(int from, int to) {
    return myKeys.iterator(from, to);
  }

  public #E#Iterator valuesIterator(int from, int to) {
    return myValues.iterator(from, to);
  }

  private void checkIndex(int index) {
    if (index < 0 || index >= size()) throw new IndexOutOfBoundsException(index + " " + this);
  }

  private void checkSetKeyAt(int index, int key) {
    if (index > 0 && myKeys.get(index - 1) >= key) throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index - 1) + " " + this);
    if (index + 1 < size() && myKeys.get(index + 1) <= key) throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index + 1) + " " + this);
  }

  public static class Iterator {
    private final IntIterator myKeyIt;
    private final #E#Iterator mValueIt;
    private #e# myCurrentValue;
    private int myCurrentKey;
    private boolean myAdvanced = false;

    private Iterator(IntIterator keyIt, #E#Iterator valueIt) {
      myKeyIt = keyIt;
      mValueIt = valueIt;
    }

    public boolean hasNext() {
      boolean r = myKeyIt.hasNext();
      assert r == mValueIt.hasNext();
      return r;
    }

    public void next() {
      myAdvanced = false;
      myCurrentKey = myKeyIt.next();
      myCurrentValue = mValueIt.next();
      myAdvanced = true;
    }

    public #e# value() {
      if (!myAdvanced) throw new NoSuchElementException();
      return myCurrentValue;
    }

    public int key() {
      if (!myAdvanced) throw new NoSuchElementException();
      return myCurrentKey;
    }
  }
}
