/*
 * Copyright 2010 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almworks.integers.util;

import com.almworks.integers.*;
import com.almworks.integers.optimized.SameValuesLongList;
import com.almworks.integers.optimized.SegmentedLongArray;
import com.almworks.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.almworks.integers.LongCollections.*;
import static com.almworks.integers.LongIterators.arithmetic;

public class LongCollectionsTests extends IntegersFixture {
  public static final CollectionsCompare COMPARE = new CollectionsCompare();
  private final LongArray myArray = new LongArray();

  public void testArray_RemoveSubsequentDuplicates() {
    myArray.addAll(1, 1, 2);
    myArray.sortUnique();
    checkCollection(myArray, 1, 2);
    myArray.clear();

    myArray.addAll(1, 2, 2, 3);
    myArray.sortUnique();
    checkCollection(myArray, 1, 2, 3);
    myArray.clear();

    myArray.addAll(1, 2, 3, 3);
    myArray.sortUnique();
    checkCollection(myArray, 1, 2, 3);
    myArray.clear();

    myArray.addAll(1, 1, 2, 2, 3, 3);
    myArray.sortUnique();
    checkCollection(myArray, 1, 2, 3);
  }

  public void testConcatenation() {
    LongListConcatenation concat = new LongListConcatenation();
    assertEquals(0, concat.getSliceCount());
    checkCollection(concat);
    concat.addSlice(LongList.EMPTY);
    assertEquals(1, concat.getSliceCount());
    checkCollection(concat);
    concat.addSlice(myArray);
    checkCollection(concat);
    myArray.add(1);
    checkCollection(concat, 1);
    concat.addSlice(LongList.EMPTY);
    checkCollection(concat, 1);
    SegmentedLongArray segarray = new SegmentedLongArray();
    concat.addSlice(segarray);
    concat.addSlice(LongList.EMPTY);
    checkCollection(concat, 1);
    segarray.add(3);
    checkCollection(concat, 1, 3);
    myArray.add(2);
    checkCollection(concat, 1, 2, 3);
  }

  public void testUniteTwoLengthySortedSetsAndIntersectWithThirdShort() {
    doTestSimple();
    doTestMany();
  }

  private void doTestSimple() {
    LongList a = LongArray.create(1, 2, 4, 7, 8);
    LongList b = LongArray.create(2, 3, 5, 7);
    LongList intW = LongArray.create(2, 3, 6, 7, 8);
    COMPARE.order(uniteTwoLengthySortedSetsAndIntersectWithThirdShort(a, b, intW), 2, 3, 7, 8);
    COMPARE.order(uniteTwoLengthySortedSetsAndIntersectWithThirdShort(LongList.EMPTY, b, intW), 2, 3, 7);
    COMPARE.order(uniteTwoLengthySortedSetsAndIntersectWithThirdShort(a, LongList.EMPTY, intW), 2, 7, 8);
    COMPARE.order(uniteTwoLengthySortedSetsAndIntersectWithThirdShort(a, b, LongList.EMPTY));
  }

  private void doTestMany() {
    int len = 1000;
    float intersRate = 0.3f;
    int intersLen = 100;
    int maxElem = len * 10;
    for (int nTest = 0; nTest < 10; ++nTest) {
      LongList a = createRand(len, maxElem, 2);
      LongList b = createRand(len, maxElem, 3);
      Pair<LongArray, LongArray> exp_int = createInters(a, b, intersRate, intersLen, maxElem);
      LongArray inters = exp_int.getSecond();
      IntegersDebug.println("\n/////////\na: " + a + "\nb: " + b + "\ninters:" + inters);
      COMPARE.order(exp_int.getFirst(), uniteTwoLengthySortedSetsAndIntersectWithThirdShort(a, b, inters));
    }
  }

  private LongList createRand(int len, int maxElem, int factor) {
    maxElem = maxElem / factor;
    LongArray larr = new LongArray(len);
    for (int i = 0; i < len; ++i) {
      int elem = RAND.nextInt(maxElem) * factor;
      larr.add(elem);
    }
    larr.sortUnique();
    return larr;
  }

  private Pair<LongArray, LongArray> createInters(LongList a, LongList b, float intersRate, int intersLen, int maxElem) {
    LongArray trueIntersection = new LongArray(intersLen);
    LongArray withExtra = new LongArray(intersLen);
    int sza = a.size();
    int szb = b.size();
    int max = (int)((sza + szb) / intersRate);
    for (int i = 0; i < intersLen; ++i) {
      int idx = RAND.nextInt(max);
      if (idx < sza) {
        long value = a.get(idx);
        trueIntersection.add(value);
        withExtra.add(value);
      } else if (idx < szb) {
        long value = b.get(idx - sza);
        trueIntersection.add(value);
        withExtra.add(value);
      } else {
        long value;
        do value = RAND.nextInt(maxElem);
        while (value % 2 == 0 || value % 3 == 0);
        withExtra.add(value);
      }
    }
    trueIntersection.sortUnique();
    withExtra.sortUnique();
    return Pair.create(trueIntersection, withExtra);
  }

  public void testDiffSortedLists() throws Exception {
    CHECK.order(diffSortedLists(LongList.EMPTY, LongList.EMPTY));
    CHECK.order(diffSortedLists(LongArray.create(0, 3, 4, 7), LongArray.create(1, 2, 3, 4, 6, 8)), 0, 1, 2, 6, 7, 8);

    LongArray diff = new LongArray();
    LongArray a = new LongArray();
    LongArray b = new LongArray();
    for (int i = 0; i < 10; ++i) {
      initLists(a, b, diff);
      CHECK.order(diffSortedLists(a, b), diff);
    }
  }

  private void initLists(LongArray a, LongArray b, LongArray diff) {
    a.clear();
    b.clear();
    diff.clear();
    for (int i = 0; i < 100; ++i) {
      a.add(RAND.nextInt(1000));
      b.add(RAND.nextInt(1000));
    }
    a.sortUnique();
    b.sortUnique();
    LongArray notb = LongArray.copy(a);
    notb.removeAll(b);
    LongArray nota = LongArray.copy(b);
    nota.removeAll(a);
    diff.addAll(nota);
    diff.addAll(notb);
    diff.sortUnique();
  }

  public void testAsLongList() {
    List<Long> arr = new ArrayList<Long>();
    for (int i = 0; i < 10; i += 2) {
      arr.add(Long.valueOf(i));
    }
    LongList res = asLongList(arr);
    CHECK.order(LongArray.create(0,2,4,6,8), res);

    arr.clear();
    res = asLongList(arr);
    assertEquals(res, LongList.EMPTY);
  }

  public void testArrayCopy() {
    long[] arr = {0, 1, 2, 3, 4, 5};
    long[] res = LongCollections.arrayCopy(arr, 2, 3);
    long[] expected = {2, 3, 4};
    CHECK.order(res, expected);

    res = LongCollections.arrayCopy(arr, 2, 0);
    assertEquals(res, IntegersUtils.EMPTY_LONGS);
  }

  public void checkFindDuplicate(long ... values) {
    int result = LongCollections.findDuplicate(LongArray.create(values));
    if (result == -1) {
      for (int i = 0; i < values.length; i++) {
        for (int j = i + 1; j < values.length; j++) {
          if (values[i] == values[j]) {
            fail(Arrays.toString(values));
          }
        }
      }
    } else {
      for (int i = 0; i < values.length; i++) {
        if (result != i && values[i] == values[result])
          return;
      }
      fail(Arrays.toString(values));
    }
  }

  public void testFindDuplicate() {
    checkFindDuplicate(5, 10, 20, 5);
    checkFindDuplicate(2, 4, 6, 8);
    checkFindDuplicate(2, 4, 6, 8, 2);

    int arrLength = 200;
    int maxInt = 400;
    long[] arr = new long[arrLength];
    for (int test = 0; test < 20; test++) {
      for (int i = 0; i < arrLength; i++) {
        arr[i] = RAND.nextInt(maxInt);
      }
      checkFindDuplicate(arr);
    }

  }

  public void testIsSorted() {
    assertTrue(LongCollections.isSorted(new long[]{Integer.MIN_VALUE, 1, 4, 5, 10, 20, 21, Integer.MAX_VALUE}));
    assertFalse(LongCollections.isSorted(new long[]{1, 4, 5, 20, 19}));

    assertTrue(LongCollections.isSorted(new long[]{1, 4, 5, 20, 19, 15}, 1, 3));
    assertFalse(LongCollections.isSorted(new long[]{1, 4, 3, 20, 19, 15}, 1, 3));

    assertEquals(0, LongCollections.isSortedUnique(true, new long[]{1, 5, 10, 11, 20}, 0, 5));
    assertEquals(-3, LongCollections.isSortedUnique(true, new long[]{1, 5, 5, 10, 15, 19, 19, 100, 121, 121}, 0, 10));
  }

  public void testToSorted() {
    int arrLength = 100;
    int maxVal = 150;
    LongArray expected;
    LongArray arr = new LongArray(LongProgression.arithmetic(0, arrLength, 0));

    for (int test = 0; test < 10; test++) {
      for (int i = 0; i < arrLength; i++) {
        arr.set(i, RAND.nextInt(maxVal));
      }
      expected = LongArray.copy(arr);
      expected.sort();
      CHECK.order(LongCollections.toSorted(false, arr), expected);
      CHECK.order(LongCollections.toSortedNativeArray(arr), expected.toNativeArray());
      expected.sortUnique();
      CHECK.order(LongCollections.toSorted(true, arr), expected);
      CHECK.order(LongCollections.toSortedUnique(arr), expected);
      CHECK.order(LongCollections.toSortedUnique(arr.toNativeArray()), expected);
    }
  }

  public void testToSortedSimple() {
    int arrLength = 10;
    LongArray arr = new LongArray(LongProgression.arithmetic(0, arrLength, 1));
    arr.add(arrLength++);
    for (int i = 0; i < arrLength; i++) {
      LongList expected = arr.subList(i, arr.size());
      for (boolean uniqueStatus: new boolean[]{false, true}) {
        LongList actual = LongCollections.toSorted(uniqueStatus, arr.subList(i, arr.size()).iterator());
        CHECK.order(actual, expected);

        actual = LongCollections.toSorted(uniqueStatus, arr.subList(i, arr.size()));
        CHECK.order(actual, expected);
      }
    }
  }

  public void testBinarySearch() {
    BinarySearchChecker.test(new BinarySearchChecker.BinarySearcher() {
      private long[] arr;
      private int length;

      public void init(LongArray values) {
        length = values.size();
        arr = values.toNativeArray();
      }

      public int size() {
        return length;
      }

      public long get(int index) {
        return arr[index];
      }

      public int binSearch(long value) {
        return LongCollections.binarySearch(value, arr);
      }
    });
  }

  public void testRemoveAllAtSorted() {
    LongArray a = new LongArray(LongProgression.arithmetic(0, 20));
    LongCollections.removeAllAtSorted(a, IntArray.create(0, 3, 4, 7, 10, 11, 12, 13, 19));
    CHECK.order(a.iterator(), 1, 2, 5, 6, 8, 9, 14, 15, 16, 17, 18);
    LongCollections.removeAllAtSorted(a, IntArray.create(1, 2, 3, 4, 9));
    CHECK.order(a.iterator(), 1, 9, 14, 15, 16, 18);
  }

  public void testRepeat() {
    LongList array = LongCollections.repeat(-5, 3);
    CHECK.order(array, -5, -5, -5);
    array = LongCollections.repeat(0, 4);
    CHECK.order(array, 0, 0, 0, 0);

    assertEquals(0, LongCollections.repeat(3, 0).size());
    try {
      LongCollections.repeat(3, -1);
      fail("must throw IAE");
    } catch(IllegalArgumentException ex) {}
  }

  public void testGetNextDiffValueIndex() {
    LongList testList1 = LongArray.create(1, 1, 2, 2, 2, 2, 3, 2);
    IntList expectedIndexes = IntArray.create(0, 2, 6, 7);
    CHECK.order(getNextDifferentValueIndex(testList1), expectedIndexes.iterator());

    SameValuesLongList testList2 = new SameValuesLongList();
    testList2.addAll(testList1);
    CHECK.order(getNextDifferentValueIndex(testList2), expectedIndexes.iterator());

    LongList testList3 = LongArray.create(0, 0, 1, 1, 1, 1, 10, 1);
    CHECK.order(getNextDifferentValueIndex(testList3), expectedIndexes.iterator());

    SameValuesLongList testList4 = new SameValuesLongList();
    testList4.addAll(testList3);
    CHECK.order(getNextDifferentValueIndex(testList4), expectedIndexes.iterator());
  }

  private IntIterator getNextDifferentValueIndex(LongList list) {
    WritableIntList resultingIndices = new IntArray();
    for (int i = 0; i < list.size(); i = list.getNextDifferentValueIndex(i)) {
      assertTrue("exceeded the list size!", i < list.size());
      resultingIndices.add(i);
    }
    return resultingIndices.iterator();
  }

  public void checkToBoundedString(String expected, int lim, LongArray array, LongSet set) {
    LongIterable[] iterables = {array, set, array.iterator(), set.iterator(),
        LongIterators.unionIterator(array, array.subList(0, 1))};
    for (LongIterable iterable: iterables) {
      assertEquals(iterable.toString(), expected, LongCollections.toBoundedString(iterable, lim));
    }
  }

  public void testToBoundedString() {
    LongArray array = new LongArray();
    WritableLongSortedSet set = new LongTreeSet();
    array.addAll(LongIterators.range(10));
    set.addAll(LongIterators.range(10));
    checkToBoundedString("(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)", 5, array, set);

    array.add(10);
    set.add(10);
    checkToBoundedString("[11] (0, 1, 2, 3, 4, ..., 6, 7, 8, 9, 10)", 5, array, set);

    array.addAll(LongIterators.range(11, 21));
    set.addAll(LongIterators.range(11, 21));
    checkToBoundedString("[21] (0, 1, 2, 3, 4, ..., 16, 17, 18, 19, 20)", 5, array, set);

    array.addAll(LongIterators.range(21, 40));
    set.addAll(LongIterators.range(21, 40));
    StringBuilder expected = new StringBuilder().append("(0");
    for (long i: range(1, 39)) {
      expected.append(", ").append(i);
    }
    expected.append(')');
    checkToBoundedString(expected.toString(), 20, array, set);

    array = LongArray.create(0, 1, 2, 3, 4);
    set.clear();
    set.addAll(array);
    checkToBoundedString("[5] (0, 1, ..., 3, 4)", 2, array, set);
  }

  // TODO add test union for unsortable hash set
  public void testUnionSets() {
    WritableLongSet[] sets = new WritableLongSet[2];
    LongArray[] arrays = new LongArray[2];
    LongList expected, actual;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 2; j++) {
        arrays[j] = generateRandomLongArray(10000, true);
        sets[j] = LongTreeSet.createFromSortedUnique(arrays[j]);
      }
      expected = union(arrays[0], arrays[1]);
      actual = union(sets[0], sets[1]).toArray();
      CHECK.order(expected, actual);
    }
  }

  // TODO add test intersection for unsortable hash set
  public void testIntersectionSets() {
    WritableLongSet[] sets = new WritableLongSet[2];
    LongArray[] arrays = new LongArray[2];
    LongList expected, actual;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 2; j++) {
        arrays[j] = generateRandomLongArray(10000, true);
        sets[j] = LongTreeSet.createFromSortedUnique(arrays[j]);
      }
      expected = intersectionSorted(arrays[0], arrays[1]);
      actual = intersection(sets[0], sets[1]).toArray();
      CHECK.order(expected, actual);
    }
  }

  public void testComplementSorted() {
    new SetOperationsChecker().check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return LongCollections.complementSorted(arrays[0], arrays[1]).iterator();
      }
    }, new SetOperationsChecker.MinusGetter(), true, true);
  }

}
