/*
 * Copyright 2014 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// CODE GENERATED FROM com/almworks/integers/PIterators.tpl


package com.almworks.integers;

import com.almworks.integers.func.IntToInt;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntIterators {
  /**
   * @return an infinite iterator whose {@code value()} is always equal to the specified value
   */
  public static IntIterator repeat(final int value) {
    return  new AbstractIntIteratorWithFlag() {
      @Override
      protected int valueImpl() {
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
   * @return iterator that repeats the {@code value} {@code count} times
   */
  public static IntIterator repeat(final int value, int count) {
    final int lim0 = Math.max(count, 0);
    return new AbstractIntIterator() {
      int myCount = lim0;

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return myCount > 0;
      }

      @Override
      public IntIterator next() throws NoSuchElementException {
        if (myCount == 0) {
          throw new NoSuchElementException();
        }
        myCount--;
        return this;
      }

      @Override
      public boolean hasValue() {
        return myCount != lim0;
      }

      @Override
      public int value() throws NoSuchElementException {
        if (!hasValue()) {
          throw new NoSuchElementException();
        }
        return value;
      }
    };
  }

  /**
   * @return an infinite iterator that cycles through {@code values}
   */
  public static IntIterator cycle(final int... values) {
    if (values.length == 0) return IntIterator.EMPTY;
    if (values.length == 1) return repeat(values[0]);
    return new AbstractIntIterator() {
      int current = -1;
      public int value() throws NoSuchElementException {
        if (!hasValue()) throw new NoSuchElementException();
        return values[current];
      }

      @Override
      public IntIterator next() throws NoSuchElementException {
        current++;
        if (current == values.length) current = 0;
        return this;
      }

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }

      @Override
      public boolean hasValue() {
        return current != -1;
      }
    };
  }

  /**
   * @see IntProgression#arithmetic(int, int, int)
   * @see #arithmeticProgression(int, int)
   */
  public static IntIterator arithmetic(int start, final int count, final int step) {
    if (step == 0) return repeat(start, count);
    if (count < 0) throw new IllegalArgumentException("count < 0");
    final int myInitialValue = start - step;
    return new AbstractIntIterator() {
      int myCount = count;
      int myValue = myInitialValue;
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return myCount > 0;
      }

      @Override
      public boolean hasValue() {
        return myValue != myInitialValue;
      }

      @Override
      public int value() throws NoSuchElementException {
        if (!hasValue()) throw new NoSuchElementException();
        return myValue;
      }

      @Override
      public IntIterator next() {
        if (myCount <= 0) throw new NoSuchElementException();
        myCount--;
        myValue += step;
        return this;
      }
    };
  }

  /**
   * @see IntProgression#arithmetic(int, int)
   */
  public static IntIterator arithmetic(final int start, final int count) {
    return arithmetic(start, count, 1);
  }

  /**
   * @return an infinite arithmetic progression beginning with {@code start}, increasing by {@code step}.
   * Unlike {@link #arithmetic(int, int, int)} this iterator is infinite
   * @see #arithmetic(int, int, int)
   */
  public static IntIterator arithmeticProgression(final int start, final int step) {
    if (step == 0) {
      return repeat(start);
    }
    return new AbstractIntIterator() {
      int myCurrentValue = start - step;
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }

      @Override
      public boolean hasValue() {
        return myCurrentValue != start - step;
      }

      @Override
      public int value() throws NoSuchElementException {
        if (!hasValue()) {
          throw new NoSuchElementException();
        }
        return myCurrentValue;
      }

      @Override
      public IntIterator next() {
        myCurrentValue += step;
        return this;
      }
    };
  }

  /**
   * @return iterator that can be iterated not more than {@code lim} times.
   */
  public static IntIterator limit(final IntIterable iterable, int lim) {
    final int lim0 = Math.max(lim, 0);
    return new AbstractIntIterator() {
      IntIterator it = iterable.iterator();
      int count = lim0;

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return count > 0 && it.hasNext();
      }

      @Override
      public IntIterator next() throws NoSuchElementException {
        if (count == 0) {
          throw new NoSuchElementException();
        }
        it.next();
        count--;
        return this;
      }

      @Override
      public boolean hasValue() {
        return count != lim0;
      }

      @Override
      public int value() throws NoSuchElementException {
        if (!hasValue()) {
          throw new NoSuchElementException();
        }
        return it.value();
      }
    };
  }

  /**
   * @see IntProgression#range(int, int, int)
   */
  public static IntIterator range(int start, int stop, int step) throws IllegalArgumentException {
    return IntIterators.arithmetic(start, IntProgression.getCount(start, stop, step), step);
  }

  /**
   * @see IntProgression#range(int, int)
   */
  public static IntIterator range(int start, int stop) {
    return range(start, stop, 1);
  }

  /**
   * @see IntProgression#range(int)
   */
  public static IntIterator range(int to) {
    return range(0, to, 1);
  }

  public static IntIterator concat(final IntIterable... iterables) {
    if (iterables == null || iterables.length == 0) return IntIterator.EMPTY;
    return new IntConcatIterator(iterables);
  }

  /**
   * @param iterables array of sorted unique {@code IntIterable}
   * @return union of iterables.
   * If {@code iterables == null || iterables.length == 0}, {@link IntIterator#EMPTY} is returned
   * */
  public static IntIterator unionIterator(IntIterable ... iterables) {
    if (iterables == null) return IntIterator.EMPTY;
    switch (iterables.length) {
      case 0: return IntIterator.EMPTY;
      case 1: return iterables[0].iterator();
      case 2: return new IntUnionIteratorOfTwo(iterables[0], iterables[1]);
      default: return new IntUnionIterator(iterables);
    }
  }

  /**
   * @param iterables array of sorted unique {@code IntIterable}
   * @return intersection of iterables.
   * If {@code iterables == null || iterables.length == 0}, {@link IntIterator#EMPTY} is returned
   * */
  public static IntIterator intersectionIterator(IntIterable ... iterables) {
    if (iterables == null || iterables.length == 0) return IntIterator.EMPTY;
    return new IntIntersectionIterator(iterables);
  }

  /**
   * @param currentModCount A function that returns the current modification count.
   *                        The function argument has no meaning and should be ignored.
   * @return wrapper around the specified iterator that throws ConcurrentModificationException if
   * {@code currentModCount} changes its value.
   * @throws NullPointerException if {@code iterator} or {@code currentModCount} are null.
   */
  @NotNull
  public static IntIterator failFastIterator(IntIterator iterator, final IntToInt currentModCount) throws NullPointerException {
    if (iterator == null || currentModCount == null) {
      throw new NullPointerException();
    }
    return new IntFailFastIterator(iterator) {
      @Override
      protected int getCurrentModCount() {
        return currentModCount.invoke(0);
      }
    };
  }

  /**
   * Beware of boxing: every call to {@link java.util.Iterator#next()} leads to boxing
   */
  public static Iterator<Integer> asIterator(final IntIterator iterator) {
    return new Iterator<Integer>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public Integer next() {
        return iterator.nextValue();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * Beware of unboxing: it always occurs on receiving the next element of {@code iterator}
   */
  public static IntIterator asIntIterator(final Iterator<Integer> iterator) {
    return new IntFindingIterator() {
      @Override
      protected boolean findNext() throws ConcurrentModificationException {
        if (!iterator.hasNext()) return false;
        iterator.next();
        myNext = iterator.next();
        return true;
      }
    };
  }

  public static IntIterator asIntIterator(final IntIterator iterator) {
    return new AbstractIntIterator() {
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return iterator.hasNext();
      }

      @Override
      public boolean hasValue() {
        return iterator.hasValue();
      }

      @Override
      public int value() throws NoSuchElementException {
        return iterator.value();
      }

      @Override
      public IntIterator next() {
        iterator.next();
        return this;
      }
    };
  }
}
