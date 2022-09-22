package com.predictor;

import java.util.function.BiFunction;

public abstract class LossFn extends Fn {

    private static final String[] VALID_LOSS_FNS = {"cross-entropy", "log", "squared"};

    private final BiFunction<Vec, Vec, Vec> fn;

    // Constructor for a loss function.
    public LossFn(String lossFn) throws FnException {
        fnName = lossFn;

        switch (lossFn) {
            case "cross-entropy":
            case "log":
                fn = this::crossEntropy;
                break;
            case "squared":
                fn = this::squared;
                break;
            default:
                throw new FnException(lossFn  + " isn't a valid loss "
                        + "function.");
        }
    }

    // Applies this loss function on a prediction
    // and an outcome, and returns the output.
    public Vec apply(Vec prediction, Vec outcome) {
        return fn.apply(prediction, outcome);
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

    // Applies the logistic loss function on an outcome vector
    // and a prediction vector, and returns the output vector.
    private Vec crossEntropy(Vec outcome, Vec prediction) {
        Vec resultVector = new Vec(outcome.length());
        double sig;

        for (int i = 0; i < prediction.length(); i++) {
            sig = ActFn.sigmoid(prediction.get(i));

            resultVector.set(i, -outcome.get(i) * Math.log(sig)
                    - (1-outcome.get(i)) * Math.log(1-sig));
        }

        return resultVector;
    }

    // Returns the output of the logistic loss
    // function given an outcome and a prediction.
    public static double crossEntropy(double outcome, double prediction) {
        double sig = ActFn.sigmoid(prediction);

        return -outcome * Math.log(sig) - (1-outcome) * Math.log(1-sig);
    }

    // Returns the output of the derivative of the logistic loss function
    // (with respect to the prediction) given an outcome and a prediction.
    public static double crossEntropyDerivative(
            double outcome, double prediction) {
        double sig = ActFn.sigmoid(prediction);

        return (-outcome/sig + (1-outcome)/(1-sig))
                * ActFn.sigmoidDerivative(prediction);
    }

    // Returns the output vector of the gradient of the
    // logistic loss function (with respect to the prediction
    // vector) given an outcome vector and a prediction vector.
    public static Vec crossEntropyGradient(Vec outcome, Vec prediction) {
        Vec resultVector = new Vec(outcome.length());

        for (int i = 0; i < resultVector.length(); i++) {
            resultVector.set(
                    i, crossEntropyDerivative(
                            outcome.get(i), prediction.get(i)));
        }

        return resultVector;
    }

    // Applies the squared error function on an outcome vector
    // and a prediction vector, and returns the output vector.
    private Vec squared(Vec outcome, Vec prediction) {
        Vec resultVector = new Vec(outcome.length());

        for (int i = 0; i < prediction.length(); i++) {
            resultVector.set(i,
                    Math.pow(outcome.get(i) - prediction.get(i), 2));
        }

        return resultVector;
    }

    // Returns the output of the squared error
    // function given an outcome and a prediction.
    public static double squared(double outcome, double prediction) {
        return Math.pow(outcome - prediction, 2);
    }

    // Returns the output of the derivative of the squared error function
    // (with respect to the prediction) given an outcome and a prediction.
    public static double squaredDerivative(double outcome, double prediction) {
        return -2 * (outcome - prediction);
    }

    // Returns the output vector of the gradient of the
    // squared error function (with respect to the prediction
    // vector) given an outcome vector and a prediction vector.
    public static Vec squaredGradient(Vec outcome, Vec prediction) {
        Vec resultVector = new Vec(outcome.length());

        for (int i = 0; i < resultVector.length(); i++) {
            resultVector.set(
                    i, squaredDerivative(outcome.get(i), prediction.get(i)));
        }

        return resultVector;
    }

    // Returns the String representation of this loss function.
    public String toString() {
        return "Loss function: " + fnName;
    }
}
