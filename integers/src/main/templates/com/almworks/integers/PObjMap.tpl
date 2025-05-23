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

import java.util.Collection;
import java.util.Iterator;

public interface #E#ObjMap<T> extends #E#ObjIterable<T> {
  #E#ObjMap EMPTY = new #E#ObjEmptyMap();

  /**
   * @return the value for the specified key if this map contains key {@code key}. Otherwise returns default value.
   */
  T get(#e# key);

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
   * @return true if this map contains any of the elements produced by {@code keys}.
   * Otherwise false. If {@code keys} produce no elements returns false.
   */
  boolean containsAnyKeys(#E#Iterable keys);

  /**
   * @return the number of keys/values in this map(its cardinality)
   */
  int size();

  /**
   * @return true if this map contains no elements
   */
  boolean isEmpty();

  #E#Iterator keysIterator();

  Iterator<T> valuesIterator();

  /**
   * @return a {@link #E#Set} view of the keys contained in this map.
   */
  #E#Set keySet();

  Collection<T> values();

  /**
   * Compares the specified object with this map for equality. Returns
   * <tt>true</tt> if and only if the specified object is also a
   * {@link #E#ObjMap} and both objects contain exactly the same key-value pairs,
   * regardless of their order
   */
  public boolean equals(Object o);

  /**
   * @return A hash code of elements stored in the map. The hash code
   * is defined as a sum of hash codes of keys and values stored
   * within the map. Because addition is commutative, this ensures that different order
   * of elements in a map does not affect the hash code.
   */
  public int hashCode();
}