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

import com.almworks.integers.segmented.LongSegmentedArray;

import java.util.ArrayList;
import java.util.List;

public class LongListConcatenationTests extends LongListChecker<LongListConcatenation> {

  @Override
  protected List<LongListConcatenation> createLongListVariants(long... values) {
    List<LongListConcatenation> res = new ArrayList();

    // []
    LongArray array = new LongArray(values);
    res.add(new LongListConcatenation(array));
    int len2 = values.length / 2;
    if (values.length == 0 || len2 == 0) return res;

    // [][]
    LongList[] lists = {new LongArray(array.subList(0, len2)), new LongArray(array.subList(len2, array.size()))};
    res.add(new LongListConcatenation(lists));

    LongListConcatenation concatenation = new LongListConcatenation();
    concatenation.addSlice(lists[0]);
    concatenation.addSlice(lists[1]);
    res.add(concatenation);

    concatenation = new LongListConcatenation();
    concatenation.addSlice(LongList.EMPTY);
    concatenation.addSlice(lists[0]);
    concatenation.addSlice(LongList.EMPTY);
    concatenation.addSlice(lists[1]);
    concatenation.addSlice(LongList.EMPTY);
    concatenation.addSlice(LongList.EMPTY);
    res.add(concatenation);

    // [][][][]...[]
    concatenation = new LongListConcatenation();
    for (long value : values) {
      concatenation.addSlice(LongArray.create(value));
      if (myRand.nextInt(5) == 0) {
        concatenation.addSlice(LongList.EMPTY);
      }
    }
    res.add(concatenation);
    return res;
  }

  public void testConcatUnmodifiable() {
    LongList[] res = {LongArray.create(0, 1, 2), LongArray.create(3, 4), LongArray.create(5)};

    LongList concat = LongListConcatenation.concatUnmodifiable(res);
    LongArray expected = LongArray.create(0, 1, 2, 3, 4, 5);
    CHECK.order(expected, concat);

    concat = LongListConcatenation.concatUnmodifiable(res[0]);
    expected = LongArray.create(0, 1, 2);
    CHECK.order(expected, concat);

    concat = LongListConcatenation.concatUnmodifiable();
    CHECK.order(LongArray.create(), concat);
  }

  public void testGet2() {
    LongList[] arrays = {LongArray.create(0, 1, 2), LongArray.create(3, 4), LongArray.create(5)};
    LongListConcatenation res = new LongListConcatenation(arrays);

    assertEquals(6, res.size());
    assertEquals(3, res.getSliceCount());
    for(int i = 0; i < 6; i++) {
      assertEquals(i, res.get(i));
    }
  }

  public void testIndexOfIsEmptyIterator() {
    LongListConcatenation res = new LongListConcatenation();
    assertTrue(res.isEmpty());

    LongList[] arrays = {LongArray.create(5, 4, 3), LongArray.create(2, 1), LongArray.create(0)};
    for (LongList arr : arrays) {
      res.addSlice(arr);
    }
    assertFalse(res.isEmpty());

    for ( int i = 0; i < 6; i++) {
      assertEquals(5 - i, res.indexOf(i));
    }

    CHECK.order(LongArray.create(5, 4, 3, 2).iterator(), res.iterator(0, 4));
  }

  public void testSimple() {
    LongArray myArray = new LongArray();
    LongListConcatenation concat = new LongListConcatenation();
    assertEquals(0, concat.getSliceCount());
    checkCollection(concat);
    concat.addSlice(LongList.EMPTY);
    assertEquals(1, concat.getSliceCount());
    checkCollection(concat);
    concat.addSlice(myArray);
    checkCollection(concat);
    myArray.add(1);
    checkCollection(concat, 1);
    concat.addSlice(LongList.EMPTY);
    checkCollection(concat, 1);
    LongSegmentedArray segarray = new LongSegmentedArray();
    concat.addSlice(segarray);
    concat.addSlice(LongList.EMPTY);
    checkCollection(concat, 1);
    segarray.add(3);
    checkCollection(concat, 1, 3);
    myArray.add(2);
    checkCollection(concat, 1, 2, 3);
  }
}
