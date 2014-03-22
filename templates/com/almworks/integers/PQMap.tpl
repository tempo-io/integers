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

public interface #E##F#Map extends #E##F#Iterable {
  #E##F#Map EMPTY = new #E##F#EmptyMap();

  #f# DEFAULT_VALUE = 0;

  /**
   * @return the value for the specified key if this map contains key {@code key}. Otherwise returns default value.
   */
  #f# get(#e# key);

  /**
   * @return true if this map contains key {@code key}. Otherwise false
   */
  boolean containsKey(#e# key);

  /**
   * @return true if this map contains all of the elements produced by {@code keys}.
   * Otherwise false
   */
  boolean containsKeys(#E#Iterable keys);

  /**
   * @return the number of keys/values in this map(its cardinality)
   */
  int size();

  /**
   * @return true if this map contains no elements
   */
  boolean isEmpty();

  @NotNull
  #E##F#Iterator iterator();

  #E#Iterator keysIterator();

  #F#Iterator valuesIterator();

  /**
   * Compares the specified object with this map for equality. Returns
   * <tt>true</tt> if and only if the specified object is also a
   * {@link #E##F#Map} and both objects contain exactly the same key-value pairs,
   * regardless of their order
   */
  public boolean equals(Object o);

  /**
   * @return A hash code of elements stored in the map. The hash code
   * is defined as a sum of hash codes of keys and values stored
   * within the map). Because sum is commutative, this ensures that different order
   * of elements in a map does not affect the hash code.
   */
  public int hashCode();
}