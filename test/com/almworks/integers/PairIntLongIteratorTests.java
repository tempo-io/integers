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

public class PairIntLongIteratorTests extends IntegersFixture {
  public void testCase() {
    IntIterator arr1 = IntArray.create(1,2,3,4,5).iterator();
    LongIterator arr2 = LongArray.create(5, 10, 15, 20, 25).iterator();
    PairIntLongIterator res = new PairIntLongIterator(arr1,arr2);


    res = res.iterator();

    assertTrue(res.hasNext());

    boolean caught = false;
    try {
      res.value1();
    } catch(NoSuchElementException ex) {
      caught = true;
    }
    assertTrue("caught IOOBE", caught);

    caught = false;
    try {
      res.value2();
    } catch(NoSuchElementException ex) {
      caught = true;
    }
    assertTrue("caught IOOBE", caught);

    int e1;
    long e2;
    for (int i = 1; i <= 5; i++) {
      res.next();
      e1 = res.value1();
      e2 = res.value2();
      assertEquals(e1, i);
      assertEquals(e2, i*5);
    }

    assertFalse(res.hasNext());
    caught = false;
    try {
      res.next();
    } catch(NoSuchElementException ex) {
      caught = true;
    }

    caught = false;
    try {
      res.remove();
    } catch(UnsupportedOperationException ex) {
      caught = true;
    }
    assertTrue("caught UOE", caught);
  }
}
