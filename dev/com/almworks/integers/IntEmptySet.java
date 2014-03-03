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

// CODE GENERATED FROM com/almworks/integers/PEmptySet.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public class IntEmptySet extends AbstractIntSet implements IntSortedSet {
  @Override
  protected void toNativeArrayImpl(int[] dest, int destPos) {
  }

  @Override
  public boolean contains(int value) {
    return false;
  }

  @Override
  public int size() {
    return 0;
  }

  @NotNull
  @Override
  public IntIterator iterator() {
    return IntIterator.EMPTY;
  }

  @Override
  public IntIterator tailIterator(int fromElement) {
    return IntIterator.EMPTY;
  }

  @Override
  public int getUpperBound() {
    return Integer.MIN_VALUE;
  }

  @Override
  public int getLowerBound() {
    return Integer.MAX_VALUE;
  }
}
