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

import java.util.NoSuchElementException;


public class IndexedLongListIteratorTests extends IntegersFixture {
  public void testSimpleCase() {
    LongArray list = LongArray.create(2, 3, 9);
    IntListIterator indexes = IntArray.create(0, 2).iterator();
    IndexedLongListIterator res = new IndexedLongListIterator(list, indexes);
    CHECK.order(LongArray.create(2, 9).iterator(), res);


  }

  public void testListOperationsCase() {
    LongArray list = LongArray.create(1, 2, 3, 4, 5);
    IntListIterator indexes = IntArray.create(0, 2, 4).iterator();
    IndexedLongListIterator res = new IndexedLongListIterator(list, indexes);

    // check .get
    assertEquals(3, res.get(2));

    // check index, move
    boolean caught = false;
    try {
      res.index();
    } catch (NoSuchElementException ex) {
      caught = true;
    }
    assertTrue("caught NSEE", caught);

    for(int i = 0; i < 3; i++) {
      res.next();
      assertEquals(i, res.index());
    }
    for(int i = 2; i > 0; i--) {
      assertEquals(i, res.index());
      res.move(-1);
    }

    for(int i = 0; i < 2; i++) {
      assertEquals(i, res.index());
      res.move(1);
    }
    assertEquals(2, res.index());
  }

  public void testIndexOutOfBoundsExceptionCase() {
    LongArray list = LongArray.create(2, 3, 9);
    IntListIterator indexes = IntArray.create(0, 2).iterator();
    IndexedLongListIterator res = new IndexedLongListIterator(list, indexes);

    // check for IOOBE
    boolean caught = false;
    try {
      res.value();
    } catch(NoSuchElementException ex) {
      caught = true;
    }
    assertTrue("caught IOOBE", caught);
  }
  public void testRandomCase() {

  }
  public void testSpecificationCase() {

  }
}
