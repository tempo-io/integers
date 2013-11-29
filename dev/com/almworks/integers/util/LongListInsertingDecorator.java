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

// CODE GENERATED FROM com/almworks/integers/util/PListInsertingDecorator.tpl


package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class LongListInsertingDecorator extends AbstractLongListDecorator {
  private final IntLongMap myInserted;

  public LongListInsertingDecorator(LongList base, IntLongMap inserted) {
    super(base);
    myInserted = inserted;
  }

  public LongListInsertingDecorator(LongList base) {
    this(base, new IntLongMap());
  }

  public int size() {
    return base().size() + myInserted.size();
  }

  public long get(int index) {
    int idx = findInsertion(index);
    if (idx >= 0)
      return myInserted.getValueAt(idx);
    idx = -idx - 1;
    return base().get(index - idx);
  }

  private int findInsertion(int index) {
    return myInserted.findKey(index);
  }

  private int insertedBefore(int index) {
    int res = findInsertion(index);
    return (res >= 0) ? res : -res - 1;
  }

  public boolean isEmpty() {
    return myInserted.isEmpty() && base().isEmpty();
  }

  @NotNull
  public LongListIterator iterator(int from, int to) {
    return new LocalIterator(from, to);
  }

  // todo refactor
  public boolean iterate(int from, int to, LongVisitor visitor) {
    int idx = findInsertion(from);
    boolean iterateBase;
    if (idx < 0) {
      idx = -idx - 1;
      iterateBase = true;
    } else {
      iterateBase = false;
    }
    int count = myInserted.size();
    while (from < to) {
      if (iterateBase) {
        int limit = idx < count ? myInserted.getKeyAt(idx) - idx : base().size();
        limit = Math.min(limit, to - idx);
        if (!iterateBase(from - idx, limit, visitor))
          return false;
        from = limit + idx;
      }
      while (idx < count && from < to && from == myInserted.getKeyAt(idx)) {
        if (!visitor.accept(myInserted.getValueAt(idx), this))
          return false;
        idx++;
        from++;
      }
      iterateBase = true;
    }
    return true;
  }

  public void insert(int index, long value) {
    int idx = insertedBefore(index);
    myInserted.adjustKeys(idx, myInserted.size(), 1);
    assert !myInserted.containsKey(index) : index + " " + myInserted;
    myInserted.insertAt(idx, index, value);
  }

  public int getInsertCount() {
    return myInserted.size();
  }

  public IntIterator insertIndexIterator() {
    return myInserted.keysIterator(0, myInserted.size());
  }

  public LongIterator insertValueIterator() {
    return myInserted.valuesIterator(0, myInserted.size());
  }

  public int getNewIndex(int baseIndex) {
    int idx = insertedBefore(baseIndex);
    for (IntListIterator ii = myInserted.keysIterator(idx, myInserted.size()); ii.hasNext(); idx++) {
      if (ii.nextValue() - idx > baseIndex)
        break;
    }
    return baseIndex + idx;
  }

  private class LocalIterator extends AbstractLongListIndexIterator {
    private LongListIterator myBaseIterator;
    private IntLongIterator myInsertedIterator;
    // position of the myInsertedIterator
    private int myCurInsert = -1;

    private LocalIterator(int from, int to) {
      super(from, to);
      int idx = insertedBefore(from);
      myBaseIterator = base().iterator(from - idx);
      updateMyInsertedIterator(idx);
    }

    /**
     * set the position of the myInsertedIterator to the {@code idx}
     */
    private void updateMyInsertedIterator(int idx) {
      myInsertedIterator = myInserted.iterator(idx);
      advanceToNextInsert();
    }

    /**
     * switch the myInsertedIterator to the next insert and update myCurInsert
     */
    private void advanceToNextInsert() {
      if (myInsertedIterator.hasNext()) {
        myInsertedIterator.next();
        myCurInsert = myInsertedIterator.left();
      } else myCurInsert = -1;
    }

    public boolean hasNext() {
      boolean r = super.hasNext();
      assert !r || (myCurInsert >= 0 || myInsertedIterator.hasNext() || myBaseIterator.hasNext()) : this;
      return r;
    }

    public LongListIterator next() throws NoSuchElementException {
      if (myCurInsert >= 0 && myCurInsert == getNextIndex() - 1) {
        // if current index on inserted value
        advanceToNextInsert();
      } else {
        if (myBaseIterator.hasNext()) {
          myBaseIterator.next();
        }
      }
      super.next();
      return this;
    }

    public long value() throws NoSuchElementException {
      if (!hasValue())
        throw new NoSuchElementException();
      if (myCurInsert >= 0 && myCurInsert == getNextIndex() - 1) {
        // if current index on inserted value
        return myInsertedIterator.right();
      } else {
        return myBaseIterator.value();
      }
    }

    public void move(int count) throws ConcurrentModificationException, NoSuchElementException {
      int idx0 = insertedBefore(getNextIndex() - 1);
      super.move(count);
      int idx = insertedBefore(getNextIndex() - 1);
      // (idx0 - idx1) - count of insertions between old getNextIndex() and new getNextIndex()
      myBaseIterator.move(count + idx0 - idx);
      updateMyInsertedIterator(idx);
    }

    protected long absget(int index) {
      return LongListInsertingDecorator.this.get(index);
    }
  }
}

