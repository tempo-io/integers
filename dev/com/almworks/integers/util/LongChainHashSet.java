package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LongChainHashSet extends AbstractWritableLongSet implements WritableLongSet {
  private int[] myHead;
  private int[] myNext;
  private long[] myKeys;
  private int headNum;
  private int cnt = 1;
  private float loadFactor;
  private int threshold;

  static final int DEFAULT_INITIAL_CAPACITY = 16;
  static final float DEFAULT_LOAD_FACTOR = 0.75f;

  public LongChainHashSet(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
      throw new IllegalArgumentException("Illegal initial capacity: " +
          initialCapacity);
    if (!(0 < loadFactor && loadFactor <= 1))
      throw new IllegalArgumentException("Illegal load factor: " +
          loadFactor);

    headNum = 1;
    // todo fix: now final capacity of set less that initialCapacity
    while (headNum < initialCapacity) headNum <<= 1;

    this.loadFactor = loadFactor;
    threshold = (int)(this.headNum * loadFactor);
    myHead = new int[this.headNum];
    myKeys = new long[threshold];
    myNext = new int[threshold];
  }

  public LongChainHashSet(int initialCapacity){
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  public LongChainHashSet(){
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  private void modified() {
    assert size() < threshold;
    if (size() + 1 == threshold) {
      resize();
    }
    myModCount++;
  }

  private void resize() {
    int newHeadNum = myHead.length<<1;
    int thresholdNew = (int)(newHeadNum * loadFactor);

    int[] myHeadNew = new int[newHeadNum];
    int[] myNextNew = new int[thresholdNew];

    cnt = 1;
    // todo more effective, refactor
    LongArray values = toArray();

    LongArray buffer = LongCollections.collectIterables(thresholdNew, new LongIterator.Single(0), toArray());
    long[] myKeysNew = buffer.extractHostArray();

    for (int i = 0; i < values.size(); i++) {
      int h = index(hash(values.get(i)), newHeadNum);
      myNextNew[cnt] = myHeadNew[h];
      myHeadNew[h] = cnt++;
    }
    myNext = myNextNew;
    myKeys = myKeysNew;
    myHead = myHeadNew;
    threshold = thresholdNew;
    headNum = newHeadNum;
  }

  // todo optimize(now effective only when size() < headNum)
  protected boolean include0(long value) {
    myKeys = LongCollections.ensureCapacity(myKeys, cnt + 1);
    myNext = IntCollections.ensureCapacity(myNext, cnt + 1);
    if (contains(value)) return false;
    int h = index(hash(value), headNum);
    myNext[cnt] = myHead[h];
    myKeys[cnt] = value;
    myHead[h] = cnt++;
    return true;
  }

  @Override
  public void addAll(LongList values) {
    modified();
    int size = values.size();
    myKeys = LongCollections.ensureCapacity(myKeys, cnt + size);
    myNext = IntCollections.ensureCapacity(myNext, cnt + size);


  }

  // todo add BitSet
  @Override
  protected boolean exclude0(long value) {
    int h = index(hash(value), headNum);
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

  private int index(int hash, int length) {
    return (hash > 0 ? hash : -hash) & (length - 1);
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
    // todo add BitSet and do more effective
    LongArray res = new LongArray();
    for (int head = 0; head < headNum; head++) {
      for(int i = myHead[head]; i != 0; i = myNext[i]) {
        res.add(myKeys[i]);
      }
    }
    return res;
  }

  public boolean contains(long value) {
    int h = index(hash(value), headNum);
    for (int i = myHead[h]; i != 0; i = myNext[i])
      if (myKeys[i] == value) return true;
    return false;
  }

  // todo more effective
  public LongChainHashSet retain(LongList values) {
    LongArray array = toArray();
    array.retain(values);
    clear();
    addAll(array);
    return this;
  }
}