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

package com.almworks.integers.func;

public class #E#Functions {
  private #E#Functions() {}

  public static final #E#Function2 ADD = new #E#Function2() {
    @Override
    public #e# invoke(#e# a, #e# b) {
      return a + b;
    }

    @Override
    public String toString() {
      return "+";
    }
  };

  public static final #E#Function2 MULT = new #E#Function2() {
    @Override
    public #e# invoke(#e# a, #e# b) {
      return a * b;
    }

    @Override
    public String toString() {
      return "*";
    }
  };

  public static final #E#Function2 MOD = new #E#Function2() {
    @Override
    public #e# invoke(#e# a, #e# b) {
      return a % b;
    }

    @Override
    public String toString() {
      return "%";
    }
  };

  public static final #E#Function I = new #E#Function() {
    @Override
    public #e# invoke(#e# a) {
      return a;
    }

    @Override
    public String toString() {
      return "I";
    }
  };

  public static #E#Function apply(final #E#Function2 f, final #e# a) {
    return new #E#Function() {
      @Override
      public #e# invoke(#e# b) {
        return f.invoke(a, b);
      }

      @Override
      public String toString() {
        return f + " " + a;
      }
    };
  }

  public static #E#Function2 swap(final #E#Function2 f) {
    return new #E#Function2() {
      @Override
      public #e# invoke(#e# a, #e# b) {
        return f.invoke(b, a);
      }

      @Override
      public String toString() {
        return "swap (" + f + ')';
      }
    };
  }

  public static #E#Function2 ignore1(final #E#Function f) {
    return new #E#Function2() {
      @Override
      public #e# invoke(#e# a, #e# b) {
        return f.invoke(b);
      }

      @Override
      public String toString() {
        return "ingore1 (" + f + ')';
      }
    };
  }
}
