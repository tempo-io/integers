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

package com.almworks.integers;

import com.almworks.util.RandomHolder;
import junit.framework.TestCase;

import java.util.Random;

public class LongArrayTests extends TestCase {
  private static final IntCollectionsCompare CHECK = new IntCollectionsCompare();
  private final LongArray myArray = new LongArray();

  public void testaddAll() {
    myArray.addAll(0,1,2);
    CHECK.order(myArray, LongArray.create(0, 1, 2));

    myArray.addAllNotMore(LongArray.create(3, 4, 10, 20), 2);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 3, 4));

    myArray.addAllNotMore(LongArray.create(5, 6, 10, 20), 2);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 3, 4, 5, 6));


    LongArray copiedArray = LongArray.copy(new long[]{10, 20, 30});
    CHECK.order(copiedArray, LongArray.create(10, 20, 30));

    assertTrue(myArray.equalOrder(new long[]{0,1,2,3,4,5,6}));
    assertFalse(myArray.equalOrder(new long[]{0,1,2,3,4,5,20}));

    assertTrue(myArray.equalSortedValues(LongArray.create(0, 1, 2, 3, 4, 5, 6)));
    assertFalse(myArray.equalSortedValues(LongArray.create(0, 1, 2, 3, 4, 5, 20)));

    myArray.expand(3, 4);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 3, 4, 5, 6, 3, 4, 5, 6));

    int index = myArray.indexOf(2);
    assertEquals(index, 2);

    myArray.insert(3, 100);
    assertEquals(myArray.get(3), 100);

    myArray.removeRange(4, 10);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 100, 5, 6));

    myArray.insertMultiple(3, 10, 4);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 10, 10, 10, 10, 100, 5, 6));

    LongArray test = LongArray.create(0, 20, 21, 30, 35, 80);
    CHECK.order(test, new LongArray(test.iterator()));

    test.removeSorted(20);
    CHECK.order(test, LongArray.create(0, 21, 30, 35, 80));

    test.set(0, 10);
    test.setAll(3, LongArray.create(5, 31, 36, 100), 1, 2);
    CHECK.order(test, LongArray.create(10, 21, 30, 31, 36));
    test.setRange(1, 3, -9);
    CHECK.order(test, LongArray.create(10, -9, -9, 31, 36));

    CHECK.order(LongArray.singleton(Long.valueOf(-239)), LongArray.create(-239));

//    System.out.println(myArray.size());
    long[] a = myArray.extractHostArray();
//    System.out.println("size:" + a.length);

//    long[] expected = {0, 1, 2, 3, 4, 5, 6, 3, 4, 5, 6};
//    for (int i = 0; i < expected.length; i++) {
//      assertEquals(expected[i], a[i]);
//    }

  }

  public void testSort() {
    Random r = new RandomHolder().getRandom();
    int arrayLength = 200;
    int maxValue = Integer.MAX_VALUE;
    LongArray res = new LongArray();

    for (int j = 0; j < arrayLength; j++) {
      res.add((long)r.nextInt(maxValue));
    }

    LongArray expected = LongArray.copy(res);
    for (int i = 0; i < arrayLength; i++) {
      for (int j = 0; j < arrayLength - 1; j++) {
        if (expected.get(j) > expected.get(j+1)) {
          expected.swap(j, j+1);
        }
      }
    }
    res.sort();
    CHECK.order(res, expected);
  }

  public void test() {
    WritableLongListIterator iter = LongArray.create(0, 2, 4).iterator();
  }
}
