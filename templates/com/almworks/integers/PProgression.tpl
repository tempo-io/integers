#APPLY#IJ
package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public abstract class #E#Progression extends Abstract#E#List {
  public static #E#Progression arithmetic(#e# initial, int count, #e# step) {
    return new Arithmetic(initial, count, step);
  }

  public static #E#Progression arithmetic(#e# initial, int count) {
    return new Arithmetic(initial, count, 1);
  }

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

public static class Arithmetic extends #E#Progression {
    private final #e# myInitial;
    private final #e# myStep;
    private final int myCount;

    public Arithmetic(#e# initial, int count, #e# step) {
      myInitial = initial;
      myStep = step;
      myCount = count;
    }

    public int indexOf(#e# value) {
      if (value == myInitial)
        return 0;
      #e# steps = (value - myInitial) / myStep;
      if (steps <= 0 || steps >= myCount)
        return -1;
      return value == get((int)steps) ? (int)steps : -1;
    }

    public int size() {
      return myCount;
    }

    public #e# get(int index) {
      return myInitial + myStep * index;
    }

    @NotNull
    public #E#ListIterator iterator() {
      return new ArithmeticIterator(myInitial, myStep, myCount);
    }

    public static #e#[] fillArray(#e# initial, #e# difference, int count) {
      #e#[] result = new #e#[count];
      #e# value = initial;
      for (int i = 0; i < result.length; i++) {
        result[i] = value;
        value += difference;
      }
      return result;
    }
  }


  public static class ArithmeticIterator extends Abstract#E#ListIndexIterator {
    private final #e# myInitial;
    private final #e# myDifference;

    public ArithmeticIterator(#e# initial, #e# difference, int count) {
      super(0, count);
      myInitial = initial;
      myDifference = difference;
    }

    public #e# absget(int index) throws NoSuchElementException {
      return myInitial + index * myDifference;
    }
  }
}
