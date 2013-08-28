package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Igor Sereda
 */
public class AmortizedSortedLongSetTemp implements WritableLongSet {
  private static final int DEFAULT_CHUNKSIZE = 512;

  private LongArray myBaseList = new LongArray();
  private final DynamicLongSet myAdded = new DynamicLongSet();
  private final DynamicLongSet myRemoved = new DynamicLongSet();

  private int myChunkSize = DEFAULT_CHUNKSIZE;

  public void add(long value) {
    myRemoved.remove(value);
    myAdded.add(value);
    maybeCoalesce();
  }

  public boolean include(long value) {
    boolean contain = contains(value);
    if (!contain) add(value);
    return !contain;
  }

  public void addAll(LongList values) {
    addAll(values.iterator());
  }

  public void addAll(LongIterator iterator) {
    while (iterator.hasNext())
      add(iterator.nextValue());
  }

  public void addAll(long... values) {
    if (values != null && values.length != 0) {
      if (values.length == 1) {
        add(values[0]);
      } else {
        addAll(new LongArray(values));
      }
    }
  }

  public void remove(long value) {
    myAdded.remove(value);
    myRemoved.add(value);
    maybeCoalesce();
  }

  public boolean exclude(long value) {
    boolean contain = contains(value);
    if (contain) remove(value);
    return contain;
  }

  public void removeAll(long ... values) {
    for (long value : values) {
      remove(value);
    }
  }

  @Override
  public void retain(LongList values) {
    coalesce();
    myBaseList.retain(values);
  }

  public boolean contains(long value) {
    if (myRemoved.contains(value)) return false;
    if (myAdded.contains(value)) return true;
    return myBaseList.binarySearch(value) >= 0;
  }

  public boolean containsAll(LongIterable values) {
    for (LongIterator iterator : values.iterator()) {
      if (!contains(iterator.value())) return false;
    }
    return true;
  }

  private void maybeCoalesce() {
    if (myAdded.size() + myRemoved.size() >= myChunkSize) {
      coalesce();
    }
  }

  private void coalesce() {
    myBaseList.removeAll(myRemoved.toLongArray());
    myBaseList.merge(myBaseList);
    myRemoved.clear();
    myBaseList.clear();
  }

  public LongIterator tailIterator(long value) {
    int baseIndex = myBaseList.binarySearch(value);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    LongIterator baseIterator = myBaseList.iterator(baseIndex, myBaseList.size());
    if (myAdded.isEmpty() && myRemoved.isEmpty()) return baseIterator;
    return new CoalescingIterator(baseIterator, myAdded.tailIterator(value), myRemoved);
  }

  @NotNull
  public LongIterator iterator() {
    return new CoalescingIterator(myBaseList.iterator(), myAdded.iterator(), myRemoved);
  }

  public void replaceFrom(LongSetBuilder builder) {
    myAdded.clear();
    myRemoved.clear();
    myBaseList = new LongArray(builder.toTemporaryReadOnlySortedCollection());
  }

  public boolean isEmpty() {
    if (!myAdded.isEmpty()) return false;
    if (myBaseList.isEmpty()) return true;
    if (myRemoved.isEmpty()) return false;
    return !iterator().hasNext();
  }

  public void clear() {
    myAdded.clear();
    myRemoved.clear();
    myBaseList = new LongArray();
  }

  public int size() {
    int size = myBaseList.size();
    // intersection of myRemoved and myAdded is empty
    for (LongIterator iterator: myAdded.iterator()) {
      if (myBaseList.binarySearch(iterator.value()) < 0) size++;
    }
    for (LongIterator iterator: myRemoved.iterator()) {
      if (myBaseList.binarySearch(iterator.value()) >= 0) size--;
    }
    return size;
}

  public LongList toList() {
    coalesce();
    return myBaseList;
  }

  private static class CoalescingIterator extends FindingLongIterator {
    private final LongIterator myBaseIterator;
    private final LongIterator myAddedIterator;
    private final DynamicLongSet myRemoved;

    private boolean myHasNextBase;
    private long myNextBase;
    private boolean myHasNextAdded;
    private long myNextAdded;

    public CoalescingIterator(LongIterator baseIterator, LongIterator addedIterator, DynamicLongSet removed) {
      myBaseIterator = baseIterator;
      myAddedIterator = addedIterator;
      myRemoved = removed;
      myHasNextBase = baseIterator.hasNext();
      myNextBase = myHasNextBase ? baseIterator.nextValue() : 0;
      myHasNextAdded = addedIterator.hasNext();
      myNextAdded = myHasNextAdded ? addedIterator.nextValue() : 0;
    }

    protected boolean findNext() {
      while (myHasNextBase || myHasNextAdded) {
        long value;
        if (!myHasNextAdded || (myHasNextBase && myNextBase <= myNextAdded)) {
          value = myNextBase;
          myHasNextBase = myBaseIterator.hasNext();
          myNextBase = myHasNextBase ? myBaseIterator.nextValue() : 0;
        } else {
          value = myNextAdded;
          myHasNextAdded = myAddedIterator.hasNext();
          myNextAdded = myHasNextAdded ? myAddedIterator.nextValue() : 0;
        }
        assert value >= myNext : myNext + " " + value;
        if (value > myNext && !myRemoved.contains(value)) {
          myNext = value;
          return true;
        }
      }
      return false;
    }
  }
}
