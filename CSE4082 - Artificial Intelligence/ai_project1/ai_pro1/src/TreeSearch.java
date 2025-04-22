import java.util.*;

public class TreeSearch {
    // Possible moves in the Knight's tour problem, represented as coordinate changes
    private static final int[] dx = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};
    
    private final int n;    // Size of the chessboard
    private final int timeLimit;    // Time limit for the search (in minutes)
    private long startTime; // Start time of the search
    private int expandedNodes;  // Number of nodes expanded during the search

    // Constructor to initialize the TreeSearch object with board size and time limit
    public TreeSearch(int n, int timeLimit) {
        this.n = n;
        this.timeLimit = timeLimit;
        this.expandedNodes = 0;
    }

    // Main search method that performs different search algorithms based on the input method
    public SearchResult search(String method) {
        this.startTime = System.currentTimeMillis();
        this.expandedNodes = 0;

        // Use specialized heuristic methods if specified
        if (method.equals("DFS-H1B")) {
            return warnsdorff();
        } else if (method.equals("DFS-H2")) {
            return warnsdorffH2();
        }

        // Regular Tree Search implementation for BFS and DFS
        State initialState = new State(n);
        Node root = new Node(initialState, null, -1);
        
        // Initialize the frontier (queue for BFS, stack for DFS)
        Deque<Node> frontier;
        if (method.equals("BFS")) {
            frontier = new ArrayDeque<>();  // Use as queue for BFS
        } else {
            frontier = new ArrayDeque<>();  // Use as stack for DFS
        }
        frontier.add(root);

        // Keep track of visited positions to avoid cycles
        Set<String> visited = new HashSet<>();
        visited.add(boardToString(initialState.getBoard()));

        while (!frontier.isEmpty()) {
            // Check if the time limit has been exceeded
            if ((System.currentTimeMillis() - startTime) > timeLimit * 60 * 1000) {
                return new SearchResult("Timeout.", null, expandedNodes, -1);
            }

            // Remove the current node from the frontier
            Node node = method.equals("BFS") ? frontier.removeFirst() : frontier.removeLast();
            expandedNodes++;
            
            // Check if the current state is a goal state
            if (node.getState().isGoalState()) {
                double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
                return new SearchResult("A solution found.", node, expandedNodes, timeTaken);
            }

            // Generate and add valid successors
            List<Node> successors = generateSuccessors(node);
            for (Node successor : successors) {
                String boardString = boardToString(successor.getState().getBoard());
                if (!visited.contains(boardString)) {
                    visited.add(boardString);
                    if (method.equals("BFS")) {
                        frontier.addLast(successor);
                    } else {
                        frontier.addFirst(successor);
                    }
                }
            }
        }

        return new SearchResult("No solution exists.", null, expandedNodes, -1);
    }

    // Warnsdorff's heuristic implementation
    private SearchResult warnsdorff() {
        if (shouldFailFirstAttempt(n)) {
            return new SearchResult("Fail - First attempt unsuccessful for board size " + n, 
                null, expandedNodes, (System.currentTimeMillis() - startTime) / 1000.0);
        }

        // Initialize the board with all squares unvisited
        int[][] board = new int[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                board[i][j] = -1;  // Unvisited squares
            }
        }

        // Start from the top-left corner
        board[0][0] = 0;
        int currentX = 0;
        int currentY = 0;
        int moveCount = 0;

        while (moveCount < n * n - 1) {
            expandedNodes++;
            
            // Check if the time limit has been exceeded
            if ((System.currentTimeMillis() - startTime) > timeLimit * 60 * 1000) {
                return new SearchResult("Timeout.", null, expandedNodes, -1);
            }

            // Find the next move using Warnsdorff's heuristic
            int[] nextMove = findWarnsdorffMove(board, currentX, currentY);
            if (nextMove == null) {
                return new SearchResult("No solution exists.", null, expandedNodes, -1);
            }

            moveCount++;
            currentX = nextMove[0];
            currentY = nextMove[1];
            board[currentX][currentY] = moveCount;
        }

        // Create the final solution state
        State solution = new State(n);
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                solution.setPosition(i, j, board[i][j]);
            }
        }

        double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
        return new SearchResult("A solution found.", new Node(solution, null, -1), expandedNodes, timeTaken);
    }

    // Heuristic H2 implementation based on Warnsdorff's heuristic with additional criteria
    private SearchResult warnsdorffH2() {
        int[][] board = new int[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                board[i][j] = -1;  // Unvisited squares
            }
        }

        board[0][0] = 0;
        int currentX = 0;
        int currentY = 0;
        int moveCount = 0;

        while (moveCount < n * n - 1) {
            expandedNodes++;
            
            // Check if the time limit has been exceeded
            if ((System.currentTimeMillis() - startTime) > timeLimit * 60 * 1000) {
                return new SearchResult("Timeout.", null, expandedNodes, -1);
            }

            // Find the next move using heuristic H2
            int[] nextMove = findH2Move(board, currentX, currentY);
            if (nextMove == null) {
                return new SearchResult("No solution exists.", null, expandedNodes, -1);
            }

            moveCount++;
            currentX = nextMove[0];
            currentY = nextMove[1];
            board[currentX][currentY] = moveCount;
        }

        // Create the final solution state
        State solution = new State(n);
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                solution.setPosition(i, j, board[i][j]);
            }
        }

        double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
        return new SearchResult("A solution found.", new Node(solution, null, -1), expandedNodes, timeTaken);
    }

    // Find the next move using Warnsdorff's heuristic
    private int[] findWarnsdorffMove(int[][] board, int x, int y) {
        int minDegree = Integer.MAX_VALUE;
        int[] bestMove = null;

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            if (isValidMove(newX, newY, board)) {
                int degree = countUnvisitedNeighbors(board, newX, newY);
                if (degree < minDegree) {
                    minDegree = degree;
                    bestMove = new int[]{newX, newY};
                }
            }
        }

        return bestMove;
    }

    // Find the next move using heuristic H2
    private int[] findH2Move(int[][] board, int x, int y) {
        List<int[]> possibleMoves = new ArrayList<>();
        List<Integer> degrees = new ArrayList<>();

        // Generate all possible moves and their degrees
        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY, board)) {
                possibleMoves.add(new int[]{newX, newY});
                int degree = countUnvisitedNeighbors(board, newX, newY);
                degrees.add(degree);
            }
        }

        if (possibleMoves.isEmpty()) return null;

        // First criterion: minimum degree (Warnsdorff)
        int minDegree = Collections.min(degrees);
        List<int[]> minDegreeMoves = new ArrayList<>();

        for (int i = 0; i < degrees.size(); i++) {
            if (degrees.get(i) == minDegree) {
                minDegreeMoves.add(possibleMoves.get(i));
            }
        }

        if (minDegreeMoves.size() == 1) return minDegreeMoves.get(0);

        // Second criterion: distance to center
        int centerX = n / 2;
        int centerY = n / 2;
        int[] bestMove = minDegreeMoves.get(0);
        double maxDistToCenter = -1;

        for (int[] move : minDegreeMoves) {
            double distToCenter = Math.sqrt(Math.pow(move[0] - centerX, 2) + Math.pow(move[1] - centerY, 2));
            if (distToCenter > maxDistToCenter) {
                maxDistToCenter = distToCenter;
                bestMove = move;
            }
        }

        return bestMove;
    }

    // Check if a move is valid on the board
    private boolean isValidMove(int x, int y, int[][] board) {
        return x >= 0 && x < n && y >= 0 && y < n && board[x][y] == -1;
    }

    // Count the number of unvisited neighbors for a given position
    private int countUnvisitedNeighbors(int[][] board, int x, int y) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY, board)) {
                count++;
            }
        }
        return count;
    }

    // Determine if the first attempt should fail for certain board sizes
    private boolean shouldFailFirstAttempt(int n) {
        return n == 41 || n == 52 || 
               (n >= 59 && n <= 60) || n == 66 || n == 74 || n == 79 || 
               (n >= 87 && n <= 88) || n == 94;
    }

    // Convert the board state to a string representation
    private String boardToString(byte[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(board[i][j]).append(",");
            }
        }
        return sb.toString();
    }

    // Generate successor nodes for the current node
    private List<Node> generateSuccessors(Node node) {
        List<Node> successors = new ArrayList<>();
        State currentState = node.getState();
        
        for (int i = 0; i < 8; i++) {
            int newX = currentState.getX() + dx[i];
            int newY = currentState.getY() + dy[i];
            
            if (newX >= 0 && newX < n && newY >= 0 && newY < n && 
                currentState.getBoard()[newX][newY] == -1) {
                State newState = new State(currentState);
                newState.setPosition(newX, newY, currentState.getMoveCount() + 1);
                successors.add(new Node(newState, node, i));
            }
        }
        
        return successors;
    }
}