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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Iterates through a list of unique long lists in O(N * log(K)), where K - number of lists, N - average size,
 * providing unique sorted values that exist in every list
 *
 * @author Eugene Vagin
 */
public class LongIntersectionIterator extends LongSetOperationsIterator {

  public LongIntersectionIterator(LongIterable... iterables) {
    super(longIterablesToIterators(Arrays.asList(iterables)));
  }

  public LongIntersectionIterator(List<? extends LongIterable> iterables) {
    super(longIterablesToIterators(iterables));
  }

  private boolean equalValues() {
    long topValue = getTopIterator().value();
    for (int i = parent(heapLength) + TOP; i <= heapLength; i++) {
      if (topValue != myIts.get(myHeap[i]).value()) {
        return false;
      }
    }
    return true;
  }

  protected boolean findNext() {
    if (myIts.size() == 0) {
      return false;
    }
    for (int i = 0, n = myIts.size(); i < n; i++) {
      if (myIts.get(i).hasNext()) {
        myIts.get(i).next();
        myHeap[i + TOP] = i;
      } else {
        return false;
      }
    }
    buildHeap();
    if (IntegersDebug.PRINT) outputHeap();

    while (!equalValues()) {
      LongIterator topIterator = getTopIterator();
      if (!topIterator.hasNext()) return false;

      long prev = topIterator.value();
      topIterator.next();
      assert prev < topIterator.value() : myHeap[TOP] + " " + prev + " " + topIterator.value();
      heapify(TOP);
    }
    // all values are the same as the TOP
    myCurrent = getTopIterator().value();
    return true;
  }
}