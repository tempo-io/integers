/*
 * Copyright 2011 ALM Works Ltd
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

import java.util.Iterator;

/**
 * The purpose of this object is to make possible the usage of Writable#E#ListIterator in FOR-EACH loop.
 * For example, if Writable#E#ListIterator j is given, then it can be used in this way: <br>
 * {@code for (Writable#E#ListIterator i: new Writable#E#ListIterableFromIterator(j)) ... }
 */
class Writable#E#ListIterableFromIterator implements Iterable<Writable#E#ListIterator> {
  private Writable#E#ListIterator myIterator;

  public Writable#E#ListIterableFromIterator (Writable#E#ListIterator i) {
    myIterator = i;
  }
  
  public Iterator<Writable#E#ListIterator> iterator () {
    return (Iterator)myIterator;
  }
}
