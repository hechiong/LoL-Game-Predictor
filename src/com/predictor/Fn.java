package com.predictor;

public abstract class Fn {

    private String fnName;

    // Returns whether the function is a valid function or not.
    public static boolean isValidFn(String fn) {
        return ActFn.isValidActFn(fn) || LossFn.isValidLossFn(fn)
                || OperatorFn.isValidOperatorFn(fn);
    }

    // Returns the name of this function.
    protected String getFnName() {
        return fnName;
    }

    // Sets the name of this function.
    protected void setFnName(String name) {
        fnName = name;
    }
}
