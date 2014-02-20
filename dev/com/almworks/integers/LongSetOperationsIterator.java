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

package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Eugene Vagin
 * */
abstract class LongSetOperationsIterator extends LongFindingIterator {
  /**
   * ArrayList is preferable implementation. Using LinkedList may be ineffective
   * */
  protected final List<LongIterator> myIts;
  /**
   * myHeap elements start from index 1, index 0 is unused
   **/
  protected final int[] myHeap;
  protected int heapLength;
  protected static final int TOP = 1;

  public LongSetOperationsIterator(@NotNull List<LongIterator> iterators) {
    myIts = iterators;
    heapLength = myIts.size();
    myHeap = new int[heapLength + 1];
  }

  public LongSetOperationsIterator(@NotNull LongIterator... iterators) {
    this(Arrays.asList(iterators));
  }

  protected static List<LongIterator> longIterablesToIterators(List<? extends LongIterable> includes) {
    List<LongIterator> result = new ArrayList<LongIterator>(includes.size());
    for (LongIterable arr : includes) {
      result.add(arr.iterator());
    }
    return result;
  }

  protected static int parent(int i) { return i/2;  }

  private static int left(int i) {   return i*2; }

  private static int right(int i) {  return i*2 + 1; }

  protected final void heapify(int i) {
    int l = left(i), r = right(i), least;
    if (l <= heapLength && myIts.get(myHeap[l]).value() < myIts.get(myHeap[i]).value()) {
      least = l;
    } else {
      least = i;
    }
    if (r <= heapLength && myIts.get(myHeap[r]).value() < myIts.get(myHeap[least]).value()) {
      least = r;
    }
    if (least != i) {
      IntCollections.swap(myHeap, i, least);
      heapify(least);
    }
  }

  protected final void buildHeap() {
    for (int i = parent(heapLength); i >= 1; i--) {
      heapify(i);
    }
  }

  protected final void outputHeap() {
    if (!IntegersDebug.PRINT) return;
    IntegersDebug.print("output:", heapLength, ": ");
    for (int i = 1; i <= heapLength; i++) {
      IntegersDebug.print("(", myHeap[i], myIts.get(myHeap[i]).value(), ")");
    }
    IntegersDebug.println();
  }

  protected LongIterator getTopIterator() {
    return myIts.get(myHeap[TOP]);
  }
}
