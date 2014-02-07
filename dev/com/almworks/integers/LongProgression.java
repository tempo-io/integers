/*
* Copyright 2010 ALM Works Ltd
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

// CODE GENERATED FROM com/almworks/integers/PProgression.tpl

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
   * @param stop ending value, exclusive; may be less than {@code start}, then {@code step} must be negative
   * @param step may be negative, then {@code stop} must be not greater than {@code start}
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
   * Returns the minimum value {@code count} such that {@code start + step * count} is
   * between start and stop
   * @param start starting value, inclusive
   * @param stop ending value, exclusive; may be less than {@code start}, then {@code step} must be negative
   * @param step may be negative, then {@code stop} must be not greater than {@code start}
   * @return minimum value {@code count} such that {@code start + step * count} is
   * between start and stop
   * @throws IllegalArgumentException {@code if step == 0 || (start < stop && step < 0) || (stop < start && step > 0)}
   */
  public static int getCount(long start, long stop, long step) {
    if (step == 0) {
      throw new IllegalArgumentException("step = 0");
    }
    if (start == stop || (start < stop && step < 0) || (stop < start && step > 0)) {
      return 0;
    }
    return 1 + (int)((Math.abs(stop - start) - 1) / Math.abs(step));
  }

  public static class Arithmetic extends LongProgression {
    private final long myInitial;
    private final long myStep;
    private final int myCount;

    public Arithmetic(long initial, int count, long step) {
      myInitial = initial;
      myStep = step;
      myCount = count;
    }

    public int indexOf(long value) {
      if (value == myInitial)
        return 0;
      long steps = (value - myInitial) / myStep;
      if (steps <= 0 || steps >= myCount)
        return -1;
      return value == get((int)steps) ? (int)steps : -1;
    }

    public int size() {
      return myCount;
    }

    public long get(int index) {
      return myInitial + myStep * index;
    }

    @NotNull
    public LongListIterator iterator() {
      return new ArithmeticIterator(myInitial, myStep, myCount);
    }

    public static long[] fillArray(long initial, long step, int count) {
      long[] result = new long[count];
      long value = initial;
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
    private final long myInitial;
    private final long myDifference;

    public ArithmeticIterator(long start, long step, int count) {
      super(0, count);
      myInitial = start;
      myDifference = step;
    }

    public long absget(int index) throws NoSuchElementException {
      return myInitial + index * myDifference;
    }
  }
}
