package com.ethz;

/**
 * An abstract black box for solving the assignment problem.
 */
class Hungarian {
    final private String[] people;
    final private String[] assignments;
    final private int[][] wishes;
    final int width;
    final int height;

    private int[][] matrix;

    public Hungarian(String[] people, String[] assignments, int[][] wishes) {
        this.people = people;
        this.assignments = assignments;
        this.wishes = wishes;
        this.height = people.length;
        this.width = assignments.length;

        this.matrix = new int[height][width];
    }

    public void solve() {

    }

    // Maybe add printstream params?
    public void output() {

    }
}
