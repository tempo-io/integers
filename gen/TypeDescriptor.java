/*
 * Copyright 2010 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.*;
import java.util.regex.Pattern;

/**
 * Contains substitutions for specific integer types.
 */
public enum TypeDescriptor {
  INT("int", "Integer", "Int", "INT", "I"),
  LONG("long", "Long", "Long", "LONG", "J"),
//    BYTE("byte", "Byte", "Byte", "BYTE", "B"),
//    SHORT("short", "Short", "Short", "SHORT", "S")
  ;

  private final Map<PatternType, String> myReplacements = new HashMap<PatternType, String>();
  private final String myApplicableCode;

  TypeDescriptor(String type, String objectWrapper, String shortName, String capitalizedName, String applicableCode) {
    myApplicableCode = applicableCode;
    myReplacements.put(PatternType.TYPE, type);
    myReplacements.put(PatternType.OBJECT_WRAPPER, objectWrapper);
    myReplacements.put(PatternType.SHORT_NAME, shortName);
    myReplacements.put(PatternType.CAPS_NAME, capitalizedName);
  }

  public String apply(String template) {
    String out = template;
    for (PatternType aspect : PatternType.values()) {
      out = aspect.getPattern().matcher(out).replaceAll(myReplacements.get(aspect));
    }
    return out;
  }

  public static List<TypeDescriptor> getApplicableTypes(String applicabilityString) {
    List<TypeDescriptor> applicableTypes = new ArrayList<TypeDescriptor>();
    for (TypeDescriptor t : values()) {
      if(applicabilityString.contains(t.myApplicableCode)) {
        applicableTypes.add(t);
      }
    }
    return applicableTypes;
  }

  public static String getAllApplicableString() {
    StringBuilder all = new StringBuilder(values().length);
    for (TypeDescriptor t : values()) {
      all.append(t.myApplicableCode);
    }
    return all.toString();
  }

  /**
   * Contains possible type of mapping patterns. These patterns are replaced in the template sources to corresponding variants of primitive integer type names.
   * */
  public static enum PatternType {
    /** Name of the primitive type, like <strong><tt>long</tt></strong>. */
    TYPE("#e#"),
    /** Name of the corresponding Object-descendant wrapper type, like <strong><tt>Long</tt></strong>. */
    OBJECT_WRAPPER("#EW#"),
    /** Short variant of primitive type name, starting from capital letter. Use in class names. Examples: <strong><tt>Int</tt></strong>, <strong><tt>Long</tt></strong>. */
    SHORT_NAME("#E#"),
    /** Capitalized variant of {@link #SHORT_NAME}. Use in constants' names. Examples: <strong><tt>INT</tt></strong>, <strong><tt>LONG</tt></strong>. */
    CAPS_NAME("#EC#"),
    ;

    private final String myPattern;

    Pattern getPattern() {
      return Pattern.compile(myPattern);
    }

    PatternType(String pattern) {
      myPattern = pattern;
    }
  }
}
