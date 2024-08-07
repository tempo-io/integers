package com.almworks.integers;

import java.util.Arrays;

@SuppressWarnings({"UnusedDeclaration"})
public class IntCollectionsCompare extends CollectionsCompare {
  private final CollectionsCompare Compare = new CollectionsCompare();

  public IntCollectionsCompare() {
  }

  public void order(IntIterator actual, int ... expected) {
    Compare.order(IntCollections.toNativeArray(actual), expected);
  }

  public void order(IntIterator actual, IntIterator expected) {
    Compare.order(IntCollections.toNativeArray(actual), IntCollections.toNativeArray(expected));
  }

  public void unordered(IntList actual, IntList expected) {
    unordered(actual.toNativeArray(), expected.toNativeArray());
  }

  public void unordered(LongList actual, LongList expected) {
    unordered(actual.toNativeArray(), expected.toNativeArray());
  }

  public void unordered(IntList actual, int ... expected) {
    unordered(actual.toNativeArray(), expected);
  }

  public void emptyIntersection(int[] array1, int[] array2) {
    array1 = IntCollections.arrayCopy(array1);
    array2 = IntCollections.arrayCopy(array2);
    Arrays.sort(array1);
    Arrays.sort(array2);
    for (int val : array1) {
      if (IntCollections.binarySearch(val, array2) >= 0) {
        Failure failure = createFailure();
        failure.actual().setIntArray(array1);
        failure.expected().setIntArray(array2);
        failure.fail("Equal elements");
      }
    }
  }

  public void unordered(int[] actual, int... expected) {
    int[] actualCopy = IntCollections.arrayCopy(actual);
    Arrays.sort(actualCopy);
    int[] expectedCopy = IntCollections.arrayCopy(expected);
    Arrays.sort(expectedCopy);
    order(actualCopy, expectedCopy);
  }

  public void unordered(long[] actual, long... expected) {
    long[] actualCopy = Arrays.copyOf(actual, actual.length);
    Arrays.sort(actualCopy);
    long[] expectedCopy = Arrays.copyOf(expected, expected.length);
    Arrays.sort(expectedCopy);
    order(actualCopy, expectedCopy);
  }
}
