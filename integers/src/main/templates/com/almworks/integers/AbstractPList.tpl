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

import java.util.List;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntegersUtils.EMPTY_#EC#S;

public abstract class Abstract#E#List implements #E#List {
  public boolean isEmpty() {
    return size() == 0;
  }

  public StringBuilder toString(StringBuilder builder) {
    builder.append(IntegersUtils.substringAfterLast(getClass().getName(), ".")).append(" ").append(size()).append(" [");
    String sep = "";
    for  (#E#Iterator ii : this) {
      builder.append(sep).append(ii.value());
      sep = ", ";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }

  @NotNull
  public #E#ListIterator iterator() {
    return iterator(0, size());
  }

  @NotNull
  public #E#ListIterator iterator(int from) {
    return iterator(from, size());
  }

  @NotNull
  public #E#ListIterator iterator(int from, int to) {
    if (from >= to) {
      assert from == to : from + " " + to;
      return #E#Iterator.EMPTY;
    }
    return new IndexIterator(from, to);
  }

  public #E#List get(final IntList indices) {
    if (indices == null) {
      throw new NullPointerException("indices");
    }
    return new Abstract#E#List() {
      @Override
      public int size() {
        return indices.size();
      }

      @Override
      public #e# get(int index) throws NoSuchElementException {
        return Abstract#E#List.this.get(indices.get(index));
      }
    };
  }

  /**
   * Returns the index of the first occurrence of the specified element
   * in this list, or -1 if this list does not contain the element.
   * */
  public int indexOf(#e# value) {
    int i = 0;
    for (#E#Iterator ii : this) {
      if (ii.value() == value) return i;
      i++;
    }
    return -1;
  }

  public boolean contains(#e# value) {
    return indexOf(value) >= 0;
  }

  public #e#[] toNativeArray(int startIndex, #e#[] dest, int destOffset, int length) {
    if (length > 0) {
      #E#Iterator ii = iterator(startIndex, size());
      int e = destOffset + length;
      for (int i = destOffset; i < e; i++) {
        assert ii.hasNext();
        dest[i] = ii.nextValue();
      }
    }
    return dest;
  }

  public #E#List subList(int from, int to) {
    int sz = size();
    if (from < 0 || from > sz || to < 0 || to > sz || to < from)
      throw new IndexOutOfBoundsException(from + " " + to + " " + sz);
    if (from == 0 && to == sz)
      return this;
    else
      return new SubList(this, from, to);
  }

  public #e#[] toNativeArray() {
    int size = size();
    if (size == 0) return EMPTY_#EC#S;
    return toNativeArray(0, new #e#[size], 0, size);
  }

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof #E#List))
      return false;
    #E#List that = (#E#List) o;
    if (size() != that.size())
      return false;
    #E#Iterator ii1 = iterator();
    #E#Iterator ii2 = that.iterator();
    while (ii1.hasNext() && ii2.hasNext()) {
      if (ii1.nextValue() != ii2.nextValue())
        return false;
    }
    return !(ii1.hasNext() || ii2.hasNext());
  }

  public int hashCode() {
    int hashCode = 1;
    for (#E#Iterator ii : this) {
      hashCode = 31 * hashCode + (int)ii.value();
    }
    return hashCode;
  }

  /**
   * Returns index of the first value in the list
   */
  public int binarySearch(#e# value) {
    return binarySearch(value, 0, size());
  }

  protected boolean isSorted(boolean isUnique) {
    #E#Iterator it = iterator();
    if (!it.hasNext()) return true;
    #e# prev = it.nextValue();
    while (it.hasNext()) {
      #e# next = it.nextValue();
      if (next < prev || (isUnique && next == prev)) return false;
      prev = next;
    }
    return true;
  }

  /**
   * @return true if this list is sorted and does not contain duplicates, otherwise false
   */
  @Override
  public boolean isSortedUnique() {
    return isSorted(true);
  }

  /**
   * @return true if this list is sorted, otherwise false
   */
  @Override
  public boolean isSorted() {
    return isSorted(false);
  }

  public int binarySearch(#e# value, int from, int to) {
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

    comp = #E#Collections.compare(value, get(from));
    if (comp == 0)
      return from;
    else if (comp < 0)
      return -from - 1;
    if (to - from == 1)
      return -to - 1;
    comp = #E#Collections.compare(get(to - 1), value);
    if (comp < 0) {
      return -to - 1;
    } else if (comp == 0) {
      // skip to the earliest appearance of value
      low = to - 1;
      high = low - 1;
      found = true;
    }

    while (low <= high) {
      int mid = (low + high) >>> 1;
      comp = #E#Collections.compare(get(mid), value);
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
        #e# v = get(p);
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

  /**
   * @return {@code get(size() - backwardIndex - 1)}
   */
  public #e# getLast(int backwardIndex) {
    return get(size() - backwardIndex - 1);
  }

  public boolean equalSortedUniqueValues(#E#List collection) {
    assert isSortedUnique();
    if (size() != collection.size())
      return false;
    #E#Iterator ownIt = iterator();
    #e# prevOther = #EW#.MIN_VALUE;
    for (#E#Iterator it : collection) {
      #e# own = ownIt.nextValue();
      #e# other = it.value();
      if (other < prevOther) {
        assert false : collection; // Not sorted
        return false;
      }
      if (own != other)
        return false;
      prevOther = other;
    }
    return true;
  }

  protected class IndexIterator extends Abstract#E#ListIndexIterator {
    public IndexIterator(int from, int to) {
      super(from, to);
    }

    protected #e# absget(int index) {
      return Abstract#E#List.this.get(index);
    }
  }

  @Override
  public List<#EW#> toList() {
    List<#EW#> list = IntegersUtils.arrayList();
    for(#E#Iterator ii : Abstract#E#List.this) {
      list.add(ii.value());
    }
    return list;
  }

  protected static class SubList extends Abstract#E#List {
    private final Abstract#E#List myParent;
    private final int myFrom;
    private final int myTo;

    public SubList(Abstract#E#List parent, int from, int to) {
      assert to >= from : from + " " + to;
      myParent = parent;
      myFrom = from;
      myTo = to;
    }

    public int size() {
      return myTo - myFrom;
    }

    public #e# get(int index) {
      if (index < 0)
        throw new ArrayIndexOutOfBoundsException(index);
      index += myFrom;
      if (index >= myTo)
        throw new ArrayIndexOutOfBoundsException(index - myFrom);
      return myParent.get(index);
    }

    public #E#List subList(int from, int to) {
      int sz = size();
      if (from < 0 || from > sz || to < 0 || to > sz || to < from)
        throw new IndexOutOfBoundsException(from + " " + to + " " + sz);
      if (from == 0 && to == sz)
        return this;
      else
        return new SubList(myParent, myFrom + from, myFrom + to);
    }

    @Override
    public #e#[] toNativeArray(int startIndex, #e#[] dest, int destOffset, int length) {
      return getParent().toNativeArray(myFrom + startIndex, dest, destOffset, length);
    }

    @Override
    public #e#[] toNativeArray() {
      int size = size();
      if (size == 0) return EMPTY_#EC#S;
      return getParent().toNativeArray(myFrom, new #e#[size], 0, size);
    }

    public Abstract#E#List getParent() {
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
