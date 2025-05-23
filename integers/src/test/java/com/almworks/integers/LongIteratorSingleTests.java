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
import java.util.NoSuchElementException;

public class LongIteratorSingleTests extends IntegersFixture {
  public void testAll() {
    LongIterator.Single element = new LongIterator.Single(1);
    assertTrue(element.hasNext());

    try {
      element.value();
      fail("not caught NSEE");
    } catch(NoSuchElementException ex) { }

    element.next();
    assertEquals(1, element.value());

    try {
      element.next();
      fail("not caught NSEE");
    } catch(NoSuchElementException ex) { }
  }
}