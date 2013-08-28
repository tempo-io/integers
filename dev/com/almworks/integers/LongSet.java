package com.almworks.integers;

public interface LongSet extends LongIterable {
  LongList toList();

  boolean isEmpty();

  int size();

  boolean contains(long value);

  LongIterator iterator();

  LongIterator tailIterator(long value);

  boolean containsAll(LongIterable values);
}
