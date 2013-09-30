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

import java.util.Arrays;
import java.util.List;

/**
 * Iterates through a list of unique long lists in O(N * log(K)), where K - number of lists, N - average size,
 * providing unique sorted values that exist in at least one of lists
 *
 * @author Eugene Vagin
 */
public class LongUnionIterator extends LongSetOperationsIterator {
  private boolean myIsHeapBuilt = false;

  public LongUnionIterator(List<LongIterator> iterators) {
    super(iterators);
  }

  public LongUnionIterator(LongIterable... iterables) {
    super(longIterablesToIterators(Arrays.asList(iterables)));
  }

  @NotNull
  public static LongUnionIterator create(List<? extends LongIterable> iterables) {
    return new LongUnionIterator(longIterablesToIterators(iterables));
  }

  protected boolean findNext() {
    if (!myIsHeapBuilt) {
      myIsHeapBuilt = true;
      heapLength = 0;
      for (int i = 0, n = myIts.size(); i < n; i++) {
        if (myIts.get(i).hasNext()) {
          myIts.get(i).next();
          heapLength++;
          myHeap[heapLength] = i;
        }
      }
      buildHeap();
    }
    if (IntegersDebug.PRINT) outputHeap();
    if (heapLength == 0) return false;
    assert heapLength > 0 : "heapLength < 0: " + heapLength;
    myCurrent = getTopIterator().value();
    while (myIts.get(myHeap[TOP]).value() == myCurrent && heapLength > 0) {
      LongIterator topIterator = getTopIterator();
      if (topIterator.hasNext()) {
        topIterator.next();
        assert myCurrent < topIterator.value() : myHeap[TOP] + " " + myCurrent + " " + topIterator.value();
      } else {
        IntCollections.swap(myHeap, TOP, heapLength);
        heapLength--;
      }
      heapify(TOP);
    }
    return true;
  }
}