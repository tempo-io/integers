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
 * Iterates through two unique sorted int lists in O(N+M), providing unique sorted values that exist in
 * either of lists
 */
public class SortedLongListUnionIterator extends FindingLongIterator {
  private final LongIterator my[];
  private long myNext = Long.MIN_VALUE;
  private boolean myIterated[];

  public SortedLongListUnionIterator(LongIterator iterators[]) {
    my = new LongIterator[iterators.length];
    myIterated = new boolean[iterators.length];
    for (int i = 0; i < iterators.length; i++) {
      my[i] = iterators[i];
    }
  }

  public static SortedLongListUnionIterator create(LongIterable includes[]) {
    LongIterator result[] = new LongIterator[includes.length];
    for (int i = 0; i < includes.length; i++) {
      result[i] = includes[i].iterator();
    }

    return new SortedLongListUnionIterator(result);
  }

  protected boolean findNext() {
    for (int i = 0; i < my.length; i++) {
      if (!myIterated[i] && my[i].hasNext()) {
        myIterated[i] = true;
        my[i].next();
      }
    }

    boolean someIterated = false;
    for (int i = 0; i < my.length && !someIterated; i++)
      if (myIterated[i])
        someIterated = true;

    if (!someIterated)
      return false;

    long min = Long.MAX_VALUE;
    for (int i = 0; i < my.length; i++) {
//      System.out.println("valormax " + i + " " + valueOrMax(i));
      min = Math.min(valueOrMax(i), min);
    }
    myNext = min;
    //System.out.println(" :" + myNext);

    for (int i = 0; i < my.length; i++) {
//      System.out.printf("%d %d %b %d\n", i , my[i].value(), my[i].hasNext(), myNext);
      if (myIterated[i] && (my[i].value() == myNext)) {
        if (my[i].hasNext()) {
          long prev = my[i].value();
          my[i].next();
          assert prev < my[i].value() : i + " " + prev + " " + my[i].value();
        } else {
          myIterated[i] = false;
        }
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
    return myNext;
  }
}