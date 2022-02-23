package com.predictor;

public class Fn {

    public static final String[] VALID_FNS = {"add", "dot", "identity",
                                              "leaky relu", "relu", "sigmoid",
                                              "tanh"};

    protected String fnName;

    // Returns whether the function is valid or not.
    public static boolean isValidFn(String fn) {
        for (String validFn : VALID_FNS) {
            if (fn.equals(validFn)) {
                return true;
            }
        }
        return false;
    }

    // Returns the output of the ReLu function given an
    // input and a value in case the input is negative.
    public static double relu(double val, double x) {
        if (x < 0) {
            return val * x;
        }
        return x;
    }

    // Returns the output of the sigmoid function given an input.
    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    // Returns the output of the hyperbolic tangent function given an input.
    public static double tanh(double x) {
        return (2 / (1 + Math.exp(-x * 2))) - 1;
    }
}
