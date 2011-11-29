/*
 * Copyright 2011 ALM Works Ltd
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

import java.util.NoSuchElementException;

public abstract class Abstract#E#Iterator implements #E#Iterator {

  public #e# nextValue() {
    next();
    return value();
  }

  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  class Single extends Abstract#E#Iterator {
    private #e# myValue;
    private boolean myIterated;

    public Single(#e# value) {
      myValue = value;
    }

    public boolean hasNext() {
      return !myIterated;
    }

    public #E#Iterator next() throws NoSuchElementException {
      if (myIterated) throw new NoSuchElementException();
      myIterated = true;
      return this;
    }

    public #e# value() {
      return myValue;
    }
  }
}
