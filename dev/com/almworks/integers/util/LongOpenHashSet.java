package com.almworks.integers.util;

import com.almworks.integers.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LongOpenHashSet extends AbstractWritableLongSet implements WritableLongSet {

  @Override
  protected boolean include0(long value) {
    return false;
  }

  @Override
  protected boolean exclude0(long value) {
    return false;
  }

  @Override
  public void clear() {
  }

  @Override
  public WritableLongSet retain(LongList values) {
    return null;
  }

  @Override
  public boolean contains(long value) {
    return false;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public LongArray toArray() {
    return null;
  }

  @NotNull
  @Override
  public LongIterator iterator() {
    return null;
  }
}