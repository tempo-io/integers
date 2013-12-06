package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class EmptyIntLongIterator extends AbstractIntLongIterator {
  @Override
  public boolean hasNext() throws ConcurrentModificationException {
    return false;
  }

  @Override
  public IntLongIterator next() {
    throw new NoSuchElementException();
  }

  @Override
  public boolean hasValue() {
    return false;
  }

  @Override
  public int left() throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  @Override
  public long right() throws NoSuchElementException {
    throw new NoSuchElementException();
  }
}
