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
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;
import static com.almworks.integers.LongProgression.range;
import static com.almworks.integers.util.LongListRemovingDecorator.createFromPrepared;
import static com.almworks.integers.util.LongListRemovingDecorator.prepareSortedIndices;


public class LongListRemovingDecoratorTests extends LongListChecker {

  @Override
  protected List<? extends LongList> createLongListVariants(long... values) {
    LongArray expected = LongArray.copy(values);
    List<LongList> res = new ArrayList<LongList>();

    // [...]
    LongArray source = LongArray.copy(values);
    LongList resArray = new LongListRemovingDecorator(source);
    CHECK.order(resArray, values);
    res.add(resArray);

    if (values.length == 0) return res;

    // [...]~
    source = LongArray.copy(values);
    source.add(-RAND.nextInt());
    IntArray indices = IntArray.create(values.length);
    prepareSortedIndices(indices);
    resArray = createFromPrepared(source, indices);
    CHECK.order(resArray, values);
    res.add(resArray);

    // ~[...]~
    source = LongCollections.collectIterables(values.length + 2, new LongIterator.Single(-RAND.nextInt()), source);
    indices = IntArray.create(0, values.length + 1);
    prepareSortedIndices(indices);
    resArray = createFromPrepared(source, indices);
    CHECK.order(resArray, values);
    res.add(resArray);

    // ~[...]
    source = LongArray.copy(source);
    source.removeLast();
    indices = IntArray.create(0);
    prepareSortedIndices(indices);
    resArray = createFromPrepared(source, indices);
    CHECK.order(resArray, values);
    res.add(resArray);

    // [..~..]
    int pos = source.size() / 2;
    if (pos != 0) {
      source = LongArray.copy(values);
      source.insert(pos, -RAND.nextInt());
      indices = IntArray.create(pos);
      prepareSortedIndices(indices);
      resArray = createFromPrepared(source, indices);
      CHECK.order(resArray, values);
      res.add(resArray);
    }

    // random removes
    if (4 < values.length && values.length < 100) {
      int count = 4;
      for (int i = 0; i < count; i++) {
        source = LongArray.copy(values);
        indices = IntArray.create();
        int maxDiff = 4;
        int curIdx = RAND.nextInt(maxDiff);
        while (curIdx < source.size()) {
          indices.add(curIdx);
          source.insert(curIdx, RAND.nextInt());
          curIdx += 1 + RAND.nextInt(maxDiff);
        }
        prepareSortedIndices(indices);
        resArray = createFromPrepared(source, indices);
        CHECK.order(resArray, values);
        res.add(resArray);
      }
    }
    return res;
  }

  public void testSimpleCreateFromSorted() {
    LongList base = LongArray.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    WritableIntList indexes = IntArray.create(0, 2, 4, 6, 8);

    prepareSortedIndices(indexes);
    LongListRemovingDecorator arr = createFromPrepared(base, indexes);
    CHECK.order(arr.iterator(), 1, 3, 5, 7, 9);
  }

  public void testRandomCreateFromSorted() {
    int arrLength = 100;
    int indexesLength = 50;
    int maxValue = 1000;

    LongArray base = generateRandomLongArray(arrLength, UNORDERED, maxValue);
    for (int test = 0; test < 20; test++) {
      IntArray indexes = generateRandomIntArray(indexesLength, SORTED_UNIQUE, arrLength);
      LongArray expected = LongArray.copy(base);
      expected.removeAllAtSorted(indexes.iterator());

      prepareSortedIndices(indexes);
      LongListRemovingDecorator arr = createFromPrepared(base, indexes);
      CHECK.order(arr.iterator(), expected.iterator());
    }
  }

  public void testIterator() {
    LongArray source = LongArray.create(10,13,15,14,11,12,16,17,18),
      expected = LongArray.create(10,15,14,12,16,18),
      result = new LongArray();
    IntArray indexes = IntArray.create(1, 4, 7);
    prepareSortedIndices(indexes);
    LongListRemovingDecorator tst2 = createFromPrepared(source, indexes);
    result.addAll(tst2.iterator());
    assertEquals(expected, result);
  }

  public void testIteratorGet() {
    LongArray source = LongArray.create(10, 13, 15, 14, 11, 12, 16, 17, 18),
      expected = LongArray.create(10, 15, 14, 12, 16, 18);
    IntArray indexes = IntArray.create(1, 4, 7);
    prepareSortedIndices(indexes);
    LongListRemovingDecorator tst2 =
      createFromPrepared(source, indexes);
    LongListIterator it = tst2.iterator(), expIt = expected.iterator();
    while (it.hasNext()) {
      assertEquals(it.nextValue(), expIt.nextValue());
    }
    assertEquals(it.hasNext(), expIt.hasNext());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(it.get(-i), expIt.get(-i));
    }
  }

  public void testCompound() {
    long[][] valuesArray = {{0, 1, 2, 3, 4, 5, 6}, {10, 11, 12, 13, 14, 15, 16}};
    int[][] indicesToRemove = {{0, 4, 1, 5}, {2, 6, 1}, {0, 6, 1, 2}};

    for (long[] values : valuesArray) {
      LongArray array = LongArray.create(values);
      for (int[] indices : indicesToRemove) {
        IntList removedIndices = LongListRemovingDecorator.prepareUnsortedIndices(indices);
        LongListRemovingDecorator rem = createFromPrepared(array, removedIndices);

        LongArray expected = LongArray.copy(values);
        removedIndices = IntCollections.toSorted(false, new IntArray(indices));
        expected.removeAllAtSorted(removedIndices);
        CHECK.order(expected, rem);

        checkRemovedIndexes(rem, removedIndices.toNativeArray());
        assertEquals(indices.length, rem.getRemoveCount());
      }
    }
  }

  public void testSimple() {
    LongArray array = LongArray.create(0, 1, 2, 3, 4, 5, 6);
    IntArray indexes = IntArray.create(1, 3, 5);
    prepareSortedIndices(indexes);
    LongListRemovingDecorator rem = createFromPrepared(array, indexes);

    LongArray expected = LongArray.create(0, 2, 4, 6);
    CHECK.order(expected, rem);
    LongListIterator it = rem.iterator();
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), rem.get(i));
      assertEquals(expected.get(i), it.nextValue());
    }
  }

  public void testGetSimple() {
    IntArray prepared = IntArray.create(2);
    prepareSortedIndices(prepared);
    LongListRemovingDecorator rem = createFromPrepared(range(0, 4), prepared);
    LongListIterator it = rem.iterator();
    it.next();
    assertEquals(3, it.get(2));
    it.next();
    assertEquals(3, it.get(1));
  }
}
