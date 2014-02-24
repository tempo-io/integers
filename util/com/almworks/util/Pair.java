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

package com.almworks.util;

import java.util.Comparator;

/**
 * @author : Dyoma
 */
public abstract class Pair <A, B> {
  public static final Pair[] EMPRY_ARRAY = new Pair[0];

  public abstract A getFirst();

  public abstract B getSecond();

  public int hashCode() {
    A first = getFirst();
    int code = first == null ? 9010391 : first.hashCode();
    B second = getSecond();
    code = code * 23 + (second == null ? 28719051 : second.hashCode());
    return code;
  }

  @SuppressWarnings({"RawUseOfParameterizedType"})
  public boolean equals(Object o) {
    if (!(o instanceof Pair))
      return false;
    Object thatFirst = ((Pair) o).getFirst();
    Object thatSecond = ((Pair) o).getSecond();
    return equals(thatFirst, getFirst()) && equals(thatSecond, getSecond());
  }

  private static boolean equals(Object a, Object b) {
    if (a == b)
      return true;
    if (a == null || b == null)
      return false;
    return a.equals(b);
  }

  public final Pair<A, B> copyWithFirst(A first) {
    return create(first, getSecond());
  }

  public final Pair<A, B> copyWithSecond(B second) {
    return create(getFirst(), second);
  }

  public String toString() {
    return "Pair<" + getFirst() + ", " + getSecond() + ">";
  }

  //todo rename to "pair" and have statically imported
  public static <A, B> Pair<A, B> create(final A first, final B second) {
    return new Pair<A, B>() {
      public A getFirst() {
        return first;
      }

      public B getSecond() {
        return second;
      }
    };
  }

  public static <A, B> Pair.Builder<A, B> create() {
    return new Builder<A, B>();
  }

  @SuppressWarnings({"RawUseOfParameterizedType"})
  private static final Pair NULL_NULL = new Pair() {
    @SuppressWarnings({"ConstantConditions"})
    public Object getFirst() {
      return null;
    }

    @SuppressWarnings({"ConstantConditions"})
    public Object getSecond() {
      return null;
    }
  };
  public static <A, B> Pair<A, B> nullNull() {
    return NULL_NULL;
  }

  public static <T> Comparator<Pair<T, ?>> compareFirst(final Comparator<T> comparator) {
    return new Comparator<Pair<T, ?>>() {
      public int compare(Pair<T, ?> o1, Pair<T, ?> o2) {
        T f1 = o1 != null ? o1.getFirst() : null;
        T f2 = o2 != null ? o2.getFirst() : null;
        return comparator.compare(f1, f2);
      }
    };
  }

  public static class Builder <A, B> extends Pair<A, B> {
    private A myFirst;
    private B mySecond;

    public void setFirst(A first) {
      myFirst = first;
    }

    public void setSecond(B second) {
      mySecond = second;
    }

    public A getFirst() {
      return myFirst;
    }

    public B getSecond() {
      return mySecond;
    }
  }
}
