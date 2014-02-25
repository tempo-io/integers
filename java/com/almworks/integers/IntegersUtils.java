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
import com.almworks.integers.func.IntIntToInt;
import com.almworks.integers.func.IntProcedure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * Utilities from everywhere on which integer collections depend.
 */
public final class IntegersUtils {
  // Copied from Const.*
  public static final byte[] EMPTY_BYTES = {};
  public static final int[] EMPTY_INTS = {};
  public static final long[] EMPTY_LONGS = {};
  public static final short[] EMPTY_SHORTS = {};
  public static final int MAX_INT = Integer.MAX_VALUE;
  public static final long MAX_LONG = Long.MAX_VALUE;

  /**
   * Returns string's substring that starts after the last occurence of the separator. If separator is not encountered,
   * returns the whole string.
   *
   * @param string    the original string to look in.
   * @param separator a string that is looked for
   * @return substring of a string that starts after the last occurence of the separator
   */
  // Copied from TextUtil.substringAfterLast
  public static String substringAfterLast(String string, String separator) {
    if (string == null || separator == null)
      return string;
    int k = string.lastIndexOf(separator);
    if (k < 0)
      return string;
    else
      return string.substring(k + separator.length());
  }

  public static StringBuilder appendShortName(StringBuilder builder, Object obj) {
    String name = obj.getClass().getSimpleName();
    // LongAmortizedSet -> LAS, LongTreeSet -> LTS
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if ('A' <= c && c <= 'Z') {
        builder.append(c);
      }
    }
    return builder;
  }

  // Copied from Collections15
  public static <T> List<T> arrayList() {
    return new ArrayList<T>();
  }

  /**
   * Performs quicksort. Copied from {@link Arrays#sort(int[])} then corrected.
   * @param length - number of elements to sort
   * @param order - "comparator". Parameter are indices of elements to be compared. Will be invoked with parameters
   * from 0 to <code>length</code>-1 inclusive.
   * @param swap - procedure to swap. Parameters are indices of elements to be swapped. Will be invoked with parameters
   * from 0 to <code>length</code>-1 inclusive.
   */
  // Copied from CollectionUtil
  public static void quicksort(int length, IntIntToInt order, IntIntProcedure swap) {
    sort1(0, length, order, swap);
  }

  private static void sort1(int off, int len, IntIntToInt order, IntIntProcedure swap) {
    // Insertion sort on smallest arrays
    if (len < 7) {
      for (int i = off; i < len + off; i++)
        for (int j = i; j > off && order.invoke(j - 1, j) > 0; j--)
          swap.invoke(j, j - 1);
      return;
    }

    // Choose a partition element, v
    int m = off + (len >> 1);       // Small arrays, middle element
    if (len > 7) {
      int l = off;
      int n = off + len - 1;
      if (len > 40) {        // Big arrays, pseudomedian of 9
        int s = len / 8;
        l = med3(l, l + s, l + 2 * s, order);
        m = med3(m - s, m, m + s, order);
        n = med3(n - 2 * s, n - s, n, order);
      }
      m = med3(l, m, n, order); // Mid-size, med of 3
    }

    // Establish Invariant: v* (<v)* (>v)* v*
    int a = off, b = a, c = off + len - 1, d = c;
    while (true) {
      while (b <= c) {
        int comp = order.invoke(b, m);
        if (comp > 0)
          break;
        if (comp == 0) {
          if (a == m)
            m = b;
          else if (b == m)
            m = a;
          swap.invoke(a++, b);
        }
        b++;
      }
      while (c >= b) {
        int comp = order.invoke(c, m);
        if (comp < 0)
          break;
        if (comp == 0) {
          if (c == m)
            m = d;
          else if (d == m)
            m = c;
          swap.invoke(c, d--);
        }
        c--;
      }
      if (b > c)
        break;
      swap.invoke(b++, c--);
    }

    // Swap partition elements back to middle
    int s, n = off + len;
    s = Math.min(a - off, b - a);
    vecswap(off, b - s, s, swap);
    s = Math.min(d - c, n - d - 1);
    vecswap(b, n - s, s, swap);

    // Recursively sort non-partition-elements
    if ((s = b - a) > 1)
      sort1(off, s, order, swap);
    if ((s = d - c) > 1)
      sort1(n - s, s, order, swap);
  }

  private static void vecswap(int a, int b, int n, IntIntProcedure swap) {
    for (int i = 0; i < n; i++, a++, b++)
      swap.invoke(a, b);
  }

  private static int med3(int a, int b, int c, IntIntToInt order) {
    return (order.invoke(a, b) < 0 ? (order.invoke(b, c) < 0 ? b : order.invoke(a, c) < 0 ? c : a) :
      (order.invoke(b, c) > 0 ? b : order.invoke(a, c) > 0 ? c : a));
  }

  public static void swap(Object[] x, int a, int b) {
    Object t = x[a];
    x[a] = x[b];
    x[b] = t;
  }

  // copied from ArrayUtil
  public static int[] arrayCopy(int[] ints) {
    return arrayCopy(ints, 0, ints.length);
  }

  public static int[] arrayCopy(int[] array, int offset, int length) {
    if (length == 0)
      return EMPTY_INTS;
    int[] copy = new int[length];
    System.arraycopy(array, offset, copy, 0, length);
    return copy;
  }

  public static long[] arrayCopy(long[] array, int offset, int length) {
    if (length == 0)
      return EMPTY_LONGS;
    long[] copy = new long[length];
    System.arraycopy(array, offset, copy, 0, length);
    return copy;
  }

  public static int indexOf(int[] ints, int value) {
    return indexOf(ints, 0, ints.length, value);
  }

  public static int indexOf(int[] ints, int from, int to, int value) {
    for (int i = from; i < to; i++) {
      if (ints[i] == value)
        return i;
    }
    return -1;
  }


  /**
   * Generates all permutations of size <tt>n</tt> with the property that two subsequent permutations differ in a swap of two subsequent elements (Gray property.) <br/>
   * Before this method is called, an initial permutation is assumed. As the method proceeds, it calls the specified procedure with the left index of the pair to be swapped
   * to generate the next permutation. After it returns, all permutations have been generated. <br/>
   * Sample usage:
   * <pre><tt>
   *  // Prints out all permutations of an array.
   *  IntArray someArray = IntArray.create(2, 3, 9);
   *  allPermutations(someArray.size(), new IntProcedure() {
   *    {
   *      // This is needed to print the first permutation
   *      printArray();
   *    }
   *    public void invoke(int swapIdx) {
   *      someArray.swap(swapIdx, swapIdx + 1);
   *      printArray();
   *    }
   *    private void printArray() { System.out.println(someArray); }
   *  }
   * </tt></pre>
   * <br/>
   * @param n size of the permutation, must be > 0 (otherwise NegativeArraySizeException is thrown)
   * @param swapWithNext callback that receives an index of a position to swap with its neighbour on the right (swapPos + 1).
   * It is guaranteed that both of these indices are in the interval [0, n). Each swap produces a new permutation that differs from the previously generated ones. <br/>
   * */
  public static void allPermutations(int n, IntProcedure swapWithNext) {
    // Direct permutation
    int[] r = IntProgression.arithmetic(0, n).toNativeArray();
    // Reverse permutation
    int[] p = IntProgression.arithmetic(0, n).toNativeArray();
    // Number of iteration in the factorial base
    int[] t = new int[n];
    // Direction of each element's movement
    BitSet right = new BitSet(n);
    int j;
    while ((j = incFact(t)) > 0) {
      right.flip(j + 1, n);
      boolean rdir = right.get(j);
      swapWithNext.invoke(rdir ? p[j] : p[j] - 1);
      int k = p[j] + (rdir ? +1 : -1);
      int rk = r[k];
      IntCollections.swap(r, p[j], k);
      IntCollections.swap(p, j, rk);
    }
  }

  public static void reversePerm(IntArray p) {
    int n = p.size();
    boolean[] visited = new boolean[n];
    for (int i = 0; i < n; ++i) {
      if (visited[i]) continue;
      // Reverse the cycle starting at i
      int last = i;
      int j = p.get(i);
      while (true) {
        int next = p.get(j);
        p.set(j, last);
        visited[j] = true;
        if (j == i) break;
        last = j;
        j = next;
      }
    }
  }

  /**
   * Increments a number in the factorial-base.<Br/>
   * Number in the factorial base is represented as
   * {@code a = \overline{a_0 a_1 ... a_{n-1}} = \sum_{i=0}^{n-1}{a_i i!}, 0 \leq a_i < i+1}.<br/>
   * Although in this notation a_0 is useless, this method uses it to report overflow and space waste is minuscule.
   * @return 0-based index of the position that was incremented. 0 means overflow (in that case t[k] = 0 for all k) */
  public static int incFact(int[] t) {
    int i;
    for (i = t.length - 1; i >= 0 && t[i] + 1 > i; --i) ;
    if (i >= 0) t[i] += 1;
    for (int j = i + 1; j < t.length; ++j) t[j] = 0;
    return i;
  }

  /**
   * Copied from hppc.
   * @return the next highest power of two, or the current value if it's already a power of two or zero
   */
  public static int nextHighestPowerOfTwo(int v) {
    v--;
    v |= v >> 1;
    v |= v >> 2;
    v |= v >> 4;
    v |= v >> 8;
    v |= v >> 16;
    v++;
    return v;
  }

  /**
   * Copied from hppc.
   * @return the next highest power of two, or the current value if it's already a power of two or zero
   */
  public static long nextHighestPowerOfTwo(long v) {
    v--;
    v |= v >> 1;
    v |= v >> 2;
    v |= v >> 4;
    v |= v >> 8;
    v |= v >> 16;
    v |= v >> 32;
    v++;
    return v;
  }

  /**
   * MurmurHash3
   * Hashes a 4-byte sequence (Java int).
   */
  public static int hash(int k) {
    k ^= k >>> 16;
    k *= 0x85ebca6b;
    k ^= k >>> 13;
    k *= 0xc2b2ae35;
    k ^= k >>> 16;
    return k;
  }

  /**
   * MurmurHash3
   * Hashes an 8-byte sequence (Java long).
   */
  public static int hash(long k) {
    k ^= k >>> 33;
    k *= 0xff51afd7ed558ccdL;
    k ^= k >>> 33;
    k *= 0xc4ceb9fe1a85ec53L;
    k ^= k >>> 33;

    return (int)k;
  }

  private IntegersUtils() {}
}