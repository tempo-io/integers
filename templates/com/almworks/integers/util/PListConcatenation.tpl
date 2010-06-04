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
package com.almworks.integers.util;

import com.almworks.integers.Abstract#E#List;
import com.almworks.integers.#E#Iterator;
import com.almworks.integers.#E#List;
import com.almworks.integers.#E#ListIterator;
import com.almworks.integers.IntegersUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Arrays;

public class #E#ListConcatenation extends Abstract#E#List {
  private final List<#E#List> mySlices = IntegersUtils.arrayList();

  public #E#ListConcatenation() {
  }

  public #E#ListConcatenation(#E#List... collections) {
    mySlices.addAll(Arrays.asList(collections));
  }

  public #e#[] toArray(int sourceOffset, #e#[] dest, int destOffset, int length) {
    int slices = mySlices.size();
    for (int i = 0; i < slices && length > 0; i++) {
      #E#List list = mySlices.get(i);
      int size = list.size();
      if (sourceOffset >= size) {
        sourceOffset -= size;
      } else {
        int x = Math.min(size - sourceOffset, length);
        list.toArray(sourceOffset, dest, destOffset, x);
        destOffset += x;
        sourceOffset = 0;
        length -= x;
      }
    }
    return dest;
  }

  public int indexOf(#e# value) {
    int p = 0;
    for (#E#List slice : mySlices) {
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
      #E#List list = mySlices.get(i);
      size += list.size();
    }
    return size;
  }

  public #e# get(int index) {
    // Rewrite iterator(int, int) before replacing FOR with FOR-EACH
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0; i < mySlices.size(); i++) {
      #E#List list = mySlices.get(i);
      int size = list.size();
      if (size > index)
        return list.get(index);
      index -= size;
    }
    throw new IndexOutOfBoundsException();
  }

  public boolean isEmpty() {
    for (#E#List list : mySlices) {
      if (!list.isEmpty())
        return false;
    }
    return true;
  }

  @NotNull
  public #E#ListIterator iterator(int from, int to) {
    if (from >= to) {
      assert from == to : from + " " + to;
      return #E#Iterator.EMPTY;
    }
    // todo effective
    return super.iterator(from, to);
  }

  public int getSliceCount() {
    return mySlices.size();
  }

  public void addSlice(#E#List collection) {
    mySlices.add(collection);
  }

  public static #E#List concatUnmodifiable(#E#List... lists) {
    if (lists == null || lists.length == 0)
      return EMPTY;
    int count = 0;
    #E#List lastNonEmpty = null;
    for (#E#List col : lists) {
      if (!col.isEmpty()) {
        count++;
        lastNonEmpty = col;
      }
    }
    if (count == 0)
      return EMPTY;
    if (count == 1)
      return lastNonEmpty;
    #E#ListConcatenation r = new #E#ListConcatenation();
    for (#E#List col : lists) {
      if (!col.isEmpty())
        r.addSlice(col);
    }
    return r;
  }
}
