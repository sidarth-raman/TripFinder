package edu.brown.cs.mramesh4.TripGraph;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This is a class to take a double matrix of costs and make assignments
 * between the rows and cols.
 *
 *
 * The class draws heavy inspiration from multiple sources, that primarily deal with integer
 * examples of hungarian algorithms.
 *   1) https://www.youtube.com/watch?v=SAPG2T4Jbok: This is not a code
 *   base but rather a video that explains steps of the hungarian algorithm.
 *   Online implementations of the Hungarian algorithm: including
 *      https://www.sanfoundry.com/java-program-implement-hungarian-algorithm-bipartite-matching/https://github.com/aalmi/HungarianAlgorithm
 *
 */
public class MinMatchCostMatrix {
  private double[][] matrix; // this is a cost matrix
  // these are the markers required for the hungarian algorithm.
  private int[] squareR, squareC, rowIsCovered, colIsCovered, staredZeroesInRow;

  /**
   * This is a constructor for the minCostMatrix, we will build the identifiers.
   * needed to run the hungarian algorithm
   * @param matrix a cost matrix of doubles
   */
  public MinMatchCostMatrix(double[][]matrix) {
    this.matrix = matrix;
    squareR = new int[matrix.length];
    squareC = new int[matrix[0].length];

    rowIsCovered = new int[matrix.length];
    colIsCovered = new int[matrix[0].length];
    staredZeroesInRow = new int[matrix.length];
    Arrays.fill(staredZeroesInRow, -1);
    Arrays.fill(squareR, -1);
    Arrays.fill(squareC, -1);
  }

  /**
   * This fills a new matrix with optimal assignments between the two.
   * This is the Hungarian algorithm, and mostly utilizes what was explained
   * in the Youtube video.
   *
   * @return optimal assignment
   */
  public int[][] runHungerAlgo() {

    //these are the 1st 3 steps of the hungarian algorithm

    //get rid of the min
    step1();

    //mark the zeroes in the graph
    step2();

    //mark covered zeroes
    step3();

    //this allows us to jump steps
    while (!allColumnsAreCovered()) {
      int[] mainZero = step4();
      while (mainZero == null) {
        step7();
        mainZero = step4();
      }
      if (squareR[mainZero[0]] == -1) {
        step6(mainZero);
        step3();
      } else {
        rowIsCovered[mainZero[0]] = 1;
        colIsCovered[squareR[mainZero[0]]] = 0;
        step7();
      }
    }

    int[][] optimalAssignment = new int[matrix.length][];
    for (int i = 0; i < squareC.length; i++) {
      optimalAssignment[i] = new int[]{i, squareC[i]};
    }
    return optimalAssignment;
  }

  /**
   * Check if all columns are covered. If that's the case then the
   * optimal solution is found
   *
   * @return boolean
   */
  private boolean allColumnsAreCovered() {
    for (int i : colIsCovered) {
      if (i == 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Step 1: This is step1 of the Hungarian algorithm it does the following:
   * 1. subtract each row minima from each element of the row
   * 2. subtract each column minima from each element of the column
   */
  public void step1() {
    // rows
    for (int i = 0; i < matrix.length; i++) {
      // find the min value of the current row
      double currentRowMin = Integer.MAX_VALUE;
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] < currentRowMin) {
          currentRowMin = matrix[i][j];
        }
      }
      // subtract min value from each element of the current row
      for (int k = 0; k < matrix[i].length; k++) {
        matrix[i][k] -= currentRowMin;
      }
    }

    // cols
    for (int i = 0; i < matrix[0].length; i++) {
      // find the min value of the current column
      double currentColMin = Integer.MAX_VALUE;
      for (int j = 0; j < matrix.length; j++) {
        if (matrix[j][i] < currentColMin) {
          currentColMin = matrix[j][i];
        }
      }
      // subtract min value from each element of the current column
      for (int k = 0; k < matrix.length; k++) {
        matrix[k][i] -= currentColMin;
      }
    }
  }

  /**
   * Step 2 of the Hungarian Algorithm.
   * mark each 0 with a "square"
   */
  public void step2() {
    int[] rowHasSquare = new int[matrix.length];
    int[] colHasSquare = new int[matrix[0].length];

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix.length; j++) {
        // mark if current value == 0 & there are no other marked zeroes in the same row or column
        if (matrix[i][j] == 0 && rowHasSquare[i] == 0 && colHasSquare[j] == 0) {
          rowHasSquare[i] = 1;
          colHasSquare[j] = 1;
          squareR[i] = j;
          squareC[j] = i;
          continue;
        }
      }
    }
  }

  /**
   * Step 3:
   * Cover all columns which are marked with a "square".
   */
  public void step3() {
    for (int i = 0; i < squareC.length; i++) {
      colIsCovered[i] = squareC[i] != -1 ? 1 : 0;
    }
  }

  /**
   * Step 7:
   * 1. Find the smallest uncovered value in the matrix.
   * 2. Subtract it from all uncovered values
   * 3. Add it to all twice-covered values
   */
  public void step7() {
    // Find the smallest uncovered value in the matrix
    double minUncoveredValue = Integer.MAX_VALUE;
    for (int i = 0; i < matrix.length; i++) {
      if (rowIsCovered[i] == 1) {
        continue;
      }
      for (int j = 0; j < matrix[0].length; j++) {
        if (colIsCovered[j] == 0 && matrix[i][j] < minUncoveredValue) {
          minUncoveredValue = matrix[i][j];
        }
      }
    }

    if (minUncoveredValue > 0) {
      for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix[0].length; j++) {
          if (rowIsCovered[i] == 1 && colIsCovered[j] == 1) {
            // Add min to all twice-covered values
            matrix[i][j] += minUncoveredValue;
          } else if (rowIsCovered[i] == 0 && colIsCovered[j] == 0) {
            // Subtract min from all uncovered values
            matrix[i][j] -= minUncoveredValue;
          }
        }
      }
    }
  }

  /**
   * Step 4:
   * Find the main zero within the matrix.
   *
   * @return position of mainZero.
   */
  public int[] step4() {
    for (int i = 0; i < matrix.length; i++) {
      if (rowIsCovered[i] == 0) {
        for (int j = 0; j < matrix[i].length; j++) {
          if (matrix[i][j] == 0 && colIsCovered[j] == 0) {
            staredZeroesInRow[i] = j; // mark as 0*
            return new int[]{i, j};
          }
        }
      }
    }
    return null;
  }

  /**
   * Step 6:
   * Create a chain of alternating "squares" and Z0.
   *
   * @param mainZero  what we got from step 4
   */
  public void step6(int[] mainZero) {
    int i = mainZero[0];
    int j = mainZero[1];

    Set<int[]> k = new LinkedHashSet<>();
    k.add(mainZero);
    boolean found = false;
    do {

      if (squareC[j] != -1) {
        k.add(new int[]{squareC[j], j});
        found = true;
      } else {
        found = false;
      }

      if (!found) {
        break;
      }

      i = squareC[j];
      j = staredZeroesInRow[i];

      if (j != -1) {
        k.add(new int[]{i, j});
        found = true;
      } else {
        found = false;
      }

    } while (found);


    for (int[] zero : k) {

      if (squareC[zero[1]] == zero[0]) {
        squareC[zero[1]] = -1;
        squareR[zero[0]] = -1;
      }

      if (staredZeroesInRow[zero[0]] == zero[1]) {
        squareR[zero[0]] = zero[1];
        squareC[zero[1]] = zero[0];
      }
    }

    Arrays.fill(staredZeroesInRow, -1);
    Arrays.fill(rowIsCovered, 0);
    Arrays.fill(colIsCovered, 0);
  }



}
