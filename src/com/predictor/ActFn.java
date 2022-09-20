package com.predictor;

import java.util.function.Consumer;

public abstract class ActFn extends Fn {

    private static final String[] VALID_ACT_FNS = {"identity", "leaky relu",
                                                  "relu", "sigmoid", "tanh"};

    // Applies this activation function on the vector.
    protected void accept(Vec v) {
        Consumer<Vec> actFn = this::actFn;
        actFn.accept(v);
    }

    // Applies the activation function element-wise on the vector.
    protected abstract void actFn(Vec v);

    // Returns the output of the derivative of
    // the activation function given an input.
    protected abstract double derivative(double x);

    // Returns the output vector of the gradient of
    // the activation function given an input vector.
    protected Vec gradient(Vec v) {
        Vec resultVector = v.copy();

        for (int i = 0; i < v.length(); i++) {
            resultVector.set(i, derivative(v.get(i)));
        }

        return resultVector;
    }

    // Returns whether the function is a valid activation function.
    public static boolean isValidActFn(String fn) {
        for (String validActFn : VALID_ACT_FNS) {
            if (fn.equals(validActFn)) {
                return true;
            }
        }
        return false;
    }

    // Returns the String representation of this activation function.
    public String toString() {
        return "Activation function: " + getFnName();
    }
}
