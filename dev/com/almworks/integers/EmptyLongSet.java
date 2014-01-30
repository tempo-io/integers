package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public class EmptyLongSet extends AbstractLongSet implements LongSortedSet {
  @Override
  protected void toNativeArrayImpl(long[] dest, int destPos) {
  }

  @Override
  public boolean contains(long value) {
    return false;
  }

  @Override
  public int size() {
    return 0;
  }

  @NotNull
  @Override
  public LongIterator iterator() {
    return LongIterator.EMPTY;
  }

  @Override
  public LongIterator tailIterator(long fromElement) {
    return LongIterator.EMPTY;
  }

  @Override
  public long getUpperBound() {
    return Long.MIN_VALUE;
  }

  @Override
  public long getLowerBound() {
    return Long.MAX_VALUE;
  }
}
