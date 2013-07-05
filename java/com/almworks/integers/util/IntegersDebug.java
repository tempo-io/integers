/*
 * Copyright 2013 ALM Works Ltd
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

package com.almworks.integers.util;

import java.util.Arrays;

public class IntegersDebug {
  public static final String DEBUG_STATUS = "integers.debug";

  public static boolean isDebug() {
    boolean res = false;
    String debugStatus = System.getProperty(DEBUG_STATUS);
    try {
      res = Boolean.parseBoolean(debugStatus);
    } catch (NumberFormatException ex) {
      System.err.println("wrong debug format: " + ex);
    }
    return res;
  }

  public static void print(Object ... elements) {
    if (isDebug()) {
      StringBuilder sb = new StringBuilder();
      if (elements == null) sb.append("null");
      else {
        String sep = "";
        for (Object e : elements) {
          sb.append(sep).append(e);
          sep = " ";
        }
      }
      System.out.print(sb);
    }
  }

  public static void println(Object ... elements) {
    if (isDebug()) {
      print(elements);
      System.out.println();
    }
  }

}
