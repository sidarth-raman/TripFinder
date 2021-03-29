package edu.brown.cs.mramesh4.REPLLoop;

import edu.brown.cs.mramesh4.stars.ActionMethod;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * This class represents a REPL.
 */
public class REPL {

  private Parser inputParser;
  private HashMap<String, ActionMethod<?>> commands;

  /**
   * This is a method to create a REPL.
   * @param commands map of commands to run
   */
  public REPL(HashMap<String, ActionMethod<?>> commands) {
    this.commands = commands;
  }

  /**
   * This is a method to read the lines of the REPL.
   */
  public void read() {

    inputParser = new Parser(new InputStreamReader(System.in));

    while (true) {

      // parses user input
      String[] input = inputParser.parse();

      // EOF condition
      if (input == null) {
        break;
      }

      if (commands.containsKey(input[0])) {
        commands.get(input[0]).run(input);
        //command doesn't exist, throw ERROR
      } else {
        System.err.println("ERROR: Command does not exist.");
      }

    }
  }
}

