package com.predictor;

public class OperatorFnNode extends FunctionNode {

    private final OperatorFn operatorFn;

    // Constructor for a node associated with an operator function.
    public OperatorFnNode(String operatorFnName)
            throws OperatorFnNodeException {
        if (!OperatorFn.isValidOperatorFn(operatorFnName)) {
            throw new OperatorFnNodeException("Operator function nodes can't "
                    + "be created with invalid operator functions.");
        }

        this.fn = operatorFnName;

        if (operatorFnName.equals("add")) {
            operatorFn = new Add();
        } else {
            operatorFn = new Dot();
        }
    }


    // Computes the matrix this operator function node represents based
    // on the operator function it represents and its children nodes.
    public void compute() throws OperatorFnException, OperatorFnNodeException {
        int numNodes = getChildren().size();
        Node n0, n1;

        if (numNodes == 2) {
            n0 = getChildren().get(0);
            n1 = getChildren().get(1);

            m = operatorFn.apply(n0.getMatrix(), n1.getMatrix());
        } else {
            throw new OperatorFnNodeException("Computations for operator "
                    + "function nodes can only be made with two children "
                    + "nodes.");
        }
    }
}
