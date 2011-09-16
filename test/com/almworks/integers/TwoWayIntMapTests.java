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

import com.almworks.integers.func.IntFunction;
import com.almworks.integers.func.IntFunction2;
import com.almworks.util.TestRandom;
import junit.framework.TestCase;

import java.util.Random;

import static com.almworks.integers.IntProgression.arithmetic;
import static com.almworks.integers.func.IntFunctions.*;

public class TwoWayIntMapTests extends TestCase {
  private final CollectionsCompare compare = new CollectionsCompare();
  private final TwoWayIntMap map = new TwoWayIntMap();

  public void setUp() throws Exception {
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
    Random random = new TestRandom().getRandom();
    final int N_KEYS = 1000;
    IntArray keySet = new IntArray(N_KEYS);
    int prime0 = 47;
    for (int n = 0; n < N_KEYS; ++n) {
      int key = random.nextInt(N_KEYS);
      boolean hasAdded = keySet.contains(key);
      assertEquals(hasAdded, map.containsKey(key));
      if (!hasAdded) {
        int value = key % prime0;
        int added = map.put(key, value);
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
      for (Integer key : map.getKeys().toList()) {
        int put = map.put(key, key % prime);
        assertEquals("key=" + key + " | prime=" + prime, put, key % oldPrime);
      }
      compare.order(keySet.toNativeArray(), map.getKeys().toNativeArray());
      checkMapValsEqualKeysMod(prime);
      oldPrime = prime;
    }
  }

  private void checkMapValsEqualKeysMod(int prime) {
    for (IntIterator i = map.getKeys().iterator(); i.hasNext(); ) {
      int key = i.next();
      assertEquals("key=" + key + " | prime=" + prime, key % prime, map.get(key));
    }
  }

  public void testInsertAllSimple() {
    map.insertAll(IntArray.create(0, 3, 1, 4, 2), IntArray.create(0, 30, 10, 40, 20));
    for (int i = 0; i < 5; ++i) assertEquals(i*10, map.get(i));

    map.clear();
    assertEquals(0, map.size());

    assertEquals(0, map.put(0, 0));
    assertEquals(50, map.put(5, 50));
    assertEquals(80, map.put(8, 80));
    compare.order(map.getKeys().toNativeArray(), 0, 5, 8);
    compare.order(map.getVals().toNativeArray(), 0, 50, 80);

    map.insertAll(IntArray.create(3, 7, 1, 4, 9, 2, 6), IntArray.create(30, 70, 10, 40, 90, 20, 60));
    compare.order(map.getKeys().toNativeArray(), arithmetic(0, 10).toNativeArray());
    for (int i = 0; i < 10; ++i) assertEquals(i*10, map.get(i));

    map.clear();
    map.insertAllRo(IntProgression.arithmetic(0, 10), IntProgression.arithmetic(0, 10, 10));
    for (int i = 0; i < 10; ++i) assertEquals(i*10, map.get(i));
  }

  public void testContains() {
    assertTrue(map.containsAllKeys(IntList.EMPTY));
    assertFalse(map.containsAnyKeys(IntList.EMPTY));
    map.insertAll(IntArray.create(0, 3, 1, 5), IntArray.create(0, 30, 10, 50));
    assertTrue(map.containsAllKeys(IntArray.create(1, 5)));
    assertTrue(map.containsAnyKeys(IntArray.create(1, 5)));
    assertTrue(map.containsAllKeys(IntArray.create(3, 5, 0, 1)));
    assertTrue(map.containsAnyKeys(IntArray.create(3, 5, 0, 1)));
    assertFalse(map.containsAllKeys(IntArray.create(5, 3, 4, 1, 2, 0)));
    assertTrue(map.containsAnyKeys(IntArray.create(5, 3, 4, 1, 2, 0)));
    assertFalse(map.containsAllKeys(IntArray.create(4, 6, 8, 10)));
    assertFalse(map.containsAnyKeys(IntArray.create(4, 6, 8, 10)));
    assertFalse(map.containsAllKeys(IntArray.create(8, 6, 10, 4)));
    assertFalse(map.containsAnyKeys(IntArray.create(8, 6, 10, 4)));
  }

  public void testInsertAllRandom() {
    final int N_KEYS = 1000;
    Random rand = new TestRandom().getRandom();
    int prime = 239;
    for (int n = 0; n < 10; ++n) {
      int attempt;
      for (attempt = 0; attempt < 10; ++attempt) {
        IntArray keys = new IntArray();
        for (int i = 0; i < 5; ++i) {
          int start = rand.nextInt(N_KEYS);
          keys.addAll(arithmetic(start, i + 1));
        }
        removeDuplicates(keys);
        if (!map.containsAnyKeys(keys)) {
          IntArray vals = new IntArray(keys.size());
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

  private void removeDuplicates(IntArray keys) {
    for (int i = 0; i < keys.size(); ++i) {
      int k = keys.get(i);
      for (int j = 0; j < keys.size(); ++j) {
        if (i != j && keys.get(j) == k)
          keys.removeAt(j);
      }
    }
  }

  public void testTransformValsNotChangingSortedState() {
    map.insertAll(new IntArray(arithmetic(0, 10)), apply(MULT, 10));
    map.transformVals(apply(ADD, 1));
    for (int i = 0; i < map.size(); ++i) assertEquals(i*10+1, map.get(i));
    map.transformVals(new IntFunction() {
      public int invoke(int a) {
        return a - ((a - 1)/10)%2;
      }
    });
    for (int i = 0; i < map.size(); ++i) assertEquals(i * 10 + (1 - (i % 2)), map.get(i));
    map.transformVals(apply(MULT, 0));
    for (int i = 0; i < map.size(); ++i) assertEquals(0, map.get(i));
  }

  public void testTransformValsChangingSortedState() {
    map.insertAll(new IntArray(arithmetic(0, 10)), I);
    map.transformVals(apply(swap(MOD), 3));
    checkMapValsEqualKeysMod(3);
  }

  public void testTransformValsMany() {
    int N_KEYS = 1000;
    int prime0 = 239;
    map.insertAll(new IntArray(arithmetic(0, N_KEYS)), apply(swap(MOD), prime0));
    checkMapValsEqualKeysMod(prime0);
    int[] primes = {31, 37, 41, 43, 47};
    for (int pi = 0; pi < primes.length; ++pi) {
      final int prime = primes[pi];
      map.transformVals(new IntFunction2() {
        @Override
        public int invoke(int key, int val) {
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
}
