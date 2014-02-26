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

import static com.almworks.integers.IntIntIterators.pair;
import static com.almworks.integers.IntegersUtils.appendShortName;

public abstract class AbstractWritableIntIntMap implements WritableIntIntMap {
  protected int myModCount = 0;

  /**
   * put element without invocation of {@code AbstractWritableIntIntMap#modified()}
   */
  abstract protected int putImpl(int key, int value);

  /**
   * remove element without invocation of {@code AbstractWritableIntIntMap#modified()}
   */
  abstract protected int removeImpl(int key);

  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean containsKeys(IntIterable iterable) {
    for (IntIterator it: iterable.iterator()) {
      if (!containsKey(it.value())) return false;
    }
    return true;
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
    if (!(get(key) == value)) return false;
    removeImpl(key);
    return true;
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
    for (int key: keys) {
      removeImpl(key);
    }
  }

  public void removeAll(IntIterable keys) {
    modified();
    for (IntIterator it : keys.iterator()) {
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
    for (IntIntIterator ii : this) {
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
}
