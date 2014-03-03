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

import java.io.*;
import java.util.regex.Pattern;

/**
 * see com.almworks.util.files.FileUtil
 */
public final class FileUtil {
  private static final Pattern FILE_NAME_PARSER = Pattern.compile("(.*)[.](.*)");

  public static void writeFile(File file, String content) throws IOException {
    writeFile(file, content.getBytes());
  }

  public static void writeFile(File file, String content, String charset) throws IOException {
    writeFile(file, content.getBytes(charset));
  }

  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static void writeFile(File file, byte[] bytes) throws IOException {
    FileOutputStream stream = null;
    try {
      stream = new FileOutputStream(file);
      stream.write(bytes);
    } finally {
      closeStreamIgnoreExceptions(stream);
    }
  }

  public static void closeStreamIgnoreExceptions(OutputStream stream) {
    if (stream != null)
      try {
        stream.close();
      } catch (IOException e) {
        // ignore
      } catch (Exception e) {
        System.err.println(e);
      }
  }

  public static void closeStreamIgnoreExceptions(InputStream stream) {
    if (stream != null)
      try {
        stream.close();
      } catch (IOException e) {
        // ignore
      } catch (Exception e) {
        System.err.println(e);
      }
  }

  public static String readFile(File file) throws IOException {
    return new String(loadFile(file));
  }

  public static String readFile(String file, String encoding) throws IOException {
    return new String(loadFile(new File(file)), encoding);
  }

  public static byte[] loadFile(File file) throws IOException {
//    Threads.assertLongOperationsAllowed();
    InputStream stream = null;
    try {
      stream = new FileInputStream(file);
      return readBytes(stream);
    } finally {
      closeStreamIgnoreExceptions(stream);
    }
  }

  public static byte[] readBytes(InputStream stream) throws IOException {
    assert stream != null;
    ByteArray byteArray = new ByteArray();
    byteArray.readAllFromStream(stream);
    return byteArray.toNativeArray();
  }
}
