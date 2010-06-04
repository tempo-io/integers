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

// GENERATED CODE!!!
package com.almworks.integers;

public abstract class #E#CollectorAdapter implements #E#Collector {
  public void addAll(#E#List values) {
    addAll(values.iterator());
  }

  public void addAll(#E#Iterator iterator) {
    while (iterator.hasNext())
      add(iterator.next());
  }

  public void addAll(#e#... values) {
    for (#e# value : values) {
      add(value);
    }
  }
}
