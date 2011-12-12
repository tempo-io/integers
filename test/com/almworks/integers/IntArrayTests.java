package com.almworks.integers;

import com.almworks.util.RandomHolder;
import java.util.*;

public class IntArrayTests extends NativeIntFixture {
  private IntArray array;

  protected void setUp() throws Exception {
    super.setUp();
    array = new IntArray();
  }

  protected void tearDown() throws Exception {
    array.clear();
    array = null;
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
    int x = 10000;
    for (WritableIntListIterator ii : array.write()) {
      assertEquals(x, ii.value());
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
    array.insertMultiple(0, 1, 1024);
    array.insertMultiple(0, 2, 1024);
    checkList(array, ap(2, 0, 1024), ap(1, 0, 1024));
    array.insertMultiple(1024, 3, 1024);
    checkList(array, ap(2, 0, 1024), ap(3, 0, 1024), ap(1, 0, 1024));
    array.insertMultiple(1024, 4, 1024);
    checkList(array, ap(2, 0, 1024), ap(4, 0, 1024), ap(3, 0, 1024), ap(1, 0, 1024));
    array.clear();
    array.insertMultiple(0, 1, 10240);
    checkList(array, ap(1, 0, 10240));
    array.insertMultiple(6000, 2, 1024);
    checkList(array, ap(1, 0, 6000), ap(2, 0, 1024), ap(1, 0, 1024 * 11 - 7024));
    array.insertMultiple(2000, 3, 1024);
    checkList(array, ap(1, 0, 2000), ap(3, 0, 1024), ap(1, 0, 7024 - 3024), ap(2, 0, 1024), ap(1, 0, 1024 * 12 - 8048));
  }

  public void testRemoves() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    array.removeRange(0, 1024);
    array.removeRange(10000 - 2048, 10000 - 1024);
    array.removeRange(0, 10);
    checkList(array, ap(1034, 1, 7942));
    array.removeAt(5000);
    checkList(array, ap(1034, 1, 5000), ap(6035, 1, 2941));
  }

  public void testIteratorRemoveRange() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    WritableIntListIterator ii = array.iterator(100, 600);
    for (int i = 0; i < 10; i++)
      ii.nextValue();
    ii.removeRange(-9, 1);
    try {
      ii.removeRange(-9, 1);
      fail();
    } catch (IllegalStateException e) {
      // ok
    }
    ii.next();
    ii.move(19);
    ii.removeRange(-9, 1);
    checkList(array, ap(0, 1, 100), ap(110, 1, 10), ap(130, 1, 9870));
    ii.next();
    ii.removeRange(-10, 0);
    checkList(array, ap(0, 1, 100), ap(130, 1, 9870));
  }

  public void testIteratorRemoveFromEnd() {
    for (int i = 0; i < 10000; i++)
      array.add(i);
    WritableIntListIterator ii = array.iterator(8191, 9192);
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
    WritableIntListIterator ii = array.iterator();
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
    checkList(array.subList(10, 20), ap(10, 1, 10));
    checkList(array.subList(10, 10000), ap(10, 1, 9990));
    checkList(array.subList(9990, 10000), ap(9990, 1, 10));
    checkList(array.subList(9990, 9990));
    assertEquals(array, array.subList(0, 10000));
    assertTrue(array == array.subList(0, 10000));
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
    array.addAll(array);
    checkList(array, ap(0, 1, 10240), ap(0, 1, 10240));

    array.setAll(100, array, 100, 100);
    checkList(array, ap(0, 1, 10240), ap(0, 1, 10240));
    array.setAll(100, array.subList(200, 300));
    checkList(array, ap(0, 1, 100), ap(200, 1, 100), ap(200, 1, 10040), ap(0, 1, 10240));

    array.insertAll(5000, array.subList(3000, 5000));
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

  public void testRetain() {
    array.addAll(1, 5, 2, 4, 3);
    array.retain(IntArray.create(3, 1, 2));
    CHECK.order(array.iterator(), 1, 2, 3);
    array.retain(IntArray.create(2));
    CHECK.order(array.iterator(), 2);
  }

  public void testRemoveAll() {
    array.addAll(2, 0, 2, 1, 2, 2, 2, 2, 3, 2);
    CHECK.order(array.iterator(), 2, 0, 2, 1, 2, 2, 2, 2, 3, 2);
    array.removeAll();
    array.removeAll(2);
    CHECK.order(array.iterator(), 0, 1, 3);
    array.removeAll(2);
    CHECK.order(array.iterator(), 0, 1, 3);
    array.removeAll(0, 3);
    CHECK.order(array.iterator(), 1);
    array.removeAll(1);
    CHECK.empty(array);
  }

  public void testFromCollection() {
    List<Integer> l = new ArrayList<Integer>();
    l.add(2);
    l.add(3);
    l.add(9);
    IntArray a = IntArray.create(l);
    CHECK.order(a.toNativeArray(), 2, 3, 9);
    IntList il = IntCollections.asIntList(l);
    CHECK.order(il.toNativeArray(), 2, 3, 9);
  }

  private void testReverse(int[] a, int[] b) {
    IntArray lst = new IntArray();
    lst.addAll(a);
    IntArray referenceLst = new IntArray();
    referenceLst.addAll(b);
    lst.reverse();
    assertEquals(lst, referenceLst);
  }

  public void testReverse() {
    testReverse(new int[]{}, new int[]{});
    testReverse(new int[]{0}, new int[]{0});
    testReverse(new int[]{1,1,0}, new int[]{0,1,1});
    testReverse(new int[]{0,1,3,6,10,15,21,28,36}, new int[]{36,28,21,15,10,6,3,1,0});

    IntArray lst = new IntArray();
    Random r = new RandomHolder().getRandom();
    for (int i = 0; i < 20; i++) {
      lst.add(r.nextInt(200));
    }
    lst.sortUnique();
    lst.reverse();
    for (int i = 1; i < lst.size(); i++) {
      if (lst.get(i-1)<=lst.get(i)) {
        fail();
      }
    }
  }
}