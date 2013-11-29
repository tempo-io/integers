package com.almworks.integers;

import com.almworks.integers.util.LongSizedIterable;

public interface LongSet extends LongSizedIterable {
  /**
   * @return true if this set contains element {@code value}. Otherwise false
   */
  boolean contains(long value);

  /**
   * @return true if this set contains all of the elements produced by {@code iterable}.
   * Otherwise false
   */
  boolean containsAll(LongIterable iterable);

  /**
   * @return the number of elements in this set (its cardinality)
   */
  int size();

  /**
   * @return true if this set contains no elements
   */
  boolean isEmpty();

  /**
   * @return an array containing all the elements in this set
   * */
  LongArray toArray();
}
