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

// CODE GENERATED FROM com/almworks/integers/PFailFastIterator.tpl


package com.almworks.integers;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class LongFailFastListIterator extends LongFailFastIterator implements LongListIterator {
  protected abstract int getCurrentModCount();

  public LongFailFastListIterator(LongListIterator it) {
    super(it);
  }

  private LongListIterator getListIterator() {
    return (LongListIterator) myIterator;
  }

  @Override
  public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
    checkMod();
    getListIterator().move(offset);
  }

  @Override
  public long get(int offset) throws ConcurrentModificationException, NoSuchElementException {
    checkMod();
    return getListIterator().get(offset);
  }

  @Override
  public int index() throws NoSuchElementException {
    checkMod();
    return getListIterator().index();
  }
}