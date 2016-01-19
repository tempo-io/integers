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
import static com.almworks.integers.LongLongIterators.pair;

public abstract class AbstractWritableLongLongMap implements WritableLongLongMap {
  protected int myModCount = 0;

  /**
   * put element without invocation of {@code AbstractWritableLongLongMap#modified()}
   */
  protected abstract long putImpl(long key, long value);

  /**
   * remove element without invocation of {@code AbstractWritableLongLongMap#modified()}
   */
  protected abstract long removeImpl(long key);

  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean containsKeys(LongIterable keys) {
    for (LongIterator it: keys) {
      if (!containsKey(it.value())) return false;
    }
    return true;
  }

  public boolean containsAnyKeys(LongIterable keys) {
    for (LongIterator it: keys) {
      if (containsKey(it.value())) return true;
    }
    return false;
  }

  @Override
  public LongSet keySet() {
    return new AbstractLongSet() {
      @Override
      public boolean contains(long value) {
        return containsKey(value);
      }

      @Override
      public int size() {
        return AbstractWritableLongLongMap.this.size();
      }

      @NotNull
      @Override
      public LongIterator iterator() {
        return keysIterator();
      }
    };
  }

  protected void modified() {
    myModCount++;
  }

  public long put(long key, long value) {
    modified();
    return putImpl(key, value);
  }

  public boolean putIfAbsent(long key, long value) {
    modified();
    if (containsKey(key)) return false;
    putImpl(key, value);
    return true;
  }

  public AbstractWritableLongLongMap add(long key, long value) {
    modified();
    putImpl(key, value);
    return this;
  }

  public long remove(long key) {
    modified();
    return removeImpl(key);
  }

  public boolean remove(long key, long value) {
    modified();
    if (containsKey(key) && get(key) == value) {
      removeImpl(key);
      return true;
    }
    return false;
  }

  public void putAll(LongSizedIterable keys, LongSizedIterable values) {
    modified();
    if (keys.size() != values.size()) {
      throw new IllegalArgumentException();
    }
    putAll(pair(keys, values));
  }

  public void putAll(LongLongIterable entries) {
    modified();
    for (LongLongIterator it: entries) {
      putImpl(it.left(), it.right());
    }
  }

  @Override
  public void putAll(long[] keys, long[] values) {
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
  public void putAllKeys(LongIterable keys, LongIterable values) {
    modified();
    LongIterator keysIt = keys.iterator();
    LongIterator valuesIt = values.iterator();

    while (keysIt.hasNext() && valuesIt.hasNext()) {
      putImpl(keysIt.nextValue(), valuesIt.nextValue());
    }
    while (keysIt.hasNext()) {
      putIfAbsent(keysIt.nextValue(), DEFAULT_VALUE);
    }
  }

  public void removeAll(long... keys) {
    modified();
    if (keys != null && keys.length > 0) {
      removeAll(new LongNativeArrayIterator(keys));
    }
  }

  public void removeAll(LongIterable keys) {
    modified();
    for (LongIterator it : keys) {
      removeImpl(it.value());
    }
  }


  public StringBuilder toString(StringBuilder builder) {
    appendShortName(builder, this);
    builder.append(" ").append(size()).append(" [");
    String sep = "";
    for (LongLongIterator ii : this) {
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
    LongLongIterators.joinCurrent(cur, builders);

    String[] elements = LongLongIterators.toTableString(this);
    builders[0].append(elements[0]);
    builders[1].append(elements[1]);

    builders[0].append(" \\\n").append(builders[1]).append(" /");
    return builders[0].toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof LongLongMap)) return false;

    LongLongMap otherMap = (LongLongMap) o;

    if (otherMap.size() != size()) return false;
    for (LongLongIterator it : this) {
      long key = it.left();
      if (!otherMap.containsKey(key) || otherMap.get(key) != it.right()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (LongLongIterator it : this) {
      h += IntegersUtils.hash(it.left()) + IntegersUtils.hash(it.right());
    }
    return h;
  }
}