package com.almworks.integers.wrappers;

import com.almworks.integers.IntLongFindingIterator;
import com.carrotsearch.hppc.cursors.IntLongCursor;

import java.util.Iterator;

public class IntLongHppcWrappers {
  public static IntLongFindingIterator cursorToIntLongIterator(final Iterator<IntLongCursor> cursor) {
    return new IntLongFindingIterator() {
      @Override
      protected boolean findNext() {
        if (!cursor.hasNext()) return false;
        IntLongCursor cur = cursor.next();
        myCurrentLeft = cur.key;
        myCurrentRight = cur.value;
        return true;
      }
    };

  }
}
