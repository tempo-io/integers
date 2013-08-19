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

public class LongArrayIteratorTests extends IntegersFixture {
  private static LongArray arr;
  private static WritableLongListIterator iter;

  public void setUp() {
    arr = LongArray.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    iter = arr.iterator();
  }

  public void testCreate() throws Exception {
    long[] longArray = {0,1,2,3,4};
    LongIterator iter = LongArrayIterator.create(longArray);
    for (int i = 0; i < 5; i++) {
      assertEquals(i, iter.nextValue());
    }
  }

  public void testEmpty() {
    long[] longArray = {};
    LongIterator iter = LongArrayIterator.create(longArray);
    LongArrayIterator iter2 = new LongArrayIterator(null);

    assertFalse(iter.hasNext());
    assertFalse(iter2.hasNext());
  }

  public void testAbsget(){
    long[] arr = {1,2,3,4,5};
    LongArrayIterator a = new LongArrayIterator(arr);
    for(int i = 0; i < 5; i++) {
      assertEquals("Not equal", arr[i], a.absget(i));
    }
  }

  public void testGetSet() {
    iter.move(2);
    assertEquals(0, iter.get(-1));
    assertEquals(1, iter.get(0));
    assertEquals(2, iter.get(1));

    for(int i = -1; i < 2; i++) {
      iter.set(i, i - 1);
    }
    CHECK.order(arr, LongArray.create(-2, -1, 0, 3, 4, 5, 6, 7, 8, 9));
  }

  public void testIndex() {
    iter.move(2);
    assertEquals(1, iter.index());
    iter.move(4);
    assertEquals(5, iter.index());
  }

  public void testRemove() {
    iter.move(2);
    iter.remove();

    boolean caught = false;
    try {
      iter.move(1);
    } catch (IllegalStateException ex) {
      caught = true;
    }
    assertTrue(caught);

    iter.next();
    CHECK.order(arr, LongArray.create(0, 2, 3, 4, 5, 6, 7, 8, 9));

    iter.removeRange(0, 4);
    CHECK.order(arr, LongArray.create(0, 6, 7, 8, 9));
  }
}