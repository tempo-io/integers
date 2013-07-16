package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.IntegersUtils;

import java.util.Random;

public class IntSetBuilderTests extends IntegersFixture {
  private static final int TEMP_SIZE = 3;

  public void test() {
    check(IntegersUtils.EMPTY_INTS);
    check(range(0, 0));
    check(range(0, 0), range(0, 0), range(0, 0));
    check(range(0, 10));
    check(range(0, 10), range(10, 0));
    check(range(0, 100), range(100, 0));
    check(range(0, 100), range(100, 0), range(50, 150), range(10, -200), range(0, 0));
  }

  protected void check(int[] ... v) {
    IntSetBuilder builder = new IntSetBuilder();
    for (int[] ints : v) {
      for (int value : ints) {
        builder.add(value);
      }
    }
    checkSet(builder, v);
  }


  public void testRandom() {
    Random r = new Random();
    for (int i = 0; i < 20; i++) { // replace 100 with 20 to make test run faster on build agent
      int size = r.nextInt(16000) + 10;
      int factor = r.nextInt(10) + 1;
      int count = size * factor / 2;
      IntArray set = new IntArray();
      IntSetBuilder builder = new IntSetBuilder();
      for (int j = 0; j < count; j++) {
        int v = r.nextInt(size);
        set.add(v);
        builder.add(v);
      }
      assertEquals(count, set.size());
      set.sortUnique();
      CHECK.order(builder.toSortedCollection().iterator(), set.toNativeArray());
    }
  }

  public void testMerge() {
    IntSetBuilder b;

    b = new IntSetBuilder(5);
    b.mergeFrom(new IntSetBuilder());
    checkSet(b, IntegersUtils.EMPTY_INTS);

    b = new IntSetBuilder(5);
    b.mergeFrom(prog(0, 1, 10));
    checkSet(b, range(0, 9));

    b = new IntSetBuilder(5);
    b.mergeFrom(prog(0, 1, 10));
    b.mergeFrom(prog(0, 1, 10));
    checkSet(b, range(0, 9));

    b = new IntSetBuilder(5);
    b.mergeFrom(prog(0, 2, 10));
    b.mergeFrom(prog(1, 1, 5));
    b.mergeFrom(prog(4, 1, 25));
    checkSet(b, range(0, 28));

    b = new IntSetBuilder(10);
    b.mergeFrom(prog(0, 1, 10));
    b.mergeFrom(prog(10, 1, 1));
    b.mergeFrom(prog(11, 1, 1));
    checkSet(b, range(0, 11));

    b = new IntSetBuilder();
    b.mergeFrom(prog(100, 1, 100));
    b.mergeFrom(prog(0, 1, 100));
    checkSet(b, range(0, 199));

    b = new IntSetBuilder();
    b.mergeFrom(prog(100, 1, 100));
    checkSet(b, range(100, 199));
    b.mergeFrom(prog(200, 1, 1));
    checkSet(b, range(100, 200));
    b.mergeFrom(prog(80, 3, 20));
    checkSet(b, range(100, 200), new int[] {80, 83, 86, 89, 92, 95, 98});
    b.mergeFrom(prog(70, 1, 5));
    checkSet(b, range(100, 200), new int[] {80, 83, 86, 89, 92, 95, 98}, range(70, 74));
    b.mergeFrom(prog(201, 1, 10));
    checkSet(b, range(100, 210), new int[] {80, 83, 86, 89, 92, 95, 98}, range(70, 74));
    b.mergeFrom(prog(70, 1, 30));
    checkSet(b, range(70, 210));

    b = new IntSetBuilder();
    b.mergeFrom(prog(100, 1, 100));
    // make sure mySorted is reallocated with lots of free space
    b.mergeFrom(prog(200, 1, 1));
    b.mergeFrom(prog(190, 1, 5));
    b.mergeFrom(prog(190, 1, 20));
    checkSet(b, range(100, 209));
  }

  public void testAdd() {
    IntSetBuilder b = new IntSetBuilder();
    assertTrue(b.isEmpty());
    for (int i = 0; i < 8; i++) {
      b.add(i * 2);
    }
    assertFalse(b.isEmpty());
    b.addAll(10, 100, 200);
    b.addAll(IntArray.create(-5, 9, 3));
    b.addAll(IntArray.create(7, 15, 4).iterator());

    IntArray expected = IntArray.create();
    for (int i = 0; i < 8; i++) {
      expected.add(i * 2);
    }
    expected.addAll(10, 100, 200, -5, 9, 3, 7, 15, 4);
    expected.sortUnique();

    IntList collection = b.toTemporaryReadOnlySortedCollection();

    CHECK.order(collection.iterator(), expected.iterator());
  }

}
