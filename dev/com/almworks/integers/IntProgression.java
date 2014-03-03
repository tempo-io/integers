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

// CODE GENERATED FROM com/almworks/integers/PProgression.tpl



package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public abstract class IntProgression extends AbstractIntList {
  /**
   * @return list containing arithmetic progression.
   */
  public static IntProgression arithmetic(int start, int count, int step) {
    return new Arithmetic(start, count, step);
  }

  public static IntProgression arithmetic(int start, int count) {
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
  public static IntProgression range(int start, int stop, int step) throws IllegalArgumentException {
    return IntProgression.arithmetic(start, getCount(start, stop, step), step);
  }

  /**
   * Examples: range(4, 7) -> (4, 5, 6); range(0, -3) -> ()
   * @return {@link #range(int, int, int)} with the specified start and stop and {@code step = 1}
   * @see #range(int, int, int)
   */
  public static IntProgression range(int start, int stop) throws IllegalArgumentException {
    return range(start, stop, 1);
  }

  /**
   * Examples: range(4) -> (0, 1, 2, 3); range(-3) -> ()
   * @return {@link #range(int, int, int)} with {@code start = 0}, the specified stop and {@code step = 1}
   * @see #range(int, int, int)
   */
  public static IntProgression range(int stop) throws IllegalArgumentException {
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
  public static int getCount(int start, int stop, int step) {
    if (step == 0) {
      throw new IllegalArgumentException("step = 0");
    }
    if (start == stop || (start < stop == step < 0)) {
      return 0;
    }
    return 1 + (int)((Math.abs(stop - start) - 1) / Math.abs(step));
  }

  public static class Arithmetic extends IntProgression {
    private final int myStart;
    private final int myStep;
    private final int myCount;

    public Arithmetic(int start, int count, int step) {
      myStart = start;
      myStep = step;
      myCount = count;
    }

    public int indexOf(int value) {
      if (value == myStart)
        return 0;
      int steps = (value - myStart) / myStep;
      if (steps <= 0 || steps >= myCount)
        return -1;
      return value == get((int)steps) ? (int)steps : -1;
    }

    public int size() {
      return myCount;
    }

    public int get(int index) {
      return myStart + myStep * index;
    }

    @NotNull
    public IntListIterator iterator() {
      return new ArithmeticIterator(myStart, myStep, myCount);
    }

    public static int[] nativeArray(int start, int count, int step) {
      int[] result = new int[count];
      int value = start;
      for (int i = 0; i < result.length; i++) {
        result[i] = value;
        value += step;
      }
      return result;
    }

    @Override
    public int[] toNativeArray(int startIndex, int[] dest, int destOffset, int length) {
      int value = get(startIndex);
      for (int i = 0; i < length; i++) {
        dest[destOffset++] = value;
        value += myStep;
      }
      return dest;
    }
  }


  public static class ArithmeticIterator extends AbstractIntListIndexIterator {
    private final int myStart;
    private final int myStep;

    public ArithmeticIterator(int start, int step, int count) {
      super(0, count);
      myStart = start;
      myStep = step;
    }

    public int absget(int index) throws NoSuchElementException {
      return myStart + index * myStep;
    }
  }
}
