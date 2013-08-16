package com.almworks.integers.util;

import com.almworks.integers.LongIterator;
import com.almworks.integers.LongList;
import com.almworks.integers.util.FindingLongIterator;
import com.almworks.integers.util.LongSetBuilder;

import java.util.*;

/**
 * @author Igor Sereda
 */
public class AmortizedSortedLongSet {
  private static final int DEFAULT_CHUNKSIZE = 512;

  private LongList myBaseList = LongList.EMPTY;
  private final SortedSet<Long> myAdded = new TreeSet<Long>();
  private final Set<Long> myRemoved = new HashSet<Long>();

  private int myChunkSize = DEFAULT_CHUNKSIZE;

  public void add(long value) {
    myRemoved.remove(value);
    myAdded.add(value);
    maybeCoalesce();
  }

  public void remove(long value) {
    myAdded.remove(value);
    myRemoved.add(value);
    maybeCoalesce();
  }

  public boolean contains(long value) {
    if (myRemoved.contains(value)) return false;
    if (myAdded.contains(value)) return true;
    return myBaseList.binarySearch(value) >= 0;
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
    myBaseList = builder.toSortedCollection();
  }

  public LongIterator tailIterator(long value) {
    int baseIndex = myBaseList.binarySearch(value);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    LongIterator baseIterator = myBaseList.iterator(baseIndex, myBaseList.size());
    if (myAdded.isEmpty() && myRemoved.isEmpty()) return baseIterator;
    Iterator<Long> addedIterator = myAdded.tailSet(value).iterator();
    return new CoalescingIterator(baseIterator, addedIterator, myRemoved);
  }

  public void replaceFrom(LongSetBuilder builder) {
    myAdded.clear();
    myRemoved.clear();
    myBaseList = builder.toSortedCollection();
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
    myBaseList = LongList.EMPTY;
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
