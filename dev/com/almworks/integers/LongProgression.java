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

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public abstract class LongProgression extends AbstractLongList {
  /**
   * @return list containing arithmetic progression.
   */
  public static LongProgression arithmetic(long start, int count, long step) {
    return new Arithmetic(start, count, step);
  }

  public static LongProgression arithmetic(long start, int count) {
    return new Arithmetic(start, count, 1);
  }

  /**
   * Examples: range(3, 10, 3) -> (3, 6, 9); range(3, -10, -4) -> (3, -1, -5, -9)
   * @param start starting value, inclusive
   * @param stop ending value, exclusive. Can be less than or equal to {@code start}.
   * @param step step between adjacent values in list. Can be negative, should not be zero.
   * @return list containing arithmetic progression.
   * @throws IllegalArgumentException if {@code step == 0}
   */
  public static LongProgression range(long start, long stop, long step) throws IllegalArgumentException {
    return LongProgression.arithmetic(start, getCount(start, stop, step), step);
  }

  /**
   * Examples: range(4, 7) -> (4, 5, 6); range(0, -3) -> ()
   * @return {@link #range(long, long, long)} with the specified start and stop and {@code step = 1}
   * @see #range(long, long, long)
   */
  public static LongProgression range(long start, long stop) throws IllegalArgumentException {
    return range(start, stop, 1);
  }

  /**
   * Examples: range(4) -> (0, 1, 2, 3); range(-3) -> ()
   * @return {@link #range(long, long, long)} with {@code start = 0}, the specified stop and {@code step = 1}
   * @see #range(long, long, long)
   */
  public static LongProgression range(long stop) throws IllegalArgumentException {
    return range(0, stop, 1);
  }

  /**
   * Returns the minimum value {@code count} such that {@code start + step * count} is between start and stop
   * @param start starting value, inclusive
   * @param stop ending value, exclusive. Can be less than or equal to {@code start}.
   * @param step step between adjacent values in list. Can be negative, should not be zero.
   * @return minimum value {@code count} such that {@code start + step * count} is between start and stop
   * @throws IllegalArgumentException {@code if step == 0}
   */
  public static int getCount(long start, long stop, long step) {
    if (step == 0) {
      throw new IllegalArgumentException("step = 0");
    }
    if (start == stop || (start < stop == step < 0)) {
      return 0;
    }
    return 1 + (int)((Math.abs(stop - start) - 1) / Math.abs(step));
  }

  public static class Arithmetic extends LongProgression {
    private final long myStart;
    private final long myStep;
    private final int myCount;

    public Arithmetic(long start, int count, long step) {
      myStart = start;
      myStep = step;
      myCount = count;
    }

    public int indexOf(long value) {
      if (value == myStart)
        return 0;
      long steps = (value - myStart) / myStep;
      if (steps <= 0 || steps >= myCount)
        return -1;
      return value == get((int)steps) ? (int)steps : -1;
    }

    public int size() {
      return myCount;
    }

    public long get(int index) {
      return myStart + myStep * index;
    }

    @NotNull
    public LongListIterator iterator() {
      return new ArithmeticIterator(myStart, myStep, myCount);
    }

    public static long[] nativeArray(long start, int count, long step) {
      long[] result = new long[count];
      long value = start;
      for (int i = 0; i < result.length; i++) {
        result[i] = value;
        value += step;
      }
      return result;
    }

    @Override
    public long[] toNativeArray(int startIndex, long[] dest, int destOffset, int length) {
      long value = get(startIndex);
      for (int i = 0; i < length; i++) {
        dest[destOffset++] = value;
        value += myStep;
      }
      return dest;
    }
  }


  public static class ArithmeticIterator extends AbstractLongListIndexIterator {
    private final long myStart;
    private final long myStep;

    public ArithmeticIterator(long start, long step, int count) {
      super(0, count);
      myStart = start;
      myStep = step;
    }

    public long absget(int index) throws NoSuchElementException {
      return myStart + index * myStep;
    }
  }
}
