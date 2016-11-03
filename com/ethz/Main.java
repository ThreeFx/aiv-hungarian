package com.ethz;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        String[] people = readFile(args[0]);
        // --------------------------------------- ask about max
        Assignment[] assignments = readAssignments(args[1], 2);
        int[][] wishes = Arrays.stream(readFile(args[2])).map(line -> parseWishlist(" ", line)).toArray(int[][]::new);

        // TODO Some projects allow more than one student.
        //      Solution: create multiple columns per assignment.
        //      Of course, this has a global maximum.
        //      Consult Jonathan which maximum.



        // ------------------------------------------------------- ask about penalty
        Hungarian problem = new Hungarian(people, assignments, wishes, 1000);
        problem.solve();
        problem.printSolution();
    }

    private static String[] readFile(String fileName) throws FileNotFoundException {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(fileReader);
        return reader.lines().toArray(String[]::new);
    }

    private static Assignment[] readAssignments(String fileName, int maximum) throws FileNotFoundException {
        // Split each line into 'title' positions
        // TODO Change separator
        String[][] lines = Arrays.stream(readFile(fileName)).map(x -> x.split(" ")).toArray(String[][]::new);
        Assignment[] assignments = new Assignment[lines.length];
        // Determine the number of positions for every assignment
        // If none is found, use default maximum.
        for (int i = 0; i < lines.length; i++) {
            int numberOfStudents = 0;
            try {
                numberOfStudents = Integer.parseInt(lines[i][1]);
            } catch (Exception e) {
                System.out.println("[WARNING]: Could not determine number of students for " + lines[i][0] + ", setting " + maximum);
            } finally {
                numberOfStudents = numberOfStudents == 0 ? maximum : numberOfStudents;
                assignments[i] = new Assignment(lines[i][0], numberOfStudents);
            }
        }
        return assignments;
    }

    private static int[] parseWishlist(String separator, String input) {
        String[] numbers = input.split(separator);
        int[] result = new int[numbers.length];
        for (int i = 0; i < result.length; i++)
            result[i] = Integer.parseInt(numbers[i]);
        return result;
    }
}
