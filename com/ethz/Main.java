package com.ethz;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        String[] people = readFile(args[0]);
        String[] assignments = readFile(args[1]);
        int[][] wishes = Arrays.stream(readFile(args[2])).map(line -> makeIntList(" ", line)).toArray(int[][]::new);

        // TODO Some projects allow more than one student.
        //      Solution: create multiple columns per assignment.
        //      Of course, this has a global maximum.
        //      Consult Jonathan which maximum.

        // ------------------------------------------------------- ask about penalty
        Hungarian problem = new Hungarian(people, assignments, wishes, 1000);
        problem.solve();
        problem.output();
    }

    private static String[] readFile(String fileName) throws FileNotFoundException {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(fileReader);
        return reader.lines().toArray(String[]::new);
    }

    private static int[] makeIntList(String separator, String input) {
        String[] numbers = input.split(separator);
        int[] result = new int[numbers.length];
        for (int i = 0; i < result.length; i++)
            result[i] = Integer.parseInt(numbers[i]);
        return result;
    }
}
