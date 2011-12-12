package com.almworks.integers.optimized;

import com.almworks.integers.NativeIntFixture;
import com.almworks.integers.WritableIntListIterator;

public class SameValuesIntListTest extends NativeIntFixture {
  private SameValuesIntList array;

  protected void setUp() throws Exception {
    super.setUp();
    array = create();
  }

  protected void tearDown() throws Exception {
    array = null;
    super.tearDown();
  }

  public void testAdditions() {
    checkCollection(array);
    array.add(0);
    checkCollection(array, 0);
    array.add(0);
    checkCollection(array, 0, 0);
    array.clear();
    for (int i = 0; i < 100; i++) {
      array.add(i);
      checkCollection(array, ap(0, 1, i + 1));
    }
    array.addAll(array);
    checkCollection(array.subList(0, 100), ap(0, 1, 100));
    checkCollection(array.subList(100, 200), ap(0, 1, 100));
  }

  public void testRemove1() {
    array.addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    checkCollection(array, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    array.remove(5);
    checkCollection(array, 1, 2, 3, 4, 6, 7, 8, 9, 10);
    array.removeRange(6, 8);
    checkCollection(array, 1, 2, 3, 4, 6, 7, 10);
    array.removeRange(5, 7);
    checkCollection(array, 1, 2, 3, 4, 6);
    array.removeRange(0, 3);
    checkCollection(array, 4, 6);
    array.removeRange(0, 2);
    checkCollection(array);
  }

  public void testRemove2() {
    array.addAll(ap(0, 0, 10));
    array.addAll(ap(1, 0, 10));
    array.addAll(ap(2, 0, 10));
    array.addAll(ap(3, 0, 10));
    array.addAll(ap(2, 0, 10));
    array.addAll(ap(1, 0, 10));
    array.addAll(ap(0, 0, 10));
    assertEquals(6, array.getChangeCount());
    checkCollectionM(array, ap(0, 0, 10), ap(1, 0, 10), ap(2, 0, 10), ap(3, 0, 10), ap(2, 0, 10), ap(1, 0, 10), ap(0, 0, 10));
    array.removeRange(0, 5);
    checkCollectionM(array, ap(0, 0, 5), ap(1, 0, 10), ap(2, 0, 10), ap(3, 0, 10), ap(2, 0, 10), ap(1, 0, 10), ap(0, 0, 10));
    array.removeRange(60, 65);
    checkCollectionM(array, ap(0, 0, 5), ap(1, 0, 10), ap(2, 0, 10), ap(3, 0, 10), ap(2, 0, 10), ap(1, 0, 10), ap(0, 0, 5));
    array.removeAt(10);
    checkCollectionM(array, ap(0, 0, 5), ap(1, 0, 9), ap(2, 0, 10), ap(3, 0, 10), ap(2, 0, 10), ap(1, 0, 10), ap(0, 0, 5));
    array.removeRange(29, 40);
    checkCollectionM(array, ap(0, 0, 5), ap(1, 0, 9), ap(2, 0, 10), ap(3, 0, 5), ap(2, 0, 4), ap(1, 0, 10), ap(0, 0, 5));
    array.removeRange(25, 43);
    checkCollectionM(array, ap(0, 0, 5), ap(1, 0, 9), ap(2, 0, 10), ap(3, 0, 1), ap(0, 0, 5));
    array.removeRange(5, 23);
    checkCollectionM(array, ap(0, 0, 5), ap(2, 0, 1), ap(3, 0, 1), ap(0, 0, 5));
    array.removeRange(4, 8);
    checkCollectionM(array, ap(0, 0, 8));
    array.removeRange(0, 8);
    checkCollection(array);
  }

  public void testRemoveCollapse() {
    array.addAll(ap(1, 0, 10));
    array.addAll(ap(2, 0, 10));
    array.addAll(ap(1, 0, 10));
    array.removeRange(5, 25);
    checkCollection(array, ap(1, 0, 10));
    assertEquals(1, array.getChangeCount());
  }

  public void testSet() {
    array.addAll(ap(0, 0, 10));
    checkCollection(array, ap(0, 0, 10));
    for (int i = 0; i < 10; i++) {
      array.set(i, 100 + i);
      checkCollectionM(array, ap(100, 1, i + 1), ap(0, 0, 9 -i));
    }
    array.clear();
    array.addAll(ap(0, 0, 10));
    for (int i = 0; i < 10; i++) {
      array.set(i, 100);
      checkCollectionM(array, ap(100, 0, i + 1), ap(0, 0, 9 -i));
      assertEquals(i == 9 ? 1 : 2, array.getChangeCount());
    }
    array.clear();
    array.addAll(ap(1, 0, 10));
    array.addAll(ap(2, 0, 10));
    array.addAll(ap(1, 0, 10));
    array.setRange(5, 25, 1);
    checkCollection(array, ap(1, 0, 30));
    assertEquals(1, array.getChangeCount());

    array.clear();
    array.add(0);
    checkCollection(array, 0);
    array.set(0, 0);
    array.set(0, 1);
    checkCollection(array, 1);
    array.set(0, 2);
    checkCollection(array, 2);
    array.set(0, 0);
    checkCollection(array, 0);
    array.add(1);
    checkCollection(array, 0, 1);
    array.set(1, 1);
    array.set(1, 2);
    checkCollection(array, 0, 2);
    array.set(1, 3);
    checkCollection(array, 0, 3);
    array.set(1, 0);
    checkCollection(array, 0, 0);
    assertEquals(0, array.getChangeCount());
    array.set(1, 1);
    checkCollection(array, 0, 1);
    array.set(0, 1);
    checkCollection(array, 1, 1);
    assertEquals(1, array.getChangeCount());
    array.set(0, 2);
    checkCollection(array, 2, 1);
    array.set(0, 0);
    checkCollection(array, 0, 1);
    array.setRange(0, 2, 0);
    checkCollection(array, 0, 0);
    array.setRange(0, 2, 1);
    checkCollection(array, 1, 1);
    array.add(2);
    checkCollection(array, 1, 1, 2);
    array.setRange(1, 3, 2);
    checkCollection(array, 1, 2, 2);
  }

  public void testSetBySort() {
    for (int i = 0; i < 100; i++) {
      array.add(0);
      array.add(1);
    }
    array.sort();
    checkCollection(array.subList(0, 100), ap(0, 0, 100));
    checkCollection(array.subList(100, 200), ap(1, 0, 100));
    assertEquals(1, array.getChangeCount());
  }

  public void testInsert() {
    array.insertMultiple(0, 0, 10);
    checkCollection(array, ap(0, 0, 10));
    array.insertMultiple(0, 0, 10);
    checkCollection(array, ap(0, 0, 20));
    array.insertMultiple(0, 1, 10);
    checkCollectionM(array, ap(1, 0, 10), ap(0, 0, 20));
    array.insertMultiple(5, 2, 4);
    checkCollectionM(array, ap(1, 0, 5), ap(2, 0, 4), ap(1, 0, 5), ap(0, 0, 20));
    array.insertMultiple(34, 0, 4);
    checkCollectionM(array, ap(1, 0, 5), ap(2, 0, 4), ap(1, 0, 5), ap(0, 0, 24));
    array.insertMultiple(38, 100, 4);
    checkCollectionM(array, ap(1, 0, 5), ap(2, 0, 4), ap(1, 0, 5), ap(0, 0, 24), ap(100, 0, 4));
    array.insertMultiple(5, 1, 1);
    checkCollectionM(array, ap(1, 0, 6), ap(2, 0, 4), ap(1, 0, 5), ap(0, 0, 24), ap(100, 0, 4));
    array.insertMultiple(6, 2, 1);
    checkCollectionM(array, ap(1, 0, 6), ap(2, 0, 5), ap(1, 0, 5), ap(0, 0, 24), ap(100, 0, 4));
    array.insertMultiple(6, 3, 1);
    checkCollectionM(array, ap(1, 0, 6), ap(3, 0, 1), ap(2, 0, 5), ap(1, 0, 5), ap(0, 0, 24), ap(100, 0, 4));
  }

  public void testGetNextDifferentValueIndex() {
    array.insertMultiple(0, 1, 10);
    array.insert(10, 2);
    array.insertMultiple(11, 1, 5);
    array.insertMultiple(16, 3, 3);
    assertEquals("for index 0", 10, array.getNextDifferentValueIndex(0));
    assertEquals("for index 5", 10, array.getNextDifferentValueIndex(5));
    assertEquals("for index 9", 10, array.getNextDifferentValueIndex(9));
    assertEquals("for index 10", 11, array.getNextDifferentValueIndex(10));
    assertEquals("for index 11", 16, array.getNextDifferentValueIndex(11));
    assertEquals("for index 16", -1, array.getNextDifferentValueIndex(16));
    assertEquals("for index 18", -1, array.getNextDifferentValueIndex(18));
    // check for OOBE
    boolean caught = false;
    try {
      array.getNextDifferentValueIndex(-1);
    } catch(IndexOutOfBoundsException ex) {
      caught = true;
    }
    assertTrue("caught OOBE for index - 1", caught);
    try {
      array.getNextDifferentValueIndex(array.size());
    } catch(IndexOutOfBoundsException ex) {
      caught = true;
    }
    assertTrue("caught OOBE for index = size", caught);
  }

  private SameValuesIntList create() {
    return new SameValuesIntList();
  }

  private void testReverse(int[] a, int[] b) {
    SameValuesIntList lst = new SameValuesIntList();
    lst.addAll(a);
    SameValuesIntList referenceLst = new SameValuesIntList();
    referenceLst.addAll(b);
    lst.reverse();
    assertEquals(lst, referenceLst);
  }

  public void testReverse() {
    testReverse(new int[]{0,1,3,6,10,15,21,28,36}, new int[]{36,28,21,15,10,6,3,1,0});
    testReverse(new int[]{2, 4, 4, 5, 5, 5, 7, 7, 7, 7}, new int[]{7, 7, 7, 7, 5, 5, 5, 4, 4, 2});
    testReverse(new int[]{0,0,0,1,1}, new int[]{1,1,0,0,0});
    testReverse(new int[]{1,1,0}, new int[]{0,1,1});
    testReverse(new int[]{0,0,0,0,1,1,0}, new int[]{0,1,1,0,0,0,0});
    testReverse(new int[]{0,0,1,2,2,2,3,3,3,3}, new int[]{3,3,3,3,2,2,2,1,0,0});
    testReverse(new int[]{4,4,1,2,2,2,3,3,3,3}, new int[]{3,3,3,3,2,2,2,1,4,4});
    testReverse(new int[]{0,0,1,2,2,2,0,0,0,0}, new int[]{0,0,0,0,2,2,2,1,0,0});
    testReverse(new int[]{4,4,1,2,2,2,0,0,0,0}, new int[]{0,0,0,0,2,2,2,1,4,4});
    testReverse(new int[]{}, new int[]{});
    testReverse(new int[]{0}, new int[]{0});
    testReverse(new int[]{0, 0, 0}, new int[]{0, 0, 0});
    testReverse(new int[]{1, 1, 1}, new int[]{1, 1, 1});
    testReverse(new int[]{2, 2, 3, 3, 3}, new int[]{3, 3, 3, 2, 2});
  }

  public void testIterator() {
    array.addAll(1, 1, 1, 2, 2, 3, 3, 3);
    WritableIntListIterator i = array.iterator();
    i.next().next().next().next();
    assertEquals(2, i.value());
    i.remove();
    assertEquals(2, i.nextValue());
  }
}
