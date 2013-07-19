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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Iterates through set of unique sorted lists in O(N+M), providing unique sorted values that exist in
 * every list
 */
public class SortedLongListIntersectionIterator extends SortedLongListOperationsIterator {

  public SortedLongListIntersectionIterator(List<LongIterator> iterators) {
    super(iterators);
  }

  @NotNull
  public static SortedLongListIntersectionIterator create(LongIterable ... includes) {
    return create(Arrays.asList(includes));
  }

  @NotNull
  public static SortedLongListIntersectionIterator create(List<? extends LongIterable> includes) {
    List<LongIterator> result = new ArrayList<LongIterator>(includes.size());
    for (LongIterable arr : includes) {
      result.add(arr.iterator());
    }
    return new SortedLongListIntersectionIterator(result);
  }

  private boolean equalValues() {
    long topValue = myIts.get(myHeap[TOP]).value();
    for (int i = parent(heapLength) + TOP; i <= heapLength; i++) {
      if (topValue != myIts.get(myHeap[i]).value()) {
        return false;
      }
    }
    return true;
  }

  protected boolean findNext() {
    for (int i = 0; i < myIts.size(); i++) {
      if (myIts.get(i).hasNext()) {
        myIts.get(i).next();
        myHeap[i + TOP] = i;
      } else {
        return false;
      }
    }
    buildHeap();
    if (IntegersDebug.DEBUG) outputHeap();

    while (!equalValues()) {
      LongIterator topIterator = myIts.get(myHeap[TOP]);
      if (!topIterator.hasNext()) return false;

      long prev = topIterator.value();
      topIterator.next();
      assert prev < topIterator.value() : myHeap[TOP] + " " + prev + " " + topIterator.value();
      heapify(TOP);
    }
    // all values are the same as the TOP
    myNext = myIts.get(myHeap[TOP]).value();
    return true;
  }
}
