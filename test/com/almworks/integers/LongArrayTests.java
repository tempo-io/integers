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

import java.util.Arrays;
import java.util.Random;

public class LongArrayTests extends TestCase {
  private static final IntCollectionsCompare CHECK = new IntCollectionsCompare();
  private LongArray myArray = new LongArray();

  public void testAdd() {
    myArray.addAll(0,1,2);
    CHECK.order(myArray, LongArray.create(0, 1, 2));

    myArray.addAllNotMore(LongArray.create(3, 4, 10, 20), 2);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 3, 4));

    myArray.addAllNotMore(LongArray.create(5, 6, 10, 20), 2);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 3, 4, 5, 6));

    myArray.insert(3, 100);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 100, 3, 4, 5, 6));

    myArray.insertMultiple(1, -1, 3);
    CHECK.order(myArray, LongArray.create(0, -1, -1, -1, 1, 2, 100, 3, 4, 5, 6));
  }
  public void testCopy() {
    LongArray copiedArray = LongArray.copy(new long[]{10, 20, 30});
    CHECK.order(copiedArray, LongArray.create(10, 20, 30));
  }

  public void testEqual() {
    myArray = LongArray.create(0, 1, 2, 3, 4, 5, 6);
    assertTrue(myArray.equalOrder(new long[]{0,1,2,3,4,5,6}));
    assertFalse(myArray.equalOrder(new long[]{0,1,2,3,4,5,20}));

    assertTrue(myArray.equalSortedValues(LongArray.create(0, 1, 2, 3, 4, 5, 6)));
    assertFalse(myArray.equalSortedValues(LongArray.create(0, 1, 2, 3, 4, 5, 20)));
  }

  public void testExpand() {
    myArray = LongArray.create(0, 1, 2, 3);
    myArray.expand(1, 4);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 3, 0, 1, 2, 3));
  }

  public void testIndexOf() {
    myArray = new LongArray(LongProgression.arithmetic(99, 100, -1));
    for (int i = 0; i < 100; i++) {
      assertEquals(99 - i, myArray.indexOf(i));
    }
  }

  public void testRemove() {
    myArray = LongArray.create(0, -1, -1, -1, 1, 2, 3);
    myArray.removeRange(1, 4);
    CHECK.order(myArray, LongArray.create(0, 1, 2, 3));

    LongArray test = LongArray.create(0, 20, 21, 30, 35, 80);
    test.removeSorted(20);
    CHECK.order(test, LongArray.create(0, 21, 30, 35, 80));
  }

  public void testSet() {
    myArray = LongArray.create(0, 1, 2, 3, 4, 5);
    myArray.set(0, 10);
    myArray.setAll(3, LongArray.create(5, 31, 36, 100), 1, 2);
    CHECK.order(myArray, LongArray.create(10, 1, 2, 31, 36, 5));

    myArray.setRange(1, 3, -9);
    CHECK.order(myArray, LongArray.create(10, -9, -9, 31, 36, 5));
  }

  public void testSort() {
    Random r = new RandomHolder().getRandom();
    int arrayLength = 200;
    int maxValue = Integer.MAX_VALUE;
    LongArray res = new LongArray();

    for (int j = 0; j < arrayLength; j++) {
      res.add((long)r.nextInt(maxValue));
    }

    myArray = LongArray.copy(res);
    for (int i = 0; i < arrayLength; i++) {
      for (int j = 0; j < arrayLength - 1; j++) {
        if (myArray.get(j) > myArray.get(j+1)) {
          myArray.swap(j, j + 1);
        }
      }
    }
    res.sort();
    CHECK.order(res, myArray);
  }

  public void testOthersMethods() {
    CHECK.order(LongArray.singleton(Long.valueOf(-239)), LongArray.create(-239));

    long[] a = myArray.extractHostArray();
    CHECK.order(myArray, a);
  }

  public void testGetNextDifferentValueIndex() {
    myArray = LongArray.create(0, 1, 1, 2, 2, 2, 3, 4, 5, 5, 5);
    assertEquals(1, myArray.getNextDifferentValueIndex(0));
    assertEquals(3, myArray.getNextDifferentValueIndex(1));
    assertEquals(6, myArray.getNextDifferentValueIndex(3));
    assertEquals(7, myArray.getNextDifferentValueIndex(6));
    assertEquals(myArray.size(), myArray.getNextDifferentValueIndex(8));
  }

  public void testEnsureCapacity() {
    LongArray expected = new LongArray(LongProgression.arithmetic(0, 20, 1));
    myArray = LongArray.copy(expected);
    assertEquals(20, myArray.getCapacity());

    myArray.ensureCapacity(3);
    CHECK.order(myArray, expected);
    assertEquals(20, myArray.getCapacity());

    myArray.ensureCapacity(25);
    CHECK.order(myArray, expected);
    assertEquals(40, myArray.getCapacity());

    boolean caught = false;
    try {
      myArray.ensureCapacity(-1);
    } catch (IllegalArgumentException ex) {
      caught = true;
    }
    assertTrue("caught IAE", caught);
  }

  public void testRetain() {
    LongArray arr = new LongArray(LongProgression.arithmetic(0, 20, 1));
    arr.retainSorted(new LongArray(LongProgression.arithmetic(1, 15, 2)));
    CHECK.order(LongProgression.arithmetic(1, 10, 2), arr);
  }
}