package com.almworks.integers.util;

import com.almworks.integers.LongIterable;

/**
 * Iterables with size
 * @see com.almworks.integers.LongList
 * @see com.almworks.integers.LongSet
 */
public interface LongMeasurableIterable extends LongIterable {
  /**
   * @return size of this iterable
   */
  int size();
}
