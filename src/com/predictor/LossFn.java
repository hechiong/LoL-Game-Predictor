package com.predictor;

import java.util.function.BiFunction;

public class LossFn extends Fn {

    private final BiFunction<Double, Double, Double> fn;

    // Constuctor for a loss function.
    public LossFn(String lossFn) {
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
