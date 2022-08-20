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
    public DataNode compute() throws NodeException {
        return directGradient(function, variable);
    }

    /*
    // Returns the gradient of the function node with respect to
    // the variable node for the backward pass of backpropagation.
    private DataNode chainRuleGradient(FunctionNode fnNode, Node node)
            throws NodeException {
        DataNode resultNode;

        if (fnNode.equals(node) || fnNode.isParentOf(node)) {
            return directGradient(fnNode, node);
        }

        for (FunctionNode n : node.getParents()) {
            resultNode += chainRuleGradient(fnNode, n)
                    * directGradient(n, node);
        }

        return resultNode;
    }*/

    // Returns the gradient of the parent function node with respect
    // to the child node for the forward pass of backpropagation.
    private DataNode directGradient(
            FunctionNode parentFnNode, Node childNode) throws NodeException {
        String parentFn = parentFnNode.getFn();
        DataNode resultNode;

        if (parentFnNode.equals(childNode)) {
            resultNode = new DataNode(
                    childNode.numRows(), childNode.numCols());

            for (int i = 0; i < resultNode.numRows(); i++) {
                for (int j = 0; j < resultNode.numCols(); j++) {
                    resultNode.set(i, j, 1);
                }
            }
        } else if (!parentFnNode.isParentOf(childNode)) {
            throw new NodeException("The gradient of the parent node can only "
                    + "be found with respect to a direct child node.");
        }

        if (parentFn.equals("dot")) {
            Node dataNode = parentFnNode.getChildren().get(0);
            Node weightNode = parentFnNode.getChildren().get(1);

            if (childNode.equals(dataNode)) {
                resultNode = new DataNode(weightNode.numCols(), weightNode.numRows());

                for (int i = 0; i < resultNode.numRows(); i++) {
                    for (int j = 0; j < resultNode.numCols(); j++) {
                        resultNode.set(i, j, weightNode.get(j, i));
                    }
                }
            } else {
                resultNode = new DataNode(dataNode.numCols(), dataNode.numRows());

                for (int i = 0; i < resultNode.numRows(); i++) {
                    for (int j = 0; j < resultNode.numCols(); j++) {
                        resultNode.set(i, j, dataNode.get(j, i));
                    }
                }
            }
        } else {
            Node outcomeNode;
            resultNode = new DataNode(
                    childNode.numRows(), childNode.numCols());

            for (int i = 0; i < resultNode.numRows(); i++) {
                if (parentFn.equals("leaky relu")) {
                    resultNode.setRow(i, ActFn.reluGradient("leaky", childNode.getRow(i)));
                } else if (parentFn.equals("logistic")) {
                    outcomeNode = parentFnNode.getChildren().get(0);

                    resultNode.setRow(i, LossFn.logisticGradient(
                            outcomeNode.getRow(i), childNode.getRow(i)));
                } else if (parentFn.equals("relu")) {
                    resultNode.setRow(i, ActFn.reluGradient("", childNode.getRow(i)));
                } else if (parentFn.equals("sigmoid")) {
                    resultNode.setRow(i, ActFn.sigmoidGradient(childNode.getRow(i)));
                } else if (parentFn.equals("squared")) {
                    outcomeNode = parentFnNode.getChildren().get(0);

                    resultNode.setRow(i, LossFn.squaredGradient(
                            outcomeNode.getRow(i), childNode.getRow(i)));
                } else if (parentFn.equals("tanh")) {
                    resultNode.setRow(i, ActFn.tanhGradient(childNode.getRow(i)));
                } else {
                    for (int j = 0; j < childNode.numCols(); j++) {
                        resultNode.set(i, j, 1);
                    }
                }
            }
        }

        return resultNode;
    }
}
