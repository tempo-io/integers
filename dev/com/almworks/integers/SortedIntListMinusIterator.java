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



package com.almworks.integers;

/**
 * Iterates through two sorted int lists in O(N+M), providing values that exist in the
 * first list and do not exist in the second
 */
public class SortedIntListMinusIterator extends FindingIntIterator {
  private final IntIterator myInclude;
  private final IntIterator myExclude;

  private int myNext = Integer.MIN_VALUE;
  private int myLastExclude = Integer.MIN_VALUE;
  private boolean myExcludeIterated;

  public SortedIntListMinusIterator(IntIterator include, IntIterator exclude) {
    myInclude = include;
    myExclude = exclude;
  }

  public static SortedIntListMinusIterator create(IntIterable include, IntIterable exclude) {
    return new SortedIntListMinusIterator(include.iterator(), exclude.iterator());
  }

  protected boolean findNext() {
    int last = myNext;
    while (myInclude.hasNext()) {
      int v = myInclude.nextValue();
      assert v >= last : last + " " + v + " " + myInclude;
      if (accept(v)) {
        myNext = v;
        return true;
      }
      last = v;
    }
    return false;
  }

  private boolean accept(int v) {
    if (myExcludeIterated) {
      if (v == myLastExclude)
        return false;
      if (v < myLastExclude)
        return true;
    }
    while (myExclude.hasNext()) {
      int n = myExclude.nextValue();
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

  protected int getNext() {
    return myNext;
  }
}
