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
public class SortedLongListIntersectionIterator extends SortedLongListOperationsIterator {

  public SortedLongListIntersectionIterator(LongIterator iterators[]) {
    super(iterators);
  }

  public static SortedLongListIntersectionIterator create(LongIterable... includes) {
    LongIterator result[] = new LongIterator[includes.length];
    for (int i = 0; i < includes.length; i++) {
      result[i] = includes[i].iterator();
    }
    return new SortedLongListIntersectionIterator(result);
  }

  private boolean equalValues() { // leafs equals root
    for (int i = parent(heapLength) + 1; i <= heapLength; i++) {
      if (myIts[heap[1]].value() != myIts[heap[i]].value()) {
        return false;
      }
    }
    return true;
  }

  protected boolean findNext() {                // very lazy
    for (int i = 0; i < myIts.length; i++) {
      if (myIts[i].hasNext()) {
        myIts[i].next();
        heap[i + 1] = i;
      } else {
        return false;
      }
    }
    buildHeap();
    outputHeap();

    while ( !equalValues()) {
      if (myIts[heap[1]].hasNext()) {
        long prev = myIts[heap[1]].value();
        myIts[heap[1]].next();
        assert prev < myIts[heap[1]].value() : heap[1] + " " + prev + " " + myIts[heap[1]].value();
      } else {
        return false;
      }
      heapify(1);
    }
    myNext = myIts[heap[0]].value();
    return true;
  }

}
