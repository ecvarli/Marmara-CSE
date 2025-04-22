import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length != 3) {
            // Print usage instructions if arguments are missing or incorrect
            System.out.println("Usage: java Main <board-size> <search-method> <time-limit>");
            System.out.println("Search methods: BFS, DFS, DFS-H1B, DFS-H2");
            return;
        }

        int n = Integer.parseInt(args[0]);  // Parse the board size from the first argument
        String method = args[1];    // Parse the search method from the second argument
        int timeLimit = Integer.parseInt(args[2]);  // Parse the time limit from the third argument

        TreeSearch search = new TreeSearch(n, timeLimit);   // Initialize the TreeSearch class with the provided board size and time limit
        SearchResult result = search.search(method);    // Execute the search using the specified method

        // Print results
        System.out.println("Search Method: " + method);
        System.out.println("Time Limit: " + timeLimit + " minutes");
        System.out.println("Status: " + result.getStatus());
        System.out.println("Nodes Expanded: " + result.getExpandedNodes());

        // If a solution is found, print the time taken and the solution board
        if (result.getStatus().equals("A solution found.")) {
            System.out.println("Time taken: " + result.getTimeInSeconds() + " seconds");
            showSolutionWindow(result.getSolution());
        }
    }

    // Method to print the solution board
    private static void showSolutionWindow(Node solution) {
        State state = solution.getState();  // Retrieve the solution state from the solution node
        byte[][] board = state.getBoard();  // Get the board from the state object
        int n = state.getN();   // Get the board size

        // Create data model for JTable
        String[] columnNames = new String[n];
        for (char c = 'a'; c < 'a' + n; c++) {
            columnNames[c - 'a'] = String.valueOf(c);
        }
        Object[][] data = new Object[n][n];
        for (int y = n - 1; y >= 0; y--) {
            for (int x = 0; x < n; x++) {
                data[n - 1 - y][x] = board[x][y];
            }
        }

        // Create JTable
        JTable table = new JTable(data, columnNames);

        // Create JFrame and add the table
        JFrame frame = new JFrame("Solution");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JScrollPane(table));
        frame.pack();
        frame.setVisible(true);
    }
}