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

public class LongUnionIteratorTwoTests extends IntegersFixture {
  public void testSimple() {
    LongArray a = LongArray.create(1, 2, 5, 10),
        b = LongArray.create(-3, 2, 3, 4, 11),
    expected = LongArray.copy(a);
    expected.addAll(b);
    expected.sortUnique();
    LongArray union = new LongArray(new LongUnionIteratorOfTwo(a, b));
    CHECK.order(union, expected);
  }

  public void testAllCases() {
    new SetOperationsChecker().check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return new LongUnionIteratorOfTwo(arrays[0], arrays[1]);
      }
    }, new SetOperationsChecker.UnionGetter(), true, SortedStatus.SORTED_UNIQUE);
  }
}
