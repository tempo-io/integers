package com.almworks.integers.util;

import com.almworks.integers.IntProgression;
import com.almworks.integers.IntegersFixture;
import com.almworks.util.TestUtil;
import junit.framework.TestCase;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.NoSuchElementException;

public class IntObjMapTests extends IntegersFixture {
  private IntObjMap<String> myMap = IntObjMap.create();

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
    List<Integer> ints = IntProgression.arithmetic(0, 50).toList();
    Collections.shuffle(ints);
    for (Integer i : ints) {
      myMap.put(i, String.valueOf(i));
    }
    int last = -1;
    for (String s : myMap.getValues()) {
      assertNotNull(s);
      int cur = toInt(s, -10);
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
  public static int toInt(@Nullable String value, int defaultValue) {
    if (value == null)
      return defaultValue;
    try {
      return Integer.valueOf(value.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public void testIterable() {
    List<Integer> ints = IntProgression.arithmetic(0, 50).toList();
    Collections.shuffle(ints);
    for (Integer i : ints) {
      myMap.put(i, String.valueOf(i));
    }

    int last = -1;
    for(IntObjMap.Entry<String> p : myMap) {
      assertEquals(String.valueOf(p.getKey()), p.getValue());
      int cur = toInt(p.getValue(), -10);
      assertTrue(last + " " + cur, last < cur);
    }
  }

  public void testIterator() {
    myMap.put(0, "0");
    myMap.put(100, "100");

    final IntObjMap.IntMapIterator it = myMap.iterator();
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

    IntObjMap.IntMapIterator it2 = myMap.iterator();
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
    IntObjMap.IntMapIterator it = myMap.find(50);
    checkEntry(it.next(), 100);
    it = myMap.find(100);
    checkEntry(it.next(), 100);
  }

  public void testRemove() {
    myMap.put(0, "0");
    myMap.put(100, "100");
    assertEquals("0", myMap.remove(0));
    IntObjMap.IntMapIterator it = myMap.iterator();
    checkEntry(it.next(), 100);
    assertFalse(it.hasNext());
  }

  private void checkEntry(IntObjMap.Entry<String> e, int key) {
    assertEquals(key, e.getKey());
    assertEquals(String.valueOf(key), e.getValue());
  }

  public void testContainsKey() {
    for (int i = 0; i < 10; i += 2) {
      myMap.put(i, Integer.toString(i));
    }
    for (int i = 0; i < 10; i++) {
      assertEquals(i % 2 == 0, myMap.containsKey(i));
    }
  }

  public void testKeysIterator() {
    myMap.put(0, "0");
    myMap.put(100, "100");
    myMap.put(228, "228");
    CHECK.order(myMap.keysIterator(0, myMap.size()), 0, 100, 228);
  }
}