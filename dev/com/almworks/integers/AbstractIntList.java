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

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.almworks.integers.IntegersUtils.EMPTY_INTS;

public abstract class AbstractIntList implements IntList {
  public boolean isEmpty() {
    return size() == 0;
  }

  public StringBuilder toString(StringBuilder builder) {
    builder.append(IntegersUtils.substringAfterLast(getClass().getName(), ".")).append(" ").append(size()).append(" [");
    String sep = "";
    for  (IntIterator ii : this) {
      builder.append(sep).append(ii.value());
      sep = ",";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }

  @NotNull
  public IntListIterator iterator() {
    return iterator(0, size());
  }

  @NotNull
  public IntListIterator iterator(int from) {
    return iterator(from, size());
  }

  @NotNull
  public IntListIterator iterator(int from, int to) {
    if (from >= to) {
      assert from == to : from + " " + to;
      return IntIterator.EMPTY;
    }
    return new IndexIterator(from, to);
  }

  public int indexOf(int value) {
    int i = 0;
    for (IntIterator ii : this) {
      if (ii.value() == value) return i;
      i++;
    }
    return -1;
  }

  public boolean contains(int value) {
    return indexOf(value) >= 0;
  }

  public int[] toArray(int startIndex, int[] dest, int destOffset, int length) {
    if (length > 0) {
      IntIterator ii = iterator(startIndex, size());
      int e = destOffset + length;
      for (int i = destOffset; i < e; i++) {
        assert ii.hasNext();
        dest[i] = ii.nextValue();
      }
    }
    return dest;
  }

  public IntList subList(int from, int to) {
    int sz = size();
    if (from < 0 || from > sz || to < 0 || to > sz || to < from)
      throw new IndexOutOfBoundsException(from + " " + to + " " + sz);
    if (from == 0 && to == sz)
      return this;
    else
      return new SubList(this, from, to);
  }

  public int[] toNativeArray() {
    int size = size();
    if (size == 0) return EMPTY_INTS;
    return toArray(0, new int[size], 0, size);
  }

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof IntList))
      return false;
    IntList that = (IntList) o;
    if (size() != that.size())
      return false;
    IntIterator ii1 = iterator();
    IntIterator ii2 = that.iterator();
    while (ii1.hasNext() && ii2.hasNext()) {
      if (ii1.nextValue() != ii2.nextValue())
        return false;
    }
    return !(ii1.hasNext() || ii2.hasNext());
  }

  public int hashCode() {
    int hashCode = 1;
    for (IntIterator ii : this) {
      hashCode = 31 * hashCode + (int)ii.value();
    }
    return hashCode;
  }

  /**
   * Returns index of the first value in the list
   */
  public int binarySearch(int value) {
    return binarySearch(value, 0, size());
  }

  protected boolean checkSorted(boolean checkUnique) {
    IntIterator it = iterator();
    if (!it.hasNext()) return true;
    int prev = it.nextValue();
    while (it.hasNext()) {
      int next = it.nextValue();
      if (next < prev || (checkUnique && next == prev)) return false;
      prev = next;
    }
    return true;
  }

  @Override
  public boolean isUniqueSorted() {
    return checkSorted(true);
  }

  @Override
  public boolean isSorted() {
    return checkSorted(false);
  }

  public int binarySearch(int value, int from, int to) {
    int size = size();
    if (from < 0 || from > size || to < 0 || to > size || from > to)
      throw new IndexOutOfBoundsException(from + " " + to + " " + size + " " + value);
    int comp;
    if (from == to) {
      return -from - 1;
    }
    int low = from;
    int high = to;
    boolean found = false;

    comp = IntCollections.compare(value, get(from));
    if (comp == 0)
      return from;
    else if (comp < 0)
      return -from - 1;
    if (to - from == 1)
      return -to - 1;
    comp = IntCollections.compare(get(to - 1), value);
    if (comp < 0) {
      return -to - 1;
    } else if (comp == 0) {
      // skip to the earliest appearance of value
      low = to - 1;
      high = low - 1;
      found = true;
    }

    while (low <= high) {
      int mid = (low + high) >> 1;
      comp = IntCollections.compare(get(mid), value);
      if (comp < 0) {
        low = mid + 1;
      } else if (comp > 0) {
        high = mid - 1;
      } else {
        low = mid;
        found = true;
        break;
      }
    }
    if (!found) {
      return -(low + 1);
    }
    if (low > from && get(low - 1) == value) {
      // search back for the beginning of value, similar to binary search

      // lowest index known to contain value (high limit for search)
      int higheq = low - 1;

      // lowest index that *may* contain value (can't == from -- checked at the beginning)
      int loweq = from + 1;
      while (higheq > loweq) {
        int p = (higheq + loweq) >> 1;
        int v = get(p);
        if (v == value) {
          higheq = p;
        } else {
          assert v < value : v + " " + value;
          loweq = p + 1;
        }
      }
      return higheq;
    }
    return low;
  }

  public int getNextDifferentValueIndex(int curIndex) {
    if (curIndex < 0 || curIndex >= size())
      throw new IndexOutOfBoundsException(curIndex + " " + this);
    for (int i = curIndex + 1; i < size(); ++i) {
      if (get(i) != get(curIndex)) {
        return i;
      }
    }
    return size();
  }

  public int getLast(int backwardIndex) {
    return get(size() - backwardIndex - 1);
  }

  protected class IndexIterator extends AbstractIntListIndexIterator {
    public IndexIterator(int from, int to) {
      super(from, to);
    }

    protected int absget(int index) {
      return AbstractIntList.this.get(index);
    }
  }

  @Override
  public List<Integer> toList() {
    List<Integer> list = IntegersUtils.arrayList();
    for(IntIterator ii : AbstractIntList.this) {
      list.add(ii.value());
    }
    return list;
  }

  protected static class SubList extends AbstractIntList {
    private final AbstractIntList myParent;
    private final int myFrom;
    private final int myTo;

    public SubList(AbstractIntList parent, int from, int to) {
      assert to >= from : from + " " + to;
      myParent = parent;
      myFrom = from;
      myTo = to;
    }

    public int size() {
      return myTo - myFrom;
    }

    public int get(int index) {
      if (index < 0)
        throw new ArrayIndexOutOfBoundsException(index);
      index += myFrom;
      if (index >= myTo)
        throw new ArrayIndexOutOfBoundsException(index - myFrom);
      return myParent.get(index);
    }

    public IntList subList(int from, int to) {
      int sz = size();
      if (from < 0 || from > sz || to < 0 || to > sz || to < from)
        throw new IndexOutOfBoundsException(from + " " + to + " " + sz);
      if (from == 0 && to == sz)
        return this;
      else
        return new SubList(myParent, myFrom + from, myFrom + to);
    }

    public AbstractIntList getParent() {
      return myParent;
    }

    public int getFrom() {
      return myFrom;
    }

    public int getTo() {
      return myTo;
    }
  }
}
