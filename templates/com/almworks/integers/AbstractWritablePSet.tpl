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

public abstract class AbstractWritable#E#Set extends Abstract#E#Set implements Writable#E#Set {
  protected int myModCount = 0;

  /**
   * include element without invocation of {@code AbstractWritable#E#Set#modified()}
   */
  protected abstract boolean include0(#e# value);

  /**
   * exclude element without invocation of {@code AbstractWritable#E#Set#modified()}
   */
  protected abstract boolean exclude0(#e# value);

  protected void add0(#e# value) {
    include0(value);
  }

  protected void remove0(#e# value) {
    exclude0(value);
  }

  protected void modified() {
    myModCount++;
  }

  @Override
  public boolean include(#e# value) {
    modified();
    return include0(value);
  }

  @Override
  public boolean exclude(#e# value) {
    modified();
    return exclude0(value);
  }

  public void add(#e# value) {
    modified();
    add0(value);
  }

  public void remove(#e# value) {
    modified();
    remove0(value);
  }

  @Override
  public void removeAll(#e#... values) {
    modified();
    if (values.length == 1) {
      remove(values[0]);
    } else {
      removeAll(new #E#NativeArrayIterator(values));
    }
  }

  @Override
  public void removeAll(#E#Iterable iterable) {
    modified();
    for (#E#Iterator iterator : iterable) {
      remove0(iterator.value());
    }
  }

  @Override
  public void addAll(#E#List values) {
    modified();
    addAll(values.iterator());
  }

  @Override
  public void addAll(#E#Iterable iterable) {
    modified();
    for (#E#Iterator it : iterable) {
      include0(it.value());
    }
  }

  public void addAll(#e#... values) {
    modified();
    if (values != null && values.length != 0) {
      for (#e# value: values) {
        add0(value);
      }
    }
  }

  @Override
  public void retain(#E#List values) {
    #E#Array res = new #E#Array();
    for (#E#Iterator it: values) {
      #e# value = it.value();
      if (contains(value)) res.add(value);
    }
    clear();
    addAll(res);
  }

  protected final #E#Iterator failFast(#E#Iterator iter) {
    return new #E#FailFastIterator(iter) {
      @Override
      protected int getCurrentModCount() {
        return myModCount;
      }
    };
  }
}
