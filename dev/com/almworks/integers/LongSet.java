package com.almworks.integers;

public interface LongSet extends LongIterable {
  /**
   * @return a list containing all the elements in this set
   * */
  LongList toList();

  /**
   * @return an array containing all the elements in this set
   * */
  LongArray toArray();

  /**
   * @return true if this set contains no elements
   */
  boolean isEmpty();

  /**
   * @return the number of elements in this set (its cardinality)
   */
  int size();

  /**
   * @return true if this set contains element value. Otherwise false
   */
  boolean contains(long value);

  /**
   * @return true if this set contains all of the elements in iterable. Otherwise false
   */
  boolean containsAll(LongIterable iterable);
}
