package com.almworks.integers;

import java.util.NoSuchElementException;

/**
 * @see IntLongIterator
 * @see AbstractLongIteratorWithFlag
 */
public abstract class AbstractIntLongIteratorWithFlag extends AbstractIntLongIterator {

  protected boolean myIterated = false;

  public boolean hasValue() {
    return myIterated;
  }

  public int left() throws NoSuchElementException {
    if (!myIterated) {
      throw new NoSuchElementException();
    }
    return leftImpl();
  }

  public long right() throws NoSuchElementException {
    if (!myIterated) {
      throw new NoSuchElementException();
    }
    return rightImpl();
  }

  public IntLongIterator next() {
    nextImpl();
    myIterated = true;
    return this;
  }

  /**
   * Called after any call to {@link AbstractLongIteratorWithFlag#nextImpl()}, should return value of iterator.
   * Can safely assume that the iterator is initialized.
   * */
  protected abstract int leftImpl();

  /**
   * Called after any call to {@link AbstractLongIteratorWithFlag#nextImpl()}, should return value of iterator.
   * Can safely assume that the iterator is initialized.
   * */
  protected abstract long rightImpl();

  /**
   * Called before any call to {@link AbstractLongIteratorWithFlag#valueImpl()}, should be used to either initialize the state or advance the iterator.
   * */
  protected abstract void nextImpl() throws NoSuchElementException;
}