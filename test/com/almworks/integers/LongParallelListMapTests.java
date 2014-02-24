/*
 * Copyright 2014 ALM Works Ltd
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

import junit.framework.TestCase;

import java.util.NoSuchElementException;

public class LongParallelListMapTests extends TestCase {
  private static final CollectionsCompare CHECK = new CollectionsCompare();
  private final LongArray myStorage = new LongArray();
  private final LongParallelListMap myMap = new LongParallelListMap(myStorage);

  public void setUp() throws Exception {
    assertTrue(myMap.isEmpty());
    for (int i = 0; i < 5; i++) {
      myMap.put(i*2, i * 10 + 1);
    }
  }

  public void testGet() {
    for (int i = 0; i < 5; i++) {
      assertEquals(i * 2, myMap.getKey(i));
      assertEquals(i * 10 + 1, myMap.getValue(i));
    }
  }

  public void testFind() {
    for (int i = 0; i < 5; i++) {
      int index = myMap.findKey(i * 2);
      assertEquals(i, index);
    }
    for (int i = 1; i < 4; i++) {
      int index = myMap.findKey(i * 2, 1);
      assertEquals(i, index);
    }
  }

  public void testRemove() {
    myMap.remove(2);

    LongList expectedKeys = LongArray.create(0, 4, 6, 8);
    LongList expectedValues= LongArray.create(1, 21, 31, 41);
    CHECK.order(myMap.keysIterator(0, myMap.size()), expectedKeys.iterator());
    CHECK.order(myMap.valuesIterator(0, myMap.size()), expectedValues.iterator());
    assertEquals(-2, myMap.findKey(2));

    myMap.removeAt(3);
    expectedKeys = LongArray.create(0, 4, 6);
    expectedValues= LongArray.create(1, 21, 31);
    CHECK.order(myMap.keysIterator(0, myMap.size()), expectedKeys.iterator());
    CHECK.order(myMap.valuesIterator(0, myMap.size()), expectedValues.iterator());

    myMap.removeRange(0, 2);
    expectedKeys = LongArray.create(6);
    expectedValues= LongArray.create(31);
    CHECK.order(myMap.keysIterator(0, myMap.size()), expectedKeys.iterator());
    CHECK.order(myMap.valuesIterator(0, myMap.size()), expectedValues.iterator());

    myMap.clear();
    assertEquals(0, myMap.size());
  }

  public void testSet() {
    myMap.setAt(0, -1, -10);
    LongList expectedKeys = LongArray.create(-1, 2, 4, 6, 8);
    LongList expectedValues= LongArray.create(-10, 11, 21, 31, 41);
    CHECK.order(myMap.keysIterator(0, myMap.size()), expectedKeys.iterator());
    CHECK.order(myMap.valuesIterator(0, myMap.size()), expectedValues.iterator());

    myMap.setKey(1, 3);
    try {
      myMap.setKey(1, 4);
      fail();
    } catch (IllegalArgumentException ex) { }

    myMap.setValue(1, 100);
    expectedKeys = LongArray.create(-1, 3, 4, 6, 8);
    expectedValues= LongArray.create(-10, 100, 21, 31, 41);
    CHECK.order(myMap.keysIterator(0, myMap.size()), expectedKeys.iterator());
    CHECK.order(myMap.valuesIterator(0, myMap.size()), expectedValues.iterator());
  }

  public void testAdjustKeys() {
    myMap.adjustKeys(1, 3, 1);
    LongList expectedKeys = LongArray.create(0, 3, 5, 6, 8);
    CHECK.order(myMap.keysIterator(0, myMap.size()), expectedKeys.iterator());

    myMap.adjustKeys(1, 3, -2);
    expectedKeys = LongArray.create(0, 1, 3, 6, 8);
    CHECK.order(myMap.keysIterator(0, myMap.size()), expectedKeys.iterator());

    try {
      myMap.adjustKeys(-1, 2, 5);
      fail();
    } catch (IndexOutOfBoundsException ex) { }

    try {
      myMap.adjustKeys(1, 6, 5);
      fail();
    } catch (IndexOutOfBoundsException ex) { }

    try {
      myMap.adjustKeys(1, 2, -1);
      fail();
    } catch (IllegalArgumentException ex) { }

    try {
      myMap.adjustKeys(4, 5, -2);
      fail();
    } catch (IllegalArgumentException ex) { }
  }

  public void testPut() {
    myMap.clear();
    for (int i = 4; 0 <= i; i--) {
      myMap.put(i*2, i * 10 + 1);
    }

    LongList expectedKeys = LongArray.create(0, 2, 4, 6, 8);
    WritableLongList expectedValues= LongArray.create(1, 11, 21, 31, 41);
    CHECK.order(myMap.keysIterator(0, myMap.size()), expectedKeys.iterator());
    CHECK.order(myMap.valuesIterator(0, myMap.size()), expectedValues.iterator());

    myMap.put(0, -1);
    expectedValues.set(0, -1);
    CHECK.order(myMap.valuesIterator(0, myMap.size()), expectedValues.iterator());
  }

  public void testInsertAt() {
    myMap.insertAt(2, 3, -1);

    LongList expectedKeys = LongArray.create(0, 2, 3, 4, 6, 8);
    LongList expectedValues= LongArray.create(1, 11, -1, 21, 31, 41);
    CHECK.order(myMap.keysIterator(0, myMap.size()), expectedKeys.iterator());
    CHECK.order(myMap.valuesIterator(0, myMap.size()), expectedValues.iterator());
  }

  public void testContainsKey() {
    for(int i = 0; i < 10; i++) {
      assertEquals(myMap.containsKey(i), (i % 2) == 0);
    }
  }

  public void testIteratorGet() {
    LongParallelListMap.Iterator it = myMap.iterator(1);
    assertTrue(it.hasNext());

    try {
      it.key();
      fail();
    } catch (NoSuchElementException ex) { }

    try {
      it.value();
      fail();
    } catch (NoSuchElementException ex) { }

    it.next();
    assertEquals(2, it.key());
    assertEquals(11, it.value());

    assertEquals(4, it.getKey(1));
    assertEquals(21, it.getValue(1));


  }

  public void testIteratorMove() {
    LongParallelListMap.Iterator it = myMap.iterator(1, 3);
    it.move(2);
    assertEquals(4, it.getKey(0));
    assertEquals(21, it.getValue(0));
  }

  public void testIteratorRemove() {
    LongParallelListMap.Iterator it = myMap.iterator(1);
    it.next();
    it.removeRange(0, 2);
    CHECK.order(myMap.keysIterator(0, myMap.size()), LongArray.create(0, 6, 8).iterator());
    CHECK.order(myMap.valuesIterator(0, myMap.size()), LongArray.create(1, 31, 41).iterator());

    it = myMap.iterator(1);
    it.next();
    it.remove();
    CHECK.order(myMap.keysIterator(0, myMap.size()), LongArray.create(0, 8).iterator());
    CHECK.order(myMap.valuesIterator(0, myMap.size()), LongArray.create(1, 41).iterator());
  }

  public void testIteratorSetValue() {
    LongParallelListMap.Iterator it = myMap.iterator(1);
    for (; it.hasNext(); it.next()) {
      it.setValue(1, it.getValue(1) - 11);
    }
    LongIterator expected = LongArray.create(1, 0, 10, 20, 30).iterator();
    CHECK.order(myMap.valuesIterator(0, myMap.size()), expected);
  }

}
