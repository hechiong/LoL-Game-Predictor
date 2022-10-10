package com.predictor;

public abstract class Fn {

    private String fnName;

    // Returns the name of this function.
    protected String getFnName() {
        return fnName;
    }

    // Sets the name of this function.
    protected void setFnName(String name) {
        fnName = name;
    }
}
