package com.almworks.integers;

public class LongUnionIteratorTwoTests extends IntegersFixture {
  public void testSimple() {
    LongArray a = LongArray.create(1, 2, 5, 10),
        b = LongArray.create(-3, 2, 3, 4, 11),
    expected = LongArray.copy(a);
    expected.addAll(b);
    expected.sortUnique();
    LongArray union = new LongArray(new LongUnionIteratorOfTwo(a, b));
    CHECK.order(union, expected);
  }

  public void testAllCases() {
    new SetOperationsChecker().check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return new LongUnionIteratorOfTwo(arrays[0], arrays[1]);
      }
    }, new SetOperationsChecker.UnionGetter(), true, SortedStatus.SORTED_UNIQUE);
  }
}