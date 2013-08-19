package com.almworks.integers;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;

public class LongArrayMergePerformanceComparison extends TestCase{
  public enum MergeType { REPLACE, REALLOC, REPLACE_COUNT_ENSURE_CAPACITY }

  public static interface Merger {
    void invoke(LongArray first, LongArray second);
  }


  public static class MergeUsingSame implements Merger {
    public void invoke(LongArray first, LongArray second) {
      first.unionWithSameLengthList(second);
    }
  }

  public static class MergeUsingSmall implements Merger {
    public void invoke(LongArray first, LongArray second) {
      first.unionWithSmallArray(second);
    }
  }

  public static class MergeUsingHeuristic implements Merger {
    public void invoke(LongArray first, LongArray second) {
      first.unionWithArray(second);
    }
  }

  public long testBenchMerge(Merger merger, MergeType mType, int[] sizes, int testCount) {
    long allTime = 0, start = 0;
    for (int i = 0; i < testCount + 20; i++) {
      LongArray[] arrays = {null, null};
      for (int j = 0; j < 2; j++) {
        arrays[j] = new LongArray(sizes[j]);
        for (int k = 0; k < sizes[j]; k++) arrays[j].add(IntegersFixture.RAND.nextInt());
        arrays[j].sortUnique();
      }

      if (mType == MergeType.REPLACE_COUNT_ENSURE_CAPACITY) {
        start = System.currentTimeMillis();
      }
      if (mType != MergeType.REALLOC) {
        arrays[0].ensureCapacity(arrays[0].size() + arrays[1].size() + 10);
      }
      if (mType != MergeType.REPLACE_COUNT_ENSURE_CAPACITY) {
        start = System.currentTimeMillis();
      }
      merger.invoke(arrays[0], arrays[1]);
      if (i > 20) allTime += System.currentTimeMillis() - start;
    }
    return allTime;
  }

  public void testBenchMerge() throws IOException {
    Merger[] mergers = {new MergeUsingSame(), new MergeUsingSmall(), new MergeUsingHeuristic()};
    String[] mergersNames = {"Same", "Small", "Heuristic"};
    MergeType[] mergeTypes = {MergeType.REALLOC, MergeType.REPLACE, MergeType.REPLACE_COUNT_ENSURE_CAPACITY};
    String[] mergeTypesNames = {"realloc", "replace", "replace & count eCap"};


    int[] coefs = new IntArray(IntProgression.arithmetic(9, 15)).toNativeArray();
    int[] firstSizes = new IntArray(IntProgression.arithmetic(1000000, 10, 1)).toNativeArray();
    int testCount = 100;
    IntegersFixture.RAND.nextInt();
    System.out.print("|firstSize|coef");
    for (int j = 0; j < mergeTypesNames.length; j++) {
      for (int i = 0; i < mergersNames.length - 1; i++) {
        System.out.print("|" + mergeTypesNames[j] + ", " + mergersNames[i]);
      }
    }
    System.out.println();

    for (int firstSize : firstSizes) {
      for(int coef: coefs) {
        int[] sizes = {firstSize, firstSize / coef};
        System.out.print("|" + firstSize + "|" + coef);

        for (int j = 0; j < mergeTypes.length; j++) {
          for (int i = 0; i < mergers.length - 1; i++) {
            System.out.print("|" + testBenchMerge(mergers[i], mergeTypes[j], sizes, testCount));
//            System.out.println(mergersNames[i] + ", " +  mergeTypesNames[j] + ": " +
//                testBenchMerge(mergers[i], mergeTypes[j], sizes, testCount) + "\n ");
          }
        }
        System.out.println();
      }
    }
  }

  long stupidCopyTest(int[] src, int[] dest, int size) {
    long start = System.currentTimeMillis();
    for (int i = 0; i < size; i++) {
      dest[i] = src[i];
    }
    return System.currentTimeMillis() - start;
  }

  long stupidCopyTestWithIndexes(int[] src, int[] dest, int[] indexes, int size) {
    long start = System.currentTimeMillis();
    for (int i = 0; i < size; i++) {
      dest[i] = src[indexes[i]];
    }
    return System.currentTimeMillis() - start;
  }

  long stupidCopyTestWithIndexes0(int[] src, int[] dest, int[] indexes, int size) {
    long start = System.currentTimeMillis();
    for (int i = 0; i < size; i++) {
      System.arraycopy(src, indexes[i], dest, i, 1);
    }
    return System.currentTimeMillis() - start;
  }

  long arraycopyTest(int[] src, int[] dest, int size, int elementsCount, int[] indexes) {
    if (elementsCount == -1) {
      long start = System.currentTimeMillis();
      System.arraycopy(src, 0, dest, 0, size);
      return System.currentTimeMillis() - start;
    } else {
      int times = indexes.length;
      long start = System.currentTimeMillis();
      for (int i = 0; i < times; i++) {
        System.arraycopy(src, indexes[i], dest, i, elementsCount);
      }
      return System.currentTimeMillis() - start;
    }
  }

  public int[] getShuffledArray(int size) {
    int[] indexes = new int[size];
    for (int i = 0; i < size; i++) {
      indexes[i] = i;
    }
    for (int curSize = size; 0 < curSize; curSize--) {
      int ind = IntegersFixture.RAND.nextInt(curSize);
      IntCollections.swap(indexes, ind, curSize);
    }
    return indexes;
  }

  public void _testArrayCopy() {
    int size = 10000000;
    int[] indexes = getShuffledArray(size);
    System.out.println("e!");
//    System.out.println(Arrays.toString(indexes));
//    if (true) return;

    int[] src = new int[size], dest = new int[size];
    for (int i = 0; i < size; i++) src[i] = IntegersFixture.RAND.nextInt();

    // stupid copy
    for (int j = 0; j < 100; j++) {
      Arrays.fill(dest, -1);
      stupidCopyTest(src, dest, size);
    }
    long allTime = 0;
    for (int j = 0; j < 1000; j++) {
      Arrays.fill(dest, -1);
      allTime += stupidCopyTest(src, dest, size);
    }
    System.out.println("Stupid copy: " + allTime);

    // stupid copy with indexes
    for (int j = 0; j < 100; j++) {
      Arrays.fill(dest, -1);
      stupidCopyTestWithIndexes(src, dest, indexes, size);
    }
    allTime = 0;
    for (int j = 0; j < 1000; j++) {
      Arrays.fill(dest, -1);
      allTime += stupidCopyTestWithIndexes(src, dest, indexes, size);
    }
    System.out.println("Stupid copy with indexes: " + allTime);

    // stupid Systems.arraycopy with indexes
    for (int j = 0; j < 100; j++) {
      Arrays.fill(dest, -1);
      stupidCopyTestWithIndexes0(src, dest, indexes, size);
    }
    allTime = 0;
    for (int j = 0; j < 1000; j++) {
      Arrays.fill(dest, -1);
      allTime += stupidCopyTestWithIndexes0(src, dest, indexes, size);
    }
    System.out.println("Systems.arraycopy 1 elem, with indexes: " + allTime);

    // Systems.arraycopy
    int[] counties = {1, 2, 4, 5, 8, 10, 20, 50, 100, 200, 500, 1000, 10000, 100000, 200000, 500000, 1000000, -1};
    for (int count: counties) {
      indexes = count == -1 ? null : getShuffledArray(size/count);

      for (int j = 0; j < 100; j++) {
        Arrays.fill(dest, -1);
        arraycopyTest(src, dest, size, count, indexes);
      }
      allTime = 0;
      for (int j = 0; j < 1000; j++) {
        Arrays.fill(dest, -1);
        allTime += arraycopyTest(src, dest, size, count, indexes);
      }
      System.out.println("arraycopy full: [count=" + (count == -1 ? "all" : count) + "]: " + allTime);
    }
  }
}
