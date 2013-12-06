package com.almworks.integers.wrappers;

import com.almworks.integers.*;
import com.almworks.integers.util.FindingLongIterator;
import com.carrotsearch.hppc.LongOpenHashSet;
import com.carrotsearch.hppc.cursors.LongCursor;

import java.util.Iterator;

public class HPPCLongOpenHashSet extends AbstractWritableLongSet implements WritableLongSet {
  LongOpenHashSet set;
  int myModCount = 0;

  public HPPCLongOpenHashSet() {
    set = new LongOpenHashSet();
  }

  public HPPCLongOpenHashSet(int initialCapacity) {
    set = new LongOpenHashSet(initialCapacity);
  }

  @Override
  public void clear() {
    set.clear();
  }

  @Override
  public boolean include0(long value) {
    return set.add(value);
  }

  @Override
  public boolean exclude0(long value) {
    return set.remove(value);
  }

  @Override
  public boolean contains(long value) {
    return set.contains(value);
  }

  @Override
  public int size() {
    return set.size();
  }

  public static HPPCLongOpenHashSet createFrom(LongList list) {
    HPPCLongOpenHashSet newSet = new HPPCLongOpenHashSet(list.size());
    newSet.addAll(list);
    return newSet;
  }

  @Override
  public LongArray toArray() {
    return new LongArray(set.toArray());
  }

  protected LongIterator iterator1() {
    return new FindingLongIterator() {
      Iterator<LongCursor> cursor = set.iterator();
      @Override
      protected boolean findNext() {
        if (!cursor.hasNext()) return false;
        myCurrent = cursor.next().value;
        return true;
      }
    };
  }
}