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



package com.almworks.integers;

import com.almworks.integers.func.IntIntProcedure;
import com.almworks.integers.func.#E#Functions;
import com.almworks.integers.func.#E##E#To#E#;
import com.almworks.integers.func.#E#To#E#;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps integer keys to integer values. <br/>
 * Keys and values are sorted, and it is possible to retrieve either value by key or key by value. However, mappings are still added only by key. <br/>
 * The mapping is stored in a separate list in the following way: if {@code (k, v)} is a stored pair, then {@code v = vals[idxMap[i]]}, where {@code k = keys[i]}.
 * */
public class #E#TwoWayMap implements #E##E#Map {
  private final #E#Array myKeys = new #E#Array();
  private final IntArray myIdxMap = new IntArray();
  private final #E#Array myValues = new #E#Array();

  public boolean containsKey(#e# key) {
    return myKeys.binarySearch(key) >= 0;
  }

  public boolean containsAllKeys(#E#List keys) {
    return containsKeys(keys, true);
  }

  public boolean containsAnyKeys(#E#List keys) {
    return containsKeys(keys, false);
  }

  public boolean containsAnyKeys(#E#Iterable keys) {
    return containsKeys(keys);
  }

  @Override
  public boolean containsKeys(#E#Iterable keys) {
    for (#E#Iterator key : keys) {
      if (!containsKey(key.value())) {
        return false;
      }
    }
    return true;
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

  public boolean containsValue(#e# val) {
    return myValues.binarySearch(val) >= 0;
  }

  /** Throws {@link IllegalArgumentException} if the map does not contain the mapping for the key.
   * Call {@link #containsKey} to ensure that there is a mapping. */
  public #e# get(#e# key) throws IllegalArgumentException {
    int i = myKeys.binarySearch(key);
    if (i < 0) throw new IllegalArgumentException("Key " + key + " is not contained in " + this);
    return myValues.get(myIdxMap.get(i));
  }

  public #E#List getKeys() {
    return myKeys;
  }

  public #E#List getValues() {
    return myValues;
  }

  public void clear() {
    myKeys.clear();
    myIdxMap.clear();
    myValues.clear();
  }

  public int size() {
    assert myKeys.size() == myValues.size();
    return myKeys.size();
  }

  @Override
  public boolean isEmpty() {
    assert myKeys.isEmpty() == myValues.isEmpty();
    return myKeys.isEmpty();
  }

  /**
   * @return pair-iterator over this map in the sorted by key order.
   */
  @NotNull
  @Override
  public #E##E#Iterator iterator() {
    return new #E##E#PairIterator(myKeys, new #E#IndexedIterator(myValues, myIdxMap.iterator()));
  }

  @Override
  public #E#Iterator keysIterator() {
    return myKeys.iterator();
  }

  /**
   * Note, that this iterator isn't equal to right projection of {@link #iterator()}
   * @return iterator over values of this map in the sorted order.
   */
  @Override
  public #E#Iterator valuesIterator() {
    return myValues.iterator();
  }

  @Override
  public #E#Set keySet() {
    return #E#ListSet.setFromSortedUniqueList(myKeys);
  }

  public List<Entry> toList() {
    int n = size();
    List<Entry> l = new ArrayList<Entry>(n);
    for (int i = 0; i < n; ++i) {
      l.add(new Entry(myKeys.get(i), myValues.get(myIdxMap.get(i))));
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
      ret = myValues.removeAt(oldPos);
      int newPos = myValues.binarySearch(val);
      if (newPos < 0) newPos = -newPos - 1;
      myValues.insert(newPos, val);
      shiftValueIndexes(oldPos, newPos);
      myIdxMap.set(i, newPos);
    } else {
      ret = val;
      int ki = -i - 1;
      myKeys.insert(ki, key);
      int vi = myValues.binarySearch(val);
      if (vi < 0) vi = -vi - 1;
      myValues.insert(vi, val);
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
    assert myKeys.size() == myIdxMap.size() && myKeys.size() == myValues.size() : action + '\n' + myKeys + '\n' + myIdxMap + '\n' + myValues;
    assert myKeys.isSorted() : action + ' ' + myKeys;
    assert myValues.isSorted() : action + ' ' + myValues;
    assert checkIdxMap() : action + ' ' + myIdxMap;
    return true;
  }

  private boolean checkIdxMap() {
    IntArray sorted = new IntArray(myIdxMap);
    sorted.sort();
    return sorted.isSortedUnique();
  }

  public void insertAllRo(#E#List keys, #E#List vals) {
    insertAll(new #E#Array(keys), new #E#Array(vals));
  }

  public void insertAllRo(#E#List keys, #E#To#E# keyToValue) {
    insertAll(new #E#Array(keys), keyToValue);
  }

  public void insertAll(Writable#E#List keys, #E#To#E# keyToValue) {
    int m = keys.size();
    #E#Array vals = new #E#Array(m);
    for (int i = 0; i < m; ++i) vals.add(keyToValue.invoke(keys.get(i)));
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
    int duplicateKeyIdx = #E#Collections.findDuplicate(keys);
    if (duplicateKeyIdx >= 0) throw new IllegalArgumentException("Duplicate key " + keys.get(duplicateKeyIdx));

    if (!vals.isSorted()) vals.sort(keys);
    IntArray insPoints = new IntArray(m);
    int ins = 0;
    for (int i = 0; i < m; ++i) {
      #e# val = vals.get(i);
      ins = myValues.binarySearch(val, ins, n);
      if (ins < 0) ins = -ins - 1;
      insPoints.add(ins);
    }
    // Fix current value indices
    for (int i = 0; i < n; ++i) {
      int pos = myIdxMap.get(i);
      // diff = "how many items will be inserted before i-th value index"
      int diff = insPoints.binarySearch(pos + 1);
      if (diff < 0) diff = -diff - 1;
      myIdxMap.set(i, pos + diff);
    }
    // Insert values, keys, and value indices
    int insDiff = 0;
    for (int i = 0; i < m; ) {
      ins = insPoints.get(i);
      int s = i;
      for (; i < m && insPoints.get(i) == ins; ++i) {
        #e# key = keys.get(i);
        int ki = myKeys.binarySearch(key);
        if (ki >= 0) {
          assert false : key;
          throw new IllegalArgumentException("#E#TwoWayMap is broken");
        }
        ki = -ki - 1;
        myKeys.insert(ki, key);
        myIdxMap.insert(ki, ins + insDiff + (i - s));
      }
      myValues.insertAll(ins + insDiff, vals.subList(s, i));
      insDiff += i - s;
    }
    assert checkInvariants(keys + "\n" + vals);
  }

  /** Transforms each value using the specified function. Values are supplied in ascending order.<br/>
   * Memory: O(n). */
  public void transformValues(@NotNull #E#To#E# fun) {
    transformValues(#EW#.MIN_VALUE, fun);
  }

  /** Transforms each value using the specified function. Values are supplied in ascending order.<br/>
   * Memory: O(n). */
  public void transformValues(#e# valFrom, @NotNull #E#To#E# fun) {
    int n = size();
    boolean isSortingBroken = false;
    #e# lastValue = #EW#.MIN_VALUE;
    int viFrom = myValues.binarySearch(valFrom);
    if (viFrom < 0) viFrom = -viFrom - 1;
    for (int vi = viFrom; vi < n; ++vi) {
      #e# val = fun.invoke(myValues.get(vi));
      myValues.set(vi, val);
      if (val < lastValue) isSortingBroken = true;
      lastValue = val;
    }
    if (isSortingBroken) {
      restoreIndexMap(n);
    }
    assert checkInvariants(String.valueOf(fun));
  }

  /** Transforms the value of each mapping using the specified function (key, val). Mappings are supplied in ascending order by key. */
  public void transformValues(@NotNull #E##E#To#E# fun) {
    int n = size();
    for (int ki = 0; ki < n; ++ki) {
      int vi = myIdxMap.get(ki);
      myValues.set(vi, fun.invoke(myKeys.get(ki), myValues.get(vi)));
    }
    if (!myValues.isSorted()) {
      restoreIndexMap(n);
    }
    assert checkInvariants(String.valueOf(fun));
  }

  private void restoreIndexMap(int n) {
    // Sort the values remembering the sorting transposition, r
    final IntArray r = new IntArray(IntProgression.arithmetic(0, n));
    IntegersUtils.quicksort(n,
        // order
        #E#Functions.comparator(myValues),
        // swap
        new IntIntProcedure() {
          @Override
          public void invoke(int i, int j) {
            myValues.swap(i, j);
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

  /** Updates keys of the mappings using the specified function. Function must be injective; if duplicate key is generated, {@link NonInjectiveFunctionException} is thrown. */
  public void transformKeys(#E#To#E# injection) throws NonInjectiveFunctionException {
    #E#Array newKeys = new #E#Array(#E#Collections.map(injection, myKeys));
  
    IntArray newIdxMap = new IntArray(myIdxMap);
    sort(newKeys, newIdxMap);
    int dupIdx = #E#Collections.findDuplicateSorted(newKeys);
    if (dupIdx >= 0) throw new NonInjectiveFunctionException(newKeys.get(dupIdx), injection + " is not an injective function: generated duplicate key " + newKeys.get(dupIdx) + ", value: " + myValues.get(newIdxMap.get(dupIdx)));
    myKeys.clear();
    myIdxMap.clear();
    myKeys.addAll(newKeys);
    myIdxMap.addAll(newIdxMap);
  
    assert checkInvariants(String.valueOf(injection));
  }

  private static void sort(final Writable#E#List main, final WritableIntList parallel) {
    assert main.size() == parallel.size();
    // We cannot use PArray.sort(PArray... sortAlso) because types are different
    IntegersUtils.quicksort(main.size(),
        // compare
        #E#Functions.comparator(main),
        // swap
        new IntIntProcedure() {
          @Override
          public void invoke(int a, int b) {
            main.swap(a, b);
            parallel.swap(a, b);
          }
        }
    );
  }

  /** Removes the mapping specified by the key.
   * @throws IllegalArgumentException if there is no mapping for the key; check {@link #containsKey} before calling this method. */
  public #e# remove(#e# key) throws IllegalArgumentException {
    int ki = myKeys.binarySearch(key);
    if (ki >= 0) {
      myKeys.removeAt(ki);
      int vi = myIdxMap.removeAt(ki);
      #e# val = myValues.removeAt(vi);
      shiftValueIndexes(vi, myIdxMap.size());
      assert checkInvariants(key + " " + val);
      return val;
    } else throw new IllegalArgumentException("Cannot remove: no mapping for key " + key);
  }

  /** Removes mappings for those keys that are contained in the map. Keys not contained in the map are returned.<br/>
   * Time: O (n log m) if keys are sorted or O (m log n + n log m) otherwise. <Br/>
   * Space: O(m).
   * @return keys from the input list that are not contained in the map */
  public #E#List removeAll(#E#List keys) {
    Writable#E#List notInMap = null;

    #E#Array vsToRemove = new #E#Array();
    IntArray visToRemove = new IntArray();
    #e# lastK = #EW#.MIN_VALUE;
    boolean keysSorted = true;
    int ki = 0;
    // Remove mappings and keys; values will be removed after we fix the rest of the index map
    for (#E#ListIterator kIt = keys.iterator(); kIt.hasNext(); ) {
      #e# k = kIt.nextValue();
      keysSorted &= k > lastK;
      lastK = k;
      if (!keysSorted) ki = 0;
      ki = myKeys.binarySearch(k, ki, myKeys.size());
      if (ki < 0) {
        if (!keys.subList(0, kIt.index()).contains(k))
          (notInMap == null ? notInMap = new #E#Array() : notInMap).add(k);
        ki = -ki - 1;
      } else {
        myKeys.removeAt(ki);
        int vi = myIdxMap.removeAt(ki);
        visToRemove.add(vi);
        vsToRemove.add(myValues.get(vi));
      }
    }

    sort(vsToRemove, visToRemove);
    removeValuesSorted0(vsToRemove, visToRemove);

    assert checkInvariants(String.valueOf(keys));
    return notInMap == null ? #E#Array.EMPTY : notInMap;
  }

  private void removeValuesSorted0(Writable#E#List vsToRemove, WritableIntList visToRemove) {
    // Fix index map before removing values: we have to shift remaining values back
    int mRem = vsToRemove.size();
    int nLeft = myIdxMap.size();
    for (int i = 0; i < nLeft; ++i) {
      int vi = myIdxMap.get(i);
      #e# v = myValues.get(vi);
      int d = vsToRemove.binarySearch(v);
      if (d < 0) d = -d - 1;
      else {
        // Consider the following example:
        // vis:        [0 1 2 3 4]
        // myValues:      1 2 3 3 3
        // vsToRemove:  ^     ^
        // Here, element with vi = 2 should be shifted 1 position left and with vi = 4 -- 2 positions left.
        // In other words, we have to scroll through all values being removed that are equal to the current value, v, and decrement current index, vi, by the amount of removed values to the left of v in myValues.
        // Note that d denotes the leftmost element of vsToRemove that is equal to v (but not necessarily the leftmost equal to v in myValues).
        for (int j = d; j < mRem && vsToRemove.get(j) == v; ++j) {
          if (visToRemove.get(j) < vi)
            d += 1;
        }
      }
      myIdxMap.set(i, vi - d);
    }

    // Remove values
    visToRemove.sort();
    #E#Collections.removeAllAtSorted(myValues, visToRemove);
  }

  @NotNull
  public #E#List removeAllValuesRo(#E#List vals) {
    return removeAllValues(new #E#Array(vals));
  }

  /** @return list of values that have not been removed (not contained in the map) */
  @NotNull
  public #E#List removeAllValues(Writable#E#List vals) {
    if (!vals.isSorted()) vals.sort();
    vals.removeDuplicates();
    int m = vals.size();
    int n = size();

    IntArray visToRemove = new IntArray(m);
    #E#Array vsToRemove = new #E#Array(m);
    #E#List notInMap = fillVsToRemove(vals, m, n, visToRemove, vsToRemove);

    IntArray kisToRemove = new IntArray(m);
    for (int i = 0; i < n; ++i) {
      int vi = myIdxMap.get(i);
      if (visToRemove.binarySearch(vi) >= 0) {
        kisToRemove.add(i);
      }
    }
    #E#Collections.removeAllAtSorted(myKeys, kisToRemove);
    IntCollections.removeAllAtSorted(myIdxMap, kisToRemove);
    removeValuesSorted0(vsToRemove, visToRemove);

    assert checkInvariants(String.valueOf(vals));
    return notInMap == null ? #E#List.EMPTY : notInMap;
  }

  @Nullable
  private #E#List fillVsToRemove(Writable#E#List vals, int m, int n, IntArray visToRemove, #E#Array vsToRemove) {
    Writable#E#List notInMap = null;
    int vi = 0;
    for (int i = 0; i < m; ++i) {
      #e# v = vals.get(i);
      boolean inMap = false;
      do {
        int nvi = myValues.binarySearch(v, vi, n);
        if (nvi < 0) break;
        vi = nvi;
        visToRemove.add(vi);
        vsToRemove.add(v);
        inMap = true;
        vi += 1;
      } while (true);
      if (!inMap) (notInMap == null ? notInMap = new #E#Array() : notInMap).add(v);
    }
    return notInMap;
  }

  @Override
  public String toString() {
    return "K: " + myKeys + "\nM: " + myIdxMap + "\nV: " + myValues;
  }

  public static class Entry {
    public final #e# key;
    public final #e# val;

    public Entry(#e# key, #e# val) {
      this.key = key;
      this.val = val;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Entry entry = (Entry) o;
      return key == entry.key && val == entry.val;
    }

    @Override
    public int hashCode() {
      int result = #EW#.valueOf(key).hashCode();
      result = 31 * result + #EW#.valueOf(val).hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "(" + key + ", " + val + ')';
    }
  }

  public static class NonInjectiveFunctionException extends IllegalArgumentException {
    private final #e# myDuplicateValue;

    public NonInjectiveFunctionException(#e# duplicateValue, String msg) {
      super(msg);
      myDuplicateValue = duplicateValue;
    }

    public #e# getDuplicateValue() {
      return myDuplicateValue;
    }
  }
}
