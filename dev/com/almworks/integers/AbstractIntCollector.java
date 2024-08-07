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

// CODE GENERATED FROM com/almworks/integers/AbstractPCollector.tpl




package com.almworks.integers;

public abstract class AbstractIntCollector implements IntCollector {
  public void addAll(IntList values) {
    addAll(values.iterator());
  }

  public void addAll(IntIterable iterable) {
    for (IntIterator it : iterable) {
      add(it.value());
    }
  }

  public void addAll(int... values) {
    for (int value : values) {
      add(value);
    }
  }
}
