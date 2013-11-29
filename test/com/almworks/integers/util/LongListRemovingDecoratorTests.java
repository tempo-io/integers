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

import java.util.ArrayList;
import java.util.List;


public class LongListRemovingDecoratorTests extends LongListChecker {

  @Override
  protected List<? extends LongList> createLongListVariants(long... values) {
    LongArray expected = LongArray.copy(values);
    List<LongList> res = new ArrayList<LongList>();

    // [...]
    LongArray source = LongArray.copy(values);
    LongList resArray = new LongListRemovingDecorator(source);
    CHECK.order(expected, resArray);
    res.add(resArray);

    if (values.length == 0) return res;

    // [...]~
    source = LongArray.copy(values);
    source.add(-RAND.nextInt());
    IntArray indexes = IntArray.create(values.length);
    LongListRemovingDecorator.prepareSortedIndices(indexes);
    resArray = LongListRemovingDecorator.createFromPrepared(source, indexes);
    CHECK.order(expected, resArray);
    res.add(resArray);

    // ~[...]~
    source = LongCollections.collectIterables(values.length + 2, new LongIterator.Single(-RAND.nextInt()), source);
    indexes = IntArray.create(0, values.length + 1);
    LongListRemovingDecorator.prepareSortedIndices(indexes);
    resArray = LongListRemovingDecorator.createFromPrepared(source, indexes);
    CHECK.order(expected, resArray);
    res.add(resArray);

    // ~[...]
    source = LongArray.copy(source);
    source.removeLast();
    indexes = IntArray.create(0);
    LongListRemovingDecorator.prepareSortedIndices(indexes);
    resArray = LongListRemovingDecorator.createFromPrepared(source, indexes);
    CHECK.order(expected, resArray);
    res.add(resArray);

    // [..~..]
    int pos = source.size() / 2;
    if (pos != 0) {
      source = LongArray.copy(values);
      source.insert(pos, -RAND.nextInt());
      indexes = IntArray.create(pos);
      LongListRemovingDecorator.prepareSortedIndices(indexes);
      resArray = LongListRemovingDecorator.createFromPrepared(source, indexes);
      CHECK.order(expected, resArray);
      res.add(resArray);
    }
    return res;
  }

  public void testSimpleCreateFromSorted() {
    LongList base = LongArray.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    WritableIntList indexes = IntArray.create(0, 2, 4, 6, 8);

    LongListRemovingDecorator.prepareSortedIndices(indexes);
    LongListRemovingDecorator arr = LongListRemovingDecorator.createFromPrepared(base, indexes);
    CHECK.order(arr.iterator(), 1, 3, 5, 7, 9);
  }

  public void testRandomCreateFromSorted() {
    int arrLength = 100;
    int indexesLength = 50;
    int maxValue = 1000;

    LongArray base = generateRandomLongArray(arrLength, false, maxValue);
    for (int test = 0; test < 20; test++) {
      IntArray indexes = generateRandomIntArray(indexesLength, true, arrLength);
      LongArray expected = LongArray.copy(base);
      expected.removeAllAtSorted(indexes);

      LongListRemovingDecorator.prepareSortedIndices(indexes);
      LongListRemovingDecorator arr = LongListRemovingDecorator.createFromPrepared(base, indexes);
      CHECK.order(arr.iterator(), expected.iterator());
    }
  }

  public void testIterator() {
    LongArray source = LongArray.create(10,13,15,14,11,12,16,17,18),
        expected = LongArray.create(10,15,14,12,16,18),
        result = new LongArray();
    IntArray indexes = IntArray.create(1, 4, 7);
    LongListRemovingDecorator.prepareSortedIndices(indexes);
    LongListRemovingDecorator tst2 =
        LongListRemovingDecorator.createFromPrepared(source, indexes);
    for (LongIterator i : tst2) {
      result.add(i.value());
    }
    assertEquals(expected, result);
  }

  public void testIteratorGet() {
    LongArray source = LongArray.create(10,13,15,14,11,12,16,17,18),
        expected = LongArray.create(10,15,14,12,16,18),
        result = new LongArray();
    IntArray indexes = IntArray.create(1, 4, 7);
    LongListRemovingDecorator.prepareSortedIndices(indexes);
    LongListRemovingDecorator tst2 =
        LongListRemovingDecorator.createFromPrepared(source, indexes);
    LongListIterator it = tst2.iterator(), expIt = expected.iterator();
    while(it.hasNext()) {
      assertEquals(it.nextValue(), expIt.nextValue());
    }
    assertEquals(it.hasNext(), expIt.hasNext());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(it.get(-i), expIt.get(-i));
    }
  }

  public void testCreate() {
    LongArray myArray = new LongArray();
    myArray.addAll(0, 1, 2, 3, 4, 5, 6);
    LongList array2 = LongArray.create(10, 11, 12, 13, 14, 15, 16);

    IntList removeIndices = LongListRemovingDecorator.prepareUnsortedIndices(0, 4, 1, 5);
    LongListRemovingDecorator rem = LongListRemovingDecorator.createFromPrepared(myArray, removeIndices);
    checkCollection(rem, 2, 3, 6);
    checkRemovedIndexes(rem, 0, 1, 4, 5);
    assertEquals(4, rem.getRemoveCount());
    rem = LongListRemovingDecorator.createFromPrepared(array2, removeIndices);
    checkCollection(rem, 12, 13, 16);
    checkRemovedIndexes(rem, 0, 1, 4, 5);
    assertEquals(4, rem.getRemoveCount());

    removeIndices = LongListRemovingDecorator.prepareUnsortedIndices(2, 6, 1);
    rem = LongListRemovingDecorator.createFromPrepared(myArray, removeIndices);
    checkCollection(rem, 0, 3, 4, 5);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());
    rem = LongListRemovingDecorator.createFromPrepared(array2, removeIndices);
    checkCollection(rem, 10, 13, 14, 15);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());

    removeIndices = LongListRemovingDecorator.prepareUnsortedIndices(2, 6, 1, 2);
    rem = LongListRemovingDecorator.createFromPrepared(myArray, removeIndices);
    checkCollection(rem, 0, 3, 4, 5);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());
    rem = LongListRemovingDecorator.createFromPrepared(array2, removeIndices);
    checkCollection(rem, 10, 13, 14, 15);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());
  }

  public void testSimple() {
    LongArray array = LongArray.create(0, 1, 2, 3, 4, 5, 6);
    IntArray indexes = IntArray.create(1, 3, 5);
    LongListRemovingDecorator.prepareSortedIndices(indexes);
    LongListRemovingDecorator rem = LongListRemovingDecorator.createFromPrepared(array, indexes);

    LongArray expected = LongArray.create(0, 2, 4, 6);
//    CHECK.order(expected, rem);
    LongListIterator it = rem.iterator();
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), rem.get(i));
      assertEquals(expected.get(i), it.nextValue());
    }
  }
}
