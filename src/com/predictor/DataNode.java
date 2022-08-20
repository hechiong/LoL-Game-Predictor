package com.predictor;

public class DataNode extends Node {

    // Default constructor for a data node containing one element.
    public DataNode() {
        super();
    }

    // Constructor for a data node containing arrays of elements.
    public DataNode(int numRows, int numCols) {
        super(numRows, numCols);
    }
}
