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
   * @return an infinite iterator that cycles through {@code values}
   */
  public static LongIterator cycle(final long... values) {
    if (values.length == 0) return LongIterator.EMPTY;
    if (values.length == 1) return repeat(values[0]);
    return new AbstractLongIterator() {
      int current = -1;
      public long value() throws NoSuchElementException {
        if (!hasValue()) throw new NoSuchElementException();
        return values[current];
      }

      @Override
      public LongIterator next() throws NoSuchElementException {
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
   * @see LongProgression#arithmetic(long, int, long)
   * @see #arithmeticProgression(long, long)
   */
  public static LongIterator arithmetic(long start, final int count, final long step) {
    if (step == 0) throw new IllegalArgumentException("step = 0");
    if (count < 0) throw new IllegalArgumentException("count < 0");
    final long myInitialValue = start - step;
    return new AbstractLongIterator() {
      int myCount = count;
      long myValue = myInitialValue;
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return myCount > 0;
      }

      @Override
      public boolean hasValue() {
        return myValue != myInitialValue;
      }

      @Override
      public long value() throws NoSuchElementException {
        if (!hasValue()) throw new NoSuchElementException();
        return myValue;
      }

      @Override
      public LongIterator next() {
        if (myCount <= 0) throw new NoSuchElementException();
        myCount--;
        myValue += step;
        return this;
      }
    };
  }

  /**
   * @see LongProgression#arithmetic(long, int)
   */
  public static LongIterator arithmetic(final long start, final int count) {
    return arithmetic(start, count, 1);
  }

  /**
   * @return an infinite arithmetic progression beginning with {@code start}, increasing by {@code step}.
   * Unlike {@link #arithmetic(long, int, long)} this iterator is infinite
   * @see #arithmetic(long, int, long)
   */
  public static LongIterator arithmeticProgression(final long start, final long step) {
    if (step == 0) {
      return repeat(start);
    }
    return new AbstractLongIterator() {
      long myCurrentValue = start - step;
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }

      @Override
      public boolean hasValue() {
        return myCurrentValue != start - step;
      }

      @Override
      public long value() throws NoSuchElementException {
        if (!hasValue()) {
          throw new NoSuchElementException();
        }
        return myCurrentValue;
      }

      @Override
      public LongIterator next() {
        myCurrentValue += step;
        return this;
      }
    };
  }

  /**
   * @return iterator that can be iterated not more than {@code lim} times.
   */
  public static LongIterator limit(final LongIterable iterable, int lim) {
    final int lim0 = Math.max(lim, 0);
    return new AbstractLongIterator() {
      LongIterator it = iterable.iterator();
      int count = lim0;

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return count > 0 && it.hasNext();
      }

      @Override
      public LongIterator next() throws NoSuchElementException {
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
      public long value() throws NoSuchElementException {
        if (!hasValue()) {
          throw new NoSuchElementException();
        }
        return it.value();
      }
    };
  }

  /**
   * @see LongProgression#range(long, long, long)
   */
  public static LongIterator range(long start, long stop, long step) throws IllegalArgumentException {
    return LongIterators.arithmetic(start, LongProgression.getCount(start, stop, step), step);
  }

  /**
   * @see LongProgression#range(long, long)
   */
  public static LongIterator range(long start, long stop) {
    return range(start, stop, 1);
  }

  /**
   * @see LongProgression#range(long)
   */
  public static LongIterator range(long to) {
    return range(0, to, 1);
  }

  public static LongIterator concat(final LongIterable... iterables) {
    if (iterables == null || iterables.length == 0) return LongIterator.EMPTY;
    return new LongConcatIterator(iterables);
  }

  /**
   * @param iterables array of sorted unique {@code LongIterable}
   * @return union of iterables.
   * If {@code iterables == null || iterables.length == 0}, {@link LongIterator#EMPTY} is returned
   * */
  public static LongIterator unionIterator(LongIterable ... iterables) {
    if (iterables == null) return LongIterator.EMPTY;
    switch (iterables.length) {
      case 0: return LongIterator.EMPTY;
      case 1: return iterables[0].iterator();
      case 2: return new LongUnionIteratorOfTwo(iterables[0], iterables[1]);
      default: return new LongUnionIterator(iterables);
    }
  }

  /**
   * @param iterables array of sorted unique {@code LongIterable}
   * @return intersection of iterables.
   * If {@code iterables == null || iterables.length == 0}, {@link LongIterator#EMPTY} is returned
   * */
   public static LongIterator intersectionIterator(LongIterable ... iterables) {
    if (iterables == null || iterables.length == 0) return LongIterator.EMPTY;
    return new LongIntersectionIterator(iterables);
  }

  /**
   * @param currentModCount A function that returns the current modification count.
   *                        The function argument has no meaning and should be ignored.
   * @return wrapper around the specified iterator that throws ConcurrentModificationException if
   * {@code currentModCount} changes its value.
   * @throws NullPointerException if {@code iterator} or {@code currentModCount} are null.
   */
  @NotNull
  public static LongIterator failFastIterator(LongIterator iterator, final IntToInt currentModCount) throws NullPointerException {
    if (iterator == null || currentModCount == null) {
      throw new NullPointerException();
    }
    return new LongFailFastIterator(iterator) {
      @Override
      protected int getCurrentModCount() {
        return currentModCount.invoke(0);
      }
    };
  }

}
