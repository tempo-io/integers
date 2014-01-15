package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class IntLongIterators {
  public static IntLongIterator pair(final IntIterator left, final LongIterator right) {
    return new AbstractIntLongIteratorWithFlag() {
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return (left.hasNext() && right.hasNext());
      }

      @Override
      protected int leftImpl() {
        return left.value();
      }

      @Override
      protected long rightImpl() {
        return right.value();
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {
        myIterated = false;
        left.next();
        right.next();
        myIterated = true;
      }
    };
  }

  public static IntLongIterator pair(final IntIterable left, final LongIterable right) {
    return pair(left.iterator(), right.iterator());
  }

  public static IntIterator leftProjection(final IntLongIterator pairs) {
    return new AbstractIntIterator() {
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return pairs.hasNext();
      }

      @Override
      public int value() throws NoSuchElementException {
        return pairs.left();
      }

      @Override
      public IntIterator next() {
        pairs.next();
        return this;
      }
    };
  }

  public static LongIterator rightProjection(final IntLongIterator pairs) {
    return new AbstractLongIteratorWithFlag() {
      @Override
      protected long valueImpl() {
        return pairs.right();
      }

      @Override
      protected void nextImpl() throws NoSuchElementException {
        pairs.next();
      }

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return pairs.hasNext();
      }
    };
  }
}
