package com.almworks.integers;

import com.almworks.integers.func.LongFunction;
import com.almworks.integers.func.LongFunctions;
import com.almworks.integers.optimized.SameValuesLongList;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static com.almworks.integers.LongIterators.range;

/**
 * add {@code -Dcom.almworks.integers.check=true} in VM options to run full set checks
 * */
public abstract class WritableLongListChecker extends LongListChecker {

  @Override
  protected List<? extends LongList> createLongListVariants(long... values) {
    return createWritableLongListVariants(values);
  }

  abstract protected List<WritableLongList> createWritableLongListVariants(long... values);

  protected List<WritableLongList> empty() {
    return createWritableLongListVariants();
  }

  public void testAdd() {
    for (WritableLongList list: empty()) {
      for (int j = 0; j < 2; j++) {

        list.addAll(0, 1, 2);
        CHECK.order(LongArray.create(0, 1, 2), list);

        list.addAll(LongArray.create(3, 4, 5, 6));
        CHECK.order(LongArray.create(0, 1, 2, 3, 4, 5, 6), list);

        list.insert(3, 100);
        CHECK.order(LongArray.create(0, 1, 2, 100, 3, 4, 5, 6), list);

        list.insertMultiple(1, -1, 3);
        CHECK.order(LongArray.create(0, -1, -1, -1, 1, 2, 100, 3, 4, 5, 6), list);

        list.clear();
        assertEquals(0, list.size());
        CHECK.order(list);

        list.add(0);
        CHECK.order(list, 0);

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
        list.clear();
      }
    }
  }

  public void testAddSorted() {
    LongArray expected = LongArray.create(0, 10, 20, 30, 40);
    LongArray values = new LongArray(expected.size() * 3);
    for (int i = 0; i < expected.size(); i++) {
      values.addAll(expected.get(i) - 1, expected.get(i), expected.get(i) + 1);
    }
    for (WritableLongList list:
        createWritableLongListVariants(expected.toNativeArray())) {
      for (int i = 0; i < values.size(); i++) {
        long value = values.get(i);
        switch (i % 3) {
          case 0: {
            int index = i / 3;
            assertTrue(list.addSorted(value));
            expected.insert(index, value);
            CHECK.order(expected, list);
            expected.removeAt(index);
            list.removeAt(index);
            break;
          }
          case 1: {
            assertFalse(list.addSorted(value));
            CHECK.order(expected, list);
            break;
          }
          case 2: {
            assertTrue(list.addSorted(value));
            int index = (i + 1) / 3;
            expected.insert(index, value);
            CHECK.order(expected, list);
            expected.removeAt(index);
            list.removeAt(index);
            break;
          }
        }
      }
    }
  }

  public void testIndexOf() {
    for (WritableLongList list: createWritableLongListVariants(
        new LongArray(LongProgression.arithmetic(99, 100, -1)).extractHostArray())) {
      for (int i = 0; i < 100; i++) {
        assertEquals(99 - i, list.indexOf(i));
      }
    }
  }

  public void testSort() {
    int arrayLength = 200, maxValue = arrayLength * 2;
    for (int attempt = 0; attempt < 10; attempt++) {
      long[] res = generateRandomLongArray( arrayLength, IntegersFixture.SortedStatus.UNORDERED, maxValue).extractHostArray();
      for (WritableLongList list:
          createWritableLongListVariants(res)) {

        list.sort();
        long[] expectedNative = Arrays.copyOf(res, res.length);
        Arrays.sort(expectedNative);
        LongArray expected = new LongArray(expectedNative);
        CHECK.order(expected, list);
      }
    }
  }

  public void testSortUnique() {
    int arrayLength = 200, maxValue = arrayLength * 2;
    for (int attempt = 0; attempt < 10; attempt++) {
      long[] res = generateRandomLongArray( arrayLength, IntegersFixture.SortedStatus.UNORDERED, maxValue).extractHostArray();
      for (WritableLongList list:
          createWritableLongListVariants(res)) {

        list.sortUnique();
        long[] expectedNative = Arrays.copyOf(res, res.length);
        Arrays.sort(expectedNative);
        int newSize = LongCollections.removeSubsequentDuplicates(expectedNative, 0, expectedNative.length);
        LongArray expected = new LongArray(expectedNative, newSize);
        CHECK.order(expected, list);
      }
    }
  }

  public void testRemove() {
    for (WritableLongList list:
        createWritableLongListVariants(ap(1, 1, 10))) {
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
  }

  public void testInsertMultiple() {
    for (WritableLongList list: empty()) {
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
  }

  public void testSet() {
    for (WritableLongList list:
        createWritableLongListVariants(0, 1, 2, -1, 1, 3)) {

      list.set(0, 10);
      list.setAll(3, LongArray.create(5, 31, 36, 100), 1, 2);
      CHECK.order(list, LongArray.create(10, 1, 2, 31, 36, 3));
    }
  }

  public void testSetRange() {
    for (WritableLongList list: createWritableLongListVariants(ap(0, 1, 20))) {
      long[] expected = ap(0, 1, 20);
      list.setRange(0, 5, -1);
      for (int i = 0; i < 5; i++) {
        expected[i] = -1;
      }
      checkList(list, expected);

      list.setRange(5, 10, 4);
      for (int i = 5; i < 10; i++) {
        expected[i] = 4;
      }
      checkList(list, expected);
    }
  }

  public void testSimple() {
    for (WritableLongList list: empty()) {
      for (int i = 0; i < 10000; i++) {
        list.add(i);
      }
      for (int i = 0; i < 10000; i++) {
        assertEquals(i, list.get(i));
      }
    }
  }

  public void testInsertOneByOneInTheBeginning() {
    for (WritableLongList list: empty()) {
      for (int i = 0; i < 10000; i++) {
        list.insert(0, i);
      }
      CHECK.order(new LongProgression.Arithmetic(9999, 10000, -1), list);
    }
  }

  public void testRemoveByIterator() {
    for (WritableLongList list: empty()) {
      int COUNT = 10000;
      list.addAll(range(COUNT, 0, -1));
      int x = 10000;
      for (WritableLongListIterator ii : list.write()) {
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
  }

  public void testBoundary() {
    for (WritableLongList list: empty()) {
      list.apply(0, 0, null);
      list.clear();
      try {
        list.get(0);
        fail();
      } catch (IndexOutOfBoundsException e) { }
      list.remove(0);
      list.removeRange(0, 0);
      assertEquals(list, list.subList(0, 0));
      list.toNativeArray(0, new long[0], 0, 0);
    }
  }

  public void testInserts() {
    for (WritableLongList list: empty()) {
      list.insertMultiple(0, 1, 2048);
      checkList(list, ap(1, 0, 2048));
      list.insert(0, 2);
      list.insert(list.size(), 3);
      checkList(list, new long[] {2}, ap(1, 0, 2048), new long[] {3});
      list.insertMultiple(1, 2, 2000);
      checkList(list, ap(2, 0, 2001), ap(1, 0, 2048), new long[]{3});
      list.clear();

      // test shifts reusing whole segments
      list.insertMultiple(0, 1, 1024);
      list.insertMultiple(0, 2, 1024);
      checkList(list, ap(2, 0, 1024), ap(1, 0, 1024));
      list.insertMultiple(1024, 3, 1024);
      checkList(list, ap(2, 0, 1024), ap(3, 0, 1024), ap(1, 0, 1024));
      list.insertMultiple(1024, 4, 1024);
      checkList(list, ap(2, 0, 1024), ap(4, 0, 1024), ap(3, 0, 1024), ap(1, 0, 1024));
      list.clear();
      list.insertMultiple(0, 1, 10240);
      checkList(list, ap(1, 0, 10240));
      list.insertMultiple(6000, 2, 1024);
      checkList(list, ap(1, 0, 6000), ap(2, 0, 1024), ap(1, 0, 1024 * 11 - 7024));
      list.insertMultiple(2000, 3, 1024);
      checkList(list, ap(1, 0, 2000), ap(3, 0, 1024), ap(1, 0, 7024 - 3024), ap(2, 0, 1024), ap(1, 0, 1024 * 12 - 8048));
    }
  }

  public void testInsertException() {
    for (WritableLongList list: createWritableLongListVariants(ap(0, 1, 10))) {
      try {
        list.insert(-1, 10);
        fail();
      } catch (IndexOutOfBoundsException ex) {}

      try {
        list.insert(15, 10);
        fail();
      } catch (IndexOutOfBoundsException ex) {}
    }
  }

  public void testRemoves() {
    for (WritableLongList list: empty()) {
      for (int i = 0; i < 10000; i++)
        list.add(i);
      list.removeRange(0, 1024);
      list.removeRange(10000 - 2048, 10000 - 1024);
      list.removeRange(0, 10);
      checkList(list, ap(1034, 1, 7942));
      list.removeAt(5000);
      checkList(list, ap(1034, 1, 5000), ap(6035, 1, 2941));
    }
  }

  public void testIteratorRemove() {
    for (WritableLongList list: empty()) {
      LongArray expected = LongArray.create(1, 2, 3, 5);
      list.addAll(1, 2, 3, 4, 5);
      for (WritableLongListIterator it : list.write()) {
        if (it.value() == 4) {
          it.remove();
        }
      }
      CHECK.order(expected, list);
    }
  }

  public void testIteratorRemove2() {
    for (WritableLongList list: createWritableLongListVariants(ap(0, 1, 10))) {
      WritableLongListIterator it = list.iterator();
      it.next().next().next().next();
      assertEquals(3, it.value());
      it.remove();
      assertEquals(4, it.nextValue());
    }
  }

  public void testIteratorRemove3() {
    for (WritableLongList list: createWritableLongListVariants(ap(0, 1, 10))) {
      WritableLongListIterator it = list.iterator();
      it.next().next();
      it.remove();
      assertEquals(2, it.nextValue());

      it.move(7);
      assertEquals(9, it.value());
      it.remove();
      CHECK.order(list, 0, 2, 3, 4, 5, 6, 7, 8);
    }
  }

  public void testIteratorRemoveRange() {
    for (WritableLongList list: empty()) {
      for (int i = 0; i < 10000; i++)
        list.add(i);
      WritableLongListIterator ii = list.iterator(100, 600);
      for (int i = 0; i < 10; i++)
        ii.nextValue();
      ii.removeRange(-9, 1);
      try {
        ii.removeRange(-9, 1);
        fail();
      } catch (IllegalStateException e) {}
      ii.next();
      ii.move(19);
      ii.removeRange(-9, 1);
      checkList(list, ap(0, 1, 100), ap(110, 1, 10), ap(130, 1, 9870));
      ii.next();
      ii.removeRange(-10, 0);
      checkList(list, ap(0, 1, 100), ap(130, 1, 9870));
    }
  }

  public void testIteratorRemoveFromEnd() {
    for (WritableLongList list: empty()) {
      for (int i = 0; i < 10000; i++)
        list.add(i);
      WritableLongListIterator ii = list.iterator(8191, 9192);
      ii.nextValue();
      while (ii.hasNext()) {
        ii.nextValue();
        ii.remove();
      }
      checkList(list, ap(0, 1, 8192), ap(9192, 1, 808));
    }
  }

  public void testIteratorSkip() {
    for (WritableLongList list: empty()) {
      for (int i = 0; i < 10000; i++)
        list.add(i);
      WritableLongListIterator ii = list.iterator();
      for (int i = 0; i < 100; i++) {
        assertTrue(ii.hasNext());
        assertEquals(100 * i, ii.nextValue());
        ii.move(99);
      }
      assertFalse(ii.hasNext());
    }
  }

  public void testIteratorWrite() {
    long[] values = {1, 1, 1, 2, 2, 3, 3, 3, 3};
    LongArray expected = LongArray.create(values);
    for (WritableLongList source : createWritableLongListVariants(values)) {
      LongArray result = new LongArray();
      for (WritableLongListIterator it : source.write()) {
        result.add(it.value());
        it.set(0, -1);
      }
      CHECK.order(source, LongCollections.repeat(-1, source.size()));
      assertEquals(expected, result);
    }
  }

  public void testIteratorWrite2() {
    long[] values = {1, 1, 1, 2, 2, 3, 3};
    long[] newValues = {2, 5, 5, -1, -1, -1, 4};
    assert values.length == newValues.length;
    for (int i = 0; i < values.length; i++) {
      for (WritableLongList list : createWritableLongListVariants(values)) {
        WritableLongListIterator it = list.iterator();
        it.move(i);
        for (int j = 0; j < values.length; j++) {
          it.set(-i + j + 1, newValues[j]);
        }
        checkCollection(list, newValues);
      }
    }
  }

  public void testSubList() {
    for (WritableLongList list: empty()) {
      list.addAll(range(0, 10000));
      checkList(list.subList(10, 20), ap(10, 1, 10));
      checkList(list.subList(10, 10000), ap(10, 1, 9990));
      checkList(list.subList(9990, 10000), ap(9990, 1, 10));
      checkList(list.subList(9990, 9990));
      assertEquals(list, list.subList(0, 10000));
      assertTrue(list == list.subList(0, 10000));
    }
  }

  public void testSubSubList() {
    for (WritableLongList list: empty()) {

      for (int i = 0; i < 10000; i++)
        list.add(i);
      LongList sub = list.subList(1000, 2000);
      checkList(sub, ap(1000, 1, 1000));
      LongList subsub = sub.subList(200, 300);
      checkList(subsub, ap(1200, 1, 100));
    }
  }

  public void testCopySubList() {
    for (WritableLongList list: empty()) {
      for (int i = 0; i < 10240; i++) {
        list.add(i);
      }
      list.addAll(list);
      checkList(list, ap(0, 1, 10240), ap(0, 1, 10240));

      list.setAll(100, list, 100, 100);
      checkList(list, ap(0, 1, 10240), ap(0, 1, 10240));
      list.setAll(100, list.subList(200, 300));
      checkList(list, ap(0, 1, 100), ap(200, 1, 100), ap(200, 1, 10040), ap(0, 1, 10240));

      list.insertAll(5000, list.subList(3000, 5000));
      checkList(list, ap(0, 1, 100), ap(200, 1, 100), ap(200, 1, 4800), ap(3000, 1, 2000), ap(5000, 1, 5240), ap(0, 1, 10240));
    }
  }

  public void testCopyInsertList() {
    for (WritableLongList list: empty()) {
      list.addAll(LongProgression.arithmetic(0, 10240, 1));
      LongArray src = new LongArray();
      src.addAll(list);
      list.insertAll(2000, src, 100, 10000);
      checkList(list, ap(0, 1, 2000), ap(100, 1, 10000), ap(2000, 1, 8240));
      list.setAll(5000, src);
      checkList(list, ap(0, 1, 2000), ap(100, 1, 3000), ap(0, 1, 10240), ap(5240, 1, 5000));
    }
  }

  public void testInsertAll() {
    for (WritableLongList list: empty()) {
      list.addAll(0);
      list.insertAll(1, LongArray.create(2, 4, 6).iterator());
      CHECK.order(LongArray.create(0,2,4,6), list);

      list.insertAll(2, LongArray.create(-5, -10, -15));
      CHECK.order(LongArray.create(0, 2, -5, -10, -15, 4, 6), list);

      list.insertAll(0, LongArray.create(0, 99, 99, 0), 1, 2);
      CHECK.order(LongArray.create(99, 99, 0, 2, -5, -10, -15, 4, 6), list);
    }
  }

  public void testRemoveAll() {
    for (WritableLongList list: empty()) {
      long[] values = {2, 0, 2, 1, 2, 2, 2, 2, 3, 2};
      list.addAll(values);
      CHECK.order(list.iterator(), values);
      list.removeAll();
      list.removeAll(2);
      CHECK.order(list.iterator(), 0, 1, 3);
      list.removeAll(2);
      CHECK.order(list.iterator(), 0, 1, 3);
      list.removeAll(0, 3);
      CHECK.order(list.iterator(), 1);
      list.removeAll(1);
      CHECK.empty(list);
    }
  }

  private void testReverse(long[] array, long[] expected) {
    for (WritableLongList list: createWritableLongListVariants(array)) {
      list.reverse();
      for (WritableLongList referenceLst: createWritableLongListVariants(expected)) {
        CHECK.order(referenceLst, list);
      }
    }
  }

  public void testReverse() {
    testReverse(new long[]{}, new long[]{});
    testReverse(new long[]{4, 4, 1, 2, 2, 2, 3, 3, 3, 3}, new long[]{3, 3, 3, 3, 2, 2, 2, 1, 4, 4});
    testReverse(new long[]{4, 4, 1, 2, 2, 2, 0, 0, 0, 0}, new long[]{0, 0, 0, 0, 2, 2, 2, 1, 4, 4});
    testReverse(new long[]{2, 4, 4, 5, 5, 5, 7, 7, 7, 7}, new long[]{7, 7, 7, 7, 5, 5, 5, 4, 4, 2});
    testReverse(new long[]{2, 2, 3, 3, 3}, new long[]{3, 3, 3, 2, 2});
    testReverse(new long[]{1, 1, 1}, new long[]{1, 1, 1});
    testReverse(new long[]{1, 1, 0}, new long[]{0, 1, 1});
    testReverse(new long[]{0}, new long[]{0});
    testReverse(new long[]{0, 1, 3, 6, 10, 15, 21, 28, 36}, new long[]{36, 28, 21, 15, 10, 6, 3, 1, 0});
    testReverse(new long[]{0, 1, 3, 6, 10, 15, 21, 28, 36}, new long[]{36, 28, 21, 15, 10, 6, 3, 1, 0});
    testReverse(new long[]{0, 0, 1, 2, 2, 2, 3, 3, 3, 3}, new long[]{3, 3, 3, 3, 2, 2, 2, 1, 0, 0});
    testReverse(new long[]{0, 0, 1, 2, 2, 2, 0, 0, 0, 0}, new long[]{0, 0, 0, 0, 2, 2, 2, 1, 0, 0});
    testReverse(new long[]{0, 0, 0}, new long[]{0, 0, 0});
    testReverse(new long[]{0, 0, 0, 1, 1}, new long[]{1, 1, 0, 0, 0});
    testReverse(new long[]{0, 0, 0, 0, 1, 1, 0}, new long[]{0, 1, 1, 0, 0, 0, 0});

    // have to use toNativeArray() instead of extractHostArray(), because of in the myArray may be duplicates
    for (WritableLongList list:
        createWritableLongListVariants(generateRandomLongArray( 20, IntegersFixture.SortedStatus.SORTED_UNIQUE, 200).toNativeArray())) {
      list.reverse();
      for (int i = 1; i < list.size(); i++) {
        assertFalse(list.get(i - 1) <= list.get(i));
      }
    }
  }

  public void testRemoveAll2() {
    for (WritableLongList list: empty()) {

      long MIN = Long.MIN_VALUE, MAX = Long.MAX_VALUE;
      long[][] tests = {{MIN, MIN, 0, 0, MAX, MAX},
          {-10, -5, 0, 0, 5, 10},
          {MIN, MIN, MIN},
          {0, 0, 0},
          {MAX, MAX, MAX}};
      long[] values = {MIN, -10, -5, 0, 5, 10, 0, MAX};
      LongArray actual, expected;
      for (long[] test: tests) {
        for (long value: values) {
          actual = LongArray.copy(test);
          actual.removeAllSorted(value);
          expected = LongArray.create(test);
          expected.removeAll(value);
          CHECK.order(expected, actual);
        }
      }
    }
  }

  public void testExpandException() {
    for (int size = 0; size < 5; size++) {
      for (WritableLongList list: createWritableLongListVariants(
          LongCollections.repeat(10, size).toNativeArray())) {
        try {
          list.expand(list.size() + 1, 0);
          fail();
        } catch (IndexOutOfBoundsException ex) {}

        try {
          list.expand(-1, 0);
          fail();
        } catch (IndexOutOfBoundsException ex) {}

        try {
          list.expand(0, -10);
          fail();
        } catch (IllegalArgumentException ex) {}

        if (size > 0) {
          list.expand(1, 0);
          CHECK.order(LongCollections.repeat(10, size), list);
        }
      }
    }
  }

  protected void checkExpand(long[] expected, int index, int count, WritableLongList checked) {
    assert index <= expected.length;
    checked.expand(index, count);

    assertEquals(expected.length + count, checked.size());
    LongListIterator it = checked.iterator();

    for (int i = 0; i < index; i++) {
      assertEquals(expected[i], it.nextValue());
    }
    it.move(count);
    for (int i = index; i < expected.length; i++) {
      assertEquals(expected[i], it.nextValue());
    }
  }

  public void testExpandSimple() {
    long[] elements = {5, 10, 4, 2, 1};
    for (int i = 0; i < elements.length; i++) {
      for (WritableLongList list: empty()) {
        list.addAll(elements);
        checkExpand(elements, i, 3, list);
      }
    }
  }

  public void testExpandComplex() {
    int arrayLength = 200, maxValue = Integer.MAX_VALUE;
    int maxExpandCount = 10, attempts = 10;
    for (int attempt = 0; attempt < attempts; attempt++) {
      long[] elements = generateRandomLongArray( arrayLength, IntegersFixture.SortedStatus.UNORDERED, maxValue).extractHostArray();
      for (WritableLongList list:
          createWritableLongListVariants(elements)) {
        int index = RAND.nextInt(elements.length);
        checkExpand(elements, index, RAND.nextInt(maxExpandCount), list);
      }
    }
  }

  public void testApply() {
    for (WritableLongList list: createWritableLongListVariants(ap(0, 1, 10))) {
      long[] expected = ap(0, 1, 10);
      list.apply(2, 8, new LongFunction() {
        @Override
        public long invoke(long a) {
          return a * a - 1;
        }
      });
      for (int i = 2; i < 8; i++) {
        expected[i] =  expected[i] * expected[i] - 1;
      }
      checkList(list, expected);
    }
  }

  public void testAddThis() {
    int arraySize = 100;
    for (int attempt = 0; attempt < 10; attempt++) {
      for (WritableLongList list: empty()) {
        LongArray values = generateRandomLongArray( arraySize, IntegersFixture.SortedStatus.UNORDERED);
        long[] nativeArray = new long[arraySize * 2];
        values.toNativeArray(0, nativeArray, 0, arraySize);
        values.toNativeArray(0, nativeArray, arraySize, arraySize);

        list.addAll(values);
        list.addAll(list);
        CHECK.order(list, nativeArray);
      }
    }
  }

  public void testUpdate() {
    int size = 100, maxVal = Integer.MAX_VALUE;
    for (int attempt = 0; attempt < 10; attempt++) {
      long[] array = generateRandomLongArray( size, IntegersFixture.SortedStatus.UNORDERED, maxVal).extractHostArray();
      for (WritableLongList list: createWritableLongListVariants(array)) {
        long[] values = Arrays.copyOf(array, array.length);
        if (!(list instanceof AbstractWritableLongList)) return;
        AbstractWritableLongList abstractList = (AbstractWritableLongList)list;
        int index = RAND.nextInt(size);
        abstractList.update(index, RAND.nextInt(), LongFunctions.NEG);
        values[index] = -values[index];
        CHECK.order(abstractList, values);

        int count = RAND.nextInt(size);
        long value = RAND.nextInt();
        abstractList.update(size + count, value, LongFunctions.NEG);
        assertEquals(size + count + 1, abstractList.size());
        CHECK.order(abstractList.subList(0, size), values);
        int newSize = abstractList.size() - 1;
        CHECK.order(LongCollections.repeat(value, count), abstractList.subList(size, newSize));
        assertEquals(abstractList.get(size + count), -value);
      }
    }
  }

  public void testRemoveLast() {
    int size = 10, maxVal = Integer.MAX_VALUE;
    for (int attempt = 0; attempt < 10; attempt++) {
      LongArray expected = generateRandomLongArray( size, IntegersFixture.SortedStatus.UNORDERED, maxVal);
      long[] array = new long[expected.size() + 1];
      expected.toNativeArray(0, array, 0, expected.size());
      for (WritableLongList list: createWritableLongListVariants(array)) {
        if (!(list instanceof AbstractWritableLongList)) return;
        AbstractWritableLongList abstractList = (AbstractWritableLongList)list;
        abstractList.removeLast();
        CHECK.order(expected, abstractList);
      }
    }
  }

  public void testGetNextDifferentValueIndex2() {
    for (WritableLongList list: empty()) {
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

      // check for IOOBE
      try {
        list.getNextDifferentValueIndex(-1);
        fail("not caught OOBE for index - 1");
      } catch(IndexOutOfBoundsException ex) { }

      try {
        list.getNextDifferentValueIndex(list.size());
        fail("not caught OOBE for index = size");
      } catch(IndexOutOfBoundsException ex) { }
    }
  }

  public void testRemoveRangeSimple() {
    for (WritableLongList list : createWritableLongListVariants(0, 0, 1, 1, 0, 0)) {
      list.removeRange(2, 4);
      CHECK.order(list, 0, 0, 0, 0);
    }
  }

  public void testSetRangeSimple() {
    for (WritableLongList list : empty()) {
      list.insertMultiple(0, 2, 2);
    list.setRange(0, 2, Integer.MIN_VALUE);
    CHECK.order(list, Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
  }
}