/*
 * Copyright 2010 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almworks.integers.util;

import com.almworks.integers.*;
import com.almworks.util.Pair;
import junit.framework.TestCase;

import java.util.Random;

import static com.almworks.integers.LongCollections.uniteTwoLengthySortedSetsAndIntersectWithThirdShort;

public class LongCollectionsTests extends TestCase {
  public static final CollectionsCompare COMPARE = new CollectionsCompare();

  public void testUniteTwoLengthySortedSetsAndIntersectWithThirdShort() {
    doTestSimple();
    doTestMany();
  }

  private void doTestSimple() {
    LongList a = LongArray.create(1, 2, 4, 7, 8);
    LongList b = LongArray.create(2, 3, 5, 7);
    LongList intW = LongArray.create(2, 3, 6, 7, 8);
    COMPARE.order(uniteTwoLengthySortedSetsAndIntersectWithThirdShort(a, b, intW), 2, 3, 7, 8);
    COMPARE.order(uniteTwoLengthySortedSetsAndIntersectWithThirdShort(LongList.EMPTY, b, intW), 2, 3, 7);
    COMPARE.order(uniteTwoLengthySortedSetsAndIntersectWithThirdShort(a, LongList.EMPTY, intW), 2, 7, 8);
    COMPARE.order(uniteTwoLengthySortedSetsAndIntersectWithThirdShort(a, b, LongList.EMPTY));
  }

  private void doTestMany() {
    Random rand = new Random(System.currentTimeMillis());
    int len = 1000;
    float intersRate = 0.3f;
    int intersLen = 100;
    int maxElem = len * 10;
    for (int nTest = 0; nTest < 10; ++nTest) {
      LongList a = createRand(rand, len, maxElem, 2);
      LongList b = createRand(rand, len, maxElem, 3);
      Pair<LongArray, LongArray> exp_int = createInters(rand, a, b, intersRate, intersLen, maxElem);
      LongArray inters = exp_int.getSecond();
      System.out.println("\n/////////\na: " + a + "\nb: " + b + "\ninters:" + inters);
      COMPARE.order(exp_int.getFirst(), uniteTwoLengthySortedSetsAndIntersectWithThirdShort(a, b, inters));
    }
  }

  private LongList createRand(Random rand, int len, int maxElem, int factor) {
    maxElem = maxElem / factor;
    LongArray larr = new LongArray(len);
    for (int i = 0; i < len; ++i) {
      int elem = rand.nextInt(maxElem) * factor;
      larr.add(elem);
    }
    larr.sortUnique();
    return larr;
  }

  private Pair<LongArray, LongArray> createInters(Random rand, LongList a, LongList b, float intersRate, int intersLen, int maxElem) {
    LongArray trueIntersection = new LongArray(intersLen);
    LongArray withExtra = new LongArray(intersLen);
    int sza = a.size();
    int szb = b.size();
    int max = (int)((sza + szb) / intersRate);
    for (int i = 0; i < intersLen; ++i) {
      int idx = rand.nextInt(max);
      if (idx < sza) {
        long value = a.get(idx);
        trueIntersection.add(value);
        withExtra.add(value);
      } else if (idx < szb) {
        long value = b.get(idx - sza);
        trueIntersection.add(value);
        withExtra.add(value);
      } else {
        long value;
        do value = rand.nextInt(maxElem);
        while (value % 2 == 0 || value % 3 == 0);
        withExtra.add(value);
      }
    }
    trueIntersection.sortUnique();
    withExtra.sortUnique();
    return Pair.create(trueIntersection, withExtra);
  }
}
