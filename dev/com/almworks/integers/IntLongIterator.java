package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public interface IntLongIterator extends Iterator<IntLongIterator>, IntLongIterable {
  /**
   * Constant value for empty Iterators
   */
  IntLongIterator EMPTY = new EmptyIntLongIterator();

  /**
   * @return true next call to {@link #next()} wasn't throw NoSuchElementException
   */
  boolean hasNext() throws ConcurrentModificationException;

  /**
   * @return false if iterator has never been and
   * next call to {@link #left()} or {@link #right()} throw NoSuchElementException advanced otherwise true
   */
  boolean hasValue();

  /**
   * @throws NoSuchElementException if iterator has never been advanced
   * ({@link #next()} have never been called)
   */
  int left() throws NoSuchElementException;

  /**
   * @throws NoSuchElementException if iterator has never been advanced
   * ({@link #next()} have never been called)
   */
  long right() throws NoSuchElementException;

  class Single extends AbstractIntLongIteratorWithFlag {
    private int myLeft;
    private long myRight;

    public Single(int left, long right) {
      myLeft = left;
      myRight = right;
    }

    public boolean hasNext() {
      return !myIterated;
    }

    @Override
    protected int leftImpl() {
      return myLeft;
    }

    @Override
    protected long rightImpl() {
      return myRight;
    }

    @Override
    protected void nextImpl() throws NoSuchElementException {
      }
  }
}
