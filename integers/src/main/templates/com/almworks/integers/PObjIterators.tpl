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

package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class #E#ObjIterators {
  public static <T> #E#ObjIterator<T> pair(final #E#Iterator left, final Iterator<T> right) {
    return new #E#ObjPairIterator(left, right);
  }

  public static <T> #E#ObjIterator<T> pair(final #E#Iterable left, final Iterable<T> right) {
    return new #E#ObjPairIterator(left.iterator(), right.iterator());
  }

  public static #E#Iterator leftProjection(final #E#ObjIterator pairs) {
    return new Abstract#E#IteratorWithFlag() {
      @Override
      public #e# valueImpl() {
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

  public static <T> Iterator<T> rightProjection(final #E#ObjIterator<T> pairs) {
    return new Iterator() {
      @Override
      public boolean hasNext() {
        return pairs.hasNext();
      }

      @Override
      public T next() {
        pairs.next();
        return pairs.right();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}