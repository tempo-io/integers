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

// CODE GENERATED FROM com/almworks/integers/PListConcatenation.tpl




package com.almworks.integers;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * List that allows to access a concatenation of several lists (here called slices) as a list
 * as if they go one after the other.
 * <br>Changes in lists propagate to this list — added / removed values are immediately visible through this list.
 * <br>Because of that, {@link #get(int)}, {@link #indexOf(int)}, {@link #size()}, {@link #isEmpty()} are O(n).
 * <br>Slices can be added during the lifetime of the list.
 */
public class IntListConcatenation extends AbstractIntList {
  private final List<IntList> mySlices = IntegersUtils.arrayList();

  public IntListConcatenation() {
  }

  public IntListConcatenation(IntList... collections) {
    mySlices.addAll(Arrays.asList(collections));
  }

  public int[] toNativeArray(int sourceOffset, int[] dest, int destOffset, int length) {
    int slices = mySlices.size();
    for (int i = 0; i < slices && length > 0; i++) {
      IntList list = mySlices.get(i);
      int size = list.size();
      if (sourceOffset >= size) {
        sourceOffset -= size;
      } else {
        int x = Math.min(size - sourceOffset, length);
        list.toNativeArray(sourceOffset, dest, destOffset, x);
        destOffset += x;
        sourceOffset = 0;
        length -= x;
      }
    }
    return dest;
  }

  public int indexOf(int value) {
    int p = 0;
    for (IntList slice : mySlices) {
      int k = slice.indexOf(value);
      if (k >= 0)
        return p + k;
      p += slice.size();
    }
    return -1;
  }

  public int size() {
    int size = 0;
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0; i < mySlices.size(); i++) {
      IntList list = mySlices.get(i);
      size += list.size();
    }
    return size;
  }

  public int get(int index) {
    // Rewrite iterator(int, int) before replacing FOR with FOR-EACH
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0; i < mySlices.size(); i++) {
      IntList list = mySlices.get(i);
      int size = list.size();
      if (size > index)
        return list.get(index);
      index -= size;
    }
    throw new IndexOutOfBoundsException();
  }

  public boolean isEmpty() {
    for (IntList list : mySlices) {
      if (!list.isEmpty())
        return false;
    }
    return true;
  }

  @NotNull
  public IntListIterator iterator(int from, int to) {
    if (from >= to) {
      assert from == to : from + " " + to;
      return IntIterator.EMPTY;
    }
    // todo effective
    return super.iterator(from, to);
  }

  public int getSliceCount() {
    return mySlices.size();
  }

  public void addSlice(IntList collection) {
    mySlices.add(collection);
  }

  public static IntList concatUnmodifiable(IntList... lists) {
    if (lists == null || lists.length == 0)
      return EMPTY;
    int count = 0;
    IntList lastNonEmpty = null;
    for (IntList col : lists) {
      if (!col.isEmpty()) {
        count++;
        lastNonEmpty = col;
      }
    }
    if (count == 0)
      return EMPTY;
    if (count == 1)
      return lastNonEmpty;
    IntListConcatenation r = new IntListConcatenation();
    for (IntList col : lists) {
      if (!col.isEmpty())
        r.addSlice(col);
    }
    return r;
  }
}
