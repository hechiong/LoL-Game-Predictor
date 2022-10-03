package com.predictor;

public class Dot extends OperatorFn {

    // Constructor for a dot operator function.
    public Dot() {
        setFnName("dot");
    }

    // Applies the dot operator function on the
    // two operands and returns the result matrix.
    protected Vec[] apply(Vec[] firstOperand, Vec[] secondOperand)
            throws OperatorFnException {
        double elem1;
        double elem2;
        int numCols;
        int numRows;
        Vec[] dotMatrix;

        if (!areValidDimensions(firstOperand, secondOperand)) {
            throw new DotException("The number of columns of the first operand"
                    + " must equal to the number of rows of the second operand"
                    + " to use the dot operator function.");
        }

        numCols = secondOperand[0].length();
        numRows = firstOperand.length;

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
    protected boolean areValidDimensions(Vec[] firstOperand,
                                         Vec[] secondOperand) {
        int firstOperandNumCols = firstOperand[0].length();
        int secondOperandNumRows = secondOperand.length;

        return firstOperandNumCols == secondOperandNumRows;
    }

    // Returns the output matrix of the gradient of the dot operator
    // function with respect to the indexed operand given two operands.
    protected Vec[] gradient(int index, Vec[] firstOperand,
                             Vec[] secondOperand)
            throws OperatorFnException {
        int numCols;
        int numRows;
        Vec[] dotGradient;
        Vec[] oppositeOperand;

        if (index != 0 && index != 1) {
            throw new DotException("The gradient of the dot operator function "
                    + "can only be taken with respect to the first or second "
                    + "operand.");
        } else if (!areValidDimensions(firstOperand, secondOperand)) {
            throw new DotException("The number of columns of the first operand"
                    + " must equal to the number of rows of the second operand"
                    + " to use the dot operator function.");
        }

        if (index == 0) {
            oppositeOperand = secondOperand;
        } else {
            oppositeOperand = firstOperand;
        }

        numCols = oppositeOperand.length;
        numRows = oppositeOperand[0].length();

        dotGradient = new Vec[numRows];
        for (int i = 0; i < numRows; i++) {
            dotGradient[i] = new Vec(numCols);

            for (int j = 0; j < numCols; j++) {
                dotGradient[i].set(j, oppositeOperand[j].get(i));
            }
        }

        return dotGradient;
    }
}
