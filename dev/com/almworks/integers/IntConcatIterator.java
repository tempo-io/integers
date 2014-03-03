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

// CODE GENERATED FROM com/almworks/integers/PConcatIterator.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public class IntConcatIterator extends IntFindingIterator implements IntIterator {
  private final Iterator<IntIterable> myIterables;
  private IntIterator myCurIterator = IntIterator.EMPTY;

  public IntConcatIterator(IntIterable ... iterables) {
    this(Arrays.asList(iterables));
  }

  public IntConcatIterator(@NotNull Iterable<IntIterable> iterables) {
    this.myIterables = iterables.iterator();
  }

  protected boolean findNext() {
    if (myCurIterator.hasNext()) {
      myNext = myCurIterator.nextValue();
      return true;
    }

    while (!myCurIterator.hasNext() && myIterables.hasNext()) {
      myCurIterator = myIterables.next().iterator();
    }

    if (myCurIterator.hasNext()) {
      myNext = myCurIterator.nextValue();
      return true;
    }

    return false;
  }
}