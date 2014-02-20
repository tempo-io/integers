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


public class LongListIndexedIteratorTests extends IntegersFixture {
  public void testSimpleCase() {
    LongArray list = LongArray.create(2, 3, 9);
    IntListIterator indices = IntArray.create(0, 2).iterator();
    LongListIndexedIterator res = new LongListIndexedIterator(list, indices);
    CHECK.order(LongArray.create(2, 9).iterator(), res);
  }

  public void testListOperationsCase() {
    LongArray list = LongArray.create(1, 2, 3, 4, 5);
    IntListIterator indices = IntArray.create(0, 2, 4).iterator();
    LongListIndexedIterator res = new LongListIndexedIterator(list, indices);

    // check .get
    assertEquals(3, res.get(2));

    try {
      res.index();
      fail("caught NSEE");
    } catch (NoSuchElementException ex) { }

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

  public void testNoSuchElementExceptionCase() {
    LongArray list = LongArray.create(2, 3, 9);
    IntListIterator indices = IntArray.create(0, 2).iterator();
    LongListIndexedIterator res = new LongListIndexedIterator(list, indices);

    // check for NSEE
    try {
      res.value();
      fail("not caught NSEE");
    } catch(NoSuchElementException ex) { }
  }
}
