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

package com.almworks.integers.segmented;

import com.almworks.integers.*;
import com.almworks.integers.func.LongFunctions;

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public class LongSegmentedArrayTests extends WritableLongListChecker<LongSegmentedArray> {
  private TestEnvForLongSegmentedArray myEnv;
  private LongSegmentedArray array;
  private final int segmentSize = 1024;
  private final int checkedSize = segmentSize * 2 - 1;

  public void setUp() throws Exception {
    super.setUp();
    myEnv = new TestEnvForLongSegmentedArray();
    array = new LongSegmentedArray(myEnv);
  }


  @Override
  protected List<LongSegmentedArray> createWritableLongLists(long... values) {
    List<LongSegmentedArray> res = new ArrayList<LongSegmentedArray>();
    array = new LongSegmentedArray();
    array.addAll(values);
    res.add(array);

    array = new LongSegmentedArray();
    array.addAll(LongCollections.repeat(-1, 1025));
    array.clear();
    array.addAll(values);
    res.add(array);

    if (values.length < segmentSize) {
      for (int count = segmentSize - values.length - 1; count < segmentSize - values.length + 1; count++) {
        array = new LongSegmentedArray();
        array.addAll(LongCollections.repeat(-1, count));
        array.addAll(values);
        array.removeRange(0, count);
        res.add(array);
      }
    }

    if (IntegersDebug.CHECK && values.length == checkedSize) {
      LongArray vals = new LongArray(values);
      int size = values.length;
      int ss = segmentSize;
      int[] points = {0, ss/2, ss - 1, ss, ss + ss/2, ss * 2 - 1};
      for (int point: points) {
        array = new LongSegmentedArray();
        array.addAll(vals.subList(0, point));
        array.add(RAND.nextLong());
        array.addAll(vals.subList(point, size));
        array.removeAt(point);
        res.add(array);
      }
    }
    return res;
  }

  public void testBigArrays() {
    int maxValue = 1000;
    for (int i = 0; i < 10; i++) {
      long[] ar = generateRandomLongArray(checkedSize, UNORDERED, maxValue).extractHostArray();
      checkValues(ar);
      checkGetMethods(ar);
    }
  }

  protected void tearDown() throws Exception {
    array.clear();
    array = null;
    myEnv = null;
    super.tearDown();
  }

  public void testCopy() {
    LongSegmentedArray a1 = array;
    for (int i = 100000; i > 0; i--) {
      a1.add(i);
    }
    myEnv.clear();
    LongSegmentedArray a2 = a1.clone();
    assertEquals(0, myEnv.allocateCount);
    assertEquals(100000, a2.size());
    for (int i = 100000; i > 0; i--) {
      assertEquals(i, a2.get(100000 - i));
    }
    a1.set(9999, -1);
    assertEquals(1, myEnv.allocateCount);
    for (int i = 100000; i > 0; i--) {
      assertEquals(i, a2.get(100000 - i));
    }
    a2.set(500, -2);
    a2.set(9999, -3);
    assertEquals(2, myEnv.allocateCount);
    assertEquals(-2, a2.get(500));
    assertEquals(-3, a2.get(9999));
    assertEquals(99500, a1.get(500));
    assertEquals(-1, a1.get(9999));

    a1.removeRange(0, 10240);
    assertEquals(2, myEnv.freeCount);
  }

  public void testCloneApply() {
    int attemptsCount = 10;

    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      long[] values = generateRandomLongArray(checkedSize, UNORDERED).extractHostArray();
      LongArray expected = LongArray.copy(values);
      LongArray expected2 = LongArray.copy(values);
      expected2.apply(10, 20, LongFunctions.INC);
      expected2.apply(1025, 1030, LongFunctions.INC);
      for (LongSegmentedArray ar : createWritableLongLists(values)) {
        LongSegmentedArray clone = ar.clone();

        clone.apply(10, 20, LongFunctions.INC);
        clone.apply(1025, 1030, LongFunctions.INC);

        CHECK.order(ar, expected);
        CHECK.order(clone, expected2);
      }
    }
  }

  public void testCloneSetAll() {
    int attemptsCount = 10;

    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      long[] values = generateRandomLongArray(checkedSize, UNORDERED).extractHostArray();
      LongArray expected = LongArray.copy(values);
      LongArray expected2 = LongArray.copy(values);
      LongArray setAll1 = generateRandomLongArray(10, UNORDERED);
      LongArray setAll2 = generateRandomLongArray(5, UNORDERED);
      expected2.setAll(10, setAll1, 0, 10);
      expected2.setAll(1025, setAll2, 0, 5);

      for (LongSegmentedArray ar : createWritableLongLists(values)) {
        LongSegmentedArray clone = ar.clone();

        clone.setAll(10, setAll1, 0, 10);
        clone.setAll(1025, setAll2, 0, 5);

        CHECK.order(ar, expected);
        CHECK.order(clone, expected2);
      }
    }
  }

  @Override
  public void testBoundary() {
    assertEquals(0, array.size());
    array.apply(0, 0, null);
    array.clear();
    LongSegmentedArray clone = array.clone();
    assertEquals(array, clone);
    clone.clear();
    try {
      array.get(0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // ok
    }
    array.remove(0);
    array.removeRange(0, 0);
    assertEquals(array, array.subList(0, 0));
    array.toNativeArray(0, new long[0], 0, 0);
  }

  public void testInserts2() {
    array.insertMultiple(0, 1, 2048);
    checkList(array, ap(1, 2048, 0));
    array.insert(0, 2);
    array.insert(array.size(), 3);
    checkList(array, new long[] {2}, ap(1, 2048, 0), new long[] {3});
    array.insertMultiple(1, 2, 2000);
    checkList(array, ap(2, 2001, 0), ap(1, 2048, 0), new long[] {3});
    array.clear();

    // test shifts reusing whole segments
    myEnv.clear();
    array.insertMultiple(0, 1, 1024);
    assertEquals(0, myEnv.copied);
    array.insertMultiple(0, 2, 1024);
    assertEquals(0, myEnv.copied);
    checkList(array, ap(2, 1024, 0), ap(1, 1024, 0));
    array.insertMultiple(1024, 3, 1024);
    assertEquals(3, myEnv.allocateCount);
    assertEquals(0, myEnv.copied);
    checkList(array, ap(2, 1024, 0), ap(3, 1024, 0), ap(1, 1024, 0));
    array.insertMultiple(1024, 4, 1024);
    assertEquals(4, myEnv.allocateCount);
    assertEquals(0, myEnv.copied);
    checkList(array, ap(2, 1024, 0), ap(4, 1024, 0), ap(3, 1024, 0), ap(1, 1024, 0));
    array.clear();
    array.insertMultiple(0, 1, 10240);
    checkList(array, ap(1, 10240, 0));
    myEnv.clear();
    array.insertMultiple(6000, 2, 1024);
    assertEquals(1, myEnv.allocateCount);
    assertEquals(6 * 1024 - 6000, myEnv.copied);
    checkList(array, ap(1, 6000, 0), ap(2, 1024, 0), ap(1, 1024 * 11 - 7024, 0));
    myEnv.clear();
    array.insertMultiple(2000, 3, 1024);
    assertEquals(1, myEnv.allocateCount);
    assertEquals(2000 - 1024, myEnv.copied);
    checkList(array, ap(1, 2000, 0), ap(3, 1024, 0), ap(1, 7024 - 3024, 0), ap(2, 1024, 0), ap(1, 1024 * 12 - 8048, 0));
  }

  public void testRemoves2() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    myEnv.clear();
    array.removeRange(0, 1024);
    assertEquals(1, myEnv.freeCount);
    assertEquals(0, myEnv.copied);
    array.removeRange(10000 - 2048, 10000 - 1024);
    assertEquals(2, myEnv.freeCount);
    assertEquals(0, myEnv.copied);
    array.removeRange(0, 10);
    assertEquals(0, myEnv.copied);
    checkList(array, ap(1034, 7942, 1));
    array.removeAt(5000);
    assertEquals(2941, myEnv.copied);
    checkList(array, ap(1034, 5000, 1), ap(6035, 2941, 1));
  }

  @Override
  public void testSubList() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    myEnv.clear();
    checkList(array.subList(10, 20), ap(10, 10, 1));
    checkList(array.subList(10, 10000), ap(10, 9990, 1));
    checkList(array.subList(9990, 10000), ap(9990, 10, 1));
    checkList(array.subList(9990, 9990));
    assertEquals(array, array.subList(0, 10000));
    assertTrue(array == array.subList(0, 10000));
    assertEquals(0, myEnv.allocateCount);
    assertEquals(0, myEnv.copied);
  }

  @Override
  public void testCopySubList() {
    for (int i = 0; i < 10240; i++) {
      array.add(i);
    }
    myEnv.clear();
    array.addAll(array);
    checkList(array, ap(0, 10240, 1), ap(0, 10240, 1));

// segments are allocated and instantly freed - see to do in {@link SegmentedIntArray#insertSegmented}
//    assertEquals(0, myEnv.allocateCount);
    assertEquals(myEnv.allocateCount, myEnv.freeCount);
    assertEquals(0, myEnv.copied);

    array.setAll(100, array, 100, 100);
    checkList(array, ap(0, 10240, 1), ap(0, 10240, 1));
    array.setAll(100, array.subList(200, 300));
    checkList(array, ap(0, 100, 1), ap(200, 100, 1), ap(200, 10040, 1), ap(0, 10240, 1));

    array.insertAll(5000, array.subList(3000, 5000));
    checkList(array, ap(0, 100, 1), ap(200, 100, 1), ap(200, 4800, 1), ap(3000, 2000, 1), ap(5000, 5240, 1), ap(0, 10240, 1));

  }

  public void testCopyClone() {
    array.addAll(LongProgression.range(10240));
    myEnv.clear();
    array.addAll(array.clone());
    checkList(array, ap(0, 10240, 1), ap(0, 10240, 1));

    assertEquals(0, myEnv.copied);

    array.setAll(100, array, 100, 100);
    checkList(array, ap(0, 10240, 1), ap(0, 10240, 1));
    array.setAll(100, array.clone(200, 300));
    checkList(array, ap(0, 100, 1), ap(200, 100, 1), ap(200, 10040, 1), ap(0, 10240, 1));

    array.insertAll(5000, array.clone(3000, 5000));
    checkList(array, ap(0, 100, 1), ap(200, 100, 1), ap(200, 4800, 1), ap(3000, 2000, 1), ap(5000, 5240, 1), ap(0, 10240, 1));
  }

  public static void segmentedLongArrayChecker(LongList expected, LongSegmentedArray actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      long val = expected.get(i);
      assertTrue(val == -1 || val == actual.get(i));
    }
  }

  public void testExpandSimpleCase() {
    int[] elements = {5, 10, 4, 2, 1};
    for ( int i = 0; i < 5; i++) {
      array.add(elements[i]);
    }
    LongArray expected = LongArray.create(5, 10, 4, 2, 1);
    CHECK.order(array.iterator(), expected.iterator());

    for (int i = 0; i < 3; i++) {
      expected.insert(3, -1);
    }
    array.expand(3, 3);
    segmentedLongArrayChecker(expected, array);

    for (int i = 0; i < 2; i++) {
      expected.insert(3, -1);
    }
    array.expand(6, 2);
    segmentedLongArrayChecker(expected, array);

    try {
      array.expand(array.size() + 1, 5);
      fail();
    } catch (IndexOutOfBoundsException ex) {}

    try {
      array.expand(-1, 3);
      fail();
    } catch (IndexOutOfBoundsException ex) { }

    array.expand(array.size(), 5);
    for (int i = 0; i < 5; i++) {
      expected.add(-1);
    }
    segmentedLongArrayChecker(expected, array);
  }

  public void testSimpleResize() {
    long[] values0 = {-1, 0, 10, -10, 20, 30};
    LongArray values = new LongArray(values0);
    LongArray toAdd = LongArray.create(0, -4, -10, 20);
    LongList expected = LongCollections.concatLists(values, toAdd);
    for (WritableLongList list : createWritableLongLists(values0)) {
      CHECK.order(values, list);
      list.addAll(toAdd);
      CHECK.order(expected, list);
    }

    LongList valuesList = LongProgression.range(1024);
    values0 = valuesList.toNativeArray();
    long[] expected0 = LongCollections.collectLists(valuesList, new LongList.Single(1025)).extractHostArray();
    for (WritableLongList list : createWritableLongLists(values0)) {
      list.add(1025);
      CHECK.order(list, expected0);
    }
  }

  public void testMoveAndSet() {
    LongArray expected = new LongArray(LongProgression.arithmetic(0, 1050));
    array.addAll(expected);
    WritableLongListIterator it = array.iterator();
    it.move(1049);
    assertEquals(1048, it.value());
    it.remove();
    assertEquals(1049, it.nextValue());


    it.set(-48, -1000);
    assertEquals(-1000, array.get(1000));
    it.set(-48, 1000);

    it.move(-49);
    assertEquals(1000, it.nextValue());
    assertEquals(1030, it.get(30));
    it.set(30, -1030);
    assertEquals(-1030, array.get(1030));
    it.set(30, 1030);

    it.move(30);
    assertEquals(1000, it.get(-30));

    expected.removeLast();
    expected.removeLast();
    expected.add(1049);
    CHECK.order(expected, array);
  }

  public void testInserts3() {
    LongArray expected = new LongArray(ap(0, 10, 1));
    array.addAll(ap(0, 10, 1));
    array.insertMultiple(3, 100, 5);
    expected.insertMultiple(3, 100, 5);

    CHECK.order(expected, array);
  }

  public void testIteratorGet() {
    array.addAll(LongIterators.range(0, 1030));
    LongListIterator it = array.iterator();
    it.move(1024);
    for (int i = -10, cur = 1013; i < 5; i++) {
      assertEquals(it.get(i), cur++);
    }
  }

  public void testCreate() {
    int[] sizes = {15, 100, 200, 1022, 1025, 2000, 2050};
    for (int size : sizes) {
      LongArray expected = generateRandomLongArray(size, UNORDERED);
      array = LongSegmentedArray.create(expected);
      CHECK.order(expected, array);
    }
  }
}