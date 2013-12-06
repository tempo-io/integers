package com.almworks.integers;

public interface WritableLongSet extends LongCollector, LongSet {

  /**
   * Removes all of the elements from this set.
   * The set will be empty after this call returns.
   */
  void clear();

  /**
  * Removes the specified element from this set.
  * */
  void remove(long value);

  /**
   * Removes from this set all elements that are contained in the {@code values}
   */
  void removeAll(long ... values);

  /**
   * Removes from this set all elements that are contained in the list {@code values}
   */
  void removeAll(LongList values);

  /**
   * Removes from this set all elements that are produced by {@code iterator}
   */
  void removeAll(LongIterator iterator);

  /**
   * retain this set with values
   * @return this
   * */
  void retain(LongList values);

  /**
   * Adds the specified element to this set if it is not already present.
   * If this set already contains the element, the call leaves the set
   * unchanged and returns {@code false}. Otherwise {@code true}.
   *
   * @return {@code false} if this set already contains the element.
   * Otherwise {@code true}.
   * If return value is unused, better use {@link WritableLongSet#add(long)}
   * */
  boolean include(long value);

  /**
   * Removes the specified element from this set.
   * If this set didn't contain the element, the call leaves the set
   * unchanged and returns {@code false}. Otherwise {@code true}.
   *
   * @return {@code true} if this set contained {@code value}. Otherwise {@code false}.
   * If return value is unused, better use {@link WritableLongSet#remove(long)}
   * */
  boolean exclude(long value);
}
