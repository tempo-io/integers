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

// CODE GENERATED FROM com/almworks/integers/PQEmptyMap.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public class IntIntEmptyMap implements IntIntMap {
  @Override
  public int get(int key) {
    return 0;
  }

  @Override
  public boolean containsKey(int key) {
    return false;
  }

  @Override
  public boolean containsKeys(IntIterable keys) {
    return false;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @NotNull
  @Override
  public IntIntIterator iterator() {
    return IntIntIterator.EMPTY;
  }

  @Override
  public IntIterator keysIterator() {
    return IntIterator.EMPTY;
  }

  @Override
  public IntIterator valuesIterator() {
    return IntIterator.EMPTY;
  }

  @Override
  public IntSet keySet() {
    return IntSet.EMPTY;
  }
}
