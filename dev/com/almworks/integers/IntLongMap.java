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

// CODE GENERATED FROM com/almworks/integers/IntPMap.tpl


package com.almworks.integers;

import com.almworks.integers.func.IntFunction;
import com.almworks.integers.func.LongFunction;
import com.almworks.integers.util.FailFastIntLongIterator;
import com.almworks.integers.util.IntegersDebug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.NoSuchElementException;

public class IntLongMap extends AbstractWritableIntLongMap {
  private final WritableIntList myKeys;
  private final WritableLongList myValues;
  @Nullable private ConsistencyViolatingMutator myMutator;

  public IntLongMap(WritableIntList keys, WritableLongList values) {
    myKeys = keys;
    myValues = values;
    String error = checkInvariants();
    if (error != null) {
      throw new IllegalArgumentException(error);
    }
  }

  public IntLongMap() {
    this(new IntArray(), new LongArray());
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
  public long get(int key) {
    int idx = findKey(key);
    // todo update default value
    return Math.max((idx >= 0) ? getValueAt(idx) - 1 : 0, 0);
  }

  public int findKey(int key) {
    checkMutatorPresence();
    return findKey(key, 0);
  }

  public long getValueAt(int index) {
    checkMutatorPresence();
    return myValues.get(index);
  }

  @Override
  protected long putImpl(int key, long value) {
    checkMutatorPresence();
    int idx = findKey(key);
    long oldValue = 0;
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

  public void insertAt(int index, int key, long value) {
    checkMutatorPresence();
    assert index >= 0 && index <= size() : index + " " + this;
    assert
        index == 0 || myKeys.get(index - 1) < key : index + " " + key + " " + myKeys.get(index - 1);
    assert index == size() || myKeys.get(index) > key : index + " " + key + " " + myKeys.get(index);
    doInsert(index, key, value);
  }

  private void doInsert(int idx, int key, long value) {
    checkMutatorPresence();
    myKeys.insert(idx, key);
    myValues.insert(idx, value);
  }

  public void adjustKeys(int from, int to, int increment) {
    checkMutatorPresence();
    if (from >= to) return;
    if (from < 0) throw new IndexOutOfBoundsException(from + " " + this);
    int sz = size();
    if (to > sz) throw new IndexOutOfBoundsException(to + " " + this);
    if (from > 0) {
      int b = getKeyAt(from) + increment;
      if (getKeyAt(from - 1) >= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + myKeys.get(from - 1) + " " + b);
    }
    if (to < sz) {
      int b = getKeyAt(to - 1) + increment;
      if (getKeyAt(to) <= b)
        throw new IllegalArgumentException(from + " " + to + " " + increment + " " + getKeyAt(to) + " " + b);
    }
    for (WritableIntListIterator it = myKeys.iterator(from, to); it.hasNext();) it.set(0, it.nextValue() + increment);
  }

  public void setKey(int index, int key) {
    checkMutatorPresence();
    checkIndex(index);
    checkSetKeyAt(index, key);
    myKeys.set(index, key);
  }

  @Override
  protected long removeImpl(int key) {
    checkMutatorPresence();
    int idx = findKey(key);
    // todo update default value
    if (idx < 0) {
      return 0;
    } else {
      long oldValue = getValueAt(idx);
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

  public int getKeyAt(int index) {
    checkMutatorPresence();
    return myKeys.get(index);
  }

  public int findKey(int key, int from) {
    checkMutatorPresence();
    int size = myKeys.size();
    assert from == size || from == 0 || myKeys.get(from - 1) < key : key + " " + from + " " + this;
    return myKeys.binarySearch(key, from, size);
  }

  public void setAt(int index, int key, long value) {
    checkMutatorPresence();
    checkIndex(index);
    checkSetKeyAt(index, key);
    myKeys.set(index, key);
    myValues.set(index, value);
  }

  public IntLongIterator iterator(int from) {
    return iterator(from, size());
  }

  public IntLongIterator iterator(int from, int to) {
    checkMutatorPresence();
    return IntLongIterators.pair(myKeys.iterator(from, to), myValues.iterator(from, to));
  }

  public IntLongIterator iterator() {
    return failFast(iterator(0));
  }

  public boolean containsKey(int key) {
    checkMutatorPresence();
    return findKey(key) >= 0;
  }

  public IntListIterator keysIterator(int from) {
    return keysIterator(from, size());
  }

  public IntListIterator keysIterator(int from, int to) {
    checkMutatorPresence();
    return myKeys.iterator(from, to);
  }

  public IntIterator keysIterator() {
    return failFast(keysIterator(0));
  }

  public LongIterator valuesIterator(int from) {
    return valuesIterator(from, size());
  }

  public LongIterator valuesIterator(int from, int to) {
    checkMutatorPresence();
    return myValues.iterator(from, to);
  }

  public LongIterator valuesIterator() {
    return failFast(valuesIterator(0));
  }

  public IntList keysToList() {
    checkMutatorPresence();
    return myKeys;
  }

  public LongList valuesToList() {
    checkMutatorPresence();
    return myValues;
  }

  private void checkIndex(int index) {
    if (index < 0 || index >= size()) throw new IndexOutOfBoundsException(index + " " + this);
  }

  private void checkSetKeyAt(int index, int key) {
    if (index > 0 && myKeys.get(index - 1) >= key) {
      throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index - 1) + " " + this);
    }
    if (index + 1 < size() && myKeys.get(index + 1) <= key) {
      throw new IllegalArgumentException(index + " " + key + " " + myKeys.get(index + 1) + " " + this);
    }
  }

  /**
   * Checks if this map correct: <ul>
   *   <li>sizes of {@link #myKeys} and {@link #myValues} should be equal;
   *   <li>{@link #myKeys} should be sorted unique;
   *   <li>adjacent elements in {@link #myValues} should be different;
   * </ul>
   * @return String with information about error in {@link #myKeys} and {@link #myValues} if it exist,
   * otherwise {@code null}
   */
  public String checkInvariants() {
    if (myKeys.size() != myValues.size()) {
      return "sizes of keys and values should be equal";
    }
    if (!myKeys.isUniqueSorted()) {
      return "keys should be sorted unique";
    }
    if (myValues.size() < 2) {
      return null;
    }
    long curValue;
    long lastValue = myValues.get(0);
    int idx = 0;
    for (LongIterator ii : myValues.iterator(1)) {
      curValue = ii.value();
      if (curValue == lastValue) {
        return "adjacent elements in values are equal; indices: " + idx + " " + (idx + 1);
      }
      lastValue = curValue;
      idx++;
    }
    return null;
  }

  private void checkMutatorPresence() throws IllegalStateException {
    if (myMutator != null) throw new IllegalStateException();
  }

  /**
   * Enters this {@code IntLongMap} into a mode in which consistency-breaking mutations are allowed.
   *
   * <p>While in this mode, usage of all of this {@code IntLongMap}'s methods
   * (except {@code size()} and {@code empty()}) would throw IllegalStateException.
   * Instead of them, {@code myMutator}'s methods should be used.<br>
   * {@code myMutator.commit()} brings this {@code IntLongMap} back to its normal state.</p>
   * @throws IllegalStateException if this {@code IntLongMap} is already in mutation state.
   */
  public ConsistencyViolatingMutator startMutation() throws IllegalStateException {
    return new ConsistencyViolatingMutator();
  }

  public class ConsistencyViolatingMutator {

    public ConsistencyViolatingMutator() {
      if (myMutator != null) throw new IllegalStateException();
      myMutator = this;
    }

    public int getKey(int index) {
      return myKeys.get(index);
    }

    public long getValue(int index) {
      return myValues.get(index);
    }

    public void setKey(int index, int key) {
      myKeys.set(index, key);
    }

    public void setValue(int index, long val) {
      myValues.set(index, val);
    }

    public void insertAt(int idx, int key, long value) {
      myKeys.insert(idx, key);
      myValues.insert(idx, value);
    }

    public void removeAt(int idx) {
      myKeys.removeAt(idx);
      myValues.removeAt(idx);
    }

    public void reverseValues() {
      myValues.reverse();
    }

    public ConsistencyViolatingMutator replace(IntList keys, LongList values) {
      myKeys.clear();
      myValues.clear();
      myKeys.addAll(keys);
      myValues.addAll(values);
      return this;
    }

    public void addPair(int key, long value) {
      myKeys.add(key);
      myValues.add(value);
    }

    public void commit() {
      String error = checkInvariants();
      if (error != null) {
        throw new IllegalStateException(error);
      }
      IntLongMap.this.myMutator = null;
    }
  }

}
