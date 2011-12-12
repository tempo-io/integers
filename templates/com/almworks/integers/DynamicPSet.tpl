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
public class Dynamic#E#Set implements #E#Iterable {
  /** Dummy key for NIL. */
  private static final #e# NIL_DUMMY_KEY = #EW#.MIN_VALUE;
  private static final #e#[] EMPTY_KEYS = new #e#[] { #EW#.MIN_VALUE };
  private static final int[] EMPTY_INDEXES = new int[] { 0 };
  /** Index into the backing arrays of the last entry plus 1. It is the insertion point when {@code myRemoved == null}. */
  private int myFront;
  /** Key values. */
  private #e#[] myKeys;
  /** Tree structure: contains indexes into key, left, right, black. */
  private int[] myLeft;
  /** Tree structure: contains indexes into key, left, right, black. */
  private int[] myRight;
  /** Node color : false for red, true for black. */
  private final BitSet myBlack;
  /** List of removed nodes. Null if no internal nodes are removed */
  private BitSet myRemoved;
  /** Index into key, left, right, black that denotes the current root node. */
  private int myRoot;

  //see shrink() method
  private static final int SHRINK_FACTOR = 6; //testing. actual should be less, say, 3
  private static final int SHRINK_MIN_LENGTH = 4; //8
  // this one is used in fromSorted#E#Iterable(). A new myKeys array is created in this method, and its size is
  // the size of the given #E#Iterable multiplied by this constant (it's the space for new elements to be added later).
  private static final int EXPAND_FACTOR = 2;
  // these three costants are used in building a tree from a given list of values.
  // See fromSorted#E#Iterable paramcompactifyType.
  private static final int COMPACTIFY_TO_ADD = -1;
  private static final int COMPACTIFY_TO_REMOVE = 1;
  private static final int COMPACTIFY_BALANCED = 3;

  private SoftReference<int[]> myStackCache = new SoftReference<int[]>(IntegersUtils.EMPTY_INTS);

  public Dynamic#E#Set() {
    myBlack = new BitSet();
    init();
  }

  public Dynamic#E#Set(int initialCapacity) {
    initialCapacity += 1;
    myBlack = new BitSet(initialCapacity);
    init();
    myKeys = new #e#[initialCapacity];
    myLeft = new int[initialCapacity];
    myRight = new int[initialCapacity];
    myKeys[0] = NIL_DUMMY_KEY;
    myBlack.set(0);
  }

  private void init() {
    myKeys = EMPTY_KEYS;
    myLeft = EMPTY_INDEXES;
    myRight = EMPTY_INDEXES;
    myBlack.set(0);
    myRoot = 0;
    myFront = 1;
    myRemoved = null;
    myStackCache = new SoftReference<int[]>(IntegersUtils.EMPTY_INTS);
  }

  private void initNode(int x, #e# key) {
    myKeys[x] = key;
    myLeft[x] = 0;
    myRight[x] = 0;
    myBlack.clear(x);
    if (myRemoved != null)
      if (myRemoved.cardinality() == 1)
        myRemoved = null;
      else
        myRemoved.clear(x);
  }

  public void clear() {
    myBlack.clear();
    init();
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
    return myRemoved == null ? myFront - 1 : myFront - 1 - myRemoved.cardinality();
  }

  public boolean isEmpty() {
    boolean ret = myRoot == 0;
    assert (size() == 0) == ret : size() + " " + myRoot;
    return ret;
  }

  public void addAll(#E#List keys) {
    int[] ps = prepareAdd(keys.size());
    for (#E#Iterator i : keys) {
      add0(i.value(), ps);
    }
  }

  public void addAll(Dynamic#E#Set keys) {
    int[] ps = prepareAdd(keys.size());
    for (#E#Iterator ii : keys) {
      add0(ii.value(), ps);
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
    x = myRemoved == null ? myFront++ : myRemoved.nextSetBit(0);
    initNode(x, key);
    // x is RED already (myBlack.get(x) == false), so no modifications to myBlack
    // Insert into the tree
    if (psi == 0) myRoot = x;
    else (key < k ? myLeft : myRight)[ps[psi - 1]] = x;
    balanceAfterAdd(x, ps, psi, key);
    assert checkRedBlackTreeInvariants("add key:" + key);
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
    return fetchStackCache(n);
  }

  private void grow(int n) {
    int futureSize = size() + n + 1;
    myKeys = #E#Collections.ensureCapacity(myKeys, futureSize);
    myLeft = IntCollections.ensureCapacity(myLeft, futureSize);
    myRight = IntCollections.ensureCapacity(myRight, futureSize);
  }

  /**
   *
   * @param n difference between future size and actual size
   * @return
   */
  private int[] fetchStackCache(int n) {
    int fh = estimateFutureHeight(n);
    int[] cache = myStackCache.get();
    if (cache != null && cache.length >= fh) return cache;
    cache = new int[fh];
    myStackCache = new SoftReference<int[]>(cache);
    return cache;
  }

  /** Returns a number higher than the height after adding n elements.*/
  private int estimateFutureHeight(int n) {
    return height(size() + n) + 1;
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
    int height = height(size());
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
    assert myFront >= 1 : whatWasDoing + " " + myFront;
    assert (size() == 0) == isEmpty() : whatWasDoing + " " + myFront + ' ' + myRoot;

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
    final int hEst = height(size());
    visitULR(0, new IntFunction2() {
      @Override
      public int invoke(int x, int h) {
        if (myLeft[x] == 0 && myRight[x] == 0) {
          // we're at the bottom
          assert hEst >= h : whatWasDoing + "\n" + h + ' ' + hEst + ' ' + size() + ' ' + myFront;
        }
        return h + 1;
      }
    });

    // 6. any unreachable node should be contained in myRemoved
    BitSet unremoved = new BitSet(myFront);
    if (myRoot != 0) unremoved.set(myRoot);
    for (int i = 1; i < myFront; i++) {
      if (myLeft[i] > 0) unremoved.set(myLeft[i]);
      if (myRight[i] > 0) unremoved.set(myRight[i]);
    }
    if (myRemoved == null)
      assert unremoved.cardinality() == size();
    else {
      assert myRemoved.length() <= myFront;
      BitSet xor = new BitSet(myFront);
      xor.or(myRemoved);
      xor.xor(unremoved);
      assert (xor.nextClearBit(1) == myFront);
    }

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
    int idWidth = max(log10(myFront), 4);
    #e# longestKey = max(abs(getMax()), abs(getMin()));
    int keyWidth = max(log10(longestKey), 3);
    sb.append(String.format("%" + idWidth + "s | %" + keyWidth + "s | %" + idWidth + "s | %" + idWidth + "s\n", "id", "key", "left", "right"));
    String idFormat = "%" + idWidth + "d";
    String keyFormat = "%" + keyWidth + "d";
    for (int i = 1; i < size(); ++i) {
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

  private void shrink() {
    int s = size();
    if (s > SHRINK_MIN_LENGTH && s*SHRINK_FACTOR < myKeys.length)
      compactify();
  }

  public void removeAll(#E#Iterable keys) {
    for (#E#Iterator i : keys) {
      remove0(i.value());
    }
    shrink();
  }

  public void removeAll(#e#... keys) {
    for (#e# key : keys) {
      remove0(key);
    }
    shrink();
  }

  public boolean remove(#e# key) {
    boolean ret = remove0(key);
    shrink();
    return ret;
  }

  private boolean remove0(#e# key) {
    if (isEmpty()) return false;

    int[] parentsStack = fetchStackCache(0);
    int xsi = -1;

    //searching for an index Z which contains the key.
    int z = myRoot;
    while (myKeys[z] != key) {
      if (z == 0)
        return false;
      parentsStack[++xsi] = z;
      z = key < myKeys[z] ? myLeft[z] : myRight[z];
    }

    // searching for an index Y which will be actually cleared.
    int y = z;
    if (myLeft[z] != 0 && myRight[z] != 0) {
      parentsStack[++xsi] = y;
      y = myRight[y];
      while (myLeft[y] != 0) {
        parentsStack[++xsi] = y;
        y = myLeft[y];
      }
    }
    // if actually other node was removed, then we should copy its content.
    if (z != y) myKeys[z] = myKeys[y];

    // searching for Y's child X. Y can't have 2 children.
    int x = (myLeft[y] != 0) ? myLeft[y] : myRight[y];

    // Removing a node.
    // Linking parent of Y to X (or myRoot if there's no parent). This way Y becomes "removed" (unreachable)
    if (y == myRoot) myRoot = x;
    else {
      int parentOfY = parentsStack[xsi];
      if (myLeft[parentOfY] == y)
        myLeft[parentOfY] = x;
      else myRight[parentOfY] = x;
    }
    myKeys[y] = 0;
    myLeft[y] = 0;
    myRight[y] = 0;
    if (y == myFront-1)
      myFront--;
    else {
      if (myRemoved == null) myRemoved = new BitSet(y+1);
      myRemoved.set(y);
    }

    if (myBlack.get(y)) balanceAfterRemove(x, parentsStack, xsi);

    assert checkRedBlackTreeInvariants("remove key:" + key);
    return true;
  }

  private void balanceAfterRemove(int x, int[] parentsStack, int xsi) {
    int[] mainBranch, otherBranch;
    int parentOfX, grandParentOfX, w;
    while (x != myRoot && myBlack.get(x)) {
      parentOfX = parentsStack[xsi];
      if (myLeft[parentOfX] == x) {
        mainBranch = myLeft;
        otherBranch = myRight;
      } else {
        mainBranch = myRight;
        otherBranch = myLeft;
      }
      // W is X's uncle
      w = otherBranch[parentOfX];
      if (!myBlack.get(w)) {
        myBlack.set(w);
        myBlack.clear(parentOfX);
        grandParentOfX = (xsi == 0) ? 0 : parentsStack[xsi-1];
        rotate(parentOfX, grandParentOfX, mainBranch, otherBranch);
        parentsStack[xsi] = w;
        parentsStack[++xsi] = parentOfX;
        w = otherBranch[parentOfX];
      }
      if (myBlack.get(mainBranch[w]) && myBlack.get(otherBranch[w])) {
        myBlack.clear(w);
        x = parentOfX;
        xsi--;
      } else {
        if (myBlack.get(otherBranch[w])) {
          myBlack.set(mainBranch[w]);
          myBlack.clear(w);
          rotate(w, parentOfX, otherBranch, mainBranch);
          w = otherBranch[parentOfX];
        }
        myBlack.set(w, myBlack.get(parentOfX));
        myBlack.set(parentOfX);
        myBlack.set(otherBranch[w]);
        grandParentOfX = (xsi == 0) ? 0 : parentsStack[xsi-1];
        rotate(parentOfX, grandParentOfX, mainBranch, otherBranch);
        x = myRoot;
      }
    }
    myBlack.set(x);
  }

  /**
   * This method rebuilds this Dynamic#E#Set.
   * After running this method, this object might use a different amount of memory than it used before.
   * Actually, it will use memory which is needed to hold EXPAND_FACTOR*size() elements.
   */
  public void compactify() {
    fromSorted#E#Iterable(this, size(), COMPACTIFY_BALANCED);
  }

  /**
   * This method replaces this set's keys with values given in src and rebuilds its tree.
   * The tree is built in such a way that the difference between heights of any two leafs is 0 or 1.
   * In other words, graphical representation of a tree would look as a triangle.
   * @param compactifyType Type of tree coloring.
   *    Tree colors can be set in different ways, 3 modes are provided (see constants):
   *    1. If there's a minimum of red nodes, adding would be fast and removing would be slow.
   *       In this mode only last-level nodes would be red (if the last level is unfilled)
   *    2. If there's a maximum of red nodes, adding would be slow and removing would be fast.
   *       In this mode even levels will be entirely red (except last 2 levels, whose colors are set according to rb-restrictions)
   *    3. If an amount of red nodes is between min and max, there'll be a balance between remove and add average timing.
   *       In this mode every 4-th level will be entirely red (except last 2 levels)
   *    The constants values are not conventional, they're actually used in the logic.
   */
  private void fromSorted#E#Iterable(#E#Iterable src, int srcSize, int compactifyType) {
    #e#[] newKeys;
    if (srcSize == 0)
      newKeys = EMPTY_KEYS;
    else {
      newKeys = new #e#[Math.max(SHRINK_MIN_LENGTH, srcSize*EXPAND_FACTOR)];
      int i = 0;
      for (#E#Iterator ii : src) {
        newKeys[++i] = ii.value();
        assert (i==1 || newKeys[i] >= newKeys[i-1]);
      }
    }
    fromPreparedArray(newKeys, srcSize, compactifyType);
    assert checkRedBlackTreeInvariants("fromSorted#E#Iterable");
  }

  /**
   * @param newKeys array which will become the new myKeys
   * @param usedSize a number of actual elements in newKeys
   */
  private void fromPreparedArray(#e#[] newKeys, int usedSize, int compactifyType) {
    myBlack.clear();
    init();
    myKeys = newKeys;
    myFront = usedSize+1;
    if (usedSize == 0)
      return;
    myLeft = new int[myKeys.length];
    myRight = new int[myKeys.length];

    int top = 2;
    while (top <= usedSize) top *= 2;
    // todo explain
    boolean lastPairsAreBlack = (usedSize < 3*top/4) || usedSize == 1;
    myRoot = rearrangeStep(1, usedSize, compactifyType, compactifyType, lastPairsAreBlack);
  }

  private int rearrangeStep(int offset, int length, int colorCounter, int maxCounter, boolean lpab) {
    int halfLength = length/2;
    int index = offset + halfLength;
    if (length == 1)
      myBlack.set(index, lpab);
    else if (length == 2) {
      myBlack.set(index);
      myLeft[index] = offset;
    } else {
      // calculating the node color
      boolean isBlack = true;
      if (colorCounter == 0) {
        isBlack = false;
        colorCounter = maxCounter;
      } else if (colorCounter > 0)
        colorCounter--;
      
      if (length == 3 && !lpab)
        myBlack.set(index);
      else
        myBlack.set(index, isBlack);
      myLeft[index] = rearrangeStep(offset, halfLength, colorCounter, maxCounter, lpab);
      myRight[index] = rearrangeStep(index + 1, length - halfLength - 1, colorCounter, maxCounter, lpab);
    }
    return index;
  }

  public #E#Array toSorted#E#Array() {
    #e#[] arr = new #e#[size()];
    int i = 0;
    for (#E#Iterator it : this) {
      arr[i++] = it.value();
    }
    return new #E#Array(arr);
  }

  public static Dynamic#E#Set fromSortedList(#E#List src) {
    return fromSortedList0(src, COMPACTIFY_BALANCED);
  }

  public static Dynamic#E#Set fromSortedListToAdd(#E#List src) {
    return fromSortedList0(src, COMPACTIFY_TO_ADD);
  }

  public static Dynamic#E#Set fromSortedListToRemove(#E#List src) {
    return fromSortedList0(src, COMPACTIFY_TO_REMOVE);
  }
  
  private static Dynamic#E#Set fromSortedList0(#E#List src, int compactifyType) {
    assert #E#Collections.isSorted(src.toNativeArray());
    Dynamic#E#Set res = new Dynamic#E#Set();
    res.fromSorted#E#Iterable(src, src.size(), compactifyType);
    return res;
  }

  public #E#Iterator iterator() {
    return new LURIterator();
  }

  private class LURIterator extends Abstract#E#Iterator {
    private #e# myValue;
    private int x = myRoot;
    private final int[] xs;
    private int xsi;

    public LURIterator() {
      xs = new int[height(size())];
    }

    @Override
    public boolean hasNext() throws ConcurrentModificationException {
      return x != 0 || xsi > 0;
    }

    public #E#Iterator next() throws ConcurrentModificationException, NoSuchElementException {
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
      myValue = myKeys[x];
      x = myRight[x];
      return this;
    }

    public #e# value() throws IllegalStateException {
      if (x == myRoot) throw new IllegalStateException();
      return myValue;
    }
  }
}
