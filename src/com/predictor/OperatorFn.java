package com.predictor;

public abstract class OperatorFn extends Fn {

    private static final String[] VALID_OPERATOR_FNS = {"add", "dot"};

    // Applies this operator function on the two
    // operands and returns the result matrix.
    protected abstract Vec[] apply(Vec[] firstOperand, Vec[] secondOperand)
            throws OperatorFnException;

    // Returns whether the dimensions of the two operands
    // are valid for this operator function or not.
    protected abstract boolean areValidDimensions(Vec[] firstOperand,
                                                  Vec[] secondOperand);

    // Returns the output matrix of the gradient of this operator
    // function with respect to an operand given two operands.
    protected abstract Vec[] gradient(int index, Vec[] firstOperand,
                                      Vec[] secondOperand)
            throws OperatorFnException;

    // Returns whether the function is a valid operator function.
    protected static boolean isValidOperatorFn(String fn) {
        for (String validOperatorFn : VALID_OPERATOR_FNS) {
            if (fn.equals(validOperatorFn)) {
                return true;
            }
        }
        return false;
    }
}
