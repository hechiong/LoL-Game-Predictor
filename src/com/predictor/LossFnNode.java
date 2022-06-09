package com.predictor;

public class LossFnNode extends FunctionNode {

    // Constructor for a node associated with a loss function.
    public LossFnNode(String lossFn) throws NodeException {
        super(lossFn);

        if (!LossFn.isValidLossFn(lossFn)) {
            throw new NodeException("Loss function nodes can't be created "
                    + "with invalid loss functions.");
        }
    }

    // Computes the vector this loss function node will represent
    // based on the loss function it represents and its child node.
    public Vec[] compute() throws FnException, NodeException {
        if (computed) {
            return m;
        }

        Node n0, n1;
        int numNodes = getChildren().size();

        if (numNodes == 2) {
            LossFn lossFn = new LossFn(getFn());
            n0 = getChildren().get(0);
            n1 = getChildren().get(1);
            m = new Vec[n0.numRows()];

            for (int i = 0; i < numRows(); i++) {
                m[i] = new Vec(n0.numCols());

                m[i].set(0, lossFn.apply(n0.get(i, 0), n1.get(i, 0)));
            }

            computed = true;
        } else {
            throw new NodeException("Computations can't be made for invalid "
                    + "loss function nodes.");
        }

        return m;
    }
}
