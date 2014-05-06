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
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongObjIterators {
  public static LongObjIterator pair(final LongIterator left, final Iterator right) {
    return new LongObjPairIterator(left, right);
  }

  public static LongIterator leftProjection(final LongObjIterator pairs) {
    return new AbstractLongIteratorWithFlag() {
      @Override
      public long valueImpl() {
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

  public static Iterator rightProjection(final LongObjIterator pairs) {
    return new Iterator() {
      @Override
      public boolean hasNext() {
        return pairs.hasNext();
      }

      @Override
      public Object next() {
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