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

package com.almworks.integers;

import com.almworks.integers.func.IntToInt;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class #E#Iterators {
  /**
   * @return an infinite iterator whose {@code value()} is always equal to the specified value
   */
  public static #E#Iterator repeat(final #e# value) {
    return  new Abstract#E#IteratorWithFlag() {
      @Override
      protected #e# valueImpl() {
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
  public static #E#Iterator repeat(final #e# value, int count) {
    final int lim0 = Math.max(count, 0);
    return new Abstract#E#Iterator() {
      int count = lim0;

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return count > 0;
      }

      @Override
      public #E#Iterator next() throws NoSuchElementException {
        if (count == 0) {
          throw new NoSuchElementException();
        }
        count--;
        return this;
      }

      @Override
      public boolean hasValue() {
        return count != lim0;
      }

      @Override
      public #e# value() throws NoSuchElementException {
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
  public static #E#Iterator cycle(final #e#... values) {
    if (values.length == 0) return #E#Iterator.EMPTY;
    if (values.length == 1) return repeat(values[0]);
    return new Abstract#E#Iterator() {
      int current = -1;
      public #e# value() throws NoSuchElementException {
        if (!hasValue()) throw new NoSuchElementException();
        return values[current];
      }

      @Override
      public #E#Iterator next() throws NoSuchElementException {
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
   * @see #E#Progression#arithmetic(#e#, int, #e#)
   * @see #arithmeticProgression(#e#, #e#)
   */
  public static #E#Iterator arithmetic(#e# start, final int count, final #e# step) {
    if (step == 0) return repeat(start, count);
    if (count < 0) throw new IllegalArgumentException("count < 0");
    final #e# myInitialValue = start - step;
    return new Abstract#E#Iterator() {
      int myCount = count;
      #e# myValue = myInitialValue;
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return myCount > 0;
      }

      @Override
      public boolean hasValue() {
        return myValue != myInitialValue;
      }

      @Override
      public #e# value() throws NoSuchElementException {
        if (!hasValue()) throw new NoSuchElementException();
        return myValue;
      }

      @Override
      public #E#Iterator next() {
        if (myCount <= 0) throw new NoSuchElementException();
        myCount--;
        myValue += step;
        return this;
      }
    };
  }

  /**
   * @see #E#Progression#arithmetic(#e#, int)
   */
  public static #E#Iterator arithmetic(final #e# start, final int count) {
    return arithmetic(start, count, 1);
  }

  /**
   * @return an infinite arithmetic progression beginning with {@code start}, increasing by {@code step}.
   * Unlike {@link #arithmetic(#e#, int, #e#)} this iterator is infinite
   * @see #arithmetic(#e#, int, #e#)
   */
  public static #E#Iterator arithmeticProgression(final #e# start, final #e# step) {
    if (step == 0) {
      return repeat(start);
    }
    return new Abstract#E#Iterator() {
      #e# myCurrentValue = start - step;
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }

      @Override
      public boolean hasValue() {
        return myCurrentValue != start - step;
      }

      @Override
      public #e# value() throws NoSuchElementException {
        if (!hasValue()) {
          throw new NoSuchElementException();
        }
        return myCurrentValue;
      }

      @Override
      public #E#Iterator next() {
        myCurrentValue += step;
        return this;
      }
    };
  }

  /**
   * @return iterator that can be iterated not more than {@code lim} times.
   */
  public static #E#Iterator limit(final #E#Iterable iterable, int lim) {
    final int lim0 = Math.max(lim, 0);
    return new Abstract#E#Iterator() {
      #E#Iterator it = iterable.iterator();
      int count = lim0;

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return count > 0 && it.hasNext();
      }

      @Override
      public #E#Iterator next() throws NoSuchElementException {
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
      public #e# value() throws NoSuchElementException {
        if (!hasValue()) {
          throw new NoSuchElementException();
        }
        return it.value();
      }
    };
  }

  /**
   * @see #E#Progression#range(#e#, #e#, #e#)
   */
  public static #E#Iterator range(#e# start, #e# stop, #e# step) throws IllegalArgumentException {
    return #E#Iterators.arithmetic(start, #E#Progression.getCount(start, stop, step), step);
  }

  /**
   * @see #E#Progression#range(#e#, #e#)
   */
  public static #E#Iterator range(#e# start, #e# stop) {
    return range(start, stop, 1);
  }

  /**
   * @see #E#Progression#range(#e#)
   */
  public static #E#Iterator range(#e# to) {
    return range(0, to, 1);
  }

  public static #E#Iterator concat(final #E#Iterable... iterables) {
    if (iterables == null || iterables.length == 0) return #E#Iterator.EMPTY;
    return new #E#ConcatIterator(iterables);
  }

  /**
   * @param iterables array of sorted unique {@code #E#Iterable}
   * @return union of iterables.
   * If {@code iterables == null || iterables.length == 0}, {@link #E#Iterator#EMPTY} is returned
   * */
  public static #E#Iterator unionIterator(#E#Iterable ... iterables) {
    if (iterables == null) return #E#Iterator.EMPTY;
    switch (iterables.length) {
      case 0: return #E#Iterator.EMPTY;
      case 1: return iterables[0].iterator();
      case 2: return new #E#UnionIteratorOfTwo(iterables[0], iterables[1]);
      default: return new #E#UnionIterator(iterables);
    }
  }

  /**
   * @param iterables array of sorted unique {@code #E#Iterable}
   * @return intersection of iterables.
   * If {@code iterables == null || iterables.length == 0}, {@link #E#Iterator#EMPTY} is returned
   * */
   public static #E#Iterator intersectionIterator(#E#Iterable ... iterables) {
    if (iterables == null || iterables.length == 0) return #E#Iterator.EMPTY;
    return new #E#IntersectionIterator(iterables);
  }

  /**
   * @param currentModCount A function that returns the current modification count.
   *                        The function argument has no meaning and should be ignored.
   * @return wrapper around the specified iterator that throws ConcurrentModificationException if
   * {@code currentModCount} changes its value.
   * @throws NullPointerException if {@code iterator} or {@code currentModCount} are null.
   */
  @NotNull
  public static #E#Iterator failFastIterator(#E#Iterator iterator, final IntToInt currentModCount) throws NullPointerException {
    if (iterator == null || currentModCount == null) {
      throw new NullPointerException();
    }
    return new #E#FailFastIterator(iterator) {
      @Override
      protected int getCurrentModCount() {
        return currentModCount.invoke(0);
      }
    };
  }

}
