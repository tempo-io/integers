package com.almworks.integers;

import java.util.Comparator;

public class LongIterableLexicographicComparator implements Comparator<LongIterable> {
  public static final LongIterableLexicographicComparator LONG_ITERABLE_LEXICOGRAPHIC_COMPARATOR = new LongIterableLexicographicComparator();

  @Override
  public int compare(LongIterable l1, LongIterable l2) {
    if (l1 == l2) return 0;
    if (l1 == null) return -1;
    if (l2 == null) return 1;
    LongIterator i1 = l1.iterator();
    LongIterator i2 = l2.iterator();
    while(i1.hasNext() && i2.hasNext()) {
      int comp = LongCollections.compare(i1.nextValue(), i2.nextValue());
      if (comp != 0) return comp;
    }
    if (i1.hasNext()) return 1;
    if (i2.hasNext()) return -1;
    return 0;
  }
}
