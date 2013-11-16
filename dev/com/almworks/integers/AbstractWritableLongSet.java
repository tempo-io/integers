package com.almworks.integers;

public abstract class AbstractWritableLongSet implements WritableLongSet {
  protected int myModCount = 0;

  private void modified() {
    myModCount++;
  }

  /**
   * include element without invocation of {@code AbstractWritableLongSet#modified()}
   */
  protected abstract boolean include0(long value);

  @Override
  public boolean include(long value) {
    modified();
    return include0(value);
  }

  /**
   * exclude element without invocation of {@code AbstractWritableLongSet#modified()}
   */
  protected abstract boolean exclude0(long value);

  @Override
  public boolean exclude(long value) {
    modified();
    return exclude0(value);
  }

  public void add(long value) {
    modified();
    include0(value);
  }

  public void remove(long value) {
    modified();
    exclude0(value);
  }

  @Override
  public void removeAll(long... keys) {
    modified();
    for (long key : keys) {
      exclude0(key);
    }
  }

  @Override
  public void removeAll(LongList values) {
    modified();
    removeAll(values.iterator());
  }

  @Override
  public void removeAll(LongIterator iterator) {
    modified();
    while (iterator.hasNext()) {
      exclude0(iterator.nextValue());
    }
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
      include0(iterator.nextValue());
    }
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
