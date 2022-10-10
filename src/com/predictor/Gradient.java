package com.predictor;

public class Gradient {

    private final FunctionNode function;
    private final Node variable;

    // Constructor for taking the gradient
    // of a function with respect to a variable.
    public Gradient(FunctionNode fn, Node var) {
        function = fn;
        variable = var;
    }

    // Returns the gradient of the function
    // node with respect to the variable node.
    public DataNode compute() throws FnException, NodeException {
        return directGradient(function, variable);
    }

    // Returns the gradient of the parent function node with respect to
    // the child node if they have a direct relationship with each other.
    private DataNode directGradient(FunctionNode parentFnNode, Node childNode)
            throws FnException, NodeException {
        DataNode resultNode;
        int index = 0;
        Node inputNode;
        Node outcomeNode = new DataNode();
        Node weightNode;
        String parentFn = parentFnNode.getFn();
        Vec[] resultMatrix;

        if (!childNode.isChildOf(parentFnNode)) {
            throw new NodeException("The gradient of the parent node can only "
                    + "be found with respect to a direct child node.");
        } else if (parentFnNode.equals(childNode)) {
            resultNode = new DataNode(
                    childNode.numRows(), childNode.numCols());

            for (int i = 0; i < resultNode.numRows(); i++) {
                for (int j = 0; j < resultNode.numCols(); j++) {
                    resultNode.set(i, j, 1);
                }
            }
        } else if (OperatorFn.isValidOperatorFn(parentFn)) {
            inputNode = parentFnNode.getChildren().get(0);
            weightNode = parentFnNode.getChildren().get(1);

            if (childNode.equals(weightNode)) {
                index = 1;
            }

            if (parentFn.equals("add")) {
                resultMatrix = new Add().gradient(
                        index, inputNode.getMatrix(), weightNode.getMatrix());
            } else {
                resultMatrix = new Dot().gradient(
                        index, inputNode.getMatrix(), weightNode.getMatrix());
            }

            resultNode = new DataNode(
                    resultMatrix.length, resultMatrix[0].length());

            for (int i = 0; i < resultNode.numRows(); i++) {
                resultNode.setRow(i, resultMatrix[i]);
            }
        } else {
            if (LossFn.isValidLossFn(parentFn)) {
                outcomeNode = parentFnNode.getChildren().get(0);
            }

            resultNode = new DataNode(
                    childNode.numRows(), childNode.numCols());

            for (int i = 0; i < resultNode.numRows(); i++) {
                switch (parentFn) {
                    case "identity":
                        resultNode.setRow(i, new Identity().gradient(childNode.getRow(i)));
                        break;
                    case "leaky relu":
                        resultNode.setRow(i, new LeakyReLU().gradient(childNode.getRow(i)));
                        break;
                    case "relu":
                        resultNode.setRow(i, new ReLU().gradient(childNode.getRow(i)));
                        break;
                    case "sigmoid":
                        resultNode.setRow(i, new Sigmoid().gradient(childNode.getRow(i)));
                        break;
                    case "tanh":
                        resultNode.setRow(i, new Tanh().gradient(childNode.getRow(i)));
                        break;
                    case "absolute error":
                        resultNode.setRow(i, new AbsoluteError().gradient(
                                outcomeNode.getRow(i), childNode.getRow(i)));
                        break;
                    case "cross-entropy":
                        resultNode.setRow(i, new CrossEntropy().gradient(
                                outcomeNode.getRow(i), childNode.getRow(i)));
                        break;
                    case "hinge":
                        resultNode.setRow(i, new Hinge().gradient(
                                outcomeNode.getRow(i), childNode.getRow(i)));
                        break;
                    default:
                        resultNode.setRow(i, new SquaredError().gradient(
                                outcomeNode.getRow(i), childNode.getRow(i)));
                }
            }
        }

        return resultNode;
    }
}
