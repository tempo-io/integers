package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ChainHashLongSet extends AbstractWritableLongSet implements WritableLongSet {
  private int[] myHead;
  private int[] myNext;
  private long[] myKeys;
  private int headNum;
  private int cnt = 1;
  private int myModCount = 0;

  public ChainHashLongSet(int headNum, int maxSize) {
    this.headNum = headNum;
    myHead = new int[headNum];
    myNext = new int[maxSize + 1];
    myKeys = new long[maxSize + 1];
  }

  private void modified() {
    myModCount++;
  }

  // todo optimize(now effective only when size() < headNum)
  public boolean include(long value) {
    myKeys = LongCollections.ensureCapacity(myKeys, cnt + 1);
    myNext = IntCollections.ensureCapacity(myNext, cnt + 1);
    modified();
    if (contains(value)) return false;
    int h = index(hash(value));
    myNext[cnt] = myHead[h];
    myKeys[cnt] = value;
    myHead[h] = cnt++;
    return true;
  }

  // todo add BitSet
  @Override
  public boolean exclude(long value) {
    modified();
    int h = index(hash(value));
    int i = myHead[h];
    if (i == 0) return false;
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

  private int hash(long value) {
    return ((int)(value ^ (value >>> 32))) + 1;
  }

  private int index(int hash) {
    return (hash > 0 ? hash : -hash) % headNum;
  }

  // todo fix size()
  @Override
  public int size() {
    int count = 0;
    for (int head = 0; head < headNum; head++) {
      for(int i = myHead[head]; i != 0; i = myNext[i]) {
        count++;
      }
    }
    return count;
  }

  @Override
  @NotNull
  public LongIterator iterator() {
    return new FailFastLongIterator(toArray().iterator()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public void clear() {
    modified();
    cnt = 1;
    Arrays.fill(myHead, 0);
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

  public boolean contains(long value) {
    int h = index(hash(value));
    for (int i = myHead[h]; i != 0; i = myNext[i])
      if (myKeys[i] == value) return true;
    return false;
  }

  // todo more effective
  public ChainHashLongSet retain(LongList values) {
    LongArray array = toArray();
    array.retain(values);
    clear();
    addAll(array);
    return this;
  }
}