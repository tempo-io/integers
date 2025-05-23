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

/**
 * Iterates through two sorted #e# lists in O(N+M), providing values that exist in the
 * first list and do not exist in the second
 */
public class #E#MinusIterator extends #E#FindingIterator {
  private final #E#Iterator myInclude;
  private final #E#Iterator myExclude;

  public #E#MinusIterator(#E#Iterable include, #E#Iterable exclude) {
    myInclude = include.iterator();
    myExclude = exclude.iterator();
  }

  protected boolean findNext() {
    #e# last = myNext;
    while (myInclude.hasNext()) {
      #e# v = myInclude.nextValue();
      assert !hasValue() || v >= last : last + " " + v + " " + myInclude;
      if (accept(v)) {
        myNext = v;
        return true;
      }
      last = v;
    }
    return false;
  }

  private boolean accept(#e# v) {
    #e# excludeValue = #EW#.MIN_VALUE;
    if (myExclude.hasValue()) {
      excludeValue = myExclude.value();
      if (v < excludeValue) {
        return true;
      } else if ( v == excludeValue) {
        return false;
      }
    }
    while (myExclude.hasNext()) {
      myExclude.next();
      assert myExclude.value() >= excludeValue : excludeValue + " " + myExclude.value() + "  " + myExclude;
      excludeValue = myExclude.value();
      if (v < excludeValue) {
        return true;
      } else if ( v == excludeValue) {
        return false;
      }
    }
    return true;
  }

}