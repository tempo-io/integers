package com.almworks.integers;

import java.util.ArrayList;
import java.util.List;

public class LongUnionIteratorTests extends IntegersFixture {
  public void testCreate() {
    List<LongArray> list = new ArrayList<LongArray>();
    list.add(LongArray.create(1, 2, 5, 10));
    list.add(LongArray.create(-3, 2, 3, 4, 11));
    LongArray union = new LongArray(new LongUnionIterator(list));
    CHECK.order(union, -3, 1, 2, 3, 4, 5, 10, 11);
  }

  public void testAllCases() {
    new SetOperationsChecker().check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return new LongUnionIterator(arrays);
      }
    }, new SetOperationsChecker.UnionGetter(), false, SortedStatus.SORTED_UNIQUE);
  }

  public void testIteratorSpecification() {
    LongIteratorSpecificationChecker.checkIterator(new LongIteratorSpecificationChecker.IteratorGetter() {
      @Override
      public List<? extends LongIterator> get(final long... values) {
        if (!new LongArray(values).isUniqueSorted()) {
          throw new IllegalArgumentException();
        }
        List<LongIterator> res = new ArrayList<LongIterator>();
        AbstractLongList list = new LongArray(values);
        int length = values.length;
        res.add(new LongUnionIterator(new LongNativeArrayIterator(values)));
        res.add(new LongUnionIterator(new LongNativeArrayIterator(values), LongIterator.EMPTY));
        res.add(new LongUnionIterator(LongIterator.EMPTY, new LongNativeArrayIterator(values)));

        if (length > 1) {
          int idx = values.length / 2;
          res.add(new LongUnionIterator(
              new LongNativeArrayIterator(values, 0, idx),
              new LongNativeArrayIterator(values, idx, length)));

          res.add(new LongUnionIterator(
              new LongNativeArrayIterator(values, 0, idx),
              new LongNativeArrayIterator(values)));

          res.add(new LongUnionIterator(
              new LongIndexedIterator(list, IntIterators.range(0, length, 2)),
              new LongIndexedIterator(list, IntIterators.range(1, length, 2))));

          res.add((new LongUnionIterator(
              new LongNativeArrayIterator(values),
              new LongIndexedIterator(list, IntIterators.range(0, length, 2)))));
        }

        if (length > 2) {
          res.add(new LongUnionIterator(
              new LongIndexedIterator(list, IntIterators.range(0, length, 3)),
              new LongIndexedIterator(list, IntIterators.range(1, length, 3)),
              new LongIndexedIterator(list, IntIterators.range(2, length, 3))));
        }

        return res;
      }
    }, LongIteratorSpecificationChecker.ValuesType.SORTED_UNIQUE);
  }
}
