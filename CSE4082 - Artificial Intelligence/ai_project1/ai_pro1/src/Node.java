// Node.java
public class Node {
    private final State state;  // The current state represented by this node
    private final Node parent;  // Reference to the parent node in the tree
    private final int moveDirection;  // Direction of the move that led to this state
    private final int depth;    // Depth of the node in the tree

    // Constructor to initialize a Node with its state, parent node, and move direction
    public Node(State state, Node parent, int moveDirection) {
        this.state = state;
        this.parent = parent;
        this.moveDirection = moveDirection;
        this.depth = (parent == null) ? 0 : parent.depth + 1;   // If the parent is null, this is the root node; otherwise, increment the parent's depth
    }

    public State getState() { return state; }   // Getter for the current state
    public Node getParent() { return parent; }  // Getter for the parent node
    public int getMoveDirection() { return moveDirection; } // Getter for the direction of the move that led to this node
    public int getDepth() { return depth; } // Getter for the depth of the node in the tree
}