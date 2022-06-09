package com.predictor;

import java.util.function.BiFunction;

public class LossFn extends Fn {

    protected static final String[] VALID_LOSS_FNS = {"logistic", "squared"};

    private final BiFunction<Double, Double, Double> fn;

    // Constuctor for a loss function.
    public LossFn(String lossFn) throws FnException {
        if (!isValidLossFn(lossFn))  {
            throw new FnException(lossFn  + " isn't a valid loss function.");
        }

        fnName = lossFn;
        switch (lossFn) {
            case "logistic":
                fn = this::logistic;
                break;
            default:
                fn = this::squared;
                fnName = "squared";
        }
    }

    // Applies this loss function on a prediction and an outcome.
    public double apply(double prediction, double outcome) {
        return fn.apply(prediction, outcome);
    }

    // Returns whether the loss function is defined in this class.
    public static boolean contains(String lossFn) {
        return lossFn.equals("logistic") || lossFn.equals("squared");
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

    // Applies the logistic loss function on a prediction and an outcome.
    private double logistic(double prediction, double outcome) {
        double sig = sigmoid(prediction);
        return -outcome * Math.log(sig) - (1-outcome) * Math.log(1-sig);
    }

    // Applies the squared error function on a prediction and an outcome.
    private double squared(double prediction, double outcome) {
        return Math.pow(prediction - outcome, 2);
    }

    // Returns the String representation of this loss function.
    public String toString() {
        return "Loss function: " + fnName;
    }
}
