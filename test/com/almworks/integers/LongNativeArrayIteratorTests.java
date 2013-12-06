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

import java.util.ArrayList;
import java.util.List;

public class LongNativeArrayIteratorTests extends IntegersFixture {
//  private static LongArray arr;
  private static LongListIterator iter;

  @Override
  protected void setUp() throws Exception {
    long[] nArray = {0, 1, 2, 3, 4, 5};
    iter = new LongArrayIterator(nArray);
    super.setUp();
  }

  public void testIteratorSpecification() {
    LongIteratorSpecificationChecker.check(new LongIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public List<LongIterator> get(long... values) {
        List<LongIterator> res = new ArrayList<LongIterator>();
        res.add(new LongArrayIterator(values));
        int length = values.length;
        long[] values2 = LongCollections.ensureCapacity(values, length * 2);
        for (int i = length; i < length * 2; i++) {
          values2[i] = RAND.nextLong();
        }
        res.add(new LongArrayIterator(values2, 0, length));

        LongArray arr = new LongArray(length * 2);
        arr.addAll(generateRandomLongArray(length, false));
        arr.addAll(values);
        res.add(new LongArrayIterator(arr.extractHostArray(), length, length * 2));

        return res;
      }
    });
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
    long[] nArray = {0, 1, 2, 3, 4, 5};
    iter = new LongArrayIterator(nArray);
    iter.move(2);
    assertEquals(0, iter.get(-1));
    assertEquals(1, iter.get(0));
    assertEquals(2, iter.get(1));
  }

  public void testIndex() {
    iter.move(2);
    assertEquals(1, iter.index());
    iter.move(4);
    assertEquals(5, iter.index());
  }

  public void testSimple() {
    LongIterator it = LongArray.create(0, 1, 2).iterator();
    assertFalse(it.hasValue());
    assertTrue(it.hasNext());
    it.next();
    assertEquals(0, it.value());
    assertTrue(it.hasNext());
    assertEquals(0, it.value());

    assertEquals(1, it.nextValue());
    assertTrue(it.hasNext());
    assertEquals(1, it.value());
  }
}
