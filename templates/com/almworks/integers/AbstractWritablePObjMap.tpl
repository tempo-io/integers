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

import java.util.Collection;
import java.util.Iterator;

import static com.almworks.integers.IntegersUtils.appendShortName;

public abstract class AbstractWritable#E#ObjMap<T> implements Writable#E#ObjMap<T> {
  protected int myModCount = 0;

  /**
   * put element without invocation of {@code AbstractWritable#E#IntMap#modified()}
   */
  protected abstract T putImpl(#e# key, T value);

  /**
   * remove element without invocation of {@code AbstractWritable#E#IntMap#modified()}
   */
  protected abstract T removeImpl(#e# key);

  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean containsKeys(#E#Iterable iterable) {
    for (#E#Iterator it: iterable) {
      if (!containsKey(it.value())) return false;
    }
    return true;
  }

  @Override
  public boolean containsAnyKeys(#E#Iterable iterable) {
    for (#E#Iterator it: iterable) {
      if (containsKey(it.value())) return true;
    }
    return false;
  }

  @Override
  public #E#Set keySet() {
    return new Abstract#E#Set() {
      @Override
      public boolean contains(#e# value) {
        return containsKey(value);
      }

      @Override
      public int size() {
        return AbstractWritable#E#ObjMap.this.size();
      }

      @NotNull
      @Override
      public #E#Iterator iterator() {
        return keysIterator();
      }
    };
  }

  protected void modified() {
    myModCount++;
  }

  public T put(#e# key, T value) {
    modified();
    return putImpl(key, value);
  }

  public boolean putIfAbsent(#e# key, T value) {
    modified();
    if (containsKey(key)) return false;
    putImpl(key, value);
    return true;
  }

  public AbstractWritable#E#ObjMap<T> add(#e# key, T value) {
    modified();
    putImpl(key, value);
    return this;
  }

  public T remove(#e# key) {
    modified();
    return removeImpl(key);
  }

  public boolean remove(#e# key, T value) {
    modified();
    if (containsKey(key) && get(key) == value) {
      removeImpl(key);
      return true;
    }
    return false;
  }

  public void putAll(#E#ObjIterable<T> entries) {
    modified();
    for (#E#ObjIterator<T> it: entries) {
      putImpl(it.left(), it.right());
    }
  }

  @Override
  public void putAll(#E#SizedIterable keys, Collection<T> values) {
    modified();
    if (keys.size() != values.size()) {
      throw new IllegalArgumentException();
    }
    putAll(#E#ObjIterators.pair(keys.iterator(), values.iterator()));
  }

  @Override
  public void putAll(#e#[] keys, T[] values) {
    modified();
    if (keys.length != values.length) {
      throw new IllegalArgumentException();
    }
    int size = keys.length;
    for (int i = 0; i < size; i++) {
      putImpl(keys[i], values[i]);
    }
  }

  public void putAllKeys(#E#Iterable keys, Iterable<T> values) {
    modified();
    #E#Iterator keysIt = keys.iterator();
    Iterator<T> valuesIt = values.iterator();

    while (keysIt.hasNext() && valuesIt.hasNext()) {
      putImpl(keysIt.nextValue(), valuesIt.next());
    }
    while (keysIt.hasNext()) {
      putIfAbsent(keysIt.nextValue(), null);
    }
  }

  public void removeAll(#e#... keys) {
    modified();
    for (#e# key: keys) {
      removeImpl(key);
    }
  }

  public void removeAll(#E#Iterable keys) {
    modified();
    for (#E#Iterator it : keys) {
      removeImpl(it.value());
    }
  }

  public StringBuilder toString(StringBuilder builder) {
    appendShortName(builder, this);
    builder.append(" ").append(size()).append(" [");
    String sep = "";
    for (#E#ObjIterator<T> ii : this) {
      builder.append(sep).append('(').append(ii.left()).append(' ').append(ii.right()).append(')');
      sep = ", ";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }

  private void joinCurrent(StringBuilder[] cur, StringBuilder[] builders) {
    int maxLength = Math.max(cur[0].length(), cur[1].length());
    for (int idx = 0; idx < 2; idx++) {
      for (int i = 0; i < maxLength - cur[idx].length(); i++) {
        builders[idx].append(' ');
      }
      builders[idx].append(cur[idx]);
    }
  }

  public String toTableString() {
    StringBuilder[] builders = {new StringBuilder(), new StringBuilder()};
    StringBuilder[] cur = {new StringBuilder(), new StringBuilder()};

    cur[0] = appendShortName(cur[0], this).append(" /");
    cur[1].append(size()).append(" \\");
    joinCurrent(cur, builders);

    String sep = "";
    for (#E#ObjIterator<T> ii : this) {
      cur[0].setLength(0);
      cur[1].setLength(0);

      cur[0].append(ii.left());
      cur[1].append(ii.right());

      builders[0].append(sep);
      builders[1].append(sep);


      joinCurrent(cur, builders);
      sep = ", ";
    }
    builders[0].append(" \\\n").append(builders[1]).append(" /");
    return builders[0].toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof #E#ObjMap)) return false;

    #E#ObjMap<T> otherMap = (#E#ObjMap<T>) o;

    if (otherMap.size() != size()) return false;
    for (#E#ObjIterator<T> it : this) {
      #e# key = it.left();
      if (!otherMap.containsKey(key) || otherMap.get(key) != it.right()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (#E#ObjIterator<T> it : this) {
      h += IntegersUtils.hash(it.left()) + it.right().hashCode();
    }
    return h;
  }
}
