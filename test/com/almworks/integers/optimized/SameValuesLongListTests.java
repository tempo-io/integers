package com.almworks.integers.optimized;

import com.almworks.integers.*;

import java.util.ArrayList;
import java.util.List;

public class SameValuesLongListTests extends LongListChecker {

  @Override
  protected List<LongList> createLongListVariants(long... values) {
    List<LongList> res = new ArrayList<LongList>();
    SameValuesLongList array = new SameValuesLongList();
    array.addAll(values);
    res.add(array);
    return res;
  }

  private SameValuesLongList list;

  public void setUp() throws Exception {
    super.setUp();
    list = create();
  }

  protected void tearDown() throws Exception {
    list = null;
    super.tearDown();
  }

  public void testAdditions() {
    checkCollection(list);
    list.add(0);
    checkCollection(list, 0);
    list.add(0);
    checkCollection(list, 0, 0);
    list.clear();
    for (int i = 0; i < 100; i++) {
      list.add(i);
      checkCollection(list, ap(0, 1, i + 1));
    }
    list.addAll(list);
    checkCollection(list.subList(0, 100), ap(0, 1, 100));
    checkCollection(list.subList(100, 200), ap(0, 1, 100));
  }

  public void testRemove1() {
    list.addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    checkCollection(list, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    list.remove(5);
    checkCollection(list, 1, 2, 3, 4, 6, 7, 8, 9, 10);
    list.removeRange(6, 8);
    checkCollection(list, 1, 2, 3, 4, 6, 7, 10);
    list.removeRange(5, 7);
    checkCollection(list, 1, 2, 3, 4, 6);
    list.removeRange(0, 3);
    checkCollection(list, 4, 6);
    list.removeRange(0, 2);
    checkCollection(list);
  }

  public void testRemove2() {
    list.addAll(ap(0, 0, 10));
    list.addAll(ap(1, 0, 10));
    list.addAll(ap(2, 0, 10));
    list.addAll(ap(3, 0, 10));
    list.addAll(ap(2, 0, 10));
    list.addAll(ap(1, 0, 10));
    list.addAll(ap(0, 0, 10));
    assertEquals(6, list.getChangeCount());
    checkCollectionM(list, ap(0, 0, 10), ap(1, 0, 10), ap(2, 0, 10), ap(3, 0, 10), ap(2, 0, 10), ap(1, 0, 10), ap(0, 0, 10));
    list.removeRange(0, 5);
    checkCollectionM(list, ap(0, 0, 5), ap(1, 0, 10), ap(2, 0, 10), ap(3, 0, 10), ap(2, 0, 10), ap(1, 0, 10), ap(0, 0, 10));
    list.removeRange(60, 65);
    checkCollectionM(list, ap(0, 0, 5), ap(1, 0, 10), ap(2, 0, 10), ap(3, 0, 10), ap(2, 0, 10), ap(1, 0, 10), ap(0, 0, 5));
    list.removeAt(10);
    checkCollectionM(list, ap(0, 0, 5), ap(1, 0, 9), ap(2, 0, 10), ap(3, 0, 10), ap(2, 0, 10), ap(1, 0, 10), ap(0, 0, 5));
    list.removeRange(29, 40);
    checkCollectionM(list, ap(0, 0, 5), ap(1, 0, 9), ap(2, 0, 10), ap(3, 0, 5), ap(2, 0, 4), ap(1, 0, 10), ap(0, 0, 5));
    list.removeRange(25, 43);
    checkCollectionM(list, ap(0, 0, 5), ap(1, 0, 9), ap(2, 0, 10), ap(3, 0, 1), ap(0, 0, 5));
    list.removeRange(5, 23);
    checkCollectionM(list, ap(0, 0, 5), ap(2, 0, 1), ap(3, 0, 1), ap(0, 0, 5));
    list.removeRange(4, 8);
    checkCollectionM(list, ap(0, 0, 8));
    list.removeRange(0, 8);
    checkCollection(list);
  }

  public void testRemoveCollapse() {
    list.addAll(ap(1, 0, 10));
    list.addAll(ap(2, 0, 10));
    list.addAll(ap(1, 0, 10));
    list.removeRange(5, 25);
    checkCollection(list, ap(1, 0, 10));
    assertEquals(1, list.getChangeCount());
  }

  public void testSet() {
    list.addAll(ap(0, 0, 10));
    checkCollection(list, ap(0, 0, 10));
    for (int i = 0; i < 10; i++) {
      list.set(i, 100 + i);
      checkCollectionM(list, ap(100, 1, i + 1), ap(0, 0, 9 -i));
    }
    list.clear();
    list.addAll(ap(0, 0, 10));
    for (int i = 0; i < 10; i++) {
      list.set(i, 100);
      checkCollectionM(list, ap(100, 0, i + 1), ap(0, 0, 9 -i));
      assertEquals(i == 9 ? 1 : 2, list.getChangeCount());
    }
    list.clear();
    list.addAll(ap(1, 0, 10));
    list.addAll(ap(2, 0, 10));
    list.addAll(ap(1, 0, 10));
    list.setRange(5, 25, 1);
    checkCollection(list, ap(1, 0, 30));
    assertEquals(1, list.getChangeCount());

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
    assertEquals(1, list.getChangeCount());
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
      LongArray array = generateRandomLongArray(1000, false, 500);
      array.sort();
      list.addAll(array);
      int index = RAND.nextInt(list.size());
      int len = list.size() - index;
      LongList insertArray;
      if (len != 0) {
        insertArray = generateRandomLongArray(len, false, len / 2);
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

  public void testSetBySort() {
    for (int i = 0; i < 100; i++) {
      list.add(0);
      list.add(1);
    }
    list.sort();
    checkCollection(list.subList(0, 100), ap(0, 0, 100));
    checkCollection(list.subList(100, 200), ap(1, 0, 100));
    assertEquals(1, list.getChangeCount());
  }

  public void testInsert() {
    list.insertMultiple(0, 0, 10);
    checkCollection(list, ap(0, 0, 10));
    list.insertMultiple(0, 0, 10);
    checkCollection(list, ap(0, 0, 20));
    list.insertMultiple(0, 1, 10);
    checkCollectionM(list, ap(1, 0, 10), ap(0, 0, 20));
    list.insertMultiple(5, 2, 4);
    checkCollectionM(list, ap(1, 0, 5), ap(2, 0, 4), ap(1, 0, 5), ap(0, 0, 20));
    list.insertMultiple(34, 0, 4);
    checkCollectionM(list, ap(1, 0, 5), ap(2, 0, 4), ap(1, 0, 5), ap(0, 0, 24));
    list.insertMultiple(38, 100, 4);
    checkCollectionM(list, ap(1, 0, 5), ap(2, 0, 4), ap(1, 0, 5), ap(0, 0, 24), ap(100, 0, 4));
    list.insertMultiple(5, 1, 1);
    checkCollectionM(list, ap(1, 0, 6), ap(2, 0, 4), ap(1, 0, 5), ap(0, 0, 24), ap(100, 0, 4));
    list.insertMultiple(6, 2, 1);
    checkCollectionM(list, ap(1, 0, 6), ap(2, 0, 5), ap(1, 0, 5), ap(0, 0, 24), ap(100, 0, 4));
    list.insertMultiple(6, 3, 1);
    checkCollectionM(list, ap(1, 0, 6), ap(3, 0, 1), ap(2, 0, 5), ap(1, 0, 5), ap(0, 0, 24), ap(100, 0, 4));
  }

  public void testInsertAll() {
    list.addAll(0);
    list.insertAll(1, LongArray.create(2, 4, 6).iterator());
    CHECK.order(LongArray.create(0,2,4,6), list);

    list.insertAll(2, LongArray.create(-5, -10, -15));
    CHECK.order(LongArray.create(0, 2, -5, -10, -15, 4, 6), list);

    list.insertAll(0, LongArray.create(0, 99, 99, 0), 1, 2);
    CHECK.order(LongArray.create(99, 99, 0, 2, -5, -10, -15, 4, 6), list);
  }

  public void testGetNextDifferentValueIndex() {
    list.insertMultiple(0, 1, 10);
    list.insert(10, 2);
    list.insertMultiple(11, 1, 5);
    list.insertMultiple(16, 3, 3);
    assertEquals("for index 0", 10, list.getNextDifferentValueIndex(0));
    assertEquals("for index 5", 10, list.getNextDifferentValueIndex(5));
    assertEquals("for index 9", 10, list.getNextDifferentValueIndex(9));
    assertEquals("for index 10", 11, list.getNextDifferentValueIndex(10));
    assertEquals("for index 11", 16, list.getNextDifferentValueIndex(11));
    assertEquals("for index 16", list.size(), list.getNextDifferentValueIndex(16));
    assertEquals("for index 18", list.size(), list.getNextDifferentValueIndex(18));
    // check for OOBE
    boolean caught = false;
    try {
      list.getNextDifferentValueIndex(-1);
    } catch(IndexOutOfBoundsException ex) {
      caught = true;
    }
    assertTrue("caught OOBE for index - 1", caught);
    try {
      list.getNextDifferentValueIndex(list.size());
    } catch(IndexOutOfBoundsException ex) {
      caught = true;
    }
    assertTrue("caught OOBE for index = size", caught);
  }

  private SameValuesLongList create() {
    return new SameValuesLongList();
  }

  private void testReverse(long[] a, long[] b) {
    SameValuesLongList lst = new SameValuesLongList ();
    lst.addAll(a);
    SameValuesLongList referenceLst = new SameValuesLongList ();
    referenceLst.addAll(b);
    lst.reverse();
    assertEquals(lst, referenceLst);
  }

  public void testReverse() {
    testReverse(new long[]{0,1,3,6,10,15,21,28,36}, new long[]{36,28,21,15,10,6,3,1,0});
    testReverse(new long[]{2, 4, 4, 5, 5, 5, 7, 7, 7, 7}, new long[]{7, 7, 7, 7, 5, 5, 5, 4, 4, 2});
    testReverse(new long[]{0,0,0,1,1}, new long[]{1,1,0,0,0});
    testReverse(new long[]{1,1,0}, new long[]{0,1,1});
    testReverse(new long[]{0,0,0,0,1,1,0}, new long[]{0,1,1,0,0,0,0});
    testReverse(new long[]{0,0,1,2,2,2,3,3,3,3}, new long[]{3,3,3,3,2,2,2,1,0,0});
    testReverse(new long[]{4,4,1,2,2,2,3,3,3,3}, new long[]{3,3,3,3,2,2,2,1,4,4});
    testReverse(new long[]{0,0,1,2,2,2,0,0,0,0}, new long[]{0,0,0,0,2,2,2,1,0,0});
    testReverse(new long[]{4,4,1,2,2,2,0,0,0,0}, new long[]{0,0,0,0,2,2,2,1,4,4});
    testReverse(new long[]{}, new long[]{});
    testReverse(new long[]{0}, new long[]{0});
    testReverse(new long[]{0, 0, 0}, new long[]{0, 0, 0});
    testReverse(new long[]{1, 1, 1}, new long[]{1, 1, 1});
    testReverse(new long[]{2, 2, 3, 3, 3}, new long[]{3, 3, 3, 2, 2});
  }

  public void testIteratorSpecification() {
    LongIteratorSpecificationChecker.check(new LongIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public LongIterator get(long... values) {
        SameValuesLongList list = new SameValuesLongList();
        list.addAll(values);
        return list.iterator();
      }
    });
  }

  public void testIteratorRemove() {
    list.addAll(1, 1, 1, 2, 2, 3, 3, 3);
    WritableLongListIterator i = list.iterator();
    i.next().next().next().next();
    assertEquals(2, i.value());
    i.remove();
    assertEquals(2, i.nextValue());
  }

  public void testIteratorWrite() {
    SameValuesLongList result, expected, source;

    source = new SameValuesLongList();
    source.addAll(1, 1, 1, 2, 2, 3, 3, 3, 3);

    expected = new SameValuesLongList();
    expected.addAll(1, 1, 1, 2, 2, 3, 3, 3, 3);

    result = new SameValuesLongList();
    for (WritableLongListIterator it : source.write()) {
      result.add(it.value());
      it.set(0, -1);
    }
    CHECK.order(source, LongCollections.repeat(-1, source.size()));
    assertEquals(expected, result);
  }

  public void testExpandSimpleCase() {
    int[] elements = {5, 10, 4, 2, 1};
    int[] counts = {1, 2, 1, 1, 1};
    for ( int i = 0; i < 5; i++) {
      for ( int j = 0; j < counts[i]; j++) {
        list.add(elements[i]);
      }
    }
    LongArray expected = LongArray.create(5, 10, 10, 4, 2, 1);
    CHECK.order(list.iterator(), expected.iterator());

    for (int i = 0; i < 3; i++) {
      expected.insert(3, 4);
    }
    list.expand(3, 3);
    CHECK.order(list.iterator(), expected.iterator());

    for (int i = 0; i < 2; i++) {
      expected.insert(3, 4);
    }
    list.expand(6, 2);
    CHECK.order(list.iterator(), expected.iterator());

    boolean caught = false;
    try {
      list.expand(list.size() + 1, 5);
    } catch (IndexOutOfBoundsException ex) {
      caught = true;
    }
    assertTrue(caught);

    caught = false;
    try {
      list.expand(-1, 3);
    } catch (IndexOutOfBoundsException ex) {
      caught = true;
    }
    assertTrue(caught);

    list.expand(list.size(), 5);
    long val = expected.getLast(0);
    for (long i = 0; i < 5; i++) {
      expected.add(val);
    }
    CHECK.order(list.iterator(), expected.iterator());
  }

  public void testExpandComplexCase() {
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

  public void testSortUnique() {
    for (int size = 64; size < 1024; size *= 2) {
      LongArray expected = IntegersFixture.generateRandomLongArray(size, false, size);
      list.clear();
      expected.sort();
      list.addAll(expected);
      expected.sortUnique();
      list.sortUnique();
      CHECK.order(list.iterator(), expected.iterator());
    }
  }

  // todo update with 0 in counts
  public void testCreate() {
    for (int i = 0; i < 10; i++) {
      LongArray values = generateRandomLongArray(100, false);
      IntArray counts = generateRandomIntArray(100, false, 1, 4);
      LongArray expected = new LongArray(values.size() * 3);
      for (int j = 0; j < values.size(); j++) {
        for (int k = 0; k < counts.get(j); k++) {
          expected.add(values.get(j));
        }
      }

      SameValuesLongList actual = SameValuesLongList.create(values, counts);
      CHECK.order(expected, actual);
    }

    LongArray expected = generateRandomLongArray(100, false);
    CHECK.order(expected, SameValuesLongList.create(expected));
  }
}
