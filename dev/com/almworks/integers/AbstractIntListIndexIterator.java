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

// CODE GENERATED FROM com/almworks/integers/AbstractPListIndexIterator.tpl




package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class AbstractIntListIndexIterator extends AbstractIntIterator implements IntListIterator {
  private int myFrom;
  private int myTo;
  private int myNext;

  protected AbstractIntListIndexIterator(int from, int to) {
    if (from > to) throw new IllegalArgumentException(from + " > " + to);
    if (from < 0) throw new IllegalArgumentException("from < 0");
    myFrom = from;
    myTo = to;
    myNext = myFrom;
  }

  protected abstract int absget(int index);

  public boolean hasNext() {
    return myNext < myTo;
  }

  public IntListIterator next() throws ConcurrentModificationException, NoSuchElementException {
    if (myNext >= myTo) throw new NoSuchElementException();
    myNext++;
    return this;
  }

  public boolean hasValue() {
    return myFrom < myNext;
  }

  public int value() throws NoSuchElementException {
    if (!hasValue())
      throw new NoSuchElementException();
    return absget(myNext-1);
  }

  public void move(int count) throws ConcurrentModificationException, NoSuchElementException {
    if (count == 0) return;
    int p = myNext - 1 + count;
    if (p < myFrom || p >= myTo) throw new NoSuchElementException(count + " " + this);
    myNext = p + 1;
  }

  public int get(int relativeOffset) throws NoSuchElementException {
    int idx = myNext - 1 + relativeOffset;
    if (idx < myFrom || idx >= myTo)
      throw new NoSuchElementException();
    return absget(idx);
  }

  public int index() throws NoSuchElementException {
    if (myNext <= myFrom)
      throw new NoSuchElementException();
    return myNext - 1;
  }

  public String toString() {
    return (myNext - 1) + "@[" + myFrom + ";" + myTo + ")";
  }

  protected final int getNextIndex() { return myNext; }

  protected final int getFrom() { return myFrom; }

  protected final int getTo() { return myTo; }

  protected final void setNext(int index) { myNext = index; }

  protected final void decrementTo(int count) { myTo -= count; }
}