package com.predictor;

public class ActFnNode extends FunctionNode {

    private final ActFn actFn;

    // Constructor for a node associated with an activation function.
    public ActFnNode(String actFnName) throws ActFnNodeException {
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
            case "tanh":
                actFn = new Tanh();
                break;
            default:
                throw new ActFnNodeException("Activation function nodes can't "
                        + "be created with invalid activation functions.");
        }

        setFn(actFnName);
    }

    // Computes the vector this activation function node represents
    // based on the activation function it represents and its child node.
    public void compute() throws ActFnNodeException {
        int numNodes = getChildren().size();
        Node childNode;
        Vec[] matrix;

        if (numNodes == 1) {
            childNode = getChildren().get(0);
            matrix = new Vec[childNode.numRows()];

            for (int i = 0; i < numRows(); i++) {
                matrix[i] = childNode.getRow(i);
                actFn.accept(matrix[i]);
            }

            setMatrix(matrix);
        } else {
            throw new ActFnNodeException("Computations for activation function"
                    + " nodes can only be made with one child node.");
        }
    }
}
