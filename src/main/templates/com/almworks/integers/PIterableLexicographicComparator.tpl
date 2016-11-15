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

package com.almworks.integers;

import java.util.Comparator;

public class #E#IterableLexicographicComparator implements Comparator<#E#Iterable> {
  public static final #E#IterableLexicographicComparator #EC#_ITERABLE_LEXICOGRAPHIC_COMPARATOR = new #E#IterableLexicographicComparator();

  @Override
  public int compare(#E#Iterable l1, #E#Iterable l2) {
    if (l1 == l2) return 0;
    if (l1 == null) return -1;
    if (l2 == null) return 1;
    #E#Iterator i1 = l1.iterator();
    #E#Iterator i2 = l2.iterator();
    while(i1.hasNext() && i2.hasNext()) {
      int comp = #E#Collections.compare(i1.nextValue(), i2.nextValue());
      if (comp != 0) return comp;
    }
    if (i1.hasNext()) return 1;
    if (i2.hasNext()) return -1;
    return 0;
  }
}
