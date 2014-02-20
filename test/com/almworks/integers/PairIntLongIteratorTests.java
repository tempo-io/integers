package com.almworks.integers;

import java.util.Arrays;
import java.util.List;

import static com.almworks.integers.IntLongIterators.leftProjection;
import static com.almworks.integers.IntLongIterators.rightProjection;
import static com.almworks.integers.IntegersFixture.SortedStatus.UNORDERED;

public class PairIntLongIteratorTests extends IntegersFixture {
  public void testRightIteratorSpecification() {
    LongIteratorSpecificationChecker.checkIterator(new LongIteratorSpecificationChecker.IteratorGetter<LongIterator>() {
      private LongIterator getFromProjection(IntIterable leftIterable, LongIterable rightIterable) {
        return rightProjection(new PairIntLongIterator(leftIterable, rightIterable));
      }

      @Override
      public List<LongIterator> get(long... values) {
        int length = values.length;
        LongArray right = new LongArray(values);
        return Arrays.asList(
            getFromProjection(generateRandomIntArray(values.length, UNORDERED), right),
            getFromProjection(IntCollections.repeat(0, length), right),
            getFromProjection(IntCollections.repeat(0, length + 10), right),
            getFromProjection(IntCollections.repeat(Integer.MAX_VALUE, length), right)
        );
      }
    });
  }

  public void testSimple() {
    int[][] leftVariants = {{}, {0, 1, 2}, {10, 20, 30}, {0, 2, 4, 6}, {Integer.MIN_VALUE, Integer.MAX_VALUE, 1, 2, 0}};
    for (int[] left : leftVariants) {
      for (int[] right0 : leftVariants) {
        IntList leftList = new IntArray(left);
        LongList rightList = asLongs(new IntArray(right0));
        IntList leftSingle = new IntList.Single(Integer.MIN_VALUE);
        LongList rightSingle = new LongList.Single(Long.MIN_VALUE);

        checkPairIterator(left, right0, leftList, rightList);
        if (leftList.size() == rightList.size()) {
          checkPairIterator(left, right0, IntCollections.concatLists(leftList, leftSingle), rightList);
          checkPairIterator(left, right0, leftList, LongCollections.concatLists(rightList, rightSingle));
        }
      }
    }
  }

  private void checkPairIterator(int[] left, int[] right, IntList leftList, LongList rightList) {
    PairIntLongIterator pit = new PairIntLongIterator(leftList, rightList);
    assertFalse(pit.hasValue());
    int len = Math.min(left.length, right.length);

    assertEquals(Arrays.toString(left) + " " + Arrays.toString(right), pit.hasNext(), len != 0);
    for (int i = 0; i < len; i++) {
      assertTrue(pit.hasNext());
      pit.next();
      assertEquals(left[i], pit.left());
      assertEquals(right[i], pit.right());
    }
    assertFalse(pit.hasNext());

    int[] leftExpected = new int[len];
    System.arraycopy(left, 0, leftExpected, 0, len);

    long[] rightExpected = new long[len];
    for (int i = 0; i < len; i++) rightExpected[i] = right[i];

    CHECK.order(leftProjection(new PairIntLongIterator(leftList, rightList)), leftExpected);
    CHECK.order(rightProjection(new PairIntLongIterator(leftList, rightList)), rightExpected);
  }
}