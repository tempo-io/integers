package com.almworks.integers;

import com.almworks.integers.func.IntFunction;
import com.almworks.integers.util.*;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class LongIterators {
  /**
   * @return an infinite iterator whose {@code value()} is always equal to the specified value
   */
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

  /**
   * @return an iterator that infinitely cycles through {@code values}
   */
  public static LongIterator cycle(final long... values) {
    if (values.length == 0) return LongIterator.EMPTY;
    if (values.length == 1) return repeat(values[0]);
    return new AbstractLongIteratorWithFlag() {
      int current = -1;
      @Override
      protected long valueImpl() {
        return values[current];
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {
        current++;
        if (current == values.length) current = 0;
      }

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }
    };
  }

  /**
   * @return an arithmetic progression beginning with {@code start}, increasing by {@code step}
   */
  public static LongIterator arithmeticProgression(final long start, final long step) {
    return  new AbstractLongIteratorWithFlag() {
      long currentValue = start - step;
      @Override
      protected long valueImpl() {
        return currentValue;
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {
        currentValue += step;
      }

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }
    };
  }

  /**
   * @return iterator that can be iterated not more than {@code lim} times.
   */
  public static LongIterator limit(final LongIterable iterable, final int lim) {
    return new FindingLongIterator() {
      LongIterator it = iterable.iterator();
      int count = lim;
      @Override
      protected boolean findNext() {
        if (count == 0 || !it.hasNext()) return false;
        count--;
        myCurrent = it.nextValue();
        return true;
      }
    };
  }

  public static LongIterator arithmetic(final long initial, final int count, final long step) {
    if (step == 0) throw new IllegalArgumentException("step = 0");
    return new AbstractLongIteratorWithFlag() {
      int myCount = count;
      long myValue = initial - step;

      @Override
      protected void nextImpl() throws NoSuchElementException {
        myCount--;
        myValue += step;
      }

      @Override
      protected long valueImpl() {
        return myValue;
      }

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return myCount > 0;
      }
    };
  }

  public static LongIterator arithmetic(final long initial, final int count) {
    return arithmetic(initial, count, 1);
  }

  /**
   * @param from starting index, inclusive
   * @param to ending index, exclusive
   * @return an iterator containing arithmetic progression.
   * @throws IllegalArgumentException if {@code step == 0}
   */

  /**
   * {@link LongProgression#range(long, long, long)}
   */
  public static LongIterator range(final long from, final long to, final long step) throws IllegalArgumentException {
    if (step == 0) throw new IllegalArgumentException("step = 0");
    int myCount = 1 + (int)((to - 1 - from) / step);
    if (myCount < 0) throw new IllegalArgumentException();
    return arithmetic(from, myCount, step);
  }

  // todo javadoc from range(from, to, step)
  public static LongIterator range(long from, long to) {
    return range(from, to, 1);
  }

  public static LongIterator range(long to) {
    return range(0, to, 1);
  }

  public static LongIterator concat(final LongIterable... iterables) {
    if (iterables == null || iterables.length == 0) return LongIterator.EMPTY;
    return new LongConcatIterator(iterables);
  }

  /**
   * @param iterables array of sorted {@code LongIterable}
   * @return union of iterables.
   * */
  public static LongIterator unionIterator(LongIterable ... iterables) {
    switch (iterables.length) {
      case 0: return LongIterator.EMPTY;
      case 1: return iterables[0].iterator();
      case 2: return LongUnionIteratorTwo.create(iterables[0], iterables[1]);
      default: return new LongUnionIterator(iterables);
    }
  }

  /**
   * @param iterables array of sorted {@code LongIterable}
   * @return intersection of iterables.
   * */
  public static LongIterator intersectionIterator(LongIterable ... iterables) {
    return new LongIntersectionIterator(iterables);
  }

  public static LongIterator failFastIterator(LongIterator iterator, final IntFunction currentModCount) {
    return new FailFastLongIterator(iterator) {
      @Override
      protected int getCurrentModCount() {
        return currentModCount.invoke(0);
      }
    };
  }

}
