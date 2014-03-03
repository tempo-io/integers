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

package com.almworks.integers.generator
class SkipImportsRegex {
  static DOT = /\s*[.]\s*/
  static I = /import\s+(?:static\s+)?((?:\w|#)+(?:$DOT(?:\w|#)+)*(?:$DOT[*])?)\s*;/
  static J = /(?:$I(?:\s*$I)*)/
  static K = /(?m:\/\/\s*$J\s*?)/
  static L = /(?:\/[*]\s*$J\s*[*]\/)/
  static M = /(?:$J|$K|$L)/
  static P = /(?:package\s+(?:\w|#)+(?:$DOT(?:\w|#)+)*\s*);/
  static N = /(?:$P?(\s*(?:$M(?:\s*$M)*)\s*))/

  public static final String REGEX = N;
  public static final String WHITESPACE_REGEX = /$P?(\s*)/
}
