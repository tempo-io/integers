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

// CODE GENERATED FROM com/almworks/integers/AbstractWritablePSet.tpl


package com.almworks.integers;

public abstract class AbstractWritableIntSet extends AbstractIntSet implements WritableIntSet {
  protected int myModCount = 0;

  /**
   * include element without invocation of {@code AbstractWritableIntSet#modified()}
   */
  protected abstract boolean include0(int value);

  /**
   * exclude element without invocation of {@code AbstractWritableIntSet#modified()}
   */
  protected abstract boolean exclude0(int value);

  protected void add0(int value) {
    include0(value);
  }

  protected void remove0(int value) {
    exclude0(value);
  }

  protected void modified() {
    myModCount++;
  }

  @Override
  public boolean include(int value) {
    modified();
    return include0(value);
  }

  @Override
  public boolean exclude(int value) {
    modified();
    return exclude0(value);
  }

  public void add(int value) {
    modified();
    add0(value);
  }

  public void remove(int value) {
    modified();
    remove0(value);
  }

  @Override
  public void removeAll(int... values) {
    modified();
    if (values.length == 1) {
      remove(values[0]);
    } else {
      removeAll(new IntNativeArrayIterator(values));
    }
  }

  @Override
  public void removeAll(IntList values) {
    modified();
    removeAll(values.iterator());
  }

  @Override
  public void removeAll(IntIterator iterator) {
    modified();
    while (iterator.hasNext()) {
      remove0(iterator.nextValue());
    }
  }

  @Override
  public void addAll(IntList values) {
    modified();
    addAll(values.iterator());
  }

  @Override
  public void addAll(IntIterable iterable) {
    modified();
    for (IntIterator it : iterable) {
      include0(it.value());
    }
  }

  public void addAll(int... values) {
    modified();
    if (values != null && values.length != 0) {
      for (int value: values) {
        add0(value);
      }
    }
  }

  @Override
  public void retain(IntList values) {
    IntArray res = new IntArray();
    for (IntIterator it: values.iterator()) {
      int value = it.value();
      if (contains(value)) res.add(value);
    }
    clear();
    addAll(res);
  }

  protected final IntIterator failFast(IntIterator iter) {
    return new IntFailFastIterator(iter) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }
}
