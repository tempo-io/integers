package com.almworks.integers;

import java.util.*;

public final class Containers {
  private Containers() {}

  public static <T> List<T> collectList(Iterator<? extends T> listIterator) {
    ArrayList<T> result = new ArrayList<T>();
    while (listIterator.hasNext()) {
      T t = listIterator.next();
      result.add(t);
    }
    return result;
  }

  public static <T> List<T> collectList(Enumeration<? extends T> enumeration) {
    ArrayList<T> result = new ArrayList<T>();
    while (enumeration.hasMoreElements()) {
      T t = enumeration.nextElement();
      result.add(t);
    }
    return result;
  }

}
