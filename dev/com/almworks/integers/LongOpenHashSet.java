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

// CODE GENERATED FROM com/almworks/integers/POpenHashSet.tpl


package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

/**
 * A hash set of {@code long}'s, implemented using using open
 * addressing with linear probing for collision resolution.
 * <br>It makes no guarantees as to the iteration order of the set;
 * in particular, it does not guarantee that the order will remain constant over time.
 *
 * <p><b>Important note.</b> The implementation uses power-of-two tables and linear
 * probing, which may cause poor performance (many collisions) if hash values are
 * not properly distributed.
 * <p>As a hash function, this implementation uses the finalization step from
 * Austin Appleby's <code>MurmurHash3</code>.
 * To use a custom hash function, override {@link #hash(long)}.
 *
 * <p>Differencies from {@link LongChainHashSet} by performance:
 * <ul>
 *   <li>{@link #include(long)} and {@link #contains(long)} are faster if hash values are properly distributed
 *   <li>{@link #remove(long)} is slower
 *   <li>{@link #toArray()} is slower
 *   <li>iterating through the set using {@link #iterator()} is slower
 * </ul>
 *
 * With the specified threshold {@link IntChainHashSet} takes {@code M*(1+2*f)} memory
 * where {@code M} - memory for {@link IntOpenHashSet}, {@code f} - loadFactor.
 * {@link LongChainHashSet} takes {@code M*(1+3*f)/2} memory, where {@code M} - memory for {@link LongOpenHashSet}
 *
 * @see	    LongChainHashSet
 * @see	    LongTreeSet
 */
public class LongOpenHashSet extends AbstractWritableLongSet implements WritableLongSet {
  final static int DEFAULT_CAPACITY = 16;
  final static float DEFAULT_LOAD_FACTOR = 0.75f;

  private long[] myKeys;
  private BitSet myAllocated;
  private final float myLoadFactor;
  private int myThreshold;
  private int mySize = 0;
  private int myMask;
  private final long myPerturbation = System.identityHashCode(this);

  /**
   * No guarantees are made regarding the number of elements that can be added to the created set without rehash.
   * {@code initialCapacity} influences only the amount of memory initially allocated.
   * If you need such guarantees, use {@link #createForAdd(int, float)}.
   */
  public LongOpenHashSet(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
      throw new IllegalArgumentException("Illegal initial capacity: " +
          initialCapacity);
    if (!(0 < loadFactor && loadFactor < 1))
      throw new IllegalArgumentException("Illegal load factor: " +
          loadFactor);

    int keysLength = IntegersUtils.nextHighestPowerOfTwo(initialCapacity);
    assert (keysLength & (keysLength - 1)) == 0;

    this.myLoadFactor = loadFactor;
    init(new long[keysLength], new BitSet(keysLength), (int) (keysLength * loadFactor));
  }

  /**
   * Creates new hashset with default load factor
   * @see #LongOpenHashSet(int, float)
   */
  public LongOpenHashSet(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  /**
   * Creates new hashset with default load factor and default capacity
   */
  public LongOpenHashSet() {
    this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  private void init(long[] keys, BitSet allocated, int threshold) {
    myKeys = keys;
    myAllocated = allocated;
    myThreshold = threshold;
    myMask = keys.length - 1;
    assert (myMask & (myMask + 1)) == 0;
  }

  /**
   * Creates new hashset with the specified load factor
   * that is garanteed to not invoke {@code resize} after adding {@code count} elements
   * @return new hashset with the specified capacity dependent on {@code count} and {@code loadFactor}
   */
  public static LongOpenHashSet createForAdd(int count, float loadFactor) {
    int initialCapacity = (int)(count / loadFactor) + 1;
    return new LongOpenHashSet(initialCapacity, loadFactor);
  }

  /**
   * Creates new hashset with default load factor
   * @see #createForAdd(int, float)
   */
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
    return createFrom(keys == null ? LongList.EMPTY : new LongArray(keys));
  }

  protected int hash(long value) {
    return IntegersUtils.hash(value ^ myPerturbation);
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

    for (LongIterator it: this) {
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

  /**
   *  @return maximum number of elements that this set may contain without rehash
   */
  public int getThreshold() {
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
    return failFast(new LongFindingIterator() {
      int curSlot = 0;
      @Override
      protected boolean findNext() {
        curSlot = myAllocated.nextSetBit(curSlot);
        if (curSlot == -1) return false;
        myNext = myKeys[curSlot];
        curSlot++;
        return true;
      }
    });
  }
}