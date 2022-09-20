package com.predictor;

public class ReLU extends ActFn {

    // Constructor for a ReLU activation function.
    public ReLU() {
        setFnName("relu");
    }

    // Applies the ReLU function element-wise on the vector.
    protected void actFn(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, relu(0, v.get(i)));
        }
    }

    // Returns the output of the derivative
    // of the ReLU function given an input.
    protected double derivative(double x) {
        if (x <= 0) {
            return 0;
        }
        return 1;
    }

    // Returns the output of the ReLU function given an
    // input and a scalar in case the input is negative.
    protected static double relu(double scalar, double x) {
        if (x <= 0) {
            return scalar * x;
        }
        return x;
    }
}

