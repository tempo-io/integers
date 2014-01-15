package com.almworks.integers;

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
    String name = getClass().getSimpleName();
    // LongAmortizedSet -> LAS, LongTreeSet -> LTS
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if ('A' <= c && c <= 'Z') {
        builder.append(c);
      }
    }
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
