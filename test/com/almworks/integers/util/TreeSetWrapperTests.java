package com.almworks.integers.util;

import com.almworks.integers.LongList;
import com.almworks.integers.WritableLongSet;
import com.almworks.integers.WritableLongSetChecker;

public class TreeSetWrapperTests extends WritableLongSetChecker {
  protected WritableLongSet createSet() {
    return createSetWithCapacity(-1);
  }

  protected WritableLongSet createSetWithCapacity(int capacity) {
    return new TreeSetWrapper();
  }

  protected WritableLongSet[] createSetFromSortedList(LongList sortedList) {
    return new WritableLongSet[]{ new TreeSetWrapper(sortedList)};
  }
}
