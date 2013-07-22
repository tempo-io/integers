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

import com.almworks.integers.IntCollections;
import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Iterates through a list of unique long lists in O(N * log(K)), where K - number of lists, N - average size
 * providing unique sorted values that exist in at least one of lists
 */
public class SortedLongListUnionIterator extends SortedLongListOperationsIterator {
  private boolean myIsHeapBuilt = false;

  public SortedLongListUnionIterator(List<LongIterator> iterators) {
    super(iterators);
  }

  @NotNull
  public static SortedLongListUnionIterator create(LongIterable... includes) {
    return create(Arrays.asList(includes));
  }

  @NotNull
  public static SortedLongListUnionIterator create(List<? extends LongIterable> includes) {
    List<LongIterator> result = new ArrayList<LongIterator>(includes.size());
    for (LongIterable arr : includes) {
      result.add(arr.iterator());
    }
    return new SortedLongListUnionIterator(result);
  }

  protected boolean findNext() {
    if (!myIsHeapBuilt) {
      myIsHeapBuilt = true;
      if (IntegersDebug.DEBUG) IntegersDebug.println(myIts.size());
      heapLength = 0;
      for (int i = 0; i < myIts.size(); i++) {
        if (myIts.get(i).hasNext()) {
          myIts.get(i).next();
          heapLength++;
          myHeap[heapLength] = i;
        }
      }

      buildHeap();
    }
    if (IntegersDebug.DEBUG) outputHeap();
    assert heapLength >= 0 : "heapLength < 0: " + heapLength;
    if (heapLength == 0) return false;
    myNext = myIts.get(myHeap[TOP]).value();
    while (myIts.get(myHeap[TOP]).value() == myNext && heapLength > 0) {
      LongIterator topIterator = myIts.get(myHeap[TOP]);
      if (topIterator.hasNext()) {
        long prev = topIterator.value();
        topIterator.next();
        assert prev < topIterator.value() : myHeap[TOP] + " " + prev + " " + topIterator.value();
      } else {
        IntCollections.swap(myHeap, TOP, heapLength);
        heapLength--;
      }
      heapify(TOP);
    }
    return true;
  }
}