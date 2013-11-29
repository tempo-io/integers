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
import java.util.NoSuchElementException;

/**
 * class for test {@code LongList} implementation.
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public abstract class LongListChecker extends IntegersFixture {
  /**
   * @return list of different representations of LongList's with the specified values.
   */
  protected abstract List<? extends LongList> createLongListVariants(long... values);

  protected void _testCHECK(long... values) {
    LongList expected = LongArray.create(values);
    for (LongList arr : createLongListVariants(values)) {
      CHECK.order(expected, arr);
    }
  }

  public void testStatusMethods() {
    _testCHECK();
    _testCHECK(0, 2, 4, 6, 8);
    _testCHECK(0, 10, 10, 20);
    _testCHECK(0, 10, 9);
    _testCHECK(Integer.MIN_VALUE, 10, 20, 40, Integer.MAX_VALUE);
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

  private void _testIterator(long ... values) {
    LongList expected = LongArray.create(values);
    for (LongList arr : createLongListVariants(values)) {
      CHECK.order(expected.iterator(), arr.iterator());
    }
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
    int arrLength = 200;
    long[] arr = new long[arrLength];
    for (int test = 0; test < 20; test++) {
      for (int i = 0; i < arrLength; i++) {
        arr[i] = RAND.nextInt();
      }
      _testGetMethods(arr);
      _testCHECK(arr);
      _testIterator(arr);
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
    LongIteratorSpecificationChecker.check(new LongIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public List<LongIterator> get(long... values) {
        List<? extends LongList> lists = createLongListVariants(values);
        List<LongIterator> res = new ArrayList<LongIterator>(lists.size());
        for (LongList list : lists) {
          res.add(list.iterator());
        }
        return res;
      }
    });
  }

  public void testIteratorMove() {
    LongArray res = new LongArray(ap(1, 2, 10));
    for (LongList list: createLongListVariants(ap(1, 2, 10))) {
      LongListIterator it = list.iterator();
      assertFalse(it.hasValue());
      try {
        it.value();
        fail();
      } catch (NoSuchElementException ex) {}
      it.move(1);
      assertEquals(0, it.index());
      assertEquals(res.get(0), it.value());
      assertEquals(res.get(1), it.nextValue());
      assertEquals(1, it.index());
      assertEquals(res.get(0), it.get(-1));
      assertEquals(res.get(9), it.get(8));
      try {
        it.move(-2);
        fail();
      } catch (NoSuchElementException ex) {}

      it.move(3);
//      it.next();
      assertEquals(4, it.index());
      assertEquals(res.get(4), it.value());
      assertEquals(res.get(4), it.get(0));
      it.move(-1);
      assertEquals(3, it.index());
      assertEquals(res.get(4), it.get(1));
      assertEquals(res.get(4), it.nextValue());
    }
  }

  public void testGet() {
    for (int attempt = 0; attempt < 10; attempt++) {

      for (LongList list:
          createLongListVariants(generateRandomLongArray(1000, false).extractHostArray())) {
        IntArray indices = generateRandomIntArray(100, false, 0, 1000);
        if (!(list instanceof AbstractLongList)) return;
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
      long[] array = generateRandomLongArray(1000, false).extractHostArray();
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
