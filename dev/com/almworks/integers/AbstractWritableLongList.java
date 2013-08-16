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

// CODE GENERATED FROM com/almworks/integers/AbstractWritablePList.tpl


package com.almworks.integers;

import com.almworks.integers.func.IntFunction;
import com.almworks.integers.func.LongFunction;
// function on indices, hence int
import com.almworks.integers.func.IntFunction2;
// function on indices, hence int
import com.almworks.integers.func.IntProcedure2;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class AbstractWritableLongList extends AbstractLongList implements WritableLongList {
  private transient int myModCount;
  private int mySize;

  protected final void modified() {
    myModCount++;
  }

  protected final int modCount() {
    return myModCount;
  }

  public final int size() {
    return mySize;
  }

  protected void updateSize(int size) {
    if (mySize != size) {
      mySize = size;
      modified();
    }
  }

  @NotNull
  public WritableLongListIterator iterator() {
    return iterator(0, size());
  }

  @NotNull
  public WritableLongListIterator iterator(int from) {
    return iterator(from, size());
  }

  @NotNull
  public WritableLongListIterator iterator(int from, int to) {
    if (from >= to) {
      assert from == to : from + " " + to;
      return LongIterator.EMPTY;
    }
    return new WritableIndexIterator(from, to);
  }

  @NotNull public Iterable<WritableLongListIterator> write() {
    return LongIterables.fromWritableListIterator(iterator());
  }

  public void addAll(LongList values) {
    if (values == this || values instanceof SubList && ((SubList) values).getParent() == this) {
      int sz = values.size();
      for (int i = 0; i < sz; i++)
        add(values.get(i));
    } else {
      addAll(values.iterator());
    }
  }

  public void addAll(LongIterator iterator) {
    while (iterator.hasNext())
      add(iterator.nextValue());
  }

  public void addAll(long... values) {
    if (values != null && values.length != 0) {
      if (values.length == 1) {
        add(values[0]);
      } else {
        addAll(new LongArray(values));
      }
    }
  }

  public boolean remove(long value) {
    int index = indexOf(value);
    if (index >= 0) {
      removeAt(index);
      return true;
    }
    return false;
  }

  public long removeAt(int index) {
    long value = get(index);
    removeRange(index, index + 1);
    return value;
  }

  public void removeAll(long value) {
    for (WritableLongListIterator ii : write()) {
      if (ii.value() == value) {
        ii.remove();
      }
    }
  }

  /**
   * Removes all appearances of value if this collection is sorted
   */
  public void removeAllSorted(long value) {
    int from = binarySearch(value);
    if (from >= 0) {
      int to;
      if (value < Long.MAX_VALUE) {
        to = binarySearch((long)(value + 1), from + 1, size());
        if (to < 0)
          to = -to - 1;
      } else to = size();
      assert to > from : from + " " + to;
      removeRange(from, to);
    }
  }

  /**
   * Removes all values contained in collection.
   * <p/>
   * Method 1: iterate through this, lookup collection
   * Cost 1: N*cost(lookup(R))
   * Method 2: iterate through collection, lookup this
   * Cost 2: R*cost(lookup(N))
   * Method 3: iterate through sorted this and sorted collection
   * Cost 3: max(R, N)
   * <p/>
   * // todo something effective
   */
  public void removeAll(LongList collection) {
    for (LongIterator ii : collection)
      removeAll(ii.value());
  }

  public void removeAll(long... values) {
    if (values != null && values.length > 0) {
      removeAll(new LongArray(values));
    }
  }

  public void clear() {
    removeRange(0, size());
  }

  public void insert(int index, long value) {
    insertMultiple(index, value, 1);
  }

  @Override
  public boolean addSorted(long value) {
    int index = binarySearch(value);
    if (index >= 0) return false;
    index = -index - 1;
    insert(index, value);
    return true;
  }

  public void add(long value) {
    insertMultiple(size(), value, 1);
  }

  public void insertAll(int index, LongIterator iterator) {
    while (iterator.hasNext())
      insert(index++, iterator.nextValue());
  }

  public void insertAll(int index, LongList list) {
    if (list != null && !list.isEmpty()) {
      insertAll(index, list, 0, list.size());
    }
  }

  public void insertAll(int index, LongList values, int sourceIndex, int count) {
    insertAll(index, values.iterator(sourceIndex, sourceIndex + count));
  }

  public void set(int index, long value) {
    setRange(index, index + 1, value);
  }

  public void setAll(int index, LongList values) {
    if (values != null && !values.isEmpty())
      setAll(index, values, 0, values.size());
  }

  public void setAll(int index, LongList values, int sourceOffset, int count) {
    if (count <= 0) return;
    int sz = size();
    checkAddedCount(index, count, sz);
    transfer(values.iterator(sourceOffset, sourceOffset + count), iterator(index, sz), count);
  }

  private void checkAddedCount(int index, int count, int size) {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException(index);
    if (index + count > size)
      throw new ArrayIndexOutOfBoundsException(index + " " + count + " " + size);
  }

  private void transfer(LongIterator si, WritableLongListIterator di, int count) {
    for (int i = 0; i < count; i++) {
      assert si.hasNext();
      assert di.hasNext();
      di.next();
      di.set(0, si.nextValue());
    }
  }

  public void apply(int from, int to, LongFunction function) {
    for (int i = from; i < to; i++)
      set(i, function.invoke(get(i)));
  }

  public void sort(final WritableLongList ... sortAlso) {
    if (sortAlso != null) {
      for (WritableLongList list : sortAlso) {
        assert list.size() == size();
      }
    }
    IntegersUtils.quicksort(size(), new IntFunction2() {
      public int invoke(int a, int b) {
        return LongCollections.compare(get(a), get(b));
      }
    }, new IntProcedure2() {
      public void invoke(int a, int b) {
        swap(a, b);
        if (sortAlso != null)
          for (WritableLongList list : sortAlso) {
            list.swap(a, b);
          }
      }
    });
  }

  /**
   * @param sortAlso ties in this array are broken via elements of this array. Must not be shorter than {@code a1}
   * @throws IllegalArgumentException in case the second array is shorter than the first
   * */
  public void sortByFirstThenBySecond(final WritableLongList sortAlso) {
    if (size() > sortAlso.size()) throw new IllegalArgumentException("This array is longer than sortAlso: " +
        size() + " > " + sortAlso.size());
    IntegersUtils.quicksort(size(), new IntFunction2() {
          @Override
          public int invoke(int a, int b) {
            int comp = LongCollections.compare(get(a), get(b));
            if (comp == 0) comp = LongCollections.compare(sortAlso.get(a), sortAlso.get(b));
            return comp;
          }
        },
        new IntProcedure2() {
          @Override
          public void invoke(int a, int b) {
            swap(a, b);
            sortAlso.swap(a, b);
          }
        });
  }

  public void swap(int index1, int index2) {
    if (index1 != index2) {
      long t = get(index1);
      set(index1, get(index2));
      set(index2, t);
    }
  }

  public void removeDuplicates() {
    assert isSorted() : this;
    if (size() < 2) return;
    WritableLongListIterator ii = iterator();
    assert ii.hasNext();
    long last = ii.nextValue();
    while (ii.hasNext()) {
      long next = ii.nextValue();
      assert next >= last : last + " " + next;
      if (next == last) {
        ii.remove();
      } else {
        last = next;
      }
    }
  }

  public void insertMultiple(int index, long value, int count) {
    if (count <= 0)
      return;
    expand(index, count);
    setRange(index, index + count, value);
  }

  public long removeLast() {
    int index = size() - 1;
    long result = get(index);
    removeAt(index);
    return result;
  }

  public void reverse() {
    int j = size() - 1;
    for (int i = 0; i < j; i++, j--) swap(i,j);
  }

  /** Updates the value in the specified list at the specified index; if the list is currently shorter, it is first appended
   * with {@code defaultValue} up to the {@code idx}.
   * @param update the update function to apply. See {@link com.almworks.integers.func.IntFunctions}
   * @return the updated value
   * */
  public long update(int idx, long defaultValue, LongFunction update) {
    if (size() <= idx) insertMultiple(size(), defaultValue, idx - size() + 1);
    long updated = update.invoke(get(idx));
    set(idx, updated);
    return updated;
  }


  protected class WritableIndexIterator extends IndexIterator implements WritableLongListIterator {
    private int myIterationModCount = myModCount;

    public WritableIndexIterator(int from, int to) {
      super(from, to);
    }

    public boolean hasNext() {
      checkMod();
      int index = getNextIndex();
      return (index < 0) ? -index - 1 < getTo() : index < getTo();
    }

    public long get(int relativeOffset) throws NoSuchElementException {
      checkMod();
      if (isJustRemoved())
        throw new IllegalStateException();
      return super.get(relativeOffset);
    }

    public int index() throws NoSuchElementException {
      checkMod();
      if (isJustRemoved())
        throw new IllegalStateException();
      return super.index();
    }

    public WritableLongListIterator next() throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      setNotRemoved();
      return (WritableLongListIterator)super.next();
    }

    public long value() throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      if (isJustRemoved())
        throw new IllegalStateException();
      return super.value();
    }

    public void move(int count) throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      if (isJustRemoved())
        throw new IllegalStateException();
      super.move(count);
    }

    public void removeRange(int fromOffset, int toOffset) throws NoSuchElementException {
      checkMod();
      if (isJustRemoved())
        throw new IllegalStateException();
      if (fromOffset >= toOffset) {
        assert fromOffset == toOffset : fromOffset + " " + toOffset + " " + this;
        return;
      }
      int curPos = getNextIndex() - 1;
      int f = curPos + fromOffset;
      int t = curPos + toOffset;
      if (f < getFrom() || t > getTo())
        throw new NoSuchElementException(fromOffset + " " + toOffset + " " + this);
      AbstractWritableLongList.this.removeRange(f, t);
      setNext(f);
      decrementTo(t - f);
      sync();
      setJustRemoved();
    }

    protected void sync() {
      myIterationModCount = myModCount;
    }

    public void remove() {
      checkMod();
      if (isJustRemoved())
        throw new IllegalStateException();
      int p = getNextIndex() - 1;
      if (p < getFrom())
        throw new NoSuchElementException(String.valueOf(this));
      assert getNextIndex() <= getTo() : getNextIndex() + " " + getTo();
      removeAt(p);
      setNext(p);
      decrementTo(1);
      sync();
      setJustRemoved();
    }

    public void set(int offset, long value) throws NoSuchElementException {
      checkMod();
      if (isJustRemoved())
        throw new IllegalStateException();
      int p = getNextIndex() - 1 + offset;
      if (p < getFrom() || p >= getTo())
        throw new NoSuchElementException();
      AbstractWritableLongList.this.set(p, value);
      sync();
    }

    protected void checkMod() {
      if (myIterationModCount != myModCount)
        throw new ConcurrentModificationException(myIterationModCount + " " + myModCount);
    }

    protected boolean isJustRemoved() {
      return getNextIndex() < 0;
    }

    protected void setJustRemoved() {
      int next = getNextIndex();
      if (next >= 0)
        setNext(-next - 1);
    }

    protected void setNotRemoved() {
      int next = getNextIndex();
      if (next < 0)
        setNext(-next - 1);
    }
  }
}
