package com.almworks.integers;

import com.almworks.integers.util.*;

import static com.almworks.integers.IntLongIterators.*;

public abstract class AbstractWritableIntLongMap implements WritableIntLongMap {
  protected int myModCount = 0;

  abstract public boolean containsKey(int key);

  abstract public int size();

  abstract public IntLongIterator iterator();

  abstract public IntIterator keysIterator();

  abstract public LongIterator valuesIterator();

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

  public boolean isEmpty() {
    return size() == 0;
  }

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

  @Override
  public boolean containsKeys(IntIterable iterable) {
    for (IntIterator it: iterable.iterator()) {
      if (!containsKey(it.value())) return false;
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

  public void putAll(IntSizedIterable keys, LongSizedIterable values) {
    modified();
    if (keys.size() != values.size()) {
      throw new IllegalArgumentException();
    }
    putAll(pair(keys, values));
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
    putAllKeys(keys, values);
  }

  @Override
  public void putAllKeys(IntIterable keys, LongIterable values) {
    modified();
    IntIterator keysIt = keys.iterator();
    LongIterator valuesIt = values.iterator();

    putAll(pair(keysIt, valuesIt));
    putAll(pair(keysIt, LongIterators.repeat(DEFAULT_VALUE)));
  }

  @Override
  public void putAllKeys(int[] keys, long[] values) {
    modified();
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++) {
      putImpl(keys[i], values[i]);
    }
    for (int i = size; i < keys.length; i++) {
      putImpl(keys[i], DEFAULT_VALUE);
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