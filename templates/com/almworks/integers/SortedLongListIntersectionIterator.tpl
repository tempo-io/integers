/*
 * Copyright 20TOP0 ALM Works Ltd
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

import java.util.ArrayList;
import java.util.List;

/**
 * Iterates through two sorted int lists in O(N+M), providing values that exist in
 * both lists
 */
public class SortedLongListIntersectionIterator extends SortedLongListOperationsIterator {

  public SortedLongListIntersectionIterator(List<LongIterator> iterators) {
    super(iterators);
  }

  public static SortedLongListIntersectionIterator create(LongIterable... includes) {
    if (includes.length == 0)
      throw new NullPointerException("No elements");

    List<LongIterator> result = new ArrayList<LongIterator>(includes.length);
    for (int i = 0; i < includes.length; i++) {
      result.add(includes[i].iterator());
    }
    return new SortedLongListIntersectionIterator(result);
  }

  private boolean equalValues() { // leafs equals root
    for (int i = parent(heapLength) + TOP; i <= heapLength; i++) {
      if (myIts[myHeap[TOP]].value() != myIts[myHeap[i]].value()) {
        return false;
      }
    }
    return true;
  }

  protected boolean findNext() {                // very lazy
    for (int i = 0; i < myIts.length; i++) {
      if (myIts[i].hasNext()) {
        myIts[i].next();
        myHeap[i + TOP] = i;
      } else {
        return false;
      }
    }
    buildHeap();
    outputHeap();

    while ( !equalValues()) {
      int top = myHeap[TOP];
      if (myIts[top].hasNext()) {
        long prev = myIts[top].value();
        myIts[top].next();
        assert prev < myIts[top].value() : top + " " + prev + " " + myIts[top].value();
      } else {
        return false;
      }
      heapify(TOP);
    }
    myNext = myIts[myHeap[0]].value();
    return true;
  }

}
