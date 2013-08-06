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

// CODE GENERATED FROM com/almworks/integers/util/SortedPListIntersectionIterator.tpl


package com.almworks.integers.util;

import com.almworks.integers.LongIterator;
import com.almworks.integers.LongIterable;

/**
 * Iterates through two sorted int lists in O(N+M), providing values that exist in
 * both lists
 */
public class SortedLongListIntersectionIterator extends FindingLongIterator {
  private final LongIterator myFirst;
  private final LongIterator mySecond;

  private long myNext = Long.MIN_VALUE;
  private long myLastSecond = Long.MIN_VALUE;
  private boolean mySecondIterated;

  public SortedLongListIntersectionIterator(LongIterator first, LongIterator second) {
    myFirst = first;
    mySecond = second;
  }

  public static SortedLongListIntersectionIterator create(LongIterable include, LongIterable exclude) {
    return new SortedLongListIntersectionIterator(include.iterator(), exclude.iterator());
  }

  protected boolean findNext() {
    long last = myNext;
    while (myFirst.hasNext()) {
      long v = myFirst.nextValue();
      assert v >= last : last + " " + v + " " + myFirst;
      if (accept(v)) {
        myNext = v;
        return true;
      }
      last = v;
    }
    return false;
  }

  private boolean accept(long v) {
    if (mySecondIterated) {
      if (v == myLastSecond)
        return true;
      if (v < myLastSecond)
        return false;
    }
    while (mySecond.hasNext()) {
      long n = mySecond.nextValue();
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

  protected long getNext() {
    return myNext;
  }
}
