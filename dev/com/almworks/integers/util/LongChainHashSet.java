package com.almworks.integers.util;

import com.almworks.integers.*;

import java.util.Arrays;
import java.util.BitSet;

public class LongChainHashSet extends AbstractWritableLongSet implements WritableLongSet {
  private int[] myHead;
  private int[] myNext;
  private long[] myKeys;

  private int myHeadNum;
  private int cnt = 1;
  private int threshold;
  private int mySize = 0;
  private final float loadFactor;
  private final BitSet myRemoved = new BitSet();

  static final int DEFAULT_INITIAL_CAPACITY = 16;
  static final float DEFAULT_LOAD_FACTOR = 0.75f;
  private int myMask;

  public LongChainHashSet(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
      throw new IllegalArgumentException("Illegal initial capacity: " +
          initialCapacity);
    if (!(0 < loadFactor && loadFactor <= 1))
      throw new IllegalArgumentException("Illegal load factor: " +
          loadFactor);

    myHeadNum = IntegersUtils.nextHighestPowerOfTwo(initialCapacity);
    assert (myHeadNum & (myHeadNum - 1)) == 0 : myHeadNum;

    this.loadFactor = loadFactor;
    threshold = (int)(myHeadNum * loadFactor);
    myHead = new int[this.myHeadNum];
    myKeys = new long[threshold];
    myNext = new int[threshold];
    myMask = myHeadNum - 1;
  }

  public LongChainHashSet(int initialCapacity){
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  public LongChainHashSet(){
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }


  public static LongChainHashSet createForAdd(int count, float loadFactor) {
    int initialCapacity = (int)(count / loadFactor) + 1;
    return new LongChainHashSet(initialCapacity, loadFactor);
  }

  public static LongChainHashSet createForAdd(int initialCapacity) {
    return createForAdd(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  private void resize(int newCapacity) {
    // check that newCapacity = 2^k
    assert (newCapacity & (newCapacity - 1)) == 0 && newCapacity > 0;
    assert cnt == size() + 1 + myRemoved.cardinality();

    int mask = newCapacity - 1;
    int thresholdNew = (int)(newCapacity * loadFactor);
    int[] myHeadNew = new int[newCapacity + 1];
    int[] myNextNew = new int[thresholdNew + 1];

    long[] myKeysNew = new long[thresholdNew + 1];
    myKeysNew[0] = 0;
    toNativeArray(myKeysNew, 1);

    for (int i = 1; i <= mySize; i++) {
      int h = index(hash(myKeysNew[i]), mask);
      myNextNew[i] = myHeadNew[h];
      myHeadNew[h] = i;
    }
    cnt = mySize + 1;
    myNext = myNextNew;
    myKeys = myKeysNew;
    myHead = myHeadNew;
    threshold = thresholdNew;
    myHeadNum = newCapacity;
    myMask = mask;
  }

  protected boolean include0(long value) {
    if (size() + 1 >= threshold) {
      resize(myHeadNum << 1);
    }
    return include1(value);
  }

  private int createNode(long value) {
    mySize++;
    int res;
    if (myRemoved.isEmpty()) {
      assert cnt < threshold;
      res = cnt++;
    } else {
      res = myRemoved.nextSetBit(0);
      myRemoved.clear(res);
    }
    myKeys[res] = value;
    myNext[res] = 0;
    return res;
  }

  protected boolean include1(long value) {
    int h = index(hash(value), myMask);
    int prev = myHead[h];
    if (prev == 0) {
      myHead[h] = createNode(value);
      return true;
    }
    for (int cur = prev; cur != 0; cur = myNext[cur]) {
      if (myKeys[cur] == value) {
        return false;
      }
      prev = cur;
    }
    myNext[prev] = createNode(value);
    return true;
  }

  @Override
  public void addAll(LongList values) {
    modified();
    int newSize = size() + values.size();
    if (newSize >= threshold) {
      int newCap = IntegersUtils.nextHighestPowerOfTwo((int)(newSize / loadFactor) + 1);
      resize(newCap);
    }
    for (LongIterator it: values.iterator()) {
      include1(it.value());
    }
  }

  @Override
  protected boolean exclude0(long value) {
    int h = index(hash(value), myMask);
    int i = myHead[h];
    if (i == 0) return false;
    if (myKeys[i] == value) {
      myHead[h] = myNext[i];
      removeNode(i);
      return true;
    }
    while (true) {
      int prev = i;
      i = myNext[i];
      if (i == 0) break;

      if (myKeys[i] == value) {
        myNext[prev] = myNext[i];
        removeNode(i);
        return true;
      }
    }
    return false;
  }

  private void removeNode(int i) {
    if (i == cnt - 1) {
      cnt--;
    } else {
      assert !myRemoved.get(i);
      myRemoved.set(i);
    }
    mySize--;
  }

  protected int hash(long value) {
    return IntegersUtils.hash(value);
//    return ((int)(value ^ (value >>> 32))) + 1;
  }

  private int index(int hash, int mask) {
    // length = 2^k
    // "hash & mask" equal to "hash % length"
    return hash & mask;
  }

  public int size() {
    return mySize;
  }

  public LongIterator iterator() {
    return failFast(new FindingLongIterator() {
      private int curHead = 0;
      private int curIndex = 0;

      @Override
      protected boolean findNext() {
        while (curIndex == 0) {
          if (curHead == myHeadNum) return false;
          curIndex = myHead[curHead++];
        }
        myCurrent = myKeys[curIndex];
        curIndex = myNext[curIndex];
        return true;
      }
    });
  }

  public void clear() {
    modified();
    cnt = 1;
    mySize = 0;
    Arrays.fill(myHead, 0);
    myRemoved.clear();
  }

  @Override
  public void toNativeArrayImpl(long[] dest, int destPos) {
    // todo add 2 variants: below and new LongArray(iterator()) if myRemoved.cardinality() > size() / 10
    int from = 1, to = myRemoved.nextSetBit(from);
    int index = destPos;
    int len;
    while (to != -1) {
      len = to - from;
      if (len != 0){
        System.arraycopy(myKeys, from, dest, index, len);
        index += len;
      }
      from = to + 1;
      to = myRemoved.nextSetBit(from);
    }
    System.arraycopy(myKeys, from, dest, index, cnt - from);
  }

  public boolean contains(long value) {
    int h = index(hash(value), myMask);
    for (int i = myHead[h]; i != 0; i = myNext[i])
      if (myKeys[i] == value) return true;
    return false;
  }

}