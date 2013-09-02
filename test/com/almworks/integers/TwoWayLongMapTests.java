/*
 * Copyright 2011 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almworks.integers;

import com.almworks.integers.func.LongFunction;
import com.almworks.integers.func.LongFunction2;
import junit.framework.ComparisonFailure;

import java.util.List;

import static com.almworks.integers.LongProgression.arithmetic;
import static com.almworks.integers.func.LongFunctions.*;

public class TwoWayLongMapTests extends IntegersFixture {
  private final CollectionsCompare compare = new CollectionsCompare();
  private final TwoWayLongMap map = new TwoWayLongMap();

  public void setUp() {
    super.setUp();
    map.clear();
  }

  public void testPutSimple() {
    assertEquals(10, map.put(0, 10));
    assertEquals(12, map.put(2, 12));
    assertEquals(10, map.get(0));
    assertEquals(12, map.get(2));

    assertEquals(11, map.put(1, 11));
    assertEquals(11, map.get(1));

    assertEquals(11, map.put(1, 111));
    assertEquals(111, map.get(1));
  }

  public void testPutSimple2() {
    assertEquals(0, map.put(0, 0));
    assertEquals(30, map.put(3, 30));
    assertEquals(10, map.put(1, 10));
    assertEquals(40, map.put(4, 40));
    assertEquals(20, map.put(2, 20));
    for (int i = 0; i < 5; ++i) assertEquals(10 * i, map.get(i));

    assertEquals(10, map.put(1, 25));
    assertEquals(0, map.get(0));
    assertEquals(25, map.get(1));
    assertEquals(20, map.get(2));
    assertEquals(30, map.get(3));
    assertEquals(40, map.get(4));

    assertEquals(25, map.put(1, -5));
    assertEquals(0, map.get(0));
    assertEquals(-5, map.get(1));
    assertEquals(20, map.get(2));
    assertEquals(30, map.get(3));
    assertEquals(40, map.get(4));

    assertEquals(20, map.put(2, 100));
    assertEquals(0, map.get(0));
    assertEquals(-5, map.get(1));
    assertEquals(100, map.get(2));
    assertEquals(30, map.get(3));
    assertEquals(40, map.get(4));
  }

  public void testPutRandom() {
    final int N_KEYS = 1000;
    LongArray keySet = new LongArray(N_KEYS);
    int prime0 = 47;
    for (int n = 0; n < N_KEYS; ++n) {
      int key = RAND.nextInt(N_KEYS);
      boolean hasAdded = keySet.contains(key);
      assertEquals(hasAdded, map.containsKey(key));
      if (!hasAdded) {
        int value = key % prime0;
        long added = map.put(key, value);
        assertEquals(value, added);
        keySet.add(key);
      }
    }
    keySet.sort();
    compare.order(keySet.toNativeArray(), map.getKeys().toNativeArray());
    checkMapValsEqualKeysMod(prime0);

    int[] primes = new int[] { 53, 59, 61, 67, 71 };
    int oldPrime = prime0;
    for (int prime : primes) {
      for (Long key : map.getKeys().toList()) {
        long put = map.put(key, key % prime);
        assertEquals("key=" + key + " | prime=" + prime, put, key % oldPrime);
      }
      compare.order(keySet.toNativeArray(), map.getKeys().toNativeArray());
      checkMapValsEqualKeysMod(prime);
      oldPrime = prime;
    }
  }

  private void checkMapValsEqualKeysMod(int prime) {
/*
    for (IntIterator i = map.getKeys().iterator(); i.hasNext(); ) {
      int key = i.nextValue();
      assertEquals("key=" + key + " | prime=" + prime, key % prime, map.get(key));
    }
*/
    checkMapEqivalentTo(apply(swap(MOD), prime));
  }

  private void checkMapEqivalentTo(LongFunction f) {
    StringBuilder expected = new StringBuilder();
    StringBuilder actual = new StringBuilder();
    boolean fail = false;
    for (LongIterator i : map.getKeys()) {
      long key = i.value();
      long exp = f.invoke(key);
      long act = map.get(key);
      expected.append(new TwoWayLongMap.Entry(key, exp)).append("\n");
      actual.append(new TwoWayLongMap.Entry(key, act)).append("\n");
      fail |= exp != act;
    }
    if (fail) {
      throw new ComparisonFailure("Map is not equivalent of " + f, expected.toString(), actual.toString());
    }
  }

  public void testInsertAllSimple() {
    map.insertAll(LongArray.create(0, 3, 1, 4, 2), LongArray.create(0, 30, 10, 40, 20));
    for (int i = 0; i < 5; ++i) assertEquals(i*10, map.get(i));

    map.clear();
    assertEquals(0, map.size());

    assertEquals(0, map.put(0, 0));
    assertEquals(50, map.put(5, 50));
    assertEquals(80, map.put(8, 80));
    compare.order(map.getKeys().toNativeArray(), 0, 5, 8);
    compare.order(map.getVals().toNativeArray(), 0, 50, 80);

    map.insertAll(LongArray.create(3, 7, 1, 4, 9, 2, 6), LongArray.create(30, 70, 10, 40, 90, 20, 60));
    compare.order(map.getKeys().toNativeArray(), arithmetic(0, 10).toNativeArray());
    for (int i = 0; i < 10; ++i) assertEquals(i*10, map.get(i));

    map.clear();
    map.insertAllRo(LongProgression.arithmetic(0, 10), LongProgression.arithmetic(0, 10, 10));
    for (int i = 0; i < 10; ++i) assertEquals(i*10, map.get(i));
  }

  public void testContains() {
    assertTrue(map.containsAllKeys(LongList.EMPTY));
    assertFalse(map.containsAnyKeys(LongList.EMPTY));
    map.insertAll(LongArray.create(0, 3, 1, 5), LongArray.create(0, 30, 10, 50));
    assertTrue(map.containsAllKeys(LongArray.create(1, 5)));
    assertTrue(map.containsAnyKeys(LongArray.create(1, 5)));
    assertTrue(map.containsAllKeys(LongArray.create(3, 5, 0, 1)));
    assertTrue(map.containsAnyKeys(LongArray.create(3, 5, 0, 1)));
    assertFalse(map.containsAllKeys(LongArray.create(5, 3, 4, 1, 2, 0)));
    assertTrue(map.containsAnyKeys(LongArray.create(5, 3, 4, 1, 2, 0)));
    assertFalse(map.containsAllKeys(LongArray.create(4, 6, 8, 10)));
    assertFalse(map.containsAnyKeys(LongArray.create(4, 6, 8, 10)));
    assertFalse(map.containsAllKeys(LongArray.create(8, 6, 10, 4)));
    assertFalse(map.containsAnyKeys(LongArray.create(8, 6, 10, 4)));
  }

  public void testInsertAllRandom() {
    final int N_KEYS = 1000;
    int prime = 239;
    for (int n = 0; n < 10; ++n) {
      int attempt;
      for (attempt = 0; attempt < 10; ++attempt) {
        LongArray keys = new LongArray();
        for (int i = 0; i < 5; ++i) {
          int start = RAND.nextInt(N_KEYS);
          keys.addAll(arithmetic(start, i + 1));
        }
        removeDuplicates(keys);
        if (!map.containsAnyKeys(keys)) {
          LongArray vals = new LongArray(keys.size());
          for (int i = 0; i < keys.size(); ++i) {
            vals.add(keys.get(i) % prime);
          }
          map.insertAll(keys, vals);
          checkMapValsEqualKeysMod(prime);
          break;
        }
      }
      if (attempt >= 10) {
        System.err.println("Test has not finished: not enough keys left on iteration " + n);
        return;
      }
    }
  }

  private void removeDuplicates(LongArray keys) {
    for (int i = 0; i < keys.size(); ++i) {
      long k1 = keys.get(i);
      for (WritableLongListIterator keyIt : keys.write()) {
        long k2 = keyIt.value();
        if (keyIt.index() != i && k1 == k2) keyIt.remove();
      }
    }
  }

  public void testTransformValsNotChangingSortedState() {
    map.insertAll(new LongArray(arithmetic(0, 10)), apply(MULT, 10));
    map.transformVals(apply(ADD, 1));
    for (int i = 0; i < map.size(); ++i) assertEquals(i*10+1, map.get(i));
    map.transformVals(new LongFunction() {
      public long invoke(long a) {
        return a - ((a - 1)/10)%2;
      }
    });
    for (int i = 0; i < map.size(); ++i) assertEquals(i * 10 + (1 - (i % 2)), map.get(i));
    map.transformVals(apply(MULT, 0));
    for (int i = 0; i < map.size(); ++i) assertEquals(0, map.get(i));
  }

  public void testTransformValsChangingSortedState() {
    map.insertAll(new LongArray(arithmetic(0, 10)), I);
    map.transformVals(apply(swap(MOD), 3));
    checkMapValsEqualKeysMod(3);
  }

  public void testTransformValsMany() {
    int N_KEYS = 1000;
    int prime0 = 239;
    map.insertAll(new LongArray(arithmetic(0, N_KEYS)), apply(swap(MOD), prime0));
    checkMapValsEqualKeysMod(prime0);
    int[] primes = {31, 37, 41, 43, 47};
    for (int pi = 0; pi < primes.length; ++pi) {
      final int prime = primes[pi];
      map.transformVals(new LongFunction2() {
        @Override
        public long invoke(long key, long val) {
          return (key % prime == 0) ? prime : val;
        }
      });
      for (int key = 0; key < N_KEYS; ++key) {
        int expected = key % prime0;
        for (int j = 0; j <= pi; ++j) {
          if (key % primes[j] == 0) expected = primes[j];
        }
        assertEquals(expected, map.get(key));
      }
    }
  }

  public void testRemoveSimple() {
    map.insertAllRo(arithmetic(0, 10), apply(swap(MOD), 3));
    map.remove(3);
    map.remove(5);
    map.remove(1);
    map.remove(2);
    assertFalse(map.containsAnyKeys(LongArray.create(1, 2, 3, 5)));
    assertEquals(6, map.size());
    checkMapValsEqualKeysMod(3);
  }

  public void testRemoveRandom() {
    final int N_KEYS = 1000;
    int prime = 41;
    map.insertAllRo(arithmetic(0, N_KEYS, 3), apply(swap(MOD), prime));
    LongArray removed = new LongArray();
    for (int i = 0; i < N_KEYS / 10; ++i) {
      int k;
      while (true) {
        k = RAND.nextInt(N_KEYS)*3;
        if (!removed.contains(k)) {
          removed.add(k);
          break;
        }
      }
      map.remove(k);
      checkMapValsEqualKeysMod(prime);
    }
  }

  public void testRemoveAllSimple() {
    map.insertAllRo(arithmetic(0, 10), apply(swap(MOD), 3));
    LongArray toRemove = LongArray.create(1, 9, 3, 2, 4);
    LongList notInMap = map.removeAll(toRemove);
    assertTrue(notInMap.isEmpty());
    assertFalse(map.containsAnyKeys(toRemove));
    assertEquals(10 - toRemove.size(), map.size());
    checkMapValsEqualKeysMod(3);

    toRemove = LongArray.create(-2, -1, 0, 1, 5, 6, 8, 10);
    notInMap = map.removeAll(toRemove);
    compare.order(notInMap.iterator(), -2, -1, 1, 10);
    compare.order(map.getKeys().iterator(), 7);
    checkMapValsEqualKeysMod(3);

    map.clear();
    map.insertAllRo(arithmetic(0, 10), apply(swap(MOD), 3));
    notInMap = map.removeAll(LongArray.create(7, 9, 8));
    assertTrue(notInMap.isEmpty());
    compare.order(map.getKeys().iterator(), 0, 1, 2, 3, 4, 5, 6);
    checkMapValsEqualKeysMod(3);
  }

  public void testRemoveAllRandom() {
    final int N_KEYS = 3000;
    final int N_REMOVED_STEP = 20;
    final int N_TRIALS = 100;
    final int PRIME = 41;
    LongArray alreadyRemoved = new LongArray();
    map.insertAllRo(LongProgression.arithmetic(0, N_KEYS), apply(swap(MOD), PRIME));
    for (int i = 0; i < N_TRIALS; ++i) {
      LongArray toRemove = new LongArray(N_REMOVED_STEP);
      for (int j = 0; j < N_REMOVED_STEP; ++j) toRemove.add(RAND.nextInt(N_KEYS));
      LongList notInMap = map.removeAll(toRemove);
      for (int j = 0; j < notInMap.size(); ++j)
        assertTrue(i + " " + j, alreadyRemoved.contains(notInMap.get(j)));
      checkMapValsEqualKeysMod(PRIME);
      alreadyRemoved.addAll(toRemove);
      assertFalse(map.containsAnyKeys(toRemove));
      assertFalse(map.containsAnyKeys(alreadyRemoved));
    }
  }

  public void testPermute() {
    map.insertAllRo(LongProgression.arithmetic(0, 10), apply(swap(MOD), 3));
    permute(LongProgression.arithmetic(9, 10, -1));
    checkMapEqivalentTo(compose(apply(swap(MOD), 3), compose(apply(ADD, 9), NEG)));

    map.clear();
    map.insertAllRo(LongProgression.arithmetic(0, 10), apply(swap(MOD), 3));
    permute(LongArray.create(0, 1, /* { */6, 4, 3, 7, 2, 5/* } */, 8, 9));
    compare.order(pairs(
        0, 0,
        1, 1,
        2, 0,
        3, 1,
        4, 0,
        5, 1,
        6, 2,
        7, 2,
        8, 2,
        9, 0
    ), map.toList());

    map.clear();
    map.insertAllRo(LongProgression.arithmetic(0, 10), apply(swap(MOD), 3));
    map.transformKeys(apply(ADD, 1));
    checkMapEqivalentTo(compose(apply(swap(MOD), 3), apply(ADD, -1)));
    map.transformKeys(apply(ADD, 1));
    checkMapEqivalentTo(compose(apply(swap(MOD), 3), apply(ADD, -2)));
    map.transformKeys(apply(ADD, 1));
    checkMapValsEqualKeysMod(3);
  }


  private void permute(final LongList permutation) {
    map.transformKeys(new LongFunction() {
      @Override
      public long invoke(long a) {
        return permutation.get((int)a);
      }
    });
  }

  private List<TwoWayLongMap.Entry> pairs(int... ps) {
    assertTrue(ps.length % 2 == 0);
    List<TwoWayLongMap.Entry> list = IntegersUtils.arrayList();
    for (int i = 0; i < ps.length; i += 2) {
      list.add(new TwoWayLongMap.Entry(ps[i], ps[i+1]));
    }
    return list;
  }

  public void testRemoveAllVals() {
    map.insertAllRo(LongProgression.arithmetic(0, 10), apply(swap(MOD), 3));
    LongList notInMap;
    notInMap = map.removeAllVals(LongArray.create(2, 0));
    compare.empty(notInMap.toNativeArray());
    compare.order(map.getKeys().iterator(), 1, 4, 7);
    checkMapValsEqualKeysMod(3);
    map.removeAllVals(LongArray.create(1));
    assertEquals(0, map.size());

    map.clear();
    map.insertAllRo(LongProgression.arithmetic(0, 10, 2), apply(swap(MOD), 13));
    // Important: specify one of the values not in the map
    notInMap = map.removeAllVals(LongArray.create(6, 7, 8, 9, 10));
    compare.order(notInMap.iterator(), 7, 9);
    compare.order(map.getKeys().iterator(), 0, 2, 4, 12, 14, 16, 18);
    checkMapValsEqualKeysMod(13);
  }

  public void testRemoveAllValsMany() {
    final int N_KEYS = 2000;
    final int PRIME = 43;
    final int N_ATTEMPTS = 10;
    final int VALS_IN_ATTEMPT = 3;
    map.insertAllRo(LongProgression.arithmetic(0, N_KEYS), apply(swap(MOD), PRIME));
    LongArray removed = new LongArray();
    for (int i = 0; i < N_ATTEMPTS; ++i) {
      LongArray toRemove = new LongArray();
      for (int j = 0; j < VALS_IN_ATTEMPT; ++j) {
        int v = RAND.nextInt(PRIME);
        toRemove.add(v);
        removed.add(v);
      }
      map.removeAllVals(toRemove);
      for (int j = 0; j < removed.size(); ++j) {
        assertFalse(i + "\n" + map.getKeys() + "\n" + toRemove + "\n" + removed.get(j), map.getKeys().contains(removed.get(j)));
      }
      checkMapValsEqualKeysMod(PRIME);
    }

  }
  public void testNonInjectiveFunctionException() {
    map.insertAll(new LongArray(arithmetic(0, 10)), apply(MULT, 10));
    map.transformVals(apply(ADD, 1));
//    System.out.println(map.size());
    for (int i = 0; i < map.size(); ++i) assertEquals(i*10+1, map.get(i));

    boolean caught = false;
    try {
      map.transformKeys(new LongFunction() {
        public long invoke(long a) {
          return a + 1 + a%2;
        }
      });
    } catch (TwoWayLongMap.NonInjectiveFunctionException ex) {
      caught = true;
      assertEquals(3, ex.getDuplicateValue());
    }
    assertTrue(caught);

  }
}