/*
 * Copyright 2014 ALM Works Ltd
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

public class LongListDiffIndexedDecoratorTests extends IntegersFixture {
  private static LongArray source = LongArray.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  private static IntArray indices = IntArray.create(0, 0, 0, 2, 2, 2);
  private static LongListDiffIndexedDecorator array;

  public void setUp() throws Exception {
    super.setUp();
    array = new LongListDiffIndexedDecorator(source, indices);
  }

  public void testGet() {
    long[] expected = {0, 1, 2, 5, 6, 7};

    CHECK.order(array, expected);
    CHECK.order(indices.iterator(), array.getIndexes().iterator());
    CHECK.order(source.iterator(), array.getSource().iterator());

    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], array.get(i));
    }
  }

  public void testStatusMethods() {
    assertFalse(array.isEmpty());
    assertEquals(6, array.size());
  }

  public void testIterator() {
    LongListIterator myIt = array.iterator(1, array.size() - 1);
    assertTrue(myIt.hasNext());

    assertEquals(1, myIt.nextValue());
    assertEquals(2, myIt.nextValue());

    myIt.move(1);
    assertEquals(6, myIt.nextValue());
    assertFalse(myIt.hasNext());
    assertEquals(4, myIt.index());
    assertEquals(2, myIt.get(-2));
  }
}