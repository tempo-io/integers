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

// CODE GENERATED FROM com/almworks/integers/PUnionIteratorOfTwo.tpl


package com.almworks.integers;

public class IntUnionIteratorOfTwo extends IntFindingIterator {
  private final IntIterator myIts[] = new IntIterator[2];
  private int myItsCount = 2;
  private int[] v = {0, 0};

  public IntUnionIteratorOfTwo(IntIterable first, IntIterable second) {
    myIts[0] = first.iterator();
    myIts[1] = second.iterator();
    advanceIterator(0);
    advanceIterator(1);
    maybeMoveIterator1();
  }

  protected boolean findNext() {
    if (myItsCount == 0) return false;
    if (myItsCount == 2) {
      if (v[0] < v[1]) {
        myNext = v[0];
        advanceIterator(0);
      } else if (v[1] < v[0]) {
        myNext = v[1];
        advanceIterator(1);
      } else {
        myNext = v[0];
        advanceIterator(0);
        advanceIterator(1);
      }
      maybeMoveIterator1();
      return true;
    } else {
      myNext = myIts[0].value();
      advanceIterator(0);
      return true;
    }
  }

  private void maybeMoveIterator1() {
    if (myIts[0] == null) {
      myIts[0] = myIts[1];
      v[0] = v[1];
    }
  }

  private void advanceIterator(int ind) {
    if (myIts[ind].hasNext()) {
      v[ind] = myIts[ind].nextValue();
    } else {
      myIts[ind] = null;
      myItsCount--;
    }
  }
}