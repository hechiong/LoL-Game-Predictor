package com.predictor;

public abstract class FunctionNode extends Node {

    // Computes the matrix this function node will represent
    // based on the function it represents and its children nodes.
    public abstract void compute() throws FnException, NodeException;
}

