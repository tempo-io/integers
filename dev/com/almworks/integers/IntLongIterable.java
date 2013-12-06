package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public interface IntLongIterable extends Iterable<IntLongIterator> {
  /**
   * @return fail-fast read-only iterator for the collection. If Iterable represents empty collection it
   * should return {@link IntLongIterator#EMPTY}
   */
  @NotNull
  IntLongIterator iterator();
}