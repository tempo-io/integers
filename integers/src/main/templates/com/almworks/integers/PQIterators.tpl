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
import java.util.NoSuchElementException;

public class #E##F#Iterators {
  public static #E##F#Iterator pair(final #E#Iterable left, final #F#Iterable right) {
    return new #E##F#PairIterator(left, right);
  }

  public static #E#Iterator leftProjection(final #E##F#Iterator pairs) {
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

  public static #F#Iterator rightProjection(final #E##F#Iterator pairs) {
    return new Abstract#F#IteratorWithFlag() {
      @Override
      protected #f# valueImpl() {
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

  public static String[] toTableString(#E##F#Iterable iterable) {
    StringBuilder[] builders = {new StringBuilder(), new StringBuilder()};
    StringBuilder[] cur = {new StringBuilder(), new StringBuilder()};

    String sep = "";
    for (#E##F#Iterator ii : iterable) {
      cur[0].setLength(0);
      cur[1].setLength(0);

      cur[0].append(ii.left());
      cur[1].append(ii.right());

      builders[0].append(sep);
      builders[1].append(sep);

      joinCurrent(cur, builders);
      sep = ", ";
    }
    return new String[]{builders[0].toString(), builders[1].toString()};
  }

  public static void joinCurrent(StringBuilder[] cur, StringBuilder[] builders) {
    int maxLength = Math.max(cur[0].length(), cur[1].length());
    for (int idx = 0; idx < 2; idx++) {
      for (int i = 0; i < maxLength - cur[idx].length(); i++) {
        builders[idx].append(' ');
      }
      builders[idx].append(cur[idx]);
    }
  }
}
