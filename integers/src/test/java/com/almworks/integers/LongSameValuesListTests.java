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

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.LongCollections.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public class LongSameValuesListTests extends WritableLongListChecker<LongSameValuesList> {

  @Override
  protected List<LongSameValuesList> createWritableLongLists(long... values) {
    List<LongSameValuesList> res = new ArrayList<LongSameValuesList>();
    LongSameValuesList array = new LongSameValuesList();
    array.addAll(values);
    res.add(array);
    array = new LongSameValuesList();
    array.addAll(LongCollections.repeat(0, 10));
    array.addAll(values);
    array.removeRange(0, 10);
    res.add(array);

    array = new LongSameValuesList();
    array.addAll(values);
    array.addAll(LongCollections.repeat(0, 10));
    array.removeRange(values.length, values.length + 10);
    res.add(array);

    res.add(LongSameValuesList.create(new LongArray(values)));
    return res;
  }

  private LongSameValuesList list;

  public void setUp() throws Exception {
    super.setUp();
    list = new LongSameValuesList();
  }

  protected void tearDown() throws Exception {
    list = null;
    super.tearDown();
  }

  public void testRemove2() {
    list.addAll(ap(0, 10, 0));
    list.addAll(ap(1, 10, 0));
    list.addAll(ap(2, 10, 0));
    list.addAll(ap(3, 10, 0));
    list.addAll(ap(2, 10, 0));
    list.addAll(ap(1, 10, 0));
    list.addAll(ap(0, 10, 0));
    assertEquals(6, list.getChangeCount());
    checkCollectionM(list, ap(0, 10, 0), ap(1, 10, 0), ap(2, 10, 0), ap(3, 10, 0), ap(2, 10, 0), ap(1, 10, 0), ap(0, 10, 0));
    list.removeRange(0, 5);
    checkCollectionM(list, ap(0, 5, 0), ap(1, 10, 0), ap(2, 10, 0), ap(3, 10, 0), ap(2, 10, 0), ap(1, 10, 0), ap(0, 10, 0));
    list.removeRange(60, 65);
    checkCollectionM(list, ap(0, 5, 0), ap(1, 10, 0), ap(2, 10, 0), ap(3, 10, 0), ap(2, 10, 0), ap(1, 10, 0), ap(0, 5, 0));
    list.removeAt(10);
    checkCollectionM(list, ap(0, 5, 0), ap(1, 9, 0), ap(2, 10, 0), ap(3, 10, 0), ap(2, 10, 0), ap(1, 10, 0), ap(0, 5, 0));
    list.removeRange(29, 40);
    checkCollectionM(list, ap(0, 5, 0), ap(1, 9, 0), ap(2, 10, 0), ap(3, 5, 0), ap(2, 4, 0), ap(1, 10, 0), ap(0, 5, 0));
    list.removeRange(25, 43);
    checkCollectionM(list, ap(0, 5, 0), ap(1, 9, 0), ap(2, 10, 0), ap(3, 1, 0), ap(0, 5, 0));
    list.removeRange(5, 23);
    checkCollectionM(list, ap(0, 5, 0), ap(2, 1, 0), ap(3, 1, 0), ap(0, 5, 0));
    list.removeRange(4, 8);
    checkCollectionM(list, ap(0, 8, 0));
    list.removeRange(0, 8);
    checkCollection(list);
  }

  public void testRemoveCollapse() {
    list.addAll(ap(1, 10, 0));
    list.addAll(ap(2, 10, 0));
    list.addAll(ap(1, 10, 0));
    list.removeRange(5, 25);
    checkCollection(list, ap(1, 10, 0));
    assertEquals(0, list.getChangeCount());
  }

  public void testSet() {
    list.addAll(ap(0, 10, 0));
    checkCollection(list, ap(0, 10, 0));
    for (int i = 0; i < 10; i++) {
      list.set(i, 100 + i);
      checkCollectionM(list, ap(100, i + 1, 1), ap(0, 9 -i, 0));
    }
    list.clear();
    list.addAll(ap(0, 10, 0));
    for (int i = 0; i < 10; i++) {
      list.set(i, 100);
      checkCollectionM(list, ap(100, i + 1, 0), ap(0, 9 -i, 0));
      assertEquals(i == 9 ? 0 : 1, list.getChangeCount());
    }
    list.clear();
    list.addAll(ap(1, 10, 0));
    list.addAll(ap(2, 10, 0));
    list.addAll(ap(1, 10, 0));
    list.setRange(5, 25, 1);
    checkCollection(list, ap(1, 30, 0));
    assertEquals(0, list.getChangeCount());

    list.clear();
    list.add(0);
    checkCollection(list, 0);
    list.set(0, 0);
    list.set(0, 1);
    checkCollection(list, 1);
    list.set(0, 2);
    checkCollection(list, 2);
    list.set(0, 0);
    checkCollection(list, 0);
    list.add(1);
    checkCollection(list, 0, 1);
    list.set(1, 1);
    list.set(1, 2);
    checkCollection(list, 0, 2);
    list.set(1, 3);
    checkCollection(list, 0, 3);
    list.set(1, 0);
    checkCollection(list, 0, 0);
    assertEquals(0, list.getChangeCount());
    list.set(1, 1);
    checkCollection(list, 0, 1);
    list.set(0, 1);
    checkCollection(list, 1, 1);
    assertEquals(0, list.getChangeCount());
    list.set(0, 2);
    checkCollection(list, 2, 1);
    list.set(0, 0);
    checkCollection(list, 0, 1);
    list.setRange(0, 2, 0);
    checkCollection(list, 0, 0);
    list.setRange(0, 2, 1);
    checkCollection(list, 1, 1);
    list.add(2);
    checkCollection(list, 1, 1, 2);
    list.setRange(1, 3, 2);
    checkCollection(list, 1, 2, 2);
  }

  public void testSetAll() {
    for (int i = 0; i < 10; i++) {
      LongArray array = generateRandomLongArray(1000, UNORDERED, 500);
      array.sort();
      list.addAll(array);
      int index = myRand.nextInt(list.size());
      int len = list.size() - index;
      LongList insertArray;
      if (len > 1) {
        insertArray = generateRandomLongArray(len, UNORDERED, len / 2);
      } else {
        insertArray = LongList.EMPTY;
      }
      list.setAll(index, insertArray);

      LongArray expected = new LongArray(list.iterator(0, index));
      expected.addAll(insertArray);
      expected.addAll(list.iterator(index + len));

      CHECK.order(expected, list);
    }
  }

  public void testSetBySort0() {
    int s = 5;
    list.addAll(LongCollections.repeat(0, s));
    list.addAll(LongCollections.repeat(1, s));
//    list.swap(0, 1);
    list.setRange(0, 1, 0);
    assertEquals(1, list.getChangeCount());
    checkCollection(list.subList(0, s), ap(0, s, 0));
    list.setRange(1, 2, 0);
    assertEquals(1, list.getChangeCount());
    checkCollection(list.subList(0, s), ap(0, s, 0));
    list.swap(0, 1);
    checkCollection(list.subList(0, s), ap(0, s, 0));
    checkCollection(list.subList(s, s * 2), ap(1, s, 0));
    assertEquals(1, list.getChangeCount());
  }


  public void testSetBySort() {
    for (int i = 0; i < 100; i++) {
      list.add(0);
      list.add(1);
    }
    list.sort();
    checkCollection(list.subList(0, 100), ap(0, 100, 0));
    checkCollection(list.subList(100, 200), ap(1, 100, 0));
    assertEquals(1, list.getChangeCount());
  }

  @Override
  protected void checkExpand(long[] expected, int index, int count, WritableLongList checked) {
    assert index <= expected.length;
    checked.expand(index, count);

    assertEquals(expected.length + count, checked.size());
    LongListIterator it = checked.iterator();

    for (int i = 0; i < index; i++) {
      assertEquals(expected[i], it.nextValue());
    }
    for (int i = 0; i < count; i++) {
      assertEquals(expected[index], it.nextValue());
    }
    for (int i = index; i < expected.length; i++) {
      assertEquals(expected[i], it.nextValue());
    }
  }

  public void testExpandSimple2() {
    list.expand(0, 10);
    CHECK.order(LongCollections.repeat(0, 10), list);
    list.insert(0, 99);
    list.clear();
    list.expand(0, 3);
    CHECK.order(LongCollections.repeat(0, 3), list);
  }

  public void testExpandComplex2() {
    LongArray expected = LongArray.create(0, 1, 2, 3, 4, 5);
    list.addAll(expected.iterator());
    CHECK.order(list.iterator(), expected.iterator());
    LongList addedValues;

    // value, count > 0, index
    int[][] arguments = {{10, 1000, 0}, {99, 10000, 0}, {77, 10000, 1000}, {33, 5000, 21006}};
    for(int[] args : arguments) {
      addedValues = LongCollections.repeat(args[0], args[1]);
      expected.insertAll(args[2], addedValues);
      list.insert(args[2], args[0]);
      list.expand(args[2], args[1] - 1);
      CHECK.order(list.iterator(), expected.iterator());
    }
  }

  public void testCreate() {
    LongArray values          = LongArray.create(0,2,4,6,8);
    IntArray counts           = IntArray.create(0, 1, 0, 3, 0);
    LongArray expected        = LongArray.create(2, 6, 6, 6);
    LongSameValuesList actual = LongSameValuesList.create(values, counts);
    CHECK.order(expected, actual);

    actual = LongSameValuesList.create(LongArray.create(9, 9, 2, 4, 6), IntArray.create(0, 1, 0, 3, 1));
    expected = LongArray.create(9, 4, 4, 4, 6);
    CHECK.order(expected, actual);

    int attemptsCount = 12;
    for (int i = 0; i < attemptsCount; i++) {
      values = generateRandomLongArray(100, UNORDERED);
      counts = generateRandomIntArray(100, UNORDERED, 4);
      if (i == attemptsCount - 1) {
        values.add(myRand.nextLong());
        counts.add(0);
      }
      expected = new LongArray(values.size() * 3);
      for (int j = 0; j < values.size(); j++) {
        expected.addAll(LongCollections.repeat(values.get(j), counts.get(j)));
      }
      actual = LongSameValuesList.create(values, counts);
      CHECK.order(expected, actual);
    }

    for (int i = 0; i < attemptsCount; i++) {
      values = generateRandomLongArray(100, SORTED, 150);
      counts = new IntArray(IntCollections.repeat(1, 100));
      actual = LongSameValuesList.create(values, counts);
      CHECK.order(values, actual);
    }

    expected = generateRandomLongArray(100, UNORDERED);
    CHECK.order(expected, LongSameValuesList.create(expected));
  }

  public void testCreateException() {
    try {
      list = LongSameValuesList.create(LongArray.create(1, 2, 3), IntArray.create(1, 2));
      fail();
    } catch (IllegalArgumentException _) {
      // ok
    }

    try {
      list = LongSameValuesList.create(LongArray.create(1, 2, 3), IntArray.create(1, -1));
      fail();
    } catch (IllegalArgumentException _) {
      // ok
    }

  }

  public void testRemoveFromBeginning() {
    LongArray expected = LongArray.create(-1, 0, 1, 2);
    list = LongSameValuesList.create(expected);
    list.removeAt(0);
    expected.removeAt(0);
    CHECK.order(expected, list);
  }

  public void testCreateSimple() {
    int count = 4;
    LongArray values = LongArray.create(0, 2, 4);
    LongArray expected = new LongArray(values.size() * count);
    for (int i = 0; i < values.size(); i++) {
      expected.addAll(LongCollections.repeat(values.get(i), count));
    }
    list = LongSameValuesList.create(values, IntCollections.repeat(4, values.size()));
    CHECK.order(expected, list);

    expected = LongArray.create(1, 1, 2, 2, 3, 3, 3, 3, 3, 4);
    list = LongSameValuesList.create(expected);
    CHECK.order(expected, list);
  }

  public void testSetRangeAndRemoveRangeComplex() {
    int[][] countsArray = {{1, 1, 1, 1, 1}, {1, 2, 3, 4, 5}, {5, 4, 3, 2, 1}, {2, 1, 2, 1, 2}};
    long[][] valuesArray = {{0, 10, 20, 30, 40}, {-1, 1, -1, 1, -1}, {0, 1, 2, 0, 3}};

    for (int[] counts : countsArray) {
      int size = 0;
      LongArray values = new LongArray(size);
      for (int count : counts) size += count;
      for (long[] values0 : valuesArray) {
        values.clear();
        assert values0.length == counts.length;
        for (int j = 0; j < counts.length; j++) {
          values.addAll(LongCollections.repeat(values0[j], counts[j]));
        }
        checkSetRangeAndRemoveRange(values);
      }

    }
  }

  private int getChangeCount(LongList expected) {
    int changeCount = 0;
    for (int i = 1; i < expected.size(); i++) {
      if (expected.get(i) != expected.get(i - 1)) changeCount++;
    }
    return changeCount;
  }

  private void checkSetRangeAndRemoveRange(LongList values) {
    int size = values.size();
    for (int from = 0; from < size; from++) {
      for(int to = from + 1; to < size; to++) {
        LongList valuesForSetRange = toSortedUnique(values.subList(max(0, from - 1), min(to + 1, size)));
        for (int val = 0; val < valuesForSetRange.size(); val++) {
          list = LongSameValuesList.create(values);
          list.setRange(from, to, val);
          LongList expected = concatLists(values.subList(0, from), repeat(val, to - from), values.subList(to, size));
          CHECK.order(expected, list);
          assertEquals(getChangeCount(expected), list.getChangeCount());
        }

        list = LongSameValuesList.create(values);
        list.removeRange(from, to);
        LongList expected = concatLists(values.subList(0, from), values.subList(to, size));
        CHECK.order(expected, list);
        assertEquals(getChangeCount(expected), list.getChangeCount());

      }
    }
  }

  public void testSetRangeSimple2() {
    int count = 4;
    LongArray values = LongArray.create(0, 2, 4);
    LongArray expected = new LongArray(values.size() * count);
    for (int i = 0; i < values.size(); i++) {
      expected.addAll(LongCollections.repeat(values.get(i), count));
    }
    list.addAll(expected);
    CHECK.order(expected, list);

    int[][] valuesForSet = {{0, 1, 1}, {1, 2, 1}, {4, 9, -1}};
    for (int[] vals : valuesForSet) {
      list.setRange(vals[0], vals[1], vals[2]);
      expected.setRange(vals[0], vals[1], vals[2]);
      CHECK.order(expected, list);
    }
  }

  public void testRemoveAllSimple() {
    list.addAll(0, 1, 0, 1, 0, 1);
    list.removeAll(0);
    checkCollection(list, 1, 1, 1);
  }
}