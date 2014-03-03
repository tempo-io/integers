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

import com.almworks.integers.func.IntIntToInt;
import com.almworks.integers.func.IntIntProcedure;
import com.almworks.integers.func.#E#To#E#;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class AbstractWritable#E#List extends Abstract#E#List implements Writable#E#List {
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
  public Writable#E#ListIterator iterator() {
    return iterator(0, size());
  }

  @NotNull
  public Writable#E#ListIterator iterator(int from) {
    return iterator(from, size());
  }

  @NotNull
  public Writable#E#ListIterator iterator(int from, int to) {
    if (from >= to) {
      assert from == to : from + " " + to;
      return #E#Iterator.EMPTY;
    }
    return new WritableIndexIterator(from, to);
  }

  @NotNull public Iterable<Writable#E#ListIterator> write() {
    return #E#Iterables.fromWritableListIterator(iterator());
  }

  public void addAll(#E#List values) {
    if (values == this || values instanceof SubList && ((SubList) values).getParent() == this) {
      int sz = values.size();
      for (int i = 0; i < sz; i++)
        add(values.get(i));
    } else {
      addAll(values.iterator());
    }
  }

  public void addAll(#E#Iterable iterable) {
    for (#E#Iterator it : iterable) {
      add(it.value());
    }
  }

  public void addAll(#e#... values) {
    if (values != null && values.length != 0) {
      if (values.length == 1) {
        add(values[0]);
      } else {
        addAll(new #E#Array(values));
      }
    }
  }

  public boolean remove(#e# value) {
    int index = indexOf(value);
    if (index >= 0) {
      removeAt(index);
      return true;
    }
    return false;
  }

  public #e# removeAt(int index) {
    #e# value = get(index);
    removeRange(index, index + 1);
    return value;
  }

  // todo more effective - we do extra copies
  public boolean removeAll(#e# value) {
    boolean modified = false;
    for (Writable#E#ListIterator ii : write()) {
      if (ii.value() == value) {
        ii.remove();
        modified = true;
      }
    }
    return modified;
  }

  public boolean removeAllSorted(#e# value) {
    int from = binarySearch(value);
    if (from >= 0) {
      int to;
      if (value < #EW#.MAX_VALUE) {
        to = binarySearch(value + 1, from + 1, size());
        if (to < 0)
          to = -to - 1;
      } else to = size();
      assert to > from : from + " " + to;
      removeRange(from, to);
      return true;
    }
    return false;
  }

  /**
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
  public boolean removeAll(#E#Iterable iterable) {
    boolean modified = false;
    for (#E#Iterator ii : iterable) {
      modified |= removeAll(ii.value());
    }
    return modified;
  }

  public boolean removeAll(#e#... values) {
    if (values != null && values.length > 0) {
      return removeAll(new #E#NativeArrayIterator(values));
    }
    return false;
  }

  public void clear() {
    removeRange(0, size());
  }

  public void insert(int index, #e# value) {
    insertMultiple(index, value, 1);
  }

  @Override
  public boolean addSorted(#e# value) {
    int index = binarySearch(value);
    if (index >= 0) return false;
    index = -index - 1;
    insert(index, value);
    return true;
  }

  public void add(#e# value) {
    insertMultiple(size(), value, 1);
  }

  public void insertAll(int index, #E#Iterator iterator) {
    while (iterator.hasNext())
      insert(index++, iterator.nextValue());
  }

  public void insertAll(int index, #E#List list) {
    if (list != null && !list.isEmpty()) {
      insertAll(index, list, 0, list.size());
    }
  }

  public void insertAll(int index, #E#List values, int sourceIndex, int count) {
    insertAll(index, values.iterator(sourceIndex, sourceIndex + count));
  }

  public void set(int index, #e# value) {
    setRange(index, index + 1, value);
  }

  public void setAll(int index, #E#List values) {
    if (values != null && !values.isEmpty())
      setAll(index, values, 0, values.size());
  }

  public void setAll(int index, #E#List values, int sourceOffset, int count) {
    if (count <= 0) return;
    int sz = size();
    checkAddedCount(index, count, sz);
    if (values == this || values instanceof SubList && ((SubList) values).getParent() == this) {
      for (int i = 0, sourceIndex = sourceOffset; i < count; i++, sourceIndex++) {
        set(index + i, values.get(sourceIndex));
      }
      return;
    }
    transfer(values.iterator(sourceOffset, sourceOffset + count), iterator(index, sz), count);
  }

  private void checkAddedCount(int index, int count, int size) {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException(index);
    if (index + count > size)
      throw new ArrayIndexOutOfBoundsException(index + " " + count + " " + size);
  }

  private void transfer(#E#Iterator si, Writable#E#ListIterator di, int count) {
    for (int i = 0; i < count; i++) {
      assert si.hasNext();
      assert di.hasNext();
      di.next();
      di.set(0, si.nextValue());
    }
  }

  public void apply(int from, int to, #E#To#E# function) {
    for (int i = from; i < to; i++)
      set(i, function.invoke(get(i)));
  }

  /**
   * Sorts this list using quicksort copied from {@link java.util.Arrays#sort(int[])} then corrected
   * @param sortAlso lists in which the order is changed as well as this list
   */
  public void sort(final Writable#E#List... sortAlso) {
    if (sortAlso != null) {
      for (Writable#E#List list : sortAlso) {
        assert list.size() == size();
      }
    }
    IntegersUtils.quicksort(size(), new IntIntToInt() {
      public int invoke(int a, int b) {
        return #E#Collections.compare(get(a), get(b));
      }
    }, new IntIntProcedure() {
      public void invoke(int a, int b) {
        swap(a, b);
        if (sortAlso != null)
          for (Writable#E#List list : sortAlso) {
            list.swap(a, b);
          }
      }
    });
  }

  public void sortUnique() {
    sort();
    removeDuplicates();
  }

  public void swap(int index1, int index2) {
    if (index1 != index2) {
      #e# t = get(index1);
      set(index1, get(index2));
      set(index2, t);
    }
  }

  public void removeDuplicates() {
    assert isSorted() : this;
    if (size() < 2) return;
    Writable#E#ListIterator ii = iterator();
    assert ii.hasNext();
    #e# last = ii.nextValue();
    while (ii.hasNext()) {
      #e# next = ii.nextValue();
      assert next >= last : last + " " + next;
      if (next == last) {
        ii.remove();
      } else {
        last = next;
      }
    }
  }

  public void insertMultiple(int index, #e# value, int count) {
    if (count < 0) throw new IllegalArgumentException();
    if (index < 0 || index > size())
      throw new IndexOutOfBoundsException(index + " " + this);
    if (count == 0) return;

    expand(index, count);
    setRange(index, index + count, value);
  }

  public #e# removeLast() {
    int index = size() - 1;
    #e# result = get(index);
    removeAt(index);
    return result;
  }

  public void reverse() {
    int j = size() - 1;
    for (int i = 0; i < j; i++, j--) {
      swap(i,j);
    }
  }

  /** Updates the value in this list at the specified index; if list is currently shorter, it is first appended
   * with {@code defaultValue} up to {@code idx}.
   * @param update the update function to apply. See {@link com.almworks.integers.func.#E#Functions}
   * @return the updated value
   * */
  public #e# update(int idx, #e# defaultValue, #E#To#E# update) {
    if (size() <= idx) insertMultiple(size(), defaultValue, idx - size() + 1);
    #e# updated = update.invoke(get(idx));
    set(idx, updated);
    return updated;
  }

  protected class WritableIndexIterator extends IndexIterator implements Writable#E#ListIterator {
    private int myIterationModCount = myModCount;

    public WritableIndexIterator(int from, int to) {
      super(from, to);
    }

    public boolean hasNext() {
      checkMod();
      int index = getNextIndex();
      return (index < 0) ? -index - 1 < getTo() : index < getTo();
    }

    public #e# get(int relativeOffset) throws NoSuchElementException {
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

    public Writable#E#ListIterator next() throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      setNotRemoved();
      return (Writable#E#ListIterator)super.next();
    }

    public #e# value() throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      if (isJustRemoved())
        throw new IllegalStateException();
      return super.value();
    }

    public boolean hasValue() throws ConcurrentModificationException {
      checkMod();
      return super.hasValue();
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
      AbstractWritable#E#List.this.removeRange(f, t);
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

    public void set(int offset, #e# value) throws NoSuchElementException {
      checkMod();
      if (isJustRemoved())
        throw new IllegalStateException();
      int p = getNextIndex() - 1 + offset;
      if (p < getFrom() || p >= getTo())
        throw new NoSuchElementException();
      AbstractWritable#E#List.this.set(p, value);
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
