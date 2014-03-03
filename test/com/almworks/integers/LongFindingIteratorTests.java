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

import java.util.Arrays;
import java.util.List;

public class LongFindingIteratorTests extends IntegersFixture {
  LongArray values = LongArray.create(0, 2, 4, 6, 8);
  LongIterator it;
  public void setUp() throws Exception {
    super.setUp();
    it = new LongFindingIterator() {
      LongIterator innerIt = values.iterator();
      @Override
      protected boolean findNext() {
        if (!innerIt.hasNext()) return false;
        myNext = innerIt.nextValue();
        return true;
      }
    };
  }

  public void testIteratorSpecification() {
    LongIteratorSpecificationChecker.checkIterator(new LongIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public List<? extends LongIterator> get(final long... values) {
        return Arrays.asList(new LongFindingIterator() {
          LongIterator innerIt = LongArray.create(values).iterator();

          @Override
          protected boolean findNext() {
            if (!innerIt.hasNext()) return false;
            myNext = innerIt.nextValue();
            return true;
          }
        });
      }
    });
  }

  public void testSimple() {
    assertFalse(it.hasValue());
    for (int i = 0, n = values.size(); i < n; i++) {
      assertTrue(it.hasNext());
      it.next();
      assertTrue(it.hasNext() || (i == n - 1 && !it.hasNext()));
      assertEquals(values.get(i), it.value());
      assertTrue(it.hasValue());
    }
  }

  public void testSimple2() {
    long cur = 0;
    while (it.hasNext()) {
      assertEquals(cur, it.nextValue());
      cur += 2;
    }
  }

  public void testSimple3() {
    for (int i = 0; i < 5; i++) {
      assertEquals(i * 2, it.nextValue());
    }
  }
}
