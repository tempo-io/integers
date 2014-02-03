package com.almworks.integers.optimized;

import com.almworks.integers.*;

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
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

    res.add(SameValuesLongList.create(new LongArray(values)));
    return res;
  }

  private SameValuesLongList list;

  public void setUp() throws Exception {
    super.setUp();
    list = new SameValuesLongList();
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
    assertEquals(0, list.getChangeCount());
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
      assertEquals(i == 9 ? 0 : 1, list.getChangeCount());
    }
    list.clear();
    list.addAll(ap(1, 0, 10));
    list.addAll(ap(2, 0, 10));
    list.addAll(ap(1, 0, 10));
    list.setRange(5, 25, 1);
    checkCollection(list, ap(1, 0, 30));
    assertEquals(0, list.getChangeCount());

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
    assertEquals(0, list.getChangeCount());
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
      LongArray array = generateRandomLongArray(1000, UNORDERED, 500);
      array.sort();
      list.addAll(array);
      int index = RAND.nextInt(list.size());
      int len = list.size() - index;
      LongList insertArray;
      if (len > 1) {
        insertArray = generateRandomLongArray(len, UNORDERED, len / 2);
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

  public void testSetBySort0() {
    int s = 5;
    list.addAll(LongCollections.repeat(0, s));
    list.addAll(LongCollections.repeat(1, s));
//    list.swap(0, 1);
    list.setRange(0, 1, 0);
    assertEquals(1, list.getChangeCount());
    checkCollection(list.subList(0, s), ap(0, 0, s));
    list.setRange(1, 2, 0);
    assertEquals(1, list.getChangeCount());
    checkCollection(list.subList(0, s), ap(0, 0, s));
    list.swap(0, 1);
    checkCollection(list.subList(0, s), ap(0, 0, s));
    checkCollection(list.subList(s, s * 2), ap(1, 0, s));
    assertEquals(1, list.getChangeCount());
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

  public void testExpandSimple2() {
    list.expand(0, 10);
    CHECK.order(LongCollections.repeat(0, 10), list);
    list.insert(0, 99);
    list.clear();
    list.expand(0, 3);
    CHECK.order(LongCollections.repeat(0, 3), list);
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

    actual = SameValuesLongList.create(LongArray.create(9, 9, 2, 4, 6), IntArray.create(0, 1, 0, 3, 1));
    expected = LongArray.create(9, 4, 4, 4, 6);
    CHECK.order(expected, actual);

    int attemptsCount = 12;
    for (int i = 0; i < attemptsCount; i++) {
      values = generateRandomLongArray(100, UNORDERED);
      counts = generateRandomIntArray(100, UNORDERED, 4);
      if (i == attemptsCount - 1) {
        values.add(RAND.nextLong());
        counts.add(0);
      }
      expected = new LongArray(values.size() * 3);
      for (int j = 0; j < values.size(); j++) {
        expected.addAll(LongCollections.repeat(values.get(j), counts.get(j)));
      }
      actual = SameValuesLongList.create(values, counts);
      CHECK.order(expected, actual);
    }

    for (int i = 0; i < attemptsCount; i++) {
      values = generateRandomLongArray(100, SORTED, 150);
      counts = new IntArray(IntCollections.repeat(1, 100));
      actual = SameValuesLongList.create(values, counts);
      CHECK.order(values, actual);
    }

    expected = generateRandomLongArray(100, UNORDERED);
    CHECK.order(expected, SameValuesLongList.create(expected));
  }

  public void testCreateException() {
    try {
      list = SameValuesLongList.create(LongArray.create(1, 2, 3), IntArray.create(1, 2));
      System.out.println(list);
      fail();
    } catch (IllegalArgumentException _) {
    }
  }

  public void testRemoveFromBeginning() {
    LongArray expected = LongArray.create(-1, 0, 1, 2);
    list = SameValuesLongList.create(expected);
    list.removeAt(0);
    expected.removeAt(0);
    CHECK.order(expected, list);
  }

  public void testCreateSimple() {
    int count = 4;
    LongArray values = LongArray.create(0, 2, 4);
    LongArray expected = new LongArray(values.size() * count);
    for (int i = 0; i < values.size(); i++) {
      expected.addAll(LongCollections.repeat(values.get(i), count));
    }
    list = SameValuesLongList.create(values, IntCollections.repeat(4, values.size()));
    CHECK.order(expected, list);

    expected = LongArray.create(1, 1, 2, 2, 3, 3, 3, 3, 3, 4);
    list = SameValuesLongList.create(expected);
    CHECK.order(expected, list);
  }

  public void testSetRangeSimple2() {
    int count = 4;
    LongArray values = LongArray.create(0, 2, 4);
    LongArray expected = new LongArray(values.size() * count);
    for (int i = 0; i < values.size(); i++) {
      expected.addAll(LongCollections.repeat(values.get(i), count));
    }
    list.addAll(expected);
    CHECK.order(expected, list);


    int[][] valuesForSet = {{0, 1, 1}, {1, 2, 1}, {4, 9, -1}};
    for (int[] vals : valuesForSet) {
      list.setRange(vals[0], vals[1], vals[2]);
      expected.setRange(vals[0], vals[1], vals[2]);
      CHECK.order(expected, list);
    }
  }
}