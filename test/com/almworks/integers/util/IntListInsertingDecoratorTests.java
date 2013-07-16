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

import com.almworks.integers.IntArray;
import com.almworks.integers.IntIterator;
import com.almworks.integers.IntegersFixture;

public class IntListInsertingDecoratorTests extends IntegersFixture {
  private final IntArray myArray = new IntArray();

  public void testInsertDecorator() {
    IntListInsertingDecorator ins = new IntListInsertingDecorator(myArray);
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
//    myArray.addAll(1, 2, 4, 7, 8, 9);
    myArray.addAll(1, 2, 4, 7, 8, 9, 11);
    IntListInsertingDecorator dec = new IntListInsertingDecorator(myArray);
    dec.insert(0, 0);
    dec.insert(3, 3);
    dec.insert(5, 5);
    dec.insert(6, 6);
/*
    dec.insert(10, 10);
    dec.insert(11, 11);
    assertEquals(12, dec.size());
    int x = 0;
    for (IntIterator i : dec) {
      assertEquals(x++, i.value());
    }
    assertEquals(12, x);
*/
    dec.insert(10, 10);
    assertEquals(12, dec.size());
    int x = 0;
    for (IntIterator i : dec) {
      assertEquals(x++, i.value());
    }
    assertEquals(12, x);

    myArray.clear();
    myArray.addAll(1, 2);
    dec = new IntListInsertingDecorator(myArray);
    dec.insert(0, 0);
    x = 0;
    for (IntIterator i : dec) assertEquals(x++, i.value());

  }

  public void testIsEmpty() {
    IntArray expected = IntArray.create(0, 1, 2, 5);
    IntListInsertingDecorator res = new IntListInsertingDecorator(IntArray.create());

    assertTrue(res.isEmpty());
    res.insert(0, 5);
    assertFalse(res.isEmpty());

    res = new IntListInsertingDecorator(expected);
    assertFalse(res.isEmpty());
  }
}
