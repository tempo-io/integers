package com.almworks.integers.util;

import com.almworks.integers.IntegersFixture;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongProgression;
import com.almworks.util.RandomHolder;
import com.almworks.util.TestUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LongObjMapTests extends IntegersFixture {
  private LongObjMap<String> myMap = LongObjMap.create();

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
    for(LongObjMap.Entry<String> p : myMap) {
      assertEquals(String.valueOf(p.getKey()), p.getValue());
      long cur = toLong(p.getValue(), -10);
      assertTrue(last + " " + cur, last < cur);
    }
  }

  public void testIterator() {
    myMap.put(0, "0");
    myMap.put(100, "100");

    final LongObjMap.LongMapIterator it = myMap.iterator();
    assertFalse(it.hasPrevious());
    assertTrue(it.hasNext());
    checkEntry(it.next(), 0);
    checkEntry(it.previous(), 0);
    it.next();
    checkEntry(it.next(), 100);
    assertFalse(it.hasNext());
    TestUtil.mustThrow(NoSuchElementException.class, new Runnable() { public void run() { it.next(); }});
    checkEntry(it.previous(), 100);

    myMap.put(50, "50");
    TestUtil.mustThrow(ConcurrentModificationException.class, new Runnable() { public void run() {it.next();} });

    LongObjMap.LongMapIterator it2 = myMap.iterator();
    checkEntry(it2.next(), 0);
    checkEntry(it2.next(), 50);
    it2.remove();
    checkEntry(it2.next(), 100);
    checkEntry(it2.previous(), 100);
    checkEntry(it2.previous(), 0);

    checkEntry(it2.next(), 0);
    checkEntry(it2.previous(), 0);
    it2.remove();
    assertTrue(it2.hasNext());
    assertFalse(it2.hasPrevious());
    checkEntry(it2.next(), 100);
  }

  public void testFind() {
    myMap.put(0, "0");
    myMap.put(100, "100");
    LongObjMap.LongMapIterator it = myMap.find(50);
    checkEntry(it.next(), 100);
    it = myMap.find(100);
    checkEntry(it.next(), 100);
  }

  public void testRemove() {
    myMap.put(0, "0");
    myMap.put(100, "100");
    assertEquals("0", myMap.remove(0));
    LongObjMap.LongMapIterator it = myMap.iterator();
    checkEntry(it.next(), 100);
    assertFalse(it.hasNext());
  }

  private void checkEntry(LongObjMap.Entry<String> e, int key) {
    assertEquals(key, e.getKey());
    assertEquals(String.valueOf(key), e.getValue());
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
    CHECK.order(myMap.keySet(), 0, 100, 228);

    LongArray expected = new LongArray(10000);
    expected.addAll(0, 100, 228);
    for (int i = 0; i < 10000; i++) {
      long value = rand.nextInt();
      expected.add(value);
      myMap.put(value, Long.toString(value));
    }
    expected.sortUnique();
    CHECK.order(myMap.keySet(), expected);
  }
}