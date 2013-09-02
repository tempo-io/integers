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

import com.almworks.integers.func.LongFunction;

import java.util.NoSuchElementException;

public class PairIntLongIteratorTests extends IntegersFixture {
  private IntIterator arr1;
  private LongIterator arr2;
  private PairIntLongIterator res;

  public void setUp() {
    super.setUp();
    arr1 = IntArray.create(1, 2, 3, 4, 5).iterator();
    arr2 = LongArray.create(5, 10, 15, 20, 25).iterator();
    res = new PairIntLongIterator(arr1,arr2);
  }

  public void testNoSuchElementException(){
    boolean caught = false;
    try {
      res.value1();
    } catch(NoSuchElementException ex) {
      caught = true;
    }
    assertTrue("caught NSEE", caught);

    caught = false;
    try {
      res.value2();
    } catch(NoSuchElementException ex) {
      caught = true;
    }
    assertTrue("caught NSEE", caught);

    while (res.hasNext()) res.next();

    caught = false;
    try {
      res.next();
    } catch(NoSuchElementException ex) {
      caught = true;
    }
    assertTrue("caught NSEE", caught);
  }

  public void testUOE() {
    res = res.iterator();
    res.next();
    boolean caught = false;
    try {
      res.remove();
    } catch(UnsupportedOperationException ex) {
      caught = true;
    }
    assertTrue("caught UOE", caught);
  }

  public void testValueSimpleCase() {
    res = res.iterator();

    assertTrue(res.hasNext());
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
  }

  public void testValueComplexCase() {
    IntArray keys = new IntArray(IntProgression.arithmetic(0, 1000, 1));

    LongArray values0 = new LongArray(LongProgression.arithmetic(0, 1000, 1));
    LongArray values = new LongArray(values0.toNativeArray());
    values.apply(0, 1000, new LongFunction() {
      @Override
      public long invoke(long a) {
        return a * a - a;
      }
    });

    res = new PairIntLongIterator(keys.iterator(), values.iterator());
    for (int i = 0; i < 1000; i++) {
      res.next();
      int v1 = res.value1();
      long v2 = res.value2();
      assertEquals(i, v1);
      assertEquals(i * i - i, v2);
    }
  }
}