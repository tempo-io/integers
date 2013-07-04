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

import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import com.almworks.integers.NativeIntFixture;
import com.almworks.util.RandomHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.almworks.integers.LongArray.create;

public class SortedLongListIntersectionIteratorTests extends NativeIntFixture {
  long max = Long.MAX_VALUE, min = Long.MIN_VALUE;

  public void templateCase(LongArray arrays[], LongIterator expected) {
    List<LongIterator> iterators = new ArrayList<LongIterator>(arrays.length);
    IntegersDebug.println(arrays.length);

    for (int i = 0; i < arrays.length; i++) {
      iterators.add(arrays[i].iterator());
      IntegersDebug.println("iterator ", i, " : ", arrays[i]);
    }
    SortedLongListIntersectionIterator res = new SortedLongListIntersectionIterator(iterators);
    CHECK.order(res, expected);
  }

  public void testSimpleCase() {
    LongArray res[] = {
        create(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24),
        create(3, 6, 9, 12, 15, 18, 21, 24)};
    templateCase(res, create(6, 12, 18, 24).iterator());

    res[0] = create(1, 2, 3, 4);
    res[1] = create(5, 6, 7, 8);
    templateCase(res, create().iterator());

    res[0] = create(0, 2, 3, 4, 6, 7, 8, 9, 10, 16, 17, 18, 19, 20);
    res[1] = create(0, 5, 10, 15, 20);
    templateCase(res, create(0, 10, 20).iterator());
  }


  public void testRandomIntersectionEsistsCase() {
    Random r = new RandomHolder().getRandom();
    int intersectionLength = 0;
    int resLength = 1000;
    int maxArrayLength = 1000;
    int maxValue = Integer.MAX_VALUE;


    LongArray res[] = new LongArray[resLength];
    LongArray expected;

    LongArray intersection = create();
    for ( int i = 0; i < intersectionLength; i++) {
        intersection.add((long)r.nextInt(maxArrayLength));
    }

    for (int i = 0; i < resLength; i++) {
      int arrayLength = r.nextInt(maxArrayLength);
      res[i] = LongArray.copy(intersection);

      for (int j = 0; j < arrayLength; j++) {
        res[i].add((long)r.nextInt(maxValue));
      }
      res[i].sortUnique();
      IntegersDebug.println(res[i]);
    }

    expected = LongArray.copy(res[0]);
    for (int i = 1; i < resLength; i++) {
      expected.retain(res[i]);
    }
    assert expected.isUniqueSorted();
    IntegersDebug.println("expected", expected);
    int len = 0;
    for (LongIterator iter = expected.iterator(); iter.hasNext(); iter.next()) len++;
    System.out.println("expected len: " + len);
    templateCase(res, expected.iterator());
  }

}
