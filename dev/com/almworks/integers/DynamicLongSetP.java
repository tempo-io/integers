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
import com.almworks.integers.util.FailFastLongIterator;
import com.almworks.integers.util.IntegersDebug;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/** A red-black tree implementation of a set. Single-thread access only. <br/>
 * Use if you are frequently adding and querying. */
public class DynamicLongSetP implements LongIterable, WritableSortedLongSet, Cloneable {
  /** Dummy key for NIL. */
  private static final long NIL_DUMMY_KEY = Long.MIN_VALUE;
  private static final long[] EMPTY_KEYS = new long[] { NIL_DUMMY_KEY };
  private static final int[] EMPTY_INDEXES = new int[] { 0 };
  /** Index into the backing arrays of the last entry + 1 (for the NIL). */
  private int myFront;
  /** Key values. */
  private long[] myKeys;
  /** Tree structure: contains indexes into key, left, right, black. */
  private int[] myLeft;
  /** Tree structure: contains indexes into key, left, right, black. */
  private int[] myRight;
  private int[] myParent;
  /** Node color : false for red, true for black. */
  private final BitSet myBlack;
  /** List of removed nodes. Null if no internal nodes are removed */
  private BitSet myRemoved;
  /** Index into key, left, right, black that denotes the current root node. */
  private int myRoot;

  private int myModCount = 0;

  private static final int SHRINK_FACTOR = 3; // for testing try 6
  private static final int SHRINK_MIN_LENGTH = 8; // for testing try 4
  // used in fromSortedLongIterable(). A new myKeys array is created in this method, and its size is
  // the size of the given LongIterable multiplied by this constant (additional space for new elements to be added later).
  private static final int EXPAND_FACTOR = 2;

  /**
   * This enum is used in {@link com.almworks.integers.DynamicLongSetP#compactify(com.almworks.integers.DynamicLongSetP.ColoringType)} and
   * {@link com.almworks.integers.DynamicLongSetP#fromSortedList(com.almworks.integers.LongList, com.almworks.integers.DynamicLongSetP.ColoringType)}
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

  public DynamicLongSetP() {
    myBlack = new BitSet();
    myRemoved = new BitSet();
    init();
  }

  public DynamicLongSetP(int initialCapacity) {
    initialCapacity += 1;
    myBlack = new BitSet(initialCapacity);
    myRemoved = new BitSet(initialCapacity);
    init();
    myKeys = new long[initialCapacity];
    myLeft = new int[initialCapacity];
    myRight = new int[initialCapacity];
    myParent = new int[initialCapacity];
    myKeys[0] = NIL_DUMMY_KEY;
  }

  private void init() {
    myKeys = EMPTY_KEYS;
    myLeft = EMPTY_INDEXES;
    myRight = EMPTY_INDEXES;
    myParent = EMPTY_INDEXES;
    myBlack.set(0);
    myRoot = 0;
    myFront = 1;
    myRemoved.clear();
  }

  public void clear() {
    modified();
    myBlack.clear();
    init();
    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("clear");
  }

  private void modified() {
    myModCount++;
  }

  /** @return {@link Long#MIN_VALUE} in case the set is empty */
  public long getUpperBound() {
    return myKeys[traverseToEnd(myRight)];
  }

  /** @return {@link Long#MAX_VALUE} in case the set is empty */
  public long getLowerBound() {
    if (size() == 0) return Long.MAX_VALUE;
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

  public boolean containsAll(LongIterable keys) {
    for (LongIterator it : keys.iterator()) {
      if (!contains(it.value())) return false;
    }
    return true;
  }

  public int size() {
    return myRemoved.isEmpty() ? myFront - 1 : myFront - 1 - myRemoved.cardinality();
  }

  public boolean isEmpty() {
    boolean ret = myRoot == 0;
    assert (size() == 0) == ret : size() + " " + myRoot;
    return ret;
  }

  @Override
  public LongArray toArray() {
    return toLongArray();
  }

  public void addAll(LongList keys) {
    modified();
    maybeGrow(keys.size());
    for (LongIterator i : keys) {
      push0(i.value());
    }
  }

  public void addAll(DynamicLongSetP keys) {
    modified();
    maybeGrow(keys.size());
    for (LongIterator ii : keys) {
      push0(ii.value());
    }
  }

  public void addAll(long... keys) {
    modified();
    if (keys == null) return;
    maybeGrow(keys.length);
    for (long key : keys) {
      push0(key);
    }
  }

  public void addAll(LongIterator iterator) {
    if (!iterator.hasNext()) return;
    modified();
    while (iterator.hasNext()) {
      add(iterator.nextValue());
    }
  }

  public void add(long key) {
    include(key);
  }

  /**
   * @return false if set already contains key
   * */
  public boolean include(long key) {
    modified();
    maybeGrow(1);
    return push0(key);
  }

  private boolean push0(long key) {
    int x = myRoot;
    int p = 0;
    // actually, k is always overwritten if it is used (psi > 0), but compiler does not know that
    long k = 0;
    while (x != 0) {
      k = myKeys[x];
      if (key == k) return false;
      p = x;
      x = key < k ? myLeft[x] : myRight[x];
    }
    x = createNode(key, p);

    // x is RED already (myBlack.get(x) == false), so no modifications to myBlack
    // Insert into the tree
    if (p == 0) myRoot = x;
    else (key < k ? myLeft : myRight)[p] = x;
    balanceAfterAdd(x, p, key);
    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("add key:" + key);
    return true;
  }

  private int createNode(long key, int p) {
    int x = myRemoved.isEmpty() ? myFront++ : myRemoved.nextSetBit(0);
    myKeys[x] = key;
    myLeft[x] = 0;
    myRight[x] = 0;
    myParent[x] = p;
    myBlack.clear(x);
    if (!myRemoved.isEmpty()) {
      myRemoved.clear(x);
    }
    return x;
  }

  /**
   * @param x the node being inserted, RED
   * @param debugKey a string to assist understanding what were we adding if assertion fails
   * */
  private void balanceAfterAdd(int x, int p, long debugKey) {
    // grandparent
    int pp = myParent[p];
    while (!myBlack.get(p)) {
      assert checkChildParent(p, pp, debugKey);
      assert checkChildParent(x, p, debugKey);
      int[] branch1, branch2;
      if (p == myLeft[pp]) {
        branch1 = myLeft;
        branch2 = myRight;
      } else {
        branch1 = myRight;
        branch2 = myLeft;
      }
      // Uncle (parent's sibling)
      int u = branch2[pp];
      if (!myBlack.get(u)) {
        myBlack.set(p);
        myBlack.set(u);
        myBlack.clear(pp);
        x = pp;
        p = myParent[x];
        pp = myParent[p];
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
        int ppp = myParent[pp];
        // Takes pp (which is now red) and makes it a branch-2 children of p (which is now black); note that branch-1 children of p is x, which is red and both children are black
        rotate(pp, ppp, branch2, branch1);
      }
    }
    myBlack.set(myRoot);
  }

  private boolean checkChildParent(int child, int parent, long debugKey) {
    assert (myLeft[parent] == child || myRight[parent] == child) && myParent[child] == parent : debugMegaPrint("add " + debugKey + "\nproblem with child " + child, parent);
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

    int ym = mainBranch[y];
    otherBranch[x] = ym;
    if (ym != 0) myParent[ym] = x;
    mainBranch[y] = x;
    myParent[x] = y;

    myParent[y] = p;
    if (p == 0) myRoot = y;
    else {
      if (x == myLeft[p]) myLeft[p] = y;
      else {
        assert x == myRight[p] : "tree structure broken " + x + '\n' + dumpArrays(p);
        myRight[p] = y;
      }
    }
  }

  private void maybeGrow(int n) {
    int oldSz = myKeys.length;
    int futureSize = size() + n + 1;
    myKeys = LongCollections.ensureCapacity(myKeys, futureSize);
    myLeft = IntCollections.ensureCapacity(myLeft, futureSize);
    myRight = IntCollections.ensureCapacity(myRight, futureSize);
    myParent = IntCollections.ensureCapacity(myParent, futureSize);
    if (IntegersDebug.PRINT) IntegersDebug.format("%20s %4d -> %4d  %H  %s", "grow", oldSz, myKeys.length, this, last4MethodNames());
  }

  private static String last4MethodNames() {
    if (!IntegersDebug.PRINT) return "";
    List<StackTraceElement> frame = Arrays.asList(new Exception().getStackTrace());
    StringBuilder sb = new StringBuilder();
    String sep = "";
    for (StackTraceElement e : frame.subList(2, Math.min(frame.size(), 6))) {
      sb.append(sep).append(e.getMethodName().replace(".*\\.(.*?)", "$1")).append("@").append(e.getLineNumber());
      sep = ", ";
    }
    return sb.toString();
  }

  /**
   * This method rebuilds this DynamicLongSetP, after that it will use just the amount of memory needed to hold size() elements.
   * (Usually it uses more memory before this method is run)
   * This method builds a new tree based on the same keyset.
   * All levels of the new tree are filled, except, probably, the last one.
   * Tree is built by levels top-down, within one level first all left children are added, then all right children.
   * To keep black-height on all paths the same, if there's an unfilled level,
   * it's made completely red and the penultimate level is made completely black.
   * All the other levels are made black, except every 4-th level, starting from level 2,
   * which are made red. This type of coloring guarantees a balance between average times taken
   * by subsequent add and remove operations.
   */
  void compactify() {
    int oldCapa = myKeys.length;
    compactify(ColoringType.BALANCED);
    if (IntegersDebug.PRINT) IntegersDebug.format("%20s %4d -> %4d  %H  %s", "compactify", oldCapa, myKeys.length, this, last4MethodNames());
  }

  /**
   * This method is similar to {@link com.almworks.integers.DynamicLongSetP#compactify()},
   * except the way the internal levels are colored.
   * @param coloringType the way the internal levels are colored.
   *                     Internal levels are all levels except the last one (two if the last one is not full.)
   *   <ul><li>{@link ColoringType#TO_REMOVE} colors every 2nd non-last levels red, theoretically making subsequent removals faster.
   *       <li>{@link ColoringType#BALANCED} colors every 4th non-last levels red, similar to {@link com.almworks.integers.DynamicLongSetP#compactify()}.
   *       <li>{@link ColoringType#TO_ADD} colors all non-last levels black, theoretically making subsequent additions faster.
   *   </ul>
   */
  void compactify(ColoringType coloringType) {
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
      newKeys[0] = NIL_DUMMY_KEY;
      for (LongIterator ii : src) {
        newKeys[++i] = ii.value();
        assert (i==1 || newKeys[i] >= newKeys[i-1]) : newKeys;
      }
    }
    fromPreparedArray(newKeys, srcSize, coloringType);
    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("fromSortedLongIterable");
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
    myParent = new int[myKeys.length];

    int levels = log(2, usedSize);
    int top = 1 << levels;
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
      myParent[offset] = index;
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
      myParent[myLeft[index]] = index;
      myParent[myRight[index]] = index;
    }
    return index;
  }

  /**
   * Builds a new DynamicLongSetP based on values of src. src isn't used internally, its contents are copied.
   */
  public static DynamicLongSetP fromSortedList(LongList src) {
    return fromSortedList0(src, ColoringType.BALANCED);
  }

  /**
   * This method is similar to {@link com.almworks.integers.DynamicLongSetP#fromSortedList(com.almworks.integers.LongList)},
   * except the way the internal levels are colored.
   * @param coloringType the way the internal levels are colored. Internal levels are all levels except the last two
   *                     if the last one is unfilled.
   *   <br>TO_REMOVE colors every 2th non-last level red, theoretically making subsequent removals faster;
   *   <br>BALANCED colors every 4th non-last level red, similar to {@link com.almworks.integers.DynamicLongSetP#fromSortedList(com.almworks.integers.LongList)};
   *   <br>TO_ADD colors all non-last level black, theoretically making subsequent additions faster;
   */
  public static DynamicLongSetP fromSortedList(LongList src, ColoringType coloringType) {
    return fromSortedList0(src, coloringType);
  }

  private static DynamicLongSetP fromSortedList0(LongList src, ColoringType coloringType) {
    DynamicLongSetP res = new DynamicLongSetP();
    res.fromSortedLongIterable(src, src.size(), coloringType);
    assert res.checkRedsAmount(coloringType);
    return res;
  }

  public LongIterator iterator() {
    return new FailFastLongIterator(new IndexedLongIterator(new LongArray(myKeys), new LURIterator())) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public LongIterator tailIterator(long key) {
    return new FailFastLongIterator(new IndexedLongIterator(new LongArray(myKeys), new LURIterator(key))) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }

  public void removeAll(long... keys) {
    modified();
    for (long key : keys) {
      remove0(key);
    }
    maybeShrink();
  }

  public void removeAll(LongList keys) {
    removeAll(keys.iterator());
  }

  public void removeAll(LongIterator keys) {
    modified();
    for (LongIterator it: keys) {
      remove0(it.value());
    }
    maybeShrink();
  }

  public void remove(long key) {
    exclude(key);
  }

  public boolean exclude(long key) {
    modified();
    boolean ret = remove0(key);
    maybeShrink();
    return ret;
  }

  public WritableLongSet retain(LongList values) {
    LongArray array = toLongArray();
    array.retain(values);
    clear();
    fromSortedLongIterable(array, array.size(), ColoringType.BALANCED);
    return this;
  }

  public void retain(DynamicLongSetP set) {
    LongArray array = toLongArray();
    array.retainSorted(set.toLongArray());
    clear();
    fromSortedList(array, ColoringType.BALANCED);
  }

  private void maybeShrink() {
/*
    int s = size();
    int l = myKeys.length;
    if (s > SHRINK_MIN_LENGTH && s*SHRINK_FACTOR < myKeys.length) {
      compactify();
      IntegersDebug.format("%20s %4d -> %4d  %H  %s", "shrink", l, myKeys.length, this, last4MethodNames());
    }
*/
  }

  private boolean remove0(long key) {
    if (isEmpty()) return false;

    IntegersDebug.println("=== Remove", key, "===");

    //searching for an index Z which contains the key.
    int z = myRoot;
    while (myKeys[z] != key && z != 0) {
      z = key < myKeys[z] ? myLeft[z] : myRight[z];
    }
    if (z == 0) return false;

    // searching for an index Y which will be actually cleared.
    int y = z;
    if (myLeft[z] != 0 && myRight[z] != 0) {
      y = myRight[y];
      while (myLeft[y] != 0) {
        y = myLeft[y];
      }
    }
    if (z != y) myKeys[z] = myKeys[y];

    // Child of Y. Y can't have 2 children.
    int x = (myLeft[y] != 0) ? myLeft[y] : myRight[y];

    int yp = myParent[y];
    if (x != 0 || y == myRoot) {
      if (yp == 0) myRoot = x;
      else if (y == myLeft[yp]) myLeft[yp] = x;
      else myRight[yp] = x;
      myParent[x] = yp;
      if (myBlack.get(y)) {
        balanceAfterRemove(x);
      }
    } else {
      balanceAfterRemove(y);
      if (yp != 0) {
        if (y == myLeft[yp]) myLeft[yp] = 0;
        else if (y == myRight[yp]) myRight[yp] = 0;
        else assert false;
      }
    }
    free(y);

    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("remove key:" + key);

    if (IntegersDebug.PRINT) {
      debugPrintTreeStructure(System.out);
    }

    return true;
  }

  private boolean free(int y) {
    myKeys[y] = 0;
    myLeft[y] = 0;
    myRight[y] = 0;
    if (y == myFront-1) {
      myFront--;
    } else {
      if (myRemoved.isEmpty()) myRemoved = new BitSet(y+1);
      myRemoved.set(y);
    }
    boolean black = myBlack.get(y);
    myBlack.clear(y);
    return black;
  }

  private void balanceAfterRemove(int x) {
    int[] mainBranch, otherBranch;
    int xp, w;
    while (x != myRoot && myBlack.get(x)) {
      xp = myParent[x];
      if (myLeft[xp] == x) {
        mainBranch = myLeft;
        otherBranch = myRight;
      } else {
        mainBranch = myRight;
        otherBranch = myLeft;
      }
      w = otherBranch[xp];

      if (IntegersDebug.PRINT) {
        IntegersDebug.println("balance", "x =", keyOrNil(x), "w =", keyOrNil(w));
      }

      if (!myBlack.get(w)) {
        // then loop is also finished
        myBlack.set(w);
        myBlack.clear(xp);
        rotate(xp, myParent[xp], mainBranch, otherBranch);
        w = otherBranch[xp];
      }
      if (myBlack.get(mainBranch[w]) && myBlack.get(otherBranch[w])) {
        myBlack.clear(w);
        x = xp;
      } else {
        if (myBlack.get(otherBranch[w])) {
          myBlack.set(mainBranch[w]);
          myBlack.clear(w);
          rotate(w, xp, otherBranch, mainBranch);
          w = otherBranch[xp];
        }
        myBlack.set(w, myBlack.get(xp));
        myBlack.set(xp);
        myBlack.set(otherBranch[w]);
        rotate(xp, myParent[xp], mainBranch, otherBranch);
        x = myRoot;
      }
    }
    myBlack.set(x);
  }

  private String keyOrNil(int x) {
    return myKeys[x] == NIL_DUMMY_KEY ? "NIL" : String.valueOf(myKeys[x]);
  }

  /**
   * @return sorted LongArray with values from this set
   * */
  public LongArray toLongArray() {
    long[] arr = new long[size()];
    int i = 0;
    for (IntIterator it : new LURIterator()) {
      arr[i++] = myKeys[it.value()];
    }
    return new LongArray(arr);
  }

  public LongList toList() {
    return toLongArray();
  }

  public String toDebugString() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    debugPrintTreeStructure(new PrintStream(baos));
    try {
      return baos.toString("US-ASCII");
    } catch (UnsupportedEncodingException e) {
      assert false: e;
      return "DynamicLongSetP";
    }
  }

  @Override
  public String toString() {
    return LongCollections.toBoundedString(this);
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
    IntArray xs = new IntArray();
    IntArray auxVals = new IntArray();
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

    assert myKeys[0] == NIL_DUMMY_KEY : whatWasDoing + "\n" + myKeys[0];
    assert myLeft[0] == 0  : whatWasDoing + "\n" + myLeft[0];
    assert myRight[0] == 0 : whatWasDoing + "\n" + myRight[0];
    assert myParent[0] == 0 : whatWasDoing + "\n" + myParent[0];
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

        // 4. Check parents
        assert l == 0 || myParent[l] == x : debugMegaPrint(whatWasDoing + "\n" + l + " " + x, l);
        assert r == 0 || myParent[r] == x : debugMegaPrint(whatWasDoing + "\n" + r + " " + x, r);

        return bh;
      }
    });

    // 5. Red-black tree property-2: Root is black
    assert myBlack.get(myRoot) : debugMegaPrint(whatWasDoing, myRoot);

    // any unreachable node should be contained in myRemoved
    final BitSet unremoved = new BitSet(myFront);
    visitULR(0, new IntFunction2() {
      @Override
      public int invoke(int x, int _) {
        if (x != 0) unremoved.set(x);
        return _;
      }
    });
    if (myRemoved.isEmpty())
      assert unremoved.cardinality() == size() : whatWasDoing + "\n" + size() + ' ' + unremoved.cardinality() + '\n' + unremoved + '\n' + dumpArrays(0);
    else {
      assert myRemoved.length() <= myFront : myFront + "\n" + myRemoved + "\n" + debugMegaPrint(whatWasDoing, 0);
      BitSet xor = new BitSet(myFront);
      xor.or(myRemoved);
      xor.xor(unremoved);
      assert xor.nextClearBit(1) == myFront : myFront + " " + xor.nextClearBit(1) + '\n' + xor + '\n' + myRemoved + '\n' + unremoved + '\n' + debugMegaPrint(whatWasDoing, 0);
    }

    // BONUS: check parents
    for (IntIterator node : nodeLurIterator()) {
      int x = node.value();
      int l = myLeft[x];
      if (l > 0) assert myParent[l] == x : x + " " + l + " " + myParent[l] + "\n" + debugMegaPrint(whatWasDoing, x);
      int r = myRight[x];
      if (r > 0) assert myParent[r] == x : x + " " + r + " " + myParent[r] + "\n" + debugMegaPrint(whatWasDoing, x);
    }
    assert myParent[myRoot] == 0 : debugMegaPrint(whatWasDoing, myRoot);

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
    long longestKey = max(abs(getUpperBound()), abs(getLowerBound()));
    int keyWidth = max(log(10, longestKey), 3);
    sb.append(String.format("%" + idWidth + "s | %" + keyWidth + "s | %" + idWidth + "s | %" + idWidth + "s | %" + idWidth + "s\n",
      "id", "key", "left", "right", "parent"));
    String idFormat = "%" + idWidth + "d";
    String keyFormat = "%" + keyWidth + "d";
    for (int i = 1; i < size(); ++i) {
      sb.append(String.format(idFormat, i)).append(" | ")
          .append(String.format(keyFormat, myKeys[i])).append(" | ")
          .append(String.format(idFormat, myLeft[i])).append(" | ")
          .append(String.format(idFormat, myRight[i])).append(" | ")
          .append(String.format(idFormat, myParent[i]))
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
    return true;
  }

  private class LURIterator extends AbstractIntIterator {
    private int myValue;
    private int x = myRoot;
    private final int[] ps;
    private int psi;
    private boolean myIterated = false;

    public LURIterator() {
      ps = new int[64];
    }

    public LURIterator(long key) {
      this();
      if (myRoot == 0) return;

      // Parents stack top + 1
      psi = 0;
      long curKey;
      while (x != 0) {
        curKey = myKeys[x];
        if (key <= curKey) {
          ps[psi++] = x;
          x = myLeft[x];
        } else {
          x = myRight[x];
        }
      }
    }

    public boolean hasNext() throws ConcurrentModificationException {
      return x != 0 || psi > 0;
    }

    public IntIterator next() throws ConcurrentModificationException, NoSuchElementException {
      if (!hasNext()) throw new NoSuchElementException();
      myIterated = true;
      if (x == 0) {
        x = ps[--psi];
      }  else {
        int l = myLeft[x];
        while (l != 0) {
          ps[psi++] = x;
          x = l;
          l = myLeft[x];
        }
      }
      myValue = x;
      x = myRight[x];
      return this;
    }

    public boolean hasValue() {
      return myIterated;
    }

    public int value() throws NoSuchElementException {
      if (!hasValue()) throw new NoSuchElementException();
      return myValue;
    }
  }

  /********************************************************************************************************************/
  /** MODIFICATIONS FOLLOW             */

  public DynamicLongSetP clone() {
    try {
      DynamicLongSetP set = (DynamicLongSetP) super.clone();
      set.myFront = myFront;
      set.myRoot = myRoot;
      set.myBlack.or(myBlack);
      set.myRemoved.or(myRemoved);
      System.arraycopy(myKeys, 0, set.myKeys, 0, myKeys.length);
      System.arraycopy(myLeft, 0, set.myLeft, 0, myLeft.length);
      System.arraycopy(myRight, 0, set.myRight, 0, myRight.length);
      System.arraycopy(myParent, 0, set.myParent, 0, myParent.length);
      return set;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  int left(int node) {
    return myLeft[node];
  }

  int right(int node) {
    return myRight[node];
  }

  int key(int node) {
    return myRight[node];
  }

  boolean black(int node) {
    return myBlack.get(node);
  }

  IntIterator nodeLurIterator() {
    return new LURIterator();
  }

  IntIterator nodeLurIterator(int node) {
    if (!contains(myKeys[node])) throw new IllegalStateException(String.valueOf(node));
    return new LURIterator(myKeys[node]);
  }

  boolean leftChildOfParent(int node) {
    if (node == myRoot || node == 0) return true;
    boolean l = myLeft[myParent[node]] == node;
    boolean r = myRight[myParent[node]] == node;
    assert l ^ r : node + " " + l + " " + r + "|" + debugMegaPrint("", node);
    return l;
  }

  int parent(int node) {
    return myParent[node];
  }

  int uncle(int node) {
    if (node == myRoot) return myRoot;
    int parent = myParent[node];
    return (leftChildOfParent(parent) ? myRight : myLeft)[myParent[parent]];
  }
}
