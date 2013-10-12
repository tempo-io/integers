package com.almworks.integers.util;

import com.almworks.integers.*;

import java.util.*;

public class LongHashSetWrapper implements WritableLongSet {
  TreeSet<Long> set = new TreeSet<Long>();
  int myModCount = 0;

  public LongHashSetWrapper() {}

  public LongHashSetWrapper(int capacity) {}

  public LongHashSetWrapper(LongList values) {
    for (int i = 0, n = values.size(); i < n; i++) {
      set.add(values.get(i));
    }
  }

  private void modified() {
    myModCount++;
  }

  @Override
  public void clear() {
    modified();
    set.clear();
  }

  @Override
  public void remove(long value) {
    modified();
    set.remove(value);
  }

  @Override
  public void removeAll(long... values) {
    modified();
    for (long val: values) {
      set.remove(val);
    }
  }

  @Override
  public void removeAll(LongList values) {
    modified();
    removeAll(values.toNativeArray());
  }

  @Override
  public void removeAll(LongIterator iterator) {
    modified();
    removeAll(new LongArray(iterator));
  }

  @Override
  public LongHashSetWrapper retain(LongList values) {
    modified();
    List aList = new ArrayList<Long>(values.size());
    for (int i = 0; i < values.size(); i++) {
      aList.add(values.get(i));
    }
    set.retainAll(aList);
    return this;
  }

  @Override
  public boolean include(long value) {
    modified();
    return set.add(value);
  }

  @Override
  public boolean exclude(long value) {
    modified();
    return set.remove(value);
  }

  @Override
  public void add(long value) {
    modified();
    set.add(value);
  }

  @Override
  public void addAll(LongList values) {
    modified();
    addAll(values.iterator());
  }

  @Override
  public void addAll(LongIterator iterator) {
    modified();
    while (iterator.hasNext()) {
      set.add(iterator.nextValue());
    }
  }

  @Override
  public void addAll(long... values) {
    modified();
    for (long val: values) {
      set.add(val);
    }
  }

  @Override
  public LongList toList() {
    return toArray();
  }

  @Override
  public LongArray toArray() {
    return new LongArray(iterator());
  }

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public boolean contains(long value) {
    return set.contains(value);
  }

  @Override
  public LongIterator iterator() {
    return new FailFastLongIterator(new FindingLongIterator() {
      Iterator<Long> it = set.iterator();
      @Override
      protected boolean findNext() {
        if (!it.hasNext()) return false;
        myCurrent = it.next();
        return true;
      }
    }) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public LongIterator tailIterator(final long value) {
    return new FindingLongIterator() {
      Iterator<Long> it = set.tailSet(value).iterator();
      @Override
      protected boolean findNext() {
        if (!it.hasNext()) return false;
        myCurrent = it.next();
        return true;
      }
    };
  }

  @Override
  public boolean containsAll(LongIterable iterable) {
    return set.containsAll(Arrays.asList(iterable));
  }
}