package com.almworks.integers.util;

import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;
import com.almworks.integers.util.FindingLongIterator;

public class LongUnionIteratorTwo extends FindingLongIterator {
  private final LongIterator myIts[] = new LongIterator[2];
  private int myItsCount;
  private long[] v = {0, 0};

  public LongUnionIteratorTwo(LongIterator first, LongIterator second) {
    if (first.hasNext()) myIts[myItsCount++] = first;
    if (second.hasNext()) myIts[myItsCount++] = second;
    for (int i = 0; i < myItsCount; i++) {
      switchIterator(i);
      v[i] = myIts[i].value();
    }
  }

  public static LongUnionIteratorTwo create(LongIterable include, LongIterable exclude) {
    return new LongUnionIteratorTwo(include.iterator(), exclude.iterator());
  }

  protected boolean findNext() {
    if (myItsCount == 0) return false;
    if (myItsCount == 2) {
      if (v[0] <= v[1]) {
        myCurrent = v[0];
        if (v[0] == v[1]) switchIterator(1);
        switchIterator(0);
      } else {
        myCurrent = v[1];
        switchIterator(1);
      }
      if (myIts[0] == null) {
        myIts[0] = myIts[1];
        v[0] = v[1];
      }
      return true;
    }
    myCurrent = myIts[0].value();
    switchIterator(0);
    return true;
  }

  private void switchIterator(int ind) {
    if (myIts[ind].hasNext()) {
      v[ind] = myIts[ind].nextValue();
    } else {
      myIts[ind] = null;
      myItsCount--;
    }
  }
}