package com.almworks.integers;

/**
 * Basic LCS algorithm. 
 * @author Igor Sereda
 */
public class LongLongestCommonSubsequence {
  private final LongList aseq;
  private final LongList bseq;
  private final int alen;
  private final int blen;
  private final int[] lens;

  private LongLongestCommonSubsequence(LongList aseq, LongList bseq) {
    this.aseq = aseq;
    this.bseq = bseq;
    alen = aseq.size();
    blen = bseq.size();
    lens = new int[alen * blen];
  }

  public static LongList getLCS(LongList aseq, LongList bseq) {
    if (aseq == null || bseq == null || aseq.isEmpty() || bseq.isEmpty()) return LongList.EMPTY;
    return new LongLongestCommonSubsequence(aseq, bseq).calc();
  }

  private LongList calc() {
    for (int i = 0; i < alen; i++) {
      for (int j = 0; j < blen; j++) {
        if (aseq.get(i) == bseq.get(j)) {
          lens[p(i, j)] = m(i - 1, j - 1) + 1;
        } else {
          lens[p(i, j)] = Math.max(m(i - 1, j), m(i, j - 1));
        }
      }
    }
    int lcslen = m(alen - 1, blen - 1);
    if (lcslen == 0) return LongList.EMPTY;
    long[] r = new long[lcslen];
    int ri = lcslen - 1;
    int i = alen - 1, j = blen - 1;
    while (i >= 0 && j >= 0 && ri >= 0) {
      long v = aseq.get(i);
      if (v == bseq.get(j)) {
        r[ri--] = v;
        i--;
        j--;
      } else if (m(i, j - 1) > m(i - 1, j)) {
        j--;
      } else {
        i--;
      }
    }
    assert ri == -1 : ri + " " + i + " " + j + " " + aseq + " " + bseq;
    assert i == -1 || j == -1 : i + " " + j + " " + aseq + " " + bseq;
    return new LongArray(r);
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