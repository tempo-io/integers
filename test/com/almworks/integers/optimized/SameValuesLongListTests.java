package com.almworks.integers.optimized;

import com.almworks.integers.*;

import java.util.ArrayList;
import java.util.List;

public class SameValuesLongListTests extends WritableLongListChecker {

  @Override
  protected List<WritableLongList> createWritableLongListVariants(long... values) {
    List<WritableLongList> res = new ArrayList<WritableLongList>();
    SameValuesLongList array = new SameValuesLongList();
    array.addAll(values);
    res.add(array);

    array = new SameValuesLongList();
    array.addAll(LongCollections.repeat(0, 10));
    array.addAll(values);
    array.removeRange(0, 10);
    res.add(array);

    array = new SameValuesLongList();
    array.addAll(values);
    array.addAll(LongCollections.repeat(0, 10));
    array.removeRange(values.length, values.length + 10);
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

  private SameValuesLongList create() {
    return new SameValuesLongList();
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
    SameValuesLongList actual = SameValuesLongList.create(values, counts);
    CHECK.order(expected, actual);

    for (int i = 0; i < 12; i++) {
      values = generateRandomLongArray(100, false);
      counts = generateRandomIntArray(100, false, 4);
      if (i == 11) {
        values.add(RAND.nextLong());
        counts.add(0);
      }
      expected = new LongArray(values.size() * 3);
      for (int j = 0; j < values.size(); j++) {
        for (int k = 0; k < counts.get(j); k++) {
          expected.add(values.get(j));
        }
      }
      actual = SameValuesLongList.create(values, counts);
      CHECK.order(expected, actual);
    }

    expected = generateRandomLongArray(100, false);
//    CHECK.order(expected, SameValuesLongList.create(expected));
  }
}
