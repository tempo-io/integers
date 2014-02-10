package com.almworks.integers;

import static com.almworks.integers.IntegersUtils.appendShortName;

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
}
