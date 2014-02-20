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
  ;
  String[] myStrings;

  StringSets(String... strings) {
    myStrings = strings;
  }

  public String[] getStrings() {
    return myStrings;
  }
}
