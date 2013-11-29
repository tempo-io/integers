package com.almworks.integers;

import com.almworks.integers.util.FailFastLongIterator;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractWritableLongSet implements WritableLongSet {
  protected int myModCount = 0;

  /**
   * include element without invocation of {@code AbstractWritableLongSet#modified()}
   */
  protected abstract boolean include0(long value);

  /**
   * exclude element without invocation of {@code AbstractWritableLongSet#modified()}
   */
  protected abstract boolean exclude0(long value);

  protected abstract LongIterator iterator1();

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

  protected void add0(long value) {
    include0(value);
  }

  public void remove(long value) {
    modified();
    remove0(value);
  }

  protected void remove0(long value) {
    exclude0(value);
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
  public void addAll(LongIterator iterator) {
    modified();
    while (iterator.hasNext()) {
      include0(iterator.nextValue());
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
  public boolean containsAll(LongIterable iterable) {
    if (iterable == this) return true;
    for (LongIterator it: iterable.iterator()) {
      if (!contains(it.nextValue())) return false;
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @NotNull
  @Override
  public LongIterator iterator() {
    return new FailFastLongIterator(iterator1()) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public StringBuilder toString(StringBuilder builder) {
    String name = getClass().getSimpleName();
    // LongAmortizedSet -> LAS, LongTreeSet -> LTS
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if ('A' <= c && c <= 'Z') {
        builder.append(c);
      }
    }
    builder.append(" ").append(size()).append(" [");
    String sep = "";
    for  (LongIterator ii : this) {
      builder.append(sep).append(ii.value());
      sep = ", ";
    }
    builder.append("]");
    return builder;
  }

  public final String toString() {
    return toString(new StringBuilder()).toString();
  }
}
