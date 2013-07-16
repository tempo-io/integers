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
  public static LongProgression arithmetic(long initial, int count, long step) {
    return new Arithmetic(initial, count, step);
  }

  public static LongProgression arithmetic(long initial, int count) {
    return new Arithmetic(initial, count, 1);
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

    public static long[] fillArray(long initial, long difference, int count) {
      long[] result = new long[count];
      long value = initial;
      for (int i = 0; i < result.length; i++) {
        result[i] = value;
        value += difference;
      }
      return result;
    }
  }


  public static class ArithmeticIterator extends AbstractLongListIndexIterator {
    private final long myInitial;
    private final long myDifference;

    public ArithmeticIterator(long initial, long difference, int count) {
      super(0, count);
      myInitial = initial;
      myDifference = difference;
    }

    public long absget(int index) throws NoSuchElementException {
      return myInitial + index * myDifference;
    }
  }
}
