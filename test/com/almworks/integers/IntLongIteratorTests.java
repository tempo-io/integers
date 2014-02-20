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

import com.almworks.integers.func.LongToLong;

import java.util.NoSuchElementException;

public class IntLongIteratorTests extends IntegersFixture {
  private IntLongIterator res;

  public void setUp() throws Exception {
    super.setUp();
    IntIterator arr1 = IntArray.create(1, 2, 3, 4, 5).iterator();
    LongIterator arr2 = LongArray.create(5, 10, 15, 20, 25).iterator();
    res = IntLongIterators.pair(arr1, arr2);
  }

  public void testNoSuchElementException(){
    try {
      res.left();
      fail("not caught NSEE");
    } catch(NoSuchElementException ex) { }

    try {
      res.right();
      fail("not caught NSEE");
    } catch(NoSuchElementException ex) { }

    while (res.hasNext()) res.next();

    try {
      res.next();
      fail("not caught NSEE");
    } catch(NoSuchElementException ex) { }
  }

  public void testUnsupportedOperationException() {
    res = res.iterator();
    res.next();
    try {
      res.remove();
      fail("not caught UOE");
    } catch(UnsupportedOperationException ex) { }
  }

  public void testValueSimpleCase() {
    res = res.iterator();

    assertTrue(res.hasNext());
    int e1;
    long e2;
    for (int i = 1; i <= 5; i++) {
      res.next();
      e1 = res.left();
      e2 = res.right();
      assertEquals(e1, i);
      assertEquals(e2, i*5);
    }

    assertFalse(res.hasNext());
  }

  public void testValueComplexCase() {
    IntArray keys = new IntArray(IntProgression.arithmetic(0, 1000, 1));

    LongArray values0 = new LongArray(LongProgression.arithmetic(0, 1000, 1));
    LongArray values = new LongArray(values0.toNativeArray());
    values.apply(0, 1000, new LongToLong() {
      @Override
      public long invoke(long a) {
        return a * a - a;
      }
    });

    res = IntLongIterators.pair(keys.iterator(), values.iterator());
    for (int i = 0; i < 1000; i++) {
      res.next();
      int v1 = res.left();
      long v2 = res.right();
      assertEquals(i, v1);
      assertEquals(i * i - i, v2);
    }
  }
}