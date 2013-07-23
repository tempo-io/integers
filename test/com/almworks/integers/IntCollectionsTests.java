package com.almworks.integers;

import com.almworks.integers.optimized.SameValuesIntList;
import com.almworks.integers.optimized.SegmentedIntArray;
import com.almworks.integers.util.*;

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.LongArray.create;


public class IntCollectionsTests extends IntegersFixture {
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
    for (int i = 0; i < list.size(); i = list.getNextDifferentValueIndex(i)) {
      assertTrue("exceeded the list size!", i < list.size());
      resultingIndices.add(i);
    }
    return resultingIndices.iterator();
  }

  public void testUnion() {
    IntArray arr = IntArray.create(0, 2, 4, 6, 8);
    IntArray add = IntArray.create(1, 3, 5, 7, 9);
    IntArray expected = new IntArray(IntProgression.arithmetic(0, 10));
    IntArray union = IntCollections.unionWithSmallArray(arr.toNativeArray(), 5, add.toNativeArray(), 5);
    CHECK.order(union.iterator(), expected.iterator());
    union = IntCollections.unionWithSameLengthList(arr.toNativeArray(), 5, add);
    CHECK.order(union.iterator(), expected.iterator());

    union.ensureCapacity(16);
    System.out.println(union.getCapacity());
    int unionSize = union.size();
    union = IntCollections.unionWithSmallArray(union.toNativeArray(), unionSize, new int[]{5, 10, 15}, 3);
    expected.addAll(10, 15);
    CHECK.order(union.iterator(), expected.iterator());
    union = IntCollections.unionWithSameLengthList(union.toNativeArray(), unionSize, IntArray.create(5, 10, 15));
    CHECK.order(union.iterator(), expected.iterator());

    union = IntCollections.unionWithSmallArray(null, 0, new int[]{1, 2, 3}, 3);
    CHECK.order(union.iterator(), 1, 2, 3);
    union = IntCollections.unionWithSameLengthList(null, 0, IntArray.create(1, 2, 3));
    CHECK.order(union.iterator(), 1, 2, 3);
  }
}