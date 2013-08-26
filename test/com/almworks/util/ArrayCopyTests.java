package com.almworks.util;

import com.almworks.integers.IntArray;
import com.almworks.integers.IntegersFixture;
import junit.framework.TestCase;

import java.util.Arrays;

public class ArrayCopyTests extends TestCase {
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

  public IntArray getShuffledIndexesArray(int size) {
    IntArray indexes = new IntArray(size);
    for (int i = 0; i < size; i++) {
      indexes.add(i);
    }
    indexes.shuffle(IntegersFixture.RAND);
    return indexes;
  }

  public void testArrayCopy() {
    int size = 10000000;
    int[] indexes = getShuffledIndexesArray(size).extractHostArray();
    System.out.println("Indexes was obtained");

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
      indexes = count == -1 ? null : getShuffledIndexesArray(size / count).extractHostArray();

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
