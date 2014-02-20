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

import junit.framework.TestCase;

import java.util.NoSuchElementException;

public class LongParallelListTests extends TestCase {
  private static final CollectionsCompare CHECK = new CollectionsCompare();
  private final LongArray myStorage = new LongArray();
  private final LongParallelList myList = new LongParallelList(myStorage, 2);

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

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

    long[] res = new long[2];
    myList.get(1, res);
    CHECK.order(res, 2, 3);

    try {
      myList.insert(0, null);
      fail();
    } catch (IllegalArgumentException ex) {}

    try {
      myList.insert(0, 1);
      fail();
    } catch (IllegalArgumentException ex) {}
  }

  public void testIteratorNextSet() {
    myList.insert(0, 0, 1);
    myList.insert(1, 2, 3);
    checkStorage(0, 1, 2, 3);

    LongParallelList.Iterator it = myList.iterator(0);
    long[] vals = new long[2];
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

    it.set(-1, 0, -10);
    it.set(-1, 1, -5);
    checkStorage(-10, -5, 2, 3);

    it = myList.iterator(1);
    it.next(null);
    it.set(0, 0, 20);
    checkStorage(-10, -5, 20, 3);
  }

  public void testGet() {
    myList.insert(0, -2, -3);
    myList.insert(1, 1, 2);
    myList.insert(2, 10, 20);
    checkStorage(-2, -3, 1, 2, 10, 20);

    long[] vals = new long[2];

    myList.get(0, vals);
    CHECK.order(vals, -2, -3);
    myList.get(1, vals);
    CHECK.order(vals, 1, 2);

    try {
      myList.get(0, null);
      fail();
    } catch (IllegalArgumentException ex) { }

    try {
      long[] vals1 = new long[1];
      myList.get(0, vals1);
      fail();
    } catch (IllegalArgumentException ex) { }
  }

  public void testIteratorGetRemoveRange() {
    myList.insert(0, -2, -3);
    myList.insert(1, 1, 2);
    myList.insert(2, 10, 20);
    myList.insert(3, 3, 4);
    myList.insert(4, 30, 40);
    checkStorage(-2, -3, 1, 2, 10, 20, 3, 4, 30, 40);

    LongParallelList.Iterator it = myList.iterator(1);
    it.next(null);
    long[] vals = new long[2];

    it.get(0, vals);
    CHECK.order(vals, 1, 2);
    assertEquals(it.get(0, 0), 1);
    assertEquals(it.get(0, 1), 2);

    it.get(1, vals);
    CHECK.order(vals, 10, 20);

    it.next(null);
    it.get(0, vals);
    CHECK.order(vals, 10, 20);

    try {
      it.get(0, 2);
      fail();
    } catch (IllegalArgumentException ex) { }

    it.removeRange(0, 1);
    checkStorage(-2, -3, 1, 2, 3, 4, 30, 40);
    it.next(null);
    it.get(-1, vals);
    CHECK.order(vals, 1, 2);
    it.get(0, vals);
    CHECK.order(vals, 3, 4);

    it = myList.iterator(0);
    it.next(null);
    it.get(0, vals);
    CHECK.order(vals, -2, -3);
    it.next(null);
    it.get(-1, vals);
    CHECK.order(vals, -2, -3);
    it.removeRange(-1, 0);
    checkStorage(1, 2, 3, 4, 30, 40);
    it.next(null);
    it.get(0, vals);
    CHECK.order(vals, 1, 2);

    it.removeRange(1, 3);
    checkStorage(1, 2);
  }

  private void checkStorage(long ... expected) {
    CHECK.order(myStorage.iterator(), expected);
  }

  public void testCreateListAccessor() {
    myList.insert(0, 0, 1);
    myList.insert(1, 2, 3);
    myList.insert(2, 4, 5);
    checkStorage(0, 1, 2, 3, 4, 5);

    LongList values = myList.createListAccessor(0);
    long[] res = values.toNativeArray();
    long[] expected = {0, 2, 4};
    CHECK.order(res, expected);
  }

  public void testRemoveClearIsEmpty() {
    myList.insert(0, 0, 1);
    myList.insert(1, 2, 3);
    myList.insert(2, 4, 5);
    checkStorage(0, 1, 2, 3, 4, 5);

    myList.removeAt(1);
    checkStorage(0, 1, 4, 5);

    myList.insert(2, 6, 7);
    myList.insert(3, 8, 9);
    checkStorage(0, 1, 4, 5, 6, 7, 8, 9);

    myList.removeRange(1, 3);
    checkStorage(0, 1, 8, 9);

    assertFalse(myList.isEmpty());
    myList.clear();
    checkStorage();
    assertTrue(myList.isEmpty());
  }

  public void testSet() {
    myList.insert(0, 0, 1);
    myList.insert(1, 2, 3);
    checkStorage(0, 1, 2, 3);

    myList.set(1, 1, 5);
    checkStorage(0, 1, 2, 5);
  }

  public void testIteratorMove() {
    myList.insert(0, 0, 1);
    myList.insert(1, 2, 3);
    myList.insert(2, 4, 5);
    checkStorage(0, 1, 2, 3, 4, 5);

    LongParallelList.Iterator it = myList.iterator(1, 3);
    long[] vals = new long[2];

    it.next(vals);
    CHECK.order(vals, 2, 3);
    it.next(vals);
    CHECK.order(vals, 4, 5);

    it.move(-1);
    it.next(vals);
    CHECK.order(vals, 4, 5);

    it.get(-1, vals);
    CHECK.order(vals, 2, 3);

    try {
      it.next(vals);
      fail();
    } catch (NoSuchElementException ex) {
      // ok
    }
  }
}