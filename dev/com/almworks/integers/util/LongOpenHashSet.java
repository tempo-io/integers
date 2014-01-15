package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public class LongOpenHashSet extends AbstractWritableLongSet implements WritableLongSet {
  public final static int DEFAULT_CAPACITY = 16;
  public final static float DEFAULT_LOAD_FACTOR = 0.75f;

  private long[] myKeys;
  private int myKeysLength;
  private BitSet myAllocated;
  private final float myLoadFactor;
  private int myThreshold;
  private int mySize = 0;
  private int myMask;

  public LongOpenHashSet() {
    this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  public LongOpenHashSet(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  public LongOpenHashSet(int initialCapacity, float loadFactor) {
      if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " +
            initialCapacity);
      if (!(0 < loadFactor && loadFactor <= 1))
        throw new IllegalArgumentException("Illegal load factor: " +
            loadFactor);

    myKeysLength = IntegersUtils.nextHighestPowerOfTwo(initialCapacity);
    assert (myKeysLength & (myKeysLength - 1)) == 0;

    this.myLoadFactor = loadFactor;
    myThreshold = (int)(myKeysLength * loadFactor);
//    myHead = new int[this.myHeadNum];
    myKeys = new long[myKeysLength];
    myMask = myKeysLength - 1;
    myAllocated = new BitSet(myKeysLength);
    //    myNext = new int[myThreshold];
  }

  public static LongOpenHashSet createForAdd(int count, float loadFactor) {
    int initialCapacity = (int)(count / loadFactor) + 1;
    return new LongOpenHashSet(initialCapacity, loadFactor);
  }

  public static LongOpenHashSet createForAdd(int count) {
    return createForAdd(count, DEFAULT_LOAD_FACTOR);
  }

  public static LongOpenHashSet createFrom(LongIterable keys) {
    int capacity = LongCollections.sizeOfIterable(keys, 0);
    LongOpenHashSet set = createForAdd(capacity);
    for (LongIterator it : keys.iterator()) {
      set.add(it.value());
    }
    return set;
  }

  public static LongOpenHashSet createFrom(long ... keys) {
    return createFrom(keys == null ? LongIterator.EMPTY : new LongArray(keys));
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

  private void resize(int newCapacity) {
    assert (newCapacity & (newCapacity - 1)) == 0 && newCapacity > 0;
    int mask = newCapacity - 1;
    int thresholdNew = (int)(newCapacity * myLoadFactor);

    long[] myKeysNew = new long[newCapacity];
    BitSet myAllocatedNew = new BitSet(newCapacity);

    for (LongIterator it: iterator()) {
      long value = it.value();
      int slot = index(hash(value), mask);
      while (myAllocatedNew.get(slot)) {
        slot = index(slot + 1, mask);
      }
      myKeysNew[slot] = value;
      myAllocatedNew.set(slot);
    }

    myKeys = myKeysNew;
    myKeysLength = myKeys.length;
    myAllocated = myAllocatedNew;
    myThreshold = thresholdNew;
    myMask = mask;
  }

  @Override
  protected boolean include0(long value) {
    if (size() + 1 >= myThreshold) {
      resize(myKeysLength << 1);
    }
    return include1(value);
  }

  protected boolean include1(long value) {
    int slot = index(hash(value), myMask);
    while (myAllocated.get(slot)) {
      if (value == myKeys[slot]) return false;
      slot = index(slot + 1, myMask);
    }
    myKeys[slot] = value;
    myAllocated.set(slot);
    mySize++;
    return true;
  }

  /**
   * Shift all the slot-conflicting values allocated to (and including) <code>slot</code>.
   * copied from hppc
   */
  private void shiftConflictingKeys(int slotCurr) {
    // Copied nearly verbatim from fastutil's impl.
    int slotPrev, slotOther;
    while (true) {
      slotPrev = slotCurr;
      slotCurr = index(slotCurr + 1, myMask);

      while (myAllocated.get(slotCurr)) {
        slotOther = hash(myKeys[slotCurr]) & myMask;
        if (slotPrev <= slotCurr) {
          // We are on the right of the original slot.
          if (slotPrev >= slotOther || slotOther > slotCurr) break;
        }
        else {
          // We have wrapped around.
          if (slotPrev >= slotOther && slotOther > slotCurr) break;
        }
        slotCurr = index(slotCurr + 1, myMask);
      }

      if (!myAllocated.get(slotCurr)) break;

      // Shift key/value pair.
      myKeys[slotPrev] = myKeys[slotCurr];
    }

    myAllocated.clear(slotPrev);
  }

  public boolean exclude0(long value) {
    int slot = index(hash(value), myMask);

    while (myAllocated.get(slot)) {
      if (value == myKeys[slot]) {
        mySize--;
        shiftConflictingKeys(slot);
        return true;
      }
      slot = index(slot + 1, myMask);
    }
    return false;
  }

  @Override
  public void clear() {
    mySize = 0;
    myAllocated.clear();
  }

  @Override
  public boolean contains(long value) {
    int slot = index(hash(value), myMask);
    while (myAllocated.get(slot)) {
      if (value == myKeys[slot]) return true;
      slot = index(slot + 1, myMask);
    }
    return false;
  }

  @Override
  public int size() {
    return mySize;
  }


  @Override
  public void toNativeArrayImpl(long[] dest, int destPos) {
    int j = destPos;
    for (int i = 0; i < myKeysLength; i++) {
      if (myAllocated.get(i)) dest[j++] = myKeys[i];
    }
  }

  @NotNull
  public LongIterator iterator() {
    return failFast(new FindingLongIterator() {
      int curSlot = 0;
      @Override
      protected boolean findNext() {
        if (curSlot == -1) return false;
        curSlot = myAllocated.nextSetBit(curSlot);
        if (curSlot == -1) return false;
        myCurrent = myKeys[curSlot];
        curSlot++;
        return true;
      }
    });
  }
}