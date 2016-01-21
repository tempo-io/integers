package com.almworks.integers;

import com.almworks.integers.wrappers.#E#IntHppcOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.almworks.integers.#E#Collections.toBoundedString;

/**
 * @author Igor Sereda
 *
 * Copied from IntLongestCommonSequence.
 */
public class #E#LongestCommonSubsequence {
  private final #E#List aseq;
  private final #E#List bseq;
  private final int alen;
  private final int blen;
  private final int[] lens;
  private final int prefixSize;
  private final int suffixSize;

  private #E#LongestCommonSubsequence(#E#List aseq, #E#List bseq, int prefixSize, int suffixSize) {
    this.aseq = aseq;
    this.bseq = bseq;
    alen = aseq.size();
    blen = bseq.size();
    lens = new int[alen * blen];
    this.prefixSize = prefixSize;
    this.suffixSize = suffixSize;
  }

  @Nullable
  private static #E#List tryLcsForPermutation(#E#List aseq, #E#List bseq) {
    int sz = aseq.size();
    #E#OpenHashSet from = #E#OpenHashSet.createFrom(aseq);
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
  public static #E#List getLcsForPermutation(#E#List aseq, #E#List bseq) {
    assert aseq.size() == bseq.size() : toBoundedString(aseq) + toBoundedString(bseq);
    int sz = aseq.size();
    #E#IntHppcOpenHashMap aseqMap = new #E#IntHppcOpenHashMap(sz);
    for (int i = 0; i < sz; i++) {
      assert !aseqMap.containsKey(aseq.get(i)) : "duplicates aren't allowed in aseq: " + aseq.get(i); // check no duplicates in aseq
      aseqMap.put(aseq.get(i), i);
    }
    assert aseqMap.size() == bseq.size() && aseqMap.containsKeys(bseq)
      : "bseq should be permutation of aseq: " + toBoundedString(aseqMap.keySet()) + " " + toBoundedString(bseq);
    IntArray ids = new IntArray();
    for (#E#Iterator it : bseq) {
      ids.add(aseqMap.get(it.value()));
    }

    IntList subseq = IntLongestIncreasingSubsequence.getLIS(ids);
    #E#Array res = new #E#Array(subseq.size());
    for (IntIterator it : subseq) {
      res.add(aseq.get(it.value()));
    }
    return res;
  }

  public static #E#List getLCS(#E#List aseq, #E#List bseq) {
    return getLCS(aseq, bseq, false);
  }

  public static #E#List getLCS(#E#List aseq, #E#List bseq, boolean tryOptimize) {
    if (aseq == null || bseq == null || aseq.isEmpty() || bseq.isEmpty()) {
      return #E#List.EMPTY;
    }

    // memory | time        | algo
    // O(n^2) | O(n^2)      | LCS
    // O(n)   | O(n*log(n)) | longest increasing subsequence algo applicable for permutations
    #E#List lcs = tryOptimize ? tryLcsForPermutation(aseq, bseq) : null;
    if (lcs != null) {
      return lcs;
    }
    int maxsize = Math.min(aseq.size(), bseq.size());
    #E#List prefix = getPrefix(aseq, bseq, maxsize);
    if (prefix.size() == maxsize) {
      return prefix;
    }
    #E#List suffix = getSuffix(aseq, bseq, maxsize);
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
    #e#[] r = new #E#LongestCommonSubsequence(aseq, bseq, prefix.size(), suffix.size()).calcLCS();
    if (hasPrefix) {
      prefix.toNativeArray(0, r, 0, prefix.size());
    }
    if (hasSuffix) {
      suffix.toNativeArray(0, r, r.length - suffix.size(), suffix.size());
    }
    return r.length == 0 ? #E#List.EMPTY : new #E#Array(r);
  }

  private static #E#List getSuffix(#E#List aseq, #E#List bseq, int maxsize) {
    int i = 0;
    int ai = aseq.size(), bi = bseq.size();
    while (i < maxsize && aseq.get(--ai) == bseq.get(--bi)) i++;
    return i == 0 ? #E#List.EMPTY : aseq.subList(aseq.size() - i, aseq.size());
  }

  @NotNull
  private static #E#List getPrefix(@NotNull #E#List aseq, @NotNull #E#List bseq, int maxsize) {
    int i = 0;
    while (i < maxsize && aseq.get(i) == bseq.get(i)) i++;
    return i == 0 ? #E#List.EMPTY : aseq.subList(0, i);
  }

  private #e#[] calcLCS() {
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
    #e#[] r = new #e#[prefixSize + lcslen + suffixSize];
    if (lcslen == 0) return r;
    int ri = lcslen - 1;
    int i = alen - 1, j = blen - 1;
    while (i >= 0 && j >= 0 && ri >= 0) {
      #e# v = aseq.get(i);
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