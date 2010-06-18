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

package com.almworks.integers.util;

import com.almworks.integers.#E#Iterator;

/**
 * Iterates through two sorted int lists in O(N+M), providing values that exist in
 * both lists
 */
public class Sorted#E#ListIntersectionIterator extends Finding#E#Iterator {
  private final #E#Iterator myFirst;
  private final #E#Iterator mySecond;

  private #e# myNext = #EW#.MIN_VALUE;
  private #e# myLastSecond = #EW#.MIN_VALUE;
  private boolean mySecondIterated;

  public Sorted#E#ListIntersectionIterator(#E#Iterator first, #E#Iterator second) {
    myFirst = first;
    mySecond = second;
  }

  protected boolean findNext() {
    #e# last = myNext;
    while (myFirst.hasNext()) {
      #e# v = myFirst.next();
      assert v >= last : last + " " + v + " " + myFirst;
      if (accept(v)) {
        myNext = v;
        return true;
      }
      last = v;
    }
    return false;
  }

  private boolean accept(#e# v) {
    if (mySecondIterated) {
      if (v == myLastSecond)
        return true;
      if (v < myLastSecond)
        return false;
    }
    while (mySecond.hasNext()) {
      #e# n = mySecond.next();
      assert n >= myLastSecond : myLastSecond + " " + n + " " + mySecond;
      myLastSecond = n;
      mySecondIterated = true;
      if (v == myLastSecond)
        return true;
      if (v < myLastSecond)
        return false;
    }
    return false;
  }

  protected #e# getNext() {
    return myNext;
  }
}
