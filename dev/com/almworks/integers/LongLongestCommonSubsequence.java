package com.almworks.integers;

import com.almworks.integers.wrappers.LongIntHppcOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.almworks.integers.LongCollections.toBoundedString;

/**
 * @author Igor Sereda
 *
 * Copied from IntLongestCommonSequence.
 */
public class LongLongestCommonSubsequence {
  private final LongList aseq;
  private final LongList bseq;
  private final int alen;
  private final int blen;
  private final int[] lens;
  private final int prefixSize;
  private final int suffixSize;

  private LongLongestCommonSubsequence(LongList aseq, LongList bseq, int prefixSize, int suffixSize) {
    this.aseq = aseq;
    this.bseq = bseq;
    alen = aseq.size();
    blen = bseq.size();
    lens = new int[alen * blen];
    this.prefixSize = prefixSize;
    this.suffixSize = suffixSize;
  }

  @Nullable
  private static LongList tryLcsForPermutation(LongList aseq, LongList bseq) {
    int sz = aseq.size();
    LongOpenHashSet from = LongOpenHashSet.createFrom(aseq);
    if (sz != bseq.size() ||
      sz != from.size() ||
      !from.containsAll(bseq)) {
      return null;
    }
    return getLcsForPermutation(aseq, bseq);
  }

  /**
   * contract: aseq shouldn't contain duplicates and bseq should be permutation of aseq.
   * @param aseq list without duplicates
   * @param bseq list without duplicates
   * @return longest common subsequence for {@code aseq} and {@code bseq}
   */
  public static LongList getLcsForPermutation(LongList aseq, LongList bseq) {
    assert aseq.size() == bseq.size() : toBoundedString(aseq) + toBoundedString(bseq);
    int sz = aseq.size();
    LongIntHppcOpenHashMap aseqMap = new LongIntHppcOpenHashMap(sz);
    for (int i = 0; i < sz; i++) {
      assert !aseqMap.containsKey(aseq.get(i)) : "duplicates aren't allowed in aseq: " + aseq.get(i); // check no duplicates in aseq
      aseqMap.put(aseq.get(i), i);
    }
    assert aseqMap.size() == bseq.size() && aseqMap.containsKeys(bseq)
      : "bseq should be permutation of aseq: " + toBoundedString(aseqMap.keySet()) + " " + toBoundedString(bseq);
    IntArray ids = new IntArray();
    for (LongIterator it : bseq) {
      ids.add(aseqMap.get(it.value()));
    }

    IntList subseq = IntLongestIncreasingSubsequence.getLIS(ids);
    LongArray res = new LongArray(subseq.size());
    for (IntIterator it : subseq) {
      res.add(aseq.get(it.value()));
    }
    return res;
  }

  public static LongList getLCS(LongList aseq, LongList bseq) {
    return getLCS(aseq, bseq, false);
  }

  public static LongList getLCS(LongList aseq, LongList bseq, boolean tryOptimize) {
    if (aseq == null || bseq == null || aseq.isEmpty() || bseq.isEmpty()) {
      return LongList.EMPTY;
    }

    // memory | time        | algo
    // O(n^2) | O(n^2)      | LCS
    // O(n)   | O(n*log(n)) | longest increasing subsequence algo applicable for permutations
    LongList lcs = tryOptimize ? tryLcsForPermutation(aseq, bseq) : null;
    if (lcs != null) {
      return lcs;
    }
    int maxsize = Math.min(aseq.size(), bseq.size());
    LongList prefix = getPrefix(aseq, bseq, maxsize);
    if (prefix.size() == maxsize) {
      return prefix;
    }
    LongList suffix = getSuffix(aseq, bseq, maxsize);
    if (suffix.size() == maxsize) {
      return suffix;
    }
    boolean hasPrefix = !prefix.isEmpty();
    boolean hasSuffix = !suffix.isEmpty();
    if (hasPrefix) {
      aseq = aseq.subList(prefix.size(), aseq.size());
      bseq = bseq.subList(prefix.size(), bseq.size());
    }
    if (hasSuffix) {
      aseq = aseq.subList(0, aseq.size() - suffix.size());
      bseq = bseq.subList(0, bseq.size() - suffix.size());
    }
    long[] r = new LongLongestCommonSubsequence(aseq, bseq, prefix.size(), suffix.size()).calcLCS();
    if (hasPrefix) {
      prefix.toNativeArray(0, r, 0, prefix.size());
    }
    if (hasSuffix) {
      suffix.toNativeArray(0, r, r.length - suffix.size(), suffix.size());
    }
    return r.length == 0 ? LongList.EMPTY : new LongArray(r);
  }

  private static LongList getSuffix(LongList aseq, LongList bseq, int maxsize) {
    int i = 0;
    int ai = aseq.size(), bi = bseq.size();
    while (i < maxsize && aseq.get(--ai) == bseq.get(--bi)) i++;
    return i == 0 ? LongList.EMPTY : aseq.subList(aseq.size() - i, aseq.size());
  }

  @NotNull
  private static LongList getPrefix(@NotNull LongList aseq, @NotNull LongList bseq, int maxsize) {
    int i = 0;
    while (i < maxsize && aseq.get(i) == bseq.get(i)) i++;
    return i == 0 ? LongList.EMPTY : aseq.subList(0, i);
  }

  private long[] calcLCS() {
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
    long[] r = new long[prefixSize + lcslen + suffixSize];
    if (lcslen == 0) return r;
    int ri = lcslen - 1;
    int i = alen - 1, j = blen - 1;
    while (i >= 0 && j >= 0 && ri >= 0) {
      long v = aseq.get(i);
      if (v == bseq.get(j)) {
        r[prefixSize + (ri--)] = v;
        i--;
        j--;
      } else if (m(i, j - 1) > m(i - 1, j)) {
        j--;
      } else {
        i--;
      }
    }
    assert ri == -1 : ri + " " + i + " " + j + " " + aseq + " " + bseq;

    // this is a wrong assumption because we finish the cycle when resulting array is full, which happens
    // earlier than we reach back to [0] index if the first elements are not in the LCS.
//    assert i == -1 || j == -1 : i + " " + j + " " + aseq + " " + bseq;
    return r;
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
