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

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED;

public class LongMinusIteratorTests extends IntegersFixture {
  private LongArray create(long... values) {
    return new LongArray(values);
  }

  public void testMinusNotUnique() {
    testMinus(create(1, 1, 3, 3, 5, 5), create(1, 3, 5), create());
    testMinus(create(1, 1, 3, 3, 5, 5), create(1, 5), create(3, 3));
    testMinus(create(1, 1, 3, 3, 5, 5), create(1, 5, 5), create(3, 3));
    testMinus(create(1, 3, 5), create(1, 1, 5, 5), create(3));
    testMinus(create(1, 3, 5, 7, 9), create(1, 5, 9), create(3, 7));
  }

  private void testMinus(LongArray include, LongArray exclude, LongArray difference) {
    IntegersFixture.assertContents(new LongMinusIterator(include.iterator(), exclude.iterator()), difference);
  }

  public void testAllCases() {
    new SetOperationsChecker().check(myRand, new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return new LongMinusIterator(arrays[0], arrays[1]);
      }
    }, new SetOperationsChecker.MinusGetter(), true, SORTED);
  }

}
