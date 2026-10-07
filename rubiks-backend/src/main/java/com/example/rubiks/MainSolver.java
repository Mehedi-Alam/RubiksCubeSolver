package com.example.rubiks;

import java.util.ArrayList;
import java.util.List;

public class MainSolver {

    private final Heuristic heuristic;
    private List<Integer> solutionPath;

    public MainSolver() {
        this.heuristic = new Heuristic();
    }

    // returns list of moves (0-17)
    public List<Integer> solve(Cube scrambledCube) {

        solutionPath = new ArrayList<>();

        // minimum possible moves left
        int threshold = heuristic.getCost(scrambledCube);

        System.out.println("Starting IDA*. Initial threshold: "+threshold);
        long startTime = System.currentTimeMillis();

        // IDA* outer loop
        while (true) {

            // run bounded DFS search
            int nextThreshold = search(scrambledCube, 0, threshold, solutionPath, -1);

            // if search returns -1 then solution is found
            if (nextThreshold == -1) {
                long endTime = System.currentTimeMillis();
                System.out.println("Cube solved in "+(endTime - startTime) + " ms");
                return solutionPath;
            }

            // if search space returns Integer.max_value, the cube is unsolvable
            if (nextThreshold == Integer.MAX_VALUE) {
                System.out.println("Error: cube is unsolvable");
                return null;
            }

            System.out.println("Threshold "+ threshold +" exhausted. Increasing limit to: "+nextThreshold);
            threshold = nextThreshold;
        }
    }

    // recursive DFS helper method
    private int search(Cube cube, int currentMoves, int threshold, List<Integer> path, int lastMove) {

        // estimated cost: f(n) = g(n) + h(n)
        int estimatedTotalCost = currentMoves + heuristic.getCost(cube);

        // if estimate exceeds threshold, cut off branch
        if (estimatedTotalCost > threshold) {

            // return value that triggered cutoff
            return estimatedTotalCost;
        }

        // database says 0 moves remain, and we verify cube is solved
        if (heuristic.getCost(cube) == 0 && isFullySolved(cube)) {

            // -1 meaning successful
            return -1;
        }

        int minOverThreshold = Integer.MAX_VALUE;

        // branch out into the 18 possible moves
        for (int move =  0; move < 18; move++) {

            // avoid redundant move
            if (isRedundantMove(move, lastMove)) {
                continue;
            }

            // clone cube and execute turn
            Cube childCube = cube.clone();
            childCube.executeMove(move);

            // save move in tracking list
            path.add(move);

            // recursion to go deeper in tree, incrementing move count
            int result = search(childCube, currentMoves + 1, threshold, path, move);

            // if child branch found a solution, pass success signal back
            if (result == -1) {
                return -1;
            }

            // keep track of the smallest cost that exceeded current threshold limit
            if (result < minOverThreshold) {
                minOverThreshold = result;
            }

            // backtrack, remove the move from path history if the branch hits dead end
            path.remove(path.size() - 1);
        }
        return minOverThreshold;
    }

    private boolean isFullySolved(Cube cube) {

        return cube.isSolved();
    }

    public static boolean isRedundantMove(int currentMove, int lastMove) {

        // if this is first move then not redundant
        if (lastMove == -1) {
            return false;
        }

        // faces that the moves are turning
        int currentFace = getFace(currentMove);
        int lastFace = getFace(lastMove);

        // move is redundant if both moves turn the same face
        if (currentFace == lastFace) {
            return true;
        }

        return false;
    }

    // helper to return which face the move is on
    private static int getFace(int move) {

        // 4/2 = face 2, 5/2 = face 2 (L & LPrime)
        if (move < 12) {
            return move / 2;
        }

        else {
            return move - 12;
        }
    }
}