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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LongConcatIterator extends FindingLongIterator implements LongIterator {
  private LongIterator myCurIt = LongIterator.EMPTY;
  private boolean myEnded = false;
  private Iterator<LongIterable> itIt;

  public LongConcatIterator(@NotNull List<LongIterable> iterables) {
    if (iterables.size() == 0) {
      myEnded = true;
    } else {
      itIt = iterables.iterator();
    }
  }

  public LongConcatIterator(@NotNull LongIterable ... iterables) {
    this(Arrays.asList(iterables));
  }

  private boolean checkCurIterator() {
    if (myCurIt.hasNext()) {
      myCurrent = myCurIt.nextValue();
      return true;
    }
    return false;
  }

  protected boolean findNext() {
    if (checkCurIterator()) return true;
    if (myEnded) return false;
    if (!myCurIt.hasNext()) {
      while (itIt.hasNext() && !myCurIt.hasNext()) {
        myCurIt = itIt.next().iterator();
      }
    }
    if (checkCurIterator()) {
      return true;
    } else {
      myEnded = true;
      return false;
    }
  }
}