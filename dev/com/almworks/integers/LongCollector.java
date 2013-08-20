/*
 * Copyright 2010 ALM Works Ltd
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

// CODE GENERATED FROM com/almworks/integers/PCollector.tpl


package com.almworks.integers;

/**
* Interface for write-only collection
*/
public interface LongCollector {
  LongCollector DUMMY = new Dummy();

  void add(long value);

  void addAll(LongList values);

  void addAll(LongIterator iterator);

  void addAll(long ... values);

  class Dummy implements LongCollector {
    @Override
    public void add(long value) {
    }

    @Override
    public void addAll(LongList values) {
    }

    @Override
    public void addAll(LongIterator iterator) {
    }

    @Override
    public void addAll(long... values) {
    }
  }
}
