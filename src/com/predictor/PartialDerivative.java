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

    // Returns the partial derivative of the function node
    // with respect to a column index of the variable node.
    public double compute(int index) throws NodeException {
        return chainRuleDerivative(function, variable, index);
    }

    // Returns the partial derivative of the first
    // function node with respect to the second function
    // node for the forward pass of backpropagation.
    private double chainRuleDerivative(
            FunctionNode fnNode1, FunctionNode fnNode2, int index)
            throws NodeException {
        double result = 0;

        if (fnNode1.equals(fnNode2) || fnNode1.isParentOf(fnNode2)) {
            return directDerivative(fnNode1, fnNode2, index);
        }

        for (FunctionNode node : fnNode2.parents) {
            result += chainRuleDerivative(fnNode1, node, index)
                    * directDerivative(node, fnNode2, index);
        }

        return result;
    }

    // Returns the partial derivative of the parent
    // function node with respect to the child function
    // node for the forward pass of backpropagation.
    private double directDerivative(
            FunctionNode parentNode, FunctionNode childNode, int index)
            throws NodeException {
        String parentFn = parentNode.getFn();
        double result = 0;
        double x = childNode.get(index, 0);

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
            Node weightNode = parentNode.children.get(1);

            for (int i = 0; i < weightNode.numCols(); i++) {
                result += weightNode.get(i, index);
            }

            return result;
        } else if (parentFn.equals("sigmoid")) {
            return Fn.sigmoid(x) * (1 - Fn.sigmoid(x));
        } else if (parentFn.equals("tanh")) {
            return 1 - Math.pow(Fn.tanh(x), 2);
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
