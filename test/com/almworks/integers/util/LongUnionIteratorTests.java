package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import com.almworks.integers.SetOperationsChecker;

import java.util.ArrayList;
import java.util.List;

public class LongUnionIteratorTests extends IntegersFixture {
  public void testCreate() {
    List<LongArray> list = new ArrayList<LongArray>();
    list.add(LongArray.create(1, 2, 5, 10));
    list.add(LongArray.create(-3, 2, 3, 4, 11));
    LongArray union = new LongArray(LongUnionIterator.create(list));
    CHECK.order(union, -3, 1, 2, 3, 4, 5, 10, 11);
  }

  public void testAllCases() {
    new SetOperationsChecker().check(new SetOperationsChecker.newSetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        LongArray res = new LongArray(new LongUnionIterator(arrays));
        return res.iterator();
      }
    }, new SetOperationsChecker.UnionGetter(), true, false);
  }

}
