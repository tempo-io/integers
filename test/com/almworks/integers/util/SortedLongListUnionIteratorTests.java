package com.almworks.integers.util;

import com.almworks.integers.IntArray;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import com.almworks.integers.NativeIntFixture;

import static com.almworks.integers.LongArray.create;

public class SortedLongListUnionIteratorTests extends NativeIntFixture {

  public void templateCase(LongArray a, LongArray b, long ... expected) {

    SortedLongListUnionIterator res = SortedLongListUnionIterator.create(a.iterator(), b.iterator());
    CHECK.order(res, expected);
  }

  public void testSimpleCase() {
    templateCase(create(1, 3, 5, 7),
                 create(2, 3, 4, 6, 100), 1, 2, 3, 4, 5, 6, 7, 100);
  }

  public void testComplexCase() {

  }

}
