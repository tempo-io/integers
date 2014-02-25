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

// CODE GENERATED FROM com/almworks/integers/PQMap.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public interface LongLongMap extends LongLongIterable {

  long DEFAULT_VALUE = 0;

  /**
   * @return the value for the specified key if this map contains key {@code key}. Otherwise returns default value.
   */
  long get(long key);

  /**
   * @return true if this map contains key {@code key}. Otherwise false
   */
  boolean containsKey(long key);

  /**
   * @return true if this map contains all of the elements produced by {@code keys}.
   * Otherwise false
   */
  boolean containsKeys(LongIterable keys);

  /**
   * @return the number of keys/values in this map(its cardinality)
   */
  int size();

  /**
   * @return true if this map contains no elements
   */
  boolean isEmpty();

  @NotNull
  LongLongIterator iterator();

  LongIterator keysIterator();

  LongIterator valuesIterator();
}
