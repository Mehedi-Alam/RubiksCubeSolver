# Rubik's Cube Solver

A full-stack web application that provides an interactive Rubik's Cube interface and calculates the optimal solution from any scrambled state using advanced search algorithms.

## Project Overview

This project bridges a React-based frontend with a powerful Java backend to solve a Rubik's Cube. Users can manipulate a digital cube in the browser, scramble it, and request a solution. The backend computes the most efficient path to the solved state and returns the sequence of moves to the user.

## Tech Stack

* **Frontend:** React (Vite)
* **Backend:** Java Spring Boot
* **Algorithms:** Iterative Deepening A* (IDA*), Depth-First Search (DFS)
* **Optimization:** Pattern Database Heuristics

## How It Works

1. **State Capture:** The React frontend maintains the current configuration of the cube and sends this state to the backend via a REST API endpoint.
2. **Heuristic Search:** The Java backend receives the scrambled state and initiates an Iterative Deepening A* (IDA*) search. 
3. **Pattern Databases:** To keep the search time extremely low, the algorithm relies on pre-computed pattern databases (such as `cornerDB.bin`). These act as lookup tables that instantly tell the algorithm the minimum number of moves required to solve specific subsets of the cube, dramatically pruning the search tree.
4. **Resolution:** Once the optimal move sequence is found, it is sent back to the frontend, allowing the user to follow the steps to solve the cube.

*Note: The `cornerDB.bin` heuristic lookup table is generated locally on the first run rather than tracked in version control, as it exceeds standard Git file size limits.*

## Local Setup

To run this project locally, you will need two terminal windows to run the frontend and backend concurrently.

### 1. Backend (Java Spring Boot)
Navigate to the backend directory and start the Spring Boot server:
```bash
cd rubiks
./mvnw spring-boot:run
