package com.almworks.integers;


import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class IntIterators {
  public static IntIterator range(final int from, final int to, final int step) throws IllegalArgumentException {
    if (step == 0) throw new IllegalArgumentException("step = 0");
    int myCount = 1 + (int)((to - 1 - from) / step);
    if (myCount < 0) throw new IllegalArgumentException();
    return arithmetic(from, myCount, step);
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
}
