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

import static com.almworks.integers.LongProgression.Arithmetic.fillArray;

public class LongListInsertingDecoratorTests extends LongListChecker {

  @Override
  protected List<? extends LongList> createLongListVariants(long... values) {
    List<LongList> res = new ArrayList<LongList>();

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
    return res;
  }

  private final LongArray myArray = new LongArray();

  @Override
  protected void setUp() throws Exception {
    myArray.clear();
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

  public void testSimple() {
    LongArray source = LongArray.create(ap(0, 10, 7));
    LongListInsertingDecorator tst = new LongListInsertingDecorator(source, new IntLongMap());
    tst.insert(5, 45);
    tst.insert(3, 25);
    tst.insert(4, 28);
    LongArray expected = new LongArray(tst);
//    System.out.println(tst);

    LongListIterator it = tst.iterator();
    LongListIterator expIt = expected.iterator();

    for (int i = 0; i < 3; i++) {
      it.next();
      expIt.next();
    }

    assertEquals(expIt.value(), it.value());
    it.move(-2);
    expIt.move(-2);
    assertEquals(expIt.value(), it.value());
  }
}
