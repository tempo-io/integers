package com.almworks.integers;

public interface LongSet extends LongSizedIterable {
  LongSortedSet EMPTY = new LongEmptySet();

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
   * @return an array containing all the elements in this set.
   * The array contains no duplicates.
   * @see #toNativeArray(long[])
   * */
  LongArray toArray();

   /**
   * Writes values from this set (without duplicates) to dest.
   * @param dest destination array. Its length should be greater than
   * {@link #size()}{@code + destPos}
   * @param destPos starting position in the destination data.
   * @return dest
   * @exception  IndexOutOfBoundsException  if copying would cause
   *               access of data outside array bounds.
   */
  long[] toNativeArray(long[] dest, int destPos);

  /**
   * Writes values from this set to {@code dest}.
   * @see {@link #toNativeArray(long[], int)}
   */
  long[] toNativeArray(long[] dest);
}
