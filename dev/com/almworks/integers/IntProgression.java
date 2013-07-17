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

public abstract class IntProgression extends AbstractIntList {
  public static IntProgression arithmetic(int initial, int count, int step) {
    return new Arithmetic(initial, count, step);
  }

  public static IntProgression arithmetic(int initial, int count) {
    return new Arithmetic(initial, count, 1);
  }

  public static class Arithmetic extends IntProgression {
    private final int myInitial;
    private final int myStep;
    private final int myCount;

    public Arithmetic(int initial, int count, int step) {
      myInitial = initial;
      myStep = step;
      myCount = count;
    }

    public int indexOf(int value) {
      if (value == myInitial)
        return 0;
      int steps = (value - myInitial) / myStep;
      if (steps <= 0 || steps >= myCount)
        return -1;
      return value == get((int)steps) ? (int)steps : -1;
    }

    public int size() {
      return myCount;
    }

    public int get(int index) {
      return myInitial + myStep * index;
    }

    @NotNull
    public IntListIterator iterator() {
      return new ArithmeticIterator(myInitial, myStep, myCount);
    }

    public static int[] fillArray(int initial, int difference, int count) {
      int[] result = new int[count];
      int value = initial;
      for (int i = 0; i < result.length; i++) {
        result[i] = value;
        value += difference;
      }
      return result;
    }
  }


  public static class ArithmeticIterator extends AbstractIntListIndexIterator {
    private final int myInitial;
    private final int myDifference;

    public ArithmeticIterator(int initial, int difference, int count) {
      super(0, count);
      myInitial = initial;
      myDifference = difference;
    }

    public int absget(int index) throws NoSuchElementException {
      return myInitial + index * myDifference;
    }
  }
}
