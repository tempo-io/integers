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

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class #E#Iterables {

  public static Iterable<#E#Iterator> fromIterator(final #E#Iterator i) {
    return new Iterable<#E#Iterator>() {
      @Override @NotNull public #E#Iterator iterator() {
        return i;
      }
    };
  }

  public static Iterable<#E#ListIterator> fromListIterator(final #E#ListIterator i) {
    return new Iterable<#E#ListIterator>() {
      @Override @NotNull public Iterator<#E#ListIterator> iterator() {
        return (Iterator)i;
      }
    };
  }

  public static Iterable<Writable#E#ListIterator> fromWritableListIterator(final Writable#E#ListIterator i) {
    return new Iterable<Writable#E#ListIterator>() {
      @Override @NotNull public Iterator<Writable#E#ListIterator> iterator() {
        return (Iterator)i;
      }
    };
  }
}
