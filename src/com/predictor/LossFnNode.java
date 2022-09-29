package com.predictor;

public class LossFnNode extends FunctionNode {

    private final LossFn lossFn;

    // Constructor for a node associated with a loss function.
    public LossFnNode(String lossFnName) throws LossFnNodeException {
        if (!LossFn.isValidLossFn(lossFnName)) {
            throw new LossFnNodeException("Loss function nodes can't be "
                    + "created with invalid loss functions.");
        }

        this.fn = lossFnName;

        switch (lossFnName) {
            case "absolute error":
                lossFn = new AbsoluteError();
                break;
            case "cross-entropy":
            case "log":
                lossFn = new CrossEntropy();
                break;
            case "hinge":
                lossFn = new Hinge();
                break;
            default:
                lossFn = new SquaredError();
        }
    }

    // Computes the vector this loss function node represents
    // based on the loss function it represents and its child node.
    public void compute() throws LossFnException, LossFnNodeException {
        int numNodes = getChildren().size();
        Node outcomeNode;
        Node predNode;

        if (numNodes == 2) {
            outcomeNode = getChildren().get(0);
            predNode = getChildren().get(1);
            m = new Vec[outcomeNode.numRows()];

            for (int i = 0; i < numRows(); i++) {
                m[i] = new Vec(outcomeNode.numCols());

                setRow(i, lossFn.apply(outcomeNode.getRow(i), predNode.getRow(i)));
            }
        } else {
            throw new LossFnNodeException("Computations for loss function "
                    + "nodes can only be made with two children nodes.");
        }
    }
}
