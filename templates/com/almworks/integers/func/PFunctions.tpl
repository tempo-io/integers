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



package com.almworks.integers.func;

import com.almworks.integers.#E#Iterable;
import com.almworks.integers.#E#Iterator;

public class #E#Functions {
  private #E#Functions() {}

  public static final #E#To#E# NEG = new #E#To#E#() {
    @Override
    public #e# invoke(#e# a) {
      return -a;
    }

    @Override
    public String toString() {
      return "-";
    }
  };

  public static final #E#To#E# INC = new #E#To#E#() {
    @Override
    public #e# invoke(#e# a) {
      return a + 1;
    }

    @Override
    public String toString() {
      return "+1";
    }
  };

  public static final #E#To#E# DEC = new #E#To#E#() {
    @Override
    public #e# invoke(#e# a) {
      return a - 1;
    }

    @Override
    public String toString() {
      return "-1";
    }
  };

  public static final #E#To#E# SQR = new #E#To#E#() {
    @Override
    public #e# invoke(#e# a) {
      return a * a;
    }

    @Override
    public String toString() {
      return "^2";
    }
  };

  public static final #E#To#E# I = new #E#To#E#() {
    @Override
    public #e# invoke(#e# a) {
      return a;
    }

    @Override
    public String toString() {
      return "I";
    }
  };

  public static final #E##E#To#E# ADD = new #E##E#To#E#() {
    @Override
    public #e# invoke(#e# a, #e# b) {
      return a + b;
    }

    @Override
    public String toString() {
      return "+";
    }
  };

  public static final #E##E#To#E# MULT = new #E##E#To#E#() {
    @Override
    public #e# invoke(#e# a, #e# b) {
      return a * b;
    }

    @Override
    public String toString() {
      return "*";
    }
  };

  public static final #E##E#To#E# MOD = new #E##E#To#E#() {
    @Override
    public #e# invoke(#e# a, #e# b) {
      return a % b;
    }

    @Override
    public String toString() {
      return "%";
    }
  };

  public static #E#To#E# apply(final #E##E#To#E# f, final #e# a) {
    return new #E#To#E#() {
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

  public static #E#To#E# swap(final #e# v1, final #e# v2) {
    return new #E#To#E#() {
      @Override
      public #e# invoke(#e# x) {
        return
            x == v1 ? v2 :
                x == v2 ? v1 :
                    x;
      }

      @Override
      public String toString() {
        return v1 + " <-> " + v2;
      }
    };
  }

  public static #E#To#E# compose(final #E#To#E# f1, final #E#To#E# f2) {
    return new #E#To#E#() {
      @Override
      public #e# invoke(#e# a) {
        return f1.invoke(f2.invoke(a));
      }

      @Override
      public String toString() {
        return '(' + f1.toString() + ") o (" + f2.toString() + ')';
      }
    };
  }

  /** Returns a function that returns values from the supplied Iterable. Function argument is ignored. */
  public static #E#To#E# sequence(final #E#Iterable iterable) {
    return new #E#To#E#() {
      #E#Iterator it = iterable.iterator();
      @Override
      public #e# invoke(#e# a) {
        return it.nextValue();
      }

      @Override
      public String toString() {
        return "i => " + iterable;
      }
    };
  }

  public static #E##E#To#E# swap(final #E##E#To#E# f) {
    return new #E##E#To#E#() {
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

  public static #E##E#To#E# ignore1(final #E#To#E# f) {
    return new #E##E#To#E#() {
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
