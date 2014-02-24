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

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntegersUtils.arrayCopy;

/**
 * Removing decorator for a native int list.
 */
public abstract class AbstractLongListRemovingDecorator extends AbstractLongListDecorator {
  protected AbstractLongListRemovingDecorator(LongList base) {
    super(base);
  }

  protected abstract IntList getRemovedPrepared();

  public int size() {
    return Math.max(0, base().size() - getRemovedPrepared().size());
  }

  public long get(int index) {
    return base().get(index + removedBefore(index));
  }

  public boolean iterate(int from, int to, LongVisitor visitor) {
    IntList removedPrepared = getRemovedPrepared();
    int idx = removedBefore(from);
    int left = to - from;
    while (left > 0) {
      if (idx >= removedPrepared.size() || removedPrepared.get(idx) > from + left)
        return iterateBase(from + idx, from + idx + left, visitor);
      to = removedPrepared.get(idx);
      if (!iterateBase(from + idx, to + idx, visitor))
        return false;
      int processed = to - from;
      left -= processed;
      from += processed;
      while (idx < removedPrepared.size() && removedPrepared.get(idx) == to)
        idx++;
    }
    return true;
  }

  /**
   * @return count of elements, removed before {@code index}
   */
  protected final int removedBefore(int index) {
    int idx = getRemovedPrepared().binarySearch(index + 1);
    if (idx < 0)
      return -idx - 1;
    return idx;
  }

  @NotNull
  public LongListIterator iterator(int from, int to) {
    return new LocalIterator(from, to);
  }

  public IntListIterator removedIndexIterator() {
    return new RemovedIndexIterator(getRemovedPrepared().iterator());
  }

  public LongIterator removedValueIterator() {
    return new LongListIndexedIterator(base(), removedIndexIterator());
  }

  public int getRemoveCount() {
    return getRemovedPrepared().size();
  }

  public boolean isRemovedAt(int baseIndex) {
    return getNewIndex(baseIndex) < 0;
  }

  public int getNewIndex(int baseIndex) {
    IntList removedPrepared = getRemovedPrepared();
    int size = removedPrepared.size();
    if (size == 0)
      return baseIndex;
    int idx = removedPrepared.binarySearch(baseIndex);
    if (idx < 0)
      idx = -idx - 1;
    if (idx >= size)
      idx = size - 1;
    while (idx >= 0) {
      int v = removedPrepared.get(idx);
      int removed = v + idx;
      if (removed == baseIndex)
        return -1;
      if (removed < baseIndex)
        return v + (baseIndex - removed) - 1;
      idx--;
    }
    return baseIndex;
  }

  /**
   * Prepares the given sorted indices array to be used by the decorator.
   * @param indices indices array, must be sorted, must not contain duplicates.
   */
  protected static void prepareSortedIndicesInternal(WritableIntList indices) {
    int i = 1;
    int last = 0;
    // todo apply "-i" when sortedRemoveIndexes are collected
    for (WritableIntListIterator ii = indices.iterator(1); ii.hasNext();) {
      int value = ii.nextValue();
      assert value > last : i + " " + last + " " + value;
      last = value;
      ii.set(0, value - i);
      i++;
    }
  }

  /**
   * For the given list of remove indices (may be empty, unsorted, or contain duplicates), creates a prepared list of remove indices ready to be used by implementations of this class.
   * @param removeIndices remove indices
   * @return
   */
  protected static IntArray prepareUnsortedIndicesInternal(int... removeIndices) {
    int[] correctRemove = arrayCopy(removeIndices);
    Arrays.sort(correctRemove);
    int dupCount = 0;
    int prev = 0;
    for (int i = 0; i < correctRemove.length; i++) {
      if (i > 0 && prev == correctRemove[i])
        dupCount++;
      else {
        prev = correctRemove[i];
        correctRemove[i - dupCount] = prev - (i - dupCount);
      }
    }
    return new IntArray(correctRemove, correctRemove.length - dupCount);
  }

  private class LocalIterator extends AbstractLongListIndexIterator {
    private LongListIterator myBaseIterator;
    private int myNextRemovedIndex;

    private LocalIterator(int from, int to) {
      super(from, to);
      int count = removedBefore(from);
      // super iterator checks borders, so we can suppose that from and to are correct.
      myBaseIterator = base().iterator();
      int offset = from + count;
      if (offset != 0) {
        myBaseIterator.move(offset);
      }
      updateMyNextRemovedIndex(count);
    }

    /**
     * Converts {@code count} in removed indices to the corresponding position in myBaseList
     * and sets the obtained value to the myNextRemovedIndex
     */
    private void updateMyNextRemovedIndex(int count) {
      assert count >= 0;
      if (count < getRemoveCount()) {
        myNextRemovedIndex = getRemovedPrepared().get(count) + count;
      } else {
        myNextRemovedIndex = -1;
      }
    }

    public LongListIterator next() throws ConcurrentModificationException, NoSuchElementException {
      super.next();
      myBaseIterator.next();
      if (myBaseIterator.index() == myNextRemovedIndex) {
        updateBaseIterator();
      }
      return this;
    }

    /**
     * Updates position of myBaseIterator in accordance with this iterator position.
     */
    private void updateBaseIterator() {
      int count = removedBefore(index());
      int offset = index() + count - (myBaseIterator.hasValue() ? myBaseIterator.index() : -1);
      myBaseIterator.move(offset);
      updateMyNextRemovedIndex(count);
    }

    public long value() {
      if (!hasValue()) {
        throw new NoSuchElementException();
      }
      return myBaseIterator.value();
    }

    public boolean hasNext() {
      return getNextIndex() < getTo() && myBaseIterator.hasNext();
    }

    public void move(int count) throws ConcurrentModificationException, NoSuchElementException {
      super.move(count);
      if (count != 0) {
        updateBaseIterator();
      }
    }

    protected long absget(int index) {
      if (index == index()) {
        return value();
      } else {
        return AbstractLongListRemovingDecorator.this.get(index);
      }
    }
  }
}
