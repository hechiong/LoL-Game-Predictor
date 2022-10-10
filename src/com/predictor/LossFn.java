package com.predictor;

public abstract class LossFn extends Fn {

    private static final String[] VALID_LOSS_FNS = {"absolute error",
            "cross-entropy", "hinge", "log", "squared error"};

    // Applies this loss function on an outcome vector and
    // a prediction vector and returns the result vector.
    protected abstract Vec apply(Vec outcome, Vec prediction)
            throws LossFnException;

    // Checks if the outcome and prediction vectors have equal lengths.
    protected static void checkLengths(Vec outcome, Vec prediction)
            throws LossFnException {
        if (outcome.length() != prediction.length()) {
            throw new LossFnException("The outcome and prediction vectors must"
                    + " have equal lengths to use a loss function.");
        }
    }

    // Returns the output of the derivative of this loss function
    // with respect to the prediction given an outcome and a prediction.
    protected abstract double derivative(double outcome, double prediction);

    // Returns the output vector of the gradient of this
    // loss function with respect to the prediction vector
    // given an outcome vector and a prediction vector.
    protected Vec gradient(Vec outcome, Vec prediction) {
        Vec outputVector = new Vec(outcome.length());

        for (int i = 0; i < outputVector.length(); i++) {
            outputVector.set(i, derivative(outcome.get(i), prediction.get(i)));
        }

        return outputVector;
    }

    // Returns whether the function is a valid loss function.
    public static boolean isValidLossFn(String fn) {
        for (String validLossFn : VALID_LOSS_FNS) {
            if (fn.equals(validLossFn)) {
                return true;
            }
        }
        return false;
    }

    // Returns the String representation of this loss function.
    public String toString() {
        return "Loss function: " + getFnName();
    }
}
