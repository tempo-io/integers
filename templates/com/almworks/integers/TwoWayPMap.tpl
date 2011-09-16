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

import com.almworks.integers.func.*;
import org.jetbrains.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps integer keys to integer values. <br/>
 * Keys and values are sorted, and it is possible to retrieve either value by key or key by value. However, mappings are still added only by key. <br/>
 * The mapping is stored in a separate list in the following way: if {@code (k, v)} is a stored pair, then {@code v = vals[idxMap[i]]}, where {@code k = keys[i]}.
 * */
public class TwoWay#E#Map {
  private final #E#Array myKeys = new #E#Array();
  private final IntArray myIdxMap = new IntArray();
  private final #E#Array myVals = new #E#Array();

  public boolean containsKey(#e# key) {
    return myKeys.contains(key);
  }

  public boolean containsAllKeys(#E#List keys) {
    return containsKeys(keys, true);
  }

  public boolean containsAnyKeys(#E#List keys) {
    return containsKeys(keys, false);
  }

  public boolean containsKeys(#E#List keys, boolean all) {
    #EW# key;
    if (keys.isSorted()) {
      key = containsKeysSorted(keys, all);
    } else {
      key = containsKeysUnsorted(keys, all);
    }
    return (key == null) == all;
  }

  /** @return returns the first key from <tt>keys</tt> that (is contained in the map if <tt>shouldContain</tt> and is not contained in the map otherwise) if such key exists, otherwise <tt>null</tt>. */
  @Nullable
  private #EW# containsKeysSorted(#E#List keys, boolean shouldContain) {
    for (int i = 0,
      m = keys.size(),
      pos = 0,
      n = myKeys.size()
        ; i < m; ++i)
    {
      #e# key = keys.get(i);
      pos = myKeys.binarySearch(key, pos, n);
      if (shouldContain ? pos < 0 : pos >= 0) return key;
      if (pos < 0) pos = -pos - 1;
    }
    return null;
  }

  /** @return returns the first key from <tt>keys</tt> that (is contained in the map if <tt>shouldContain</tt> and is not contained in the map otherwise) if such key exists, otherwise <tt>null</tt>. */
  @Nullable
  private #EW# containsKeysUnsorted(#E#List keys, boolean shouldContain) {
    for (int i = 0, iEnd = keys.size(); i < iEnd; ++i) {
      #e# key = keys.get(i);
      if (containsKey(key) != shouldContain) return key;
    }
    return null;
  }

  public boolean containsVal(#e# val) {
    return myVals.contains(val);
  }

  /** Throws {@link IllegalArgumentException} if the map does not contain the mapping for the key.
   * Call {@link #containsKey} to ensure that there is a mapping. */
  public #e# get(#e# key) throws IllegalArgumentException {
    int i = myKeys.binarySearch(key);
    if (i < 0) throw new IllegalArgumentException("Key " + key + " is not contained in " + this);
    return myVals.get(myIdxMap.get(i));
  }

  public #E#List getKeys() {
    return myKeys;
  }

  public #E#List getVals() {
    return myVals;
  }

  public void clear() {
    myKeys.clear();
    myIdxMap.clear();
    myVals.clear();
  }

  public int size() {
    return myKeys.size();
  }

  public List<Entry> toList() {
    int n = size();
    List<Entry> l = new ArrayList<Entry>(n);
    for (int i = 0; i < n; ++i) {
      l.add(new Entry(myKeys.get(i), myVals.get(myIdxMap.get(i))));
    }
    return l;
  }

  /**
   * Adds a mapping from {@code key} to {@code val}. <br/>
   * In case val is greater than all current values, insertion is O(1), otherwise, O(size()). For multiple insertions, better call {@link #insertAll}.
   * @return in case there was previously a mapping for {@code key}, returns the old value; otherwise, {@code val}. <br/>
   * The return value policy if this method differs from {@link java.util.Map#put}: the latter answers two questions: "have we added a mapping" and "have we modified an existing mapping",
   * while this method answers only one: "have we modified an existing mapping" but does not create a wrapper to return the result.
   * If the first question is needed, create another method with wrapper return type.
   * */
  public #e# put(#e# key, #e# val) {
    int i = myKeys.binarySearch(key);
    #e# ret;
    if (i >= 0) {
      int oldPos = myIdxMap.get(i);
      ret = myVals.removeAt(oldPos);
      int newPos = myVals.binarySearch(val);
      if (newPos < 0) newPos = -newPos - 1;
      myVals.insert(newPos, val);
      shiftValueIndexes(oldPos, newPos);
      myIdxMap.set(i, newPos);
    } else {
      ret = val;
      int ki = -i - 1;
      myKeys.insert(ki, key);
      int vi = myVals.binarySearch(val);
      if (vi < 0) vi = -vi - 1;
      myVals.insert(vi, val);
      shiftValueIndexes(myIdxMap.size(), vi);
      myIdxMap.insert(ki, vi);
    }
    assert checkInvariants(key + " " + ret + ' ' + val);
    return ret;
  }

  private void shiftValueIndexes(int oldPos, int newPos) {
    int inc = newPos > oldPos ? -1 : +1;
    if (newPos != oldPos) {
      for (int i = 0, iEnd = myIdxMap.size(); i < iEnd; ++i) {
        int pos = myIdxMap.get(i);
        if (inc > 0
          ? pos >= newPos && pos < oldPos
          : pos > oldPos && pos <= newPos)
        {
          myIdxMap.set(i, pos + inc);
        }
      }
    }
  }

  private boolean checkInvariants(String action) {
    assert myKeys.size() == myIdxMap.size() && myKeys.size() == myVals.size() : action + '\n' + myKeys + '\n' + myIdxMap + '\n' + myVals;
    assert myKeys.isSorted() : action + ' ' + myKeys;
    assert myVals.isSorted() : action + ' ' + myVals;
    assert checkIdxMap() : action + ' ' + myIdxMap;
    return true;
  }

  private boolean checkIdxMap() {
    IntArray sorted = new IntArray(myIdxMap);
    sorted.sort();
    return sorted.isUniqueSorted();
  }

  public void insertAllRo(#E#List keys, #E#List vals) {
    insertAll(new #E#Array(keys), new #E#Array(vals));
  }

  public void insertAll(Writable#E#List keys, #E#Function keyToVal) {
    int m = keys.size();
    #E#Array vals = new #E#Array(m);
    for (int i = 0; i < m; ++i) vals.add(keyToVal.invoke(keys.get(i)));
    insertAll(keys, vals);
  }

  /**
   * Adds mappings from {@code keys.get(i)} to {@code vals.get(i)} for all {@code i}. If sizes of {@code keys} and {@code vals} are not equal, throws {@link IllegalArgumentException}.<br/>
   * Does not support updating mappings: if for any key from {@code keys} {@link #containsKey containsKey(key)}, throws {@link IllegalArgumentException}. Also, the specified keys array should not contain duplicates. <Br/>
   * Parameter lists are changed as a result of calling this method. <br/>
   * Complexity: {@code O(m*log(m) + m*log(n) + n*log(m))}, where {@code n = size()} is the size of this map before calling this method, and {@code m = keys.size()}.
   * */
  public void insertAll(Writable#E#List keys, Writable#E#List vals) throws IllegalArgumentException {
    int m = keys.size();
    int n = myIdxMap.size();

    if (vals.size() != m) throw new IllegalArgumentException("Sizes of keys and values lists are not equal: " + m + " keys, but " + vals.size() + " values");
    #EW# violatingKey = containsKeysUnsorted(keys, false);
    if (violatingKey != null) throw new IllegalArgumentException("Cannot insert multiple mappings because key " + violatingKey + " is already contained");
    #EW# duplicateKey = findDuplicate(keys);
    if (duplicateKey != null) throw new IllegalArgumentException("Duplicate key " + duplicateKey);

    if (!vals.isSorted()) vals.sort(keys);
    IntArray insPoints = new IntArray(m);
    int ins = 0;
    for (int i = 0; i < m; ++i) {
      #e# val = vals.get(i);
      ins = myVals.binarySearch(val, ins, n);
      if (ins < 0) ins = -ins - 1;
      insPoints.add(ins);
    }
    // Fix current value indexes
    for (int i = 0; i < n; ++i) {
      int pos = myIdxMap.get(i);
      // diff = "how many items will be inserted before i-th value index"
      int diff = insPoints.binarySearch(pos + 1);
      if (diff < 0) diff = -diff - 1;
      myIdxMap.set(i, pos + diff);
    }
    // Insert values, keys, and value indexes
    int insDiff = 0;
    for (int i = 0; i < m; ) {
      ins = insPoints.get(i);
      int s = i;
      for (; i < m && insPoints.get(i) == ins; ++i) {
        #e# key = keys.get(i);
        int ki = myKeys.binarySearch(key);
        if (ki >= 0) {
          assert false : key;
          throw new IllegalArgumentException("TwoWay#E#Map is broken");
        }
        ki = -ki - 1;
        myKeys.insert(ki, key);
        myIdxMap.insert(ki, ins + insDiff + (i - s));
      }
      myVals.insertAll(ins + insDiff, vals.subList(s, i));
      insDiff += i - s;
    }
    assert checkInvariants(keys + "\n" + vals);
  }

  private #EW# findDuplicate(#E#List keys) {
    #E#Array sorted = new #E#Array(keys);
    sorted.sort();
    #EW# prev = null;
    for (int i = 0, m = keys.size(); i < m; ++i) {
      #e# k = keys.get(i);
      if (prev != null && prev == k) return k;
      prev = k;
    }
    return null;
  }

  /** Transforms each value using the specified function. */
  public void transformVals(@NotNull #E#Function f) {
    int n = size();
    boolean isSortingBroken = false;
    #e# lastVal = #EW#.MIN_VALUE;
    for (int i = 0; i < n; ++i) {
      #e# val = f.invoke(myVals.get(i));
      myVals.set(i, val);
      if (val < lastVal) isSortingBroken = true;
      lastVal = val;
    }
    if (isSortingBroken) {
      restoreIndexMap(n);
    }
    assert checkInvariants(String.valueOf(f));
  }

  /** Transforms the value of each mapping using the specified function (key, val). */
  public void transformVals(@NotNull #E#Function2 f) {
    int n = size();
    for (int ki = 0; ki < n; ++ki) {
      int vi = myIdxMap.get(ki);
      myVals.set(vi, f.invoke(myKeys.get(ki), myVals.get(vi)));
    }
    if (!myVals.isSorted()) {
      restoreIndexMap(n);
    }
    assert checkInvariants(String.valueOf(f));
  }

  private void restoreIndexMap(int n) {
    // Sort the values remembering the sorting transposition, r
    final IntArray r = new IntArray(IntProgression.arithmetic(0, n));
    IntegersUtils.quicksort(n,
      // order
      new IntFunction2() {
        @Override
        public int invoke(int i, int j) {
          return #E#Collections.compare(myVals.get(i), myVals.get(j));
        }
      },
      // swap
      new IntProcedure2() {
        @Override
        public void invoke(int i, int j) {
          myVals.swap(i, j);
          r.swap(i, j);
        }
      }
    );
    // The trickier part is to restore the index map.
    // Previously, we had k = v*p where k - keys, v - values, p - index map (effectively, a transposition); a*p is a new vector,b, such as b[i] = a[p[i]].
    // Now, v' = v*r, so p' = r^-1*p because k = v*p = v*r*r^-1*p = (v*r)*(r^-1*p), where (v*r) is the new values list, and (r^-1*p) is the new index map.
    // Calculate r^-1 ("r reciprocal")
    IntArray rRecip = new IntArray(new int[n]);
    for (int i = 0; i < n; ++i) {
      rRecip.set(r.get(i), i);
    }
    // newIdxMap = r^-1*p
    IntArray newIdxMap = new IntArray(myIdxMap);
    for (int i = 0; i < n; ++i) {
      newIdxMap.set(i, rRecip.get(myIdxMap.get(i)));
    }
    myIdxMap.setAll(0, newIdxMap);
  }

  public static class Entry {
    public final #e# key;
    public final #e# val;

    private Entry(#e# key, #e# val) {
      this.key = key;
      this.val = val;
    }

    @Override
    public String toString() {
      return "(" + key + ", " + val + ')';
    }
  }
}
