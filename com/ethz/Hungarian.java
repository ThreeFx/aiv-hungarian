package com.ethz;

class Hungarian {
    final private String[] people;
    final private Assignment[] assignments;
    final private int totalPositions;
    final private int[] offset;
    final private int[][] wishes;
    final private int defaultPenalty;

    final private HungarianSolver solver;
    final private int[][] matrix;

    public Hungarian(String[] people, Assignment[] assignments, int[][] wishes, int defaultPenalty) {
        this.people = people;
        this.assignments = assignments;

        // Determine the total number of positions and the respective offsets
        this.offset = new int[assignments.length];
        int positions = 0;
        for (int i = 0; i < assignments.length; i++) {
            positions += assignments[i].positions;
            offset[i] = positions - assignments[i].positions;
        }
        totalPositions = positions;

        // Wishes are ordered from highest to lowest priority
        // wishes are also 1-indexed. This is corrected here;
        this.wishes = wishes;
        for (int i = 0; i < wishes.length; i++) {
            for (int j = 0; j < wishes[i].length; j++) {
                wishes[i][j] -= 1;
            }
        }
        this.defaultPenalty = defaultPenalty;

        this.matrix = createCostMatrix();
        this.solver = new HungarianSolver(this.matrix);
    }

    // TODO Remove aliasing.
    public void printSolution() {
        System.out.print(solver.showMatrix());
    }

    public void solve() {
        solver.solve();
    }

    /**
     * Creates a simple cost matrix based on the input values.
     */
    private int[][] createCostMatrix() {
        int[][] matrix = new int[people.length][totalPositions];

        // Initialize the matrix with no wishes
        for (int i = 0; i < people.length; i++) {
            for (int j = 0; j < totalPositions; j++) {
                matrix[i][j] = defaultPenalty;
            }
        }

        // TODO respect multiple wishes

        // Set the wishes for every student
        for (int i = 0; i < people.length; i++) {
            for (int j = 0; j < wishes[i].length; j++) {
                int aI = wishes[i][j];
                for (int k = offset[aI]; k < offset[aI] + assignments[aI].positions; k++) {
                    matrix[i][k] = j + 1;
                }
            }
        }

        // Just for testing.
        //matrix = new int[3][3];
        //for (int i = 0; i < 3; i++) {
        //    for (int j = 0; j < 3; j++) {
        //        matrix[i][j] = (i + 1) * (j + 1);
        //    }
        //}

        return matrix;
    }
}

/**
 * Helper class for storing information about Assignments.
 */
class Assignment {
    public final String name;
    public final int positions;

    public Assignment(String name, int positions) {
        this.name = name;
        this.positions = positions;
    }
}
