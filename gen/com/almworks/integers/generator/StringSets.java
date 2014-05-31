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

/**
 * Contains possible type of mapping patterns. These patterns are replaced in the template sources to corresponding variants of primitive integer type names.
 * */
public enum StringSets {
  E("#e#",     // Name of the primitive type. Examples: int, long.
      "#EW#",  // Name of the corresponding Object-descendant wrapper type. Examples: Integer, Long.
      "#E#",   // Short variant of primitive type name, starting from capital letter. Use in class names. Examples: Int, Long.
      "#EC#"), // Capitalized variant of short name. Use in constants' names. Examples: INT, LONG.
  F("#f#", "#FW#", "#F#", "#FC#"),
  INT("int", "Integer", "Int", "INT"),
  LONG("long", "Long", "Long", "LONG"),
  SHORT("short", "Short", "Short", "SHORT"),
  BYTE("byte", "Byte", "Byte", "BYTE"),
  CHAR("char", "Character", "Char", "CHAR"),
  ;
  String[] myStrings;

  StringSets(String... strings) {
    myStrings = strings;
  }

  public String[] getStrings() {
    return myStrings;
  }
}
