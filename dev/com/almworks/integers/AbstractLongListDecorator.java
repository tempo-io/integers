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

// CODE GENERATED FROM com/almworks/integers/AbstractPListDecorator.tpl




package com.almworks.integers;

public abstract class AbstractLongListDecorator extends AbstractLongList {
  private final LongList myBase;

  protected AbstractLongListDecorator(LongList base) {
    myBase = base;
  }

  protected final LongList base() {
    return myBase;
  }

  public abstract boolean iterate(int from, int to, LongVisitor visitor);

  protected final boolean iterateBase(int from, int to, LongVisitor visitor) {
    return iterate(myBase, from, to, visitor);
  }


  public static boolean iterate(LongList list, int from, int to, LongVisitor visitor) {
    if (list instanceof AbstractLongListDecorator) {
      if (!((AbstractLongListDecorator) list).iterate(from, to, visitor))
        return false;
    } else {
      for (int i = from; i < to; i++) {
        if (!visitor.accept(list.get(i), list))
          return false;
      }
    }
    return true;
  }

  public LongList getBase() {
    return myBase;
  }

  public interface LongVisitor {
    boolean accept(long value, LongList source);
  }
}
