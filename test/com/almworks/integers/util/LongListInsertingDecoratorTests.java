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

package com.almworks.integers.util;

import com.almworks.integers.*;

import java.util.ArrayList;
import java.util.List;

public class LongListInsertingDecoratorTests extends LongListChecker<LongListInsertingDecorator> {

  @Override
  protected List<LongListInsertingDecorator> createLongListVariants(long... values) {
    List<LongListInsertingDecorator> res = new ArrayList<LongListInsertingDecorator>();

    // [...]
    LongArray array = LongArray.copy(values);
    res.add(new LongListInsertingDecorator(array));
    if (array.isEmpty()) return res;

    // [...]~
    array = LongArray.copy(array);
    long lastVal = array.removeLast();
    res.add(new LongListInsertingDecorator(array, new IntLongMap(IntArray.create(array.size()), LongArray.create(lastVal))));

    if (values.length > 1) {
      // ~[...]~
      array = LongArray.copy(array);
      long firstVal = array.removeAt(0);
      res.add(new LongListInsertingDecorator(array, new IntLongMap(IntArray.create(0, values.length - 1), LongArray.create(firstVal, lastVal))));
    }

    // ~[...]
    array = LongArray.copy(values);
    long firstVal = array.removeAt(0);
    res.add(new LongListInsertingDecorator(array, new IntLongMap(IntArray.create(0), LongArray.create(firstVal))));

    // [..~..]
    array = LongArray.copy(values);
    int pos = array.size() / 2;
    if (pos != 0) {
      long posVal = array.removeAt(pos);
      res.add(new LongListInsertingDecorator(array, new IntLongMap(IntArray.create(pos), LongArray.create(posVal))));
    }

    // random inserts
    if (4 < values.length && values.length < 100) {
      int count = 4;
      for (int i = 0; i < count; i++) {
        LongArray source = LongArray.copy(values);
        IntLongMap inserted = new IntLongMap();
        int maxDiff = 4;
        int curIdx = RAND.nextInt(maxDiff);
        int removeCount = 0;
        while (curIdx < source.size()) {
          inserted.add(curIdx + removeCount, source.removeAt(curIdx));
          curIdx += 1 + RAND.nextInt(maxDiff);
          removeCount++;
        }
        LongListInsertingDecorator resArray = new LongListInsertingDecorator(source, inserted);
        CHECK.order(resArray, values);
        res.add(resArray);
      }
    }

    return res;
  }

  private final LongArray myArray = new LongArray();

  @Override
  protected void setUp() throws Exception {
    myArray.clear();
  }

  protected void checkInsertIndexes(final LongListInsertingDecorator ins, int... expected) {
    if (expected == null) {
      expected = IntegersUtils.EMPTY_INTS;
    }
    CHECK.order(ins.insertIndexIterator(), expected);
    LongList base = ins.getBase();
    for (int i = 0; i < base.size(); i++) {
      int newIndex = ins.getNewIndex(i);
      assertTrue(IntegersUtils.indexOf(expected, newIndex) < 0);
      assertEquals(base.get(i), ins.get(newIndex));
    }
    final int[] expectedCopy = expected;
    boolean res = ins.iterate(0, ins.size(), new AbstractLongListDecorator.LongVisitor() {
      int index = -1;

      public boolean accept(long value, LongList source) {
        index++;
        assertEquals(value, ins.get(index));
        if (IntegersUtils.indexOf(expectedCopy, index) >= 0)
          assertSame(ins, source);
        else
          assertSame(ins.getBase(), source);
        return true;
      }
    });
    assertTrue(res);
  }


  public void testInsertDecorator() {
    LongListInsertingDecorator ins = new LongListInsertingDecorator(myArray);
    checkCollection(ins);
    assertEquals(0, ins.getInsertCount());

    ins.insert(0, 1);
    checkCollection(ins, 1);
    assertEquals(1, ins.getInsertCount());
    checkInsertIndexes(ins, 0);
    CHECK.order(ins.insertValueIterator(), 1);

    myArray.insert(0, 3);
    checkCollection(ins, 1, 3);
    myArray.insert(1, 4);
    checkCollection(ins, 1, 3, 4);
    assertEquals(1, ins.getInsertCount());
    checkInsertIndexes(ins, 0);
    CHECK.order(ins.insertValueIterator(), 1);

    ins.insert(1, 2);
    checkCollection(ins, 1, 2, 3, 4);
    assertEquals(2, ins.getInsertCount());
    checkInsertIndexes(ins, 0, 1);
    CHECK.order(ins.insertValueIterator(), 1, 2);

    ins.insert(0, 0);
    checkCollection(ins, 0, 1, 2, 3, 4);
    assertEquals(3, ins.getInsertCount());
    checkInsertIndexes(ins, 0, 1, 2);
    CHECK.order(ins.insertValueIterator(), 0, 1, 2);

    ins.insert(5, 5);
    checkCollection(ins, 0, 1, 2, 3, 4, 5);
    assertEquals(4, ins.getInsertCount());
    checkInsertIndexes(ins, 0, 1, 2, 5);
    CHECK.order(ins.insertValueIterator(), 0, 1, 2, 5);
  }

  public void testInsertingDecoratorIterator() {
    myArray.addAll(1, 2, 4, 7, 8, 9, 11);
    LongListInsertingDecorator dec = new LongListInsertingDecorator(myArray);
    dec.insert(0, 0);
    dec.insert(3, 3);
    dec.insert(5, 5);
    dec.insert(6, 6);
    dec.insert(10, 10);

    assertEquals(12, dec.size());
    int x = 0;
    for (LongIterator i : dec) {
      assertEquals(x++, i.value());
    }
    assertEquals(12, x);

    myArray.clear();
    myArray.addAll(1, 2);
    dec = new LongListInsertingDecorator(myArray);
    dec.insert(0, 0);
    x = 0;
    for (LongIterator i: dec) {
      assertEquals(x++, i.value());
    }
  }

  public void testIsEmpty() {
    LongArray expected = LongArray.create(0, 1, 2, 5);
    LongListInsertingDecorator res = new LongListInsertingDecorator(LongArray.create());

    assertTrue(res.isEmpty());
    res.insert(0, 5);
    assertFalse(res.isEmpty());

    res = new LongListInsertingDecorator(expected);
    assertFalse(res.isEmpty());
  }

  public void testIndexSimple() {
    LongArray source = LongArray.create(1, 3, 4, 7, 8, 9);
    LongListInsertingDecorator tst = new LongListInsertingDecorator(source, new IntLongMap());
    IntArray toInsert = IntArray.create(0, 2, 5, 6, 10);
    for (IntIterator it : toInsert) {
      tst.insert(it.value(), it.value());
    }
    // [0*, 1, 2*, 3, 4, 5*, 6*, 7, 8, 9, 10*]
    LongListIterator it = tst.iterator();
    while (it.hasNext()) {
      it.next();
      assertEquals(it.index(), it.value());
    }
    checkCollection(tst, LongProgression.Arithmetic.fillArray(0, 1, 11));
  }

  public void testMapConstructor() {
    LongArray source = LongArray.create(2, 4, 6);
    LongListInsertingDecorator tst = new LongListInsertingDecorator(source,
        new IntLongMap(IntArray.create(0, 3), LongArray.create(-20, 20)));
    CHECK.order(tst, -20, 2, 4, 20, 6);

    tst = new LongListInsertingDecorator(source, new IntLongMap());
    CHECK.order(source, tst);

    tst = new LongListInsertingDecorator(source,
        new IntLongMap(IntArray.create(0, 4), LongArray.create(-20, 20)));
    CHECK.order(tst, -20, 2, 4, 6, 20);

    try {
      new LongListInsertingDecorator(source,
          new IntLongMap(IntArray.create(0, 5), LongArray.create(-20, 20)));
      fail();
    } catch (IllegalArgumentException _) {
      // ok
    }

    try {
      new LongListInsertingDecorator(source,
          new IntLongMap(IntArray.create(-2, 4), LongArray.create(-20, 20)));
      fail();
    } catch (IllegalArgumentException _) {
      // ok
    }
  }

  public void test() {
    myArray.addAll(0, 2, 4, 6);
    LongListInsertingDecorator list = new LongListInsertingDecorator(myArray);
    list.insert(1, 1);
    list.insert(3, 3);
    list.insert(5, 5);
    System.out.println(list);
  }
}
