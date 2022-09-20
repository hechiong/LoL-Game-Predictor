package com.predictor;

public class ParameterNode extends Node {

    private final Vec[] matrixChanges;

    // Constructor for a parameter node representing
    // a matrix with some number of rows and columns.
    public ParameterNode(int numRows, int numCols) {
        super(numRows, numCols);

        matrixChanges = new Vec[numRows];

        resetChanges();
    }

    // Adds the node to this parameter's matrix changes element-wise
    // and stores the result in this parameter's matrix changes.
    public void addChanges(Node n) throws NodeException {
        if (numCols() != n.numCols() || numRows() != n.numRows()) {
            throw new NodeException("The node must match this node's "
                    + "dimensions in order to add the node with its matrix "
                    + "changes.");
        }

        for (int i = 0; i < numRows(); i++) {
            matrixChanges[i].add(n.getRow(i));
        }
    }

    // Resets the matrix changes that would be made to this parameter.
    private void resetChanges() {
        for (int i = 0; i < numRows(); i++) {
            matrixChanges[i] = new Vec(numCols());
        }
    }

    // Scales this parameter's matrix changes by some scalar.
    public void scaleChanges(double scalar) {
        for (int i = 0; i < numRows(); i++) {
            matrixChanges[i].scale(scalar);
        }
    }

    // Updates this parameter by adding the matrix
    // changes to the matrix this parameter represents.
    private void updateParameter() {
        for (int i = 0; i < numRows(); i++) {
            m[i].add(matrixChanges[i]);
        }
    }

    // Updates this parameter and resets its matrix changes.
    public void updateParameterAndResetChanges() {
        updateParameter();
        resetChanges();
    }
}
