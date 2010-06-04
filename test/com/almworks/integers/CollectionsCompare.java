package com.almworks.integers;

import junit.framework.Assert;
import junit.framework.ComparisonFailure;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.almworks.integers.IntegersUtils.indexOf;

/**
 * @author : Dyoma
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CollectionsCompare {
  public static Object[] EMPTY_OBJECTS = new Object[]{};

  public void singleElement(Object element, Collection<?> collection) {
    Assert.assertNotNull("Collection is null", collection);
    int size = collection.size();
    if (size == 1 && areEqual(collection.iterator().next(), element)) return;
    Failure failure = createFailure();
    failure.actual().setCollection(collection);
    failure.expected().setElement(element);
    if (size == 0) failure.fail("Empty collection");
    if (size > 1) failure.fail("Expected 1 element but was " + size);
    failure.fail();
  }

  private static boolean areEqual(Object a, Object b) {
    return a == b || !(a == null || b == null) && a.equals(b);
  }

  public static boolean areOrdersEqual(@Nullable List<?> list1, @Nullable List<?> list2) {
    if (list1 == null || list2 == null)
      return list1 == list2;
    int size = list1.size();
    if (size != list2.size())
      return false;
    for (int i = 0; i < size; i++)
      if (!areEqual(list1.get(i), list2.get(i)))
        return false;
    return true;
  }

  public void order(Object[] expected, List<?> actual) {
    order(Arrays.asList(expected), actual);
  }

  public void order(Object[] expected, Object[] actual) {
    order(Arrays.asList(expected), Arrays.asList(actual));
  }

  public void order(List<?> expected, List<?> actual) {
    Assert.assertNotNull("Expected in null", expected);
    Assert.assertNotNull("Collection in null", actual);
    if (areOrdersEqual(expected, actual)) return;
    Failure failure = createFailure();
    failure.expected().setCollection(expected);
    failure.actual().setCollection(actual);
    int expectedSize = expected.size();
    int actualSize = actual.size();
    if (expectedSize != actualSize)
      failure.failSizes();
    else
      failure.fail("Elements aren't equal");
  }

  public void order(int[] actual, int ... expected) {
    Assert.assertNotNull("Actual is null", actual);
    Assert.assertNotNull("Expected is null", expected);
    if (Arrays.equals(actual, expected)) return;
    Failure failure = createFailure();
    failure.actual().setIntArray(actual);
    failure.expected().setIntArray(expected);
    if (actual.length != expected.length)
      failure.failSizes();
    else
      failure.fail("Ints aren't equal");
  }

  public void order(long[] actual, long ... expected) {
    Assert.assertNotNull("Actual is null", actual);
    Assert.assertNotNull("Expected is null", expected);
    if (Arrays.equals(actual, expected)) return;
    Failure failure = createFailure();
    failure.actual().setLongArray(actual);
    failure.expected().setLongArray(expected);
    if (actual.length != expected.length)
      failure.failSizes();
    else
      failure.fail("Longs aren't equal");
  }

  public void order(byte[] actual, byte... expected) {
    Assert.assertNotNull("Actual is null", actual);
    Assert.assertNotNull("Expected is null", expected);

    if (Arrays.equals(actual, expected)) return;
    Failure failure = createFailure();
    failure.actual().setByteArray(actual);
    failure.expected().setByteArray(expected);
    if (actual.length != expected.length)
      failure.failSizes();
    else
      failure.fail("Bytes aren't equal");
  }

  public void unordered(Collection<?> actual, Object ... expected) {
    ArrayList<?> expectedList = new ArrayList(Arrays.asList(expected));
    ArrayList<?> actualList = new ArrayList(actual);
    unordered(expectedList, actualList);
  }

  public void unordered(List<?> expectedList, List<?> actualList) {
    expectedList = sort(expectedList);
    actualList = sort(actualList);
    order(expectedList, actualList);
  }

  private java.util.List<Object> sort(List<?> list) {
    java.util.List<Object> copy = new ArrayList<Object>(list);
    Collections.sort(copy, new Comparator() {
      public int compare(Object o1, Object o2) {
        if ((o1 instanceof Comparable) && (o2 instanceof Comparable))
          //noinspection RedundantCast
          return ((Comparable<? super Comparable<?>>) o1).compareTo((Comparable<?>)o2);
        return compareInts(o1.hashCode(), o2.hashCode());
      }
    });
    return copy;
  }

  public static int compareInts(int a, int b) {
    return (a < b ? -1 : (a == b ? 0 : 1));
  }

  public void order(Object[] expected, Enumeration<?> actual) {
    order(expected, Containers.collectList(actual));
  }

  public void order(Object[] objects, Iterator<?> iterator) {
    order(objects, Containers.collectList(iterator));
  }

  public void empty(Collection<?> collection) {
    order(EMPTY_OBJECTS, Collections.enumeration(collection));
  }

  public void empty(int[] array) {
    order(array);
  }

  public void empty(Enumeration<?> enumeration) {
    empty(Containers.collectList(enumeration));
  }

  public void empty(Object[] array) {
    empty(array != null ? Arrays.asList(array) : Collections.emptyList());
  }

  public void size(int expectedSize, Collection<?> collection) {
    if (expectedSize == collection.size())
      return;
    if (expectedSize == 0)
      empty(collection);
    Failure failure = createFailure();
    failure.expected().setSize(expectedSize);
    failure.actual().setCollection(collection);
    failure.failSizes();
  }

  public void size(int expectedSize, Object[] array) {
    size(expectedSize, Arrays.asList(array));
  }

  public void size(int expectedSize, int[] array) {
    if (expectedSize == array.length)
      return;
    if (expectedSize == 0)
      empty(array);
    Failure failure = createFailure();
    failure.expected().setSize(expectedSize);
    failure.actual().setIntArray(array);
    failure.failSizes();
  }

  public <T> void contains(T element, T[] array) {
    contains(element, Arrays.asList(array));
  }

  public <T> void contains(T element, Collection<T> collection) {
    if (collection.contains(element))
      return;
    Failure failure = createFailure();
    failure.expected().setElement(element);
    failure.actual().setCollection(collection);
    failure.fail("Expected contains");
  }

  public void contains(int val, int[] array) {
    if (indexOf(array, val) >= 0)
      return;
    Failure failure = createFailure();
    failure.expected().setElement(val);
    failure.actual().setIntArray(array);
    failure.fail("Expected contains");
  }

  protected Failure createFailure() {
    return new Failure();
  }

  public void bits(BitSet actual, int ... expected) {
    int expectedSize = expected.length;
    while (expectedSize > 0 && expected[expectedSize - 1] == 0)
      expectedSize--;
    if (expectedSize != expected.length) {
      int[] tmp = new int[expectedSize];
      System.arraycopy(expected, 0, tmp, 0, expectedSize);
      expected = tmp;
    }
    int[] actualInts = new int[actual.length()];
    for (int i = 0; i < actualInts.length; i++)
      actualInts[i] = actual.get(i) ? 1 : 0;
    order(actualInts, expected);
  }

  public static String toString(int[] array) {
    StringBuilder builder = new StringBuilder();
    String sep = "";
    for (int n : array) {
      builder.append(sep);
      sep = "\n";
      builder.append(n);
    }
    return builder.toString();
  }

  public static String toString(long[] array) {
    StringBuilder builder = new StringBuilder();
    String sep = "";
    for (long n : array) {
      builder.append(sep);
      sep = "\n";
      builder.append(n);
    }
    return builder.toString();
  }

  public static String toString(byte[] array) {
    StringBuilder builder = new StringBuilder();
    String sep = "";
    for (byte b : array) {
      builder.append(sep);
      sep = "\n";
      builder.append(Integer.toString((int)b, 0x10));
    }
    return builder.toString();
  }

  public void order(List<?> actual, Object ... expected) {
    if (expected == null)
      expected = EMPTY_OBJECTS;
    order(Arrays.asList(expected), actual);
  }

  public void empty(Iterable<?> iterable) {
    empty(Containers.collectList(iterable.iterator()));
  }

  protected static class FailureSide {
    private String myString;
    private int mySize = -1;

    public void setCollection(Collection<?> collection) {
      mySize = collection.size();
      myString = toText(collection);
    }

    public void setIntArray(int[] array) {
      mySize = array.length;
      myString = CollectionsCompare.toString(array);
    }

    public void setLongArray(long[] array) {
      mySize = array.length;
      myString = CollectionsCompare.toString(array);
    }

    public void setByteArray(byte[] array) {
      mySize = array.length;
      myString = CollectionsCompare.toString(array);
    }

    public void setElement(Object element) {
      mySize = 1;
      myString = elementToText(element);
    }

    public void setArray(Object[] expected) {
      mySize = expected.length;
      setCollection(Arrays.asList(expected));
    }

    private String toText(Collection<?> collection) {
      StringBuffer result = new StringBuffer();
      String separator = "";
      for (Object o : collection) {
        result.append(separator);
        separator = "\n";
        result.append(elementToText(o));
      }
      return result.toString();
    }

    private String toText(BitSet bitSet) {
      StringBuilder builder = new StringBuilder();
      String sep = "";
      for (int i = 0; i < bitSet.length(); i++) {
        builder.append(sep);
        sep = "\n";
        builder.append(bitSet.get(i) ? 1 : 0);
      }
      return builder.toString();
    }

    private String elementToText(Object o) {
      return o == null ? null : o.toString();
    }

    public int getSize() {
      return mySize;
    }

    public String getString() {
      return myString;
    }

    public void setSize(int size) {
      mySize = size;
      myString = "";
    }

    public void setBitSet(BitSet bitSet) {
      mySize = bitSet.length();
      myString = toText(bitSet);
    }
  }

  protected static class Failure {
    private final FailureSide myExpected;
    private final FailureSide myActual;

    public Failure() {
      myExpected = new FailureSide();
      myActual = new FailureSide();
    }

    public FailureSide expected() {
      return myExpected;
    }

    public FailureSide actual() {
      return myActual;
    }

    public void fail(String message) {
      throw new ComparisonFailure(message, myExpected.getString(), myActual.getString());
    }

    public void failSizes() {
      fail("Size mismatch. Expected <" + expected().getSize() + "> but was <" + actual().getSize() + ">");
    }

    public void fail() {
      fail("");
    }
  }
}
