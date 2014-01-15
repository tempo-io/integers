package com.almworks.integers;

import com.almworks.integers.DynamicLongSet;
import com.almworks.integers.IntIterator;
import com.almworks.integers.IntegersFixture;

/**
 * Generator of a test case that covers all cases in red-black tree remove.
 * The generated sequence is a series of batches, each containing 3 additions and 2 removals.
 * */
public class LTSCasesGenTests extends IntegersFixture {

  // dimension 0: (two children) or (one child and the property is fulfilled by the next node) | size = 2
  // dimension 1: deleted node is R or B                                                       | size = 2
  // dimension 2: the deleted node is left / right child of its parent                         | size = 2
  // dimension 3: cases                                                                        | size = 5
  // case 1: uncle of deleted node is red
  // case 2: uncle of deleted node is black && all cases for children colors (4 cases)
  //
  boolean[][][][] casesVisited = new boolean[2][][][];
  int casesLeft;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    casesLeft = 0;
    for (int d0 = 0; d0 < 2; ++d0) {
      casesVisited[d0] = new boolean[2][][];
      for (int d1 = 0; d1 < 2; ++d1) {
        casesVisited[d0][d1] = new boolean[2][];
        for (int d2 = 0; d2 < 2; ++d2) {
          casesVisited[d0][d1][d2] = new boolean[5];
          casesLeft += 5;
        }
      }
    }
  }

  public void testGen() {
    int MAX_VAL = 100;

    DynamicLongSet set = new DynamicLongSet();
    while (casesLeft > 0) {
    batch:
      for (int add1 = 0; add1 < MAX_VAL; ++add1) {
        for (int add2 = 0; add2 < MAX_VAL; ++add2) {
          if (add2 == add1) continue;
          for (int add3 = 0; add3 < MAX_VAL; ++add3) {
            if (add3 == add1 || add3 == add2) continue;

            DynamicLongSet curSet = set.clone();
            curSet.addAll(add1, add2, add3);

            int nRem = 0;
          rem:
            for (int rem = 0; rem < MAX_VAL; ++rem) {
              for (IntIterator nodeIt : curSet.nodeLurIterator()) {
                if (isUnseenCaseRealization(curSet, nodeIt.value())) {
                  casesLeft -= 1;
                  curSet.exclude(curSet.key(nodeIt.value()));
                  if (nRem == 0) {
                    nRem = 1;
                    break rem;
                  } else {
                    set = curSet;
                    System.out.format("add %d %d %d rem %d%n", add1, add2, add3, rem);
                    break batch;
                  }
                }
              }
            }
          }
        }
      }
      // Nothing added, add random
      int add1 = RAND.nextInt(MAX_VAL);
      int add2 = RAND.nextInt(MAX_VAL);
      int add3 = RAND.nextInt(MAX_VAL);
      set.addAll(add1, add2, add3);
      System.out.format("add %d %d %d%n", add1, add2, add3);
    }
  }

  private boolean isUnseenCaseRealization(DynamicLongSet set, int node) {
    // dim0
    int delNode;
    int d0case;
    if (set.left(node) > 0 && set.right(node) > 0) {
      delNode = set.nodeLurIterator(node).next().nextValue();
      d0case = 0;
    } else {
      delNode = node;
      d0case = 1;
    }

    // dim1
    int d1case = set.black(delNode) ? 0 : 1;

    // all the following cases do not make sense if node is red, since there is no balancing

    // dim2
    int d2case = set.leftChildOfParent(delNode) ? 0 : 1;

    // dim3
    // case1
    int delUncle = set.uncle(delNode);
    int d3case;
    if (!set.black(delUncle)) {
      d3case = 0;
    } else {
      todo
    }

    return false;
  }
}
