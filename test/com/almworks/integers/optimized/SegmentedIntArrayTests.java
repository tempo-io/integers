package com.almworks.integers.optimized;

import com.almworks.integers.*;

import java.util.NoSuchElementException;

public class SegmentedIntArrayTests extends NativeIntFixture {
  private TestEnvForSegmentedIntArray myEnv;
  private SegmentedIntArray array;

  protected void setUp() throws Exception {
    super.setUp();
    myEnv = new TestEnvForSegmentedIntArray();
    array = new SegmentedIntArray(myEnv);
  }

  protected void tearDown() throws Exception {
    array.clear();
    array = null;
    myEnv = null;
    super.tearDown();
  }

  public void testSimple() {
    for (int i = 0; i < 10000; i++) {
      array.add(i);
    }
    for (int i = 0; i < 10000; i++) {
      assertEquals(i, array.get(i));
    }
  }

  public void testCopy() {
    SegmentedIntArray a1 = array;
    for (int i = 100000; i > 0; i--) {
      a1.add(i);
    }
    myEnv.clear();
    SegmentedIntArray a2 = a1.clone();
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

  public void testInsertOneByOneInTheBeginning() {
    for (int i = 0; i < 10000; i++)
      array.insert(0, i);
    new IntCollectionsCompare().order(array.toNativeArray(),
      new IntProgression.Arithmetic(9999, 10000, -1).toNativeArray());
  }

  public void testRemoveByIterator() {
    int COUNT = 10000;
    for (int i = 0; i < COUNT; i++)
      array.add(COUNT - i);
    WritableIntListIterator ii = array.listIterator();
    int x = 10000;
    while (ii.hasNext()) {
      assertEquals(x, ii.nextValue());
      assertEquals(x, ii.get(0));
      if (x > 1)
        assertEquals(x - 1, ii.get(1));
      ii.set(0, 999);
      assertEquals(999, ii.get(0));
      ii.remove();
      x--;
    }
  }

  public void testBoundary() {
    assertEquals(0, array.size());
    array.apply(0, 0, null);
    array.clear();
    SegmentedIntArray clone = array.clone();
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
    array.toArray(0, new int[0], 0, 0);
  }

  public void testInserts() {
    array.insertMultiple(0, 1, 2048);
    checkList(array, ap(1, 0, 2048));
    array.insert(0, 2);
    array.insert(array.size(), 3);
    checkList(array, new int[] {2}, ap(1, 0, 2048), new int[] {3});
    array.insertMultiple(1, 2, 2000);
    checkList(array, ap(2, 0, 2001), ap(1, 0, 2048), new int[] {3});
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

  public void testRemoves() {
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

  public void testIteratorRemoveRange() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    WritableIntListIterator ii = array.listIterator(100, 600);
    for (int i = 0; i < 10; i++)
      ii.nextValue();
    ii.removeRange(-9, 1);
    try {
      ii.removeRange(-9, 1);
      fail();
    } catch (NoSuchElementException e) {
      // ok
    }
    ii.move(20);
    ii.removeRange(-9, 1);
    checkList(array, ap(0, 1, 100), ap(110, 1, 10), ap(130, 1, 9870));
    ii.removeRange(-9, 1);
    checkList(array, ap(0, 1, 100), ap(130, 1, 9870));
  }

  public void testIteratorRemoveFromEnd() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    WritableIntListIterator ii = array.listIterator(8191, 9192);
    ii.nextValue();
    while (ii.hasNext()) {
      ii.nextValue();
      ii.remove();
    }
    checkList(array, ap(0, 1, 8192), ap(9192, 1, 808));
  }

  public void testIteratorSkip() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    WritableIntListIterator ii = array.listIterator();
    for (int i = 0; i < 100; i++) {
      assertTrue(ii.hasNext());
      assertEquals(100 * i, ii.nextValue());
      ii.move(99);
    }
    assertFalse(ii.hasNext());
  }

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

  public void testSubSubList() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    IntList sub = array.subList(1000, 2000);
    checkList(sub, ap(1000, 1, 1000));
    IntList subsub = sub.subList(200, 300);
    checkList(subsub, ap(1200, 1, 100));
  }

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

  public void testCopyInsertList() {
    array.addAll(IntProgression.arithmetic(0, 10240, 1));
    IntArray list = new IntArray();
    list.addAll(array);
    array.insertAll(2000, list, 100, 10000);
    checkList(array, ap(0, 1, 2000), ap(100, 1, 10000), ap(2000, 1, 8240));
    array.setAll(5000, list);
    checkList(array, ap(0, 1, 2000), ap(100, 1, 3000), ap(0, 1, 10240), ap(5240, 1, 5000));
  }

  private void testReverse(int[] a, int[] b) {
    SegmentedIntArray lst = new SegmentedIntArray();
    lst.addAll(a);
    SegmentedIntArray referenceLst = new SegmentedIntArray();
    referenceLst.addAll(b);
    lst.reverseInPlace();
    assertEquals(lst, referenceLst);
  }

  public void testReverse() {
    testReverse(new int[]{}, new int[]{});
    testReverse(new int[]{0}, new int[]{0});
    testReverse(new int[]{1,1,0}, new int[]{0,1,1});
    testReverse(new int[]{0,1,3,6,10,15,21,28,36}, new int[]{36,28,21,15,10,6,3,1,0});
  }
}
