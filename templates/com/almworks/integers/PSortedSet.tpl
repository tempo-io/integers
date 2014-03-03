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

public interface #E#SortedSet extends #E#Set {
  /**
   * @return a uniquely sorted array containing all the elements of this set
   * */
  #E#Array toArray();

  /**
   * @return an iterator over this set in the sorted order
   * */
  @NotNull
  #E#Iterator iterator();

  /**
   * @return an iterator over of this set in the sorted order whose elements are greater than or equal to fromElement.
   */
  #E#Iterator tailIterator(#e# fromElement);

  /**
   * @return the greatest element of this set if it exists or {@link #EW##MIN_VALUE} in case the set is empty
   */
  public #e# getUpperBound();

  /**
   * @return the smallest element of this set if it exist or {@link #EW##MAX_VALUE} in case the set is empty
   */
  public #e# getLowerBound();

    /**
     * Writes values from this set to {@code dest} in the ascending order.<br>
     * {@inheritDoc}
     */
  #e#[] toNativeArray(#e#[] dest, int destPos);

  /**
   * Writes values from this set to {@code dest} in the ascending order.
   * @see {@link #toNativeArray(#e#[], int)}
   */
  @Override
  #e#[] toNativeArray(#e#[] dest);
}
