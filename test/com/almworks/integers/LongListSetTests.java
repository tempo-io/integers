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

import java.util.ArrayList;
import java.util.List;

public class LongListSetTests extends LongSetChecker<LongListSet> {
  @Override
  protected List<LongListSet> createSets(LongList sortedUniqueList) {
    List<LongListSet> sets = new ArrayList<LongListSet>();
    sets.add(LongListSet.setFromSortedList(sortedUniqueList));
    sets.add(LongListSet.setFromSortedUniqueList(sortedUniqueList));
    for (int i = 1; i < 4; i++) {
      LongList sortedList = LongSameValuesList.create(sortedUniqueList, IntIterators.repeat(i));
      sets.add(LongListSet.setFromSortedList(sortedList));
    }
    return sets;
  }

  @Override
  protected LongListSet createSet(LongList sortedUniqueList) {
    return LongListSet.setFromSortedList(sortedUniqueList);
  }

  @Override
  protected boolean isSortedSet() {
    return true;
  }
}
