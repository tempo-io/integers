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
  private final LongIterable[] iterables;
  int curItNum = 0;
  LongIterator curIt = LongIterator.EMPTY;
  boolean myEnded = false;

  public LongConcatIterator(LongIterable ... iterables) {
    this.iterables = iterables;
  }

  protected boolean findNext() {
    if (myEnded) return false;
    while (!curIt.hasNext() && curItNum < iterables.length) {
      curIt = iterables[curItNum++].iterator();
    }

    if (curIt.hasNext()) {
      myCurrent = curIt.nextValue();
      return true;
    }
    myEnded = true;
    return false;
  }
}