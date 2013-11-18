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

import com.almworks.integers.func.LongFunction;
import com.almworks.integers.func.LongFunctions;

import java.util.ArrayList;
import java.util.List;

public class LongArrayTests extends WritableLongListChecker {
  private LongArray array = new LongArray();
  private SetOperationsChecker setOperations = new SetOperationsChecker();

  protected void tearDown() throws Exception {
    array.clear();
    array = null;
    super.tearDown();
  }

  @Override
  protected List<WritableLongList> createWritableLongListVariants(long... values) {
    List<WritableLongList> res = new ArrayList<WritableLongList>();
    LongArray arr = LongArray.copy(values);
    res.add(arr);

    arr = new LongArray(values.length * 2);
    arr.addAll(values);
    res.add(arr);

    arr = new LongArray(values.length * 2);
    arr.addAll(values);
    arr.addAll(generateRandomLongArray(values.length, false));
    arr.removeRange(values.length, values.length * 2);
    res.add(arr);

    return res;
  }

  public void testAddAllNotMore() {
    array.addAllNotMore(LongArray.create(3, 4, 10, 20), 2);
    CHECK.order(array, LongArray.create(3, 4));

    array.addAllNotMore(LongArray.create(5, 6, 10, 20), 2);
    CHECK.order(array, LongArray.create(3, 4, 5, 6));

    assertEquals(2, array.addAllNotMore(LongArray.create(10, 20, 30, 50, 100).iterator(), 2));
    CHECK.order(array, LongArray.create(3, 4, 5, 6, 10, 20));

    assertEquals(1, array.addAllNotMore(LongArray.create(30).iterator(), 4));
    CHECK.order(array, LongArray.create(3, 4, 5, 6, 10, 20, 30));
  }

  public void testCopy() {
    LongArray copiedArray = LongArray.copy(new long[]{10, 20, 30});
    CHECK.order(copiedArray, LongArray.create(10, 20, 30));
  }

  public void testEqual() {
    array = LongArray.create(0, 1, 2, 3, 4, 5, 6);
    assertTrue(array.equalOrder(new long[]{0, 1, 2, 3, 4, 5, 6}));
    assertFalse(array.equalOrder(new long[]{0, 1, 2, 3, 4, 5, 20}));

    assertTrue(array.equalSortedValues(LongArray.create(0, 1, 2, 3, 4, 5, 6)));
    assertFalse(array.equalSortedValues(LongArray.create(0, 1, 2, 3, 4, 5, 20)));
  }

  public void testExpand() {
    // todo add more cases
    array = new LongArray();
    array = LongArray.create(0, 1, 2, 3);
    array.expand(1, 4);
    CHECK.order(array, LongArray.create(0, 1, 2, 3, 0, 1, 2, 3));
  }

  public void testRemoveSorted() {
    array = LongArray.create(0, 20, 21, 30, 35, 80);
    array.removeSorted(20);
    CHECK.order(array, LongArray.create(0, 21, 30, 35, 80));
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

  public void testRetain() {
    LongArray arr = LongArray.create(2, 3, 5, 6, 8, 9, 10, 13, 3, 4, 5, 3);
    LongList values = LongArray.create(1, 4, 5, 6, 7, 8, 10, 15);
    LongArray expected = LongArray.create(5, 6, 8, 10, 4, 5);
    arr.retain(values);
    CHECK.order(expected, arr);

    arr = new LongArray(LongProgression.arithmetic(0, 20, 1));
    arr.retainSorted(new LongArray(LongProgression.arithmetic(1, 15, 2)));
    CHECK.order(LongProgression.arithmetic(1, 10, 2), arr);

    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray res = LongArray.copy(arrays[0]);
        res.retain(arrays[1]);
        res.sortUnique();
        return res;
      }
    }, new SetOperationsChecker.IntersectionGetter(false), false, true);

    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray res = LongArray.copy(arrays[0]);
        res.retain(arrays[1]);
        return res;
      }
    }, new SetOperationsChecker.IntersectionGetter(true), true, true);
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

  public void testUnion() {
    SetOperationsChecker.SetCreator unionGetter = new SetOperationsChecker.UnionGetter();
    // is likely to be launched branch with realloc
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        return arrays[0].mergeWithSameLength(arrays[1]);
      }
    }, unionGetter, true, true);

    // guaranteed launch branch with replace
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray copy = LongArray.copy(arrays[0]);
        copy.ensureCapacity(arrays[0].size() + arrays[1].size());
        return copy.mergeWithSameLength(arrays[1]);
      }
    }, unionGetter, true, true);

    // is likely to be launched branch with realloc
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        return arrays[0].mergeWithSmall(arrays[1]);
      }
    }, unionGetter, true, true);

    // guaranteed launch branch with replace
    setOperations.check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterable get(LongArray... arrays) {
        LongArray copy = LongArray.copy(arrays[0]);
        copy.ensureCapacity(arrays[0].size() + arrays[1].size());
        return copy.mergeWithSmall(arrays[1]);
      }
    }, unionGetter, true, true);
  }

  public void testRemoveSortedIndexesFromSorted() {
    int arSize = 100;
    int indexesSize = 10;
    int attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      array = generateRandomLongArray(arSize, false);
      IntArray indexes = generateRandomIntArray(indexesSize, true, array.size());
      if (attempt % 2 == 0) {
        indexes.add(indexes.get(indexes.size() / 2) + 1);
        indexes.sortUnique();
      }
      LongArray expected = LongArray.copy(array);
      for (int i = indexes.size() - 1; i >= 0; i--) {
        expected.removeAt(indexes.get(i));
      }
      array.removeSortedIndexes(indexes);
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
}