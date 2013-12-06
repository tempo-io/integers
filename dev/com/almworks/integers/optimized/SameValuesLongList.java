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

// CODE GENERATED FROM com/almworks/integers/optimized/SameValuesPList.tpl


package com.almworks.integers.optimized;

import com.almworks.integers.*;
import com.almworks.integers.util.IntegersDebug;
import com.almworks.integers.util.LongSizedIterable;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * This list is memory-optimized to contain values where each value is
 * likely to be the same as the previous one. Values are stored as
 * a map index_where_value_starts=>value.
 * <p/>
 * Starting value is 0, that is, if map is empty, all values are 0.
 */
public class SameValuesLongList extends AbstractWritableLongList {
  /**
   * Maps index to the value that starts at that index
   */
  private IntLongMap myMap;

  public SameValuesLongList() {
    this(new IntLongMap());
  }

  public SameValuesLongList(IntLongMap hostMap) {
    assert hostMap.isEmpty() : hostMap;
    myMap = hostMap;
  }

  // todo write setAll

  // todo javadoc, values.size() <= counts.size()
  public static SameValuesLongList create(LongSizedIterable values, IntIterable counts) {
    IntArray mapKeys = new IntArray(values.size());
    LongArray mapValues = new LongArray(values.size());
    mapKeys.add(0);
    int last = 0, cur, curCount;
    long curValue;
    IntIterator it = counts.iterator();
    LongIterator valIt = values.iterator();
    for (int i = 0, n = values.size(); i < n; i++) {
      curCount = it.nextValue();
      curValue = valIt.nextValue();
      if (curCount != 0) {
        mapValues.add(curValue);
        cur = last + curCount;
        mapKeys.add(cur);
        last = cur;
      }
    }
    SameValuesLongList list = new SameValuesLongList();
    list.updateSize(mapKeys.removeLast());
    list.myMap = new IntLongMap(mapKeys, mapValues);
    list.checkInvariants();
    return list;
  }

  public static SameValuesLongList create(LongArray values) {
    return create(values, IntCollections.repeat(1, values.size()));
  }


  public long get(int index) {
    assert !IntegersDebug.CHECK || checkInvariants();
    if (index < 0 || index >= size())
      throw new IndexOutOfBoundsException(index + " " + this);
    int ki = myMap.findKey(index);
    return valueForFind(ki);
  }

  private long valueForFind(int ki) {
    if (ki == -1) {
      // no earlier index
      return 0;
    } else if (ki < 0) {
      // take previous index
      ki = -ki - 2;
    }
    return myMap.getValueAt(ki);
  }

  public void add(long value) {
    assert !IntegersDebug.CHECK || checkInvariants();
    int sz = size();
    int msz = myMap.size();
    long lastValue = msz == 0 ? value + 1 : myMap.getValueAt(msz - 1);
    if (value != lastValue) {
      myMap.insertAt(msz, sz, value);
    }
    updateSize(sz + 1);
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  public void insertMultiple(int index, long value, int count) {
    assert !IntegersDebug.CHECK || checkInvariants();
    if (count < 0) throw new IllegalArgumentException();
    if (count == 0) return;

    int size = size();
    if (index < 0 || index > size)
      throw new IndexOutOfBoundsException(index + " " + this);
    int ki = myMap.findKey(index);
    // will shift all rightward indexes by +count
    int shiftFrom = ki >= 0 ? ki : -ki - 1;
    // previous value before this insertion
    long prevValue = prevValueForFindIndex(ki);
    int sz = myMap.size();
    // we have to adjust first, or insert will fail because keys will conflict
    if (shiftFrom < sz) {
      myMap.adjustKeys(shiftFrom, sz, count);
    }
    // if prevValue == value, then nothing needs to be inserted -- the values will be correct by themselves
    if (prevValue != value) {
      if (ki >= 0 && myMap.getValueAt(ki) == value) {
        // we have inserted the same value as following
        // so "move back" pointer at ki
        myMap.setKey(ki, index);
      } else {
        myMap.insertAt(shiftFrom, index, value);
        // now check if we have inserted in the middle of another range, splitting it in two. add second part if needed.
        if (index < size && ki < 0) {
          myMap.insertAt(shiftFrom + 1, index + count, prevValue);
        }
      }
    }
    updateSize(size + count);
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  /**
   * Increases the list size and shifts all values to the right of {@code index}.
   * If {@code 0 <= index} and {@code index < size()} the resulting "hole"
   * in the range {@code [index; index + count)} contains {@code count} values equal to {@code get(index)}.
   * If {@code index == size()} method works as if {@code index == size() - 1}.
   * Invoking {@code expand(0, count)} will add {@code count} zeros.
   *
   * @param index where to insert the "hole", index must be >= 0 and <= size()
   * @param count how much size increase is needed, must be >= 0
   *
   * @throws IndexOutOfBoundsException when index < 0 or index > size
   * @throws IllegalArgumentException when count < 0
   */
  public void expand(int index, int count) {
    assert !IntegersDebug.CHECK || checkInvariants();
    if (count < 0) {
      throw new IllegalArgumentException();
    }
    int size = size();
    if (index < 0 || index > size)
      throw new IndexOutOfBoundsException(index + " " + this);
    if (count == 0) {
      return;
    }
    int ki = myMap.findKey(index);
    // will shift all rightward indexes by +count
    int shiftFrom = ki >= 0 ? ki + 1 : -ki - 1;
    int sz = myMap.size();
    // we have to adjust first, or insert will fail because keys will conflict
    if (shiftFrom < sz) {
      myMap.adjustKeys(shiftFrom, sz, count);
    }
    updateSize(size + count);
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  public void setRange(int from, int to, long value) {
    assert !IntegersDebug.CHECK || checkInvariants();
    if (from >= to) return;
    int size = size();
    if (from < 0 || to > size) {
      throw new IndexOutOfBoundsException(from + " " + to + " " + this);
    }

    int fi = myMap.findKey(from);
    int removeFrom = fi >= 0 ? fi : -fi - 1;
    long prevValue = prevValueForFindIndex(fi);
    int ti = myMap.findKey(to, removeFrom);
    int removeTo = ti >= 0 ? ti + 1 : -ti - 1;
    long nextValue = valueForFind(ti);

    if (removeFrom < removeTo) {
      myMap.removeRange(removeFrom, removeTo);
    }

    int p = removeFrom;
    if (value != prevValue) {
      myMap.insertAt(p, from, value);
      p++;
    }
    if (to < size && value != nextValue) {
      myMap.insertAt(p, to, nextValue);
    }

    modified();
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  private long prevValueForFindIndex(int findIndex) {
    return findIndex == -1 || findIndex == 0 ? Integer.MIN_VALUE : myMap.getValueAt(findIndex < 0 ? -findIndex - 2 : findIndex - 1);
  }

  private boolean checkInvariants() {
    assert myMap != null;
    int size = size();
    if (myMap.size() == 0) {
      assert size == 0;
    } else {
      assert size >= 0 : size;
      long lastValue = myMap.isEmpty() ? 0 : myMap.getValueAt(0) - 1;
      int lastKey = -1;
      for (int i = 0; i < myMap.size(); i++) {
        int key = myMap.getKeyAt(i);
        long value = myMap.getValueAt(i);
        assert key > lastKey : i + " " + lastKey + " " + key;
        assert value != lastValue : i + " " + value;
        assert key < size : i + " " + key + " " + size;
        lastKey = key;
        lastValue = value;
      }
    }
    return true;
  }

  @Override
  public void sortUnique() {
    LongArray newValues = new LongArray(myMap.valuesIterator(0, myMap.size()));
    newValues.sortUnique();
    int newSize = newValues.size();
    IntArray newIndexes = new IntArray(newSize);
    for (int i = 0; i < newSize; i++) newIndexes.add(i);
    myMap = new IntLongMap(newIndexes, newValues);
    updateSize(newSize);
  }


  /**
   * Remove range from the list, keeping optimal structure
   */
  public void removeRange(int from, int to) {
    assert !IntegersDebug.CHECK || checkInvariants();

    if (from >= to)
      return;
    int size = size();
    if (from < 0 || to > size)
      throw new IndexOutOfBoundsException(from + " " + to + " " + this);
    int count = to - from;

    // fi will hold search result for "from", could be negative
    int fi = myMap.findKey(from);

    // removeFrom is the "insertion point" for "from" - pairs may be removed starting from that place.
    int removeFrom = fi;
    if (removeFrom < 0) {
      removeFrom = -removeFrom - 1;
    }

    // whether to set new boundary (if the end of removed range falls in the middle of current range
    boolean set = false;

    // the value that follows removed range, relevant only if set == true.
    long followingValue = 0;

    // exclusive high boundary for removing pairs from map
    int removeTo;

    if (to == size) {
      // remove all leftover pairs, don't set anything
      removeTo = myMap.size();
    } else {
      // ti will hold search result for "to", could be negative
      // search starts at "removeFrom", because to > from
      int ti = myMap.findKey(to, removeFrom);

      if (ti >= 0) {
        // exact boundary is found: don't set new boundary, remove everything up to this boundary
        removeTo = ti;
      } else {
        if (ti == -1) {
          // removal within leading zeroes
          removeTo = 0;
        } else {
          // will remove all up to the last boundary
          // removeTo is guaranteed to be less than myMap.size()
          removeTo = -ti - 2;
          followingValue = myMap.getValueAt(removeTo);

          // compare preceding and following values: if they are equal, don't set boundary - just remove
          long prevValue = prevValueForFindIndex(fi);
          if (followingValue != prevValue)
            set = true;
          else
            removeTo++;
        }
      }
    }

    // remove if needed
    if (removeFrom < removeTo) {
      myMap.removeRange(removeFrom, removeTo);
    }

    // pairs at "removeTo" are now at "removeFrom"
    if (set) {
      // set new boundary
      myMap.setAt(removeFrom, from, followingValue);
      removeFrom++;
    }
    /*else if (removeFrom < myMap.size()) {
      // check if we can collapse adjacent pairs with the same value
      int v = myMap.getValueAt(removeFrom);
      if (removeFrom == 0 && v == 0 || removeFrom > 0 && myMap.getValueAt(removeFrom - 1) == v) {
        myMap.removeAt(removeFrom);
      }
    }*/

    // decrement indexes that follow removed range
    myMap.adjustKeys(removeFrom, myMap.size(), -count);

    updateSize(size - count);
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  @NotNull
  public WritableLongListIterator iterator(int from, int to) {
    if (from > to || from < 0 || to > size())
      throw new IndexOutOfBoundsException(from + " " + to + " " + this);
    return new SameValuesIterator(from, to);
  }

  @Override
  public void swap(int index1, int index2) {
    if (index1 != index2) {
      long t = get(index1);
      long d = get(index2);
      if (t == d) return;
      set(index1, d);
      set(index2, t);
    }
  }

  public void clear() {
    myMap.clear();
    updateSize(0);
  }

  public int getChangeCount() {
    return isEmpty() ? 0: myMap.size() - 1;
  }

  public int getNextDifferentValueIndex(int curIndex) {
    if (curIndex < 0 || curIndex >= size())
      throw new IndexOutOfBoundsException(curIndex + " " + this);
    int ki = myMap.findKey(curIndex);
    // find key ki for the index from which start the values equal to a[curIndex]
    if(ki < 0) ki = -ki - 2;
    // increase the found key to get the next different value
    ++ki;
    return ki < myMap.size() ? myMap.getKeyAt(ki) : size();
  }

  public void reverse() {
    int mSize = myMap.size();
    if (mSize < 2) return;

    int idx = 0, sz = size();

    // example
    // keys:   (0, 2, 3, 6)  -> (0, 4, 7, 8); size = 10
    // values: (4, 1, 2, 3)  -> (3, 2, 1, 4)

    IntLongMap.ConsistencyViolatingMutator m = myMap.startMutation();

    for (int j = mSize - 1; idx < j; j--) {
      long valSwp = m.getValue(idx);
      m.setValue(idx, m.getValue(j));
      m.setValue(j, valSwp);

      idx++;
      if (idx == j) break;

      int keySwp = m.getKey(idx);
      m.setKey(idx, sz - m.getKey(j));
      m.setKey(j, sz - keySwp);
    }

    if ((mSize & 1) == 0) {
      m.setKey(idx, sz - m.getKey(idx));
    }

    m.commit();
    assert !IntegersDebug.CHECK || checkInvariants();
  }


  private final class SameValuesIterator extends WritableIndexIterator {
    private IntLongIterator myIterator;
    private long myValue;
    private int myNextChangeIndex;

    public SameValuesIterator(int from, int to) {
      super(from, to);
      sync();
    }

    protected void sync() {
      super.sync();
      int p = hasValue() ? myMap.findKey(index()) : myMap.findKey(getNextIndex());
      if (p == -1) {
        myValue = 0;
        myIterator = myMap.iterator();
      } else {
        if (p < 0) {
          p = -p - 2;
        }
        myIterator = myMap.iterator(p);
        myIterator.next();
        myValue = myIterator.right();
      }
      advanceToNextChange();
    }

    private void advanceToNextChange() {
      if (myIterator.hasNext()) {
        myIterator.next();
        myNextChangeIndex = myIterator.left();
      } else myNextChangeIndex = size();
    }

    public WritableIndexIterator next() throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      setNotRemoved();
      if (getNextIndex() >= getTo())
        throw new NoSuchElementException();
      if (getNextIndex() == myNextChangeIndex) {
        myValue = myIterator.right();
        advanceToNextChange();
      }
      setNext(getNextIndex() + 1);
      return this;
    }

    public long value() throws NoSuchElementException {
      if (isJustRemoved())
        throw new IllegalStateException();
      if (!hasValue())
        throw new NoSuchElementException();
      return myValue;
    }

    public void move(int count) throws ConcurrentModificationException, NoSuchElementException {
      super.move(count);
      if (count != 0)
        sync();
    }
  }
}
