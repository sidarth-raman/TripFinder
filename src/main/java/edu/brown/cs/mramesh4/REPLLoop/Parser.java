package edu.brown.cs.mramesh4.REPLLoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses a string input based on a ReaderType specific by the caller.
 */
public class Parser {

  private BufferedReader in;

  /**
   * Insyantiates a parser given a reader Type.
   * @param readerType specified reader type
   * @param <T> generic for reader type
   */
  public <T> Parser(T readerType) {
    in = new BufferedReader((Reader) readerType);
  }

  /**
   * Parses a string acquired from the specified readerType as an array.
   *              (non-positive signifies as many times as possible)
   * @return array of strings, each element being separated by the delimiter in the original string
   */
  public String[] parse() {
    String input = "";
    try {
      input = in.readLine();
    } catch (IOException e) {
      System.out.println("ERROR: I/O error");
    }

    // EOF condition
    if (input == null) {
      return null;
    }
    return this.splitString(input);
  }

  /**
   * Helper method for splitting string. This method is static so it can be used by
   * frontend handlers.
   * @param currLine representing String you want to parse
   * @return String array representing parsed String
   */
  public static String[] splitString(String currLine) {
    List<String> matchList = new ArrayList<String>();
    /**
     * regex for splitting string at spaces excluding quotes
     * see this link: https://stackoverflow.com/questions/366202/regex-for-splitting-a
     * -string-using-space-when-not-surrounded-by-single-or-double/366532
     */
    Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
    Matcher regexMatcher = regex.matcher(currLine);
    while (regexMatcher.find()) {
      matchList.add(regexMatcher.group());
    }
    int size = matchList.size();
    String[] command = new String[size];
    return matchList.toArray(command);
  }
}
