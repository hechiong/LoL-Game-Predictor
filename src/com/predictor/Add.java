package com.predictor;

public class Add extends OperatorFn {

    // Constructor for an add operator function.
    public Add() {
        setFnName("add");
    }

    // Applies the add operator function on the operands.
    protected Vec[] apply(Vec[]... operands) throws OperatorFnException {
        Vec[] sumMatrix;
        int numCols;
        int numRows;

        areValidDimensions(operands);

        numRows = operands[0].length;
        numCols = operands[0][0].length();

        sumMatrix = new Vec[numRows];
        for (int i = 0; i < numRows; i++) {
            sumMatrix[i] = new Vec(numCols);
        }

        for (Vec[] operand : operands) {
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    sumMatrix[i].set(j, sumMatrix[i].get(j)
                            + operand[i].get(j));
                }
            }
        }

        return sumMatrix;
    }

    // Returns whether the operands have equal dimensions or not.
    protected boolean areValidDimensions(Vec[]... operands)
            throws OperatorFnException {
        Vec[] operand;
        int numRows;
        int numCols;

        if (hasValidNumberOfOperands(operands)) {
            throw new AddException("There must be more than one operand to use"
                    + " the add function.");
        }

        numRows = operands[0].length;
        numCols = operands[0][0].length();

        for (int i = 1; i < operands.length; i++) {
            operand = operands[i];

            if (operand.length != numRows || operand[0].length() != numCols) {
                return false;
            }
        }

        return true;
    }

    // Returns the output matrix of the gradient of the add operator
    // function given some operands and with respect to an operand.
    protected Vec[] gradient(int index, Vec[]... operands)
            throws OperatorFnException {
        Vec[] addGradient;
        int numRows;
        int numCols;

        areValidDimensions(operands);

        numRows = operands[0].length;
        numCols = operands[0][0].length();

        addGradient = new Vec[numRows];
        for (int i = 0; i < numRows; i++) {
            addGradient[i] = new Vec(numCols);

            for (int j = 0; j < numCols; j++) {
                addGradient[i].set(j, 1);
            }
        }

        return addGradient;
    }

    // Returns whether there's more than one operand or not.
    protected boolean hasValidNumberOfOperands(Vec[]... operands) {
        return operands.length > 1;
    }
}
