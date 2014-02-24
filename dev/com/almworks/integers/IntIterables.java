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

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class IntIterables {

  public static Iterable<IntIterator> fromIterator(final IntIterator i) {
    return new Iterable<IntIterator>() {
      @Override @NotNull public IntIterator iterator() {
        return i;
      }
    };
  }

  public static Iterable<IntListIterator> fromListIterator(final IntListIterator i) {
    return new Iterable<IntListIterator>() {
      @Override @NotNull public Iterator<IntListIterator> iterator() {
        return (Iterator)i;
      }
    };
  }

  public static Iterable<WritableIntListIterator> fromWritableListIterator(final WritableIntListIterator i) {
    return new Iterable<WritableIntListIterator>() {
      @Override @NotNull public Iterator<WritableIntListIterator> iterator() {
        return (Iterator)i;
      }
    };
  }
}
