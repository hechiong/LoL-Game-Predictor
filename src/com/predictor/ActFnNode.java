package com.predictor;

public class ActFnNode extends FunctionNode {

    // Constructor for a node associated with an activation function.
    public ActFnNode(String actFn) throws NodeException {
        super(actFn);

        if (!ActFn.isValidActFn(actFn)) {
            throw new NodeException("Activation function nodes can't be "
                    + "created with invalid activation functions.");
        }
    }

    // Computes the vector this activation function node will represent
    // based on the activation function it represents and its child node.
    public Vec[] compute() throws FnException, NodeException {
        if (computed) {
            return m;
        }

        Node n0;
        int numNodes = getChildren().size();

        if (numNodes == 1) {
            ActFn actFn = new ActFn(getFn());
            n0 = getChildren().get(0);
            m = new Vec[n0.numRows()];

            for (int i = 0; i < numRows(); i++) {
                m[i] = new Vec(n0.numCols());

                for (int j = 0; j < numCols(); j++) {
                    m[i].set(j, n0.get(i, j));
                }
                actFn.accept(m[i]);
            }

            computed = true;
        } else {
            throw new NodeException("Computations can't be made for invalid "
                    + "activation function nodes.");
        }

        return m;
    }
}
