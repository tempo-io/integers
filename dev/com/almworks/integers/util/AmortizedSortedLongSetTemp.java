package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;

/**
 * @author Igor Sereda
 */
public class AmortizedSortedLongSetTemp implements WritableLongSet {
  private static final int DEFAULT_CHUNKSIZE = 512;

  private LongArray myBaseList;
  private final DynamicLongSet myAdded = new DynamicLongSet();
  private final DynamicLongSet myRemoved = new DynamicLongSet();

  private int myModCount = 0;
  private boolean myCoalescingStatus;

  public AmortizedSortedLongSetTemp() {
    myBaseList = new LongArray();
  }

  public void add(long value) {
    modified();
    myRemoved.remove(value);
    myAdded.add(value);
    maybeCoalesce();
  }

  public boolean include(long value) {
    modified();
    boolean contain = contains(value);
    if (!contain) add(value);
    return !contain;
  }

  public void addAll(LongList values) {
    modified();
    addAll(values.iterator());
  }

  public void addAll(LongIterator iterator) {
    modified();
    while (iterator.hasNext())
      add(iterator.nextValue());
  }

  public void addAll(long... values) {
    modified();
    if (values != null && values.length != 0) {
      if (values.length == 1) {
        add(values[0]);
      } else {
        addAll(new LongArray(values));
      }
    }
  }

  public void remove(long value) {
    modified();
    myAdded.remove(value);
    myRemoved.add(value);
    maybeCoalesce();
  }

  public boolean exclude(long value) {
    modified();
    boolean contain = contains(value);
    if (contain) remove(value);
    return contain;
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

  @Override
  public AmortizedSortedLongSetTemp retain(LongList values) {
    modified();
    coalesce();
    myBaseList.retain(values);
    return this;
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

  private void modified() {
    myModCount++;
  }

  private void maybeCoalesce() {
    if (myAdded.size() + myRemoved.size() >= DEFAULT_CHUNKSIZE) {
      coalesce();
    }
  }

  void coalesce() {
    myCoalescingStatus = true;
    myBaseList.removeAll(myRemoved.toLongArray());
    myBaseList.merge(myAdded.toList());
    myAdded.clear();
    myRemoved.clear();
  }

  public LongIterator tailIterator(long value) {
    myCoalescingStatus = false;
    int baseIndex = myBaseList.binarySearch(value);
    if (baseIndex < 0) baseIndex = -baseIndex - 1;
    LongIterator baseIterator = myBaseList.iterator(baseIndex, myBaseList.size());
    if (myAdded.isEmpty() && myRemoved.isEmpty()) {
      return new FailFastLongIterator(new CoalescingIterator(baseIterator, LongIterator.EMPTY, LongIterator.EMPTY)) {
        @Override
        protected int getCurrentModCount() {
          return myModCount;
        }
      };
    }
    return new FailFastLongIterator(new CoalescingIterator(baseIterator, myAdded.tailIterator(value), myRemoved.iterator())) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  @NotNull
  public LongIterator iterator() {
    myCoalescingStatus = false;
    LongIterator baseIterator = myBaseList.iterator(0, myBaseList.size());
    if (myAdded.isEmpty() && myRemoved.isEmpty()) return baseIterator;
    return new FailFastLongIterator(new CoalescingIterator(baseIterator, myAdded.iterator(), myRemoved.iterator())) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public void replaceFrom(LongSetBuilder builder) {
    myAdded.clear();
    myRemoved.clear();
    myBaseList = new LongArray(builder.toTemporaryReadOnlySortedCollection());
  }

  public static AmortizedSortedLongSetTemp fromSortedIterable(LongIterable src) {
    return fromSortedIterable(src, 0);
  }

  public static AmortizedSortedLongSetTemp fromSortedIterable(LongIterable src, int capacity) {
    AmortizedSortedLongSetTemp res = new AmortizedSortedLongSetTemp();
    res.myBaseList = LongCollections.collectSortedSet(src.iterator(), capacity);
    return res;
  }

  public static AmortizedSortedLongSetTemp fromSortedList(LongList src) {
    AmortizedSortedLongSetTemp res = new AmortizedSortedLongSetTemp();
    res.myBaseList = new LongArray(src);
    return res;
  }

  public boolean isEmpty() {
    if (!myAdded.isEmpty()) return false;
    if (myBaseList.isEmpty()) return true;
    if (myRemoved.isEmpty()) return false;
    return !iterator().hasNext();
  }

  public void clear() {
    modified();
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

  public LongArray toLongArray() {
    coalesce();
    return LongArray.copy(myBaseList);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AmortizedSortedLongSet\n");
    builder.append("myBaseList: ").append(LongCollections.toBoundedString(myBaseList)).append('\n');
    builder.append("myAdded: ").append(LongCollections.toBoundedString(myAdded)).append('\n');
    builder.append("myRemoved: ").append(LongCollections.toBoundedString(myRemoved));
    return builder.toString();
  }

  private class CoalescingIterator extends AbstractLongIteratorWithFlag {
    private LongIterator myIterator;
    private long myNext = Long.MIN_VALUE;

    private boolean myCoalescingStatusAtCreation;

    public CoalescingIterator(LongIterator baseIterator, LongIterator addedIterator, LongIterator removedIterator) {
      myIterator = new LongUnionIterator(new LongMinusIterator(baseIterator, removedIterator), addedIterator);
      myCoalescingStatusAtCreation = myCoalescingStatus;
    }

    public boolean hasNext() {
      return myIterator.hasNext();
    }

    public void nextImpl() {
      if (myCoalescingStatusAtCreation != myCoalescingStatus) {
        myCoalescingStatusAtCreation = myCoalescingStatus;
        // baseIndex always >= -1
        if (!myIterated) {
          myIterator = myBaseList.iterator();
        } else {
          // myIterated = true -> myNext always exist in myBaseList
          int baseIndex = myBaseList.binarySearch(myNext);
          myIterator = myBaseList.iterator(baseIndex + 1, myBaseList.size());
        }
      }
      myNext = myIterator.nextValue();
    }

    protected long valueImpl() {
      return myNext;
    }

    public boolean hasValue() {
      return myIterator.hasValue();
    }
  }
}
