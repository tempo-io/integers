package com.almworks.integers;

import com.almworks.integers.IntIterable;

/**
 * Iterables with size
 * @see com.almworks.integers.IntList
 * @see com.almworks.integers.LongSet
 */
public interface IntSizedIterable extends IntIterable {
  /**
   * Size of the collection. May not be efficient (up to O(N)).
   * @return the number of values in the collection
   */
  int size();
}
