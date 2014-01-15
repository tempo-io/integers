package com.almworks.integers.wrappers;

import com.almworks.integers.LongIterator;
import com.almworks.integers.util.FindingLongIterator;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.LongCursor;

import java.util.Iterator;

public class LongHppcWrappers {
  public static LongIterator cursorToLongIterator(final Iterator<LongCursor> cursor) {
    return new FindingLongIterator() {
      @Override
      protected boolean findNext() {
        if (!cursor.hasNext()) return false;
        myCurrent = cursor.next().value;
        return true;
      }
    };

  }
}
