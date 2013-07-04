/*
 * Copyright 2013 ALM Works Ltd
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

package com.almworks.integers.util;

import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;

public abstract class SortedLongListOperationsIterator extends FindingLongIterator {
  protected final LongIterator[] myIts;
  protected int[] heap;
  protected int heapLength;
  protected long myNext = Long.MIN_VALUE;

  public SortedLongListOperationsIterator(LongIterator iterators[]) {
    myIts = new LongIterator[iterators.length];

    heapLength = myIts.length;
    heap = new int[heapLength + 1];
    for (int i = 0; i < iterators.length; i++) {
      myIts[i] = iterators[i];
    }
  }

  static int parent(int i) { return i/2;  }

  static int left(int i) {   return i<<1; }

  static int right(int i) {  return (i<<1) + 1;  }

  protected void swap(int i, int j) {
    int t = heap[i];
    heap[i] = heap[j];
    heap[j] = t;
  }

  protected void heapify(int i) {
    int l = left(i), r = right(i), least;
    if (l <= heapLength && myIts[heap[l]].value() < myIts[heap[i]].value()) {
      least = l;
    } else {
      least = i;
    }
    if (r <= heapLength && myIts[heap[r]].value() < myIts[heap[least]].value()) {
      least = r;
    }
    if (least != i) {
      swap(i, least);
      heapify(least);
    }
  }

  protected void buildHeap() {
    for (int i = parent(heapLength); i >= 1; i--) {
      heapify(i);
//      System.out.println("build!");
    }
  }

  protected void outputHeap() {
    IntegersDebug.print("output: " + heapLength + " : ");
    for (int i = 1; i <= heapLength; i++) {
      IntegersDebug.print(heap[i], myIts[heap[i]].value());
    }
    IntegersDebug.println();
  }

  protected long getNext() {
    //System.out.print(myNext + " ");
    return myNext;
  }
}