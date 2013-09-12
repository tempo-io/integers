package com.almworks.integers.util;

import com.almworks.integers.*;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * An LongIterator over the specified LongIterable that deviates from a normal LongIterator in a few useful ways:
 * <ol>
 *   <li>If {@code value()} is called without first calling next(): instead of throwing NSEE,
 *   this class calls next() on the given iterator.
 *   <li>{@code hasValue()} always true
 *   <li>If {@code next()} is called when {@code hasNext()} is false: this class does not throw NSEE,
 *   and returns postValue in {@code value()}.
 * </ol>
 * */
public class WithPrePostValueLongIterator extends AbstractLongIterator {
  private final LongIterator myIterator;
  private final long myPostValue;
  private boolean myIterated;

  public WithPrePostValueLongIterator(LongIterable iterable, int postValue) {
    myIterator = iterable.iterator();
    myPostValue = postValue;
  }

  @Override
  public boolean hasNext() throws ConcurrentModificationException {
    return myIterator.hasNext();
  }

  @Override
  public long value() throws NoSuchElementException {
    if (!myIterated) next();
    return myIterated ? myIterator.value() : myPostValue;
  }

  @Override
  public LongIterator next() {
    if (myIterator.hasNext()) {
      myIterator.next();
      myIterated = true;
    } else {
      // Turning on postValue
      myIterated = false;
    }
    return this;
  }

  public boolean hasValue() {
    return !myIterated;
  }
}