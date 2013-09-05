package com.almworks.integers;

import com.almworks.integers.optimized.SameValuesLongList;
import com.almworks.integers.optimized.SegmentedLongArray;
import com.almworks.integers.util.LongListInsertingDecorator;
import com.almworks.integers.util.ReadonlyLongListRemovingDecorator;

public class LongIteratorTests extends IntegersFixture {
  public void testSimple() {
    assertContents(
        LongArray.create(1, 3, 5).iterator(),
        LongArray.create(1, 3, 5));
  }

  public void testCursor() {
    LongProgression.Arithmetic rO = new LongProgression.Arithmetic(1,5,1);
    for (LongIterator it : rO) {
      long b = it.value();
    }

    WritableLongList result, expected, source;

    source = new SameValuesLongList();
    source.addAll(1, 1, 1, 2, 2, 3, 3, 3, 3);

    expected = new SameValuesLongList();
    expected.addAll(1, 1, 1, 2, 2, 3, 3, 3, 3);
    result = new SameValuesLongList();
    for (WritableLongListIterator it : source.write()) {
      result.add(it.value());
      it.set(0, 3);
    }
    assertEquals(expected, result);

    expected = new SameValuesLongList();
    expected.addAll(1, 2, 3, 5);
    result = new SegmentedLongArray();
    result.addAll(1, 2, 3, 4, 5);
    for (WritableLongListIterator it : result.write()) {
      if (it.value() == 4) {
        it.remove();
      }
    }
    assertEquals(expected, result);

    source.clear();
    source.addAll(8,9);
    expected.clear();
    expected.addAll(8,9,10,13,15);
    result.clear();
    LongListInsertingDecorator tst = new LongListInsertingDecorator(source);
    tst.insert(2,10);
    tst.insert(3,13);
    tst.insert(4,15);
    for (LongIterator i : tst) {
      result.add(i.value());
    }
    assertEquals(expected, result);

    source.clear();
    source.addAll(10,13,15,14,11,12,16,17,18);
    expected.clear();
    expected.addAll(10,15,14,12,16,18);
    result.clear();
    ReadonlyLongListRemovingDecorator tst2 =
        ReadonlyLongListRemovingDecorator.createFromPrepared(source, new IntArray(new int[]{1,3,5}));
    for (LongIterator i : tst2) {
      result.add(i.value());
    }
    assertEquals(expected, result);
  }
}