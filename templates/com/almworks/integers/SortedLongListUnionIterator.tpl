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

// CODE GENERATED FROM com/almworks/integers/util/SortedPListIntersectionIterator.tpl


package com.almworks.integers.util;

import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;

/**
 * Iterates through two unique sorted int lists in O(N+M), providing unique sorted values that exist in
 * either of lists
 */
public class SortedLongListUnionIterator extends FindingLongIterator {
  private final LongIterator my[];
  private long myNext = Long.MIN_VALUE;
  //private boolean myIterated[];
  private int heap[];
  private int heapLength;
  private boolean isHeapBuilded = false;

  public SortedLongListUnionIterator(LongIterator iterators[]) {
    //? len = iterators.length;
    my = new LongIterator[iterators.length];
//    myIterated = new boolean[iterators.length];
    int size = 1;
    while (size < iterators.length) size <<= 1; //fix it
    size++;
    //System.out.println(size);

    heap = new int[size];
    heapLength = 0;
    for (int i = 0; i < iterators.length; i++) {
      my[i] = iterators[i];
    }
  }

  static int PARENT(int i) {
    return i/2;
  }
  static int LEFT(int i) {
    return i<<1;
  }
  static int RIGHT(int i) {
    return (i<<1) + 1;
  }


  public static SortedLongListUnionIterator create(LongIterable includes[]) {
    LongIterator result[] = new LongIterator[includes.length];
    for (int i = 0; i < includes.length; i++) {
      result[i] = includes[i].iterator();
    }

    return new SortedLongListUnionIterator(result);
  }

  private void swap(int i, int j) {
    int t = heap[i];
    heap[i] = heap[j];
    heap[j] = t;
  }

  private void heapify(int i) {
    int l = LEFT(i);
    int r = RIGHT(i);
//    System.out.printf("heapify: %d %d %d %d ", i, heapLength, l, r);
    int least;
    if (l <= heapLength && my[heap[l]].value() < my[heap[i]].value()) {
      least = l;
    } else {
      least = i;
    }
//    //System.out.printf("%d %d %d\n", r, least, my.length);
    if (r <= heapLength && my[heap[r]].value() < my[heap[least]].value()) {
      least = r;
    }
//    System.out.println(least);
    if (least != i) {
      swap(i, least);
      heapify(least);
    }
//    //System.out.println("Im here!");
  }

  private void buildHeap() {
    for (int i = PARENT(heapLength); i >= 1; i--) {
      heapify(i);
//      System.out.println("build!");
    }
  }

  private void outputHeap() {
    System.out.print("output: " + heapLength + " : ");
    for (int i = 1; i <= heapLength; i++) {
      System.out.printf(" (%d %d)", heap[i], my[heap[i]].value());
    }
    System.out.println();
  }

  protected boolean findNext() {
    if (!isHeapBuilded) {
      isHeapBuilded = true;
      for (int i = 0; i < my.length; i++) {
        if (my[i].hasNext()) {
          my[i].next();
          heapLength++;
          //System.out.println(heapLength);
          heap[heapLength] = i;
        }
      }
      //System.out.println("heapLength: " + heapLength);
      buildHeap();
    }
//    outputHeap();
    assert heapLength >= 0 : "heapLength: " + heapLength;
    if (heapLength == 0)
      return false;
//    System.out.printf("%d %d %d %d %d\n", heap[1], heap[2], my[heap[1]].value(), my[heap[2]].value(), heapLength);
    myNext = my[heap[1]].value();
    while (my[heap[1]].value() == myNext && heapLength > 0) {
      if (my[heap[1]].hasNext()) {
        long prev = my[heap[1]].value();
        my[heap[1]].next();
        assert prev < my[heap[1]].value() : heap[1] + " " + prev + " " + my[heap[1]].value();
      } else {
        swap(1, heapLength);
        heapLength--;
      }
      heapify(1);
    }
    return true;
  }

  protected long getNext() {
    //System.out.print(myNext + " ");
    return myNext;
  }
}