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
import com.almworks.integers.util.LongSizedIterable;

import java.io.*;
import java.util.*;

import static com.almworks.integers.IntIterators.range;
import static java.lang.Math.abs;
import static java.lang.Math.max;

/** A red-black tree implementation of a set. Single-thread access only. <br/>
 * Use if you are frequently adding and querying. */
public class LongTreeSet extends AbstractWritableLongSet implements WritableLongSortedSet {
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
  /** Node color : false for red, true for black. */
  private BitSet myBlack;
  /** List of removed nodes. Null if no internal nodes are removed */
  private BitSet myRemoved;
  /** Index into key, left, right, black that denotes the current root node. */
  private int myRoot;

  private static final int SHRINK_FACTOR = 3; // for testing try 6
  private static final int SHRINK_MIN_LENGTH = 8; // for testing try 4
  // used in fromSortedLongIterable(). A new myKeys array is created in this method, and its size is
  // the size of the given LongIterable multiplied by this constant (additional space for new elements to be added later).

  int myMaxSize = -1;
  int myCountedHeight = -1;

  private int[] myStackCache = IntegersUtils.EMPTY_INTS;

  /**
   * This enum is used in {@link LongTreeSet#compactify(LongTreeSet.ColoringType)} and
   * {@link LongTreeSet#initFromSortedUnique(LongIterable, int, com.almworks.integers.LongTreeSet.ColoringType)}
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

  public LongTreeSet() {
    myBlack = new BitSet();
    myRemoved = new BitSet();
    init();
  }

  /**
   * Constructs an empty <tt>LongTreeSet</tt> with the specified initial capacity.
   * */
  public LongTreeSet(int initialCapacity) {
    if (initialCapacity < 0) throw new IllegalArgumentException();
    initialCapacity += 1;
    myBlack = new BitSet(initialCapacity);
    myRemoved = new BitSet(initialCapacity);
    init();
    myKeys = new long[initialCapacity];
    myLeft = new int[initialCapacity];
    myRight = new int[initialCapacity];
    myKeys[0] = NIL_DUMMY_KEY;
  }

  /**
   * @param coloringType the way the internal levels are colored. Internal levels are all levels except the last two
   *                     if the last one is unfilled.
   *   <br>TO_REMOVE colors every 2th non-last level red, theoretically making subsequent removals faster;
   *   <br>BALANCED colors every 4th non-last level red;
   *   <br>TO_ADD colors all non-last level black, theoretically making subsequent additions faster;
   * @return new {@code LongTreeSet} with elements from {@code src} with the specified capacity and coloring type.
   */
  public static LongTreeSet createFromSortedUnique(LongIterable src, int capacity, ColoringType coloringType) {
    if (capacity < 0) throw new IllegalArgumentException();
    LongTreeSet res = new LongTreeSet();
    res.initFromSortedUnique(src, capacity, coloringType);
    return res;
  }

  /**
   * To create {@code LongTreeSet} with the specified capacity use
   * {@link com.almworks.integers.LongTreeSet#createFromSortedUnique(LongIterable, int, com.almworks.integers.LongTreeSet.ColoringType)}
   * @return {@code LongTreeSet} with elements from {@code src}.
   * If {@code src} is a {@code LongSizedIterable}, capacity of new set equals to {@code src.size()}.
   */
  public static LongTreeSet createFromSortedUnique(LongIterable src) {
    return createFromSortedUnique(src,
        LongCollections.sizeOfIterable(src, 0), ColoringType.BALANCED);
  }

  private void init() {
    myKeys = EMPTY_KEYS;
    myLeft = EMPTY_INDEXES;
    myRight = EMPTY_INDEXES;
    myBlack.set(0);
    myRoot = 0;
    myFront = 1;
    myRemoved.clear();
    myStackCache = IntegersUtils.EMPTY_INTS;
  }

  public void clear() {
    modified();
    myBlack.clear();
    init();
    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("clear");
  }

  public long getUpperBound() {
    return myKeys[traverseToEnd(myRight)];
  }

  public long getLowerBound() {
    if (isEmpty()) return Long.MAX_VALUE;
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
    return myFront - 1 - myRemoved.cardinality();
  }

  @Override
  public boolean isEmpty() {
    boolean ret = myRoot == 0;
    assert (size() == 0) == ret : size() + " " + myRoot;
    return ret;
  }

  // todo optimize, if keys small, it's cheaper create new LongArray and use fromSortedList(..)
  // K* log(S + K) -> K*log(K) + (S + K)
  @Override
  public void addAll(LongList keys) {
    addAll((LongSizedIterable)keys);
  }

  public void addAll(LongSizedIterable keys) {
    modified();
    int[] ps = prepareAdd(keys.size());
    for (LongIterator ii : keys) {
      include0(ii.value(), ps);
    }
  }

  @Override
  public void addAll(long... values) {
    modified();
    if (values != null && values.length != 0) {
      if (values.length == 1) {
        add(values[0]);
      } else {
        addAll(new LongArray(values));
      }
    }
  }


  // todo optimize - we can don't check myStackCache on the every adding
  @Override
  public void addAll(LongIterable iterable) {
    modified();
    for (LongIterator it : iterable) {
      include0(it.value());
    }
  }

  /**
   * @return false if set already contains key
   * */
  protected boolean include0(long key) {
    return include0(key, prepareAdd(1));
  }

  private boolean include0(long key, int[] ps) {
    int x = myRoot;
    // we need these zeros for the balancing,
    // where we need to know the parent and the grandparent of the node being balanced;
    // since we start from the root, its parent and grandparent are zeros
    ps[0] = 0;
    ps[1] = 0;
    // Parents stack top + 1
    int psi = 2;
    // actually, k is always overwritten if it is used (psi > 2), but compiler does not know that
    long k = 0;
    while (x != 0) {
      k = myKeys[x];
      if (key == k) return false;
      ps[psi++] = x;
      x = key < k ? myLeft[x] : myRight[x];
    }
    x = createNode(key);

    // x is RED already (myBlack.get(x) == false), so no modifications to myBlack
    // Insert into the tree
    if (psi == 2) myRoot = x;
    else (key < k ? myLeft : myRight)[ps[psi - 1]] = x;
    balanceAfterAdd(x, ps, psi, key);
    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("add key:" + key);
    return true;
  }

  private int createNode(long key) {
    int x;
    if (myRemoved.isEmpty()) {
      x = myFront++;
    } else {
      x = myRemoved.nextSetBit(0);
      myRemoved.clear(x);
    }
    myKeys[x] = key;
    myLeft[x] = myRight[x] = 0;
    myBlack.clear(x);
    return x;
  }

  /**
   * @param x the node being inserted, RED
   * @param ps ancestors of the node x; ps[0] is the root, ps[psi] is the parent of x
   * @param psi currently used ancestor of x; if psi is < 0, we continue to think that the corresponding ancestor is NIL.
   * @param debugKey a string to assist understanding what were we adding if assertion fails
   * */
  private void balanceAfterAdd(int x, int[] ps, int psi, long debugKey) {
    // parent
    int p = ps[--psi];
    // grandparent
    int pp = ps[--psi];
    while (!myBlack.get(p)) {
      assert !IntegersDebug.CHECK || checkChildParent(p, pp, debugKey);
      assert !IntegersDebug.CHECK || checkChildParent(x, p, debugKey);
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
        p = ps[--psi];
        pp = ps[--psi];
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
        // We're not outside the array, because for that we need pp == 0, what are we rotating then?
        int ppp = ps[--psi];
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
    int oldSz = myKeys.length;
    int futureSize = size() + n + 1;
    if (futureSize > myKeys.length) {
      // length of myKeys, myLeft, myRight are always the same
      myKeys = LongCollections.ensureCapacity(myKeys, futureSize);
      myLeft = IntCollections.ensureCapacity(myLeft, futureSize);
      myRight = IntCollections.ensureCapacity(myRight, futureSize);
    }
    if (IntegersDebug.PRINT) IntegersDebug.format("%20s %4d -> %4d  %H  %s\n", "grow", oldSz, myKeys.length, this, last4MethodNames());
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
   * @param n difference between future size and actual size
   */
  private int[] fetchStackCache(int n) {
    // 2 is for the add(): sometimes we need to know the grandparent
    int fh = height(size() + n) + 2;
    if (myStackCache.length < fh) {
      myStackCache = new int[fh];
    }
    return myStackCache;
  }

  /** Estimate tree height: it can be shown that it's <= 2*log_2(N + 1) (not counting the root) */
  private int height(int n) {
    if (n < myMaxSize) return myCountedHeight;
    myMaxSize = 1;

    int lg2 = 0;
    while (myMaxSize <= n) {
      lg2++;
      myMaxSize <<= 1;
    }
    myCountedHeight = lg2 << 1;
    return myCountedHeight;
  }

  /**
   * This method rebuilds this LongTreeSet, after that it will use just the amount of memory needed to hold size() elements.
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
   * This method is similar to {@link LongTreeSet#compactify()},
   * except the way the internal levels are colored.
   * @param coloringType the way the internal levels are colored.
   *                     Internal levels are all levels except the last one (two if the last one is not full.)
   *   <ul><li>{@link ColoringType#TO_REMOVE} colors every 2nd non-last levels red, theoretically making subsequent removals faster.
   *       <li>{@link ColoringType#BALANCED} colors every 4th non-last levels red, similar to {@link LongTreeSet#compactify()}.
   *       <li>{@link ColoringType#TO_ADD} colors all non-last levels black, theoretically making subsequent additions faster.
   *   </ul>
   */
  void compactify(ColoringType coloringType) {
    modified();
    initFromSortedUnique(this, size(), coloringType);
  }

  private void initFromSortedUnique(LongIterable src, int capacity, ColoringType coloringType) {
    LongArray buf = new LongArray(capacity + 1);
    buf.add(NIL_DUMMY_KEY);
    buf.addAll(src);
    int size = buf.size() - 1;
    long[] newKeys = buf.extractHostArray();
    assert LongCollections.isSortedUnique(false, newKeys, 1, size) == 0;
    initFromPreparedArray(newKeys, size, coloringType);
    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("initFromSortedUnique");
  }

  //   An idea of another BALANCED coloring that is a good tradeoff between TO_ADD and TO_REMOVE.
  // We don't do rotations in add() if the parent is black;
  // we don't do rotations in remove() if the removed node is red.
  //
  // Obviously, the probability of both events depends on the ratio of red nodes to all nodes,
  // and in first case it should be minimal (TO_ADD gives 0),
  // whereas in the second case in should be maximal (TO_REMOVE gives 2/3 + Θ(1/N));
  //
  // also, if we do inverse operations (remove() in the first case, add() in the second),
  // it's quite clear that the probability of rotations will reach the maximum with these colorings,
  // so as we take a somewhat-in-between ratio, this probability goes down.
  //
  // The consequence is that a good BALANCED setting will be the one that colors roughly half of the nodes red.
  //
  // Let's consider the full tree case first.
  // It's obvious that if we color the last level red, the ratio will be 1/2 + Θ(1/N).
  //
  // Now, if the last level is not fully packed, let's do it this way:
  // If more than 7/8 of the last level is filled, then we color just the last level.
  // The ratio in this case has lower bound 7/16 or ~44%.
  // Otherwise, color the level two levels higher (it contains 1/4 of what the last level would contain, if it had been fully packed.)
  // If it's not enough, add level four levels higher, and so on.
  // So we check if the number of nodes on the last level, d, falls into these segments:
  //
  // | if d > 7/8 = 1/2 + 3/4*1/2      | color 1 level (the last one) | ratio: 44..50% |
  // | otherwise, if d > 1/2 + 3/4*1/4 | 2 levels                     | ratio: 47..56% |
  // | otherwise, if d > 1/2 + 3/4*1/8 | 3 levels                     | ratio: 48..53% |
  // ...
  // We can just use a simple formula to get the amount of levels, starting from the last one, that need to be colored red:
  // k = floor(log_2(n)); d = n - (2^k - 1); [amount of levels] = - floor(4/3*log_2(d/(2^k) - 1/2))
  // And then just color these amount of lower levels.
  /**
   * @param newKeys array which will become the new myKeys
   * @param usedSize a number of actual elements in newKeys
   * @param coloringType a type of coloring new tree
   *
   * <br>For ADD, we want to maximize the amount of black nodes. We can color all levels except the last if it not completely filled.
   * <br>For REMOVED, we want to maximize the amount of red nodes. An easy way to do this is to color the whole levels,
   * making each second level red. (We don't implement the more optimal but harder approach.) We start with the
   * last level, which we color red, and go bottom-up, which is the optimal way if we color each second level red;
   * let's see why.
   * If the tree is not full (N != 2^K - 1), we must color the last level red to keep black heights equal on all
   * paths.
   * Otherwise, let us assume that level numbering starts with 0, which is the black root. So, the last level
   * will have number K-1. There are two cases.
   * K is odd: if we color red each second level starting from the last one, the first colored level will have
   * number 2, and we'll have 2^2 + 2^4 + ... + 2^{K - 1} red nodes (2/3 of the whole tree); if we did otherwise
   * (color from level 1), we'd have 2^1 + 2^3 + ... + 2^{K - 2} red nodes, which is 2 times less.
   * K is even: color levels from the last level up to the level number 1, it is better than coloring from level
   * number 2: 2^1 + 2^3 + ... + 2^{K - 1} > 2^2 + ... + 2^{K - 2}.
   */
  private void initFromPreparedArray(long[] newKeys, int usedSize, ColoringType coloringType) {
    myBlack = new BitSet(usedSize);
    myRemoved = new BitSet(usedSize);

    init();
    myKeys = newKeys;
    myFront = usedSize+1;
    if (usedSize == 0)
      return;
    myLeft = new int[myKeys.length];
    myRight = new int[myKeys.length];

    int levels = log(2, usedSize), step = coloringType.redLevelsDensity();
    boolean[] levelsColoring = new boolean[levels];

    // coloring of levels: (1 - black, 0 - red)
    // TO_ADD:      1...1111110 (or 1...1111111 if last level completely filled)
    // TO_REMOVE:   1...0101010
    // TO_BALANCED: 1...0111011101110
    Arrays.fill(levelsColoring, true);
    if (coloringType == ColoringType.TO_ADD) {
      // if last level completely filled (usedSize = 2^k - 1) and cT = TO_ADD color is black otherwise red
      levelsColoring[levels - 1] = (usedSize & (usedSize + 1)) == 0;
    } else {
      for (IntIterator it: range(levels - 1, 0, -step)) {
        levelsColoring[it.value()] = false;
      }
    }
    myRoot = rearrangeStep(1, usedSize, 0, levelsColoring);
  }

  private int rearrangeStep(int offset, int length, int curLevel, boolean[] levelsColoring) {
    if (length == 0) return 0;
    int halfLength = length / 2;
    int index = offset + halfLength;
    myBlack.set(index, levelsColoring[curLevel]);

    myLeft[index] = rearrangeStep(offset, halfLength, curLevel + 1, levelsColoring);
    myRight[index] = rearrangeStep(index + 1, length - halfLength - 1, curLevel + 1, levelsColoring);
    return index;
  }

  public LongIterator iterator() {
    return failFast(new IndexedLongIterator(new LongArray(myKeys), new LURIterator()));
  }

  public LongIterator tailIterator(long fromElement) {
    return failFast(new IndexedLongIterator(new LongArray(myKeys), new LURIterator(fromElement)));
  }

  @Override
  public void removeAll(long... values) {
    modified();
    removeAll(new LongArrayIterator(values));
  }

  @Override
  public void removeAll(LongIterator iterator) {
    modified();
    int[] parentStack = fetchStackCache(0);
    for (LongIterator it: iterator) {
      exclude0(it.value(), parentStack);
    }
    maybeShrink();
  }

  protected boolean exclude0(long key) {
    boolean ret = exclude0(key, fetchStackCache(0));
    maybeShrink();
    return ret;
  }

  public void retain(LongList values) {
    LongArray res = new LongArray();
    for (LongIterator it: values.iterator()) {
      long value = it.value();
      if (contains(value)) res.add(value);
    }
    clear();
    res.sortUnique();
    initFromSortedUnique(res, res.size(), ColoringType.BALANCED);
  }

  /**
   * Retains this set with the specified set
   * */
  public void retain(LongSortedSet set) {
    LongArray array = toArray();
    array.retainSorted(set.toArray());
    clear();
    initFromSortedUnique(array, array.size(), ColoringType.BALANCED);
  }

  private void maybeShrink() {
    int s = size();
    if (s > SHRINK_MIN_LENGTH && s*SHRINK_FACTOR < myKeys.length)
      compactify();
  }

  private boolean exclude0(long key, int[] parentsStack) {
    if (isEmpty()) return false;

    int xsi = 0;
    parentsStack[0] = 0;

    //searching for an index Z which contains the key.
    int z = myRoot;
    while (myKeys[z] != key || z == 0) {
      if (z == 0)
        return false;
      parentsStack[++xsi] = z;
      z = key < myKeys[z] ? myLeft[z] : myRight[z];
    }

    // picking the node which will be actually cleared following z in the LUR order.
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
    int x = myLeft[y];
    if (x == 0) x = myRight[y];

    // removing y from tree
    if (y == myRoot) {
      myRoot = x;
    } else {
      int parentOfY = parentsStack[xsi];
      if (myLeft[parentOfY] == y)
        myLeft[parentOfY] = x;
      else myRight[parentOfY] = x;
    }

    if (myBlack.get(y)) {
      balanceAfterRemove(x, parentsStack, xsi);
    }
    free(y);

    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("remove key:" + key);
    return true;
  }

  private void free(int y) {
    myKeys[y] = 0;
    myLeft[y] = 0;
    myRight[y] = 0;
    myBlack.clear(y);
    if (y == myFront - 1) {
      myFront--;
    } else {
      myRemoved.set(y);
    }
  }

  private void balanceAfterRemove(int x, int[] parentsStack, int xsi) {
    int[] mainBranch, otherBranch;
    int parentOfX, w;
    while (x != myRoot && myBlack.get(x)) {
      parentOfX = parentsStack[xsi];
      if (x == myLeft[parentOfX]) {
        mainBranch = myLeft;
        otherBranch = myRight;
      } else {
        mainBranch = myRight;
        otherBranch = myLeft;
      }
      w = otherBranch[parentOfX];

      if (IntegersDebug.PRINT) {
        IntegersDebug.println("balance", "x =", keyOrNil(x), "w =", keyOrNil(w));
      }

      if (!myBlack.get(w)) {
        // the loop is also finished after the current iteration for any branch we take
        myBlack.set(w);
        myBlack.clear(parentOfX);
        rotate(parentOfX, parentsStack[xsi-1], mainBranch, otherBranch);
        parentsStack[xsi] = w;
        parentsStack[++xsi] = parentOfX;
        w = otherBranch[parentOfX];
      }
      // now w always black
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
        // now otherBranch[w] always red
        myBlack.set(parentOfX);
        myBlack.set(otherBranch[w]);
        rotate(parentOfX, parentsStack[xsi - 1], mainBranch, otherBranch);
        x = myRoot;
      }
    }
    myBlack.set(x);
  }

  private String keyOrNil(int x) {
    return myKeys[x] == NIL_DUMMY_KEY ? "NIL" : String.valueOf(myKeys[x]);
  }

  @Override
  protected void toNativeArrayImpl(long[] dest, int destPos) {
    int i = destPos;
    for (IntIterator it : new LURIterator()) {
      dest[i++] = myKeys[it.value()];
    }
  }

  public String toDebugString() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    debugPrintTreeStructure(new PrintStream(baos));
    try {
      return baos.toString("US-ASCII");
    } catch (UnsupportedEncodingException e) {
      assert false: e;
      return "LongTreeSet";
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
    assert myKeys[0] == NIL_DUMMY_KEY : whatWasDoing + "\n" + myKeys[0];
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
    final int heightEstimate = height(size());
    visitULR(0, new IntFunction2() {
      @Override
      public int invoke(int x, int h) {
        if (myLeft[x] == 0 && myRight[x] == 0) {
          // we're at the bottom
          assert heightEstimate >= h : whatWasDoing + "\n" + h + ' ' + heightEstimate + ' ' + size() + ' ' + myFront;
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
    if (myRemoved.isEmpty())
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
    long longestKey = max(abs(getUpperBound()), abs(getLowerBound()));
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

  private class LURIterator extends AbstractIntIterator {
    private int myValue;
    private int x = myRoot;
    private final int[] ps;
    private int psi;
    private boolean myIterated = false;

    public LURIterator() {
      int[] cache = myStackCache;
      if (cache == null || cache.length < height(size())) {
        ps = new int[height(size())];
      } else {
        ps = cache;
        myStackCache = IntegersUtils.EMPTY_INTS;
      }
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
}
