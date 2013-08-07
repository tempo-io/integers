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

import com.almworks.integers.NativeIntFixture;

import java.util.NoSuchElementException;


public class IndexedIntIteratorTests extends NativeIntFixture{
  public void testSimpleCase() {
    IntArray list = IntArray.create(2, 3, 9);
    IntIterator indexes = IntArray.create(0, 2).iterator();
    IndexedIntIterator res = new IndexedIntIterator(list, indexes);
    CHECK.order(res, IntArray.create(2, 9).iterator());

    list = IntArray.create(1, 2, 3, 4, 5);
    indexes = IntArray.create(0, 2, 4).iterator();
    res = new IndexedIntIterator(list, indexes);
    CHECK.order(res, IntArray.create(1, 3, 5).iterator());
  }

  public void testNoSuchElementExceptionCase() {
    IntArray list = IntArray.create(2, 3, 9);
    IntIterator indexes = IntArray.create(0, 2).iterator();
    IndexedIntIterator res = new IndexedIntIterator(list, indexes);
    res.next(); res.next();

    // check for NSEE
    boolean caught = false;
    try {
//      res.value();
      System.out.println(res.nextValue());
    } catch(NoSuchElementException ex) {
      caught = true;
    }
    assertTrue("caught NSEE", caught);
  }

  public void testIndexOutOfBoundsExceptionCase() {
    IntArray list = IntArray.create(2, 3, 9);
    IntIterator indexes = IntArray.create(0, 2).iterator();
    IndexedIntIterator res = new IndexedIntIterator(list, indexes);

    // check for IOOBE
    boolean caught = false;
    try {
      res.value();
    } catch(IllegalStateException ex) {
      caught = true;
    }
    assertTrue("caught IOOBE", caught);
  }
}
