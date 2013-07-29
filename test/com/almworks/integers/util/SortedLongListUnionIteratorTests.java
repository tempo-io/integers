package com.almworks.integers.util;

import com.almworks.integers.*;

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.LongArray.create;

public class SortedLongListUnionIteratorTests extends IntegersFixture {
  public void testCreate() {
    List<LongArray> list = new ArrayList<LongArray>();
    list.add(LongArray.create(1, 2, 5, 10));
    list.add(LongArray.create(-3, 2, 3, 4, 11));
    LongArray union = new LongArray(SortedLongListUnionIterator.create(list));
    CHECK.order(union, -3, 1, 2, 3, 4, 5, 10, 11);
  }

  public void testAllCases() {
    SetOperationsChecker.testSetOperations(new SetOperationsChecker.newSetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        LongArray res = new LongArray(new SortedLongListUnionIterator(arrays));
        return res.iterator();
      }
    }, new SetOperationsChecker.UnionGetter(), false);
  }

}
