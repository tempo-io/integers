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

import com.almworks.integers.func.LongFunctions;
import com.almworks.integers.func.LongToLong;
import com.almworks.integers.segmented.LongSegmentedArray;
import com.almworks.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.*;
import static com.almworks.integers.LongCollections.*;

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
    LongSegmentedArray segarray = new LongSegmentedArray();
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
      int elem = myRand.nextInt(maxElem) * factor;
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
      int idx = myRand.nextInt(max);
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
        do value = myRand.nextInt(maxElem);
        while (value % 2 == 0 || value % 3 == 0);
        withExtra.add(value);
      }
    }
    trueIntersection.sortUnique();
    withExtra.sortUnique();
    return Pair.create(trueIntersection, withExtra);
  }

  public void testDiffSortedUniqueListsSimple() throws Exception {
    CHECK.order(diffSortedUniqueLists(LongList.EMPTY, LongList.EMPTY));
    CHECK.order(diffSortedUniqueLists(LongArray.create(0, 3, 4, 7), LongArray.create(1, 2, 3, 4, 6, 8)), 0, 1, 2, 6, 7, 8);
  }

  public void testDiffSortedUniqueLists() {
    new SetOperationsChecker().check(myRand, new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return LongCollections.diffSortedUniqueLists(arrays[0], arrays[1]).iterator();
      }
    }, new SetOperationsChecker.DiffGetter(), true, SORTED_UNIQUE);
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
        arr[i] = myRand.nextInt(maxInt);
      }
      checkFindDuplicate(arr);
    }

  }

  public void testIsSorted() {
    assertTrue(LongCollections.isSorted(new long[]{}));
    assertTrue(LongCollections.isSorted(new long[]{Integer.MIN_VALUE, 1, 4, 5, 10, 20, 21, Integer.MAX_VALUE}));
    assertFalse(LongCollections.isSorted(new long[]{1, 4, 5, 20, 19}));
    assertTrue(LongCollections.isSorted(new long[]{1, 4, 5, 20, 19, 15}, 1, 3));
    assertFalse(LongCollections.isSorted(new long[]{1, 4, 3, 20, 19, 15}, 1, 3));
  }

  public void testIsSortedUnique() {
    assertEquals(0, LongCollections.isSortedUnique(true, new long[]{}, 0, 0));
    assertEquals(0, LongCollections.isSortedUnique(true, null, 0, 0));
    assertEquals(0, LongCollections.isSortedUnique(false, new long[]{}, 0, 0));
    assertEquals(0, LongCollections.isSortedUnique(true, new long[]{1, 5, 10, 11, 20}, 0, 5));
    assertEquals(0, LongCollections.isSortedUnique(false, new long[]{1, 5, 10, 11, 20}, 0, 5));
    assertEquals(2, LongCollections.isSortedUnique(true, new long[]{1, 10, 5, 11, 4}, 0, 5));
    assertEquals(-3, LongCollections.isSortedUnique(true, new long[]{1, 5, 5, 10, 15, 19, 19, 100, 121, 121}, 0, 10));
    assertEquals(2, LongCollections.isSortedUnique(false, new long[]{1, 5, 5, 10, 15, 19, 19, 100, 121, 121}, 0, 10));
    assertEquals(5, LongCollections.isSortedUnique(false, new long[]{1, 5, 10, 15, 19, 19, 100, 121, 121}, 0, 9));
    assertEquals(7, LongCollections.isSortedUnique(false, new long[]{1, 5, 10, 15, 19, 100, 121, 121}, 0, 8));
  }

  public void testToSorted() {
    int arrLength = 100;
    int maxVal = 300;
    int attempts = 10;
    LongArray expected;
    LongArray arr;

    for (int attempt = 0; attempt < attempts; attempt++) {
      if (attempt != attempts - 1) {
        arr = generateRandomLongArray(arrLength, UNORDERED, maxVal);
      } else {
        arr = new LongArray();
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
    BinarySearchChecker.test(myRand, new BinarySearchChecker.BinarySearcher() {
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

    long[] expected = new long[10];
    Arrays.fill(expected, 3, 10, -1);
    long[] actual = new long[10];
    LongCollections.repeat(-1, 10).toNativeArray(3, actual, 3, 7);
    CHECK.order(expected, actual);

    try {
      LongCollections.repeat(3, -1);
      fail("must throw IAE");
    } catch(IllegalArgumentException ex) {
      // ok
    }
  }

  public void testGetNextDiffValueIndex() {
    LongList testList1 = LongArray.create(1, 1, 2, 2, 2, 2, 3, 2);
    IntList expectedIndexes = IntArray.create(0, 2, 6, 7);
    CHECK.order(getNextDifferentValueIndex(testList1), expectedIndexes.iterator());

    LongSameValuesList testList2 = new LongSameValuesList();
    testList2.addAll(testList1);
    CHECK.order(getNextDifferentValueIndex(testList2), expectedIndexes.iterator());

    LongList testList3 = LongArray.create(0, 0, 1, 1, 1, 1, 10, 1);
    CHECK.order(getNextDifferentValueIndex(testList3), expectedIndexes.iterator());

    LongSameValuesList testList4 = new LongSameValuesList();
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

  public void checkToBoundedString(String expected, int lim, LongArray array) {
    WritableLongSet set = LongTreeSet.createFromSortedUnique(array);
    LongIterable[] iterables = {array, set, array.iterator(), set.iterator(),
        LongIterators.unionIterator(array, array.subList(0, 1))};
    for (LongIterable iterable : iterables) {
      assertEquals(iterable.toString(), expected, LongCollections.toBoundedString(iterable, lim));
    }
  }

  public void testToBoundedString() {
    LongArray array = new LongArray();
    array.addAll(LongIterators.range(10));
    checkToBoundedString("(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)", 5, array);

    array.add(10);
    checkToBoundedString("[11] (0, 1, 2, 3, 4, ..., 6, 7, 8, 9, 10)", 5, array);

    array.addAll(LongIterators.range(11, 21));
    checkToBoundedString("[21] (0, 1, 2, 3, 4, ..., 16, 17, 18, 19, 20)", 5, array);

    array.addAll(LongIterators.range(21, 40));
    StringBuilder expected = new StringBuilder().append("(0");
    for (int i = 1; i < 40; i++) {
      expected.append(", ").append(i);
    }
    expected.append(')');
    checkToBoundedString(expected.toString(), 20, array);

    array = LongArray.create(0, 1, 2, 3, 4);
    checkToBoundedString("[5] (0, 1, ..., 3, 4)", 2, array);
  }

  // TODO add test union for unsortable hash set
  public void testUnionSortedSets() {
    int maxSize = 1000, attemptsCount = 30;
    WritableLongSet[] sets = new WritableLongSet[2];
    LongArray[] arrays = new LongArray[2];
    LongArray expected, actual;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      for (int j = 0; j < 2; j++) {
        arrays[j] = generateRandomLongArray(maxSize, IntegersFixture.SortedStatus.SORTED_UNIQUE);
        sets[j] = (myRand.nextBoolean()) ?
            LongTreeSet.createFromSortedUnique(arrays[j]) : LongOpenHashSet.createFrom(arrays[j]);
      }
      expected = new LongArray(maxSize * 2);
      expected.addAll(arrays[0]);
      expected.merge(arrays[1]);
      actual = toSortedUnion(sets[0], sets[1]).toArray();
      CHECK.order(expected, actual);
    }
  }

  public void testIntersectionUnionSets() {
    int attempts = 10, maxVal = Integer.MAX_VALUE, size = 1000;
    WritableLongSet[][] allSets = {{new LongTreeSet(), new LongOpenHashSet()},
        {new LongTreeSet(), new LongOpenHashSet()}};
    LongArray[] arrays = new LongArray[2];
    LongArray expected, actual;
    for (int attempt = 0; attempt < attempts; attempt++) {
      for (WritableLongSet set0: allSets[0]) {
        for (WritableLongSet set1: allSets[1]) {
          WritableLongSet[] sets = {set0, set1};
          for (int j = 0; j < 2; j++) {
            arrays[j] = generateRandomLongArray(size, IntegersFixture.SortedStatus.SORTED_UNIQUE, maxVal);
            sets[j].clear();
            sets[j].addAll(arrays[j]);
          }
          expected = LongArray.copy(arrays[0]);
          expected.retain(arrays[1]);
          IntegersFixture.checkSet(intersection(sets[0], sets[1]), expected);

          expected = LongArray.copy(arrays[0]);
          expected.merge(arrays[1]);
          IntegersFixture.checkSet(union(sets[0], sets[1]), expected);
        }
      }
    }
  }

  public void testComplementSorted() {
    new SetOperationsChecker().check(myRand, new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return LongCollections.complementSorted(arrays[0], arrays[1]).iterator();
      }
    }, new SetOperationsChecker.MinusGetter(), true, SORTED_UNIQUE, SORTED);
  }

  public void testIntersectionSorted() {
    new SetOperationsChecker().check(myRand, new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return LongCollections.intersectionSortedUnique(arrays[0], arrays[1]).iterator();
      }
    }, new SetOperationsChecker.IntersectionGetter(true), true, SORTED_UNIQUE);
  }

  public void testUnionSorted() {
    new SetOperationsChecker().check(myRand, new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return LongCollections.unionSortedUnique(arrays[0], arrays[1]).iterator();
      }
    }, new SetOperationsChecker.UnionGetter(), true, SORTED_UNIQUE);
  }

  public void checkToNativeArray(LongArray array) {
    long[] expected = array.toNativeArray();
    LongSet set = LongTreeSet.createFromSortedUnique(array);
    LongIterable[] iterables = {array, set, array.iterator(), set.iterator(),
        LongIterators.unionIterator(array, array.isEmpty() ? LongIterator.EMPTY : array.subList(0, 1))};
    for (LongIterable iterable: iterables) {
      CHECK.order(expected, LongCollections.toNativeArray(iterable));
    }
  }

  public void testToNativeArray() {
    long[][] arrays = {{}, {0}, {0, 1, 2}, {Long.MIN_VALUE, 0, Long.MAX_VALUE}, {0, 1, 2, 3, 4}};
    for (long[] expected: arrays) {
      checkToNativeArray(new LongArray(expected));
    }
    for (int attempt = 0; attempt < 10; attempt++) {
      checkToNativeArray(generateRandomLongArray(100, IntegersFixture.SortedStatus.SORTED_UNIQUE));
    }
  }

  public void checkToWritableSortedUnique(long[] array) {
    LongArray expected = LongArray.copy(array);
    expected.sortUnique();
    LongArray actual = toWritableSortedUnique(array);
    CHECK.order(expected, actual);
  }

  public void testToWritableSortedUnique() {
    long[][] arrays = {{}, {0}, {0, 1, 2}, {Long.MIN_VALUE, 0, Long.MAX_VALUE},
        {0, 1, 2, 3, 4}, {0, 0, 1, 2, 3}, {0, 1, 2, 2, 3, 4}, {0, 1, 2, 3, 4, 4}};
    for (long[] array : arrays) {
      checkToWritableSortedUnique(array);
    }
    for (int attempt = 0; attempt < 10; attempt++) {
      LongArray array = generateRandomLongArray(100, UNORDERED, 150);
      checkToWritableSortedUnique(array.toNativeArray());
      array.sort();
      checkToWritableSortedUnique(array.toNativeArray());
      array.removeDuplicates();
      checkToWritableSortedUnique(array.toNativeArray());
    }
  }

  public void testHasMethods() {
    int MIN = Integer.MIN_VALUE, MAX = Integer.MAX_VALUE;
    LongArray someValues = LongArray.create(MIN, MIN + 1, 0, 1, 3, 5, MAX - 1, MAX);
    for (LongArray first : LongCollections.allSubLists(someValues)) {
      for (LongArray second : LongCollections.allSubLists(someValues)) {
        IntegersDebug.println(LongCollections.toBoundedString(first) +
            " " + LongCollections.toBoundedString(second));
        assertEquals(new LongUnionIterator(first, second).hasNext(),
            hasUnion(first, second));
        assertEquals(new LongIntersectionIterator(first, second).hasNext(),
            hasIntersection(first, second));
        assertEquals(new LongMinusIterator(first.iterator(), second.iterator()).hasNext(),
            hasComplement(first, second));
      }
    }
  }

  public void testSortPairs() {
    try {
      LongCollections.sortPairs(LongArray.create(0, 1, 2), LongArray.create(10, 20));
      fail();
    } catch (IllegalArgumentException e) {}

    int attempts = 10;
    int len = 100;
    int maxVal = 200;
    for (int attempt = 0; attempt < attempts; attempt++) {
      LongArray[] actual = new LongArray[2];
      for (int i = 0; i < 2; i++) {
        actual[i] = generateRandomLongArray(len, UNORDERED, maxVal);
      }

      LongArray expected = new LongArray(len);
      for (int i = 0; i < len; i++) {
        expected.add(actual[0].get(i) * maxVal + actual[1].get(i));
      }
      expected.sort();

      LongCollections.sortPairs(actual[0], actual[1]);

      for (int i = 0; i < len; i++) {
        assertEquals(expected.get(i), actual[0].get(i) * maxVal + actual[1].get(i));
      }
    }
  }

  public void checkAllSubLists(LongList list, ArrayList<Long>[] lists, int size) {
    int i = 0;
    for (LongArray array : allSubLists(list)) {
      CHECK.order(LongCollections.asLongList(lists[i++]), array);
    }
    assertEquals(size, i);
  }

  public void testAllSubLists() {
    ArrayList<Long>[] lists = new ArrayList[65];
    lists[0] = new ArrayList<Long>();
    LongArray cur = LongArray.create();
    checkAllSubLists(cur, lists, 1);

    int size = 1;
    for (long elem = 0; elem < 6; elem++) {
      for (int j = 0; j < size; j++) {
        int idx = size + j;
        lists[idx] = new ArrayList<Long>();
        lists[idx].addAll(lists[j]);
        lists[idx].add(elem);
      }
      size <<= 1;
      cur.add(elem);
      checkAllSubLists(cur, lists, size);
    }
  }

  public void testEnsureCapacity() {
    long[] values = new long[]{0, 1, 2, 3, 4};
    for (int i = 0; i <= values.length; i++) {
      assertEquals(values, ensureCapacity(values, i));
    }
    long[] expected = new long[16];
    System.arraycopy(values, 0, expected, 0, 5);
    for (int i = values.length + 1; i < 17; i++) {
      CHECK.order(expected, ensureCapacity(values, i));
    }

    expected = new long[20];
    System.arraycopy(values, 0, expected, 0, 5);
    CHECK.order(expected, ensureCapacity(values, 20));

    values = LongProgression.Arithmetic.nativeArray(0, 20, 1);
    assertEquals(values, ensureCapacity(values, 20));
    expected = new long[40];
    System.arraycopy(values, 0, expected, 0, 20);
    CHECK.order(expected, ensureCapacity(values, 21));
    CHECK.order(expected, ensureCapacity(values, 40));
  }

  public void checkCollect(LongList ... lists) {
    LongArray expected = new LongArray();
    for (LongList array : lists) {
      expected.addAll(array);
    }
    CHECK.order(expected, collectLists(lists));

    int attemptsCount = 5;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      LongIterable[] iterables = new LongIterable[lists.length];
      for (int i = 0; i < lists.length; i++) {
        switch (myRand.nextInt(3)) {
          case 0: iterables[i] = lists[i]; break;
          case 1: iterables[i] = lists[i].iterator(); break;
          case 2:
            int size2 = lists[i].size() / 2;
            iterables[i] = new LongConcatIterator(lists[i].subList(0, size2), lists[i].subList(size2, lists[i].size()));
            break;
          default: assert false;
        }
      }
      CHECK.order(expected, collectIterables(iterables));

      for (int i = 0; i < iterables.length; i++) {
        if (iterables[i] instanceof LongIterator) {
          iterables[i] = lists[i].iterator();
        }
      }
      CHECK.order(expected, collectIterables(expected.size(), iterables));
    }
  }

  public void testCollect() {
    checkCollect(LongList.EMPTY, LongArray.create(0, 1, 10), LongArray.create(10, 20, 30));
    checkCollect(LongList.EMPTY, LongArray.create(5), LongList.EMPTY);

    int maxSize = 10, maxArraySize = 15;
    for (int size = 0; size < maxSize; size++) {
      LongArray[] arrays = new LongArray[size];
      for (int i = 0; i < arrays.length; i++) {
        arrays[i] = generateRandomLongArray(maxArraySize, UNORDERED);
      }
      checkCollect(arrays);
    }
  }

  public void testMap() {
    int attemptsCount = 10;
    int size = 200;
    int maxVal = 10000;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      LongArray values = generateRandomLongArray(size, UNORDERED, maxVal);
      LongToLong[] functions = {LongFunctions.SQR, LongFunctions.INC, LongFunctions.DEC, LongFunctions.NEG};
      for (LongToLong fun : functions) {
        long[] expected = values.toNativeArray();
        for (int i = 0; i < expected.length; i++) {
          expected[i] = fun.invoke(expected[i]);
        }
        checkCollection(LongCollections.map(fun, values), expected);
      }
    }
  }
}
