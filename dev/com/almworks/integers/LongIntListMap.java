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

// CODE GENERATED FROM com/almworks/integers/PQListMap.tpl




package com.almworks.integers;

import org.jetbrains.annotations.Nullable;

public class LongIntListMap extends AbstractWritableLongIntMap {
  private final WritableLongList myKeys;
  private final WritableIntList myValues;
  @Nullable private ConsistencyViolatingMutator myMutator;

  public LongIntListMap(WritableLongList keys, WritableIntList values) {
    myKeys = keys;
    myValues = values;
    String error = checkInvariants();
    if (error != null) {
      throw new IllegalArgumentException(error);
    }
  }

  public LongIntListMap() {
    this(new LongArray(), new IntArray());
  }

  public int size() {
    assert myKeys.size() == myValues.size();
    return myKeys.size();
  }

  public void clear() {
    checkMutatorPresence();
    myKeys.clear();
    myValues.clear();
  }

  @Override
  public int get(long key) {
    int idx = findKey(key);
    // todo update default value
    if (idx >= 0) {
      return getValueAt(idx);
    } else {
      return DEFAULT_VALUE;
    }
  }

  public int findKey(long key) {
    checkMutatorPresence();
    return findKey(key, 0);
  }

  public int getValueAt(int index) {
    checkMutatorPresence();
    return myValues.get(index);
  }

  @Override
  protected int putImpl(long key, int value) {
    checkMutatorPresence();
    int idx = findKey(key);
    int oldValue = DEFAULT_VALUE;
    if (idx >= 0) {
      oldValue = getValueAt(idx);
      setAt(idx, key, value);
    } else {
      idx = -idx - 1;
      insertAt(idx, key, value);
    }
    // todo update default value
    return oldValue;
  }

  public void insertAt(int index, long key, int value) {
    checkMutatorPresence();
    assert index >= 0 && index <= size() : index + " " + this;
    assert
        index == 0 || myKeys.get(index - 1) < key : index + " " + key + " " + myKeys.get(index - 1);
    assert index == size() || myKeys.get(index) > key : index + " " + key + " " + myKeys.get(index);
    doInsert(index, key, value);
  }

  private void doInsert(int idx, long key, int value) {
    checkMutatorPresence();
    myKeys.insert(idx, key);
    myValues.insert(idx, value);
  }

  public void adjustKeys(int from, int to, long increment) {
    checkMutatorPresence();
    if (from >= to) return;
    if (from < 0) throw new IndexOutOfBoundsException(from + " " + this);
    int sz = size();
    if (to > sz) throw new IndexOutOfBoundsException(to + " " + this);
    if (from > 0) {
      long b = getKeyAt(from) + increment;
      if (getKeyAt(from - 1) >= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + myKeys.get(from - 1) + " " + b);
    }
    if (to < sz) {
      long b = getKeyAt(to - 1) + increment;
      if (getKeyAt(to) <= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + getKeyAt(to) + " " + b);
    }
    for (WritableLongListIterator it = myKeys.iterator(from, to); it.hasNext();) it.set(0, it.nextValue() + increment);
  }

  public void setKey(int index, long key) {
    checkMutatorPresence();
    checkIndex(index);
    checkSetKeyAt(index, key);
    myKeys.set(index, key);
  }

  @Override
  protected int removeImpl(long key) {
    checkMutatorPresence();
    int idx = findKey(key);
    if (idx < 0) {
      return DEFAULT_VALUE;
    } else {
      int oldValue = getValueAt(idx);
      removeAt(idx);
      return oldValue;
    }
  }

  public void removeAt(int index) {
    removeRange(index, index + 1);
  }

  public void removeRange(int from, int to) {
    checkMutatorPresence();
    myKeys.removeRange(from, to);
    myValues.removeRange(from, to);
  }

  public long getKeyAt(int index) {
    checkMutatorPresence();
    return myKeys.get(index);
  }

  public int findKey(long key, int from) {
    checkMutatorPresence();
    int size = myKeys.size();
    assert from == size || from == 0 || myKeys.get(from - 1) < key : key + " " + from + " " + this;
    return myKeys.binarySearch(key, from, size);
  }

  public void setAt(int index, long key, int value) {
    checkMutatorPresence();
    checkIndex(index);
    checkSetKeyAt(index, key);
    myKeys.set(index, key);
    myValues.set(index, value);
  }

  public LongIntIterator iterator(int from) {
    return iterator(from, size());
  }

  public LongIntIterator iterator(int from, int to) {
    checkMutatorPresence();
    return LongIntIterators.pair(myKeys.iterator(from, to), myValues.iterator(from, to));
  }

  public LongIntIterator iterator() {
    checkMutatorPresence();
    return new LongIntFailFastIterator(iterator(0)) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public boolean containsKey(long key) {
    checkMutatorPresence();
    return findKey(key) >= 0;
  }

  public LongListIterator keysIterator(int from, int to) {
    checkMutatorPresence();
    return new LongFailFastListIterator(myKeys.iterator(from, to)) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public LongListIterator keysIterator(int from) {
    checkMutatorPresence();
    return keysIterator(from, size());
  }

  public LongListIterator keysIterator() {
    checkMutatorPresence();
    return keysIterator(0, size());
  }

  public IntListIterator valuesIterator(int from, int to) {
    checkMutatorPresence();
    return new IntFailFastListIterator(myValues.iterator(from, to)) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public IntListIterator valuesIterator(int from) {
    checkMutatorPresence();
    return valuesIterator(from, size());
  }

  public IntListIterator valuesIterator() {
    checkMutatorPresence();
    return valuesIterator(0, size());
  }

  @Override
  public LongSortedSet keySet() {
    return LongListSet.asSet(myKeys);
  }

  /**
   * Returns keys of this map.
   * Subsequent map modifications will be reflected in the returned list.
   */
  public LongList keysAsList() {
    checkMutatorPresence();
    return myKeys;
  }

  /**
   * Returns values of this map.
   * Subsequent map modifications will be reflected in the returned list.
   */
  public IntList valuesAsList() {
    checkMutatorPresence();
    return myValues;
  }

  private void checkIndex(int index) {
    if (index < 0 || index >= size()) throw new IndexOutOfBoundsException(index + " " + this);
  }

  private void checkSetKeyAt(int index, long key) {
    if (index > 0 && myKeys.get(index - 1) >= key) {
      throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index - 1) + " " + this);
    }
    if (index + 1 < size() && myKeys.get(index + 1) <= key) {
      throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index + 1) + " " + this);
    }
  }

  /**
   * Checks if this map is correct: <ul>
   *   <li>sizes of {@link #myKeys} and {@link #myValues} should be equal;
   *   <li>{@link #myKeys} should be sorted unique;
   * </ul>
   * @return String with the information about an error in {@link #myKeys} and {@link #myValues} if
   * such an error was found, otherwise {@code null}
   */
  private String checkInvariants() {
    if (myKeys.size() != myValues.size()) {
      return "sizes of keys and values should be equal";
    }
    if (!myKeys.isSortedUnique()) {
      return "keys should be sorted unique";
    }
    return null;
  }

  private void checkMutatorPresence() throws IllegalStateException {
    if (myMutator != null) throw new IllegalStateException();
  }

  /**
   * Enters this {@code LongIntListMap} into a mode in which consistency-breaking mutations are allowed.
   *
   * <p>While in this mode, usage of all of this {@code LongIntListMap}'s methods
   * (except {@code size()} and {@code empty()}) would throw IllegalStateException.
   * Instead of them, {@code myMutator}'s methods should be used.<br>
   * {@code myMutator.commit()} brings this {@code LongIntListMap} back to its normal state.</p>
   * @throws IllegalStateException if this {@code LongIntListMap} is already in mutation state.
   */
  public ConsistencyViolatingMutator startMutation() throws IllegalStateException {
    return new ConsistencyViolatingMutator();
  }

  public class ConsistencyViolatingMutator {

    public ConsistencyViolatingMutator() {
      if (myMutator != null) throw new IllegalStateException();
      myMutator = this;
    }

    public WritableLongList keys() {
      return myKeys;
    }

    public WritableIntList values() {
      return myValues;
    }

    public void commit() {
      String error = checkInvariants();
      if (error != null) {
        throw new IllegalStateException(error);
      }
      LongIntListMap.this.myMutator = null;
    }
  }

}
