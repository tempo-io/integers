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
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Serves as a map with #e# keys.
 *
 * @param <T> element type
 */
public class #E#ObjListMap<T> extends AbstractWritable#E#ObjMap<T> {
  private final #E#Array myKeys = new #E#Array();
  private final List<T> myValues = IntegersUtils.arrayList();

  public static <T> #E#ObjListMap<T> create() {
    return new #E#ObjListMap();
  }

  @Override
  protected T putImpl(#e# key, T value) {
    int pos = myKeys.binarySearch(key);
    if (pos >= 0) {
      return myValues.set(pos, value);
    } else {
      int insPos = -pos - 1;
      myKeys.insert(insPos, key);
      myValues.add(insPos, value);
      return null;
    }
  }

  /**
   * Returns null if the element is not in the map.
   */
  @Nullable
  public T get(#e# key) {
    int pos = myKeys.binarySearch(key);
    if (pos >= 0) return myValues.get(pos);
    return null;
  }

  public boolean containsKey(#e# key) {
    return myKeys.binarySearch(key) >= 0;
  }

  /**
   * Returns the leftmost #e# map iterator such that key for the entry returned by the call to {@link #E#MapIterator#next} is greater than the specified key.
   * If there are no positions with keys greater than the specified one, a past-the-end iterator is returned.
   */
  @Nullable
  public #E#MapIterator find(#e# key) {
    int pos = myKeys.binarySearch(key);
    if (pos < 0) {
      pos = -pos - 1;
    }
    return new #E#MapIterator(pos);
  }

  /**
   * @return an unmodifiable list of values sorted by their keys in ascending order.
   */
  public List<T> getValues() {
    //noinspection ReturnOfCollectionOrArrayField
    return Collections.unmodifiableList(myValues);
  }

  @Override
  protected T removeImpl(#e# key) {
    int pos = myKeys.binarySearch(key);
    if (pos >= 0) {
      myKeys.removeAt(pos);
      return myValues.remove(pos);
    } else {
      return null;
    }
  }

  @Override
  @NotNull
  public #E#MapIterator iterator() {
    return new #E#MapIterator();
  }

  @Override
  public #E#ListIterator keysIterator() {
    return new #E#FailFastListIterator(myKeys.iterator()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  @Override
  public Iterator<T> valuesIterator() {
    final Iterator<T> iterator = myValues.iterator();
    final int expectedModCount = myModCount;
    return new Iterator<T>() {
      @Override
      public boolean hasNext() {
        checkMod();
        return iterator.hasNext();
      }

      @Override
      public T next() {
        checkMod();
        return iterator.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      private void checkMod() {
        if (expectedModCount != myModCount) throw new ConcurrentModificationException();
      }
    };
  }

  @Override
  public #E#SortedSet keySet() {
    return #E#ListSet.asSet(myKeys);
  }

  public int size() {
    assert myKeys.size() == myValues.size();
    return myKeys.size();
  }

  @Override
  public void clear() {
    modified();
    myKeys.clear();
    myValues.clear();
  }

  public class #E#MapIterator extends Abstract#E#ObjIterator<T> {
    private int myFrom;
    private int myNext;
    private int myExpectedModCount;
    private boolean myIsJustRemoved;

    public #E#MapIterator() {
      this(0);
    }

    public #E#MapIterator(int keyPos) {
      if (keyPos < 0) throw new IllegalArgumentException("from < 0");
      myExpectedModCount = myModCount;
      myNext = myFrom = keyPos;
    }

    @Override
    public boolean hasNext() {
      checkMod();
      return myNext < size();
    }

    @Override
    public boolean hasValue() {
      checkMod();
      return myFrom < myNext && !myIsJustRemoved;
    }

    public #e# left() {
      checkMod();
      if (!hasValue()) throw new IllegalStateException();
      return myKeys.get(myNext - 1);
    }

    public T right() {
      checkMod();
      if (!hasValue()) throw new IllegalStateException();
      return myValues.get(myNext - 1);
    }

    public #E#ObjIterator<T> next() {
      checkMod();
      if (myNext >= size()) throw new NoSuchElementException();
      myNext++;
      myIsJustRemoved = false;
      return this;
    }

    @Override
    public void remove() {
      checkMod();
      if (myNext == 0) throw new IllegalStateException();

      try {
        myKeys.removeAt(myNext - 1);
        myValues.remove(myNext - 1);
        myExpectedModCount = myModCount;
        myNext--;
        myIsJustRemoved = true;
      } catch(IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }

    public boolean hasPrevious() {
      checkMod();
      return myNext > myFrom + 1;
    }

    public #E#ObjIterator<T> previous() {
      checkMod();
      if (!hasPrevious()) throw new NoSuchElementException();
      myNext--;
      return this;
    }

    private void checkMod() {
      if(myExpectedModCount != myModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
}
