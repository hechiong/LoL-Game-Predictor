package com.predictor;

public class OperatorFn extends Fn {

    private static final String[] VALID_OPERATOR_FNS = {"add", "dot"};

    // Returns whether the function is a valid activation function.
    public static boolean isValidOperatorFn(String fn) {
        for (String validOperatorFn : VALID_OPERATOR_FNS) {
            if (fn.equals(validOperatorFn)) {
                return true;
            }
        }
        return false;
    }
}
