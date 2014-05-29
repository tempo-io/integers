package com.almworks.integers;

import java.util.Arrays;
import java.util.List;

public class LongIntIteratorsTests extends IntegersFixture {
  public void testLeftProjection() {
    LongIteratorSpecificationChecker.checkIterator(myRand, new LongIteratorSpecificationChecker.IteratorGetter<LongIterator>() {
      @Override
      public List<LongIterator> get(long... values) {
        LongIntIterator it = new LongIntPairIterator(new LongArray(values), IntIterators.repeat(-1));
        return Arrays.asList(LongIntIterators.leftProjection(it));
      }
    });
  }
}
