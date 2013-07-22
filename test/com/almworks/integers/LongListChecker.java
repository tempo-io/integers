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

import java.util.List;
import java.util.Random;

public abstract class LongListChecker extends IntegersFixture {
  Random r = new RandomHolder().getRandom();

  protected abstract List<LongList> createLongList(long ... values);

  public void _testStatusMethods(long ... values) {
    LongList expected = LongArray.create(values);
    for (LongList arr : createLongList(values)) {
      assertEquals(expected.size(), arr.size());
      assertEquals(expected.isEmpty(), arr.isEmpty());
      assertEquals(expected.isSorted(), arr.isSorted());
      assertEquals(expected.isUniqueSorted(), arr.isUniqueSorted());
    }
  }

  public void testStatusMethods() {
    _testStatusMethods();
    _testStatusMethods(0, 2, 4, 6, 8);
    _testStatusMethods(0, 10, 10, 20);
    _testStatusMethods(0, 10, 9);
    _testStatusMethods(Integer.MIN_VALUE, 10, 20, 40, Integer.MAX_VALUE);
  }

  public void _testGetMethods(long ... values) {
    LongList expected = LongArray.create(values);
    for (LongList arr : createLongList(values)) {
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

  public void _testIterator(long ... values) {
    LongList expected = LongArray.create(values);
    for (LongList arr : createLongList(values)) {
      CHECK.order(expected.iterator(), arr.iterator());
    }
  }

  public void _testToMethods(long ... values) {
    LongList expected = LongArray.create(values);
    int length = values.length;
    long[] tmp = new long[length];
    for (LongList arr : createLongList(values)) {
      CHECK.order(expected.toArray(0, tmp, 0, length), arr.toArray(0, tmp, 0, length));
      CHECK.order(expected.toNativeArray(), arr.toNativeArray());
      CHECK.order(expected.toList(), arr.toList());
    }
  }

  public void testRandom() {
    int arrLength = 200;
    long[] arr = new long[arrLength];
    for (int test = 0; test < 20; test++) {
      for (int i = 0; i < arrLength; i++) {
        arr[i] = r.nextInt();
      }
      _testGetMethods(arr);
      _testStatusMethods(arr);
      _testIterator(arr);
      _testToMethods(arr);
    }
  }
  public void testGetNextDifferentValueIndex() {
    for (LongList arr : createLongList(0, 1, 1, 2, 2, 2, 3, 4, 5, 5, 5)) {
      assertEquals(1, arr.getNextDifferentValueIndex(0));
      assertEquals(3, arr.getNextDifferentValueIndex(1));
      assertEquals(6, arr.getNextDifferentValueIndex(3));
      assertEquals(7, arr.getNextDifferentValueIndex(6));
      assertEquals(arr.size(), arr.getNextDifferentValueIndex(8));
    }
  }

  public void testBinarySearch() {
    IntegersFixture.testBinarySearch(new BinarySearcher() {
      private LongArray arr;
      private int length;

      public void init(long... values) {
        arr = LongArray.copy(values);
        length = values.length;
      }

      public int size() {
        return length;
      }

      public long get(int index) {
        return arr.get(index);
      }

      public int binSearch(long value) {
        return arr.binarySearch(value);
      }
    });
  }
}
