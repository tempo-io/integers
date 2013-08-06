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

// CODE GENERATED FROM com/almworks/integers/PArrayIterator.tpl


package com.almworks.integers;

import static com.almworks.integers.IntegersUtils.*;

public class LongArrayIterator extends AbstractLongListIndexIterator {
  private final long[] myArray;

  public LongArrayIterator(long[] array, int from, int to) {
    super(Math.max(0, from), array == null ? 0 : Math.min(array.length, to));
    myArray = array == null ? EMPTY_LONGS : array;
  }

  public LongArrayIterator(long[] array) {
    this(array, 0, Integer.MAX_VALUE);
  }

  public static LongIterator create(long... values) {
    if (values == null || values.length == 0)
      return EMPTY;
    return new LongArrayIterator(values);
  }

  protected long absget(int index) {
    return myArray[index];
  }
}
