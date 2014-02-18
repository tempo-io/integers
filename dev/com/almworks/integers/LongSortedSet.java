package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public interface LongSortedSet extends LongSet {
  /**
   * @return a uniquely sorted array containing all the elements of this set
   * */
  LongArray toArray();

  /**
   * @return an iterator over this set in the sorted order
   * */
  @NotNull
  LongIterator iterator();

  /**
   * @return an iterator over of this set in the sorted order whose elements are greater than or equal to fromElement.
   */
  LongIterator tailIterator(long fromElement);

  /**
   * @return the greatest element of this set if it exists or {@link Long#MIN_VALUE} in case the set is empty
   */
  public long getUpperBound();

  /**
   * @return the smallest element of this set if it exist or {@link Long#MAX_VALUE} in case the set is empty
   */
  public long getLowerBound();

    /**
     * Writes values from this set to {@code dest} in the ascending order.<br>
     * {@inheritDoc}
     */
  long[] toNativeArray(long[] dest, int destPos);

  /**
   * Writes values from this set to {@code dest} in the ascending order.
   * @see {@link #toNativeArray(long[], int)}
   */
  @Override
  long[] toNativeArray(long[] dest);
}
