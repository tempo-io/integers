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
  protected List<LongList> createLongListVariants(long... values) {
    List<LongList> res = new ArrayList<LongList>();

    // [...]
    LongArray array = LongArray.copy(values);
    res.add(new WritableLongListRemovingDecorator(array));

    // [...]~
    array = LongArray.copy(array);
    array.add(RAND.nextInt());
    res.add(WritableLongListRemovingDecorator.createFromSorted(array, IntArray.create(array.size() - 1)));

    // ~[...]~
    array = LongCollections.collectIterables(values.length + 1, new LongIterator.Single(RAND.nextInt()), array);
    res.add(WritableLongListRemovingDecorator.createFromSorted(array, IntArray.create(0, array.size() - 1)));

    // ~[...]
    array = LongArray.copy(array);
    array.removeLast();
    res.add(WritableLongListRemovingDecorator.createFromSorted(array, IntArray.create(0)));

    // [..~..]
    array = LongArray.copy(values);
    int pos = array.size() / 2;
    array.insert(pos, RAND.nextInt());
    if (pos != 0) {
      res.add(WritableLongListRemovingDecorator.createFromSorted(array, IntArray.create(pos)));
    }
    return res;
  }

  public void _testSimpleCreateFromSorted() {
    LongList base = LongArray.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    WritableIntList indexes = IntArray.create(0, 2, 4, 6, 8);
    WritableLongListRemovingDecorator arr = WritableLongListRemovingDecorator.createFromSorted(base, indexes);
    CHECK.order(arr.iterator(), 1, 3, 5, 7, 9);
  }

  public void _testRandomCreateFromSorted() {
    int arrLength = 100;
    int indexesLength = 50;
    int maxValue = 1000;

    long[] values = new long[arrLength];
    for (int i = 0; i < arrLength; i++) {
      values[i] = RAND.nextInt(maxValue);
    }
    LongList base = LongArray.create(values);

    int[] nativeIndexes = new int[arrLength];
    for (int test = 0; test < 20; test++) {
      for (int i = 0; i < indexesLength; i++) {
        nativeIndexes[i] = RAND.nextInt(arrLength);
      }
      IntArray indexes = IntArray.create(nativeIndexes);
      indexes.sortUnique();

      LongArray expected = LongArray.copy(base);
      for (int i = indexes.size() - 1; i >= 0; i--) {
        int val = indexes.get(i);
        expected.removeRange(val, val + 1);
      }
      WritableLongListRemovingDecorator arr = WritableLongListRemovingDecorator.createFromSorted(base, indexes);
      CHECK.order(arr.iterator(), expected.iterator());
    }
  }

  public void testCreateFromSorted() {
    _testSimpleCreateFromSorted();
    _testRandomCreateFromSorted();
  }

  public void testIterator() {
    LongArray source = LongArray.create(10,13,15,14,11,12,16,17,18),
        expected = LongArray.create(10, 15, 14, 12, 16, 18),
        result = new LongArray();
    LongListRemovingDecorator tst2 =
        LongListRemovingDecorator.createFromPrepared(source, IntArray.create(1, 3, 5));
    for (LongIterator i : tst2) {
      result.add(i.value());
    }
    assertEquals(expected, result);
  }

  public void testIteratorGet() {
    LongArray source = LongArray.create(10,13,15,14,11,12,16,17,18),
        expected = LongArray.create(10, 15, 14, 12, 16, 18),
        result = new LongArray();
    LongListRemovingDecorator tst2 =
        LongListRemovingDecorator.createFromPrepared(source, IntArray.create(1, 3, 5));
    LongListIterator it = tst2.iterator(), expIt = expected.iterator();
    while(it.hasNext()) {
      assertEquals(it.nextValue(), expIt.nextValue());
    }
    assertEquals(it.hasNext(), expIt.hasNext());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(it.get(-i), expIt.get(-i));
    }
  }

  public void testRemoveDecorator() {
    LongArray myArray = new LongArray();
    WritableLongListRemovingDecorator rem = new WritableLongListRemovingDecorator(myArray);
    checkCollection(rem);
    checkRemovedIndexes(rem);
    CHECK.order(rem.removedValueIterator());
    assertEquals(0, rem.getRemoveCount());

    myArray.add(-1);
    checkCollection(rem, -1);
    checkRemovedIndexes(rem);
    CHECK.order(rem.removedValueIterator());
    assertEquals(0, rem.getRemoveCount());

    rem.removeAt(0);
    checkCollection(rem);
    CHECK.order(rem.removedValueIterator(), -1);
    checkRemovedIndexes(rem, 0);
    assertEquals(1, rem.getRemoveCount());

    myArray.addAll(0, 1, 2, 3, 4, 5, 6);
    checkRemovedIndexes(rem, 0);
    rem.removeAt(6);
    checkCollection(rem, 0, 1, 2, 3, 4, 5);
    CHECK.order(rem.removedValueIterator(), -1, 6);
    checkRemovedIndexes(rem, 0, 7);
    assertEquals(2, rem.getRemoveCount());

    rem.removeAt(0);
    checkCollection(rem, 1, 2, 3, 4, 5);
    CHECK.order(rem.removedValueIterator(), -1, 0, 6);
    checkRemovedIndexes(rem, 0, 1, 7);
    assertEquals(3, rem.getRemoveCount());

    rem.removeAt(2);
    checkCollection(rem, 1, 2, 4, 5);
    CHECK.order(rem.removedValueIterator(), -1, 0, 3, 6);
    checkRemovedIndexes(rem, 0, 1, 4, 7);
    assertEquals(4, rem.getRemoveCount());

    rem.removeAt(0);
    checkCollection(rem, 2, 4, 5);
    CHECK.order(rem.removedValueIterator(), -1, 0, 1, 3, 6);
    checkRemovedIndexes(rem, 0, 1, 2, 4, 7);
    assertEquals(5, rem.getRemoveCount());
  }

  public void testCreateModifyingRemoveDecorator() {
    LongArray myArray = new LongArray();
    myArray.addAll(0, 1, 2, 3, 4, 5, 6);
    WritableLongListRemovingDecorator rem = WritableLongListRemovingDecorator.createFromUnsorted(myArray, 0, 4, 1, 5);
    checkCollection(rem, 2, 3, 6);
    checkRemovedIndexes(rem, 0, 1, 4, 5);
    assertEquals(4, rem.getRemoveCount());

    rem = WritableLongListRemovingDecorator.createFromUnsorted(myArray, 2, 6, 1);
    checkCollection(rem, 0, 3, 4, 5);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());

    rem = WritableLongListRemovingDecorator.createFromUnsorted(myArray, 2, 6, 1, 2);
    checkCollection(rem, 0, 3, 4, 5);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());
  }

  public void testCreateReadonlyRemoveDecorator() {
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
}
