package com.almworks.integers.util;

import com.almworks.integers.LongList;
import com.almworks.integers.WritableLongSet;
import com.almworks.integers.WritableLongSetChecker;

public class LongTreeSetWrapperTests extends WritableLongSetChecker {
  protected boolean isSupportTailIterator() {
    return true;
  }

  protected WritableLongSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new LongTreeSetWrapper();
  }

  protected WritableLongSet[] createSetFromSortedList(LongList sortedList) {
    return new WritableLongSet[]{ new LongTreeSetWrapper(sortedList)};
  }
}
