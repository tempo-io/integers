package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ChainHashLongSet implements WritableLongSet {
  private final int[] myHead;
  private final int[] myNext;
  private final long[] myKeys;
  private int headNum;
  private int cnt = 1;

  public ChainHashLongSet(int headNum, int maxSize) {
    this.headNum = headNum;
    myHead = new int[headNum];
    myNext = new int[maxSize + 1];
    myKeys = new long[maxSize + 1];
  }

  public boolean include(long value) {
    if (contains(value)) return false;
    int h = index(hash(value));
    myNext[cnt] = myHead[h];
    myKeys[cnt] = value;
    myHead[h] = cnt++;
    return true;
  }

  @Override
  public boolean exclude(long value) {
    int h = index(hash(value));
    int i = myHead[h];
    if (myKeys[i] == value) {
      myHead[h] = myNext[i];
      return true;
    }
    int prev;
    for (prev = i, i = myNext[i]; i != 0; i = myNext[i]) {
      if (myKeys[i] == value) {
        myNext[prev] = myNext[i];
        return true;
      }
    }
    return false;
  }

  public void addAll(LongList values) {
    addAll(values.iterator());
  }

  public void addAll(LongIterator iterator) {
    while (iterator.hasNext())
      add(iterator.nextValue());
  }

  public void addAll(long... values) {
    if (values != null && values.length != 0) {
      if (values.length == 1) {
        add(values[0]);
      } else {
        addAll(new LongArray(values));
      }
    }
  }

  public boolean contains(long value) {
    int h = index(hash(value));
    for (int i = myHead[h]; i != 0; i = myNext[i])
      if (myKeys[i] == value) return true;
    return false;
  }

  private int hash(long value) {
    return ((int)(value ^ (value >>> 32))) + 1;
  }

  private int index(int hash) {
    return (hash > 0 ? hash : -hash) % headNum;
  }

  @Override
  public LongList toList() {
    return toArray();
  }

  @Override
  public LongArray toArray() {
    LongArray res = new LongArray();
    for (int head = 0; head < headNum; head++) {
      for(int i = myHead[head]; i != 0; i = myNext[i]) {
        res.add(myKeys[i]);
      }
    }
    return res;
  }

  @Override
  public boolean isEmpty() {
    return cnt == 1;
  }

  // todo wrong
  @Override
  public int size() {
    return cnt - 1;
  }

  @Override
  @NotNull
  public LongIterator iterator() {
    return toArray().iterator();
  }

  @Override
  public boolean containsAll(LongIterable iterable) {
    for (LongIterator it : iterable.iterator()) {
      if (!contains(it.value())) return false;
    }
    return true;
  }

  public void clear() {
    cnt = 1;
    Arrays.fill(myHead, 0);
  }

  @Override
  public void remove(long value) {
    exclude(value);
  }

  public void removeAll(long ... values) {
    for (long value : values) {
      remove(value);
    }
  }

  public void removeAll(LongList values) {
    removeAll(values.iterator());
  }

  public void removeAll(LongIterator iterator) {
    for (LongIterator it: iterator) {
      remove(it.value());
    }
  }

  @Override
  public WritableLongSet retain(LongList values) {
    return null;
  }

  @Override
  public void add(long value) {
    include(value);
  }
}
