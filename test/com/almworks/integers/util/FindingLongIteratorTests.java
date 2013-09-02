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

package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;

public class FindingLongIteratorTests extends IntegersFixture {
  LongArray values = LongArray.create(0, 2, 4, 6, 8);
  LongIterator it;
  public void setUp() {
    super.setUp();
    it = new FindingLongIterator() {
      LongIterator innerIt = values.iterator();
      @Override
      protected boolean findNext() {
        if (!innerIt.hasNext()) return false;
        myCurrent = innerIt.nextValue();
        return true;
      }
    };
  }

  public void testSimple() {
    testSimple(it);
  }

  public void testSimple(LongIterator it) {
    assertFalse(it.hasValue());
    for (int i = 0, n = values.size(); i < n; i++) {
      assertTrue(it.hasNext());
      it.next();
      assertTrue(it.hasNext() || (i == n - 1 && !it.hasNext()));
      assertEquals(values.get(i), it.value());
      System.out.println(it.value());
      assertTrue(it.hasValue());
    }
  }
}
