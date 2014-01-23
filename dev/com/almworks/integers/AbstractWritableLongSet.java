package com.almworks.integers;

import com.almworks.integers.util.FailFastLongIterator;

public abstract class AbstractWritableLongSet extends AbstractLongSet implements WritableLongSet {
  protected int myModCount = 0;

  /**
   * include element without invocation of {@code AbstractWritableLongSet#modified()}
   */
  protected abstract boolean include0(long value);

  /**
   * exclude element without invocation of {@code AbstractWritableLongSet#modified()}
   */
  protected abstract boolean exclude0(long value);

  protected abstract void toNativeArrayImpl(long[] dest, int destPos);

  public abstract LongIterator iterator();

  protected void add0(long value) {
    include0(value);
  }

  protected void remove0(long value) {
    exclude0(value);
  }

  protected void modified() {
    myModCount++;
  }

  @Override
  public boolean include(long value) {
    modified();
    return include0(value);
  }

  @Override
  public boolean exclude(long value) {
    modified();
    return exclude0(value);
  }

  public void add(long value) {
    modified();
    add0(value);
  }

  public void remove(long value) {
    modified();
    remove0(value);
  }

  @Override
  public void removeAll(long... values) {
    modified();
    if (values.length == 1) {
      remove(values[0]);
    } else {
      removeAll(new LongArrayIterator(values));
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
      remove0(iterator.nextValue());
    }
  }

  @Override
  public void addAll(LongList values) {
    modified();
    addAll(values.iterator());
  }

  @Override
  public void addAll(LongIterable iterable) {
    modified();
    for (LongIterator it : iterable) {
      include0(it.value());
    }
  }

  public void addAll(long... values) {
    modified();
    if (values != null && values.length != 0) {
      for (long value: values) {
        add0(value);
      }
    }
  }

  @Override
  public void retain(LongList values) {
    LongArray res = new LongArray();
    for (LongIterator it: values.iterator()) {
      long value = it.value();
      if (contains(value)) res.add(value);
    }
    clear();
    addAll(res);
  }

  protected final LongIterator failFast(LongIterator iter) {
    return new FailFastLongIterator(iter) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }
}
