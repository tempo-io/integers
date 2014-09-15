/*
 * Copyright 2014 ALM Works Ltd
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

// CODE GENERATED FROM com/almworks/integers/PIntersectionIterator.tpl



package com.almworks.integers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Iterates through a list of sorted unique long lists in O(N), where N - total length of iterables,
 * @author Eugene Vagin
 */
public class LongIntersectionIterator extends LongFindingIterator {
  /**
   * ArrayList is preferable implementation. Using LinkedList may be ineffective
   * */
  protected final List<LongIterator> myIts;

  public LongIntersectionIterator(LongIterable... iterables) {
    this(Arrays.asList(iterables));
  }

  public LongIntersectionIterator(List<? extends LongIterable> iterables) {
    myIts = longIterablesToIterators(iterables);
  }

  protected static List<LongIterator> longIterablesToIterators(List<? extends LongIterable> includes) {
    List<LongIterator> result = new ArrayList<LongIterator>(includes.size());
    for (LongIterable arr : includes) {
      result.add(arr.iterator());
    }
    return result;
  }

  @Override
  protected boolean findNext() throws ConcurrentModificationException {
    if (myIts.size() == 0) return false;
    for (int i = 0; i < myIts.size(); i++) {
      if (!myIts.get(i).hasNext()) {
        return false;
      }
      myIts.get(i).next();
    }
    myNext = myIts.get(0).value();

    boolean ok;
    do {
      ok = true;
      for (int i = 0; i < myIts.size(); i++) {
        if (!accept(myIts.get(i))) {
          if (myIts.get(i).value() < myNext) {
            return false;
          } else {
            ok = false;
            myNext = myIts.get(i).value();
            break;
          }
        }
      }
    } while (!ok);
    return true;
  }

  private boolean accept(LongIterator it) {
    assert it.hasValue();
    while (it.value() < myNext) {
      long prev = it.value();
      if (!it.hasNext()) {
        return false;
      }
      it.next();
      assert prev < it.value() : prev + " " + it.value();
    }
    return it.value() == myNext;
  }
}