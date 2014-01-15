package com.almworks.integers.wrappers;

import com.almworks.integers.LongIterator;
import com.almworks.integers.util.FindingIntLongIterator;
import com.almworks.integers.util.FindingLongIterator;
import com.carrotsearch.hppc.cursors.IntLongCursor;
import com.carrotsearch.hppc.cursors.LongCursor;

import java.util.Iterator;

public class IntLongHppcWrappers {
  public static FindingIntLongIterator cursorToIntLongIterator(final Iterator<IntLongCursor> cursor) {
    return new FindingIntLongIterator() {
      @Override
      protected boolean findNext() {
        if (!cursor.hasNext()) return false;
        myCurrentLeft = cursor.next().key;
        myCurrentRight = cursor.next().value;
        return true;
      }
    };

  }
}
