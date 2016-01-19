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

// CODE GENERATED FROM com/almworks/integers/AbstractWritablePQMap.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

import static com.almworks.integers.IntegersUtils.appendShortName;
import static com.almworks.integers.IntIntIterators.pair;

public abstract class AbstractWritableIntIntMap implements WritableIntIntMap {
  protected int myModCount = 0;

  /**
   * put element without invocation of {@code AbstractWritableIntIntMap#modified()}
   */
  protected abstract int putImpl(int key, int value);

  /**
   * remove element without invocation of {@code AbstractWritableIntIntMap#modified()}
   */
  protected abstract int removeImpl(int key);

  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean containsKeys(IntIterable keys) {
    for (IntIterator it: keys) {
      if (!containsKey(it.value())) return false;
    }
    return true;
  }

  public boolean containsAnyKeys(IntIterable keys) {
    for (IntIterator it: keys) {
      if (containsKey(it.value())) return true;
    }
    return false;
  }

  @Override
  public IntSet keySet() {
    return new AbstractIntSet() {
      @Override
      public boolean contains(int value) {
        return containsKey(value);
      }

      @Override
      public int size() {
        return AbstractWritableIntIntMap.this.size();
      }

      @NotNull
      @Override
      public IntIterator iterator() {
        return keysIterator();
      }
    };
  }

  protected void modified() {
    myModCount++;
  }

  public int put(int key, int value) {
    modified();
    return putImpl(key, value);
  }

  public boolean putIfAbsent(int key, int value) {
    modified();
    if (containsKey(key)) return false;
    putImpl(key, value);
    return true;
  }

  public AbstractWritableIntIntMap add(int key, int value) {
    modified();
    putImpl(key, value);
    return this;
  }

  public int remove(int key) {
    modified();
    return removeImpl(key);
  }

  public boolean remove(int key, int value) {
    modified();
    if (containsKey(key) && get(key) == value) {
      removeImpl(key);
      return true;
    }
    return false;
  }

  public void putAll(IntSizedIterable keys, IntSizedIterable values) {
    modified();
    if (keys.size() != values.size()) {
      throw new IllegalArgumentException();
    }
    putAll(pair(keys, values));
  }

  public void putAll(IntIntIterable entries) {
    modified();
    for (IntIntIterator it: entries) {
      putImpl(it.left(), it.right());
    }
  }

  @Override
  public void putAll(int[] keys, int[] values) {
    modified();
    if (keys.length != values.length) {
      throw new IllegalArgumentException();
    }
    int size = keys.length;
    for (int i = 0; i < size; i++) {
      putImpl(keys[i], values[i]);
    }
  }

  @Override
  public void putAllKeys(IntIterable keys, IntIterable values) {
    modified();
    IntIterator keysIt = keys.iterator();
    IntIterator valuesIt = values.iterator();

    while (keysIt.hasNext() && valuesIt.hasNext()) {
      putImpl(keysIt.nextValue(), valuesIt.nextValue());
    }
    while (keysIt.hasNext()) {
      putIfAbsent(keysIt.nextValue(), DEFAULT_VALUE);
    }
  }

  public void removeAll(int... keys) {
    modified();
    if (keys != null && keys.length > 0) {
      removeAll(new IntNativeArrayIterator(keys));
    }
  }

  public void removeAll(IntIterable keys) {
    modified();
    for (IntIterator it : keys) {
      removeImpl(it.value());
    }
  }


  public StringBuilder toString(StringBuilder builder) {
    appendShortName(builder, this);
    builder.append(" ").append(size()).append(" [");
    String sep = "";
    for (IntIntIterator ii : this) {
      builder.append(sep).append('(').append(ii.left()).append(' ').append(ii.right()).append(')');
      sep = ", ";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }

  public String toTableString() {
    StringBuilder[] builders = {new StringBuilder(), new StringBuilder()};
    StringBuilder[] cur = {new StringBuilder(), new StringBuilder()};

    cur[0] = appendShortName(cur[0], this).append(" /");
    cur[1].append(size()).append(" \\");
    IntIntIterators.joinCurrent(cur, builders);

    String[] elements = IntIntIterators.toTableString(this);
    builders[0].append(elements[0]);
    builders[1].append(elements[1]);

    builders[0].append(" \\\n").append(builders[1]).append(" /");
    return builders[0].toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof IntIntMap)) return false;

    IntIntMap otherMap = (IntIntMap) o;

    if (otherMap.size() != size()) return false;
    for (IntIntIterator it : this) {
      int key = it.left();
      if (!otherMap.containsKey(key) || otherMap.get(key) != it.right()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (IntIntIterator it : this) {
      h += IntegersUtils.hash(it.left()) + IntegersUtils.hash(it.right());
    }
    return h;
  }
}