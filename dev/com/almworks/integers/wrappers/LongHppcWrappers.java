package com.almworks.integers.wrappers;

import com.almworks.integers.LongFindingIterator;
import com.almworks.integers.LongIterator;
import com.carrotsearch.hppc.cursors.LongCursor;

import java.util.Iterator;

public class LongHppcWrappers {
  public static LongIterator cursorToLongIterator(final Iterator<LongCursor> cursor) {
    return new LongFindingIterator() {
      @Override
      protected boolean findNext() {
        if (!cursor.hasNext()) return false;
        myNext = cursor.next().value;
        return true;
      }
    };

  }
}
