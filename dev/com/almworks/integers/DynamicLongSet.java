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

// CODE GENERATED FROM com/almworks/integers/DynamicPSet.tpl


package com.almworks.integers;

import com.almworks.integers.func.IntFunction2;
import com.almworks.integers.util.IntegersDebug;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/** A red-black tree implementation of a set. Single-thread access only. <br/>
 * Use if you are frequently adding and querying. */
public class DynamicLongSet implements LongIterable {
  /** Dummy key for NIL. */
  private static final long NIL_DUMMY_KEY = Long.MIN_VALUE;
  private static final long[] EMPTY_KEYS = new long[] { Long.MIN_VALUE };
  private static final int[] EMPTY_INDEXES = new int[] { 0 };
  /** Index into the backing arrays of the last entry plus 1. It is the insertion point when {@code myRemoved == null}. */
  private int myFront;
  /** Key values. */
  private long[] myKeys;
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

  private int myModCount = 0;

  private static final int SHRINK_FACTOR = 6; //testing. actual should be less, say, 3
  private static final int SHRINK_MIN_LENGTH = 4; //8
  // used in fromSortedLongIterable(). A new myKeys array is created in this method, and its size is
  // the size of the given LongIterable multiplied by this constant (additional space for new elements to be added later).
  private static final int EXPAND_FACTOR = 2;

  /**
   * This enum is used in {@link DynamicLongSet#compactify(com.almworks.integers.DynamicLongSet.ColoringType)} and
   * {@link DynamicLongSet#fromSortedList(LongList, com.almworks.integers.DynamicLongSet.ColoringType)}
   * methods to determine the way the new tree will be colored.
   */
  public enum ColoringType {
    TO_ADD {
      public int redLevelsDensity() {return 0;}
    },
    TO_REMOVE {
      public int redLevelsDensity() {return 2;}
    },
    BALANCED {
      public int redLevelsDensity() {return 4;}
    };
    public abstract int redLevelsDensity();
  }

  private SoftReference<int[]> myStackCache = new SoftReference<int[]>(IntegersUtils.EMPTY_INTS);

  public DynamicLongSet() {
    myBlack = new BitSet();
    init();
  }

  public DynamicLongSet(int initialCapacity) {
    initialCapacity += 1;
    myBlack = new BitSet(initialCapacity);
    init();
    myKeys = new long[initialCapacity];
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

  private void initNode(int x, long key) {
    myKeys[x] = key;
    myLeft[x] = 0;
    myRight[x] = 0;
    myBlack.clear(x);
    if (myRemoved != null) {
      if (myRemoved.cardinality() == 1) {
        myRemoved = null;
      } else {
        myRemoved.clear(x);
      }
    }
  }

  public void clear() {
    myBlack.clear();
    init();
    modified();
    assert !IntegersDebug.DEBUG || checkRedBlackTreeInvariants("clear");
  }

  private void modified() {
    myModCount++;
  }

  /** @return {@link Long#MIN_VALUE} in case the set is empty */
  public long getMax() {
    return myKeys[traverseToEnd(myRight)];
  }

  /** @return {@link Long#MIN_VALUE} in case the set is empty */
  public long getMin() {
    return myKeys[traverseToEnd(myLeft)];
  }

  private int traverseToEnd(int[] branch) {
    int x = myRoot;
    for (int nextX = branch[x]; nextX != 0; nextX = branch[x]) {
      x = nextX;
    }
    return x;
  }

  public boolean contains(long key) {
    int x = myRoot;
    long k = myKeys[x];
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

  public void addAll(LongList keys) {
    modified();
    int[] ps = prepareAdd(keys.size());
    for (LongIterator i : keys) {
      add0(i.value(), ps);
    }
  }

  public void addAll(DynamicLongSet keys) {
    modified();
    int[] ps = prepareAdd(keys.size());
    for (LongIterator ii : keys) {
      add0(ii.value(), ps);
    }
  }

  public void addAll(long... keys) {
    modified();
    int[] ps = prepareAdd(keys.length);
    for (long key : keys) {
      add0(key, ps);
    }
  }

  public boolean add(long key) {
    modified();
    return add0(key, prepareAdd(1));
  }

  private boolean add0(long key, int[] ps) {
    int x = myRoot;
    // Parents stack top + 1
    int psi = 0;
    // actually, k is always overwritten if it is used (psi > 0), but compiler does not know that
    long k = 0;
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
    assert !IntegersDebug.DEBUG || checkRedBlackTreeInvariants("add key:" + key);
    return true;
  }

  /**
   * @param x the node being inserted, RED
   * @param ps ancestors of the node x; ps[0] is the root, ps[psi] is the parent of x
   * @param psi currently used ancestor of x; if psi is < 0, we continue to think that the corresponding ancestor is NIL.
   * @param debugKey a string to assist understanding what were we adding if assertion fails
   * */
  private void balanceAfterAdd(int x, int[] ps, int psi, long debugKey) {
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

  private boolean checkChildParent(int child, int parent, long debugKey) {
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
    maybeGrow(n);
    return fetchStackCache(n);
  }

  private void maybeGrow(int n) {
    int futureSize = size() + n + 1;
    myKeys = LongCollections.ensureCapacity(myKeys, futureSize);
    myLeft = IntCollections.ensureCapacity(myLeft, futureSize);
    myRight = IntCollections.ensureCapacity(myRight, futureSize);
  }

  /**
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

  /**
   * This method rebuilds this DynamicLongSet.
   * After running this method, this object will use memory which is needed to hold size() elements.
   * (Usually it uses more memory before this method is ran)
   * This method builds a new tree based on the same keyset.
   * All levels of the new tree are filled, except, probably, the last one.
   * Levels are filled firstly with all possible left children, and only then
   * with right children, all starting from the left side.
   * To follow rb-restrictions, if there's an unfilled level,
   * it's made completely red and pre-last is made completely black.
   * All the other levels are made black, except every 4-th level, starting from level 2,
   * which are made red. This type of coloring guarantees a balance between average times taken
   * by subsequent add and remove operations.
   */
  public void compactify() {
    compactify(ColoringType.BALANCED);
  }

  /**
   * This method is similar to {@link com.almworks.integers.DynamicLongSet#compactify()},
   * except the way the internal levels are colored.
   * @param coloringType the way the internal levels are colored. Internal levels are all levels except the last two
   *                     if the last one is unfilled.
   *   <br>TO_REMOVE colors every 2th non-last level red, theoretically making subsequent removals faster;
   *   <br>BALANCED colors every 4th non-last level red, similar to {@link com.almworks.integers.DynamicLongSet#compactify()};
   *   <br>TO_ADD colors all non-last level black, theoretically making subsequent additions faster;
   */
  public void compactify(ColoringType coloringType) {
    modified();
    fromSortedLongIterable(this, size(), coloringType);
    assert checkRedsAmount(coloringType);
  }

  private void fromSortedLongIterable(LongIterable src, int srcSize, ColoringType coloringType) {
    long[] newKeys;
    if (srcSize == 0)
      newKeys = EMPTY_KEYS;
    else {
      int arraySize = (coloringType == ColoringType.TO_ADD) ? srcSize * EXPAND_FACTOR : srcSize+1;
      newKeys = new long[Math.max(SHRINK_MIN_LENGTH, arraySize)];
      int i = 0;
      newKeys[0] = Long.MIN_VALUE;
      for (LongIterator ii : src) {
        newKeys[++i] = ii.value();
        assert (i==1 || newKeys[i] >= newKeys[i-1]) : newKeys;
      }
    }
    fromPreparedArray(newKeys, srcSize, coloringType);
    assert !IntegersDebug.DEBUG || checkRedBlackTreeInvariants("fromSortedLongIterable");
  }

  /**
   * @param newKeys array which will become the new myKeys
   * @param usedSize a number of actual elements in newKeys
   */
  private void fromPreparedArray(long[] newKeys, int usedSize, ColoringType coloringType) {
    myBlack.clear();
    init();
    myKeys = newKeys;
    myFront = usedSize+1;
    if (usedSize == 0)
      return;
    myLeft = new int[myKeys.length];
    myRight = new int[myKeys.length];

    int levels = log(2, usedSize);
    int top = (int)Math.pow(2, levels);
    int redLevelsDensity = coloringType.redLevelsDensity();
    // Definitoin: last pair is any pair of nodes which belong to one parent and don't have children.
    // If the last level contains only left children, then, due to the way the last level is filled,
    // last pairs (if there are any) belong entirely to pre-last level, and therefore are black.
    // Otherwise they belong entirely to the last level and are red.
    boolean lastPairsAreBlack;
    if (top != usedSize + 1)
      lastPairsAreBlack = (usedSize < 3*top/4);
    else
      lastPairsAreBlack = (coloringType == ColoringType.TO_ADD) || (levels + redLevelsDensity - 2) % redLevelsDensity != 0;
    // an index of the first internal level which will be colored red. (zero means no internal levels will be red)
    int startingCounter = (coloringType == ColoringType.TO_ADD) ? 0 : 2;
    myRoot = rearrangeStep(1, usedSize, startingCounter, redLevelsDensity, lastPairsAreBlack);
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
      if (colorCounter == 1) {
        isBlack = false;
        colorCounter = maxCounter;
      } else if (colorCounter > 1)
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

  /**
   * Builds a new DynamicLongSet based on values of src. src isn't used internally, its contents are copied.
   */
  public static DynamicLongSet fromSortedList(LongList src) {
    return fromSortedList0(src, ColoringType.BALANCED);
  }

  /**
   * This method is similar to {@link com.almworks.integers.DynamicLongSet#fromSortedList(LongList)},
   * except the way the internal levels are colored.
   * @param coloringType the way the internal levels are colored. Internal levels are all levels except the last two
   *                     if the last one is unfilled.
   *   <br>TO_REMOVE colors every 2th non-last level red, theoretically making subsequent removals faster;
   *   <br>BALANCED colors every 4th non-last level red, similar to {@link com.almworks.integers.DynamicLongSet#fromSortedList(LongList)};
   *   <br>TO_ADD colors all non-last level black, theoretically making subsequent additions faster;
   */
  public static DynamicLongSet fromSortedList(LongList src, ColoringType coloringType) {
    return fromSortedList0(src, coloringType);
  }

  private static DynamicLongSet fromSortedList0(LongList src, ColoringType coloringType) {
    DynamicLongSet res = new DynamicLongSet();
    res.fromSortedLongIterable(src, src.size(), coloringType);
    assert res.checkRedsAmount(coloringType);
    return res;
  }

  public LongIterator iterator() {
    return new KeysIterator();
  }

  public void removeAll(LongIterable keys) {
    modified();
    int[] parentsStack = fetchStackCache(0);
    for (LongIterator i : keys) {
      remove0(i.value(), parentsStack);
    }
    maybeShrink();
  }

  public void removeAll(long... keys) {
    modified();
    int[] parentsStack = fetchStackCache(0);
    for (long key : keys) {
      remove0(key, parentsStack);
    }
    maybeShrink();
  }

  public boolean remove(long key) {
    modified();
    boolean ret = remove0(key, fetchStackCache(0));
    maybeShrink();
    return ret;
  }

  public void retain(DynamicLongSet set) {
    LongArray array = toSortedLongArray();
    array.retainSorted(set.toSortedLongArray());
    clear();
    addAll(array);
  }

  private void maybeShrink() {
    int s = size();
    if (s > SHRINK_MIN_LENGTH && s*SHRINK_FACTOR < myKeys.length)
      compactify();
  }

  private boolean remove0(long key, int[] parentsStack) {
    if (isEmpty()) return false;

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
    if (z != y) myKeys[z] = myKeys[y];

    // Child of Y. Y can't have 2 children.
    int x = (myLeft[y] != 0) ? myLeft[y] : myRight[y];

    if (y == myRoot) myRoot = x;
    else {
      int parentOfY = parentsStack[xsi];
      if (myLeft[parentOfY] == y)
        myLeft[parentOfY] = x;
      else myRight[parentOfY] = x;
    }
    free(y);

    if (myBlack.get(y)) {
      balanceAfterRemove(x, parentsStack, xsi);
      myBlack.clear(y);
    }

    assert !IntegersDebug.DEBUG || checkRedBlackTreeInvariants("remove key:" + key);
    return true;
  }

  private void free(int y) {
    myKeys[y] = 0;
    myLeft[y] = 0;
    myRight[y] = 0;
    if (y == myFront-1)
      myFront--;
    else {
      if (myRemoved == null) myRemoved = new BitSet(y+1);
      myRemoved.set(y);
    }
  }

  private void balanceAfterRemove(int x, int[] parentsStack, int xsi) {
    int[] mainBranch, otherBranch;
    int parentOfX, w;
    while (x != myRoot && myBlack.get(x)) {
      parentOfX = parentsStack[xsi];
      if (myLeft[parentOfX] == x) {
        mainBranch = myLeft;
        otherBranch = myRight;
      } else {
        mainBranch = myRight;
        otherBranch = myLeft;
      }
      w = otherBranch[parentOfX];
      if (!myBlack.get(w)) {
        // then loop is also finished
        myBlack.set(w);
        myBlack.clear(parentOfX);
        rotate(parentOfX, getLastOrNil(parentsStack, xsi-1), mainBranch, otherBranch);
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
        rotate(parentOfX, getLastOrNil(parentsStack, xsi-1), mainBranch, otherBranch);
        x = myRoot;
      }
    }
    myBlack.set(x);
  }

  public LongArray toSortedLongArray() {
    long[] arr = new long[size()];
    int i = 0;
    for (IntIterator it : new LURIterator()) {
      arr[i++] = myKeys[it.value()];
    }
    return new LongArray(arr);
  }

  @Override
  public String toString() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    debugPrintTreeStructure(new PrintStream(baos));
    try {
      return baos.toString("US-ASCII");
    } catch (UnsupportedEncodingException e) {
      assert false: e;
      return "DynamicLongSet";
    }
  }

  /**
   * Visits the tree in the ULR order (up-left-right.)<br/>
   * <strong>Warning:</strong> its initial purpose was to create a visual representation of the tree, so the visitor can be invoked on NIL elements (in case when an element has only one child, for completeness of the visual representation.)
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
    if (x == 0) return;
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

  // returns the logarithm of n+1, rounded up.
  private static int log(int base, long n) {
    int ret = 0;
    do { ret += 1; n /= base; } while (n > 0L);
    return ret;
  }

  private boolean checkRedBlackTreeInvariants(final String whatWasDoing) {
    int sz = myKeys.length;
    assert sz == myLeft.length && sz == myRight.length: whatWasDoing + " | " +  sz + ' ' + myLeft.length  + ' ' + myRight.length;
    assert myFront >= 1 : whatWasDoing + " " + myFront;
    assert (size() == 0) == isEmpty() : whatWasDoing + " " + myFront + ' ' + myRoot;

    // unnecessary requirements, though following them makes the structure more clear.
    assert myKeys[0] == Long.MIN_VALUE : whatWasDoing + "\n" + myKeys[0];
    assert myLeft[0] == 0  : whatWasDoing + "\n" + myLeft[0];
    assert myRight[0] == 0 : whatWasDoing + "\n" + myRight[0];
    assert myBlack.get(0) : whatWasDoing;


    final int[] lastBlackHeight = new int[] {-1};
    visitULR(0, new IntFunction2() {
      @Override
      public int invoke(int x, int bh) {
        if (x == 0)
          return bh;

        // 1. Binary search tree property
        long k = myKeys[x];
        int l = myLeft[x];
        long lk = myKeys[l];
        if (l != 0)
          assert lk < k : debugMegaPrint(whatWasDoing, x);
        int r = myRight[x];
        long rk = myKeys[r];
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

    // any unreachable node should be contained in myRemoved
    final BitSet unremoved = new BitSet(myFront);
    visitULR(0, new IntFunction2() {
      @Override
      public int invoke(int x, int _) {
        if (x != 0) unremoved.set(x);
        return _;
      }
    });
    if (myRemoved == null)
      assert unremoved.cardinality() == size() : whatWasDoing + "\n" + size() + ' ' + unremoved.cardinality() + '\n' + unremoved + '\n' + dumpArrays(0);
    else {
      assert myRemoved.length() <= myFront : myFront + "\n" + myRemoved + "\n" + debugMegaPrint(whatWasDoing, 0);
      BitSet xor = new BitSet(myFront);
      xor.or(myRemoved);
      xor.xor(unremoved);
      assert xor.nextClearBit(1) == myFront : myFront + " " + xor.nextClearBit(1) + '\n' + xor + '\n' + myRemoved + '\n' + unremoved + '\n' + debugMegaPrint(whatWasDoing, 0);
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
    int idWidth = max(log(10, myFront), 4);
    long longestKey = max(abs(getMax()), abs(getMin()));
    int keyWidth = max(log(10, longestKey), 3);
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

  /**
   Calculates the expected amount of red nodes in a tree of a size sz after it is compactified with coloringType.
   */
  static int redsExpected(int sz, ColoringType coloringType) {
    int result = 0;
    if (coloringType == ColoringType.BALANCED || coloringType == ColoringType.TO_REMOVE) {
      int redLevelsDensity = coloringType.redLevelsDensity();
      int internalLevels = log(2, sz);
      if (Math.pow(2,internalLevels) != sz + 1)
        internalLevels = internalLevels - 2;
      int internalRedLevels = (internalLevels+redLevelsDensity-2)/redLevelsDensity;
      while (internalRedLevels > 0) {
        int levelHeight = (internalRedLevels-1)*redLevelsDensity + 2;
        result += Math.pow(2, levelHeight-1);
        internalRedLevels--;
      }
    }
    int top = (int)Math.pow(2, log(2, sz));
    if (top != sz+1)
      result += sz - top/2 + 1;
    return result;
  }

  private boolean checkRedsAmount(ColoringType coloringType) {
    int sz = size();
    int expected = redsExpected(sz, coloringType);
    int actual = sz - myBlack.cardinality() + 1;
    assert expected == actual : sz + " " + actual + " " + expected;
    System.out.println("size: " + sz + ", reds: " + actual);
    return true;
  }

  private class KeysIterator extends AbstractLongIterator {
    private LURIterator myIterator = new LURIterator();
    private final int myModCountAtCreation = myModCount;

    @Override
    public boolean hasNext() throws ConcurrentModificationException {
      checkMod();
      return myIterator.hasNext();
    }

    public LongIterator next() throws ConcurrentModificationException, NoSuchElementException {
      checkMod();
      myIterator.next();
      return this;
    }

    public boolean hasValue() {
      return myIterator.hasValue();
    }

    public long value() throws IllegalStateException {
      checkMod();
      return myKeys[myIterator.value()];
    }

    private void checkMod() {
      if (myModCountAtCreation != myModCount)
        throw new ConcurrentModificationException(myModCountAtCreation + " " + myModCount);
    }
  }

  private class LURIterator extends AbstractIntIterator {
    private int myValue;
    private int x = myRoot;
    private final int[] xs;
    private int xsi;

    public LURIterator() {
      xs = new int[height(size())];
    }

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
      myValue = x;
      x = myRight[x];
      return this;
    }

    public boolean hasValue() {
      return x != myRoot;
    }

    public int value() throws IllegalStateException {
      if (x == myRoot) throw new IllegalStateException();
      return myValue;
    }
  }
}
