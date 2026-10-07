package com.example.rubiks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class DatabaseGenerator {

    // number of all possible corner states
    private static final int DATABASE_SIZE = 88179840;

    // array to hold state scores
    private static byte[] cornerDatabase = new byte[DATABASE_SIZE];

    public static void main(String[] args) {

        System.out.println("Generating database.");

        long startTime = System.currentTimeMillis();

        // fill database with -1 (unvisited)
        for (int i = 0; i < DATABASE_SIZE; i++) {
            cornerDatabase[i] = -1;
        }

        runBFS();

        saveDatabaseToFile();

        long endTime = System.currentTimeMillis();
        System.out.println("Finished in "+ (endTime - startTime) / 1000 + " seconds.");
    }

    private static void runBFS() {

        // holds cubes to explore next
        Queue<Cube> queue = new LinkedList<>();

        // start with solved cube
        Cube solvedCube = new Cube();

        // hash cube to get its unique ID
        int startHash = calculateCornerHash(solvedCube);

        // solved state takes 0 moves to solve
        cornerDatabase[startHash] = 0;
        queue.add(solvedCube);

        int statesVisited = 1;
        int currentDepth = 0;

        // BFS loop
        while (!queue.isEmpty()) {

            Cube currentCube = queue.poll();
            int currentHash = calculateCornerHash(currentCube);
            byte depthOfCurrent = cornerDatabase[currentHash];

            // print progress
            if (depthOfCurrent > currentDepth) {
                currentDepth = depthOfCurrent;
                System.out.println("Exploring depth "+ currentDepth + " (States found: "+statesVisited + ")");
            }

            // limit depth to 8 for now
            if (depthOfCurrent == 8) {
                continue;
            }

            // try all 18 moves on current cube
            for (int move = 0; move < 18; move++) {

                // clone cube to not permanently change cube in queue
                Cube childCube = currentCube.clone();
                childCube.executeMove(move);

                int childHash = calculateCornerHash(childCube);

                // if == -1, then its first time this state is reached
                // so it's the shortest path
                if (cornerDatabase[childHash] == -1) {

                    // record cost = parents depth + 1
                    cornerDatabase[childHash] = (byte) (depthOfCurrent + 1);

                    queue.add(childCube);
                    statesVisited++;
                }
            }
        }
        System.out.println("BFS complete. Total states evaluated: "+ statesVisited);
    }

    // gives a cube state a unique ID
    public static int calculateCornerHash(Cube cube) {

        // get the 8 corners
        byte[][] corners = cube.getCorners();

        int orientation = calculateOrientation(corners);

        int permutation = calculatePermutation(corners);

        // combine into one unique ID
        // 2187 = max num of orientations (3^7)
        // multiply permutation by 2187 to prevent overlapping
        return (permutation * 2187) + orientation;
    }

    private static int calculateOrientation(byte[][] corners) {

        int orientationTotal = 0;

        // loop through the 7 corners (ignore 8th)
        for (int i = 0; i < 7; i++) {

            // returns 0,1,2 based on orientation
            int twist = getCornerTwist(corners[i]);

            // multiply twist num by 3^(6-i)
            orientationTotal += twist * (int) Math.pow(3, 6 - i);
        }
        return orientationTotal;
    }

    // looks for the position of white/yellow and returns 0,1,2 based on orientation
    private static int getCornerTwist(byte[] corner) {

        // is white(0) or yellow(5) already in correct spot? (up/down)
        // already solved
        if (corner[0] == 0 || corner[0] == 5) {
            return 0;
        }

        // if in second slot then its rotated clockwise
        else if (corner[1] == 0 || corner[1] == 5) {
            return 1;
        }

        // else its rotated counter-clockwise
        else {
            return 2;
        }
    }

    private static int calculatePermutation(byte[][] corners) {

        // array of corners
        int[] pieces = new int[8];

        for (int i = 0; i < 8; i++) {

            // assign each corner its correct position (0-7)
            // if pieces[0] = 2 then corner is currently in slot 0 but belongs in slot 2
            pieces[i] = identifyCornerPiece(corners[i]);
        }

        // pre-calculate factorials
        int[] factorials = {1, 1, 2, 6, 24, 120, 720, 5040};

        int permutationTotal = 0;

        // lehmer code loop
        for (int i = 0; i < 7; i++) { // loop through the 7 corners

            int smallerCount = 0;

            // count how many pieces to the right are smaller than current piece
            for (int j = i+1; j < 8; j++) {

                if (pieces[j] < pieces[i]) {
                    smallerCount++;
                }
            }

            // multiply by factorial of reverse index
            permutationTotal += smallerCount * factorials[7 - i];
        }
        return permutationTotal;
    }

    private static int identifyCornerPiece(byte[] corner) {

        // combine the 3 colours of the corner piece into one number
        int bitmask = (1 << corner[0]) | (1 << corner[1]) | (1 << corner[2]);

        // match unique integer to correct corner position (0-7)
        return switch (bitmask) {
            case 19 -> 0; // up-left-back (white-orange-blue) = 1+2+16=19
            case 25 -> 1;
            case 7 -> 2;
            case 13 -> 3;
            case 50 -> 4;
            case 56 -> 5;
            case 38 -> 6;
            case 44 -> 7;
            default -> throw new IllegalArgumentException("Invalid corner piece. Bitmask: " + bitmask);
        };
    }
    private static void saveDatabaseToFile() {
        System.out.println("Writing database to storage.");

        try (FileOutputStream fos = new FileOutputStream("cornerDB.bin")) {
            fos.write(cornerDatabase);
            System.out.println("Successfully saved to cornerDB.bin");
        }   catch (IOException e) {
            System.err.println("Failed to write file: " + e.getMessage());
        }
    }
}