package com.predictor;

import java.util.ArrayList;

public class Node {

    protected final ArrayList<FunctionNode> parents = new ArrayList<>();
    protected Vec[] m;

    // Default constructor for a node representing
    // a matrix containing one row and column.
    public Node() {
        m = new Vec[]{new Vec()};
    }

    // Constructor for a node representing a matrix
    // containing some number of rows and columns.
    public Node(int numCols, int numRows) {
        assert numCols > 0: "Nodes must have a positive number of columns.";
        assert numRows > 0: "Nodes must have a positive number of rows.";

        m = new Vec[numCols];

        for (int i = 0; i < numCols; i++) {
            m[i] = new Vec(numRows);
        }
    }

    // Adds a parent node to this node.
    public void addParent(FunctionNode n) {
        if ((ActFn.isValidActFn(n.getFn()) && n.getChildren().size() < 1)
                || (n.getFn().equals("dot") && n.getChildren().size() < 2)
                || n.getFn().equals("add")) {
            parents.add(n);
            n.children.add(this);
        }
    }

    // Returns the value at some column and row of this node's matrix.
    public double get(int col, int row) {
        return m[col].get(row);
    }

    // Returns a copy of the list of parents of this node.
    public ArrayList<FunctionNode> getParents() {
        return new ArrayList<>(parents);
    }

    // Returns whether the node has the same number of columns as this node.
    public boolean hasEqualNumCols(Node n) {
        return numCols() == n.numCols();
    }

    // Returns whether the node has the same number of columns as this node.
    public boolean hasEqualNumRows(Node n) {
        return numRows() == n.numRows();
    }

    // Returns whether the node has the same dimensions as this node.
    public boolean hasEqualDims(Node n) {
        return hasEqualNumCols(n) && hasEqualNumRows(n);
    }

    // Returns whether this node is a child of the node or not.
    public boolean isChildOf(FunctionNode n) {
        return n.children.contains(this);
    }

    // Returns the number of columns of this node's matrix.
    public int numCols() {
        return m.length;
    }

    // Returns the number of rows of this node's matrix.
    public int numRows() {
        return m[0].length();
    }

    // Sets the value at some column and row of this node's matrix.
    public void set(int col, int row, double value) {
        m[col].set(row, value);
    }
}
