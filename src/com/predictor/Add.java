package com.predictor;

public class Add extends OperatorFn {

    // Constructor for an add operator function.
    public Add() {
        setFnName("add");
    }

    // Applies the add operator function on the
    // two operands and returns the result matrix.
    protected Vec[] apply(Vec[] firstOperand, Vec[] secondOperand)
            throws OperatorFnException {
        int numCols;
        int numRows;
        Vec[] sumMatrix;

        checkValidOperands(firstOperand, secondOperand);

        numCols = firstOperand[0].length();
        numRows = firstOperand.length;

        sumMatrix = new Vec[numRows];
        for (int i = 0; i < numRows; i++) {
            sumMatrix[i] = new Vec(numCols);
        }

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                sumMatrix[i].set(j, firstOperand[i].get(j)
                        + secondOperand[i].get(j));
            }
        }

        return sumMatrix;
    }

    // Checks if the two operands have equal dimensions.
    protected void checkValidDimensions(Vec[] firstOperand,
                                        Vec[] secondOperand)
            throws OperatorFnException {
        int firstOperandNumCols = firstOperand[0].length();
        int firstOperandNumRows = firstOperand.length;
        int secondOperandNumCols = secondOperand[0].length();
        int secondOperandNumRows = secondOperand.length;

        if (firstOperandNumCols != secondOperandNumCols
                || firstOperandNumRows != secondOperandNumRows) {
            throw new AddException("The two operands must have the same "
                    + "dimensions to use the add operator function.");
        }
    }

    // Returns the output matrix of the gradient of the add operator
    // function with respect to the indexed operand given two operands.
    protected Vec[] gradient(int index, Vec[] firstOperand,
                             Vec[] secondOperand)
            throws OperatorFnException {
        int numCols;
        int numRows;
        Vec[] addGradient;

        checkValidOperands(firstOperand, secondOperand);

        numCols = firstOperand[0].length();
        numRows = firstOperand.length;

        addGradient = new Vec[numRows];
        for (int i = 0; i < numRows; i++) {
            addGradient[i] = new Vec(numCols);

            for (int j = 0; j < numCols; j++) {
                addGradient[i].set(j, 1);
            }
        }

        return addGradient;
    }
}
