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
import static com.almworks.integers.LongIterableLexicographicComparator.LONG_ITERABLE_LEXICOGRAPHIC_COMPARATOR;

public abstract class AbstractLongSet implements LongSet {

  protected abstract void toNativeArrayImpl(long[] dest, int destPos);

  @Override
  public boolean containsAll(LongIterable iterable) {
    if (iterable == this) return true;
    for (LongIterator it: iterable) {
      if (!contains(it.value())) return false;
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public long[] toNativeArray(long[] dest) {
    return toNativeArray(dest, 0);
  }

  @Override
  public long[] toNativeArray(long[] dest, int destPos) {
    if (destPos < 0 || destPos + size() > dest.length) {
      throw new ArrayIndexOutOfBoundsException();
    }
    toNativeArrayImpl(dest, destPos);
    return dest;
  }

  public LongArray toArray() {
    return new LongArray(toNativeArray(new long[size()]));
  }

  public StringBuilder toString(StringBuilder builder) {
    appendShortName(builder, this);
    builder.append(" ").append(size()).append(" [");
    String sep = "";
    for  (LongIterator ii : this) {
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
    if (!(o instanceof LongSet)) return false;

    LongSet otherSet = (LongSet) o;
    if (otherSet.size() != size()) {
      return false;
    }
    if (!(this instanceof LongSortedSet)) {
      return containsAll(otherSet);
    }
    if (!(otherSet instanceof LongSortedSet)) {
      return otherSet.containsAll(otherSet);
    }
    assert (this instanceof LongSortedSet) && (otherSet instanceof LongSortedSet);
    return LONG_ITERABLE_LEXICOGRAPHIC_COMPARATOR.compare(this, otherSet) == 0;
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (LongIterator it : this) {
      h += IntegersUtils.hash(it.value());
    }
    return h;
  }
}
