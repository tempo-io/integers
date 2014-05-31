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

// CODE GENERATED FROM com/almworks/integers/wrappers/PObjHppcWrappers.tpl


package com.almworks.integers.wrappers;

import com.almworks.integers.IntObjFindingIterator;
import com.carrotsearch.hppc.cursors.IntObjectCursor;

import java.util.Iterator;

public class IntObjHppcWrappers {
  public static <T> IntObjFindingIterator<T> cursorToIntObjIterator(final Iterator<IntObjectCursor<T>> cursor) {
    return new IntObjFindingIterator<T>() {
      @Override
      protected boolean findNext() {
        if (!cursor.hasNext()) return false;
        IntObjectCursor<T> cur = cursor.next();
        myNextLeft = cur.key;
        myNextRight = cur.value;
        return true;
      }
    };

  }
}
