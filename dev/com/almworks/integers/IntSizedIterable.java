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

/**
 * Iterables with size
 * @see com.almworks.integers.IntList
 * @see com.almworks.integers.LongSet
 */
public interface IntSizedIterable extends IntIterable {
  /**
   * Size of the collection. May not be efficient (up to O(N)).
   * @return the number of values in the collection
   */
  int size();
}
