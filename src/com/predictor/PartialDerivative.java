package com.predictor;

public class PartialDerivative {

    private final FunctionNode function;
    private final FunctionNode variable;

    // Constructor for taking the partial derivative
    // of a function with respect to a variable.
    public PartialDerivative(FunctionNode fn, FunctionNode var) {
        function = fn;
        variable = var;
    }

    // Returns the partial derivative of the function node with
    // respect to a column index of the variable node. A
    // non-negative dotIndex is used when the function is a dot product.
    public double compute(int index, int dotIndex) throws NodeException {
        return chainRuleDerivative(function, variable, index, dotIndex);
    }

    // Returns the partial derivative of the first function
    // node with respect to the second function node for the
    // backward pass of backpropagation. A non-negative
    // dotIndex is used when the first function is a dot product.
    private double chainRuleDerivative(
            FunctionNode fnNode1, FunctionNode fnNode2, int index,
            int dotIndex) throws NodeException {
        double result = 0;

        if (fnNode1.equals(fnNode2) || fnNode1.isParentOf(fnNode2)) {
            return directDerivative(fnNode1, fnNode2, index, dotIndex);
        }

        for (FunctionNode node : fnNode2.getParents()) {
            result += chainRuleDerivative(fnNode1, node, index, dotIndex)
                    * directDerivative(node, fnNode2, index, dotIndex);
        }

        return result;
    }

    // Returns the partial derivative of the parent
    // function node with respect to the child function
    // node for the forward pass of backpropagation. A non-negative
    // dotIndex is used when the parent function is a dot product.
    private double directDerivative(
            FunctionNode parentNode, FunctionNode childNode, int index,
            int dotIndex) throws NodeException {
        String parentFn = parentNode.getFn();
        double x = childNode.get(0, index);

        if (parentNode.equals(childNode)) {
            return 1;
        } else if (!parentNode.isParentOf(childNode)) {
            throw new NodeException("The partial derivative of the parent node"
                    + " can only be found with respect to a direct child "
                    + "node.");
        }

        if (parentFn.equals("add") || parentFn.equals("identity")) {
            return 1;
        } else if (parentFn.equals("dot")) {
            Node weightNode = parentNode.getChildren().get(1);

            return weightNode.get(index, dotIndex);
        } else if (parentFn.equals("sigmoid")) {
            return ActFn.sigmoid(x) * (1 - ActFn.sigmoid(x));
        } else if (parentFn.equals("tanh")) {
            return 1 - Math.pow(ActFn.tanh(x), 2);
        } else {
            if (parentFn.equals("leaky relu") && x <= 0) {
                return 0.01;
            } else if (parentFn.equals("relu") && x <= 0) {
                return 0;
            }
            return 1;
        }
    }
}
