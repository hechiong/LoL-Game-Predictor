package com.predictor;

public abstract class OperatorFn extends Fn {

    private static final String[] VALID_OPERATOR_FNS = {"add", "dot"};

    // Applies this operator function on the two
    // operands and returns the result matrix.
    protected abstract Vec[] apply(Vec[] firstOperand, Vec[] secondOperand)
            throws OperatorFnException;

    // Checks if either of the two operands is empty.
    protected static void checkEmptyOperands(Vec[] firstOperand,
                                             Vec[] secondOperand)
            throws OperatorFnException {
        if (firstOperand.length == 0 || firstOperand[0].length() == 0
                || secondOperand.length == 0
                || secondOperand[0].length() == 0) {
            throw new OperatorFnException("An operand to an operator function "
                    + "cannot be empty.");
        }
    }

    // Checks if the two operands are valid for this operator function.
    protected void checkValidOperands(Vec[] firstOperand,
                                        Vec[] secondOperand)
            throws OperatorFnException {
        checkEmptyOperands(firstOperand, secondOperand);
        checkValidDimensions(firstOperand, secondOperand);
    }

    // Checks if the two operands have valid
    // dimensions for this operator function.
    protected abstract void checkValidDimensions(Vec[] firstOperand,
                                                 Vec[] secondOperand)
            throws OperatorFnException;

    // Returns the output matrix of the gradient of this operator
    // function with respect to an operand given two operands.
    protected abstract Vec[] gradient(int index, Vec[] firstOperand,
                                      Vec[] secondOperand)
            throws OperatorFnException;

    // Returns whether the function is a valid operator function or not.
    protected static boolean isValidOperatorFn(String fn) {
        for (String validOperatorFn : VALID_OPERATOR_FNS) {
            if (fn.equals(validOperatorFn)) {
                return true;
            }
        }
        return false;
    }
}
