package com.almworks.integers;

import static com.almworks.integers.IntLongIterators.*;
import static com.almworks.integers.IntegersUtils.appendShortName;

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
    return new IntLongFailFastIterator(iter) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  protected final LongIterator failFast(LongIterator iter) {
    return new LongFailFastIterator(iter) {
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
    int size = keys.length;
    for (int i = 0; i < size; i++) {
      putImpl(keys[i], values[i]);
    }
  }

  @Override
  public void putAllKeys(IntIterable keys, LongIterable values) {
    modified();
    IntIterator keysIt = keys.iterator();
    LongIterator valuesIt = values.iterator();

    while (keysIt.hasNext() && valuesIt.hasNext()) {
      putImpl(keysIt.nextValue(), valuesIt.nextValue());
    }
    while (keysIt.hasNext()) {
      putIfAbsent(keysIt.nextValue(), DEFAULT_VALUE);
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


  public StringBuilder toString(StringBuilder builder) {
    appendShortName(builder, this);
    builder.append(" ").append(size()).append(" [");
    String sep = "";
    for (IntLongIterator ii : this) {
      builder.append(sep).append('(').append(ii.left()).append(' ').append(ii.right()).append(')');
      sep = ", ";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }

  private void joinCurrent(StringBuilder[] cur, StringBuilder[] builders) {
    int maxLength = Math.max(cur[0].length(), cur[1].length());
    for (int idx = 0; idx < 2; idx++) {
      for (int i = 0; i < maxLength - cur[idx].length(); i++) {
        builders[idx].append(' ');
      }
      builders[idx].append(cur[idx]);
    }
  }

  public String toTableString() {
    StringBuilder[] builders = {new StringBuilder(), new StringBuilder()};
    StringBuilder[] cur = {new StringBuilder(), new StringBuilder()};

    cur[0] = appendShortName(cur[0], this).append(" /");
    cur[1].append(size()).append(" \\");
    joinCurrent(cur, builders);

    String sep = "";
    for (IntLongIterator ii : this) {
      cur[0].setLength(0);
      cur[1].setLength(0);

      cur[0].append(ii.left());
      cur[1].append(ii.right());

      builders[0].append(sep);
      builders[1].append(sep);


      joinCurrent(cur, builders);
      sep = ", ";
    }
    builders[0].append(" \\\n").append(builders[1]).append(" /");
    return builders[0].toString();
  }
}