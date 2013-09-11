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

import com.almworks.integers.util.IntegersDebug;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.util.*;

/** A red-black tree implementation of a set. Single-thread access only. <br/>
 * Use if you are frequently adding and querying. */
@SuppressWarnings("PointlessBooleanExpression")
public class DynamicLongSetE implements LongIterable {
  private static final boolean BLACK = true, RED = false;
  private static final boolean LEFT = true, RIGHT = false;
  private static class Entry {
    long key;
    boolean color;
    Entry l;
    Entry r;

    Entry(long k) {
      this(k, RED);
    }

    private Entry(long k, boolean c) {
      key = k;
      color = c;
      l = NIL;
      r = NIL;
    }

    @Override
    public String toString() {
      return this == NIL ? "#" : (color ? "x " : "o ") + key;
    }
  }
  private static final long NIL_DUMMY_KEY = Long.MIN_VALUE;
  private static final Entry NIL = new Entry(NIL_DUMMY_KEY, BLACK);
  static {
    NIL.l = NIL;
    NIL.r = NIL;
  }
  private Entry myRoot;
  private int mySize;

  private int myModCount = 0;

  private static final int SHRINK_FACTOR = 3; // for testing try 6
  private static final int SHRINK_MIN_LENGTH = 8; // for testing try 4
  // used in fromSortedLongIterable(). A new myKeys array is created in this method, and its size is
  // the size of the given LongIterable multiplied by this constant (additional space for new elements to be added later).
  private static final int EXPAND_FACTOR = 2;

  int minSize = -1;
  int maxSize = -1;
  int countedHeight = -1;

  private static final Entry[] EMPTY_ENTRIES = new Entry[0];
  private SoftReference<Entry[]> myStackCache = new SoftReference<Entry[]>(EMPTY_ENTRIES);

  public DynamicLongSetE() {
    init();
  }

  private void init() {
    myRoot = NIL;
    myStackCache = new SoftReference<Entry[]>(EMPTY_ENTRIES);
  }

  public void clear() {
    modified();
    init();
    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("clear");
  }

  private void modified() {
    myModCount++;
  }

  /** @return {@link Long#MIN_VALUE} in case the set is empty */
  public long getUpperBound() {
    return traverseToEnd(RIGHT).key;
  }

  /** @return {@link Long#MAX_VALUE} in case the set is empty */
  public long getLowerBound() {
    if (size() == 0) return Long.MAX_VALUE;
    return traverseToEnd(LEFT).key;
  }

  private Entry traverseToEnd(boolean branch) {
    Entry x = myRoot;
    for (Entry nextX = branch ? x.l : x.r; nextX != NIL; nextX = branch ? x.l : x.r) {
      x = nextX;
    }
    return x;
  }

  public boolean contains(long key) {
    Entry x = myRoot;
    long k = x.key;
    while (x != NIL && k != key) {
      x = key < k ? x.l : x.r;
      k = x.key;
    }
    return x != NIL;
  }

  public boolean containsAll(LongIterable keys) {
    for (LongIterator it : keys.iterator()) {
      if (!contains(it.value())) return false;
    }
    return true;
  }

  public int size() {
    return mySize;
  }

  public boolean isEmpty() {
    boolean ret = myRoot == NIL;
    assert (size() == 0) == ret : size() + " " + myRoot;
    return ret;
  }

  public void addAll(LongList keys) {
    modified();
    Entry[] ps = prepareAdd(keys.size());
    for (LongIterator i : keys) {
      push0(i.value(), ps);
    }
  }

  public void addAll(DynamicLongSetE keys) {
    modified();
    Entry[] ps = prepareAdd(keys.size());
    for (LongIterator ii : keys) {
      push0(ii.value(), ps);
    }
  }

  public void addAll(long... keys) {
    modified();
    Entry[] ps = prepareAdd(keys.length);
    for (long key : keys) {
      push0(key, ps);
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
    return push0(key, prepareAdd(1));
  }

  private boolean push0(long key, Entry[] ps) {
    Entry x = myRoot;
    // Parents stack top + 1
    int psi = 0;
    // actually, k is always overwritten if it is used (psi > 0), but compiler does not know that
    long k = 0;
    while (x != NIL) {
      k = x.key;
      if (key == k) return false;
      ps[psi++] = x;
      x = key < k ? x.l : x.r;
    }
    x = createNode(key);

    // x is RED already (myBlack.get(x) == false), so no modifications to myBlack
    // Insert into the tree
    if (psi == 0) myRoot = x;
    else {
      if (key < k) ps[psi - 1].l = x;
      else         ps[psi - 1].r = x;
    }
    balanceAfterAdd(x, ps, psi, key);
    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("add key:" + key);
    return true;
  }

  private Entry createNode(long key) {
    mySize += 1;
    return new Entry(key);
  }

  /**
   * @param x the node being inserted, RED
   * @param ps ancestors of the node x; ps[0] is the root, ps[psi] is the parent of x
   * @param psi currently used ancestor of x; if psi is < 0, we continue to think that the corresponding ancestor is NIL.
   * @param debugKey a string to assist understanding what were we adding if assertion fails
   * */
  private void balanceAfterAdd(Entry x, Entry[] ps, int psi, long debugKey) {
    // parent
    Entry p = getLastOrNil(ps, --psi);
    // grandparent
    Entry pp = getLastOrNil(ps, --psi);
    while (p.color != BLACK) {
      assert checkChildParent(p, pp, debugKey);
      assert checkChildParent(x, p, debugKey);
      boolean branch1 = p == pp.l;
      boolean branch2 = !branch1;
      // Uncle (parent's sibling)
      Entry u = branch2 ? pp.l : pp.r;
      if (u.color != BLACK) {
        p.color = BLACK;
        u.color = BLACK;
        pp.color = RED;
        x = pp;
        p = getLastOrNil(ps, --psi);
        pp = getLastOrNil(ps, --psi);
      } else {
        if (branch2 && x == p.l || !branch2 && x == p.r) {
          // Rotate takes x and makes it a branch1-parent of p
          rotate(p, pp, branch2);
          // Now x is the parent of p; but we choose x' = p; so p' = x, pp' = pp
          Entry tmp = x;
          x = p;
          p = tmp;
        }
        p.color = BLACK;
        pp.color = RED;
        Entry ppp = getLastOrNil(ps, --psi);
        // Takes pp (which is now red) and makes it a branch-2 children of p (which is now black); note that branch-1 children of p is x, which is red and both children are black
        rotate(pp, ppp, branch1);
      }
    }
    myRoot.color = BLACK;
  }

  private boolean checkChildParent(Entry child, Entry parent, long debugKey) {
    assert parent.l == child || parent.r == child : debugMegaPrint("add " + debugKey + "\nproblem with child " + child);
    return true;
  }

  private static Entry getLastOrNil(Entry[] a, int i) {
    return i < 0 ? NIL : a[i];
  }

  /**
   * Rotates node x so that it becomes a mainBranch child of its child on the otherBranch.
   * E.g., left rotate: mainBranch == myLeft, rightBranch == myRight
   * */
  private void rotate(Entry x, Entry p, boolean otherBranch) {
    Entry y;
    if (otherBranch) {
      // mainBranch == RIGHT, otherBranch == LEFT
      y = x.l;
      x.l = y.r;
      y.r = x;
    } else {
      y = x.r;
      x.r = y.l;
      y.l = x;
    }
    if (p == NIL) myRoot = y;
    else {
      if (x == p.l) p.l = y;
      else {
        assert x == p.r : "tree structure broken " + x + '\n' + dumpArrays();
        p.r = y;
      }
    }
  }

  /** @return array for holding the stack for tree traversal */
  private Entry[] prepareAdd(int n) {
    return fetchStackCache(n);
  }

  /**
   * @param n difference between future size and actual size
   * @return
   */
  private Entry[] fetchStackCache(int n) {
    int fh = estimateFutureHeight(n);
    Entry[] cache = myStackCache.get();
    if (cache != null && cache.length >= fh) return cache;
    cache = new Entry[fh];
    myStackCache = new SoftReference<Entry[]>(cache);
    return cache;
  }

  /** Returns a number higher than the height after adding n elements.*/
  private int estimateFutureHeight(int n) {
    return height(size() + n) + 1;
  }

  /** Estimate tree height: it can be shown that it's <= 2*log_2(N + 1) (not counting the root) */
  private int height(int n) {
    if (minSize <= n && n < maxSize) return countedHeight;
    maxSize = 1;

    int lg2 = 0;
    while (maxSize <= n) {
      lg2++;
      maxSize <<= 1;
    }
    minSize = maxSize >> 1;
    countedHeight = lg2 << 1;
    return countedHeight;
  }

  public LongIterator iterator() {
    return new AbstractLongIterator() {
      private LURIterator it = new LURIterator();
      private long key;

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return it.hasNext();
      }

      @Override
      public boolean hasValue() {
        return it.myIterated;
      }

      @Override
      public long value() throws NoSuchElementException {
        if (!it.myIterated) throw new NoSuchElementException();
        return key;
      }

      @Override
      public LongIterator next() {
        key = it.next().key;
        return this;
      }
    };
  }

  public LongIterator tailIterator(final long k) {
    return new AbstractLongIterator() {
      private LURIterator it = new LURIterator(k);
      private long key;

      @Override
      public boolean hasNext() throws ConcurrentModificationException {
        return it.hasNext();
      }

      @Override
      public boolean hasValue() {
        return it.myIterated;
      }

      @Override
      public long value() throws NoSuchElementException {
        if (!it.myIterated) throw new NoSuchElementException();
        return key;
      }

      @Override
      public LongIterator next() {
        key = it.next().key;
        return this;
      }
    };
  }

  public void removeAll(long... keys) {
    modified();
    Entry[] parentsStack = fetchStackCache(0);
    for (long key : keys) {
      remove0(key, parentsStack);
    }
    maybeShrink();
  }

  public void removeAll(LongList keys) {
    removeAll(keys.iterator());
  }

  public void removeAll(LongIterator keys) {
    modified();
    Entry[] parentStack = fetchStackCache(0);
    for (LongIterator it: keys) {
      remove0(it.value(), parentStack);
    }
    maybeShrink();
  }

  public void remove(long key) {
    exclude(key);
  }

  public boolean exclude(long key) {
    modified();
    boolean ret = remove0(key, fetchStackCache(0));
    maybeShrink();
    return ret;
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

  private boolean remove0(long key, Entry[] parentsStack) {
    if (isEmpty()) return false;

    int xsi = -1;

    //searching for an index Z which contains the key.
    Entry z = myRoot;
    while (z.key != key && z != NIL) {
      parentsStack[++xsi] = z;
      z = key < z.key ? z.l : z.r;
    }
    if (z == NIL) return false;

    // searching for an index Y which will be actually cleared.
    Entry y = z;
    if (z.l != NIL && z.r != NIL) {
      parentsStack[++xsi] = y;
      y = y.r;
      while (y.l != NIL) {
        parentsStack[++xsi] = y;
        y = y.l;
      }
    }
    if (z != y) z.key = y.key;

    // Child of Y. Y can't have 2 children.
    Entry x = y.l;
    if (x == NIL) x = y.r;

    if (y == myRoot) myRoot = x;
    else {
      Entry parentOfY = parentsStack[xsi];
      if (parentOfY.l == y) {
        parentOfY.l = x;
      } else {
        parentOfY.r = x;
      }
    }
    free(y);

    if (y.color == BLACK) {
      balanceAfterRemove(x, parentsStack, xsi);
      y.color = RED;
    }

    assert !IntegersDebug.CHECK || checkRedBlackTreeInvariants("remove key:" + key);
    return true;
  }

  private void free(Entry y) {
    y.l = null;
    y.r = null;
    mySize -= 1;
  }

  private void balanceAfterRemove(Entry x, Entry[] parentsStack, int xsi) {
    boolean mainBranch, otherBranch;
    Entry parentOfX, w;
    while (x != myRoot && x.color == BLACK) {
      parentOfX = parentsStack[xsi];
      mainBranch = parentOfX.l == x;
      otherBranch = !mainBranch;
      w = otherBranch ? parentOfX.l : parentOfX.r;

      if (IntegersDebug.PRINT) {
        IntegersDebug.println("balance", "x =", keyOrNil(x), "w =", keyOrNil(w));
      }

      if (w.color == RED) {
        // then loop is also finished
        w.color = BLACK;
        parentOfX.color = RED;
        rotate(parentOfX, getLastOrNil(parentsStack, xsi-1), otherBranch);
        parentsStack[xsi] = w;
        parentsStack[++xsi] = parentOfX;
        w = otherBranch ? parentOfX.l : parentOfX.r;
      }
      if (w.l.color == BLACK && w.r.color == BLACK) {
        w.color = RED;
        x = parentOfX;
        xsi--;
      } else {
        if (otherBranch && w.l.color == BLACK || !otherBranch && w.r.color == BLACK) {
          (mainBranch ? w.l : w.r).color = BLACK;
          w.color = RED;
          rotate(w, parentOfX, mainBranch);
          w = otherBranch ? parentOfX.l : parentOfX.r;
        }
        w.color = parentOfX.color;
        parentOfX.color = BLACK;
        (otherBranch ? w.l : w.r).color = BLACK;
        rotate(parentOfX, getLastOrNil(parentsStack, xsi - 1), otherBranch);
        x = myRoot;
      }
    }
    x.color = BLACK;
  }

  private String keyOrNil(Entry x) {
    return x == NIL ? "NIL" : String.valueOf(x.key);
  }

  /**
   * @return sorted LongArray with values from this set
   * */
  public LongArray toLongArray() {
    long[] arr = new long[size()];
    int i = 0;
    for (Iterator<Entry> it = new LURIterator(); it.hasNext(); ) {
      arr[i++] = it.next().key;
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
      return "DynamicLongSetE";
    }
  }

  @Override
  public String toString() {
    return LongCollections.toBoundedString(this);
  }


  interface ULRVisitor {
    int invoke(Entry e, int aux);
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
  private void visitULR(int auxInit, ULRVisitor visitor) {
    Entry x = myRoot;
    if (x == NIL) return;
    int auxVal = auxInit;
    int height = height(size());
    List<Entry> xs = new ArrayList(height);
    IntArray auxVals = new IntArray(height);
    while (true) {
      auxVal = visitor.invoke(x, auxVal);
      Entry l = x.l;
      Entry r = x.r;
      if (l != NIL) {
        x = l;
        // Visit right child even if it's NIL
        xs.add(r);
        auxVals.add(auxVal);
      } else if (r != NIL) {
        x = r;
        // Visit NIL child anyway
        visitor.invoke(NIL, auxVal);
      } else if (!xs.isEmpty()) {
        // Both children are NIL, but it is not shown
        x = xs.remove(xs.size() - 1);
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
    assert (size() == 0) == isEmpty() : whatWasDoing + " " + mySize + ' ' + myRoot;

    final int[] lastBlackHeight = new int[] {-1};
    visitULR(0, new ULRVisitor() {
      @Override
      public int invoke(Entry x, int bh) {
        if (x == NIL)
          return bh;

        // 1. Binary search tree property
        long k = x.key;
        Entry l = x.l;
        long lk = l.key;
        if (l != NIL)
          assert lk < k : debugMegaPrint(whatWasDoing);
        Entry r = x.r;
        long rk = r.key;
        if (r != NIL)
          assert rk > k : debugMegaPrint(whatWasDoing);

        // 2. Red-black tree property-1: If node is red, all its children are black
        boolean nodeIsRed = x.color == RED;
        boolean bothChildrenAreBlack = l.color == BLACK && r.color == BLACK;
        assert !(nodeIsRed && !bothChildrenAreBlack) : debugMegaPrint(whatWasDoing);

        // 3. Red-black tree property-2: number of black nodes (black height) are equal for all paths from root to leaves
        if (x.color == BLACK)
          bh += 1;
        if (l == NIL || r == NIL) {
          // We're in a leaf: check our black height against the previous one or record if we're the first
          if (lastBlackHeight[0] < 0)
            lastBlackHeight[0] = bh;
          else
            assert lastBlackHeight[0] == bh : debugMegaPrint(whatWasDoing + ' ' + lastBlackHeight[0] + ' ' + bh);
        }

        return bh;
      }
    });

    // 4. Red-black tree property-2: Root is black
    assert myRoot.color == BLACK : debugMegaPrint(whatWasDoing);

    // 5. Height estimate is not less than any actual path height
    final int heightEstimate = height(size());
    visitULR(0, new ULRVisitor() {
      @Override
      public int invoke(Entry x, int h) {
        if (x.l == NIL && x.r == NIL) {
          // we're at the bottom
          assert heightEstimate >= h : whatWasDoing + "\n" + h + ' ' + heightEstimate + ' ' + size() + ' ' + mySize;
        }
        return h + 1;
      }
    });

    return true;
  }

  private String debugMegaPrint(String whatWasDoing) {
    System.err.println(whatWasDoing);
    debugPrintTreeStructure(System.err);
    return dumpArrays().insert(0, whatWasDoing + "\n").toString();
  }

  final StringBuilder dumpArrays() {
    StringBuilder sb = new StringBuilder();
    return sb;
  }

  final void debugPrintTreeStructure(final PrintStream out) {
    out.println("Legend: x - black node, o - red node, # - NIL");
    visitULR(0, new ULRVisitor() {
      @Override
      public int invoke(Entry x, int level) {
        out.print(' ');
        for (int i = 0; i < level - 1; ++i) out.print("| ");
        if (level > 0) out.append("|-");
        out.println(x);
        return level + 1;
      }
    });
  }

  private class LURIterator implements Iterator<Entry> {
    private Entry myValue;
    private Entry x = myRoot;
    private final Entry[] ps;
    private int psi;
    private boolean myIterated = false;

    public LURIterator() {
      Entry[] cache = myStackCache.get();
      if (cache == null || cache.length < height(size())) {
        ps = new Entry[height(size())];
      } else {
        ps = cache;
        myStackCache.clear();
      }
    }

    public LURIterator(long key) {
      this();
      x = myRoot;
      // Parents stack top + 1
      psi = 0;
      // actually, curKey is always overwritten if it is used (psi > 0), but compiler does not know that
      long curKey = 0;
      while (x != NIL) {
        curKey = x.key;
        if (key <= curKey) {
          ps[psi++] = x;
          x = x.l;
        } else {
          x = x.r;
        }
      }
      if (key <= curKey) {
        while (psi >= 2 && ps[psi - 2].l == ps[psi - 1]) {
          psi--;
        }
        psi--;
        x = ps[psi];
      }
    }

    public boolean hasNext() throws ConcurrentModificationException {
      return x != NIL || psi > 0;
    }

    public Entry next() throws ConcurrentModificationException, NoSuchElementException {
      if (!hasNext()) throw new NoSuchElementException();
      myIterated = true;
      if (x == NIL) x = ps[--psi];
      else {
        Entry l = x.l;
        while (l != NIL) {
          ps[psi++] = x;
          x = l;
          l = x.l;
        }
      }
      myValue = x;
      x = x.r;
      return myValue;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    public boolean hasValue() {
      return myIterated;
    }
  }
}
