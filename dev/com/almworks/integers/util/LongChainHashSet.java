package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.BitSet;

public class LongChainHashSet extends AbstractWritableLongSet implements WritableLongSet {
  private int[] myHead;
  private int[] myNext;
  // todo myKeys LongArray
  private long[] myKeys;
  private int myHeadNum;
  private int cnt = 1;
  private float loadFactor;
  private int threshold;
  private int mySize = 0;
  private BitSet myRemoved = new BitSet();

  static final int DEFAULT_INITIAL_CAPACITY = 16;
  static final float DEFAULT_LOAD_FACTOR = 0.75f;

  public LongChainHashSet(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
      throw new IllegalArgumentException("Illegal initial capacity: " +
          initialCapacity);
    if (!(0 < loadFactor && loadFactor <= 1))
      throw new IllegalArgumentException("Illegal load factor: " +
          loadFactor);

    myHeadNum = IntegersUtils.nextHighestPowerOfTwo(initialCapacity);

    this.loadFactor = loadFactor;
    threshold = (int)(this.myHeadNum * loadFactor);
    myHead = new int[this.myHeadNum];
    myKeys = new long[threshold];
    myNext = new int[threshold];
  }

  public LongChainHashSet(int initialCapacity){
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  public LongChainHashSet(){
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }


  public static LongChainHashSet createWithCapacity(int extCapacity, float loadFactor) {
    int initialCapacity = (int)(extCapacity / loadFactor) + 1;
    return new LongChainHashSet(initialCapacity, loadFactor);
  }

  public static LongChainHashSet createWithCapacity(int initialCapacity) {
    return createWithCapacity(initialCapacity, DEFAULT_LOAD_FACTOR);
  }


  private void modified() {
    myModCount++;
  }

  private void resize(int newCapacity) {
    // check that newCapacity = 2^k
    assert (newCapacity & (newCapacity - 1)) == 0 && newCapacity > 0;
    int thresholdNew = (int)(newCapacity * loadFactor);
    int[] myHeadNew = new int[newCapacity + 1];
    int[] myNextNew = new int[thresholdNew + 1];

    cnt = 1;
    LongArray buffer = LongCollections.collectIterables(thresholdNew + 1, new LongIterator.Single(0), toArray());
    long[] myKeysNew = buffer.extractHostArray();

    for (int i = 1; i <= mySize; i++) {
      int h = index(hash(myKeysNew[i]), newCapacity);
      myNextNew[cnt] = myHeadNew[h];
      myHeadNew[h] = cnt++;
    }

    myNext = myNextNew;
    myKeys = myKeysNew;
    myHead = myHeadNew;
    threshold = thresholdNew;
    myHeadNum = newCapacity;
  }

  protected boolean include0(long value) {
    if (size() + 1 >= threshold) {
      resize(myHeadNum << 1);
    }
    return include1(value);
  }

  protected boolean include1(long value) {
    if (contains(value)) return false;
    mySize++;
    int h = index(hash(value), myHeadNum);

    int x;
    if (myRemoved.isEmpty()) {
      x = cnt++;
    } else {
      x = myRemoved.nextSetBit(0);
      myRemoved.clear(x);
    }
//    if (myNext.length == 49) {
//      System.out.println(myNext.length + " " + myKeys.length + " " + x);
//    }
    myNext[x] = myHead[h];
    myKeys[x] = value;
    myHead[h] = x;
    return true;
  }

  @Override
  public void addAll(LongList values) {
    modified();
    int valuesSize = values.size();
    int newSize = size() + valuesSize;
    if (newSize >= threshold) {
      int newCap = myHeadNum << 1;
      for (int minCap = (int)(newSize / loadFactor); newCap < minCap; ) {
        newCap <<= 1;
      }
      resize(newCap);
    }
    for (LongIterator it: values.iterator()) {
      include1(it.value());
    }
  }

  @Override
  protected boolean exclude0(long value) {
    int h = index(hash(value), myHeadNum);
    int i = myHead[h];
    if (i == 0) return false;
    if (myKeys[i] == value) {
      myHead[h] = myNext[i];
      removeNode(i);
      return true;
    }
    int prev;
    for (prev = i, i = myNext[i]; i != 0; i = myNext[i]) {
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
      myRemoved.set(i);
    }
    mySize--;
  }

  private int hash(long value) {
    return ((int)(value ^ (value >>> 32))) + 1;
  }

  private int index(int hash, int length) {
    // length = 2^k
    assert length > 0 && (length & (length - 1)) == 0;
    return (hash > 0 ? hash : -hash) & (length - 1);
  }

  public int size() {
    return mySize;
  }

  @Override
  @NotNull
  public LongIterator iterator() {
    return new FailFastLongIterator(iterator1()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  private LongIterator iterator1() {
    return new FindingLongIterator() {
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
    };
  }

  public void clear() {
    modified();
    cnt = 1;
    mySize = 0;
    Arrays.fill(myHead, 0);
    Arrays.fill(myNext, 0);
    Arrays.fill(myKeys, 0);
    myRemoved.clear();
  }

  @Override
  public LongArray toArray() {
    // todo add 2 variants: below and new LongArray(iterator()) if myRemoved.cardinality() > size() / 10
    long[] res = new long[size()];
    LongArray myKeysArray = new LongArray(myKeys, size());
    int from = 1, to = myRemoved.nextSetBit(from);
    int index = 0;
    while (to != -1) {
      int len = to - from;
      if (len != 0){
        myKeysArray.toNativeArray(from, res, index, len);
        index += len;
      }
      from = to + 1;
      to = myRemoved.nextSetBit(from);
    }
    myKeysArray.toNativeArray(from, res, index, res.length - index);
    return new LongArray(res);
  }

  public boolean contains(long value) {
    int h = index(hash(value), myHeadNum);
    for (int i = myHead[h]; i != 0; i = myNext[i])
      if (myKeys[i] == value) return true;
    return false;
  }

  public LongChainHashSet retain(LongList values) {
    LongArray res = new LongArray();
    for (LongIterator it: values.iterator()) {
      long value = it.value();
      if (contains(value)) res.add(value);
    }
    clear();
    addAll(res);
    return this;
  }

  public StringBuilder toString(StringBuilder builder) {
    builder.append("LCHS ").append(size()).append(" [");
    String sep = "";
    for  (LongIterator i : this) {
      builder.append(sep).append(i.value());
      sep = ",";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }
}