package com.predictor;

public class Dot extends OperatorFn {

    // Constructor for a dot operator function.
    public Dot() {
        setFnName("dot");
    }

    // Applies the operator function on the operands.
    protected Vec[] apply(Vec[]... operands) {
        m = new Vec[n0.numRows()];
        for (int i = 0; i < m.length; i++) {
            m[i] = new Vec(n1.numCols());
        }

        if (n0.numCols() == n1.numRows()) {
            for (int i = 0; i < numRows(); i++) {
                for (int j = 0; j < numCols(); j++) {
                    for (int k = 0; k < n0.numCols(); k++) {
                        set(i, j, get(i, j) + n0.get(i, k) * n1.get(k, j));
                    }
                }
            }
        } else {
            throw new NodeException("Nodes involved in matrix "
                    + "multiplication must have valid dimensions.");
        }
    }

    // Returns the output matrix of the gradient of the dot
    // function given some operands and with respect to an operand.
    protected Vec[] gradient(int index, Vec[]... operands) {
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
    }
}
