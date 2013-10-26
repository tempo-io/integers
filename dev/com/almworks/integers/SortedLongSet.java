package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public interface SortedLongSet extends LongSet {
  /**
   * @return a uniquely sorted array containing all the elements in this set
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
}
