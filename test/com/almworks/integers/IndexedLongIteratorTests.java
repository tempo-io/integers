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


public class IndexedLongIteratorTests extends IntegersFixture {
  public void testSimpleCase() {
    LongArray list = LongArray.create(2, 3, 9);
    IntIterator indexes = IntArray.create(0, 2).iterator();
    IndexedLongIterator res = new IndexedLongIterator(list, indexes);
    CHECK.order(LongArray.create(2, 9).iterator(), res);

    list = LongArray.create(1, 2, 3, 4, 5);
    indexes = IntArray.create(0, 2, 4).iterator();
    res = new IndexedLongIterator(list, indexes);
    CHECK.order(LongArray.create(1, 3, 5).iterator(), res);
  }

  public void testEmptyCase() {
    LongArray list = LongArray.create(2, 3, 9);
    IntIterator indexes = IntArray.create().iterator();
    IndexedLongIterator res = new IndexedLongIterator(list, indexes);
    CHECK.order(LongArray.create().iterator(), res);
  }

  public void testNoSuchElementExceptionCase() {
    LongArray list = LongArray.create(2, 3, 9);
    IntIterator indexes = IntArray.create(0, 2).iterator();
    IndexedLongIterator res = new IndexedLongIterator(list, indexes);

    while (res.hasNext()) {
      res.nextValue();
    }

    // check for NSEE
    boolean caught = false;
    try {
      res.nextValue();
    } catch(NoSuchElementException ex) {
      caught = true;
    }
    assertTrue("caught NSEE", caught);
  }

  public void testRandomCase() {
    int arrayLength = 1000;
    int indexesLength = 100;
    int maxValue = Integer.MAX_VALUE;

    LongArray list = LongArray.create();
    for ( int i = 0; i < arrayLength; i++) {
      list.add((long) RAND.nextInt(maxValue));
    }
    IntArray arrayIndexes = IntArray.create();
    for ( int i = 0; i < indexesLength; i++) {
      arrayIndexes.add(RAND.nextInt(arrayLength));
    }

    LongArray expected = LongArray.create();
    for ( int i = 0; i < indexesLength; i++) {
      expected.add(list.get(arrayIndexes.get(i)));
    }

    IndexedLongIterator res = new IndexedLongIterator(list, arrayIndexes.iterator());
    CHECK.order(res, expected.iterator());

  }
}
