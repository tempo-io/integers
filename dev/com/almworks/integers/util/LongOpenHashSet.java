package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.BitSet;

public class LongOpenHashSet extends AbstractWritableLongSet implements WritableLongSet {
  public final static int DEFAULT_CAPACITY = 16;
  public final static float DEFAULT_LOAD_FACTOR = 0.75f;

  private long[] myKeys;
  private int myKeysLength;
  private BitSet myAllocated;
  private final float loadFactor;
  private int threshold;
  private int mySize = 0;

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

    this.loadFactor = loadFactor;
    threshold = (int)(myKeysLength * loadFactor);
//    myHead = new int[this.myHeadNum];
    myKeys = new long[myKeysLength];
    myAllocated = new BitSet(myKeysLength);
    //    myNext = new int[threshold];
  }

  private int hash(long value) {
    return ((int)(value ^ (value >>> 32))) + 1;
  }

  private int index(int hash, int length) {
    // length = 2^k
    assert length > 0 && (length & (length - 1)) == 0;
    return (hash > 0 ? hash : -hash) & (length - 1);
  }

  private void resize(int newCapacity) {
    assert (newCapacity & (newCapacity - 1)) == 0 && newCapacity > 0;
    int mask = newCapacity - 1;
    int thresholdNew = (int)(newCapacity * loadFactor);

    long[] myKeysNew = new long[newCapacity];
    BitSet myAllocatedNew = new BitSet(newCapacity);

    for (LongIterator it: iterator()) {
      long value = it.value();
      int slot = index(hash(value), newCapacity);
      while (myAllocatedNew.get(slot)) {
        // todo replace all ... & mask -> index ?
        slot = (slot + 1) & mask;
      }
      myKeysNew[slot] = value;
      myAllocatedNew.set(slot, true);
    }

    myKeys = myKeysNew;
    myKeysLength = myKeys.length;
    myAllocated = myAllocatedNew;
    threshold = thresholdNew;
  }

  @Override
  protected boolean include0(long value) {
    if (size() + 1 >= threshold) {
      resize(myKeysLength << 1);
    }
    return include1(value);
  }

  protected boolean include1(long value) {
    int mask = myKeys.length - 1;
    int slot = index(hash(value), myKeysLength);
    while (myAllocated.get(slot)) {
      if (value == myKeys[slot]) return false;
      slot = (slot + 1) & mask;
    }
//    System.out.println("ururu!" + slot + " " + myKeysLength + " " + myAllocated.size());
    myKeys[slot] = value;
    myAllocated.set(slot, true);
    mySize++;
    return true;
  }

  /**
   * Shift all the slot-conflicting keys allocated to (and including) <code>slot</code>.
   */
  protected void shiftConflictingKeys(int slotCurr)
  {
    // Copied nearly verbatim from fastutil's impl.
    final int mask = myKeys.length - 1;
    int slotPrev, slotOther;
    while (true) {
      slotCurr = ((slotPrev = slotCurr) + 1) & mask;

      while (myAllocated.get(slotCurr)) {
        slotOther = hash(myKeys[slotCurr]) & mask;
        if (slotPrev <= slotCurr) {
          // We are on the right of the original slot.
          if (slotPrev >= slotOther || slotOther > slotCurr) break;
        }
        else {
          // We have wrapped around.
          if (slotPrev >= slotOther && slotOther > slotCurr) break;
        }
        slotCurr = (slotCurr + 1) & mask;
      }

      if (!myAllocated.get(slotCurr)) break;

      // Shift key/value pair.
      myKeys[slotPrev] = myKeys[slotCurr];
    }

    myAllocated.set(slotPrev, false);

        /*  */
  }

  public boolean exclude0(long key) {
    int mask = myKeysLength - 1;
    int slot = index(hash(key), myKeysLength);

    while (myAllocated.get(slot)) {
      if (key == myKeys[slot]) {
        mySize--;
        shiftConflictingKeys(slot);
        return true;
      }
      slot = (slot + 1) & mask;
    }
    return false;
  }

  @Override
  public void clear() {
    mySize = 0;
    // todo maybe remove this line
    Arrays.fill(myKeys, 0);
    myAllocated.clear();
  }

  @Override
  public LongOpenHashSet retain(LongList values) {
    LongArray res = new LongArray();
    for (LongIterator it: values.iterator()) {
      long value = it.value();
      if (contains(value)) res.add(value);
    }
    clear();
    addAll(res);
    return this;
  }

  @Override
  public boolean contains(long key) {
    int mask = myKeysLength - 1;
    int slot = index(hash(key), myKeysLength);
    while (myAllocated.get(slot)) {
      if (key == myKeys[slot]) return true;
      slot = (slot + 1) & mask;
    }
    return false;
  }

  @Override
  public int size() {
    return mySize;
  }

  @Override
  public LongArray toArray() {
    LongArray res = new LongArray(mySize);
    for (int i = 0; i < myKeysLength; i++) {
      if (myAllocated.get(i)) res.add(myKeys[i]);
    }
    return res;
  }

  @NotNull
  @Override
  public LongIterator iterator() {
    return new FailFastLongIterator(iterator1()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  @NotNull
  private LongIterator iterator1() {
    return new FindingLongIterator() {
      int curSlot = 0;
      @Override
      protected boolean findNext() {
        curSlot = myAllocated.nextSetBit(curSlot);
        if (curSlot == -1) return false;
        myCurrent = myKeys[curSlot];
        curSlot++;
        return true;
      }
    };
  }

  public StringBuilder toString(StringBuilder builder) {
    builder.append("LOHS ").append(size()).append(" [");
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