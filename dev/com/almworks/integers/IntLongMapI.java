package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public interface IntLongMapI extends IntLongIterable {
  /**
   * @return true if this map contains key {@code key}. Otherwise false
   */
  boolean containsKey(int key);

  /**
   * @return true if this map contains all of the elements produced by {@code keys}.
   * Otherwise false
   */
  boolean containsKeys(IntIterable keys);

  /**
   * @return the number of keys/values in this map(its cardinality)
   */
  int size();

  /**
   * @return true if this set contains no elements
   */
  boolean isEmpty();

  @NotNull
  IntLongIterator iterator();

  IntIterator keysIterator();

  LongIterator valuesIterator();

  /**
   * @return the value for the specified key
   */
  long get(int key);
}