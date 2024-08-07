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

import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.Assert.*;

public class LongListIteratorSpecificationChecker extends LongIteratorSpecificationChecker<LongListIterator> {
  private LongListIteratorSpecificationChecker(Random random, IteratorGetter<LongListIterator> getter, ValuesType type) {
    super(random, getter, type);
  }

  public static void checkListIterator(Random random, IteratorGetter<LongListIterator> getter) {
    checkListIterator(random, getter, ValuesType.ALL);
  }

  public static void checkListIterator(Random random, IteratorGetter<LongListIterator> getter, ValuesType type) {
    LongListIteratorSpecificationChecker checker = new LongListIteratorSpecificationChecker(random, getter, type);
    checker.run();
  }

  @Override
  protected void testValues(long... values) {
    super.testValues(values);
    testIteratorIndex(values);
    testIteratorMove(values);
    testIteratorGet(values);
  }

  private void testIteratorIndex(long... values) {
    for (LongListIterator it : getter.get(values)) {
      checkValueAndCatchNSEE(it);
      try {
        it.index();
        fail();
      } catch (NoSuchElementException ex) {
        // ok
      }

      int index = 0;
      while (it.hasNext()) {
        it.next();
        assertEquals(index++, it.index());
      }
      index--;
      while (index > 0) {
        assertEquals(index--, it.index());
        it.move(-1);
      }
      if (!(it instanceof WritableLongListIterator)) continue;
      try {
        if (it.hasValue()) {
          it.remove();
          try {
            it.index();
            fail();
          } catch (IllegalStateException _) {
            // ok
          }
        } else {
          try {
            it.remove();
            fail();
          } catch (NoSuchElementException _) {
            // ok
          }
        }

      } catch (UnsupportedOperationException _) {
        // ok
      }
    }
  }

  private void testIteratorMove(long... values) {
    int length = values.length;
    if (length < 2) {
      return;
    }
    long lastVal = values[length - 1];
    for (LongListIterator it : getter.get(values)) {
      checkValueAndCatchNSEE(it);

      it.move(1);
      assertEquals(0, it.index());
      assertEquals(values[0], it.value());
      assertEquals(values[1], it.nextValue());
      assertEquals(1, it.index());
      assertEquals(values[0], it.get(-1));

      if (length > 9) {
        assertEquals(values[9], it.get(8));
      }

      try {
        it.move(-2);
        fail();
      } catch (NoSuchElementException ex) {}

      if (length > 4) {
        it.move(3);
        assertEquals(4, it.index());
        assertEquals(values[4], it.value());
        assertEquals(values[4], it.get(0));
        it.move(-1);
        assertEquals(3, it.index());
        assertEquals(values[4], it.get(1));
        assertEquals(values[4], it.nextValue());

        if (length > 9) {
          it.move(5);
          assertEquals(values[9], it.value());
          if (length == 10) {
            assertFalse(it.hasNext());
          }

          it.move(-1);
          assertEquals(values[9], it.nextValue());
          if (length == 10) {
            assertFalse(it.hasNext());
          }
        }
      }

      it.move(length - 1 - it.index());
      assertEquals(lastVal, it.value());
      assertEquals(lastVal, it.get(0));
      assertFalse(it.hasNext());

      it.move(-1);
      assertEquals(lastVal, it.nextValue());
      assertFalse(it.hasNext());
    }
  }

  private void testIteratorGet(long... values) {
    for (LongListIterator it : getter.get(values)) {
      checkValueAndCatchNSEE(it);
      for (int idx = 0; idx < values.length; idx++) {
        assertTrue(it.hasNext());
        assertEquals(values[idx], it.nextValue());

        try {
          it.get(-idx - 1);
          fail();
        } catch (NoSuchElementException _) {
          // ok
        }

        for (int i = 0; i < values.length; i++) {
          assertEquals(values[i], it.get(-idx + i));
        }

        try {
          it.get(-idx + values.length);
          fail();
        } catch (NoSuchElementException _) {
          // ok
        }
      }
    }
  }

}