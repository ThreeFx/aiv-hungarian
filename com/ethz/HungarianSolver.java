package com.ethz;

import java.util.ArrayList;

/**
 * An abstract black box for solving the assignment problem.
 */
class HungarianSolver {
    final private int width;
    final private int height;

    private int[][] matrix;
    private int[][] mask;
    private boolean[] isCoveredRow;
    private boolean[] isCoveredColumn;

    public HungarianSolver(int[][] matrix) {
        this.matrix = matrix;
        this.height = matrix.length;
        this.width = matrix[0].length;

        this.mask = new int[height][width];
        this.isCoveredRow = new boolean[height];
        this.isCoveredColumn = new boolean[width];
    }

    public String showMatrix() {
        String res = "";
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                res += matrix[row][col] + (mask[row][col] == 1 ? "*" : mask[row][col] == 2 ? "'" : " ") + " ";
            }
            res += "\n";
        }
        return res;
    }

    // TODO Refactor so stack space doesn't grow cubic.
    public void solve() {
        eliminateMinimum();
        findZeroes();
        coverColumnsOfStarredZeroes();
    }

    /**
     * For each row, subtract it's minimum value from all elements.
     */
    private void eliminateMinimum() {
        for (int row = 0; row < height; row++) {
            int minimum = Integer.MAX_VALUE;
            for (int col = 0; col < width; col++) {
                minimum = Math.min(minimum, matrix[row][col]);
            }
            for (int col = 0; col < width; col++) {
                matrix[row][col] -= minimum;
            }
        }
    }

    /**
     * Find and star uncovered zeroes in the matrix.
     * If a zero is found, cover both its row and column.
     */
    private void findZeroes() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (isUncoveredZero(row, col)) {
                    mask[row][col] = 1;
                    isCoveredRow[row] = true;
                    isCoveredColumn[col] = true;
                }
            }
        }
        for (int row = 0; row < height; row++) {
            isCoveredRow[row] = false;
        }
        for (int col = 0; col < width; col++) {
            isCoveredColumn[col] = false;
        }
    }

    private void coverColumnsOfStarredZeroes() {
        int independentZeroes = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (mask[row][col] == 1 && !isCoveredColumn[col]) {
                    isCoveredColumn[col] = true;
                    independentZeroes++;
                }
            }
        }
        if (independentZeroes >= width || independentZeroes >= height) {
            // done
            return;
        } else {
            primeZeroes();
        }
    }

    private void primeZeroes() {
        // TODO Check for possible optimization (below).
        // Do while there are still uncovered zeroes.
        while (true) {
            // Find an uncovered zero
            Position position = findUncoveredZero();

            // If none exists
            if (position == null) {
                // We are done; adjust the costs.
                adjustCosts();
                break;
            } else {
                int row = position.row;
                int col = position.col;
                mask[row][col] = 2; // prime the zero

                // Else find a starred zero in the row of the uncovered zero.
                int colOfStarredZero = getColumnOfStarredZeroInRow(row);

                // If it exists, cover its row and uncover its column.
                if (colOfStarredZero > -1) {
                    isCoveredRow[row] = true;
                    isCoveredColumn[colOfStarredZero] = false;
                } else {
                    // Else use the augmenting path algorithm
                    // from the maximal matching problem.
                    augmentPath(position);
                    break;
                }
            }
        }

        //for (int row = 0; row < height; row++) {
        //    for (int col = 0; col < width; col++) {
        //        if (isUncoveredZero(row, col)) {
        //            mask[row][col] = 2; // prime the zero
        //            int column = getColumnOfStarredZeroInRow(row);
        //            if (column > -1) {
        //                isCoveredRow[row] = true;
        //                isCoveredColumn[col] = false;
        //            }
        //        }
        //    }
        //}
    }

    private void augmentPath(Position initial) {
        ArrayList<Position> path = new ArrayList<>();
        path.add(initial);

        int colOfPrimedZero = initial.col;
        int rowOfStarredZero = getRowOfStarredZeroInColumn(colOfPrimedZero);

        while (rowOfStarredZero > -1) {
            // Add the starred zero to the path.
            Position starredZero = new Position(rowOfStarredZero, colOfPrimedZero);
            path.add(starredZero);

            // Find the primed zero in the row of the starred zero.
            // Always exists.
            colOfPrimedZero = getColumnOfPrimedZeroInRow(starredZero.row);
            Position primedZero = new Position(starredZero.row, colOfPrimedZero);
            path.add(primedZero);

            // Set our starting point to the just found primed zero.
            rowOfStarredZero = getRowOfStarredZeroInColumn(colOfPrimedZero);
        }

        modifyPath(path);
        uncoverAll();
        losePrimes();

        coverColumnsOfStarredZeroes();
    }

    private void adjustCosts() {
        // Find the smallest uncovered value in the matrix.
        int minimum = Integer.MAX_VALUE;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (!isCoveredRow[row] && !isCoveredColumn[col]) {
                    minimum = Math.min(minimum, matrix[row][col]);
                }
            }
        }

        // Add it to every covered row and
        // subtract it from every uncovered column
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (isCoveredRow[row]) {
                    matrix[row][col] += minimum;
                }
                if (!isCoveredColumn[col]) {
                    matrix[row][col] -= minimum;
                }
            }
        }

        primeZeroes();
    }

    private void modifyPath(ArrayList<Position> path) {
        for (Position currentZero : path) {
            int row = currentZero.row;
            int col = currentZero.col;

            // Replace by -= 1?
            if (mask[row][col] == 1) {
                mask[row][col] = 0;
            } else if (mask[row][col] == 2) {
                mask[row][col] = 1;
            }
        }
    }

    private void uncoverAll() {
        for (int row = 0; row < height; row++) {
            isCoveredRow[row] = false;
        }
        for (int col = 0; col < width; col++) {
            isCoveredColumn[col] = false;
        }
    }

    private void losePrimes() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (mask[row][col] == 2) {
                    mask[row][col] = 0;
                }
            }
        }
    }

    // TODO Refactor into searching with a mask?
    /**
     * Returns the column index of the primed zero in that row.
     * Returns -1 if none exists.
     */
    private int getColumnOfPrimedZeroInRow(int row) {
        for (int col = 0; col < width; col++) {
            if (mask[row][col] == 2) {
                return col;
            }
        }
        return -1;
    }

    /**
     * Returns the row index of the starred zero in that column.
     * Returns -1 if none exists.
     */
    private int getRowOfStarredZeroInColumn(int col) {
        for (int row = 0; row < height; row++) {
            if (mask[row][col] == 1) {
                return row;
            }
        }
        return -1;
    }

    /**
     * Returns the column index of the starred zero in that row.
     * Returns -1 if none exists.
     */
    private int getColumnOfStarredZeroInRow(int row) {
        for (int col = 0; col < width; col++) {
            if (mask[row][col] == 1) {
                return col;
            }
        }
        return -1;
    }

    private boolean isUncoveredZero(int row, int col) {
        return matrix[row][col] == 0 && !isCoveredRow[row] && !isCoveredColumn[col];
    }

    /**
     * Finds the position of an uncovered zero.
     * Returns its position if existent, null otherwise.
     */
    private Position findUncoveredZero() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (isUncoveredZero(row, col)) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    /**
     * Helper class for returning positions from functions.
     */
    private class Position {
        final private int row;
        final private int col;

        private Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}