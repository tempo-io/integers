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
import java.util.NoSuchElementException;

/**
 * The main purpose of this object is to make possible the usage of
 * enhanced {@code for}-statements of {@code #E#List}.
 * @see #E#List#cursor()
 */
public class #E#Cursor implements Iterable<#E#Cursor>, Iterator<#E#Cursor> {
  private #e# myValue;
  private boolean myStepDone = false;
  private #E#Iterator myIterator;

  public #E#Cursor(#E#Iterator iterator) {
    myIterator = iterator;
  }

  public #E#Cursor iterator() {
    return this;
  }

  public boolean hasNext() {
    return myIterator.hasNext();
  }

  public #e# value() {
    if (!myStepDone) throw new NoSuchElementException();
    return myValue;
  }

  public void remove() {
    if (myIterator instanceof Writable#E#ListIterator) ((Writable#E#ListIterator) myIterator).remove();
    else throw new UnsupportedOperationException();
  }

  public #E#Cursor next() {
    myValue = myIterator.next();
    myStepDone = true;
    return this;
  }
}
