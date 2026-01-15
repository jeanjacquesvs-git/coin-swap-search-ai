//van Schalkwyk
//Jean
//*******
import java.io.*;
import java.nio.file.*;
import java.util.*;

class State implements Comparable<State> {
    private int hValue; // Heuristic value (misplaced coins)
    private int[] board; // Current board state (array of integers representing the board)
    private State parent; // Parent state to trace the path

    // Constructor to initialize a State object with a given board configuration
    public State(int[] board, State parent) {
        this.board = Arrays.copyOf(board, board.length); // Copy the board state
        this.parent = parent; // Set the parent state
        this.hValue = calculateHeuristic(); // Calculate heuristic value
    }

    // Heuristic function: Count misplaced coins compared to the goal state
    public int calculateHeuristic() {
        int h = 0;
        int[] goal = {'B', 'B', 'B', 0, 'R', 'R', 'R'}; // Goal state (7 elements for 'B', 'R', '0')
        for (int i = 0; i < board.length; i++) {
            if (board[i] != goal[i]) h++; // Increase heuristic if the coin is misplaced
        }
        return h; // Return the heuristic value
    }

    // Generate all valid child states based on possible moves
    public List<State> generateChildren() {
        List<State> children = new ArrayList<>();
        int empty = findEmptyCell(); // Find the position of the empty cell (0)

        // Define possible move directions (left, right, slide, jump)
        int[][] moves = {{-1}, {1}, {-2}, {2}}; // Slide and jump left/right
        for (int[] m : moves) {
            int from = empty + m[0]; // Calculate new position after the move
            if (from >= 0 && from < 7 && board[from] != 0) { // Ensure valid position
                int[] newBoard = Arrays.copyOf(board, board.length); // Create a new board
                newBoard[empty] = newBoard[from]; // Swap the coins
                newBoard[from] = 0; // Place the empty space at the new position
                children.add(new State(newBoard, this)); // Add the child state to the list
            }
        }
        return children; // Return the list of generated child states
    }

    // Find the position of the empty space (represented by 0)
    private int findEmptyCell() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) return i; // Return the index of the empty space
        }
        return -1; // Should never happen if the puzzle is valid
    }

    // Compare states based on their heuristic value (used for sorting in the priority queue)
    public int compareTo(State other) {
        return Integer.compare(this.hValue, other.hValue); // Compare based on heuristic value
    }

    // Get the parent state of this state (for path tracing)
    public State getParent() {
        return parent;
    }

    // Convert the state to a string representation for output
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("h=" + hValue + "\n");
        for (int num : board) {
            sb.append(num == 0 ? '0' : (char) num); // Represent 0 as '0' and others as 'B' or 'R'
        }
        return sb.toString(); // Return the string representation of the state
    }

    // Check if two states are equal (used for comparison in the visited set)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false; // Check if the object is of the same type
        return Arrays.equals(this.board, ((State) o).board); // Compare the board configurations
    }

    // Generate a hash code for the state (used in the visited set)
    @Override
    public int hashCode() {
        return Arrays.hashCode(board); // Generate hash code based on the board configuration
    }
}

public class CoinsPuzzle {
    public static void main(String[] args) throws IOException {
        // Define the file paths for input and output
        String fileA = "C:\\Users\\User\\Desktop\\stateA.txt"; // Path to stateA.txt
        String fileB = "C:\\Users\\User\\Desktop\\stateB.txt"; // Path to stateB.txt

        // Read the first input file (stateA.txt) and solve the puzzle
        State start = readInput(fileA); // Read the board from stateA.txt
        solveAndWriteOutput(start, "outputA.txt", true); // Solve and write the solution to outputA.txt and print to screen

        // Read the second input file (stateB.txt) and solve the puzzle
        start = readInput(fileB); // Read the board from stateB.txt
        solveAndWriteOutput(start, "outputB.txt", true); // Solve and write the solution to outputB.txt and print to screen
    }

    // Read the input file and create the initial State object
    static State readInput(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath)); // Read all lines from the input file
        if (lines.isEmpty()) throw new IOException("Input file is empty"); // Ensure the file is not empty

        String[] tokens = lines.get(0).split(" "); // Split the first line into tokens (space-separated)
        int[] board = new int[tokens.length]; // Create an array to store the board configuration

        // Convert the token strings to integers ('0' as 0, 'B' as 'B' and 'R' as 'R')
        for (int i = 0; i < tokens.length; i++) {
            board[i] = tokens[i].equals("0") ? 0 : tokens[i].charAt(0); // Convert '0' to 0 and 'B', 'R' to their ASCII values
        }
        return new State(board, null); // Return a new State object with the initial board configuration
    }

    // Solve the puzzle and write the solution path to the specified output file and also print to screen
    static void solveAndWriteOutput(State start, String outputFileName, boolean printToScreen) throws IOException {
        PriorityQueue<State> pq = new PriorityQueue<>(); // Priority queue for states (based on heuristic)
        Set<State> visited = new HashSet<>(); // Set to keep track of visited states to avoid revisiting

        pq.add(start); // Add the initial state to the priority queue

        // A* Search loop
        while (!pq.isEmpty()) {
            State current = pq.poll(); // Get the state with the lowest heuristic value
            if (current.calculateHeuristic() == 0) { // If the goal state is reached (h=0)
                writeSolutionToFile(current, outputFileName, printToScreen); // Write the solution path to the output file and print to screen
                return; // Exit the method after writing the solution
            }
            visited.add(current); // Mark the current state as visited
            for (State child : current.generateChildren()) { // Generate child states
                if (!visited.contains(child)) pq.add(child); // Add unvisited child states to the priority queue
            }
        }
    }

    // Write the solution path (from the goal state back to the initial state) to the output file and print to screen
    static void writeSolutionToFile(State goal, String outputFileName, boolean printToScreen) throws IOException {
        List<State> path = new ArrayList<>();
        while (goal != null) {
            path.add(goal); // Add each state to the path (trace the path back to the start)
            goal = goal.getParent(); // Move to the parent state
        }
        Collections.reverse(path); // Reverse the path to show it from start to goal

        // Write the solution path to the output file
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        for (State state : path) {
            writer.write(state.toString()); // Write the state to the file
            writer.newLine(); // Add a newline after each state
            if (printToScreen) {
                System.out.println(state); // Print the state to the screen
            }
        }
        writer.write("Total Moves: " + (path.size() - 1)); // Write the total number of moves
        writer.newLine(); // Add a newline before the total moves
        if (printToScreen) {
            System.out.println("Total Moves: " + (path.size() - 1)); // Print total moves to the screen
        }
        writer.close(); // Close the writer
    }
}
