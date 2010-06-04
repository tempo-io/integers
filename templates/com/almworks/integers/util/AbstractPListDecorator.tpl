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

// GENERATED CODE!!!
package com.almworks.integers.util;

import com.almworks.integers.Abstract#E#List;
import com.almworks.integers.#E#List;

public abstract class Abstract#E#ListDecorator extends Abstract#E#List {
  private final #E#List myBase;

  protected Abstract#E#ListDecorator(#E#List base) {
    myBase = base;
  }

  protected final #E#List base() {
    return myBase;
  }

  public abstract boolean iterate(int from, int to, #E#Visitor visitor);

  protected final boolean iterateBase(int from, int to, #E#Visitor visitor) {
    return iterate(myBase, from, to, visitor);
  }


  public static boolean iterate(#E#List list, int from, int to, #E#Visitor visitor) {
    if (list instanceof Abstract#E#ListDecorator) {
      if (!((Abstract#E#ListDecorator) list).iterate(from, to, visitor))
        return false;
    } else {
      for (int i = from; i < to; i++) {
        if (!visitor.accept(list.get(i), list))
          return false;
      }
    }
    return true;
  }

  public #E#List getBase() {
    return myBase;
  }

  public interface #E#Visitor {
    boolean accept(#e# value, #E#List source);
  }
}
