package com.almworks.integers;

public interface WritableLongSet extends LongCollector, LongSet {

  /**
   * Removes all of the elements from this set.
   * The set will be empty after this call returns.
   */
  void clear();

  /**
  * Removes the specified element from this set if it is present
  * */
  void remove(long value);

  /**
   * Removes from this set all elements that are contained in the values
   */
  void removeAll(long ... values);

  /**
   * Removes from this set all elements that are contained in the list values
   */
  void removeAll(LongList values);

  /**
   * Removes from this set all elements that are contained in the iterator
   */
  void removeAll(LongIterator iterator);

  /**
   * retain this set with values
   * @return this
   * */
  WritableLongSet retain(LongList values);

  /**
   * Adds the specified element to this set if it is not already present.
   * If this set already contains the element, the call leaves the set
   * unchanged and returns {@code false}. Otherwise {@code true}.
   *
   * @return {@code true} if size of set changed. Otherwise {@code false}.
   * If return value is unused, better to use {@link WritableLongSet#add(long)}
   * */
  boolean include(long value);

  /**
   * Removes the specified element from this set if it is present.
   * If this set don't contain the element, the call leaves the set
   * unchanged and returns {@code false}. Otherwise {@code true}.
   *
   * @return {@code true} if size of set changed. Otherwise {@code false}.
   * If return value is unused, better to use {@link WritableLongSet#exclude(long)}
   * */
  boolean exclude(long value);
}
