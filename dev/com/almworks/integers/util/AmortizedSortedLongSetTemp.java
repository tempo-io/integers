package com.almworks.integers.util;

import com.almworks.integers.DynamicLongSet;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import com.almworks.integers.LongList;

import java.util.*;

/**
 * @author Igor Sereda
 */
public class AmortizedSortedLongSetTemp {
  private static final int DEFAULT_CHUNKSIZE = 512;

  private LongList myBaseList = LongList.EMPTY;
  private final DynamicLongSet myAdded = new DynamicLongSet();
  private final DynamicLongSet myRemoved = new DynamicLongSet();

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
      for (LongIterator iterator: myAdded.iterator()) {
        builder.add(iterator.value());
      }
      myAdded.clear();
    }
    myBaseList = builder.toSortedList();
  }

  public LongIterator tailIterator(long value) {
    int baseIndex = myBaseList.binarySearch(value);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    LongIterator baseIterator = myBaseList.iterator(baseIndex, myBaseList.size());
    if (myAdded.isEmpty() && myRemoved.isEmpty()) return baseIterator;
    return new CoalescingIterator(baseIterator, myAdded.tailIterator(value), myRemoved);
  }

  public void replaceFrom(LongSetBuilder builder) {
    myAdded.clear();
    myRemoved.clear();
    myBaseList = builder.toSortedList();
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
