package edu.brown.cs.mramesh4.stars;

/**.
 * This is a public interface of action methods
 * @param <T>:
 *           the type of object the method acts on
 */
public interface ActionMethod<T> {
  //runs the command and returns a list for the edu.brown.cs.mramesh4.REPL;

  /**.
   * Method to run a command.
   * @param command Command to run
   */
  void run(String[] command);
}
