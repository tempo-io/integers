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

package com.almworks.util;

import java.util.Random;

public class TestRandom {
  /** Tests may use (pseudo-)random number generator via {@link #getRandom}. To reproduce failing test, look up the used seed in the logs and set this system property. */
  public static final String RANDGEN_SEED = "tests.seed";

  private Random myRandom;

  private Random initRandom() {
    String seedStr = System.getProperty(RANDGEN_SEED);
    long seed = 0L;
    if (seedStr != null) {
      try {
        seed = Long.parseLong(seedStr);
      } catch (NumberFormatException ex) {
        System.err.println("wrong seed format: " + ex);
      }
    }
    if (seed != 0L) System.err.println("Using seed from system properties: " + seed);
    else {
      seed = System.currentTimeMillis();
      System.err.println("Using seed " + seed);
    }
    return new Random(seed);
  }

  public synchronized Random getRandom() {
    if (myRandom == null) {
      myRandom = initRandom();
    }
    return myRandom;
  }
}
