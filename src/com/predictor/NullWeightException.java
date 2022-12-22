package com.predictor;

public class NullWeightException extends Exception {

    private static final String WEIGHT_ERR_MSG = "Train or load a weight first.";

    // Constructor for a NullWeightException with an error message.
    public NullWeightException() {
        super(WEIGHT_ERR_MSG);
    }
}
