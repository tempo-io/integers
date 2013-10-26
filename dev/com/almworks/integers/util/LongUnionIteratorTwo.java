package com.almworks.integers.util;

import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;

public class LongUnionIteratorTwo extends FindingLongIterator {
  private final LongIterator[] myIts = new LongIterator[2];
  private int myItsCount;

  public LongUnionIteratorTwo(LongIterator first, LongIterator second) {
    if (first.hasNext()) myIts[myItsCount++] = first;
    if (second.hasNext()) myIts[myItsCount++] = second;
    for (int i = 0; i < myItsCount; i++) {
      switchIterator(i);
    }
  }

  public static LongUnionIteratorTwo create(LongIterable first, LongIterable second) {
    return new LongUnionIteratorTwo(first.iterator(), second.iterator());
  }

  protected boolean findNext() {
    if (myItsCount == 0) return false;
    if (myItsCount == 2) {
      long vi = myIts[0].value();
      long vj = myIts[1].value();
      if (vi <= vj) {
        myCurrent = vi;
        switchIterator(0);
        if (vi == vj) switchIterator(1);
      } else {
        myCurrent = vj;
        switchIterator(1);
      }
      if (myIts[0] == null) myIts[0] = myIts[1];
      return true;
    }
    myCurrent = myIts[0].value();
    switchIterator(0);
    return true;
  }

  private void switchIterator(int ind) {
    if (myIts[ind].hasNext()) {
      myIts[ind].next();
    } else {
      myIts[ind] = null;
      myItsCount--;
    }
  }
}