package com.almworks.integers.optimized;

import com.almworks.integers.*;
import com.almworks.integers.func.LongFunction;
import com.almworks.integers.util.IntegersDebug;

import java.util.ArrayList;
import java.util.List;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public class SegmentedLongArrayTests extends WritableLongListChecker {
  private TestEnvForSegmentedLongArray myEnv;
  private SegmentedLongArray array;
  private final int segmentSize = 1024;
  private final int checkedSize = segmentSize * 2 - 1;

  public void setUp() throws Exception {
    super.setUp();
    myEnv = new TestEnvForSegmentedLongArray();
    array = new SegmentedLongArray(myEnv);
  }


  @Override
  protected List<WritableLongList> createWritableLongListVariants(long... values) {
    List<WritableLongList> res = new ArrayList<WritableLongList>();
    SegmentedLongArray array = new SegmentedLongArray();
    array.addAll(values);
    res.add(array);

    array = new SegmentedLongArray();
    array.addAll(LongCollections.repeat(-1, 1025));
    array.clear();
    array.addAll(values);
    res.add(array);

    if (values.length == checkedSize && IntegersDebug.CHECK) {
      LongArray vals = new LongArray(values);
      int size = values.length;
      int[] points = {0, segmentSize/2, segmentSize - 1, segmentSize, segmentSize + segmentSize/2, segmentSize * 2 - 1};
      for (int point: points) {
        array = new SegmentedLongArray();
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
    for (int i = 0; i < 10; i++) {
      long[] ar = generateRandomLongArray(checkedSize, false).extractHostArray();
      _testCHECK(ar);
      _testGetMethods(ar);
    }
  }

  protected void tearDown() throws Exception {
    array.clear();
    array = null;
    myEnv = null;
    super.tearDown();
  }

  public void testCopy() {
    SegmentedLongArray a1 = array;
    for (int i = 100000; i > 0; i--) {
      a1.add(i);
    }
    myEnv.clear();
    SegmentedLongArray a2 = a1.clone();
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

  @Override
  public void testBoundary() {
    assertEquals(0, array.size());
    array.apply(0, 0, null);
    array.clear();
    SegmentedLongArray clone = array.clone();
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
    checkList(array, ap(1, 0, 2048));
    array.insert(0, 2);
    array.insert(array.size(), 3);
    checkList(array, new long[] {2}, ap(1, 0, 2048), new long[] {3});
    array.insertMultiple(1, 2, 2000);
    checkList(array, ap(2, 0, 2001), ap(1, 0, 2048), new long[] {3});
    array.clear();

    // test shifts reusing whole segments
    myEnv.clear();
    array.insertMultiple(0, 1, 1024);
    assertEquals(0, myEnv.copied);
    array.insertMultiple(0, 2, 1024);
    assertEquals(0, myEnv.copied);
    checkList(array, ap(2, 0, 1024), ap(1, 0, 1024));
    array.insertMultiple(1024, 3, 1024);
    assertEquals(3, myEnv.allocateCount);
    assertEquals(0, myEnv.copied);
    checkList(array, ap(2, 0, 1024), ap(3, 0, 1024), ap(1, 0, 1024));
    array.insertMultiple(1024, 4, 1024);
    assertEquals(4, myEnv.allocateCount);
    assertEquals(0, myEnv.copied);
    checkList(array, ap(2, 0, 1024), ap(4, 0, 1024), ap(3, 0, 1024), ap(1, 0, 1024));
    array.clear();
    array.insertMultiple(0, 1, 10240);
    checkList(array, ap(1, 0, 10240));
    myEnv.clear();
    array.insertMultiple(6000, 2, 1024);
    assertEquals(1, myEnv.allocateCount);
    assertEquals(6 * 1024 - 6000, myEnv.copied);
    checkList(array, ap(1, 0, 6000), ap(2, 0, 1024), ap(1, 0, 1024 * 11 - 7024));
    myEnv.clear();
    array.insertMultiple(2000, 3, 1024);
    assertEquals(1, myEnv.allocateCount);
    assertEquals(2000 - 1024, myEnv.copied);
    checkList(array, ap(1, 0, 2000), ap(3, 0, 1024), ap(1, 0, 7024 - 3024), ap(2, 0, 1024), ap(1, 0, 1024 * 12 - 8048));
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
    checkList(array, ap(1034, 1, 7942));
    array.removeAt(5000);
    assertEquals(2941, myEnv.copied);
    checkList(array, ap(1034, 1, 5000), ap(6035, 1, 2941));
  }

  @Override
  public void testSubList() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    myEnv.clear();
    checkList(array.subList(10, 20), ap(10, 1, 10));
    checkList(array.subList(10, 10000), ap(10, 1, 9990));
    checkList(array.subList(9990, 10000), ap(9990, 1, 10));
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
    checkList(array, ap(0, 1, 10240), ap(0, 1, 10240));

// segments are allocated and instantly freed - see to do in {@link SegmentedIntArray#insertSegmented}
//    assertEquals(0, myEnv.allocateCount);
    assertEquals(myEnv.allocateCount, myEnv.freeCount);
    assertEquals(0, myEnv.copied);

    array.setAll(100, array, 100, 100);
    checkList(array, ap(0, 1, 10240), ap(0, 1, 10240));
    array.setAll(100, array.subList(200, 300));
    checkList(array, ap(0, 1, 100), ap(200, 1, 100), ap(200, 1, 10040), ap(0, 1, 10240));

    array.insertAll(5000, array.subList(3000, 5000));
    checkList(array, ap(0, 1, 100), ap(200, 1, 100), ap(200, 1, 4800), ap(3000, 1, 2000), ap(5000, 1, 5240), ap(0, 1, 10240));

  }

  public void testCopyClone() {
    for (int i = 0; i < 10240; i++) {
      array.add(i);
    }
    myEnv.clear();
    array.addAll(array.clone());
    checkList(array, ap(0, 1, 10240), ap(0, 1, 10240));

    assertEquals(0, myEnv.copied);

    array.setAll(100, array, 100, 100);
    checkList(array, ap(0, 1, 10240), ap(0, 1, 10240));
    array.setAll(100, array.clone(200, 300));
    checkList(array, ap(0, 1, 100), ap(200, 1, 100), ap(200, 1, 10040), ap(0, 1, 10240));

    array.insertAll(5000, array.clone(3000, 5000));
    checkList(array, ap(0, 1, 100), ap(200, 1, 100), ap(200, 1, 4800), ap(3000, 1, 2000), ap(5000, 1, 5240), ap(0, 1, 10240));

  }

  public static void segmentedLongArrayChecker(LongList expected, SegmentedLongArray actual) {
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

  public void testSimpleResize()  {
    array.addAll(0);
    LongList repeat1 = LongCollections.repeat(1, 1040);
    array.addAll(repeat1);
    CHECK.order(array.iterator(), LongCollections.concatIterables(LongArray.create(0), repeat1));
  }

  public void test() {
    int j = 0;
    for (int i = 0; i < 20; i++) {
      if (i == 15) {
        i += 0;
      }
      array.insert(0, i);
    }
  }
}