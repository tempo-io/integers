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


public class WritableLongListRemovingDecoratorTests extends LongListChecker {

  @Override
  protected List<? extends LongList> createLongListVariants(long... values) {
    List<LongList> res = new ArrayList<LongList>();
    WritableLongListRemovingDecorator array;

    // [...]
    LongArray source = LongArray.copy(values);
    res.add(new WritableLongListRemovingDecorator(source));

    // [...]~
    source = LongArray.copy(source);
    source.add(RAND.nextInt());
    array = new WritableLongListRemovingDecorator(source);
    array.removeAt(values.length);
    res.add(array);

    // ~[...]~
    source = LongCollections.collectIterables(values.length + 1, new LongIterator.Single(RAND.nextInt()), source);
    array = new WritableLongListRemovingDecorator(source);
    array.removeAt(0);
    array.removeAt(values.length + 1);
    res.add(array);

    // ~[...]
    source = LongArray.copy(source);
    source.removeLast();
    array = new WritableLongListRemovingDecorator(source);
    array.removeAt(0);
    res.add(array);

    // [..~..]
    source = LongArray.copy(values);
    int pos = source.size() / 2;
    source.insert(pos, RAND.nextInt());
    if (pos != 0) {
      array = new WritableLongListRemovingDecorator(source);
      array.removeAt(pos);
      res.add(array);
    }
    return res;
  }

  public void testSimpleCreateFromSorted() {
    LongList base = LongArray.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    WritableIntList indexes = IntArray.create(0, 2, 4, 6, 8);
    WritableLongListRemovingDecorator arr = WritableLongListRemovingDecorator.createFromSorted(base, indexes);
    CHECK.order(arr.iterator(), 1, 3, 5, 7, 9);
  }

  public void testSimpleCreateFromSorted2() {
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
    LongList base = generateRandomLongArray(arrLength, IntegersFixture.SortedStatus.UNORDERED, maxValue);

    for (int test = 0; test < 20; test++) {
      IntArray indexes = generateRandomIntArray(indexesLength, SortedStatus.SORTED_UNIQUE, arrLength);
      LongArray expected = LongArray.copy(base);
      for (int i = indexes.size() - 1; i >= 0; i--) {
        int val = indexes.get(i);
        expected.removeRange(val, val + 1);
      }
      WritableLongListRemovingDecorator arr = WritableLongListRemovingDecorator.createFromSorted(base, indexes);
      CHECK.order(arr.iterator(), expected.iterator());
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

  public void testCreate() {
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

  public void testSimple() {
    LongArray array = LongArray.create(0, 1, 2, 3, 4, 5, 6);
    LongListRemovingDecorator rem = LongListRemovingDecorator.createFromPrepared(array, IntArray.create(1, 2, 3));
//    LongListRemovingDecorator rem = LongListRemovingDecorator.prepareSortedIndices(IntArray.create(1, 3, 5));
    LongArray expected = LongArray.create(0, 2, 4, 6);
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), rem.get(i));
    }
    CHECK.order(expected, rem);
    LongListIterator it = rem.iterator();
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), it.nextValue());
    }
  }
}
