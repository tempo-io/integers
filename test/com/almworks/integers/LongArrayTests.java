/*
 * Copyright 2013 ALM Works Ltd
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

package com.almworks.integers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LongArrayTests extends IntegersFixture {
  private static final CollectionsCompare CHECK = new CollectionsCompare();
  private LongArray array = new LongArray();
  private SetOperationsChecker setOperations = new SetOperationsChecker();

  public void testAdd() {
    array.addAll(0, 1, 2);
    CHECK.order(array, LongArray.create(0, 1, 2));

    array.addAllNotMore(LongArray.create(3, 4, 10, 20), 2);
    CHECK.order(array, LongArray.create(0, 1, 2, 3, 4));

    array.addAllNotMore(LongArray.create(5, 6, 10, 20), 2);
    CHECK.order(array, LongArray.create(0, 1, 2, 3, 4, 5, 6));

    array.insert(3, 100);
    CHECK.order(array, LongArray.create(0, 1, 2, 100, 3, 4, 5, 6));

    array.insertMultiple(1, -1, 3);
    CHECK.order(array, LongArray.create(0, -1, -1, -1, 1, 2, 100, 3, 4, 5, 6));
  }
  public void testCopy() {
    LongArray copiedArray = LongArray.copy(new long[]{10, 20, 30});
    CHECK.order(copiedArray, LongArray.create(10, 20, 30));
  }

  public void testEqual() {
    array = LongArray.create(0, 1, 2, 3, 4, 5, 6);
    assertTrue(array.equalOrder(new long[]{0, 1, 2, 3, 4, 5, 6}));
    assertFalse(array.equalOrder(new long[]{0, 1, 2, 3, 4, 5, 20}));

    assertTrue(array.equalSortedValues(LongArray.create(0, 1, 2, 3, 4, 5, 6)));
    assertFalse(array.equalSortedValues(LongArray.create(0, 1, 2, 3, 4, 5, 20)));
  }

  public void testExpand() {
    array = LongArray.create(0, 1, 2, 3);
    array.expand(1, 4);
    CHECK.order(array, LongArray.create(0, 1, 2, 3, 0, 1, 2, 3));
  }

  public void testIndexOf() {
    array = new LongArray(LongProgression.arithmetic(99, 100, -1));
    for (int i = 0; i < 100; i++) {
      assertEquals(99 - i, array.indexOf(i));
    }
  }

  public void testRemove() {
    array = LongArray.create(0, -1, -1, -1, 1, 2, 3);
    array.removeRange(1, 4);
    CHECK.order(array, LongArray.create(0, 1, 2, 3));

    LongArray test = LongArray.create(0, 20, 21, 30, 35, 80);
    test.removeSorted(20);
    CHECK.order(test, LongArray.create(0, 21, 30, 35, 80));
  }

  public void testSet() {
    array = LongArray.create(0, 1, 2, 3, 4, 5);
    array.set(0, 10);
    array.setAll(3, LongArray.create(5, 31, 36, 100), 1, 2);
    CHECK.order(array, LongArray.create(10, 1, 2, 31, 36, 5));

    array.setRange(1, 3, -9);
    CHECK.order(array, LongArray.create(10, -9, -9, 31, 36, 5));
  }

  public void testSort() {
    int arrayLength = 200;
    int maxValue = Integer.MAX_VALUE;
    LongArray res = new LongArray();

    for (int j = 0; j < arrayLength; j++) {
      res.add((long)rand.nextInt(maxValue));
    }

    array = LongArray.copy(res);
    for (int i = 0; i < arrayLength; i++) {
      for (int j = 0; j < arrayLength - 1; j++) {
        if (array.get(j) > array.get(j+1)) {
          array.swap(j, j + 1);
        }
      }
    }
    res.sort();
    CHECK.order(res, array);
  }

  public void testOthersMethods() {
    CHECK.order(LongArray.singleton(Long.valueOf(-239)), LongArray.create(-239));

    long[] a = array.extractHostArray();
    CHECK.order(array, a);
  }

  public void testGetNextDifferentValueIndex() {
    array = LongArray.create(0, 1, 1, 2, 2, 2, 3, 4, 5, 5, 5);
    assertEquals(1, array.getNextDifferentValueIndex(0));
    assertEquals(3, array.getNextDifferentValueIndex(1));
    assertEquals(6, array.getNextDifferentValueIndex(3));
    assertEquals(7, array.getNextDifferentValueIndex(6));
    assertEquals(array.size(), array.getNextDifferentValueIndex(8));
  }

  public void testEnsureCapacity() {
    LongArray expected = new LongArray(LongProgression.arithmetic(0, 20, 1));
    array = LongArray.copy(expected);
    assertEquals(20, array.getCapacity());

    array.ensureCapacity(3);
    CHECK.order(array, expected);
    assertEquals(20, array.getCapacity());

    array.ensureCapacity(25);
    CHECK.order(array, expected);
    assertEquals(40, array.getCapacity());

    boolean caught = false;
    try {
      array.ensureCapacity(-1);
    } catch (IllegalArgumentException ex) {
      caught = true;
    }
    assertTrue("caught IAE", caught);
  }

  public void testRetain() {
    LongArray arr = LongArray.create(2, 3, 5, 6, 8, 9, 10, 13, 3, 4, 5, 3);
    LongList values = LongArray.create(1, 4, 5, 6, 7, 8, 10, 15);
    arr.retain(values);
    CHECK.order(LongArray.create(5, 6, 8, 10, 4, 5), arr);

    arr = new LongArray(LongProgression.arithmetic(0, 20, 1));
    arr.retainSorted(new LongArray(LongProgression.arithmetic(1, 15, 2)));
    CHECK.order(LongProgression.arithmetic(1, 10, 2), arr);

    setOperations.check(new SetOperationsChecker.newSetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        arrays[0].retain(arrays[1]);
        return arrays[0].iterator();
      }
    }, new SetOperationsChecker.IntersectionGetter(false), false, true);

    setOperations.check(new SetOperationsChecker.newSetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        arrays[0].retainSorted(arrays[1]);
        return arrays[0].iterator();
      }
    }, new SetOperationsChecker.IntersectionGetter(true), true, true);
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
    new CollectionsCompare().order(array.toNativeArray(),
        new LongProgression.Arithmetic(9999, 10000, -1).toNativeArray());
  }

  public void testRemoveByIterator() {
    int COUNT = 10000;
    for (int i = 0; i < COUNT; i++)
      array.add(COUNT - i);
    int x = 10000;
    for (WritableLongListIterator ii : array.write()) {
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
    array.toArray(0, new long[0], 0, 0);
  }

  public void testInserts() {
    array.insertMultiple(0, 1, 2048);
    checkList(array, ap(1, 0, 2048));
    array.insert(0, 2);
    array.insert(array.size(), 3);
    checkList(array, new long[] {2}, ap(1, 0, 2048), new long[] {3});
    array.insertMultiple(1, 2, 2000);
    checkList(array, ap(2, 0, 2001), ap(1, 0, 2048), new long[] {3});
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
    WritableLongListIterator ii = array.iterator(100, 600);
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
    WritableLongListIterator ii = array.iterator(8191, 9192);
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
    WritableLongListIterator ii = array.iterator();
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
    LongList sub = array.subList(1000, 2000);
    checkList(sub, ap(1000, 1, 1000));
    LongList subsub = sub.subList(200, 300);
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
    array.addAll(LongProgression.arithmetic(0, 10240, 1));
    LongArray list = new LongArray();
    list.addAll(array);
    array.insertAll(2000, list, 100, 10000);
    checkList(array, ap(0, 1, 2000), ap(100, 1, 10000), ap(2000, 1, 8240));
    array.setAll(5000, list);
    checkList(array, ap(0, 1, 2000), ap(100, 1, 3000), ap(0, 1, 10240), ap(5240, 1, 5000));
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
    List<Long> l = new ArrayList<Long>();
    l.add(2L);
    l.add(3L);
    l.add(9L);
    LongArray a = LongArray.create(l);
    CHECK.order(a.toNativeArray(), 2, 3, 9);
    LongList il = LongCollections.asLongList(l);
    CHECK.order(il.toNativeArray(), 2, 3, 9);
  }

  private void testReverse(long[] a, long[] b) {
    LongArray lst = new LongArray();
    lst.addAll(a);
    LongArray referenceLst = new LongArray();
    referenceLst.addAll(b);
    lst.reverse();
    assertEquals(lst, referenceLst);
  }

  public void testReverse() {
    testReverse(new long[]{}, new long[]{});
    testReverse(new long[]{0}, new long[]{0});
    testReverse(new long[]{1,1,0}, new long[]{0,1,1});
    testReverse(new long[]{0,1,3,6,10,15,21,28,36}, new long[]{36,28,21,15,10,6,3,1,0});

    LongArray lst = new LongArray();
    for (int i = 0; i < 20; i++) {
      lst.add(rand.nextInt(200));
    }
    lst.sortUnique();
    lst.reverse();
    for (int i = 1; i < lst.size(); i++) {
      assertFalse(lst.get(i - 1) <= lst.get(i));
    }
  }

  public void testUnion() {
    SetOperationsChecker.newSetCreator unionGetter = new SetOperationsChecker.UnionGetter();
    // is likely to be launched branch with realloc
    setOperations.check(new SetOperationsChecker.newSetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return arrays[0].unionWithSameLengthList(arrays[1]).iterator();
      }
    }, unionGetter, true, true);

    // guaranteed launch branch with replace
    setOperations.check(new SetOperationsChecker.newSetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        LongArray copy = LongArray.copy(arrays[0]);
        copy.ensureCapacity(arrays[0].size() + arrays[1].size());
        return copy.unionWithSameLengthList(arrays[1]).iterator();
      }
    }, unionGetter, true, true);

    // is likely to be launched branch with realloc
    setOperations.check(new SetOperationsChecker.newSetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return arrays[0].unionWithSmallArray(arrays[1]).iterator();
      }
    }, unionGetter, true, true);

    // guaranteed launch branch with replace
    setOperations.check(new SetOperationsChecker.newSetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        LongArray copy = LongArray.copy(arrays[0]);
        copy.ensureCapacity(arrays[0].size() + arrays[1].size());
        return copy.unionWithSmallArray(arrays[1]).iterator();
      }
    }, unionGetter, true, true);
  }
}