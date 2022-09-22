package com.predictor;

public abstract class ActFn extends Fn {

    private static final String[] VALID_ACT_FNS = {"identity", "leaky relu",
                                                  "relu", "sigmoid", "tanh"};

    // Applies this activation function on the vector.
    protected abstract void accept(Vec v);

    // Returns the output of the derivative of this activation
    // function given and with respect to an input.
    protected abstract double derivative(double x);

    // Returns the output vector of the gradient of this activation
    // function given and with respect to an input vector.
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
