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

import static com.almworks.integers.IntegersUtils.appendShortName;
import static com.almworks.integers.#E#IterableLexicographicComparator.#EC#_ITERABLE_LEXICOGRAPHIC_COMPARATOR;

public abstract class Abstract#E#Set implements #E#Set {

  protected void toNativeArrayImpl(#e#[] dest, int destPos) {
    for (#E#Iterator it : iterator()) {
      dest[destPos++] = it.value();
    }
  }

  @Override
  public boolean containsAll(#E#Iterable iterable) {
    if (iterable == this) return true;
    for (#E#Iterator it: iterable) {
      if (!contains(it.value())) return false;
    }
    return true;
  }

  @Override
  public boolean containsAny(#E#Iterable iterable) {
    for (#E#Iterator it: iterable) {
      if (contains(it.value())) return true;
    }
    return false;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public #e#[] toNativeArray(#e#[] dest) {
    return toNativeArray(dest, 0);
  }

  @Override
  public #e#[] toNativeArray(#e#[] dest, int destPos) {
    if (destPos < 0 || destPos + size() > dest.length) {
      throw new ArrayIndexOutOfBoundsException();
    }
    toNativeArrayImpl(dest, destPos);
    return dest;
  }

  public #E#Array toArray() {
    return new #E#Array(toNativeArray(new #e#[size()]));
  }

  public StringBuilder toString(StringBuilder builder) {
    appendShortName(builder, this);
    builder.append(" ").append(size()).append(" [");
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

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof #E#Set)) return false;

    #E#Set otherSet = (#E#Set) o;
    if (otherSet.size() != size()) {
      return false;
    }
    if (!(this instanceof #E#SortedSet)) {
      return containsAll(otherSet);
    }
    if (!(otherSet instanceof #E#SortedSet)) {
      return otherSet.containsAll(otherSet);
    }
    assert (this instanceof #E#SortedSet) && (otherSet instanceof #E#SortedSet);
    return #EC#_ITERABLE_LEXICOGRAPHIC_COMPARATOR.compare(this, otherSet) == 0;
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (#E#Iterator it : this) {
      h += IntegersUtils.hash(it.value());
    }
    return h;
  }
}
