package com.example.rubiks;

import java.nio.file.Files;
import java.nio.file.Path;

public class Heuristic {

    private byte[] database;

    // load in database
    public Heuristic() {

        try {
            System.out.println("Loading pattern database.");
            database = Files.readAllBytes(Path.of("cornerDB.bin"));
            System.out.println("Database loaded successfully");
        }   catch (Exception e) {
                throw new RuntimeException("Could not find cornerDB.bin", e);
        }
    }

    public int getCost(Cube currentCube) {

        // get hash value of cube
        int hashID = DatabaseGenerator.calculateCornerHash(currentCube);
        // lookup hash value in database to get cost
        byte cost = database[hashID];

        // since depth currently limited to 8, unvisited states = -1
        // if -1 found then cost is at least 9
        if (cost == -1) {
            return 9;
        }

        return cost;
    }
}