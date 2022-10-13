package com.predictor;

public class OperatorFnNode extends FunctionNode {

    private final OperatorFn operatorFn;

    // Constructor for a node associated with an operator function.
    public OperatorFnNode(String operatorFnName)
            throws OperatorFnNodeException {
        switch (operatorFnName) {
            case "add":
                operatorFn = new Add();
                break;
            case "dot":
                operatorFn = new Dot();
                break;
            default:
                throw new OperatorFnNodeException("Operator function nodes "
                        + "can't be created with invalid operator functions.");
        }

        setFn(operatorFnName);
    }

    // Computes the matrix this operator function node represents based
    // on the operator function it represents and its children nodes.
    public void compute() throws OperatorFnException, OperatorFnNodeException {
        int numNodes = getChildren().size();
        Node firstOperand;
        Node secondOperand;
        Vec[] matrix;

        if (numNodes == 2) {
            firstOperand = getChildren().get(0);
            secondOperand = getChildren().get(1);

            matrix = operatorFn.apply(
                    firstOperand.getMatrix(), secondOperand.getMatrix());

            setMatrix(matrix);
        } else {
            throw new OperatorFnNodeException("Computations for operator "
                    + "function nodes can only be made with two children "
                    + "nodes.");
        }
    }
}
