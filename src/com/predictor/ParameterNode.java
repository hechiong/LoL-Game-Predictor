package com.predictor;

public class ParameterNode extends Node {

    private Vec[] matrixChanges;

    // Constructor for a parameter node representing a matrix
    // containing some number of rows and columns.
    public ParameterNode(int numCols, int numRows) {
        super(numCols, numRows);
    }
}
