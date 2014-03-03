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

import org.jetbrains.annotations.NotNull;

public class #E#EmptySet extends Abstract#E#Set implements #E#SortedSet {
  @Override
  protected void toNativeArrayImpl(#e#[] dest, int destPos) {
  }

  @Override
  public boolean contains(#e# value) {
    return false;
  }

  @Override
  public int size() {
    return 0;
  }

  @NotNull
  @Override
  public #E#Iterator iterator() {
    return #E#Iterator.EMPTY;
  }

  @Override
  public #E#Iterator tailIterator(#e# fromElement) {
    return #E#Iterator.EMPTY;
  }

  @Override
  public #e# getUpperBound() {
    return #EW#.MIN_VALUE;
  }

  @Override
  public #e# getLowerBound() {
    return #EW#.MAX_VALUE;
  }
}
