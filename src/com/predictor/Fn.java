package com.predictor;

public class Fn {

    public static final String[] VALID_FNS = {"add", "dot"};

    protected String fnName;

    // Returns whether the function is valid or not.
    public static boolean isValidFn(String fn) {
        for (String validFn : VALID_FNS) {
            if (fn.equals(validFn)) {
                return true;
            }
        }
        return ActFn.isValidActFn(fn) || LossFn.isValidLossFn(fn);
    }
}
