/*
 * Copyright 2013 ALM Works Ltd
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

public class LongConcatIterator extends FindingLongIterator implements LongIterator {
  private final LongIterable myIts[];
  private final int myLength;
  private LongIterator myCurIt = LongIterator.EMPTY;
  private int curIndex;
  private boolean myEnded = false;

  public LongConcatIterator(@NotNull LongIterable ... its) {
    if (its.length == 0) {
      myIts = null;
      myLength = 0;
      myEnded = true;
    } else {
      myIts = its;
      curIndex = 0;
      myLength = myIts.length;
    }
  }

  protected boolean findNext() {
//    if (myCurIt.hasNext()) {
//      myCurrent = myCurIt.nextValue();
//      return true;
//    }
    if (myEnded) return false;
    if (!myCurIt.hasNext()) {
      while (curIndex < myLength && !myCurIt.hasNext()) {
        myCurIt = myIts[curIndex++].iterator();
      }
    }
    if (myCurIt.hasNext()) {
      myCurrent = myCurIt.nextValue();
      return true;
    }
    myEnded = true;
    return false;
  }
}