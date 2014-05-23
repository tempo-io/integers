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

import com.almworks.integers.wrappers.LongIntHppcOpenHashMap;
import com.almworks.integers.wrappers.LongObjHppcOpenHashMap;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

import static com.almworks.integers.IntCollections.repeat;

public class LongIntMapUnsortedKeySetTests extends LongSetChecker<LongSet> {

  @Override
  protected List<LongSet> createSets(LongList sortedUniqueList) {
    LongObjHppcOpenHashMap<Integer> objMap = new LongObjHppcOpenHashMap<Integer>(sortedUniqueList.size());
    final LongIterator it = sortedUniqueList.iterator();
    objMap.putAll(new LongObjFindingIterator<Integer>() {
      @Override
      protected boolean findNext() throws ConcurrentModificationException {
        if (!it.hasNext()) return false;
        myNextLeft = it.nextValue();
        myNextRight = 0;
        return true;
      }
    });
    return Arrays.asList(createSet(sortedUniqueList), objMap.keySet());
  }

  @Override
  protected LongSet createSet(LongList sortedUniqueList) {
    LongIntHppcOpenHashMap map = new LongIntHppcOpenHashMap(sortedUniqueList.size());
    map.putAll(sortedUniqueList, repeat(0, sortedUniqueList.size()));
    return map.keySet();
  }

  @Override
  protected boolean isSortedSet() {
    return false;
  }
}
