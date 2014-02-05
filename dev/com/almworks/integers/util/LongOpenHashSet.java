package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public class LongOpenHashSet extends AbstractWritableLongSet implements WritableLongSet {
  public final static int DEFAULT_CAPACITY = 16;
  public final static float DEFAULT_LOAD_FACTOR = 0.75f;

  private long[] myKeys;
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

    int keysLength = IntegersUtils.nextHighestPowerOfTwo(initialCapacity);
    assert (keysLength & (keysLength - 1)) == 0;

    this.myLoadFactor = loadFactor;
    init(new long[keysLength], new BitSet(keysLength), (int) (keysLength * loadFactor));
  }

  private void init(long[] keys, BitSet allocated, int threshold) {
    myKeys = keys;
    myAllocated = allocated;
    myThreshold = threshold;
    myMask = keys.length - 1;
    assert (myMask & (myMask + 1)) == 0;
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
  }

  private int index(int hash, int mask) {
    // length = 2^k
    // "hash & mask" equal to "hash % length"
    return hash & mask;
  }

  private void resize(int newCapacity) {
    assert (newCapacity & (newCapacity - 1)) == 0 && newCapacity > 0;
    int mask = newCapacity - 1;

    long[] keysNew = new long[newCapacity];
    BitSet alocatedNew = new BitSet(newCapacity);

    for (LongIterator it: iterator()) {
      long value = it.value();
      int slot = index(hash(value), mask);
      while (alocatedNew.get(slot)) {
        slot = index(slot + 1, mask);
      }
      keysNew[slot] = value;
      alocatedNew.set(slot);
    }

    init(keysNew, alocatedNew, (int)(newCapacity * myLoadFactor));
  }

  @Override
  protected boolean include0(long value) {
    if (size() + 1 > myThreshold) {
      resize(myKeys.length << 1);
    }
    return include1(value);
  }

  private boolean include1(long value) {
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
        slotOther = index(hash(myKeys[slotCurr]), myMask);
        if (slotPrev <= slotCurr) {
          // We are on the right of the original slot.
          if (slotPrev >= slotOther || slotOther > slotCurr) break;
        } else {
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

  int getThreshold() {
    return myThreshold;
  }

  @Override
  protected void toNativeArrayImpl(long[] dest, int destPos) {
    int j = destPos;
    for (int i = 0; i < myKeys.length; i++) {
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