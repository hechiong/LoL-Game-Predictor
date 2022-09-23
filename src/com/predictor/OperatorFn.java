package com.predictor;

public abstract class OperatorFn extends Fn {

    private static final String[] VALID_OPERATOR_FNS = {"add", "dot"};

    // Applies this operator function on the
    // operands and returns the result matrix.
    protected abstract Vec[] apply(Vec[]... operands)
            throws OperatorFnException;

    // Returns whether the dimensions of the operands
    // are valid for this operator function or not.
    protected abstract boolean areValidDimensions(Vec[]... operands)
            throws OperatorFnException;

    // Returns the output matrix of the gradient of this operator
    // function with respect to an operand given some operands.
    protected abstract Vec[] gradient(int index, Vec[]... operands)
            throws OperatorFnException;

    // Returns whether there's a valid number of
    // operands for this operator function or not.
    protected abstract boolean hasValidNumberOfOperands(Vec[]... operands);

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
