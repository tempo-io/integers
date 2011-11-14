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

package com.almworks.integers;

import com.almworks.integers.func.#E#Function;
// function on indices, hence int
import com.almworks.integers.func.IntFunction2;
// function on indices, hence int
import com.almworks.integers.func.IntProcedure2;
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

  public void addAll(#E#List values) {
    if (values == this || values instanceof SubList && ((SubList) values).getParent() == this) {
      int sz = values.size();
      for (int i = 0; i < sz; i++)
        add(values.get(i));
    } else {
      addAll(values.iterator());
    }
  }

  public void addAll(#E#Iterator iterator) {
    while (iterator.hasNext())
      add(iterator.next());
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

  public void removeAll(#e# value) {
    for (Writable#E#ListIterator ii = iterator(); ii.hasNext();) {
      if (ii.next() == value) {
        ii.remove();
      }
    }
  }

  /**
   * Removes all appearances of value if this collection is sorted
   */
  public void removeAllSorted(#e# value) {
    int from = binarySearch(value);
    if (from >= 0) {
      int to;
      if (value < #EW#.MAX_VALUE) {
        to = binarySearch((#e#)(value + 1), from + 1, size());
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
  public void removeAll(#E#List collection) {
    for (#E#Iterator ii = collection.iterator(); ii.hasNext();)
      removeAll(ii.next());
  }

  public void removeAll(#e#... values) {
    if (values != null && values.length > 0) {
      removeAll(new #E#Array(values));
    }
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
      insert(index++, iterator.next());
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
      di.set(0, si.next());
    }
  }

  public void apply(int from, int to, #E#Function function) {
    for (int i = from; i < to; i++)
      set(i, function.invoke(get(i)));
  }

  public void sort(final Writable#E#List ... sortAlso) {
    if (sortAlso != null) {
      for (Writable#E#List list : sortAlso) {
        assert list.size() == size();
      }
    }
    IntegersUtils.quicksort(size(), new IntFunction2() {
      public int invoke(int a, int b) {
        return #E#Collections.compare(get(a), get(b));
      }
    }, new IntProcedure2() {
      public void invoke(int a, int b) {
        swap(a, b);
        if (sortAlso != null)
          for (Writable#E#List list : sortAlso) {
            list.swap(a, b);
          }
      }
    });
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
    #e# last = ii.next();
    while (ii.hasNext()) {
      #e# next = ii.next();
      assert next >= last : last + " " + next;
      if (next == last) {
        ii.remove();
      } else {
        last = next;
      }
    }
  }

  public void insertMultiple(int index, #e# value, int count) {
    if (count <= 0)
      return;
    expand(index, count);
    setRange(index, index + count, value);
  }

  public #e# removeLast() {
    int index = size() - 1;
    #e# result = get(index);
    removeAt(index);
    return result;
  }

  public void reverseInPlace() {
    int j = size() - 1;
    for (int i = 0; i < j; i++, j--) swap(i,j);
  }

  protected class WritableIndexIterator extends IndexIterator implements Writable#E#ListIterator {
    private int myIterationModCount = myModCount;

    public WritableIndexIterator(int from, int to) {
      super(from, to);
    }

    public boolean hasNext() {
      checkMod();
      return super.hasNext();
    }

    public #e# get(int relativeOffset) throws NoSuchElementException {
      checkMod();
      return super.get(relativeOffset);
    }

    public int lastIndex() {
      checkMod();
      return super.lastIndex();
    }

    public #e# next() throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      return super.next();
    }

    public void move(int count) throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      super.move(count);
    }

    public void removeRange(int fromOffset, int toOffset) throws NoSuchElementException {
      checkMod();
      if (fromOffset >= toOffset) {
        assert fromOffset == toOffset : fromOffset + " " + toOffset + " " + this;
        return;
      }
      int f = getNextIndex() - 1 + fromOffset;
      int t = getNextIndex() - 1 + toOffset;
      if (f < getFrom() || t > getTo())
        throw new NoSuchElementException(fromOffset + " " + toOffset + " " + this);
      AbstractWritable#E#List.this.removeRange(f, t);
      setNext(f);
      decrementTo(t - f);
      sync();
    }

    protected void sync() {
      myIterationModCount = myModCount;
    }

    public void remove() {
      checkMod();
      int p = getNextIndex() - 1;
      if (p < getFrom())
        throw new NoSuchElementException(String.valueOf(this));
      assert getNextIndex() <= getTo() : getNextIndex() + " " + getTo();
      removeAt(p);
      setNext(p);
      decrementTo(1);
      sync();
    }

    public void set(int offset, #e# value) throws NoSuchElementException {
      checkMod();
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
  }
}
