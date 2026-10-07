package com.example.rubiks;

import org.springframework.web.bind.annotation.*;

import java.util.*;

// tell spring this class handles web traffic
@RestController
// base url for thus controller
@RequestMapping("/api")
// allows react to talk java
@CrossOrigin(origins = "*")
public class SolverController {

    private final MainSolver solver;

    public SolverController() {
        this.solver = new MainSolver();
    }

    // map a GET request to /api/solve?turns=X
    @GetMapping("/solve")
    public Map<String, List<String>> getSolve(@RequestParam(defaultValue = "5") int turns) {

        Cube cube = new Cube();

        // scramble cube and get list of scramble moves
        List<String> scrambleSequence = randomScramble(turns, cube);

        // solve cube
        List<Integer> solutionInts = solver.solve(cube);
        List<String> solutionString = convertToString(solutionInts);

        // package both lists into a map
        Map<String, List<String>> response = new HashMap<>();
        response.put("scramble", scrambleSequence);
        response.put("solution", solutionString);

        return response;
    }

    // post endpoint that accepts list of strings
    @PostMapping("/solve-custom")
    public Map<String, List<String>> solveCustomScramble(@RequestBody Map<String, List<String>> dataReceived) {

        // ("Sequence", [...])
        // extract array using 'sequence' key
        List<String> scrambleList = dataReceived.get("sequence");
        System.out.println("Data received: "+dataReceived);
        System.out.println("Scramle list: "+scrambleList);

        Cube cube = new Cube();

        for (String moveString : scrambleList) {

            int moveInt = convertToInt(moveString);

            if (moveInt != -1) {
                cube.executeMove(moveInt);
            }
        }

        List<Integer> solutionInts = solver.solve(cube);
        List<String> solutionString = convertToString(solutionInts);

        // return scramble and solution
        Map<String, List<String>> response = new HashMap<>();

        response.put("scramble", scrambleList);
        response.put("solution", solutionString);

        return response;
    }

    @PostMapping("/solve-coloured")
    public Map<String, List<String>> solveColouredCube(@RequestBody Map<String, List<Integer>> dataReceived) {

        // extract the 54 integers
        List<Integer> cubeStateList = dataReceived.get("state");

        // convert List<Integer> into byte
        byte[] cubeStateBytes = new byte[54];

        for (int i = 0; i < 54; i++) {
            cubeStateBytes[i] = cubeStateList.get(i).byteValue();
        }

        Cube cube = new Cube();
        cube.setState(cubeStateBytes);

        List<Integer> solutionInts = solver.solve(cube);
        List<String> solutionString = convertToString(solutionInts);

        Map<String, List<String>> response = new HashMap<>();
        response.put("solution", solutionString);

        return response;
    }
    // function to scramble cube based on user inputted scramble
    private static List<String> scrambleInput(List<Integer> scrambleMoves, Cube cube) {

        for (int move : scrambleMoves) {

            cube.executeMove(move);
        }

        return convertToString(scrambleMoves);
    }

    private static List<String> randomScramble(int turnNum, Cube cube) {

        Random random = new Random();

        List<Integer> integerMoves = new ArrayList<>();

        int lastMove = -1;

        for (int i = 0; i < turnNum; i++) {

            int move;

            // keep picking random move until move is not redundant
            do {
                move = random.nextInt(18);
            }   while (MainSolver.isRedundantMove(move, lastMove));

            cube.executeMove(move);
            integerMoves.add(move);

            lastMove = move;
        }
        return convertToString(integerMoves);
    }

    private static final String[] moves_list = {"F", "F'", "B", "B'", "L", "L'", "R", "R'",
            "U", "U'", "D", "D'", "F2", "B2", "L2", "R2", "U2", "D2"};

    // converts list of integer move into readable strings
    private static List<String> convertToString(List<Integer> movesInt) {

        List<String> stringMoves = new ArrayList<>();

        for (int move : movesInt) {

            stringMoves.add(moves_list[move]);
        }

        return stringMoves;
    }

    // convert a move in string to int
    private int convertToInt(String moveString) {

        for (int i = 0; i < moves_list.length; i++) {
            if (moves_list[i].equals(moveString)) {
                return i;
            }
        }
        return -1;
    }
}
