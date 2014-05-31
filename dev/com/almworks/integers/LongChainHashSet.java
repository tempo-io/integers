/*
 * Copyright 2014 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// CODE GENERATED FROM com/almworks/integers/PChainHashSet.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.BitSet;

public class LongChainHashSet extends AbstractWritableLongSet implements WritableLongSet {
  private int[] myHead;
  private int[] myNext;
  private long[] myKeys;

  private int myFront = 1;
  private int myThreshold;
  private int mySize = 0;
  private final float myLoadFactor;
  private final BitSet myRemoved = new BitSet();

  static final int DEFAULT_INITIAL_CAPACITY = 16;
  static final float DEFAULT_LOAD_FACTOR = 0.75f;
  private int myMask;


  /**
   * No guarantees are made regarding the number of elements that can be added to the created set without rehash.
   * {@code initialCapacity} influences only the amount of memory initially allocated.
   * If you need such guarantees, use {@link #createForAdd(int, float)}.
   */
  public LongChainHashSet(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
      throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
    if (Float.isNaN(loadFactor) || loadFactor < 0)
      throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
    initialCapacity = Math.max(DEFAULT_INITIAL_CAPACITY, initialCapacity);

    int headLen = IntegersUtils.nextHighestPowerOfTwo(initialCapacity);
    assert (headLen & (headLen - 1)) == 0 : headLen;

    this.myLoadFactor = loadFactor;
    myThreshold = (int)(headLen * loadFactor) + 1;
    myHead = new int[headLen];
    myKeys = new long[myThreshold];
    myNext = new int[myThreshold];
    myMask = headLen - 1;
  }

  /**
   * Creates new hashset with default load factor
   * @see #LongChainHashSet(int, float)
   */
  public LongChainHashSet(int initialCapacity){
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  /**
   * Creates new hashset with default load factor and default capacity
   */
  public LongChainHashSet(){
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  public static LongChainHashSet createFrom(LongIterable keys) {
    int capacity = LongCollections.sizeOfIterable(keys, 0);
    LongChainHashSet set = createForAdd(capacity);
    for (LongIterator it : keys) {
      set.add(it.value());
    }
    return set;
  }


  /**
   * Creates new hashset with the specified load factor
   * that is garanteed to not invoke {@code resize} after adding {@code count} elements
   * @return new hashset with the specified capacity dependent on {@code count} and {@code loadFactor}
   */
  public static LongChainHashSet createForAdd(int count, float loadFactor) {
    int initialCapacity = (int)(count / loadFactor) + 1;
    return new LongChainHashSet(initialCapacity, loadFactor);
  }

  /**
   * Creates new hashset with default load factor
   * @see #createForAdd(int, float)
   */
  public static LongChainHashSet createForAdd(int count) {
    return createForAdd(count, DEFAULT_LOAD_FACTOR);
  }

  private void resize(int newCapacity) {
    // check that newCapacity = 2^k
    assert (newCapacity & (newCapacity - 1)) == 0 && newCapacity > 0;
    assert myFront == size() + 1 + myRemoved.cardinality();

    int mask = newCapacity - 1;
    int thresholdNew = (int)(newCapacity * myLoadFactor);
    int[] headNew = new int[newCapacity];
    int[] nextNew = new int[thresholdNew];
    long[] keysNew = new long[thresholdNew];
    keysNew[0] = 0;
    toNativeArray(keysNew, 1);

    for (int i = 1; i <= mySize; i++) {
      int h = index(hash(keysNew[i]), mask);
      nextNew[i] = headNew[h];
      headNew[h] = i;
    }
    myFront = mySize + 1;
    myNext = nextNew;
    myKeys = keysNew;
    myHead = headNew;
    myThreshold = thresholdNew;
    myMask = mask;
    myRemoved.clear();
  }

  protected boolean include0(long value) {
    if (size() + 1 >= myThreshold) {
      resize(myHead.length << 1);
    }
    return include1(value);
  }

  private int createNode(long value) {
    mySize++;
    int res;
    if (myRemoved.isEmpty()) {
      assert myFront < myThreshold;
      res = myFront++;
    } else {
      res = myRemoved.nextSetBit(1);
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
  public void addAll(long... values) {
    modified();
    int newSize = size() + values.length;
    if (newSize >= myThreshold) {
      int newCap = IntegersUtils.nextHighestPowerOfTwo((int)(newSize / myLoadFactor) + 1);
      resize(newCap);
    }
    super.addAll(values);
  }

  @Override
  public void addAll(LongList values) {
    modified();
    int newSize = size() + values.size();
    if (newSize >= myThreshold) {
      int newCap = IntegersUtils.nextHighestPowerOfTwo((int)(newSize / myLoadFactor) + 1);
      resize(newCap);
    }
    for (LongIterator it: values) {
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
    if (i == myFront - 1) {
      myFront--;
    } else {
      assert !myRemoved.get(i);
      myRemoved.set(i);
    }
    mySize--;
  }

  protected int hash(long value) {
    return IntegersUtils.hash(value);
  }

  private int index(int hash, int mask) {
    // length = 2^k
    // "hash & mask" equal to "hash % length"
    return hash & mask;
  }

  public int size() {
    return mySize;
  }

  @NotNull
  public LongIterator iterator() {
    return failFast(new LongFindingIterator() {
      private int myCurIndex = 1;
      private int myNextRemoved = myRemoved.nextSetBit(1);

      @Override
      protected boolean findNext() {
        while (myNextRemoved == myCurIndex) {
          myNextRemoved = myRemoved.nextSetBit(myNextRemoved + 1);
          myCurIndex++;
        }
        if (myCurIndex < myFront) {
          myNext = myKeys[myCurIndex++];
          return true;
        }
        return false;
      }
    });
  }

  public void clear() {
    modified();
    myFront = 1;
    mySize = 0;
    Arrays.fill(myHead, 0);
    myRemoved.clear();
  }

  @Override
  protected void toNativeArrayImpl(long[] dest, int destPos) {
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
    len = myFront - from;
    if (len != 0) {
      System.arraycopy(myKeys, from, dest, index, len);
    }
  }

  public boolean contains(long value) {
    int h = index(hash(value), myMask);
    for (int i = myHead[h]; i != 0; i = myNext[i])
      if (myKeys[i] == value) return true;
    return false;
  }

  /**
   *  @return maximum number of elements that this set may contain without rehash
   */
  public int getThreshold() {
    return myThreshold - 1;
  }
}