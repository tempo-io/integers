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

import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Iterates through two unique sorted int lists in O(N+M), providing unique sorted values that exist in
 * either of lists
 */
public class SortedLongListUnionIterator extends SortedLongListOperationsIterator {
  private boolean isHeapBuilt = false;

  public SortedLongListUnionIterator(List<LongIterator> iterators) {
    super(iterators);
  }

  public static SortedLongListUnionIterator create(LongIterable ... includes) {
    if (includes.length == 0)
      throw new NullPointerException("No elements");

    List<LongIterator> result = new ArrayList<LongIterator>(includes.length);
    for (int i = 0; i < includes.length; i++) {
      result.add(includes[i].iterator());
    }
    return new SortedLongListUnionIterator(result);
  }

  protected boolean findNext() {
    if (!isHeapBuilt) {
      isHeapBuilt = true;
      //System.out.println(myIts.length);
      heapLength = 0;
      for (int i = 0; i < myIts.length; i++) {
        if (myIts[i].hasNext()) {
          myIts[i].next();
          heapLength++;
          myHeap[heapLength] = i;
        }
      }
      buildHeap();
      outputHeap();
    }
    outputHeap();
    assert heapLength >= 0 : "heapLength<0: " + heapLength;
    if (heapLength == 0)
      return false;
    myNext = myIts[myHeap[1]].value();
    while (myIts[myHeap[1]].value() == myNext && heapLength > 0) {
      if (myIts[myHeap[1]].hasNext()) {
        long prev = myIts[myHeap[1]].value();
        myIts[myHeap[1]].next();
        assert prev < myIts[myHeap[1]].value() : myHeap[1] + " " + prev + " " + myIts[myHeap[1]].value();
      } else {
        swap(1, heapLength);
        heapLength--;
      }
      heapify(1);
    }
    return true;
  }

  protected long getNext() {
    //System.out.print(myNext + " ");
    return myNext;
  }
}