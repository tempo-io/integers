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

public class WritableLongIntMapProjection implements WritableLongSet {
  static int DEFAULT_CONTAINS_VALUE = 239;

  private WritableLongIntMap myMap;

  public WritableLongIntMapProjection(WritableLongIntMap map) {
    myMap = map;
  }

  @Override
  public void clear() {
    myMap.clear();
  }

  @Override
  public boolean include(long value) {
    return myMap.put(value, DEFAULT_CONTAINS_VALUE) != DEFAULT_CONTAINS_VALUE;
  }

  @Override
  public boolean exclude(long value) {
    return myMap.remove(value) == DEFAULT_CONTAINS_VALUE;
  }

  @Override
  public void remove(long value) {
    myMap.remove(value);
  }

  @Override
  public void removeAll(long... values) {
    myMap.removeAll(values);
  }

  @Override
  public void removeAll(LongIterable iterable) {
    myMap.removeAll(iterable);
  }

  @Override
  public void retain(LongList values) {
    LongArray res = new LongArray();
    for (LongIterator it: values) {
      long value = it.value();
      if (contains(value)) res.add(value);
    }
    clear();
    addAll(res);
  }

  @Override
  public void add(long value) {
    myMap.add(value, DEFAULT_CONTAINS_VALUE);
  }

  @Override
  public void addAll(LongList values) {
    addAll(values.iterator());
  }

  @Override
  public void addAll(LongIterable iterable) {
    for (LongIterator iterator : iterable) {
      add(iterator.value());
    }
  }

  @Override
  public void addAll(long... values) {
    for (long value : values) {
      add(value);
    }
  }

  @Override
  public boolean contains(long value) {
    return myMap.containsKey(value);
  }

  @Override
  public boolean containsAll(LongIterable iterable) {
    return myMap.containsKeys(iterable);
  }

  @Override
  public boolean containsAny(LongIterable iterable) {
    for (LongIterator ii : iterable) {
      if (myMap.containsKey(ii.value())) return true;
    }
    return false;
  }

  @Override
  public int size() {
    return myMap.size();
  }

  @Override
  public boolean isEmpty() {
    return myMap.isEmpty();
  }

  @Override
  public long[] toNativeArray(long[] dest) {
    return toNativeArray(dest, 0);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof LongSet)) return false;
    LongSet otherSet = (LongSet) o;
    if (otherSet.size() != size())
      return false;
    return containsAll(otherSet);
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (LongIterator it : this) {
      h += IntegersUtils.hash(it.value());
    }
    return h;
  }

  @Override
  public long[] toNativeArray(long[] dest, int destPos) {
    if (destPos < 0 || destPos + size() > dest.length) {
      throw new ArrayIndexOutOfBoundsException();
    }
    for (LongIterator it : this) {
      dest[destPos++] = it.value();
    }
    return dest;
  }

  public LongArray toArray() {
    return new LongArray(toNativeArray(new long[size()]));
  }

  @NotNull
  @Override
  public LongIterator iterator() {
    return myMap.keysIterator();
  }
}
