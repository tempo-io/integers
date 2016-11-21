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

public abstract class #E#Progression extends Abstract#E#List {
  /**
   * @return list containing arithmetic progression.
   */
  public static #E#Progression arithmetic(#e# start, int count, #e# step) {
    return new Arithmetic(start, count, step);
  }

  public static #E#Progression arithmetic(#e# start, int count) {
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
  public static #E#Progression range(#e# start, #e# stop, #e# step) throws IllegalArgumentException {
    return #E#Progression.arithmetic(start, getCount(start, stop, step), step);
  }

  /**
   * Examples: range(4, 7) -> (4, 5, 6); range(0, -3) -> ()
   * @return {@link #range(#e#, #e#, #e#)} with the specified start and stop and {@code step = 1}
   * @see #range(#e#, #e#, #e#)
   */
  public static #E#Progression range(#e# start, #e# stop) throws IllegalArgumentException {
    return range(start, stop, 1);
  }

  /**
   * Examples: range(4) -> (0, 1, 2, 3); range(-3) -> ()
   * @return {@link #range(#e#, #e#, #e#)} with {@code start = 0}, the specified stop and {@code step = 1}
   * @see #range(#e#, #e#, #e#)
   */
  public static #E#Progression range(#e# stop) throws IllegalArgumentException {
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
  public static int getCount(#e# start, #e# stop, #e# step) {
    if (step == 0) {
      throw new IllegalArgumentException("step = 0");
    }
    if (start == stop || (start < stop == step < 0)) {
      return 0;
    }
    return 1 + (int)((Math.abs(stop - start) - 1) / Math.abs(step));
  }

  public static class Arithmetic extends #E#Progression {
    private final #e# myStart;
    private final #e# myStep;
    private final int myCount;

    public Arithmetic(#e# start, int count, #e# step) {
      myStart = start;
      myStep = step;
      myCount = count;
    }

    public int indexOf(#e# value) {
      if (value == myStart)
        return 0;
      #e# steps = (value - myStart) / myStep;
      if (steps <= 0 || steps >= myCount)
        return -1;
      return value == get((int)steps) ? (int)steps : -1;
    }

    public int size() {
      return myCount;
    }

    public #e# get(int index) {
      return myStart + myStep * index;
    }

    @NotNull
    public #E#ListIterator iterator() {
      return new ArithmeticIterator(myStart, myStep, myCount);
    }

    public static #e#[] nativeArray(#e# start, int count, #e# step) {
      #e#[] result = new #e#[count];
      #e# value = start;
      for (int i = 0; i < result.length; i++) {
        result[i] = value;
        value += step;
      }
      return result;
    }

    @Override
    public #e#[] toNativeArray(int startIndex, #e#[] dest, int destOffset, int length) {
      #e# value = get(startIndex);
      for (int i = 0; i < length; i++) {
        dest[destOffset++] = value;
        value += myStep;
      }
      return dest;
    }
  }


  public static class ArithmeticIterator extends Abstract#E#ListIndexIterator {
    private final #e# myStart;
    private final #e# myStep;

    public ArithmeticIterator(#e# start, #e# step, int count) {
      super(0, count);
      myStart = start;
      myStep = step;
    }

    public #e# absget(int index) throws NoSuchElementException {
      return myStart + index * myStep;
    }
  }
}
