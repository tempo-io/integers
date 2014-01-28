package com.almworks.integers.util;

import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;

public class LongUnionIteratorTwo extends FindingLongIterator {
  private final LongIterator myIts[] = new LongIterator[2];
  private int myItsCount = 2;
  private long[] v = {0, 0};

  public LongUnionIteratorTwo(LongIterator first, LongIterator second) {
    myIts[0] = first;
    myIts[1] = second;
    advanceIterator(0);
    advanceIterator(1);
    maybeMoveIterator1();
  }

  public static LongUnionIteratorTwo create(LongIterable first, LongIterable second) {
    return new LongUnionIteratorTwo(first.iterator(), second.iterator());
  }

  protected boolean findNext() {
    if (myItsCount == 0) return false;
    if (myItsCount == 2) {
      if (v[0] < v[1]) {
        myCurrent = v[0];
        advanceIterator(0);
      } else if (v[1] < v[0]) {
        myCurrent = v[1];
        advanceIterator(1);
      } else {
        myCurrent = v[0];
        advanceIterator(0);
        advanceIterator(1);
      }
      maybeMoveIterator1();
      return true;
    } else {
      myCurrent = myIts[0].value();
      advanceIterator(0);
      return true;
    }
  }

  private void maybeMoveIterator1() {
    if (myIts[0] == null) {
      myIts[0] = myIts[1];
      v[0] = v[1];
    }
  }

  private void advanceIterator(int ind) {
    if (myIts[ind].hasNext()) {
      v[ind] = myIts[ind].nextValue();
    } else {
      myIts[ind] = null;
      myItsCount--;
    }
  }
}