package com.almworks.integers.optimized;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.almworks.integers.IntCollections.range;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class CyclicLongQueue extends AbstractLongList {
  private long[] myHostArray;
  private int myL;
  private int myR;
  @Nullable
  private List<PinnedIterator> myIterators;

  public CyclicLongQueue(int initialCapacity) {
    if (initialCapacity == 0) {
      myHostArray = IntegersUtils.EMPTY_LONGS;
    } else {
      // Can hold myHostArray.length - 1 values
      myHostArray = new long[initialCapacity + 1];
    }
  }

  public CyclicLongQueue() {
    this(0);
  }

  private int normalizeOver(int index) {
    return index < myHostArray.length ? index : index - myHostArray.length;
  }

  private int normalizeUnder(int index) {
    return index >= 0 ? index : myHostArray.length + index;
  }

  public long get(int i) {
    assert i < size() : i + " >= " + size();
    return myHostArray[normalizeOver(myL + i)];
  }

  /**
   * Retrieves, but does not remove, the head of this queue,
   * or returns <tt>null</tt> if this queue is empty.
   *
   * @return the head of this queue, or <tt>null</tt> if this queue is empty
   */
  public long peek() {
    if (isEmpty()) throw new NoSuchElementException();
    return myHostArray[myL];
  }

  public int size() {
    return normalizeUnder(myR - myL);
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  private void checkIterators(int toRemove) throws IllegalStateException {
    assert toRemove <= size();
    if (myIterators != null && toRemove > 0) {
      int removalPoint = normalizeOver(myL + toRemove);
      for (PinnedIterator it : myIterators) {
        if (indexBetweenLandP(it.myHostIdx, removalPoint) || indexBetweenLandP(it.myHostIdx + 1, removalPoint)) {
          throw new IllegalStateException("Iterator " + it + " prevents from removing " + toRemove + " elements");
        }
      }
    }
  }

  private boolean indexBetweenLandP(int idx, int p) {
    return
        // |-------------myR-----[myL*****p)----|
        myL <= idx && idx < p ||
        // |****p)-------myR-----[myL***********|
        p < myL && (idx < p || myL <= idx);
  }

  /** @throws IllegalStateException if there is any attached {@link #pinnedIterator pinned iterator}. */
  public void clear() throws IllegalStateException {
    checkIterators(size());
    myL = 0;
    myR = 0;
  }

  public void add(long x) {
    ensureCapacity(size() + 1);
    myHostArray[myR] = x;
    myR = normalizeOver(myR + 1);
  }

  public void addAll(long... xs) {
    if (xs == null) return;
    addAll(new LongArray(xs));
  }

  public void addAll(LongList xs) {
    if (xs == null) return;
    ensureCapacity(size() + xs.size());
    for (LongIterator x: xs) {
      myHostArray[myR] = x.value();
      myR = normalizeOver(myR + 1);
    }
  }

  /**
   * Removes the first element and returns it.
   * @throws NoSuchElementException if this array is empty
   * @throws IllegalStateException if there is a {@link #pinnedIterator()} pointing at the first element
   * */
  public long removeFirst() throws NoSuchElementException, IllegalStateException {
    if (isEmpty()) throw new NoSuchElementException();
    checkIterators(1);
    long ret = myHostArray[myL];
    myL = normalizeOver(myL + 1);
    return ret;
  }

  /**
   * Removes the first <tt>n</tt> elements; if there are less elements present, removes all elements.
   * @return the number of removed elements <tt>r</tt>, <tt>0 <= r <= n</tt>
   * @throws IndexOutOfBoundsException if <tt>n</tt> is negative
   * @throws IllegalStateException if there is a {@link #pinnedIterator()} pointing at any of the elements
   *          to be removed
   * */
  public int removeFirst(int n) throws IndexOutOfBoundsException, IllegalStateException {
    if (n < 0) throw new IndexOutOfBoundsException("n=" + n);
    n = min(n, size());
    checkIterators(n);
    myL = normalizeOver(myL + n);
    return n;
  }

  public void ensureRoomToAdd(int nToAdd) {
    ensureCapacity(size() + nToAdd);
  }

  private void ensureCapacity(int capacity) {
    int n = myHostArray.length;
    if (capacity >= n) {
      // 1 extra element is needed to distinguish empty array from "all capacity used"
      int newCapacity = max(16, max(capacity + 1, n * 2));
      long[] newHostArray = new long[newCapacity];
      int span = myR - myL;
      if (myIterators != null) for (PinnedIterator it : myIterators) it.onRealloc(myL);
      if (span >= 0) {
        System.arraycopy(myHostArray, myL, newHostArray, 0, span);
        myL = 0;
        myR = span;
      } else {
        System.arraycopy(myHostArray, myL, newHostArray, 0, n - myL);
        System.arraycopy(myHostArray, 0,   newHostArray, n - myL, myR);
        myL = 0;
        myR = n + span;
      }
      myHostArray = newHostArray;
    }
  }

  /**
   * <p>Returns an iterator that, unlike normal iterators, survives concurrent modifications to this array:
   * neither {@link #add(long)} nor {@link #removeFirst()} affect its value or make it throw
   * ConcurrentModificationException.</p>
   * <p>Note that one could alternatively use the index of the current position in this sliding array,
   * but they would need to update that index upon each modification, because these modifications
   * move the window of active indices.
   * In that regard, this iterator is "pinned" to the position in the host array.</p>
   * <p>Moreover, while this iterator is {@link #attach attached}, the elements this iterator points at
   * cannot be removed. Iterator <i>points</i> at its current (if present) and next element.
   * Because of that, one needs to call {@link #detach} on the iterator that will not be used,
   * so that removals of the element the iterator points at are possible.</p>
   */
  @NotNull
  public PinnedIterator pinnedIterator() {
    return pinnedIterator(-1);
  }

  /**
   * Creates {@link #pinnedIterator() pinned iterator} pointing at the element at {@code idx}.
   * @param idx set -1 to have iterator return the first element from the first call to nextValue()
   * */
  public PinnedIterator pinnedIterator(int idx) {
    PinnedIterator iterator = new PinnedIterator(idx);
    attach(iterator);
    return iterator;
  }

  /**
   * <p>Attaches the specified iterator to this instance, so that it won't be possible to remove a value that
   * this iterator points at via {@link #removeFirst()}, {@link #removeFirst(int)}, and {@link #clear()}.
   * Initially, iterator is attached (see {@link #pinnedIterator()}.)</p>
   *
   * <p>If the specified iterator is not over this instance, the effect is undefined.</p>
   *  */
  public void attach(@Nullable PinnedIterator iterator) {
    if (myIterators == null) myIterators = new ArrayList<PinnedIterator>();
    if (!myIterators.contains(iterator)) {
      myIterators.add(iterator);
    }
  }

  public void detach(@Nullable PinnedIterator iterator) {
    if (myIterators != null) {
      myIterators.remove(iterator);
    }
  }

  public String toStringWithPiterators() {
    return new ToStringWithPiteratorsBuilder().build().toString();
  }

  public class PinnedIterator extends AbstractLongIterator {
    private int myHostIdx;
    private int myAge;

    protected PinnedIterator(int offset) {
      myHostIdx = normalizeOver(myL + offset);
    }

    protected void onRealloc(int oldL) {
      assert myHostIdx >= oldL;
      myHostIdx = normalizeUnder(myHostIdx - oldL);
    }

    @Override
    public boolean hasNext() {
      int nextIdx = normalizeOver(myHostIdx + 1);
      return nextIdx < myR || myL > myR && nextIdx >= myL;
    }

    @Override
    public PinnedIterator next() {
      myHostIdx = normalizeOver(myHostIdx + 1);
      myAge += 1;
      return this;
    }

    public boolean hasValue() {
      return myAge > 0;
    }

    @Override
    public long value() throws NoSuchElementException {
      if (myAge == 0) throw new NoSuchElementException();
      return myHostArray[myHostIdx];
    }

    /** @return the number of times {@link #next()} has been called on this iterator. */
    public int age() {
      return myAge;
    }

    /** Current index of the iterator's position. */
    public int index() {
      return myHostIdx >= myL ? myHostIdx - myL : myHostIdx + (myHostArray.length - myL + 1);
    }

    @Override
    public String toString() {
      return "@" + myHostIdx + " in [" + myL + "," + myR + ")";
    }

    public void attach() {
      CyclicLongQueue.this.attach(PinnedIterator.this);
    }

    public void detach() {
      CyclicLongQueue.this.detach(PinnedIterator.this);
    }
  }

  /**
   * Outputs the contents (as publicly visible, not in the host array order), marking the positions of the
   * currently active pinned iterators.
   * If the size is too large (10 + number of positions pointed at by pinned iterators), displays only
   * 5 first, 5 last, and pinned elements.
   * */
  private class ToStringWithPiteratorsBuilder {
    private final StringBuilder mySb = new StringBuilder();
    private final IntArray myPiPos = new IntArray(myIterators == null ? 0 : myIterators.size());
    private final IntArray myIndices = new IntArray();
    private final boolean myShortForm = size() > 10 + myPiPos.size();

    public StringBuilder build() {
      if (myIterators != null) {
        for (PinnedIterator pi : myIterators) {
          int idx = pi.myHostIdx;
          myPiPos.add(idx + myL * (myL <= idx ? -1 : 1));
        }
        myPiPos.sortUnique();
      }
      if (myShortForm) {
        mySb.append('[').append(size()).append("] (");

        myIndices.addAll(IntProgression.arithmetic(0, min(size(), 5)));
        myIndices.addAll(IntProgression.arithmetic(max(size() - 5, 0), 5));
        myIndices.addAll(myPiPos);
        myIndices.sortUnique();
      } else {
        mySb.append('(');
        myIndices.addAll(range(size()));
      }
      appendIndices();
      mySb.append(')');
      return mySb;
    }

    private void appendIndices() {
      if (myIndices.size() == 0) return;
      int i = 1, last, cur = myIndices.get(0);
      assert myIndices.size() >= 1;
      while (true) {
        mySb.append(get(cur));
        if (myPiPos.contains(cur)) mySb.append('*');

        if (i == myIndices.size()) break;

        last = cur;
        cur = myIndices.get(i++);
        mySb.append(last + 1 == cur ? ", " : ", ..., ");
      }
    }
  }
}