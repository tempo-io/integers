/*
 * Copyright 2013 ALM Works Ltd
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

package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import com.almworks.integers.SetOperationsChecker;

import java.util.ArrayList;
import java.util.List;

public class LongIntersectionIteratorTests extends IntegersFixture {
  public void testCreate() {
    List<LongArray> list = new ArrayList<LongArray>();
    list.add(LongArray.create(1, 2, 5, 10, 20));
    list.add(LongArray.create(1, 5, 21, 30, 40));
    LongArray intersection = new LongArray(LongIntersectionIterator.create(list));
    CHECK.order(intersection, 1, 5);
  }

  public void testAllCases() {
    new SetOperationsChecker().check(new SetOperationsChecker.newSetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        LongArray res = new LongArray(new LongIntersectionIterator(arrays));
        return res.iterator();
      }
    }, new SetOperationsChecker.IntersectionGetter(false), true, false);
  }

}
