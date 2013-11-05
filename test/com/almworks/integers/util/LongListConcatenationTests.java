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
import com.almworks.integers.LongList;
import com.almworks.integers.LongListChecker;

import java.util.ArrayList;
import java.util.List;

public class LongListConcatenationTests extends LongListChecker {

  @Override
  protected List<LongList> createLongListVariants(long... values) {
    List<LongList> res = new ArrayList<LongList>();

    // []
    LongArray array = new LongArray(values);
    res.add(new LongListConcatenation(array));
    int len2 = values.length / 2;
    if (values.length == 0 || len2 == 0) return res;

    // [][]
    res.add(new LongListConcatenation(new LongArray(array.subList(0, len2)), new LongArray(array.subList(len2, array.size()))));

    // [][][][]...[]
    LongList[] lists = new LongList[values.length];
    for (int i = 0; i < values.length; i++) {
      lists[i] = LongArray.create(values[i]);
    }
    res.add(new LongListConcatenation(lists));
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

  public void testGet() {
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
}
