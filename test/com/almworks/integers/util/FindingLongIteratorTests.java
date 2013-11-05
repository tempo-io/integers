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

import com.almworks.integers.*;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class FindingLongIteratorTests extends IntegersFixture {
  public void testIteratorSpecification() {
    LongIteratorSpecificationChecker.check(new LongIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public LongIterator get(final long... values) {
        return new FindingLongIterator() {
          LongIterator innerIt = LongArray.create(values).iterator();
          @Override
          protected boolean findNext() {
            if (!innerIt.hasNext()) return false;
            myCurrent = innerIt.nextValue();
            return true;
          }
        };
      }
    });
  }

  public void test() {
  LongIterator it1 = new AbstractLongIteratorWithFlag() {
    @Override
    protected long valueImpl() {
      return 1;
    }

    @Override
    protected void nextImpl() throws NoSuchElementException {
    }

    @Override
    public boolean hasNext() throws ConcurrentModificationException {
      return true;
    }
  };

    LongIterator itSwap = new AbstractLongIteratorWithFlag() {
      int val0 = 1, val3 = 3;
      boolean cur = true;
      @Override
      protected long valueImpl() {
        return cur ? val0 : val3;
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {
        cur = !cur;
      }

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }
    };

    LongIterator itInc = new AbstractLongIteratorWithFlag() {
      int cur = 0;
      @Override
      protected long valueImpl() {
        return cur;
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {
        cur++;
      }

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return true;
      }
    };
}

}
