package com.predictor;

import java.util.ArrayList;

public abstract class Node {

    private final ArrayList<FunctionNode> parents = new ArrayList<>();
    private final ArrayList<Node> children = new ArrayList<>();
    private String fn = "";
    private Vec[] m;

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

    // Adds a child node to this node.
    protected void addChild(Node n) {
        children.add(n);
    }

    // Adds a parent node to this node if the parent
    // node has an insufficient number of children nodes.
    public void addParent(FunctionNode n) throws NodeException {
        if ((ActFn.isValidActFn(n.getFn()) && n.getChildren().size() < 1)
                || ((LossFn.isValidLossFn(n.getFn())
                || OperatorFn.isValidOperatorFn(n.getFn()))
                && n.getChildren().size() < 2)) {
            parents.add(n);
            n.addChild(this);
        } else {
            throw new NodeException("Function nodes cannot have too many "
                    + "children nodes.");
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

    // Returns the name of the function this node represents.
    public String getFn() {
        return fn;
    }

    // Returns a copy of the matrix this node represents.
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

    // Returns a copy of the specified row of this node's matrix.
    public Vec getRow(int row) {
        return m[row].copy();
    }

    // Returns whether this node is a child of the node or not.
    public boolean isChildOf(FunctionNode n) {
        return parents.contains(n);
    }

    // Returns the number of columns of this node's matrix.
    public int numCols() {
        return m[0].length();
    }

    // Returns the number of rows of this node's matrix.
    public int numRows() {
        return m.length;
    }

    // Sets the value at some column and row of this node's matrix.
    public void set(int row, int col, double value) {
        m[row].set(col, value);
    }

    // Sets this node's matrix to a specified matrix.
    public void setMatrix(Vec[] matrix) {
        m = matrix;
    }

    // Sets a row of this node's matrix to a specified vector.
    public void setRow(int row, Vec v) {
        for (int i = 0; i < numCols(); i++) {
            set(row, i, v.get(i));
        }
    }
}
