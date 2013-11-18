package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class LongIterators {
  public static LongIterator repeat(final long value) {
    return  new AbstractLongIteratorWithFlag() {
      @Override
      protected long valueImpl() {
        return value;
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {}

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }
    };
  }

  public static LongIterator repeat(final long... values) {
    if (values.length == 0) return LongIterator.EMPTY;
    return new AbstractLongIteratorWithFlag() {
      int current = -1;
      @Override
      protected long valueImpl() {
        current++;
        if (current == values.length) current = 0;
        return values[current];
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {}

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }
    };
  }

  public static LongIterator arithmeticProgression(final long start, final long step) {
    return  new AbstractLongIteratorWithFlag() {
      long currentValue = start - step;
      @Override
      protected long valueImpl() {
        currentValue += step;
        return currentValue;
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {}

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }
    };
  }
}
