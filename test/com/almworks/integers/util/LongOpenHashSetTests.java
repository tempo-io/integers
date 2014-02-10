package com.almworks.integers.util;

import com.almworks.integers.*;

import java.util.ArrayList;
import java.util.List;

import static com.almworks.integers.IntegersFixture.SortedStatus.SORTED_UNIQUE;
import static com.almworks.integers.LongProgression.range;

public class LongOpenHashSetTests extends WritableLongSetChecker<LongOpenHashSet> {

  @Override
  protected LongOpenHashSet createSet() {
    return new LongOpenHashSet();
  }

  @Override
  protected LongOpenHashSet createSetWithCapacity(int capacity) {
    return LongOpenHashSet.createForAdd(capacity);
  }

  @Override
  protected List<LongOpenHashSet> createSetFromSortedUniqueList(LongList sortedUniqueList) {
    List<LongOpenHashSet> sets = new ArrayList<LongOpenHashSet>();
    sets.add(LongOpenHashSet.createFrom(sortedUniqueList));

    set = LongOpenHashSet.createForAdd(sortedUniqueList.size(), 0.5f);
    set.addAll(sortedUniqueList);
    sets.add(set);
    return sets;
  }

  @Override
  protected LongOpenHashSet create1SetFromSortedUniqueList(LongList sortedUniqueList) {
    return LongOpenHashSet.createFrom(sortedUniqueList);
  }

  public void testCreateForAdd() {
    int maxSize = 128;
    for (int size = 0; size <= maxSize; size++) {
      assertTrue(LongOpenHashSet.createForAdd(size).getThreshold() >= size);
    }
  }

  public void testRemoveAndResizeSimple() {
    set = new LongOpenHashSet(16);
    assertEquals(12, set.getThreshold());
    set.addAll(range(11));
    set.remove(5);
    set.add(11);
    // This one leads to resize
    set.add(12);
    // This one will be written in place of the removed element
    set.add(13);
    LongArray expected = new LongArray(range(14));
    expected.removeSorted(5);
    checkSet(set, expected);
  }

  public void testRemoveAndResize() {
    int[] capacities = {16, 32, 64, 128, 256};
    int maxVal = 100000;
    int attempts = 20;
    for (int capacity : capacities) {
      for (int attempt = 0; attempt < attempts; attempt++) {
        set = new LongOpenHashSet(capacity);
        int threshold = set.getThreshold();
        LongArray values = generateRandomLongArray(capacity * 2, SORTED_UNIQUE, maxVal);
        int th1 = threshold / 2;
        set.addAll(values.subList(0, threshold));
        assertEquals(threshold, set.getThreshold());
        assertEquals(threshold, set.size());
        checkSet(set, values.subList(0, threshold));

        set.removeAll(values.subList(0, th1));
        checkSet(set, values.subList(th1, threshold));

        int th2 = threshold * 2;
        set.addAll(values.subList(threshold, th2));
        checkSet(set, values.subList(th1, th2));
      }
    }
  }
}

