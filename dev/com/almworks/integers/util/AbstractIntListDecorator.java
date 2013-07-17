/*
 * Copyright 2010 ALM Works Ltd
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

// CODE GENERATED FROM com/almworks/integers/util/AbstractPListDecorator.tpl


package com.almworks.integers.util;

import com.almworks.integers.AbstractIntList;
import com.almworks.integers.IntList;

public abstract class AbstractIntListDecorator extends AbstractIntList {
  private final IntList myBase;

  protected AbstractIntListDecorator(IntList base) {
    myBase = base;
  }

  protected final IntList base() {
    return myBase;
  }

  public abstract boolean iterate(int from, int to, IntVisitor visitor);

  protected final boolean iterateBase(int from, int to, IntVisitor visitor) {
    return iterate(myBase, from, to, visitor);
  }


  public static boolean iterate(IntList list, int from, int to, IntVisitor visitor) {
    if (list instanceof AbstractIntListDecorator) {
      if (!((AbstractIntListDecorator) list).iterate(from, to, visitor))
        return false;
    } else {
      for (int i = from; i < to; i++) {
        if (!visitor.accept(list.get(i), list))
          return false;
      }
    }
    return true;
  }

  public IntList getBase() {
    return myBase;
  }

  public interface IntVisitor {
    boolean accept(int value, IntList source);
  }
}
