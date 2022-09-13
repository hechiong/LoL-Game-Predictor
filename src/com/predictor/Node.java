package com.predictor;

import java.util.ArrayList;

public class Node {

    protected final ArrayList<Node> children = new ArrayList<>();
    protected final ArrayList<FunctionNode> parents = new ArrayList<>();
    protected String fn = "";
    protected Vec[] m;

    // Default constructor for a node representing
    // a matrix containing one row and column.
    public Node() {
        m = new Vec[]{new Vec()};
    }

    // Constructor for a node representing a matrix
    // containing some number of rows and columns.
    public Node(int numRows, int numCols) {
        assert numRows > 0: "Nodes must have a positive number of columns.";
        assert numCols > 0: "Nodes must have a positive number of rows.";

        m = new Vec[numRows];

        for (int i = 0; i < numRows; i++) {
            m[i] = new Vec(numCols);
        }
    }

    // Adds the nodes element-wise and stores the result in this node.
    public void add(Node n) throws NodeException {
        if (numCols() != n.numCols() || numRows() != n.numRows()) {
            throw new NodeException("The nodes' dimensions must match in order"
                    + " to add them together.");
        }

        for (int i = 0; i < numRows(); i++) {
            for (int j = 0; j < numCols(); j++) {
                set(i, j, get(i, j) + n.get(i, j));
            }
        }
    }

    // Adds a parent node to this node.
    public void addParent(FunctionNode n) {
        if ((ActFn.isValidActFn(n.getFn()) && n.getChildren().size() < 1)
                || (LossFn.isValidLossFn(n.getFn()) && n.getChildren().size() < 2)
                || (n.getFn().equals("dot") && n.getChildren().size() < 2)
                || n.getFn().equals("add")) {
            parents.add(n);
            n.children.add(this);
        }
    }

    // Returns the value at some column and row of this node's matrix.
    public double get(int row, int col) {
        return m[row].get(col);
    }

    // Returns a copy of the list of parents of this node.
    public ArrayList<Node> getChildren() {
        return new ArrayList<>(children);
    }

    // Returns the specified column of this node's matrix.
    public Vec getCol(int col) {
        Vec column = new Vec(numRows());

        for (int i = 0; i < numRows(); i++) {
            column.set(i, m[i].get(col));
        }

        return column;
    }

    // Returns the function this node represents.
    public String getFn() {
        return fn;
    }

    // Returns the matrix this node represents.
    public Vec[] getMatrix() {
        Vec[] mCopy = new Vec[numRows()];

        for (int i = 0; i < numRows(); i++) {
            mCopy[i] = new Vec(numCols());

            for (int j = 0; j < numCols(); j++) {
                mCopy[i].set(j, m[i].get(j));
            }
        }

        return mCopy;
    }

    // Returns a copy of the list of parents of this node.
    public ArrayList<FunctionNode> getParents() {
        return new ArrayList<>(parents);
    }

    // Returns the specified row of this node's matrix.
    public Vec getRow(int row) {
        return m[row].copy();
    }

    // Returns whether the node has the same dimensions as this node.
    public boolean hasEqualDims(Node n) {
        return hasEqualNumRows(n) && hasEqualNumCols(n);
    }

    // Returns whether the node has the same number of columns as this node.
    public boolean hasEqualNumCols(Node n) {
        return numCols() == n.numCols();
    }

    // Returns whether the node has the same number of columns as this node.
    public boolean hasEqualNumRows(Node n) {
        return numRows() == n.numRows();
    }

    // Returns whether this node is a child of the node or not.
    public boolean isChildOf(FunctionNode n) {
        return n.getChildren().contains(this);
    }

    // Returns the number of columns of this node's matrix.
    public int numCols() {
        return m[0].length();
    }

    // Returns the number of rows of this node's matrix.
    public int numRows() {
        return m.length;
    }

    // Scales this node element-wise by a scalar.
    public void scale(double scalar) throws NodeException {
        for (int i = 0; i < numRows(); i++) {
            for (int j = 0; j < numCols(); j++) {
                set(i, j, get(i, j) * scalar);
            }
        }
    }

    // Sets the value at some column and row of this node's matrix.
    public void set(int row, int col, double value) {
        m[row].set(col, value);
    }

    // Sets a column of this node's matrix to be equivalent to the vector.
    public void setCol(int col, Vec v) {
        for (int i = 0; i < numRows(); i++) {
            set(i, col, v.get(i));
        }
    }

    // Sets a row of this node's matrix to be equivalent to the vector.
    public void setRow(int row, Vec v) {
        for (int i = 0; i < numCols(); i++) {
            set(row, i, v.get(i));
        }
    }
}
