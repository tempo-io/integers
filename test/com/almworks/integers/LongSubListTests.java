/*
 * Copyright 2013 ALM Works Ltd
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

package com.almworks.integers;

import java.util.ArrayList;
import java.util.List;

public class LongSubListTests extends LongListChecker {
  @Override
  protected List<LongList> createLongList(long... values) {
    final int length = values.length;

    // [...]
    List<LongList> result = new ArrayList<LongList>(4);
    result.add(new AbstractLongList.SubList(LongArray.create(values), 0, length));

    // ~[...]
    long[] newValues = new long[length + 3];
    System.arraycopy(values, 0, newValues, 3, length);
    for (int i = 0; i < 3; i++) {
      newValues[i] = rand.nextInt();
    }
    result.add(new AbstractLongList.SubList(LongArray.create(newValues), 3, length + 3));

    // [...]~
    newValues = new long[length + 3];
    System.arraycopy(values, 0, newValues, 0, length);
    for (int i = 0; i < 3; i++) {
      newValues[i + length] = rand.nextInt();
    }
    result.add(new AbstractLongList.SubList(LongArray.create(newValues), 0, length));

    // ~[...]~
    newValues = new long[length + 6];
    System.arraycopy(values, 0, newValues, 3, length);
    for (int i = 0; i < 3; i++) {
      newValues[i] = rand.nextInt();
      newValues[i + 3 + length] = rand.nextInt();
    }
    result.add(new AbstractLongList.SubList(LongArray.create(newValues), 3, length + 3));
    return result;
  }
}
