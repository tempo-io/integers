package com.almworks.integers;

import org.jetbrains.annotations.Nullable;

/**
 * Basic LCS algorithm. 
 * @author Igor Sereda
 */

// CODE GENERATED FROM com/almworks/integers/PLongestCommonSubsequence.tpl

public class IntLongestCommonSubsequence {
  private final IntList aSequence;
  private final IntList bSequence;
  private final int alen;
  private final int blen;
  private final int[] lens;

  private IntLongestCommonSubsequence(IntList aseq, IntList bseq) {
    this.aSequence = aseq;
    this.bSequence = bseq;
    alen = aseq.size();
    blen = bseq.size();
    lens = new int[alen * blen];
  }

  public static IntList getLCS(@Nullable IntList aseq,@Nullable IntList bseq) {
    if (aseq == null || bseq == null || aseq.isEmpty() || bseq.isEmpty()) return IntList.EMPTY;
    return new IntLongestCommonSubsequence(aseq, bseq).calc();
  }

  private IntList calc() {
    for (int i = 0; i < alen; i++) {
      for (int j = 0; j < blen; j++) {
        if (aSequence.get(i) == bSequence.get(j)) {
          lens[p(i, j)] = m(i - 1, j - 1) + 1;
        } else {
          lens[p(i, j)] = Math.max(m(i - 1, j), m(i, j - 1));
        }
      }
    }
    int lcslen = m(alen - 1, blen - 1);
    if (lcslen == 0) return IntList.EMPTY;
    int[] r = new int[lcslen];
    int ri = lcslen - 1;
    int i = alen - 1, j = blen - 1;
    while (i >= 0 && j >= 0 && ri >= 0) {
      int v = aSequence.get(i);
      if (v == bSequence.get(j)) {
        r[ri--] = v;
        i--;
        j--;
      } else if (m(i, j - 1) > m(i - 1, j)) {
        j--;
      } else {
        i--;
      }
    }
    assert ri == -1 : ri + " " + i + " " + j + " " + aSequence + " " + bSequence;
    assert i == -1 || j == -1 : i + " " + j + " " + aSequence + " " + bSequence;
    return new IntArray(r);
  }

  private String debug() {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < alen; i++) {
      if (i > 0) b.append('\n');
      for (int j = 0; j < blen; j++) {
        b.append(String.format("% 4d", lens[p(i, j)]));
      }
    }
    return b.toString();
  }

  private int m(int ai, int bi) {
    assert ai >= -1 && ai < alen;
    assert bi >= -1 && bi < blen;
    if (ai < 0 || bi < 0) return 0;
    return lens[p(ai, bi)];
  }

  private int p(int ai, int bi) {
    assert ai >= 0 && ai < alen;
    assert bi >= 0 && bi < blen;
    return ai * blen + bi;
  }
}