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
}
