package com.almworks.integers;

public abstract class AbstractWritableLongSet implements WritableLongSet {

  public void remove(long value) {
    exclude(value);
  }

  @Override
  public void removeAll(long... keys) {
    for (long key : keys) {
      exclude(key);
    }
  }

  @Override
  public void removeAll(LongList values) {
    removeAll(values.iterator());
  }

  @Override
  public void removeAll(LongIterator iterator) {
    while (iterator.hasNext()) {
      remove(iterator.nextValue());
    }
  }

  @Override
  public void add(long value) {
    include(value);
  }

  @Override
  public void addAll(LongList values) {
    addAll(values.iterator());
  }

  @Override
  public void addAll(LongIterator iterator) {
    while (iterator.hasNext()) {
      add(iterator.nextValue());
    }
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

  @Override
  public boolean containsAll(LongIterable iterable) {
    for (LongIterator it: iterable.iterator()) {
      if (!contains(it.nextValue())) return false;
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }
}
