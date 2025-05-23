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

import static com.almworks.integers.IntegersUtils.EMPTY_#EC#S;

public class #E#NativeArrayIterator extends Abstract#E#ListIndexIterator {
  private final #e#[] myArray;

  public #E#NativeArrayIterator(#e#[] array, int from, int to) {
    super(Math.max(0, from), array == null ? 0 : Math.min(array.length, to));
    myArray = array == null ? EMPTY_#EC#S : array;
  }

  public #E#NativeArrayIterator(#e#[] array) {
    this(array, 0, Integer.MAX_VALUE);
  }

  public static #E#Iterator create(#e#... values) {
    if (values == null || values.length == 0)
      return EMPTY;
    return new #E#NativeArrayIterator(values);
  }

  protected #e# absget(int index) {
    return myArray[index];
  }
}
