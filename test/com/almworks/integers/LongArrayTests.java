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

import com.almworks.integers.util.LongAmortizedSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.almworks.integers.IntProgression.range;
import static com.almworks.integers.IntegersFixture.SortedStatus.*;

public class LongArrayTests extends WritableLongListChecker<LongArray> {
  private LongArray array = new LongArray();
  private SetOperationsChecker setOperations = new SetOperationsChecker();
  private SetOperationsChecker.SetCreator unionGetter = new SetOperationsChecker.UnionGetter();


  protected void tearDown() throws Exception {
    array.clear();
    array = null;
    super.tearDown();
  }

  @Override
  protected List<LongArray> createWritableLongListVariants(long... values) {
    List<LongArray> res = new ArrayList<LongArray>();
    LongArray arr = LongArray.copy(values);
    res.add(arr);

    arr = new LongArray(values.length * 2);
    arr.addAll(values);
    res.add(arr);

    arr = new LongArray(values.length * 2);
    arr.addAll(values);
    arr.addAll(generateRandomLongArray(values.length, UNORDERED));
    arr.removeRange(values.length, values.length * 2);
    res.add(arr);

    return res;
  }

  public void testAddAllNotMore() {
    array.addAllNotMore(LongArray.create(3, 4, 10, 20), 2);
    CHECK.order(LongArray.create(3, 4), array);

    array.addAllNotMore(LongArray.create(5, 6, 10, 20), 2);
    CHECK.order(LongArray.create(3, 4, 5, 6), array);

    assertEquals(2, array.addAllNotMore(LongArray.create(10, 20, 30, 50, 100).iterator(), 2));
    CHECK.order(LongArray.create(3, 4, 5, 6, 10, 20), array);

    assertEquals(1, array.addAllNotMore(LongArray.create(30).iterator(), 4));
    CHECK.order(LongArray.create(3, 4, 5, 6, 10, 20, 30), array);

    assertEquals(7, array.addAllNotMore(array, 20));
    CHECK.order(LongArray.create(3, 4, 5, 6, 10, 20, 30, 3, 4, 5, 6, 10, 20, 30), array);

    assertEquals(0, array.addAllNotMore(LongIterator.EMPTY, 2));
    CHECK.order(LongArray.create(3, 4, 5, 6, 10, 20, 30, 3, 4, 5, 6, 10, 20, 30), array);
  }

  public void testCopy() {
    LongArray copiedArray = LongArray.copy(new long[]{10, 20, 30});
    CHECK.order(copiedArray, LongArray.create(10, 20, 30));
  }

  public void testExpand() {
    array = new LongArray();
    array = LongArray.create(0, 1, 2, 3);
    array.expand(1, 4);
    CHECK.order(array, LongArray.create(0, 1, 2, 3, 0, 1, 2, 3));
  }

  public void testRemoveSorted() {
    array = LongArray.create(0, 20, 21, 30, 35, 80);
    assertTrue(array.removeSorted(20));
    CHECK.order(LongArray.create(0, 21, 30, 35, 80), array);

    array.clear();
    assertFalse(array.removeSorted(0));

    array.addAll(0, 1, 1, 1, 2, 10);
    assertTrue(array.removeSorted(1));
    CHECK.order(array, 0, 1, 1, 2, 10);
  }

  public void testEnsureCapacity() {
    LongArray expected = new LongArray(LongProgression.arithmetic(0, 20, 1));
    array = LongArray.copy(expected);
    assertEquals(20, array.getCapacity());

    array.ensureCapacity(3);
    CHECK.order(array, expected);
    assertEquals(20, array.getCapacity());

    array.ensureCapacity(25);
    CHECK.order(array, expected);
    assertEquals(40, array.getCapacity());

    try {
      array.ensureCapacity(-1);
      fail("not caught IAE");
    } catch (IllegalArgumentException ex) { }
  }

  public void checkRetain(LongList list, LongList values, boolean isSorted) {
    LongArray array = new LongArray(list);
    LongArray expected = new LongArray(array.size());
    for (int i = 0; i < array.size(); i++) {
      long value = array.get(i);
      if ((isSorted && values.binarySearch(value) >= 0) ||
          (!isSorted && values.contains(value))) {
        expected.add(value);
      }
    }
    if (isSorted) {
      array.retainSorted(values);
    } else {
      array.retain(values);
    }
    CHECK.order(expected, array);
  }

  public void testRetainSimple() {
    checkRetain(LongArray.create(Long.MIN_VALUE), LongArray.create(Long.MIN_VALUE + 1), false);
    checkRetain(LongArray.create(Long.MIN_VALUE), LongArray.create(Long.MIN_VALUE + 1), true);
    checkRetain(LongArray.create(2, 3, 5, 6, 8, 9, 10, 13, 3, 4, 5, 3),
        LongArray.create(1, 4, 5, 6, 7, 8, 10, 15), false);
    checkRetain(LongArray.create(0, 1, 2, 3, 5, 3, 2, 1, -10), LongArray.create(0, 2, -10), false);

    checkRetain(LongProgression.arithmetic(0, 20, 1), LongProgression.arithmetic(1, 15, 2), true);
    checkRetain(LongArray.create(0, 1, 2, 3, 10), LongArray.create(0, 2, 10), true);
  }

  public void testRetainComplex() {
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray res = LongArray.copy(arrays[0]);
        res.retain(arrays[1]);
        res.sortUnique();
        return res;
      }
    }, new SetOperationsChecker.IntersectionGetter(false), true, UNORDERED);

    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray res = LongArray.copy(arrays[0]);
        res.retainSorted(arrays[1]);
        res.sortUnique();
        return res;
      }
    }, new SetOperationsChecker.IntersectionGetter(true), true, UNORDERED, SORTED);
  }

  public void testRetainWithDuplicates() {
    int arraySize = 100, valuesSize = 10;
    for (int attempt = 0; attempt < 20; attempt++) {
      array = generateRandomLongArray(arraySize, SORTED, arraySize * 3 / 2);
      LongArray values = generateRandomLongArray(valuesSize, UNORDERED, arraySize * 3 / 2);
      values.addAll(values.get(range(0, values.size(), 2)));
      values.shuffle(RAND);
      checkRetain(array, values, false);

      values.sort();
      checkRetain(array, values, true);
    }
  }

  public void testFromCollection() {
    List<Long> l = new ArrayList<Long>();
    l.add(2L);
    l.add(3L);
    l.add(9L);
    LongArray a = LongArray.create(l);
    CHECK.order(a.toNativeArray(), 2, 3, 9);
    LongList il = LongCollections.asLongList(l);
    CHECK.order(il.toNativeArray(), 2, 3, 9);
  }

  public void testMergeWithSameLength() {
    // is likely to be launched branch with realloc
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray copy = LongArray.copy(arrays[0]);
        copy.mergeWithSameLength(arrays[1]);
        return copy;
      }
    }, unionGetter, true, SORTED_UNIQUE);

    // guaranteed launch branch with replace
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray copy = LongArray.copy(arrays[0]);
        copy.ensureCapacity(arrays[0].size() + arrays[1].size());
        copy.mergeWithSameLength(arrays[1]);
        return copy;
      }
    }, unionGetter, true, SORTED_UNIQUE);
  }

  public void testMergeWithSmall() {
    // is likely to be launched branch with realloc
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray copy = LongArray.copy(arrays[0]);
        copy.mergeWithSmall(arrays[1]);
        return copy;
      }
    }, unionGetter, true, SORTED_UNIQUE, SORTED);

    // guaranteed launch branch with replace
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray copy = LongArray.copy(arrays[0]);
        copy.ensureCapacity(arrays[0].size() + arrays[1].size());
        copy.mergeWithSmall(arrays[1]);
        return copy;
      }
    }, unionGetter, true, SORTED_UNIQUE, SORTED);
  }

  public void testMerge() {
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray copy = LongArray.copy(arrays[0]);
        copy.merge(arrays[1]);
        return copy;
      }
    }, unionGetter, true, SORTED_UNIQUE);
  }

  public void testRemoveAllAtSorted() {
    int arSize = 100;
    int indexesSize = 10;
    int attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      array = generateRandomLongArray(arSize, UNORDERED);
      IntArray indexes = generateRandomIntArray(indexesSize, SORTED_UNIQUE, array.size());
      if (attempt % 2 == 0) {
        indexes.add(indexes.get(indexes.size() / 2) + 1);
        indexes.sortUnique();
      }
      LongArray expected = LongArray.copy(array);
      for (int i = indexes.size() - 1; i >= 0; i--) {
        expected.removeAt(indexes.get(i));
      }
      array.removeAllAtSorted(indexes.iterator());
      CHECK.order(array, expected);
    }
  }

  public void testCreate() {
    int size = 1000, maxVal = Integer.MAX_VALUE;
    long[] values = new long[size];
    for (int attempt = 0; attempt < 10; attempt++) {
      for (int i = 0; i < size; i++) {
        values[i] = RAND.nextInt(maxVal);
      }
      LongArray actual = LongArray.create(values);
      assertEquals(values.length, actual.size());
      for (int i = 0; i < size; i++) {
        assertEquals(values[i], actual.get(i));
      }
    }
  }

  public void test() {
    LongArray ar = LongArray.create(3, 6, 7, 8, 10, 11, 15);
    LongArray src = LongArray.create(0, 0, -1, 6, -1);
    int[][] points = {null};
    System.out.println(ar.getInsertionPoints(src, points));
    System.out.println(Arrays.toString(points[0]));
  }
}