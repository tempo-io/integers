/*
 * Copyright 2014 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// CODE GENERATED FROM com/almworks/integers/PQIterators.tpl


package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class IntIntIterators {
  public static IntIntIterator pair(final IntIterator left, final IntIterator right) {
    return new AbstractIntIntIteratorWithFlag() {
      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return (left.hasNext() && right.hasNext());
      }

      @Override
      protected int leftImpl() {
        return left.value();
      }

      @Override
      protected int rightImpl() {
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

  public static IntIntIterator pair(final IntIterable left, final IntIterable right) {
    return pair(left.iterator(), right.iterator());
  }

  public static IntIterator leftProjection(final IntIntIterator pairs) {
    return new AbstractIntIteratorWithFlag() {
      @Override
      public int valueImpl() {
        return pairs.left();
      }

      @Override
      public void nextImpl() {
        pairs.next();
      }

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return pairs.hasNext();
      }
    };
  }

  public static IntIterator rightProjection(final IntIntIterator pairs) {
    return new AbstractIntIteratorWithFlag() {
      @Override
      protected int valueImpl() {
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
