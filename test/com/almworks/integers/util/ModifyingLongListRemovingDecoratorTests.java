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

import com.almworks.integers.*;


public class ModifyingLongListRemovingDecoratorTests extends IntegersFixture {

  public void _testSimpleCreateFromSorted() {
    LongList base = LongArray.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    WritableIntList indexes = IntArray.create(0, 2, 4, 6, 8);
    ModifyingLongListRemovingDecorator arr = ModifyingLongListRemovingDecorator.createFromSorted(base, indexes);
    CHECK.order(arr.iterator(), 1, 3, 5, 7, 9);
  }

  public void _testRandomCreateFromSorted() {
    int arrLength = 100;
    int indexesLength = 50;
    int maxValue = 1000;

    long[] values = new long[arrLength];
    for (int i = 0; i < arrLength; i++) {
      values[i] = RAND.nextInt(maxValue);
    }
    LongList base = LongArray.create(values);

    int[] nativeIndexes = new int[arrLength];
    for (int test = 0; test < 20; test++) {
      for (int i = 0; i < indexesLength; i++) {
        nativeIndexes[i] = RAND.nextInt(arrLength);
      }
      IntArray indexes = IntArray.create(nativeIndexes);
      indexes.sortUnique();

      LongArray expected = LongArray.copy(base);
      for (int i = indexes.size() - 1; i >= 0; i--) {
        int val = indexes.get(i);
        expected.removeRange(val, val + 1);
      }
      ModifyingLongListRemovingDecorator arr = ModifyingLongListRemovingDecorator.createFromSorted(base, indexes);
      CHECK.order(arr.iterator(), expected.iterator());
    }
  }

  public void testCreateFromSorted() {
    _testSimpleCreateFromSorted();
    _testRandomCreateFromSorted();
  }

  public void testIterator() {
    LongArray source = LongArray.create(10,13,15,14,11,12,16,17,18),
        expected = LongArray.create(10, 15, 14, 12, 16, 18),
        result = new LongArray();
    ReadonlyLongListRemovingDecorator tst2 =
        ReadonlyLongListRemovingDecorator.createFromPrepared(source, IntArray.create(1, 3, 5));
    for (LongIterator i : tst2) {
      result.add(i.value());
    }
    assertEquals(expected, result);
  }
}
