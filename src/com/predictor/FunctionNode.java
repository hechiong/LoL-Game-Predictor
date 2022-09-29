package com.predictor;

public abstract class FunctionNode extends Node {

    // Computes the vector this function node will represent
    // based on the function it represents and its children nodes.
    public void compute() throws FnException, NodeException {
        Node n0, n1;
        int numNodes = getChildren().size();

        if (numNodes > 1) {
            n0 = getChildren().get(0);
            n1 = getChildren().get(1);

            if (fn.equals("add")) {
                m = new Vec[n0.numRows()];
                for (int i = 0; i < m.length; i++) {
                    m[i] = new Vec(n0.numCols());
                }

                for (Node n : getChildren()) {
                    if (!hasEqualDims(n)) {
                        throw new NodeException("Nodes added together must "
                                + "have equal dimensions.");
                    }

                    for (int i = 0; i < numRows(); i++) {
                        for (int j = 0; j < numCols(); j++) {
                            set(i, j, get(i, j) + n.get(i, j));
                        }
                    }
                }
            } else if (fn.equals("dot") && numNodes == 2) {
                m = new Vec[n0.numRows()];
                for (int i = 0; i < m.length; i++) {
                    m[i] = new Vec(n1.numCols());
                }

                if (n0.numCols() == n1.numRows()) {
                    for (int i = 0; i < numRows(); i++) {
                        for (int j = 0; j < numCols(); j++) {
                            for (int k = 0; k < n0.numCols(); k++) {
                                set(i, j, get(i, j) + n0.get(i, k) * n1.get(k, j));
                            }
                        }
                    }
                } else {
                    throw new NodeException("Nodes involved in matrix "
                            + "multiplication must have valid dimensions.");
                }
            } else {
                throw(new NodeException("Function nodes must have an "
                        + "appropriate number of node inputs."));
            }
        } else {
            throw new NodeException("Computations can't be made for invalid "
                    + "function nodes.");
        }
    }
}

