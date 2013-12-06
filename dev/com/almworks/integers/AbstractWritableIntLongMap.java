package com.almworks.integers;

import com.almworks.integers.util.FailFastIntIterator;
import com.almworks.integers.util.FailFastIntLongIterator;
import com.almworks.integers.util.FailFastLongIterator;

public abstract class AbstractWritableIntLongMap implements WritableIntLongMap {
  protected int myModCount = 0;

  abstract public boolean containsKey(int key);

  abstract public int size();

  abstract protected IntLongIterator iteratorImpl();

  abstract protected IntIterator keysIteratorImpl();

  abstract protected LongIterator valuesIteratorImpl();

  public boolean isEmpty() {
    return size() == 0;
  }

  abstract public long get(int key);

  abstract public void clear();

  /**
   * put element without invocation of {@code AbstractWritableIntLongMap#modified()}
   */
  abstract protected long putImpl(int key, long value);

  /**
   * remove element without invocation of {@code AbstractWritableIntLongMap#modified()}
   */
  abstract protected long removeImpl(int key);

  protected final IntLongIterator failFast(IntLongIterator iter) {
    return new FailFastIntLongIterator(iter) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  protected final LongIterator failFast(LongIterator iter) {
    return new FailFastLongIterator(iter) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  protected final IntIterator failFast(IntIterator iter) {
    return new FailFastIntIterator(iter) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public IntLongIterator iterator() {
    return new FailFastIntLongIterator(iteratorImpl()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public IntIterator keysIterator() {
    return new FailFastIntIterator(keysIteratorImpl()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public LongIterator valuesIterator() {
    return new FailFastLongIterator(valuesIteratorImpl()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }


  @Override
  public boolean containsKeys(IntIterable iterable) {
    for (IntIterator it: iterable.iterator()) {
      if (!containsKey(it.nextValue())) return false;
    }
    return true;
  }

  protected void modified() {
    myModCount++;
  }

  public long put(int key, long value) {
    modified();
    return putImpl(key, value);
  }

  public boolean putIfAbsent(int key, long value) {
    modified();
    if (containsKey(key)) return false;
    putImpl(key, value);
    return true;
  }

  public AbstractWritableIntLongMap add(int key, long value) {
    modified();
    putImpl(key, value);
    return this;
  }

  public long remove(int key) {
    modified();
    return removeImpl(key);
  }

  public boolean remove(int key, long value) {
    modified();
    if (!(get(key) == value)) return false;
    removeImpl(key);
    return true;
  }

  public void putAll(IntIterable keys, LongIterable values) {
    modified();
    putAll(IntLongIterators.pair(keys.iterator(), values.iterator()));
  }

  public void putAll(IntLongIterable entries) {
    modified();
    for (IntLongIterator it: entries) {
      putImpl(it.left(), it.right());
    }
  }

  @Override
  public void putAll(int[] keys, long[] values) {
    modified();
    if (keys.length != values.length) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < keys.length; i++) {
      putImpl(keys[i], values[i]);
    }
  }

  public void removeAll(int... keys) {
    modified();
    for (int key: keys) {
      removeImpl(key);
    }
  }

  public void removeAll(IntIterable keys) {
    modified();
    for (IntIterator it : keys.iterator()) {
      removeImpl(it.value());
    }
  }
}