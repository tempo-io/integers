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
    int minSize = 16, maxSize = 266;
    float[] loadFactors = {0.1f, 0.3f, 0.5f, 0.75f, 1.0f};
    for (float loadFactor : loadFactors) {
      for (int size = minSize; size <= maxSize; size++) {
        set = LongOpenHashSet.createForAdd(size, loadFactor);
        int curThreshold = set.getThreshold();
        assertTrue(set.getThreshold() + "<" + size + "; load factor = " + loadFactor, set.getThreshold() >= size);
        LongList expected = range(size);
        set.addAll(expected);
        checkSet(set, expected);
        assertEquals(curThreshold, set.getThreshold());
      }
    }
  }

  public void testRemoveAndResizeSimple() {
    set = new LongOpenHashSet(16);
    assertEquals(12, set.getThreshold());
    set.addAll(range(11));
    set.remove(5);
    set.add(11);
    set.add(12);
    // This one leads to resize
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

