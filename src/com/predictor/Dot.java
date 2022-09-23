package com.predictor;

public class Dot extends OperatorFn {

    // Constructor for a dot operator function.
    public Dot() {
        setFnName("dot");
    }

    // Applies the dot operator function on the
    // two operands and returns the result matrix.
    protected Vec[] apply(Vec[]... operands) throws OperatorFnException {
        Vec[] dotMatrix;
        Vec[] firstOperand;
        Vec[] secondOperand;
        double elem1;
        double elem2;
        int numCols;
        int numRows;

        if (!areValidDimensions(operands)) {
            throw new DotException("The number of columns of the first operand"
                    + " must equal to the number of rows of the second operand"
                    + " to use the dot operator function.");
        }

        firstOperand = operands[0];
        secondOperand = operands[1];
        numRows = firstOperand.length;
        numCols = secondOperand[0].length();

        dotMatrix = new Vec[numRows];
        for (int i = 0; i < numRows; i++) {
            dotMatrix[i] = new Vec(numCols);
        }


        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                for (int k = 0; k < firstOperand[0].length(); k++) {
                    elem1 = firstOperand[i].get(k);
                    elem2 = secondOperand[k].get(j);

                    dotMatrix[i].set(j, dotMatrix[i].get(j) + elem1 * elem2);
                }
            }
        }

        return dotMatrix;
    }

    // Returns whether the number of columns of the first operand
    // is equal to the number of rows of the second operand or not.
    protected boolean areValidDimensions(Vec[]... operands)
            throws OperatorFnException {
        int firstOperandNumCols;
        int secondOperandNumRows;

        if (!hasValidNumberOfOperands(operands)) {
            throw new DotException("There must be exactly two operands to use "
                    + "the dot function.");
        }

        firstOperandNumCols = operands[0][0].length();
        secondOperandNumRows = operands[1].length;

        return firstOperandNumCols == secondOperandNumRows;
    }

    // Returns the output matrix of the gradient of the dot operator
    // function with respect to an operand given two operands.
    protected Vec[] gradient(int index, Vec[]... operands)
            throws OperatorFnException {
        Vec[] dotGradient;
        Vec[] oppositeOperand;
        int numRows;
        int numCols;

        areValidDimensions(operands);
        if (index != 0 && index != 1) {
            throw new DotException("The gradient of the dot operator function "
                    + "can only be taken with respect to the first or second "
                    + "operand.");
        }

        oppositeOperand = operands[1 - index];
        numRows = oppositeOperand[0].length();
        numCols = oppositeOperand.length;

        dotGradient = new Vec[numRows];
        for (int i = 0; i < numRows; i++) {
            dotGradient[i] = new Vec(numCols);

            for (int j = 0; j < numCols; j++) {
                dotGradient[i].set(j, oppositeOperand[j].get(i));
            }
        }

        return dotGradient;
    }

    // Returns whether there are two operands or not.
    protected boolean hasValidNumberOfOperands(Vec[]... operands) {
        return operands.length == 2;
    }
}
