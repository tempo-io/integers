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

import com.almworks.util.BadFormatException;
import com.almworks.util.FileUtil;
import com.almworks.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Inspired by GNU Trove generator.
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr", "HardCodedStringLiteral", "CallToSystemExit"})
public class IntegerCollectionsCodegen {
  public static final int RET_VAL_HELP = 10;
  public static final int RET_VAL_INPUT_ERR = 239;
  public static final int RET_VAL_RUNTIME_ERR = 240;

  public static final String generatedCodeNoticeTemplate = "// CODE GENERATED FROM ";

  public static final String javaComments = "/[*]([^*]*?[*])+?/|(?m://.*$)";
  public static final String javaEntityNameExtractor = "(?:public\\s+)?(?:class|enum|interface|(?:@\\s*interface))\\s+((?:\\w|#)+)";
  public static final String skipImportsRegex = SkipImportsRegex.REGEX;
  public static final String skipImportsWhitespaceRegex = SkipImportsRegex.WHITESPACE_REGEX;
  private static final String lineSeparator = String.format("%n");

  public static final String OUTPUT_EXT_PROPERTY_KEY = "integers.outputExt";
  private static final Set<String> HELP_STR = hashSet("/?", "-h", "-help");

  private final File mySourceRoot;
  private final File myDestinationRoot;
  private final boolean myCheckOnly;
  private final String myOutputFileExt;

  public IntegerCollectionsCodegen(File sourceRoot, File destinationRoot, boolean checkOnly) {
    mySourceRoot = sourceRoot;
    myDestinationRoot = destinationRoot;
    myCheckOnly = checkOnly;
    myOutputFileExt = System.getProperty(OUTPUT_EXT_PROPERTY_KEY, ".java");
  }

  private static <T> HashSet<T> hashSet(T... elem) {
    if (elem == null || elem.length == 0) return new HashSet<T>();
    HashSet<T> set = new HashSet<T>(elem.length);
    set.addAll(Arrays.asList(elem));
    return set;
  }

  /**
   * Generates Java code for collections of primitive integer types from the specified templates.
   * Directory structure under the source root is mirrored in the output.
   * @param args
   * <ol start="0">
   *  <li>Path to the template source root.</li>
   *  <li>List of paths to template files relative to the specified source root separated by {@link File#pathSeparator}.</li>
   *  <li>Root for generated Java source files.</li>
   *  <li>Boolean value indicating whether the generator should not write generated files in the destination root and rather compare the existing files with the generated ones.
   * </ol>
   * Generated files' extensions are '.java'. To specify another extension, use {@link #OUTPUT_EXT_PROPERTY_KEY} JVM property.
   */
  public static void main(String[] args) {
    if (args.length != 4 && args.length != 3 || args.length == 1 && HELP_STR.contains(args[0])) {
      printUsage();
      System.exit(RET_VAL_HELP);
    }
    System.out.println(Arrays.toString(args));
    File srcRoot = new File(args[0]);
    String[] filesList = args[1].split(File.pathSeparator);
    File destRoot = new File(args[2]);
    String checkOnlyStr = args.length < 4 ? "" : args[3];

    System.out.println("srcRoot = " + srcRoot);
    System.out.println("filesList = " + Arrays.toString(filesList));
    System.out.println("destRoot = " + destRoot);
    System.out.println("checkOnly = " + checkOnlyStr);

    if (!srcRoot.exists()) {
      System.err.println("Source root '" + srcRoot + "' does not exist");
      System.exit(RET_VAL_INPUT_ERR);
    }
    if (!destRoot.mkdirs() && !destRoot.isDirectory()) {
      System.err.println("Cannot write to destination root '" + destRoot + '\'');
      System.exit(RET_VAL_INPUT_ERR);
    }
    boolean checkOnly = Boolean.parseBoolean(checkOnlyStr);

    IntegerCollectionsCodegen generator = new IntegerCollectionsCodegen(srcRoot, destRoot, checkOnly);

    StringBuilder failedFiles = new StringBuilder();
    //noinspection CatchGenericClass
    try {
      for (String file : filesList) {
        try {
          generator.processFile(file);
        } catch (BadFormatException ex) {
          failedFiles.append('\'').append(file).append("': ").append(ex.getMessage()).append(lineSeparator);
        }
      }
    } catch (Exception ex) {
      System.err.println(ex);
      System.exit(RET_VAL_RUNTIME_ERR);
    }
    if (failedFiles.length() > 0) {
      System.err.println("Code " + (checkOnly ? "consistency verification" : "generation") + " failed for the following files: ");
      System.err.println(failedFiles);
      System.exit(RET_VAL_RUNTIME_ERR);
    }
  }

  private static void printUsage() {
    System.out.println("Usage: [src-root] [src-files-list] [dest-root] [check-only], where");
    System.out.println("   [src-root]        path to the template source root");
    System.out.println("   [src-files-list]  template files' extension");
    System.out.println("   [dest-root]       root for generated Java source files");
    System.out.println("   [check-only]      specify 'true' to avoid writing files to the destination. In this case destination files will be checked to be equal to the generated ones.");
  }

  public void processFile(String relPath) throws IOException, BadFormatException {
    File templateFile = new File(mySourceRoot, relPath);
    if (!templateFile.isFile()) throw new IOException("Cannot read '" + templateFile + '\'');
    String templateStr = FileUtil.readFile(templateFile);

    String outFileNameTemplate = identifyOutFileName(templateStr);
    File parent = new File(myDestinationRoot, relPath).getParentFile();
    if (!parent.mkdirs() && !parent.isDirectory()) {
      throw new IOException("Cannot write to folder '" + parent + '\'');
    }

    templateStr = insertGeneratedCodeNotice(relPath, templateStr);

    StringBuilder errors = new StringBuilder();

    boolean isPair = false;
    for (String s : StringSets.F.getStrings()) {
      isPair |= (outFileNameTemplate.contains(s));
    }

    for (TypeDescriptor type : TypeDescriptor.values()) {
      if (isPair != type.isPair()) continue;

      String outFileName = type.apply(outFileNameTemplate);
      String out = type.apply(templateStr);
      File outFile = new File(parent, outFileName + myOutputFileExt);
      if (!myCheckOnly) {
        System.out.println("writing to '" + outFile + '\'');
        FileUtil.writeFile(outFile, out);
      } else if(outFile.isFile()) {
        System.out.println("checking '" + outFile + '\'');
        try {
          checkCurrentMatchesGenerated(outFile, out);
        } catch (BadFormatException ex) {
          if(errors.length() == 0) errors.append("generated files differ from existing files");
          errors.append(lineSeparator).append('\'').append(outFile).append(": ").append(ex.getMessage());
        }
      } else {
        System.out.println("--skipping '" + outFile + '\'');
      }
    }
    if (errors.length() > 0) {
      throw new BadFormatException(errors.toString());
    }
  }

  /**
   * File name is generated via taking the first Java entity declaration (class, interface, annotation inteface, or enum).
   */
  public static String identifyOutFileName(String template) throws BadFormatException {
    String withoutComments = Pattern.compile(javaComments).matcher(template).replaceAll("");
    Matcher entityMatcher = Pattern.compile(javaEntityNameExtractor).matcher(withoutComments);
    if (!entityMatcher.find()) {
      throw new BadFormatException("no valid template for Java class/enum/interface declaration found");
    }
    return entityMatcher.group(1);
  }

  private String insertGeneratedCodeNotice(String relPath, String templateStr) {
    int where = 0;
    Matcher m = Pattern.compile(javaComments).matcher(templateStr);
    if (m.find()) where = m.end();
    String generatedCodeNotice = generatedCodeNoticeTemplate + relPath;
    templateStr = templateStr.substring(0, where) + lineSeparator + lineSeparator + generatedCodeNotice + lineSeparator + templateStr.substring(where);
    return templateStr;
  }

  /**
   * @throws BadFormatException if check failed
   */
  private static void checkCurrentMatchesGenerated(File currentFile, String generatedContent) throws IOException, BadFormatException {
    String currentContent = FileUtil.readFile(currentFile);

    // fix line separators
    generatedContent = generatedContent.replaceAll("\r\n", "\n");
    currentContent = currentContent.replaceAll("\r\n", "\n");

    // these matchers are used to skip the following:
    // 1. import statements
    // 2. whitespace between two import statements
    // 3. comments consisting only of import statements
    Matcher skipImpGen = Pattern.compile(skipImportsRegex).matcher(generatedContent);
    Matcher skipImpCur = Pattern.compile(skipImportsRegex).matcher(currentContent);
    // set regions for the skipping matchers - from the beginning to the start of entity (=class/enum/(@)interface) declaration
    // This approach works because:
    // 1) no imports are allowed in Java after the start of the first type declaration;
    // 2) strings and comments that have nothing to do with imports are not allowed outside type declaration.
    Matcher entityGen = Pattern.compile(javaEntityNameExtractor).matcher(generatedContent);
    if (!entityGen.find()) throw new RuntimeException("template error: should have failed before!");
    skipImpGen.region(0, entityGen.start());
    Matcher entityCur = Pattern.compile(javaEntityNameExtractor).matcher(currentContent);
    if (!entityCur.find()) throw new BadFormatException("no valid template for Java class/enum/interface declaration found in existing file");
    skipImpCur.region(0, entityCur.start());

    // start comparison
    int lG = generatedContent.length();
    char[] vG = new char[lG];
    generatedContent.getChars(0, lG, vG, 0);
    int lC = currentContent.length();
    char[] vC = new char[lC];
    currentContent.getChars(0, lC, vC, 0);
    // true if regions with imports are ahead
    boolean moreImpGen = skipImpGen.find();
    // if no matches: possibly the imports are optimized away while optimizing imports, so match whitespace.
    if (!moreImpGen) {
      skipImpGen = Pattern.compile(skipImportsWhitespaceRegex).matcher(generatedContent);
      moreImpGen = skipImpGen.find();
    }
    boolean moreImpCur = skipImpCur.find();
    if (!moreImpCur) {
      skipImpCur = Pattern.compile(skipImportsWhitespaceRegex).matcher(currentContent);
      moreImpCur = skipImpCur.find();
    }
    // lengths of files without imports
    int lenNoImpGen = 0;
    int lenNoImpCur = 0;
    for (int iG = 0, iC = 0; iG < lG && iC < lC;) {
      // the check
      if(vG[iG] != vC[iC]) {
        Pair<Integer, String> errLineGen = getLastLine(generatedContent.substring(0, iG + 1));
        Pair<Integer, String> errLineCur = getLastLine(currentContent.substring(0, iC + 1));
        // format error message
        String genMsgStart = "    line " + errLineGen.getFirst() + " in generated file '";
        int errPlacePosGen = genMsgStart.length() + errLineGen.getSecond().length();
        String errPlaceGen = String.format("%n%" + errPlacePosGen + "s%n", "^");
        String curMsgStart = "    line " + errLineCur.getFirst() + " in existing file  '";
        int errPlacePosCur = curMsgStart.length() + errLineCur.getSecond().length();
        String errPlaceCur = String.format("%n%" + errPlacePosCur + "s%n", "^");
        throw new BadFormatException(lineSeparator + genMsgStart + errLineGen.getSecond() + errPlaceGen + "  differs from" + lineSeparator + curMsgStart + errLineCur.getSecond() + errPlaceCur);
      }
      // increment and do another check
      ++iG;
      ++iC;
      ++lenNoImpGen;
      ++lenNoImpCur;
      if(iG >= lG && !(iC >= lC) || iC >= lC && !(iG >= lG)) {
        throw new BadFormatException(String.format("in lengths (excluding imports): generated %d symbols, current %d symbols", lenNoImpGen, lenNoImpCur));
      }
      // skip imports if necessary
      if(moreImpGen && iG == skipImpGen.start(1)) {
        iG = skipImpGen.end(1);
        moreImpGen = skipImpGen.find();
      }
      if(moreImpCur && iC == skipImpCur.start(1)) {
        iC = skipImpCur.end(1);
        moreImpCur = skipImpCur.find();
      }
    }
  }

  private static Pair<Integer, String> getLastLine(String s) {
    try {
      int lastLineNum = -1;
      @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"}) // String reader: no need to close
          BufferedReader br = new BufferedReader(new StringReader(s));
      String last = "";
      for (String cur = ""; cur != null; cur = br.readLine()) {
        ++lastLineNum;
        last = cur;
      }
      return Pair.create(lastLineNum, last);
    } catch (IOException e) {
      // is thrown only if StringReader or BufferReader was closed when readLine is called, which is very strange
      throw new Error(e);
    }
  }
}