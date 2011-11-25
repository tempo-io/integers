package com.almworks.integers;

import com.almworks.integers.optimized.SameValuesIntList;
import com.almworks.integers.optimized.SegmentedIntArray;
import com.almworks.integers.util.IntListConcatenation;
import com.almworks.integers.util.IntListInsertingDecorator;
import com.almworks.integers.util.ModifyingIntListRemovingDecorator;
import com.almworks.integers.util.ReadonlyIntListRemovingDecorator;


public class IntCollectionsTests extends NativeIntFixture {
  private final IntArray myArray = new IntArray();

  public void testArray_RemoveSubsequentDuplicates() {
    myArray.addAll(1, 1, 2);
    myArray.sortUnique();
    checkCollection(myArray, 1, 2);
    myArray.clear();

    myArray.addAll(1, 2, 2, 3);
    myArray.sortUnique();
    checkCollection(myArray, 1, 2, 3);
    myArray.clear();

    myArray.addAll(1, 2, 3, 3);
    myArray.sortUnique();
    checkCollection(myArray, 1, 2, 3);
    myArray.clear();

    myArray.addAll(1, 1, 2, 2, 3, 3);
    myArray.sortUnique();
    checkCollection(myArray, 1, 2, 3);
  }

  public void testInsertDecorator() {
    IntListInsertingDecorator ins = new IntListInsertingDecorator(myArray);
    checkCollection(ins);
    assertEquals(0, ins.getInsertCount());

    ins.insert(0, 1);
    checkCollection(ins, 1);
    assertEquals(1, ins.getInsertCount());
    checkInsertIndexes(ins, 0);
    CHECK.order(ins.insertValueIterator(), 1);

    myArray.insert(0, 3);
    checkCollection(ins, 1, 3);
    myArray.insert(1, 4);
    checkCollection(ins, 1, 3, 4);
    assertEquals(1, ins.getInsertCount());
    checkInsertIndexes(ins, 0);
    CHECK.order(ins.insertValueIterator(), 1);

    ins.insert(1, 2);
    checkCollection(ins, 1, 2, 3, 4);
    assertEquals(2, ins.getInsertCount());
    checkInsertIndexes(ins, 0, 1);
    CHECK.order(ins.insertValueIterator(), 1, 2);

    ins.insert(0, 0);
    checkCollection(ins, 0, 1, 2, 3, 4);
    assertEquals(3, ins.getInsertCount());
    checkInsertIndexes(ins, 0, 1, 2);
    CHECK.order(ins.insertValueIterator(), 0, 1, 2);

    ins.insert(5, 5);
    checkCollection(ins, 0, 1, 2, 3, 4, 5);
    assertEquals(4, ins.getInsertCount());
    checkInsertIndexes(ins, 0, 1, 2, 5);
    CHECK.order(ins.insertValueIterator(), 0, 1, 2, 5);
  }

  public void testConcatenation() {
    IntListConcatenation concat = new IntListConcatenation();
    assertEquals(0, concat.getSliceCount());
    checkCollection(concat);
    concat.addSlice(IntList.EMPTY);
    assertEquals(1, concat.getSliceCount());
    checkCollection(concat);
    concat.addSlice(myArray);
    checkCollection(concat);
    myArray.add(1);
    checkCollection(concat, 1);
    concat.addSlice(IntList.EMPTY);
    checkCollection(concat, 1);
    SegmentedIntArray segarray = new SegmentedIntArray();
    concat.addSlice(segarray);
    concat.addSlice(IntList.EMPTY);
    checkCollection(concat, 1);
    segarray.add(3);
    checkCollection(concat, 1, 3);
    myArray.add(2);
    checkCollection(concat, 1, 2, 3);
  }

  public void testRemoveDecorator() {
    ModifyingIntListRemovingDecorator rem = new ModifyingIntListRemovingDecorator(myArray);
    checkCollection(rem);
    checkRemovedIndexes(rem);
    CHECK.order(rem.removedValueIterator());
    assertEquals(0, rem.getRemoveCount());

    myArray.add(-1);
    checkCollection(rem, -1);
    checkRemovedIndexes(rem);
    CHECK.order(rem.removedValueIterator());
    assertEquals(0, rem.getRemoveCount());

    rem.removeAt(0);
    checkCollection(rem);
    CHECK.order(rem.removedValueIterator(), -1);
    checkRemovedIndexes(rem, 0);
    assertEquals(1, rem.getRemoveCount());

    myArray.addAll(0, 1, 2, 3, 4, 5, 6);
    checkRemovedIndexes(rem, 0);
    rem.removeAt(6);
    checkCollection(rem, 0, 1, 2, 3, 4, 5);
    CHECK.order(rem.removedValueIterator(), -1, 6);
    checkRemovedIndexes(rem, 0, 7);
    assertEquals(2, rem.getRemoveCount());

    rem.removeAt(0);
    checkCollection(rem, 1, 2, 3, 4, 5);
    CHECK.order(rem.removedValueIterator(), -1, 0, 6);
    checkRemovedIndexes(rem, 0, 1, 7);
    assertEquals(3, rem.getRemoveCount());

    rem.removeAt(2);
    checkCollection(rem, 1, 2, 4, 5);
    CHECK.order(rem.removedValueIterator(), -1, 0, 3, 6);
    checkRemovedIndexes(rem, 0, 1, 4, 7);
    assertEquals(4, rem.getRemoveCount());

    rem.removeAt(0);
    checkCollection(rem, 2, 4, 5);
    CHECK.order(rem.removedValueIterator(), -1, 0, 1, 3, 6);
    checkRemovedIndexes(rem, 0, 1, 2, 4, 7);
    assertEquals(5, rem.getRemoveCount());
  }

  public void testCreateModifyingRemoveDecorator() {
    myArray.addAll(0, 1, 2, 3, 4, 5, 6);
    ModifyingIntListRemovingDecorator rem = ModifyingIntListRemovingDecorator.createFromUnsorted(myArray, 0, 4, 1, 5);
    checkCollection(rem, 2, 3, 6);
    checkRemovedIndexes(rem, 0, 1, 4, 5);
    assertEquals(4, rem.getRemoveCount());

    rem = ModifyingIntListRemovingDecorator.createFromUnsorted(myArray, 2, 6, 1);
    checkCollection(rem, 0, 3, 4, 5);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());

    rem = ModifyingIntListRemovingDecorator.createFromUnsorted(myArray, 2, 6, 1, 2);
    checkCollection(rem, 0, 3, 4, 5);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());
  }

  public void testCreateReadonlyRemoveDecorator() {
    myArray.addAll(0, 1, 2, 3, 4, 5, 6);
    IntList array2 = IntArray.create(10, 11, 12, 13, 14, 15, 16);

    IntList removeIndices = ReadonlyIntListRemovingDecorator.prepareUnsortedIndices(0, 4, 1, 5);
    ReadonlyIntListRemovingDecorator rem = ReadonlyIntListRemovingDecorator.createFromPrepared(myArray, removeIndices);
    checkCollection(rem, 2, 3, 6);
    checkRemovedIndexes(rem, 0, 1, 4, 5);
    assertEquals(4, rem.getRemoveCount());
    rem = ReadonlyIntListRemovingDecorator.createFromPrepared(array2, removeIndices);
    checkCollection(rem, 12, 13, 16);
    checkRemovedIndexes(rem, 0, 1, 4, 5);
    assertEquals(4, rem.getRemoveCount());

    removeIndices = ReadonlyIntListRemovingDecorator.prepareUnsortedIndices(2, 6, 1);
    rem = ReadonlyIntListRemovingDecorator.createFromPrepared(myArray,removeIndices);
    checkCollection(rem, 0, 3, 4, 5);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());
    rem = ReadonlyIntListRemovingDecorator.createFromPrepared(array2,removeIndices);
    checkCollection(rem, 10, 13, 14, 15);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());

    removeIndices = ReadonlyIntListRemovingDecorator.prepareUnsortedIndices(2, 6, 1, 2);
    rem = ReadonlyIntListRemovingDecorator.createFromPrepared(myArray, removeIndices);
    checkCollection(rem, 0, 3, 4, 5);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());
    rem = ReadonlyIntListRemovingDecorator.createFromPrepared(array2, removeIndices);
    checkCollection(rem, 10, 13, 14, 15);
    checkRemovedIndexes(rem, 1, 2, 6);
    assertEquals(3, rem.getRemoveCount());
  }

  public void testGetNextDiffValueIndex() {
    IntList testList1 = IntArray.create(1, 1, 2, 2, 2, 2, 3, 2);
    IntList expected = IntArray.create(0, 2, 6, 7);
    CHECK.order(getNextDifferentValueIndex(testList1), expected.iterator());

    SameValuesIntList testList2 = new SameValuesIntList();
    testList2.addAll(testList1);
    CHECK.order(getNextDifferentValueIndex(testList2), expected.iterator());

    IntList testList3 = IntArray.create(0, 0, 1, 1, 1, 1, 10, 1);
    CHECK.order(getNextDifferentValueIndex(testList3), expected.iterator());

    SameValuesIntList testList4 = new SameValuesIntList();
    testList4.addAll(testList3);
    CHECK.order(getNextDifferentValueIndex(testList4), expected.iterator());

  }

  private IntIterator getNextDifferentValueIndex(IntList list) {
    WritableIntList resultingIndices = new IntArray();
    for (int i = 0; i != -1; i = list.getNextDifferentValueIndex(i)) {
      assertTrue("exceeded the list size!", i < list.size());
      resultingIndices.add(i);
    }
    return resultingIndices.iterator();
  }

  public void testRemoveAllAtSorted() {
    IntArray a = new IntArray(IntProgression.arithmetic(0, 20));
    IntCollections.removeAllAtSorted(a, IntArray.create(0, 3, 4, 7, 10, 11, 12, 13, 19));
    CHECK.order(a.iterator(), 1, 2, 5, 6, 8, 9, 14, 15, 16, 17, 18);
    IntCollections.removeAllAtSorted(a, IntArray.create(1, 2, 3, 4, 9));
    CHECK.order(a.iterator(), 1, 9, 14, 15, 16, 18);
  }
}
