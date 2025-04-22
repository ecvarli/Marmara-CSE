// Represents the state of a board during the search process.

public class State {
    private final byte[][] board;   // 2D array representing the board state
    private int x, y;  // Current knight position
    private int moveCount;  // Number of moves made so far
    private final int n;     // Board size

    // Constructor to initialize the board state
    public State(int n) {
        this.n = n;
        this.board = new byte[n][n];
        this.x = 0; // Starting x-coordinate
        this.y = 0; // Starting y-coordinate
        this.moveCount = 0; // Initialize move count

        // Initialize board with -1 (unvisited)
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                this.board[i][j] = -1;
            }
        }
        // Set starting position
        this.board[0][0] = 0;
    }

    // Copy constructor
    public State(State other) {
        this.n = other.n;
        this.x = other.x;
        this.y = other.y;
        this.moveCount = other.moveCount;
        this.board = new byte[n][n];
        for(int i = 0; i < n; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, n);
        }
    }

    // Check if the current state is the goal state (all squares visited)
    public boolean isGoalState() {
        return moveCount == (n * n - 1);
    }

    // Getters for state properties
    public int getX() { return x; }
    public int getY() { return y; }
    public int getMoveCount() { return moveCount; }
    public byte[][] getBoard() { return board; }
    public int getN() { return n; }

    // Set a new position for the knight and update the move count
    public void setPosition(int x, int y, int moveNumber) {
        this.x = x;
        this.y = y;
        this.board[x][y] = (byte)moveNumber;
        if (moveNumber >= 0) {  // -1 for initialization, only update if a valid move is made
            this.moveCount = moveNumber;
        }
    }
}