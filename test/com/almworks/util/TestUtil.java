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

package com.almworks.util;

import junit.framework.TestCase;

public final class TestUtil {
  public static void assertEquals(Object object1, Object object2) {
    TestCase.assertEquals(object1, object2);
    if (object1 != null) {
      TestCase.assertEquals("hashCode", object1.hashCode(), object2.hashCode());
    }
  }

  public static void mustThrow(Runnable runnable) {
    mustThrow(null, "exception was not thrown", runnable);
  }

  public static void mustThrow(String message, Runnable runnable) {
    mustThrow(null, message, runnable);
  }

  public static void mustThrow(Class<? extends Throwable> clazz, Runnable runnable) {
    mustThrow(clazz, clazz + " was not thrown", runnable);
  }

  public static void mustThrow(Class<? extends Throwable> clazz, String message, Runnable runnable) {
    if (runnable == null) {
      throw new NullPointerException("runnable");
    }
    try {
      runnable.run();
      TestCase.fail(message);
    } catch (Throwable e) {
      if (clazz != null && !clazz.isInstance(e)) {
        if (e instanceof Error) {
          throw (Error)e;
        }
        if (e instanceof RuntimeException) {
          throw (RuntimeException)e;
        }
        TestCase.fail(message + " [threw " + e + " instead of " + clazz + "]");
      }
    }
  }
}
