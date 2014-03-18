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

// CODE GENERATED FROM com/almworks/integers/AbstractPSet.tpl


package com.almworks.integers;

import static com.almworks.integers.IntegersUtils.appendShortName;

public abstract class AbstractIntSet implements IntSet {

  protected abstract void toNativeArrayImpl(int[] dest, int destPos);

  @Override
  public boolean containsAll(IntIterable iterable) {
    if (iterable == this) return true;
    for (IntIterator it: iterable) {
      if (!contains(it.value())) return false;
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public int[] toNativeArray(int[] dest) {
    return toNativeArray(dest, 0);
  }

  @Override
  public int[] toNativeArray(int[] dest, int destPos) {
    if (destPos < 0 || destPos + size() > dest.length) {
      throw new ArrayIndexOutOfBoundsException();
    }
    toNativeArrayImpl(dest, destPos);
    return dest;
  }

  public IntArray toArray() {
    return new IntArray(toNativeArray(new int[size()]));
  }

  public StringBuilder toString(StringBuilder builder) {
    appendShortName(builder, this);
    builder.append(" ").append(size()).append(" [");
    String sep = "";
    for  (IntIterator ii : this) {
      builder.append(sep).append(ii.value());
      sep = ", ";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof IntSet)) return false;

    IntSet otherSet = (IntSet) o;
    if (otherSet.size() != size()) {
      return false;
    }
    return containsAll(otherSet);
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (IntIterator it : iterator()) {
      h += IntegersUtils.hash(it.value());
    }
    return h;
  }
}
