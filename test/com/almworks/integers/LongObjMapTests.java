/*
 * Copyright 2014 ALM Works Ltd
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

import com.almworks.util.TestUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LongObjMapTests extends WritableLongObjMapChecker<LongObjMap> {
  private LongObjMap<String> myMap = LongObjMap.create();

  @Override
  protected List<WritableLongIntMapFromLongObjMap> createMapsFromLists(LongList keys, IntList values) {
    WritableLongIntMapFromLongObjMap map0 = createMap();
    map0.putAll(keys, values);
    return Arrays.asList(map0);
  }

  @Override
  protected <T> LongObjMap<T> createObjMap() {
    return new LongObjMap<T>();
  }

  @Override
  protected <T> LongObjMap<T> createObjMapWithCapacity(int capacity) {
    return createObjMap();
  }

  @Override
  protected <T> List<WritableLongObjMap<T>> createObjMapsFromLists(LongList keys, List<T> values) {
    WritableLongObjMap<T> map0 = new LongObjMap<T>();
    map0.putAll(keys, values);
    return Arrays.asList(map0);
  }

  public void testPutGet() {
    myMap.put(0, "0");
    assertEquals("0", myMap.get(0));
    myMap.put(1, "1");
    assertEquals("1", myMap.get(1));
    myMap.put(0, "A");
    assertEquals("A", myMap.get(0));
    assertNull(myMap.get(2));
  }

  public void testValuesSet() {
    List<Long> ints = LongProgression.arithmetic(0, 50).toList();
    Collections.shuffle(ints);
    for (Long i : ints) {
      myMap.put(i, String.valueOf(i));
    }
    long last = -1;
    for (String s : myMap.getValues()) {
      assertNotNull(s);
      long cur = toLong(s, -10);
      assertTrue(last + " " + cur, last < cur);
      last = cur;
    }
  }

  /**
   * Returns integer value of a string, if it can be parsed. If not, returns default value.
   *
   * @param value a string that probably holds an integer
   * @param defaultValue default value to return if string cannot be parsed
   * @return integer value of the string
   */
  // copied from Util
  public static long toLong(@Nullable String value, long defaultValue) {
    if (value == null)
      return defaultValue;
    try {
      return Long.valueOf(value.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public void testIterable() {
    List<Long> ints = LongProgression.arithmetic(0, 50).toList();
    Collections.shuffle(ints);
    for (Long i : ints) {
      myMap.put(i, String.valueOf(i));
    }

    int last = -1;

    for(LongObjIterator<String> it : myMap) {
      assertEquals(String.valueOf(it.left()), it.right());
      long cur = toLong(it.right(), -10);
      assertTrue(last + " " + cur, last < cur);
    }
  }

  public void testIterator2() {
    myMap.put(0, "0");
    myMap.put(100, "100");

    final LongObjMap.LongMapIterator it = myMap.iterator();
    assertFalse(it.hasPrevious());
    assertTrue(it.hasNext());

    checkIterator(it.next(), 0);
    checkIterator(it.next(), 100);
    assertFalse(it.hasNext());
    TestUtil.mustThrow(NoSuchElementException.class, new Runnable() { public void run() { it.next(); }});
    checkIterator(it.previous(), 0);

    myMap.put(50, "50");
    TestUtil.mustThrow(ConcurrentModificationException.class, new Runnable() { public void run() {it.next();} });

    LongObjMap.LongMapIterator it2 = myMap.iterator();
    checkIterator(it2.next(), 0);
    checkIterator(it2.next(), 50);
    it2.remove();
    checkIterator(it2.next(), 100);
    checkIterator(it2.previous(), 0);

    checkIterator(it2.next(), 100);
    checkIterator(it2.previous(), 0);
    it2.remove();
    assertTrue(it2.hasNext());
    assertFalse(it2.hasPrevious());
    checkIterator(it2.next(), 100);
  }

  public void testFind() {
    myMap.put(0, "0");
    myMap.put(100, "100");
    LongObjMap.LongMapIterator it = myMap.find(50);
    checkIterator(it.next(), 100);
    it = myMap.find(100);
    checkIterator(it.next(), 100);
  }

  public void testRemove() {
    myMap.put(0, "0");
    myMap.put(100, "100");
    assertEquals("0", myMap.remove(0));
    LongObjMap.LongMapIterator it = myMap.iterator();
    checkIterator(it.next(), 100);
    assertFalse(it.hasNext());
  }

  private void checkIterator(LongObjIterator<String> e, int key) {
    assertEquals(key, e.left());
    assertEquals(String.valueOf(key), e.right());
  }

  public void testContainsKey() {
    for (int i = 0; i < 10; i += 2) {
      myMap.put(i, Long.toString(i));
    }
    for (int i = 0; i < 10; i++) {
      assertEquals(i % 2 == 0, myMap.containsKey(i));
    }
  }

  public void testKeySet() {
    myMap.put(0, "0");
    myMap.put(100, "100");
    myMap.put(228, "228");
    checkSet(myMap.keySet(), LongArray.create(0, 100, 228));

    LongArray expected = new LongArray(10000);
    expected.addAll(0, 100, 228);
    for (int i = 0; i < 10000; i++) {
      long value = myRand.nextInt();
      expected.add(value);
      myMap.put(value, Long.toString(value));
    }
    expected.sortUnique();
    checkSet(myMap.keySet(), expected);
  }

  @Override
  protected boolean isSortedSet() {
    return false;
  }
}