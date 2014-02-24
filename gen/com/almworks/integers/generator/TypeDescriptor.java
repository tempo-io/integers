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

package com.almworks.integers.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.almworks.integers.generator.StringSets.E;
import static com.almworks.integers.generator.StringSets.F;

/**
 * Contains substitutions for specific integer types.
 */
public enum TypeDescriptor {
  INT(StringSets.INT),
  LONG(StringSets.LONG),
  INT_INT(StringSets.INT, StringSets.INT),
  INT_LONG(StringSets.INT, StringSets.LONG),
  LONG_INT(StringSets.LONG, StringSets.INT),
  LONG_LONG(StringSets.LONG, StringSets.LONG),
  ;

  Map<String, String> myReplacements = new HashMap();
  private boolean myIsPair;

  TypeDescriptor(StringSets... stringSets) {
    myIsPair = stringSets.length == 2;
    String[] curFrom = E.getStrings();
    String[] curTo = stringSets[0].getStrings();
    for (int i = 0; i < curFrom.length; i++) {
      myReplacements.put(curFrom[i], curTo[i]);
    }

    if (stringSets.length == 2) {
      curFrom = F.getStrings();
      curTo = stringSets[1].getStrings();
      for (int i = 0; i < curFrom.length; i++) {
        myReplacements.put(curFrom[i], curTo[i]);
      }
    }
  }

  boolean isPair() {
    return myIsPair;
  }

  String apply(String template) {
    String out = template;
    for (Map.Entry<String, String> entry : myReplacements.entrySet()) {
      out = Pattern.compile(entry.getKey()).matcher(out).replaceAll(entry.getValue());
    }
    return out;
  }

}
