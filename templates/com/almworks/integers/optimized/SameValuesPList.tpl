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

package com.almworks.integers.optimized;

import com.almworks.integers.AbstractWritable#E#List;
import com.almworks.integers.Int#E#Map;
import com.almworks.integers.Writable#E#ListIterator;
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
public class SameValues#E#List extends AbstractWritable#E#List {
  /**
   * Maps index to the value that starts at that index
   */
  private final Int#E#Map myMap;

  public SameValues#E#List() {
    this(new Int#E#Map());
  }

  public SameValues#E#List(Int#E#Map hostMap) {
    assert hostMap.isEmpty() : hostMap;
    myMap = hostMap;
  }

  public #e# get(int index) {
    assert checkInvariants();
    if (index < 0 || index >= size())
      throw new IndexOutOfBoundsException(index + " " + this);
    int ki = myMap.findKey(index);
    return valueForFind(ki);
  }

  private #e# valueForFind(int ki) {
    if (ki == -1) {
      // no earlier index
      return 0;
    } else if (ki < 0) {
      // take previous index
      ki = -ki - 2;
    }
    return myMap.getValueAt(ki);
  }

  public void add(#e# value) {
    assert checkInvariants();
    int sz = size();
    int msz = myMap.size();
    #e# lastValue = msz == 0 ? 0 : myMap.getValueAt(msz - 1);
    if (value != lastValue) {
      myMap.insertAt(msz, sz, value);
    }
    updateSize(sz + 1);
    assert checkInvariants();
  }

  public void insertMultiple(int index, #e# value, int count) {
    assert checkInvariants();
    if (count <= 0)
      return;
    int size = size();
    if (index < 0 || index > size)
      throw new IndexOutOfBoundsException(index + " " + this);
    int ki = myMap.findKey(index);
    // will shift all rightward indexes by +count
    int shiftFrom = ki >= 0 ? ki : -ki - 1;
    // previous value before this insertion
    #e# prevValue = prevValueForFindIndex(ki);
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
    assert checkInvariants();
  }

  public void expand(int index, int count) {
    assert checkInvariants();
    if (count <= 0)
      return;
    int size = size();
    if (index < 0 || index > size)
      throw new IndexOutOfBoundsException(index + " " + this);
    int ki = myMap.findKey(index);
    // will shift all rightward indexes by +count
    int shiftFrom = ki >= 0 ? ki + 1 : -ki - 1;
    int sz = myMap.size();
    // we have to adjust first, or insert will fail because keys will conflict
    if (shiftFrom < sz) {
      myMap.adjustKeys(shiftFrom, sz, count);
    }
    updateSize(size + count);
    assert checkInvariants();
  }


  public void setRange(int from, int to, #e# value) {
    assert checkInvariants();

    if (from >= to)
      return;
    int size = size();
    if (from < 0 || to > size)
      throw new IndexOutOfBoundsException(from + " " + to + " " + this);

    int fi = myMap.findKey(from);
    int removeFrom = fi >= 0 ? fi : -fi - 1;
    #e# prevValue = prevValueForFindIndex(fi);
    int ti = myMap.findKey(to, removeFrom);
    int removeTo = ti >= 0 ? ti + 1 : -ti - 1;
    #e# nextValue = valueForFind(ti);

    if (removeFrom < removeTo) {
      myMap.removeRange(removeFrom, removeTo);
    }

    int p = removeFrom;
    if (value != prevValue) {
      if (p != 0 || value != 0) {
        myMap.insertAt(p, from, value);
        p++;
      }
    }

    if (to < size && value != nextValue) {
      myMap.insertAt(p, to, nextValue);
    }

    modified();
    assert checkInvariants();
  }

  private #e# prevValueForFindIndex(int findIndex) {
    return findIndex == -1 || findIndex == 0 ? 0 : myMap.getValueAt(findIndex < 0 ? -findIndex - 2 : findIndex - 1);
  }

  private boolean checkInvariants() {
    assert myMap != null;
    int size = size();
    assert size >= 0 : size;
    #e# lastValue = 0;
    int lastKey = -1;
    for (int i = 0; i < myMap.size(); i++) {
      int key = myMap.getKey(i);
      #e# value = myMap.getValueAt(i);
      assert key > lastKey : i + " " + lastKey + " " + key;
      assert value != lastValue : i + " " + value;
      assert key < size : i + " " + key + " " + size;
      lastKey = key;
      lastValue = value;
    }
    return true;
  }


  /**
   * Remove range from the list, keeping optimal structure
   */
  public void removeRange(int from, int to) {
    assert checkInvariants();

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
    #e# followingValue = 0;

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
          #e# prevValue = prevValueForFindIndex(fi);
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
    assert checkInvariants();
  }

  @NotNull
  public Writable#E#ListIterator iterator(int from, int to) {
    if (from > to || from < 0 || to > size())
      throw new IndexOutOfBoundsException(from + " " + to + " " + this);
    return new SameValuesIterator(from, to);
  }

  public void clear() {
    myMap.clear();
    updateSize(0);
  }

  public int getChangeCount() {
    return myMap.size();
  }

  public int getNextDifferentValueIndex(int curIndex) {
    if (curIndex < 0 || curIndex >= size())
      throw new IndexOutOfBoundsException(curIndex + " " + this);
    int ki = myMap.findKey(curIndex);
    // find key ki for the index from which start the values equal to a[curIndex]
    if(ki < 0) ki = -ki - 2;
   // return index for the next differing value increasing the found key or -1 if the key todo[sank] text
    ++ki;
    return ki < myMap.size() ? myMap.getKey(ki) : -1;
  }


  private final class SameValuesIterator extends WritableIndexIterator {
    private Int#E#Map.Iterator myIterator;
    private #e# myValue;
    private int myNextChangeIndex;

    public SameValuesIterator(int from, int to) {
      super(from, to);
      sync();
    }

    protected void sync() {
      super.sync();
      int p = myMap.findKey(getNextIndex());
      if (p == -1) {
        myValue = 0;
        myIterator = myMap.iterator();
      } else {
        if (p < 0)
          p = -p - 2;
        myIterator = myMap.iterator(p);
        myIterator.next();
        myValue = myIterator.value();
      }
      advanceToNextChange();
    }

    private void advanceToNextChange() {
      if (myIterator.hasNext()) {
        myIterator.next();
        myNextChangeIndex = myIterator.key();
      } else myNextChangeIndex = size();
    }

    public #e# next() throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      if (getNextIndex() >= getTo())
        throw new NoSuchElementException();
      if (getNextIndex() == myNextChangeIndex) {
        myValue = myIterator.value();
        advanceToNextChange();
      }
      setNext(getNextIndex() + 1);
      return myValue;
    }

    public void move(int count) throws ConcurrentModificationException, NoSuchElementException {
      super.move(count);
      if (count != 0)
        sync();
    }
  }
}
