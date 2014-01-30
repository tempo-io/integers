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

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for testing {@code LongList} implementation.
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public abstract class LongListChecker extends IntegersFixture {
  /**
   * @return list of different representations of LongList's with the specified values.
   */
  protected abstract List<? extends LongList> createLongListVariants(long... values);

  protected void checkValues(long... values) {
    LongList expected = LongArray.create(values);
    for (LongList arr : createLongListVariants(values)) {
      assertEquals(expected.isEmpty(), arr.isEmpty());
      assertEquals(expected.isSorted(), arr.isSorted());
      assertEquals(expected.isUniqueSorted(), arr.isUniqueSorted());
      CHECK.order(expected, arr);
    }
  }

  public void testValues() {
    checkValues();
    checkValues(0, 2, 4, 6, 8);
    checkValues(0, 10, 10, 20);
    checkValues(0, 10, 9);
    checkValues(Integer.MIN_VALUE, 10, 20, 40, Integer.MAX_VALUE);
    int attemptsCount = 10;
    int size = 100;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      checkValues(generateRandomLongArray(size, SortedStatus.UNORDERED).extractHostArray());
    }
  }

  protected void _testGetMethods(long ... values) {
    LongList expected = LongArray.create(values);
    for (LongList arr : createLongListVariants(values)) {
      for (int i = 0; i < arr.size(); i++) {
        assertEquals(expected.get(i), arr.get(i));
      }

      for (int i = 0; i < arr.size(); i++) {
        for (int j = -1; j < 2; j++) {
          long value = arr.get(i) + j;
          assertEquals(expected.indexOf(value), arr.indexOf(value));
          assertEquals(expected.contains(value), arr.contains(value));
        }
      }
    }
  }

  public void testGetMethodsSimpleCase() {
    _testGetMethods(0, 10, 30, 20, 5, 4, 1, 6);
    _testGetMethods(0, -10, 20, 30, 50);
    _testGetMethods(0, 9, 9, 5, 4, -1);
  }

  private void _testToMethods(long ... values) {
    LongList expected = LongArray.create(values);
    int length = values.length;
    long[] tmp = new long[length];
    for (LongList arr : createLongListVariants(values)) {
      CHECK.order(expected.toNativeArray(0, tmp, 0, length), arr.toNativeArray(0, tmp, 0, length));
      CHECK.order(expected.toNativeArray(), arr.toNativeArray());
      CHECK.order(expected.toList(), arr.toList());
    }
  }

  public void testRandom() {
    int arrLength = 200, tests = 5;
    for (int test = 0; test < tests; test++) {
      long[] arr = generateRandomLongArray(arrLength, IntegersFixture.SortedStatus.UNORDERED).extractHostArray();
      _testGetMethods(arr);
      checkValues(arr);
      _testToMethods(arr);
    }
  }

  public void testGetNextDifferentValueIndex() {
    for (LongList arr : createLongListVariants(0, 1, 1, 2, 2, 2, 3, 4, 5, 5, 5)) {
      assertEquals(1, arr.getNextDifferentValueIndex(0));
      assertEquals(3, arr.getNextDifferentValueIndex(1));
      assertEquals(6, arr.getNextDifferentValueIndex(3));
      assertEquals(7, arr.getNextDifferentValueIndex(6));
      assertEquals(arr.size(), arr.getNextDifferentValueIndex(8));
    }
  }

  public void testBinarySearch() {
    BinarySearchChecker.test(new BinarySearchChecker.BinarySearcher() {
      private LongList list;

      public void init(LongArray values) {
        list = createLongListVariants(values.toNativeArray()).get(0);
      }

      public int size() {
        return list.size();
      }

      public long get(int index) {
        return list.get(index);
      }

      public int binSearch(long value) {
        return list.binarySearch(value);
      }
    });
  }

  public void testIteratorSpecification() {
    LongListIteratorSpecificationChecker.checkListIterator(new LongListIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public List<LongListIterator> get(long... values) {
        List<? extends LongList> lists = createLongListVariants(values);
        List<LongListIterator> res = new ArrayList<LongListIterator>(lists.size());
        for (LongList list : lists) {
          res.add(list.iterator());
        }
        return res;
      }
    });
  }

  public void testGet() {
    for (int attempt = 0; attempt < 10; attempt++) {
      for (LongList list:
          createLongListVariants(generateRandomLongArray(1000, IntegersFixture.SortedStatus.UNORDERED).extractHostArray()))
      {
        if (!(list instanceof AbstractLongList)) return;
        IntArray indices = generateRandomIntArray(100, SortedStatus.UNORDERED, 0, 1000);
        AbstractLongList abstractList = (AbstractLongList)list;
        LongList actual = abstractList.get(indices);
        for (int j = 0; j < actual.size(); j++) {
          assertEquals(abstractList.get(indices.get(j)), actual.get(j));
        }
      }
    }
  }

  public void testGetLast() {
    for (int attempt = 0; attempt < 10; attempt++) {
      long[] array = generateRandomLongArray(1000, IntegersFixture.SortedStatus.UNORDERED).extractHostArray();
      for (LongList list:
          createLongListVariants(array)) {
        if (!(list instanceof AbstractLongList)) return;
        AbstractLongList abstractList = (AbstractLongList)list;
        for (int j = 0; j < array.length; j++) {
          assertEquals(abstractList.getLast(j), array[array.length - j - 1]);
        }
      }
    }
  }
}
