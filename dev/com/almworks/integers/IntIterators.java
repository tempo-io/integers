package com.almworks.integers;


import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class IntIterators {
  public static IntIterator range(final int start, final int stop, final int step) throws IllegalArgumentException {
    return IntIterators.arithmetic(start, LongProgression.getCount(start, stop, step), step);
  }

  // todo javadoc from range(from, to, step)
  public static IntIterator range(int from, int to) {
    return range(from, to, 1);
  }

  public static IntIterator range(int to) {
    return range(0, to, 1);
  }

  public static IntIterator arithmetic(final int initial, final int count, final int step) {
    if (step == 0) throw new IllegalArgumentException("step = 0");
    return new AbstractIntIterator() {
      int myCount = count;
      int myValue = initial - step;

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return myCount > 0;
      }

      @Override
      public IntIterator next() {
        myCount--;
        myValue += step;
        return this;
      }

      @Override
      public int value() throws NoSuchElementException {
        if (myCount == count) throw new NoSuchElementException();
        return myValue;
      }
    };
  }

  /**
   * @return an infinite iterator whose {@code value()} is always equal to the specified value
   */
  public static IntIterator repeat(final int value) {
    return  new AbstractIntIterator() {
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }

      @Override
      public int value() throws NoSuchElementException {
        return value;
      }

      @Override
      public IntIterator next() {
        return this;
      }
    };
  }
}
