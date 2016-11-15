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

package com.almworks.integers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Iterates through a list of sorted unique #e# lists in O(N), where N - total length of iterables,
 * @author Eugene Vagin
 */
public class #E#IntersectionIterator extends #E#FindingIterator {
  /**
   * ArrayList is preferable implementation. Using LinkedList may be ineffective
   * */
  protected final List<#E#Iterator> myIts;

  public #E#IntersectionIterator(#E#Iterable... iterables) {
    this(Arrays.asList(iterables));
  }

  public #E#IntersectionIterator(List<? extends #E#Iterable> iterables) {
    myIts = #e#IterablesToIterators(iterables);
  }

  protected static List<#E#Iterator> #e#IterablesToIterators(List<? extends #E#Iterable> includes) {
    List<#E#Iterator> result = new ArrayList<#E#Iterator>(includes.size());
    for (#E#Iterable arr : includes) {
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

  private boolean accept(#E#Iterator it) {
    assert it.hasValue();
    while (it.value() < myNext) {
      #e# prev = it.value();
      if (!it.hasNext()) {
        return false;
      }
      it.next();
      assert prev < it.value() : prev + " " + it.value();
    }
    return it.value() == myNext;
  }
}