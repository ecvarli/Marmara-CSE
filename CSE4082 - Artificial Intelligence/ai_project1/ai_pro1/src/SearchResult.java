// SearchResult.java
// Encapsulates the results of a search operation, including the status, solution, expanded nodes, and time taken.

public class SearchResult {
    private final String status;    // Status of the search (e.g., "A solution found.", "Timeout.")
    private final Node solution;    // The final solution node (if found)
    private final int expandedNodes;    // Total number of nodes expanded during the search
    private final double timeInSeconds; // Total time taken for the search in seconds

    // Constructor to initialize the search result with its details
    public SearchResult(String status, Node solution, int expandedNodes, double timeInSeconds) {
        this.status = status;
        this.solution = solution;
        this.expandedNodes = expandedNodes;
        this.timeInSeconds = timeInSeconds;
    }

    public String getStatus() { return status; }    // Getter for the search status
    public Node getSolution() { return solution; }  // Getter for the solution node (if found)
    public int getExpandedNodes() { return expandedNodes; } // Getter for the total number of expanded nodes
    public double getTimeInSeconds() { return timeInSeconds; }  // Getter for the total time taken for the search in seconds
}   