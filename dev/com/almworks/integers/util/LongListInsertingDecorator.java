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

/**
 * Inserting decorator for {@code LongList} that designed
 * to accumulate insertions into a very large list and then use it as a read-only list.
 * Accumulation should be done with {@link #insert(int, long)}
 * <br>Also underline that the insertion index is in the new list, not the old one.
 * The benefit is that we don't copy elements around while inserting.
 * We expect the amount of insertions to be much smaller then the size of the list.
 */
public class LongListInsertingDecorator extends AbstractLongListDecorator {
  private final IntLongMap myInserted;

  public LongListInsertingDecorator(LongList base) {
    this(base, new IntLongMap());
  }

  /**
   * Creates a new decorator that decorates the specified insertions into the specified base list.
   * {@code inserted} maps indices of insertion to the inserted values,
   * where each index is interpreted in the list that results from inserting previous elements (with lesser indices.)
   * Note that in most cases you'll want to use {@link #LongListInsertingDecorator(com.almworks.integers.LongList)}
   * which constructs a decorator without any insertions and
   * add elements through {@link #insert(int, long)}. Use this method if you have previously collected the insertions.
   * <br>Examples:
   * <br>decorator( [2, 8],    [(0, 0), (2, 4), (3, 6)] ) -> [0, 2, 4, 6, 8]
   * <br>decorator( [2, 8, 9], [(2, 4)] ) -> [2, 8, 4, 9]
   * <br>decorator( [3, 9],    [(0, 2), (3, 0)] ) -> [2, 3, 9, 0]
   * <br>decorator( [0, 4],    [(5, 10)] ) -> IllegalArgumentException
   * @param inserted map in which keys represent insertion points and values the inserted values
   * @throws IllegalArgumentException if first key in {@code inserted} less than 0 or
   * last key in more than {@code base.size() + inserted.size()}
   */
  public LongListInsertingDecorator(LongList base, IntLongMap inserted) throws IllegalArgumentException {
    super(base);
    if (inserted.size() != 0) {
      if (inserted.getKeyAt(0) < 0) {
        throw new IllegalArgumentException("first key in inserted < 0.");
      }
      int key = inserted.getKeyAt(inserted.size() - 1);
      if (key >= base.size() + inserted.size()) {
        throw new IllegalArgumentException("last key in inserted too big: " + key);
      }
    }
    myInserted = inserted;
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

  /**
   * @return count of elements inserted before {@code index} inclusively
   */
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
    int idx = findInsertion(baseIndex);
    if (idx < 0) {
      idx = -idx - 1;
    }
    for (IntIterator ii : myInserted.keysIterator(idx, myInserted.size())) {
      if (ii.value() - idx > baseIndex) {
        break;
      }
      idx++;
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
      sync();
    }

    private void sync() {
      int curIndex = getNextIndex() - 1;
      int insertIdx = insertedBefore(curIndex);

      myInsertedIterator = myInserted.iterator(insertIdx);
      advanceToNextInsert();

      int baseIdx = curIndex - insertIdx;
      if (baseIdx < 0) {
        myBaseIterator = base().iterator();
      } else {
        myBaseIterator = base().iterator(baseIdx);
        advanceToNextBase();
      }
    }

    private void advanceToNextBase() {
      if (myCurInsert != getNextIndex() - 1) {
        assert myBaseIterator.hasNext();
        myBaseIterator.next();
      }
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
      super.next();
      if (myCurInsert == index() - 1) {
        advanceToNextInsert();
      }
      advanceToNextBase();
      return this;
    }

    public long value() throws NoSuchElementException {
      if (!hasValue()) {
        throw new NoSuchElementException();
      }
      if (myCurInsert == index()) {
        // if current index on inserted value
        return myInsertedIterator.right();
      } else {
        return myBaseIterator.value();
      }
    }

    public void move(int count) throws ConcurrentModificationException, NoSuchElementException {
      if (count == 0) return;
      super.move(count);
      sync();
    }

    protected long absget(int index) {
      return LongListInsertingDecorator.this.get(index);
    }
  }
}

