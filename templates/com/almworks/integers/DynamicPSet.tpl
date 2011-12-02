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

import com.almworks.integers.func.IntFunction2;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/** A red-black tree implementation of a set. Single-thread access only. <br/>
 * Use if you are frequently adding and querying. */
public class Dynamic#E#Set {
  /** Dummy key for NIL. */
  private static final #e# NIL_DUMMY_KEY = #EW#.MIN_VALUE;
  private static final #e#[] EMPTY_KEYS = new #e#[] { #EW#.MIN_VALUE };
  private static final int[] EMPTY_INDEXES = new int[] { 0 };
  /** Size of arrays, equal to the actual size of the set + 1 (for the NIL.) */
  private int mySize;
  /** Key values. */
  private #e#[] myKeys;
  /** Tree structure: contains indexes into key, left, right, black. */
  private int[] myLeft;
  /** Tree structure: contains indexes into key, left, right, black. */
  private int[] myRight;
  /** Node color : false for red, true for black. */
  private final BitSet myBlack;
  /** Index into key, left, right, black that denotes the current root node. */
  private int myRoot;

  private SoftReference<int[]> myStackCache = new SoftReference<int[]>(IntegersUtils.EMPTY_INTS);

  public Dynamic#E#Set() {
    myKeys = EMPTY_KEYS;
    myLeft = EMPTY_INDEXES;
    myRight = EMPTY_INDEXES;
    myBlack = new BitSet();
    init(false);
  }

  public Dynamic#E#Set(int initialCapacity) {
    initialCapacity += 1;
    myKeys = new #e#[initialCapacity];
    myLeft = new int[initialCapacity];
    myRight = new int[initialCapacity];
    myBlack = new BitSet(initialCapacity);
    init(true);
  }

  private void init(boolean addNil) {
    if (addNil) {
      initNode(0, NIL_DUMMY_KEY);
    }
    myBlack.set(0);
    myRoot = 0;
    mySize = 1;
    myStackCache = new SoftReference<int[]>(IntegersUtils.EMPTY_INTS);
  }

  private void initNode(int x, #e# key) {
    myKeys[x] = key;
    myLeft[x] = 0;
    myRight[x] = 0;
  }

  public void clear() {
    myKeys = EMPTY_KEYS;
    myLeft = EMPTY_INDEXES;
    myRight = EMPTY_INDEXES;
    myBlack.clear();
    init(false);
    assert checkRedBlackTreeInvariants("clear");
  }

  /** @return {@link #EW##MIN_VALUE} in case the set is empty */
  public #e# getMax() {
    return myKeys[traverseToEnd(myRight)];
  }

  /** @return {@link #EW##MIN_VALUE} in case the set is empty */
  public #e# getMin() {
    return myKeys[traverseToEnd(myLeft)];
  }

  private int traverseToEnd(int[] branch) {
    int x = myRoot;
    for (int nextX = branch[x]; nextX != 0; nextX = branch[x]) {
      x = nextX;
    }
    return x;
  }

  public boolean contains(#e# key) {
    int x = myRoot;
    #e# k = myKeys[x];
    while (x != 0 && k != key) {
      x = key < k ? myLeft[x] : myRight[x];
      k = myKeys[x];
    }
    return x != 0;
  }

  public int size() {
    return mySize - 1;
  }

  public boolean isEmpty() {
    boolean ret = myRoot == 0;
    assert (size() == 0) == ret : size() + " " + myRoot;
    return ret;
  }

  public #E#Array to#E#List() {
    #e#[] arr = new #e#[size()];
    int i = 0;
    for (LURIterator it = new LURIterator(); it.hasNext(); ) {
      arr[i++] = myKeys[it.nextValue()];
    }
    return new #E#Array(arr);
  }

  public void addAll(#E#List keys) {
    int[] ps = prepareAdd(keys.size());
    for (#E#Iterator i : keys) {
      add0(i.value(), ps);
    }
  }

  public void addAll(#e#... keys) {
    int[] ps = prepareAdd(keys.length);
    for (#e# key : keys) {
      add0(key, ps);
    }
  }

  public boolean add(#e# key) {
    return add0(key, prepareAdd(1));
  }

  private boolean add0(#e# key, int[] ps) {
    int x = myRoot;
    // Parents stack top + 1
    int psi = 0;
    // actually, k is always overwritten if it is used (psi > 0), but compiler does not know that
    #e# k = 0;
    while (x != 0) {
      k = myKeys[x];
      if (key == k) return false;
      ps[psi++] = x;
      x = key < k ? myLeft[x] : myRight[x];
    }
    // Initialize the node
    x = mySize;
    mySize += 1;
    initNode(x, key);
    // x is RED already (myBlack.get(x) == false), so no modifications to myBlack
    // Insert into the tree
    if (psi == 0) myRoot = x;
    else (key < k ? myLeft : myRight)[ps[psi - 1]] = x;
    balanceAfterAdd(x, ps, psi, key);
    assert checkRedBlackTreeInvariants("key " + key);
    return true;
  }

  /**
   * @param x the node being inserted, RED
   * @param ps ancestors of the node x; ps[0] is the root, ps[psi] is the parent of x
   * @param psi currently used ancestor of x; if psi is < 0, we continue to think that the corresponding ancestor is NIL.
   * @param debugKey a string to assist understanding what were we adding if assertion fails
   * */
  private void balanceAfterAdd(int x, int[] ps, int psi, #e# debugKey) {
    // parent
    int p = getLastOrNil(ps, --psi);
    // grandparent
    int pp = getLastOrNil(ps, --psi);
    while (!myBlack.get(p)) {
      assert checkChildParent(p, pp, debugKey);
      assert checkChildParent(x, p, debugKey);
      boolean branch1IsLeft = p == myLeft[pp];
      int[] branch1 = branch1IsLeft ? myLeft : myRight;
      int[] branch2 = branch1IsLeft ? myRight : myLeft;
      // Uncle (parent's sibling)
      int u = branch2[pp];
      if (!myBlack.get(u)) {
        myBlack.set(p);
        myBlack.set(u);
        myBlack.clear(pp);
        x = pp;
        p = getLastOrNil(ps, --psi);
        pp = getLastOrNil(ps, --psi);
      } else {
        if (x == branch2[p]) {
          // Rotate takes x and makes it a branch1-parent of p
          rotate(p, pp, branch1, branch2);
          // Now x is the parent of p; but we choose x' = p; so p' = x, pp' = pp
          int tmp = x;
          x = p;
          p = tmp;
        }
        myBlack.set(p);
        myBlack.clear(pp);
        int ppp = getLastOrNil(ps, --psi);
        // Takes pp (which is now red) and makes it a branch-2 children of p (which is now black); note that branch-1 children of p is x, which is red and both children are black
        rotate(pp, ppp, branch2, branch1);
      }
    }
    myBlack.set(myRoot);
  }

  private boolean checkChildParent(int child, int parent, #e# debugKey) {
    assert myLeft[parent] == child || myRight[parent] == child : debugMegaPrint("add " + debugKey + "\nproblem with child " + child, parent);
    return true;
  }

  private static int getLastOrNil(int[] a, int i) {
    return i < 0 ? 0 : a[i];
  }

  /**
   * Rotates node x so that it becomes a mainBranch child of its child on the otherBranch.
   * E.g., left rotate: mainBranch == myLeft, rightBranch == myRight
   * */
  private void rotate(int x, int p, int[] mainBranch, int[] otherBranch) {
    int y = otherBranch[x];
    otherBranch[x] = mainBranch[y];
    if (p == 0) myRoot = y;
    else {
      if (x == myLeft[p]) myLeft[p] = y;
      else if (x == myRight[p]) myRight[p] = y;
      else assert false : "tree structure broken " + x + '\n' + dumpArrays(p);
    }
    mainBranch[y] = x;
  }

  /** @return array for holding the stack for tree traversal */
  private int[] prepareAdd(int n) {
    grow(n);
    return fetchStackCacheForAdd(n);
  }

  private void grow(int n) {
    int futureSize = mySize + n;
    myKeys = #E#Collections.ensureCapacity(myKeys, futureSize);
    myLeft = IntCollections.ensureCapacity(myLeft, futureSize);
    myRight = IntCollections.ensureCapacity(myRight, futureSize);
  }

  private int[] fetchStackCacheForAdd(int n) {
    int fh = estimateFutureHeight(n);
    int[] cache = myStackCache.get();
    if (cache != null && cache.length >= fh) return cache;
    cache = new int[fh];
    myStackCache = new SoftReference<int[]>(cache);
    return cache;
  }

  /** Returns a number higher than the height after adding n elements.*/
  private int estimateFutureHeight(int n) {
    return height(mySize + n) + 1;
  }

  /** Estimate tree height: it can be shown that it's <= 2*log_2(N + 1) (not counting the root) */
  private int height(int n) {
    int lg2 = 0;
    while (n > 1) {
      lg2++;
      n >>= 1;
    }
    return lg2 << 1;
  }

  @Override
  public String toString() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    debugPrintTreeStructure(new PrintStream(baos));
    try {
      return baos.toString("US-ASCII");
    } catch (UnsupportedEncodingException e) {
      assert false: e;
      return "Dynamic#E#Set";
    }
  }

  /**
   * Visits the tree in the ULR order (up-left-right.)
   * @param auxInit initial value for the auxiliary function accumulated on the paths from root to leaves
   * @param visitor arguments:
   * <ol>
   *   <li>node index into arrays, x</li>
   *   <li>value of the auxiliary function on the parent of x; if x is the root, equals to <tt>auxInit</tt></li>
   * </ol>
   * Visitor returns the value of auxiliary function on x.
   * */
  private void visitULR(int auxInit, IntFunction2 visitor) {
    int x = myRoot;
    int auxVal = auxInit;
    int height = height(mySize);
    IntArray xs = new IntArray(height);
    IntArray auxVals = new IntArray(height);
    while (true) {
      auxVal = visitor.invoke(x, auxVal);
      int l = myLeft[x];
      int r = myRight[x];
      if (l != 0) {
        x = l;
        // Visit right child even if it's NIL
        xs.add(r);
        auxVals.add(auxVal);
      } else if (r != 0) {
        x = r;
        // Visit NIL child anyway
        visitor.invoke(0, auxVal);
      } else if (!xs.isEmpty()) {
        // Both children are NIL, but it is not shown
        x = xs.removeLast();
        auxVal = auxVals.removeLast();
      } else break;
    }
  }

  private static int log10(#e# n) {
    int log10 = 0;
    do { log10 += 1; n /= 10L; } while (n > 0L);
    return log10;
  }

  private boolean checkRedBlackTreeInvariants(final String whatWasDoing) {
    int sz = myKeys.length;
    assert sz == myLeft.length && sz == myRight.length: whatWasDoing + " | " +  sz + ' ' + myLeft.length  + ' ' + myRight.length;
    assert mySize >= 1 : whatWasDoing + " " + mySize;
    assert (mySize == 1) == isEmpty() : whatWasDoing + " " + mySize + ' ' + myRoot;

    final int[] lastBlackHeight = new int[] {-1};
    visitULR(0, new IntFunction2() {
      @Override
      public int invoke(int x, int bh) {
        if (x == 0)
          return bh;

        // 1. Binary search tree property
        #e# k = myKeys[x];
        int l = myLeft[x];
        #e# lk = myKeys[l];
        if (l != 0)
          assert lk < k : debugMegaPrint(whatWasDoing, x);
        int r = myRight[x];
        #e# rk = myKeys[r];
        if (r != 0)
          assert rk > k : debugMegaPrint(whatWasDoing, x);

        // 2. Red-black tree property-1: If node is red, all its children are black
        boolean nodeIsRed = !myBlack.get(x);
        boolean bothChildrenAreBlack = myBlack.get(l) && myBlack.get(r);
        assert !(nodeIsRed && !bothChildrenAreBlack) : debugMegaPrint(whatWasDoing, x);

        // 3. Read-black tree property-2: number of black nodes (black height) are equal for all paths from root to leaves
        if (myBlack.get(x))
          bh += 1;
        if (l == 0 || r == 0) {
          // We're in a leaf: check our black height against the previous one or record if we're the first
          if (lastBlackHeight[0] < 0)
            lastBlackHeight[0] = bh;
          else
            assert lastBlackHeight[0] == bh : debugMegaPrint(whatWasDoing + ' ' + lastBlackHeight[0] + ' ' + bh, x);
        }

        return bh;
      }
    });

    // 4. Red-black tree property-2: Root is black
    assert myBlack.get(myRoot) : debugMegaPrint(whatWasDoing, myRoot);

    // 5. Height estimate is not less than any actual path height
    final int hEst = height(mySize);
    visitULR(0, new IntFunction2() {
      @Override
      public int invoke(int x, int h) {
        if (myLeft[x] == 0 && myRight[x] == 0) {
          // we're at the bottom
          assert hEst >= h : whatWasDoing + "\n" + h + ' ' + hEst + ' ' + mySize;
        }
        return h + 1;
      }
    });
    return true;
  }

  private String debugMegaPrint(String whatWasDoing, int problematicNode) {
    System.err.println(whatWasDoing);
    debugPrintTreeStructure(System.err);
    return dumpArrays(problematicNode).insert(0, whatWasDoing + "\n").toString();
  }

  final StringBuilder dumpArrays(int problematicNode) {
    StringBuilder sb = new StringBuilder();
    if (problematicNode > 0) sb = debugPrintNode(problematicNode, sb);
    int idWidth = max(log10(mySize), 4);
    #e# longestKey = max(abs(getMax()), abs(getMin()));
    int keyWidth = max(log10(longestKey), 3);
    sb.append(String.format("%" + idWidth + "s | %" + keyWidth + "s | %" + idWidth + "s | %" + idWidth + "s\n", "id", "key", "left", "right"));
    String idFormat = "%" + idWidth + "d";
    String keyFormat = "%" + keyWidth + "d";
    for (int i = 1; i < mySize; ++i) {
      sb.append(String.format(idFormat, i)).append(" | ")
        .append(String.format(keyFormat, myKeys[i])).append(" | ")
        .append(String.format(idFormat, myLeft[i])).append(" | ")
        .append(String.format(idFormat, myRight[i]))
        .append("\n");
    }
    return sb;
  }

  private StringBuilder debugPrintNode(int node, StringBuilder sb) {
    return sb
        .append("node  ").append(node)
      .append("\nkey   ").append(myKeys[node])
      .append("\nleft  ").append(myLeft[node])
      .append("\nright ").append(myRight[node])
      .append("\ncolor ").append(myBlack.get(node) ? "BLACK\n" : "RED\n");
  }

  final void debugPrintTreeStructure(final PrintStream out) {
    out.println("Legend: x - black node, o - red node, # - NIL");
    visitULR(0, new IntFunction2() {
      @Override
      public int invoke(int x, int level) {
        out.print(' ');
        for (int i = 0; i < level - 1; ++i) out.print("| ");
        if (level > 0) out.append("|-");
        if (x > 0) out.append(myBlack.get(x) ? 'x' : 'o').append(' ').println(myKeys[x]);
        else out.println("#");
        return level + 1;
      }
    });
  }

  private class LURIterator extends AbstractIntIterator {
    private int myCurrent;
    private int x = myRoot;
    private final int[] xs;
    private int xsi;

    public LURIterator() {
      int[] cache = myStackCache.get();
      if (cache == null) cache = new int[height(mySize)];
      xs = cache;
    }

    @Override
    public boolean hasNext() throws ConcurrentModificationException {
      return x != 0 || xsi > 0;
    }

    public IntIterator next() throws ConcurrentModificationException, NoSuchElementException {
      if (!hasNext()) throw new NoSuchElementException();
      if (x == 0) x = xs[--xsi];
      else {
        int l = myLeft[x];
        while (l != 0) {
          xs[xsi++] = x;
          x = l;
          l = myLeft[x];
        }
      }
      myCurrent = x;
      x = myRight[x];
      return this;
    }

    public int value() throws IllegalStateException {
      if (x == myRoot) throw new IllegalStateException();
      return myCurrent;
    }
  }
}
