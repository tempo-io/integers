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
 */\n\n// CODE GENERATED FROM com/almworks/integers/PIterables.tpl\n



package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class LongIterables {

  public static Iterable<LongIterator> fromIterator(final LongIterator i) {
    return new Iterable<LongIterator>() {
      @Override @NotNull public LongIterator iterator() {
        return i;
      }
    };
  }

  public static Iterable<LongListIterator> fromListIterator(final LongListIterator i) {
    return new Iterable<LongListIterator>() {
      @Override @NotNull public Iterator<LongListIterator> iterator() {
        return (Iterator)i;
      }
    };
  }

  public static Iterable<WritableLongListIterator> fromWritableListIterator(final WritableLongListIterator i) {
    return new Iterable<WritableLongListIterator>() {
      @Override @NotNull public Iterator<WritableLongListIterator> iterator() {
        return (Iterator)i;
      }
    };
  }
}

