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
public class SortedLongListUnionIterator extends LongOperationsIterator {
  private boolean isHeapBuilt = false;

  public SortedLongListUnionIterator(List<LongIterator> iterators) {
    super(iterators);
  }

  public static SortedLongListUnionIterator create(LongIterable ... includes) {

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
    myNext = myIts[myHeap[TOP]].value();
    while (myIts[myHeap[TOP]].value() == myNext && heapLength > 0) {
      int top = myHeap[TOP];
      if (myIts[top].hasNext()) {
        long prev = myIts[top].value();
        myIts[top].next();
        assert prev < myIts[top].value() : myHeap[TOP] + " " + prev + " " + myIts[top].value();
      } else {
        swap(TOP, heapLength);
        heapLength--;
      }
      heapify(TOP);
    }
    return true;
  }

  protected long getNext() {
    //System.out.print(myNext + " ");
    return myNext;
  }
}