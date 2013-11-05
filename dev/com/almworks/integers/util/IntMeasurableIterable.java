package com.almworks.integers.util;

import com.almworks.integers.IntIterable;
import com.almworks.integers.LongIterable;

/**
 * Iterables with size
 * @see com.almworks.integers.LongList
 * @see com.almworks.integers.LongSet
 */
public interface IntMeasurableIterable extends IntIterable {
  /**
   * @return size of this iterable
   */
  int size();
}
