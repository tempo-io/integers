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

// CODE GENERATED FROM com/almworks/integers/wrappers/PHppcWrappers.tpl


package com.almworks.integers.wrappers;

import com.almworks.integers.IntFindingIterator;
import com.almworks.integers.IntIterator;
import com.carrotsearch.hppc.cursors.IntCursor;

import java.util.Iterator;

public class IntHppcWrappers {
  public static IntIterator cursorToIntIterator(final Iterator<IntCursor> cursor) {
    return new IntFindingIterator() {
      @Override
      protected boolean findNext() {
        if (!cursor.hasNext()) return false;
        myNext = cursor.next().value;
        return true;
      }
    };

  }
}
