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

/**
* Many algorigthms iterates collection sequentially from begining.
* For such algorithms simplified implementations of collection is sufficient.<br>
* This class is designed to provide simple way to define such simple collection.
*/
public interface IntIterable extends Iterable<IntIterator> {
  /**
   * @return fail-fast read-only iterator for the collection. If Iterable represents empty collection it
   * should return {@link IntIterator#EMPTY}
   */
  @NotNull
  IntIterator iterator();
}
