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

import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;

/**
 * Iterates through two sorted int lists in O(N+M), providing values that exist in
 * both lists
 */
public class SortedLongListUnionIterator extends FindingLongIterator {
  private final LongIterator my[] = new LongIterator[2];
  private long myNext = Long.MIN_VALUE;
  private boolean myIterated[] = {false, false};
//  private boolean

  public SortedLongListUnionIterator(LongIterator first, LongIterator second) {
    my[0] = first;
    my[1] = second;


    for(int index = 0; index < 2; index++) {
      my[index].next();
      myIterated[index] = true;
    }
  }

  public static SortedLongListUnionIterator create(LongIterable include, LongIterable exclude) {
    return new SortedLongListUnionIterator(include.iterator(), exclude.iterator());
  }

  protected boolean findNext() {
    if (!myIterated[0] && !myIterated[1])
      return false;

    //System.out.print(valueOrMax(0) + " " + valueOrMax(1));
    myNext = Math.min(valueOrMax(0), valueOrMax(1));
    //System.out.println(" " + myNext);

    for (int i = 0; i < 2; i++) {
      if (myIterated[i] && my[i].value() == myNext) {
        if (my[i].hasNext())
          my[i].next();
        else
          myIterated[i] = false;
      }
    }
    return true;
  }

  private long valueOrMax(int index) {
    long result;
    //System.out.println(index + " " + my[index].hasNext());
    if (myIterated[index])
      result = my[index].value();
    else
      result = Long.MAX_VALUE;
    return result;
  }

  protected long getNext() {
    //System.out.println(myNext);
    //return 1;
    return myNext;
  }
}