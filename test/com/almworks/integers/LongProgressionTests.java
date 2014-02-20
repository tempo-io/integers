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

import java.util.Arrays;
import java.util.List;

public class LongProgressionTests extends IntegersFixture {
  public void testCreator() {
    LongProgression.Arithmetic progression = new LongProgression.Arithmetic(0, 5, 2);
    LongList expected = LongArray.create(0, 2, 4, 6, 8);
    CHECK.order(expected.iterator(), progression.iterator());
  }

  public void testFillArray() {
    long[] res = LongProgression.Arithmetic.nativeArray(0, 5, 2);
    long[] expected = {0, 2, 4, 6, 8};
    CHECK.order(res, expected);
  }

  public void testIndexOf() {
    LongProgression.Arithmetic progression = new LongProgression.Arithmetic(0, 5, 2);
    for( int i = 0; i < 5; i++) {
      assertEquals(i, progression.indexOf(i * 2));
    }
    assertEquals(-1, progression.indexOf(-2));
    assertEquals(-1, progression.indexOf(3));
    assertEquals(-1, progression.indexOf(10));
  }

  public void testSimple() {
    LongProgression.Arithmetic rO = new LongProgression.Arithmetic(1,5,1);
    int i = 1;
    for (LongIterator it : rO) {
      assertEquals(i++, it.value());
    }
  }

  public void testGetCount() {
    // start, stop, step
    int attemptsCount = 30;
    int maxVal = 100000;
    int maxStep = 100000;
    for (int attempt = 0; attempt < attemptsCount; attempt++) {
      long start = RAND.nextInt(maxVal);
      long stop = RAND.nextInt(maxVal);
      if (stop == start) {
        stop = start + 1;
      }
      // step in [-maxStep, maxStep)
      long step = RAND.nextInt(maxStep * 2)  - maxStep;

      int count = LongProgression.getCount(start, stop, step);
      long lastValue = start + step * (count - 1);
      long extraValue = lastValue + step;
      if (count == 0) {
        assertTrue((stop - start) * step < 0);
      } else {
        if (step > 0) {
          assertTrue(start + " " + stop + " " + step + " " + count, lastValue < stop && stop <= extraValue);
        } else {
          assertTrue(start + " " + stop + " " + step + " " + count, extraValue <= stop && stop < lastValue);
        }
      }
    }

    try {
      LongProgression.getCount(0, 10, 0);
      fail();
    } catch (IllegalArgumentException _) {
      // ok
    }
  }

  public void testArithmeticIteratorSpecification() {
    LongIteratorSpecificationChecker.checkIterator(new LongIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public List<? extends LongIterator> get(long... values) {
        long start = 0, step = 0;
        int count = 0;
        if (values.length != 0) {
          start = values[0];
          count = values.length;
          step = (count > 1) ? values[1] - values[0] : 1;
        }
        return Arrays.asList(LongProgression.arithmetic(start, count, step).iterator());
      }
    }, LongIteratorSpecificationChecker.ValuesType.ARITHMETHIC);
  }

  public void testToNativeArray() {
    int start = 10, step = 3, count = 31;
    long[] values = new long[count];
    for (int i = 0; i < count; i++) values[i] = start + step * i;

    LongProgression list = LongProgression.arithmetic(start, count, step);
    for (int startIdx = 0; startIdx < count; startIdx++) {
      for (int endIdx = startIdx + 1; endIdx < count; endIdx++) {
        long[] expected = LongCollections.repeat(-1, count).toNativeArray();
        long[] actual = LongCollections.repeat(-1, count).toNativeArray();

        int len = endIdx - startIdx;
        System.arraycopy(values, startIdx, expected, startIdx, len);
        list.toNativeArray(startIdx, actual, startIdx, len);
        CHECK.order(actual, expected);
      }
    }

  }
}
