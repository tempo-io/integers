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
import com.almworks.integers.LongIteratorSpecificationChecker;

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

}
