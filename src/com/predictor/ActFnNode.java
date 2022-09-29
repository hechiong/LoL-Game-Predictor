package com.predictor;

public class ActFnNode extends FunctionNode {

    private final ActFn actFn;

    // Constructor for a node associated with an activation function.
    public ActFnNode(String actFnName) throws ActFnNodeException {
        if (!ActFn.isValidActFn(actFnName)) {
            throw new ActFnNodeException("Activation function nodes can't be "
                    + "created with invalid activation functions.");
        }

        this.fn = actFnName;

        switch (actFnName) {
            case "identity":
                actFn = new Identity();
                break;
            case "leaky relu":
                actFn = new LeakyReLU();
                break;
            case "relu":
                actFn = new ReLU();
                break;
            case "sigmoid":
                actFn = new Sigmoid();
                break;
            default:
                actFn = new Tanh();
        }
    }

    // Computes the vector this activation function node represents
    // based on the activation function it represents and its child node.
    public void compute() throws ActFnNodeException {
        int numNodes = getChildren().size();
        Node childNode;

        if (numNodes == 1) {
            childNode = getChildren().get(0);
            m = new Vec[childNode.numRows()];

            for (int i = 0; i < numRows(); i++) {
                m[i] = new Vec(childNode.numCols());

                setRow(i, childNode.getRow(i));
                actFn.accept(m[i]);
            }
        } else {
            throw new ActFnNodeException("Computations for activation function"
                    + " nodes can only be made with one child node.");
        }
    }
}
