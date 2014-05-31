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

// CODE GENERATED FROM com/almworks/integers/PSortedSet.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

public interface LongSortedSet extends LongSet {
  /**
   * @return a uniquely sorted array containing all the elements of this set
   * */
  LongArray toArray();

  /**
   * @return an iterator over this set in the sorted order
   * */
  @NotNull
  @Override
  LongIterator iterator();

  /**
   * @return an iterator over of this set in the sorted order whose elements are greater than or equal to fromElement.
   */
  LongIterator tailIterator(long fromElement);

  /**
   * @return the greatest element of this set if it exists or {@link Long#MIN_VALUE} in case the set is empty
   */
  public long getUpperBound();

  /**
   * @return the smallest element of this set if it exist or {@link Long#MAX_VALUE} in case the set is empty
   */
  public long getLowerBound();

    /**
     * Writes values from this set to {@code dest} in the ascending order.<br>
     * {@inheritDoc}
     */
  long[] toNativeArray(long[] dest, int destPos);

  /**
   * Writes values from this set to {@code dest} in the ascending order.
   * @see #toNativeArray(long[], int)
   */
  @Override
  long[] toNativeArray(long[] dest);
}
