package com.almworks.integers.wrappers;

import com.almworks.integers.IntIterator;
import com.almworks.integers.LongIterator;
import com.almworks.integers.util.FindingIntIterator;
import com.almworks.integers.util.FindingLongIterator;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.LongCursor;

import java.util.Iterator;

public class IntHppcWrappers {
  public static IntIterator intCursorToIterator(final Iterator<IntCursor> cursor) {
    return new FindingIntIterator() {
      int myCurrent = Integer.MAX_VALUE;
      @Override
      protected int getNext() {
        return myCurrent;
      }

      @Override
      protected boolean findNext() {
        if (!cursor.hasNext()) return false;
        myCurrent = cursor.next().value;
        return true;
      }
    };

  }
}
