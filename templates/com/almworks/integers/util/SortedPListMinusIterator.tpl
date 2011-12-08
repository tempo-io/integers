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
import com.almworks.integers.#E#Iterable;

/**
 * Iterates through two sorted #e# lists in O(N+M), providing values that exist in the
 * first list and do not exist in the second
 */
public class Sorted#E#ListMinusIterator extends Finding#E#Iterator {
  private final #E#Iterator myInclude;
  private final #E#Iterator myExclude;

  private #e# myNext = #EW#.MIN_VALUE;
  private #e# myLastExclude = #EW#.MIN_VALUE;
  private boolean myExcludeIterated;

  public Sorted#E#ListMinusIterator(#E#Iterator include, #E#Iterator exclude) {
    myInclude = include;
    myExclude = exclude;
  }

  public static Sorted#E#ListMinusIterator create(#E#Iterable include, #E#Iterable exclude) {
    return new Sorted#E#ListMinusIterator(include.iterator(), exclude.iterator());
  }

  protected boolean findNext() {
    #e# last = myNext;
    while (myInclude.hasNext()) {
      #e# v = myInclude.nextValue();
      assert v >= last : last + " " + v + " " + myInclude;
      if (accept(v)) {
        myNext = v;
        return true;
      }
      last = v;
    }
    return false;
  }

  private boolean accept(#e# v) {
    if (myExcludeIterated) {
      if (v == myLastExclude)
        return false;
      if (v < myLastExclude)
        return true;
    }
    while (myExclude.hasNext()) {
      #e# n = myExclude.nextValue();
      assert n >= myLastExclude : myLastExclude + " " + n + " " + myExclude;
      myLastExclude = n;
      myExcludeIterated = true;
      if (v == myLastExclude)
        return false;
      if (v < myLastExclude)
        return true;
    }
    return true;
  }

  protected #e# getNext() {
    return myNext;
  }
}
