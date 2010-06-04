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

// GENERATED CODE!!!
package com.almworks.integers;

import org.jetbrains.annotations.Nullable;

public class Parallel#E#List {
  private final Writable#E#List myStorage;
  private final int myListCount;

  public Parallel#E#List(Writable#E#List storage, int listCount) {
    if (storage == null)
      throw new NullPointerException();
    if (!storage.isEmpty())
      throw new IllegalArgumentException();
    if (listCount <= 0)
      throw new IllegalArgumentException();
    myStorage = storage;
    myListCount = listCount;
  }

  public int size() {
    return myStorage.size() / getListCount();
  }

  private int getListCount() {
    return myListCount;
  }

  public #E#List createListAccessor(int list) {
    return new MyList(list);
  }

  public #e# get(int offset, int list) {
    return myStorage.get(offset * getListCount() + list);
  }

  public void get(int offset, #e#[] dst) {
    if (dst == null || dst.length < getListCount())
      throw new IllegalArgumentException();
    myStorage.toArray(offset * getListCount(), dst, 0, getListCount());
  }

  public void set(int offset, int list, #e# value) {
    myStorage.set(offset * getListCount() + list, value);
  }

  public void clear() {
    myStorage.clear();
  }

  public void insert(int offset, #e# ... values) {
    if (values == null || values.length != getListCount())
      throw new IllegalArgumentException();
    myStorage.insertAll(offset * getListCount(), new #E#Array(values));
  }

  public Iterator iterator(int offset) {
    return new Iterator(myStorage.iterator(offset * getListCount()));
  }

  public Iterator iterator(int from, int to) {
    return new Iterator(myStorage.iterator(from * getListCount(), to * getListCount()));
  }

  public void removeAt(int offset) {
    removeRange(offset, offset + 1);
  }

  public void removeRange(int from, int to) {
    if (to <= from) return;
    int listCount = getListCount();
    int start = from * listCount;
    int number = to - from;
    myStorage.removeRange(start, start + listCount * number);
  }

  public boolean isEmpty() {
    return myStorage.isEmpty();
  }

  public class Iterator {
    private final Writable#E#ListIterator myIt;

    public Iterator(Writable#E#ListIterator it) {
      myIt = it;
    }

    public boolean hasNext() {
      return myIt.hasNext();
    }

    public void next(@Nullable #e#[] dst) {
      if (dst != null && dst.length < getListCount())
        throw new IllegalArgumentException();
      for (int i = 0; i < getListCount(); i++) {
        #e# val = myIt.next();
        if (dst != null)
          dst[i] = val;
      }
    }

    public void move(int offset) {
      myIt.move(offset * getListCount());
    }

    public void get(int offset, #e#[] dst) {
      if (dst == null || dst.length < getListCount())
        throw new IllegalArgumentException();
      for (int i = 0; i < getListCount(); i++)
        dst[i] = myIt.get(getListCount() * (offset - 1) + i + 1);
    }

    public #e# get(int offset, int list) {
      if (list < 0 || list >= getListCount()) throw new IllegalArgumentException();
      return myIt.get(getListCount() * (offset - 1) + list);
    }

    public void removeRange(int from, int to) {
      from--;
      to--;
      myIt.removeRange(getListCount() * from + 1, getListCount() * to + 1);
    }

    public void set(int offset, int list, #e# value) {
      myIt.set(getListCount() * (offset - 1) + list + 1, value);
    }

    public void set(int offset, #e#[] values) {
      if (values == null || values.length < getListCount())
        throw new IllegalArgumentException();
      for (int i = 0; i < getListCount(); i++)
        set(offset, i, values[i]);
    }
  }

  private class MyList extends Abstract#E#List {
    private final int myIndex;

    private MyList(int index) {
      myIndex = index;
    }

    public int size() {
      return Parallel#E#List.this.size();
    }

    public #e# get(int index) {
      return myStorage.get(index * getListCount() + myIndex);
    }
  }
}
