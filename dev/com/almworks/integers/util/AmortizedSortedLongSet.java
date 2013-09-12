package com.almworks.integers.util;

import com.almworks.integers.*;

import java.util.*;

/**
 * @author Igor Sereda
 */
public class AmortizedSortedLongSet implements WritableLongSet {
  private static final int DEFAULT_CHUNKSIZE = 512;

  private LongArray myBaseList = new LongArray();
  private final SortedSet<Long> myAdded = new TreeSet<Long>();
  private final Set<Long> myRemoved = new HashSet<Long>();

  private int myChunkSize = DEFAULT_CHUNKSIZE;

  public void add(long value) {
    myRemoved.remove(value);
    myAdded.add(value);
    maybeCoalesce();
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

  public boolean include(long value) {
    boolean contain = contains(value);
    if (!contain) add(value);
    return !contain;
  }

  public boolean exclude(long value) {
    boolean contain = contains(value);
    if (contain) remove(value);
    return contain;
  }

  public void remove(long value) {
    myAdded.remove(value);
    myRemoved.add(value);
    maybeCoalesce();
  }

  public void removeAll(long ... values) {
    for (long value : values) {
      remove(value);
    }
  }

  public void removeAll(LongList values) {
    removeAll(values.iterator());
  }

  public void removeAll(LongIterator values) {
    for (LongIterator it: values) {
      remove(it.value());
    }
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
    LongSetBuilder builder = new LongSetBuilder();
    if (myRemoved.isEmpty()) {
      builder.mergeFromSortedCollection(myBaseList);
    } else {
      int size = myBaseList.size();
      int i;
      for (i = 0; i < size; i++) {
        long v = myBaseList.get(i);
        if (!myRemoved.remove(v)) {
          builder.add(v);
          if (myRemoved.isEmpty()) break;
        }
      }
      if (i < size) {
        builder.mergeFromSortedCollection(myBaseList.subList(i, size));
      }
      myRemoved.clear();
    }
    if (!myAdded.isEmpty()) {
      for (Long value : myAdded) {
        builder.add(value);
      }
      myAdded.clear();
    }
    myBaseList = builder.toLongArray();
  }

  public LongIterator tailIterator(long value) {
    int baseIndex = myBaseList.binarySearch(value);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    LongIterator baseIterator = myBaseList.iterator(baseIndex, myBaseList.size());
    if (myAdded.isEmpty() && myRemoved.isEmpty()) return baseIterator;
    Iterator<Long> addedIterator = myAdded.tailSet(value).iterator();
    return new CoalescingIterator(baseIterator, addedIterator, myRemoved);
  }

  public LongIterator iterator() {
    return tailIterator(Long.MIN_VALUE);
  }

  public AmortizedSortedLongSet retain(LongList values) {
    coalesce();
    ((LongArray)myBaseList).retain(values);
    return this;
  }

  public void replaceFrom(LongSetBuilder builder) {
    myAdded.clear();
    myRemoved.clear();
    myBaseList = new LongArray(builder.toTemporaryReadOnlySortedCollection());
  }

  public LongArray toLongArray() {
    coalesce();
    return LongArray.copy(myBaseList);
  }

  public LongList toList() {
    coalesce();
    return myBaseList;
  }

  public int size() {
    coalesce();
    return myBaseList.size();
  }

  public boolean isEmpty() {
    if (!myAdded.isEmpty()) return false;
    if (myBaseList.isEmpty()) return true;
    if (myRemoved.isEmpty()) return false;
    return !tailIterator(Long.MIN_VALUE).hasNext();
  }

  public void clear() {
    myAdded.clear();
    myRemoved.clear();
    myBaseList = new LongArray();
  }

  private static class CoalescingIterator extends FindingLongIterator {
    private long myNext = Long.MIN_VALUE;
    private final LongIterator myBaseIterator;
    private final Iterator<Long> myAddedIterator;
    private final Set<Long> myRemoved;

    private boolean myHasNextBase;
    private long myNextBase;
    private boolean myHasNextAdded;
    private long myNextAdded;

    public CoalescingIterator(LongIterator baseIterator, Iterator<Long> addedIterator, Set<Long> removed) {
      myBaseIterator = baseIterator;
      myAddedIterator = addedIterator;
      myRemoved = removed;
      myHasNextBase = baseIterator.hasNext();
      myNextBase = myHasNextBase ? baseIterator.nextValue() : 0;
      myHasNextAdded = addedIterator.hasNext();
      myNextAdded = myHasNextAdded ? addedIterator.next() : 0;
    }

    protected long getNext() {
      return myNext;
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
          myNextAdded = myHasNextAdded ? myAddedIterator.next() : 0;
        }
        assert value >= myCurrent : myCurrent + " " + value;
        if (value > myCurrent && !myRemoved.contains(value)) {
          myCurrent = value;
          return true;
        }
      }
      return false;
    }
  }
}
