package com.almworks.integers;

import junit.framework.TestCase;

public class ParallelIntListTests extends TestCase {
  private static final IntCollectionsCompare CHECK = new IntCollectionsCompare();
  private final IntArray myStorage = new IntArray();
  private final ParallelIntList myList = new ParallelIntList(myStorage, 2);

  public void testInsert() {
    myList.insert(0, 4, 5);
    checkStorage(4, 5);
    assertEquals(4, myList.get(0, 0));
    assertEquals(5, myList.get(0, 1));

    myList.insert(0, 0, 1);
    checkStorage(0, 1, 4, 5);
    assertEquals(0, myList.get(0, 0));
    assertEquals(1, myList.get(0, 1));
    assertEquals(4, myList.get(1, 0));
    assertEquals(5, myList.get(1, 1));

    myList.insert(1, 2, 3);
    checkStorage(0, 1, 2, 3, 4, 5);

    myList.insert(3, 6, 7);
    checkStorage(0, 1, 2, 3, 4, 5, 6, 7);
  }
  
  public void testIteratorNextSet() {
    myList.insert(0, 0, 1);
    myList.insert(1, 2, 3);
    checkStorage(0, 1, 2, 3);

    ParallelIntList.Iterator it = myList.listIterator(0);
    int[] vals = new int[2];
    it.next(vals);
    CHECK.order(vals, 0, 1);
    vals[0] = 10;
    vals[1] = 11;
    it.set(0, vals);
    checkStorage(10, 11, 2, 3);
    it.set(0, 0, 0);
    it.set(0, 1, 1);
    checkStorage(0, 1, 2, 3);

    it.next(vals);
    CHECK.order(vals, 2, 3);
    vals[0] = 12;
    vals[1] = 13;
    it.set(0, vals);
    checkStorage(0, 1, 12, 13);
    it.set(0, 0, 2);
    it.set(0, 1, 3);
    checkStorage(0, 1, 2, 3);

    it = myList.listIterator(1);
    it.next(null);
    it.set(0, 0, 20);
    checkStorage(0, 1, 20, 3);
  }

  public void testIteratorGetRemoveRange() {
    myList.insert(0, -2, -3);
    myList.insert(1, 1, 2);
    myList.insert(2, 10, 20);
    myList.insert(3, 3, 4);
    myList.insert(4, 30, 40);
    checkStorage(-2, -3, 1, 2, 10, 20, 3, 4, 30, 40);

    ParallelIntList.Iterator it = myList.listIterator(1);
    it.next(null);
    int[] vals = new int[2];
    it.get(0, vals);
    CHECK.order(vals, 1, 2);
    it.get(1, vals);
    CHECK.order(vals, 10, 20);
    it.next(null);
    it.get(0, vals);
    CHECK.order(vals, 10, 20);

    it.removeRange(0, 1);
    checkStorage(-2, -3, 1, 2, 3, 4, 30, 40);
    it.get(0, vals);
    CHECK.order(vals, 1, 2);
    it.next(null);
    it.get(0, vals);
    CHECK.order(vals, 3, 4);

    it = myList.listIterator(0);
    it.next(null);
    it.get(0, vals);
    CHECK.order(vals, -2, -3);
    it.next(null);
    it.get(-1, vals);
    CHECK.order(vals, -2, -3);
    it.removeRange(-1, 0);
    checkStorage(1, 2, 3, 4, 30, 40);
    it.get(1, vals);
    CHECK.order(vals, 1, 2);

    it.removeRange(2, 4);
    checkStorage(1, 2);
    it.get(0, vals);
    CHECK.order(vals, 1, 2);
  }

  private void checkStorage(int ... expected) {
    CHECK.order(myStorage.listIterator(), expected);
  }
}
