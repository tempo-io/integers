package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import com.almworks.integers.SetOperationsChecker;
import junit.framework.TestCase;

import static com.almworks.integers.IntegersFixture.SortedStatus.*;

public class LongMinusIteratorTests extends TestCase {
  private LongArray create(long... values) {
    return new LongArray(values);
  }

  public void testMinusNotUnique() {
    testMinus(create(1, 1, 3, 3, 5, 5), create(1, 3, 5), create());
    testMinus(create(1, 1, 3, 3, 5, 5), create(1, 5), create(3, 3));
    testMinus(create(1, 1, 3, 3, 5, 5), create(1, 5, 5), create(3, 3));
    testMinus(create(1, 3, 5), create(1, 1, 5, 5), create(3));
    testMinus(create(1, 3, 5, 7, 9), create(1, 5, 9), create(3, 7));
  }

  private void testMinus(LongArray include, LongArray exclude, LongArray difference) {
    IntegersFixture.assertContents(new LongMinusIterator(include.iterator(), exclude.iterator()), difference);
  }

  public void testAllCases() {
    new SetOperationsChecker().check(new SetOperationsChecker.SetCreator() {
      @Override
      public LongIterator get(LongArray... arrays) {
        return LongMinusIterator.create(arrays[0], arrays[1]);
      }
    }, new SetOperationsChecker.MinusGetter(), true, SORTED_UNIQUE, SORTED);
  }

}
