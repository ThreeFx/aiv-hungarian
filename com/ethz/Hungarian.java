package com.ethz;

/**
 * An abstract black box for solving the assignment problem.
 */
class Hungarian {
    final private String[] people;
    final private String[] assignments;
    final private int[][] wishes;
    final private int defaultPenalty;

    private int[][] matrix;

    public Hungarian(String[] people, String[] assignments, int[][] wishes, int defaultPenalty) {
        this.people = people;
        this.assignments = assignments;
        // Wishes are ordered from highest to lowest priority
        this.wishes = wishes;
        this.defaultPenalty = defaultPenalty;
        this.height = people.length;
        this.width = assignments.length;

        createCostMatrix();
    }

    class HungarianSolve {
        final private int width;
        final private int height;

        private int[][] matrix;
        private int[][] mask;
        private boolean[] isCoveredRow;
        private boolean[] isCoveredColumn;

        public HungarianSolve(int[][] matrix) {
            this.matrix = matrix;
            this.height = matrix.length;
            this.width = matrix[0].length;

            this.mask = new int[height][width];
            this.isCoveredRow = new boolean[height];
            this.isCoveredColumn = new boolean[width];

            solve();
        }

        private void solve() {
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
         * Find and mark zeroes in the matrix.
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
                    if (mask[row][col] == 1) {
                        isCoveredColumn[col] = true;
                        independentZeroes++;
                    }
                }
            }
            if (independentZeroes >= width || independentZeroes >= height) {
                // we are done
            } else {
                primeZeroes();
            }
        }

        private void primeZeroes() {
            boolean allZeroesCovered = false;
            while (!allZeroesCovered) {
                Position position = findUncoveredZero();
                if (position != null) {
                    int row = position.x;
                    int col = position.y;
                    mask[row][col] = 2; // prime the zero

                    int colOfStarredZero = getColumnOfStarredZeroInRow(row);
                    if (colOfStarredZero > -1) {
                        isCoveredRow[row] = true;
                        isCoveredColumn[col] = false;
                    } else {
                        augmentPath(row, col);
                    }
                } else {
                    allZeroesCovered = true;
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

        private void augmentPath(int row, int col) {
            // TODO
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

        public int[][] getSolution() {
            return matrix;
        }

        /**
         * Helper class for returning positions from functions.
         */
        class Position {
            final public int x;
            final public int y;

            public Position(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }
    }

    public void solve() {

    }

    // Maybe add printstream params?
    public void output() {

    }

    /**
     * Creates a simple cost matrix based on the input values.
     */
    private int[][] createCostMatrix() {
        int[][] matrix = new int[height][width];

        // Initialize the matrix with no wishes
        for (int i = 0; i < people.length; i++) {
            for (int j = 0; j < assignments.length; j++) {
                matrix[i][j] = defaultPenalty;
            }
        }

        // Set the wishes for every student
        for (int i = 0; i < people.length; i++) {
            for (int j = 0; j < wishes[i].length; j++) {
                matrix[i][wishes[i][j]] = j + 1;
            }
        }

        return matrix;
    }
}
