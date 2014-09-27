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

public class #E##F#EmptyMap implements #E##F#Map {
  @Override
  public #f# get(#e# key) {
    return 0;
  }

  @Override
  public boolean containsKey(#e# key) {
    return false;
  }

  @Override
  public boolean containsKeys(#E#Iterable keys) {
    return false;
  }

  @Override
  public boolean containsAnyKeys(#E#Iterable keys) {
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
  public #E##F#Iterator iterator() {
    return #E##F#Iterator.EMPTY;
  }

  @Override
  public #E#Iterator keysIterator() {
    return #E#Iterator.EMPTY;
  }

  @Override
  public #F#Iterator valuesIterator() {
    return #F#Iterator.EMPTY;
  }

  @Override
  public #E#Set keySet() {
    return #E#Set.EMPTY;
  }
}
