package com.almworks.integers;

public interface WritableLongSet extends LongCollector, LongSet {

  void clear();

  void remove(long value);

  void removeAll(long ... values);

  void removeAll(LongList values);

  void removeAll(LongIterator values);

  void retain(LongList values);

  /**
   * Adds the specified element to this set if it is not already present.
   * If this set already contains the element, the call leaves the set
   * unchanged and returns {@code false}. Otherwise {@code true}.
   *
   * @return {@code true} if this set did not already contain the specified
   * element. If return value is unused, better to use {@link LongCollector#add(long)}
   * */
  boolean include(long value);

  boolean exclude(long value);
}
