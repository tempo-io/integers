package com.almworks.integers.util;

import com.almworks.integers.*;

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
    new SetOperationsChecker().check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        LongArray res = new LongArray(new LongUnionIterator(arrays));
        return res.iterator();
      }
    }, new SetOperationsChecker.UnionGetter(), true, false);
  }

  public void testSimple() {
    LongIterator it1 = LongArray.create(0, 1, 2).iterator();
    LongIterator empty = LongIterator.EMPTY;
    LongIterator minus = new LongMinusIterator(it1, empty);
    assertFalse(minus.hasValue());
    assertTrue(minus.hasNext());
    minus.next();
    assertEquals(0, minus.value());
    assertTrue(minus.hasNext());
    assertEquals(0, minus.value());

    assertEquals(1, minus.nextValue());
    assertTrue(minus.hasNext());
    assertEquals(1, minus.value());
  }
}
